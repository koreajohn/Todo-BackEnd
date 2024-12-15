package assignment.todo.controller;

import assignment.todo.dto.CalendarRequest;
import assignment.todo.dto.CalendarResponse;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import assignment.todo.repository.CalendarRepository;
import assignment.todo.repository.MemberRepository;
import assignment.todo.repository.TodoRepository;
import assignment.todo.service.CalendarService;
import assignment.todo.service.MemberService;
import assignment.todo.service.TodoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")  // 슬래시 추가
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CalendarAPIController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TodoService todoService;
    private final CalendarService calendarService;
    private final CalendarRepository calendarRepository;
    private final TodoRepository todoRepository;

    @PostMapping("/calendarSave")
    public ResponseEntity<?> todoSave(@RequestBody CalendarRequest calendarRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member =memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Calendar calendar = calendarService.saveCalendar(calendarRequest.getContent(),calendarRequest.getDate(),member);
        return ResponseEntity.ok(calendar);  // 200 OK 응답
    }

//  기본 켈린더 리스트  @PostMapping("/calendarList")
//    public List<Calendar> getCalendarList(@RequestBody @DateTimeFormat(pattern = "yyyy-MM") YearMonth date) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = null;
//        if (authentication != null) {
//            username = authentication.getName();
//        }
//        Member member = memberRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Member not found"));
//
//        List<Calendar> calendarList = calendarService.findByCreatedDateBetweenAndMemberId(date,member.getId());
//        return calendarList;
//    }

    @DeleteMapping("/calendarDelete/{calenderId}")
    public void calendarDeleteYnById(@RequestBody @PathVariable("calenderId") Long calenderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            calendarService.updateDeleteYnById(calenderId);
            todoService.updateDeleteYnById(calenderId);
        }
    }
    @PatchMapping("/calendarUpdate/{calendarId}")
    public void calendarUpdate(
            @PathVariable("calendarId") Long calendarId,
            @RequestBody String content  // content를 직접 받음
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);

        String content2 = jsonNode.get("content").asText();
        String dateStr = jsonNode.get("date").asText();
        LocalDate date = LocalDate.parse(dateStr);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        calendarService.updateCalendarContent(calendarId, member.getId(), content2,date);
    }
    // Calendar에서 추가할 때
    @PostMapping("/calendarTodoSave")
    public ResponseEntity<?> calendarSave(@RequestBody CalendarRequest calendarRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 1. Calendar 저장
        Calendar calendar = Calendar.createCalendar(
                calendarRequest.getContent(),
                calendarRequest.getDate(),
                member
        );
        calendar = calendarRepository.save(calendar);

        // 2. Todo도 같이 저장하고 연결
        Todo todo = Todo.createTodo(calendarRequest.getContent(), member);
        todo = todoRepository.save(todo);

        // 3. 1:1 관계 설정
        calendar.setTodo(todo);
        calendarRepository.save(calendar);

        return ResponseEntity.ok(calendar);
    }
    @PostMapping("/calendarList")
    public List<CalendarResponse> getCalendarList(@RequestBody @DateTimeFormat(pattern = "yyyy-MM") YearMonth date) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<CalendarResponse> calendarList = calendarService.findCalendarResponsesByMemberId(member.getId());

        return calendarList;
    }

}
