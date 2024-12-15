package assignment.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CalendarResponse {
    private Long id;
    private String content;
    private LocalDate date;
}
