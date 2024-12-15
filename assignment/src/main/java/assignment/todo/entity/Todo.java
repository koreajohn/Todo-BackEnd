package assignment.todo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Todo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "todo_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @OneToOne(mappedBy = "todo", cascade = CascadeType.ALL)
    private Calendar calendar;

    @Column(name = "is_finished")
    private int finishYn = 0;

    @Column(name = "is_deleted")
    private int deleteYn = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;



    public static Todo createTodo(String content ,Member member){
        Todo todo = new Todo();
        todo.setContent(content);
        todo.setMember(member);
        return todo;
    }



}


