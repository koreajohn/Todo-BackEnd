package assignment.todo.service;

import assignment.todo.dto.CalendarResponse;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService{

        private final CalendarRepository calendarRepository;
    @Override
    public Calendar saveCalendar(String content, LocalDate date, Member member) {
        Calendar calendar = Calendar.createCalendar(content,date,member);
        return calendarRepository.save(calendar);
    }

    @Override
    public List<Calendar> findByCreatedDateBetweenAndMemberId(YearMonth yearMonth,Long memberId) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return calendarRepository.findByCreatedDateBetweenAndMemberId(startDate, endDate,memberId);
    }

    @Override
    public void updateDeleteYnById(Long id) {
        calendarRepository.updateDeleteYnById(id);
    }

    @Override
    public void updateFinishYnById(Long id) {
        calendarRepository.updateFinishYnById(id);
    }

    @Override
    public void updateCalendarContent(Long calendarId, Long memberId, String content, LocalDate date) {
        calendarRepository.updateCalendarContent(calendarId,memberId, content,date);
    }

    @Override
    public List<CalendarResponse> findCalendarResponsesByMemberId(Long memberId) {
        return calendarRepository.findCalendarResponsesByMemberId(memberId);
    }

}
