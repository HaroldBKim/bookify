package project.bookify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    @Column(nullable = false)
    private Long resourceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;

}
