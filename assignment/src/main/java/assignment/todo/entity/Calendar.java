package assignment.todo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Column(name = "created_date")
    private LocalDate date;
    @Column(name = "is_finished")
    private int finishYn = 0;

    @Column(name = "is_deleted")
    private int deleteYn = 0;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Calendar createCalendar(String content ,LocalDate date,Member member){
        Calendar calendar = new Calendar();
        calendar.setContent(content);
        calendar.setDate(date);
        calendar.setMember(member);
        return calendar;
    }
}
