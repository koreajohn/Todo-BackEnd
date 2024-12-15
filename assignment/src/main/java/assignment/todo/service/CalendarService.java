package assignment.todo.service;

import assignment.todo.dto.CalendarRequest;
import assignment.todo.dto.CalendarResponse;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface CalendarService {
    Calendar saveCalendar(String content, LocalDate date, Member member);
    List<Calendar> findByCreatedDateBetweenAndMemberId(YearMonth yearMonth ,Long memberId);
    void updateDeleteYnById(Long id);
    void updateFinishYnById(Long id);
    void updateCalendarContent(Long todoId, Long memberId, String content, LocalDate date);
    List<CalendarResponse> findCalendarResponsesByMemberId(@Param("memberId") Long memberId);
}
