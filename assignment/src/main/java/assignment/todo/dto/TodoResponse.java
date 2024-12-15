package assignment.todo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TodoResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
}
