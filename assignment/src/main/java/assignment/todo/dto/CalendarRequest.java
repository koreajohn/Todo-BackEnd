package assignment.todo.dto;

import assignment.todo.entity.Member;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CalendarRequest {
    private String content;
    private LocalDate  date;
}
