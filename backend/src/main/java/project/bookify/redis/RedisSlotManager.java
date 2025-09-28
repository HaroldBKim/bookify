package project.bookify.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisSlotManager {

    private final StringRedisTemplate redis;

    /**
     * 다중 슬롯 - 임시 선점
     * 모든 키가 빈 슬롯이면 일정 시간 동안(EX 만료시간)만 잡고, 자동으로 풀리게 한다(NX 선점) 옵션으로 전부 SET
     * 하나라도 이미 존재하면 아무 것도 하지 않고 0 반환 (실패) -> 단일 명령으로 원자성 보장
     */
    private static final String LUA_ACQUIRE = """
    for i=1,#KEYS do
      if redis.call('EXISTS', KEYS[i]) == 1 then return 0 end
    end
    for i=1,#KEYS do
      redis.call('SET', KEYS[i], '1', 'EX', ARGV[1], 'NX')
    end
    return 1
    """;

    /**
     * 다중 슬롯 - 확정
     * 모든 키가 현재 존재해야 함 = 임시 홀드가 살아있어야 함
     * 전부 EXPIRE로 TTL을 확정TTL(예약 길이)로 연장
     * 하나라도 없으면 0 반환 (확정 실패)
     * 확정은 DB 커밋 성공 이후에 호출해야 함
     */
    private static final String LUA_CONFIRM = """
    for i=1,#KEYS do
      if redis.call('EXISTS', KEYS[i]) == 0 then return 0 end
    end
    for i=1,#KEYS do
      redis.call('EXPIRE', KEYS[i], ARGV[1])
    end
    return 1
    """;

    /**
     * 다중 슬롯 - 해제
     * DB 트랜잭션 실패/롤백 시 임시 홀드나 확정 상태를 즉시 제거
     */
    private static final String LUA_RELEASE = """
    for i=1,#KEYS do redis.call('DEL', KEYS[i]) end
    return 1
    """;


    public boolean acquireHold(List<String> keys, int ttlSec) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_ACQUIRE, Long.class);
        Long result = redis.execute(script, keys, String.valueOf(ttlSec)); // 임시 홀드 TTL
        return result != null && result == 1L;
    }

    public void confirmHold(List<String> keys, int ttlSec) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_CONFIRM, Long.class);
        Long result = redis.execute(script, keys, String.valueOf(ttlSec)); // 확정 TTL
        if (result == null || result != 1L) {
            throw new IllegalStateException("Redis 확정 실패: 임시 홀드가 만료되었거나 누락됨");
        }
    }

    public void releaseHold(List<String> keys) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_RELEASE, Long.class);
        redis.execute(script, keys);
    }

}
