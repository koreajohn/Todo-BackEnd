package assignment.todo.controller;

import assignment.todo.dto.SignUpRequest;
import assignment.todo.dto.TodoCalendarRequest;
import assignment.todo.dto.TodoRequest;
import assignment.todo.dto.TodoResponse;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import assignment.todo.repository.CalendarRepository;
import assignment.todo.repository.MemberRepository;
import assignment.todo.service.CalendarService;
import assignment.todo.service.MemberService;
import assignment.todo.service.TodoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController  // @Controller 대신 @RestController 사용
@RequestMapping("/api")  // 슬래시 추가
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "bearerAuth")
public class TodoAPIController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TodoService todoService;
    private final CalendarRepository calendarRepository;
    private final CalendarService calendarService;

    @PostMapping("/todoSave")
    public ResponseEntity<?> todoSave(@RequestBody TodoRequest todoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
       Member member =memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Todo todo = todoService.saveTodo(todoRequest.getContent(),member);
        return ResponseEntity.ok(todo);  // 200 OK 응답
    }
    @DeleteMapping("/todoDelete/{todoId}")
    public void todoDeleteYnById(@RequestBody @PathVariable("todoId") Long todoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            todoService.updateDeleteYnById(todoId);
            calendarService.updateDeleteYnById(todoId);
        }
    }
    @PatchMapping("/todoFinish/{todoId}")
    public void todoFinishYnById(@RequestBody @PathVariable("todoId") Long todoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member =memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        todoService.updateFinishYnById(todoId ,member.getId()  );
        calendarService.updateFinishYnById(todoId);
    }

// 기본 리스트 불러오가   @GetMapping("/todoList")
//    public List<Todo> getTodoList(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = null;
//        if (authentication != null) {
//            username = authentication.getName();
//        }
//        Member member =memberRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Member not found"));
//        List<Todo> todoList = todoService.findAllByMemberId(member.getId());
//        return todoList;
//    }
    @PatchMapping("/todoUpdate/{todoId}")
    public void todoUpdate(
            @PathVariable("todoId") Long todoId,
            @RequestBody String content  // content를 직접 받음
    ) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        todoService.updateTodoContent(todoId, member.getId(), content);
    }
    @GetMapping("/todoCalendarList")
    public List<TodoCalendarRequest> findTodoAndCalendarByMemberId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member =memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        List<TodoCalendarRequest> todoCalendarList = todoService.findTodoAndCalendarByMemberId(member.getId());
        return todoCalendarList;
    }

    // Todo에서 추가할 때
    @PostMapping("/todoCalendarSave")
    public ResponseEntity<?> todoCalendarSave(@RequestBody TodoRequest todoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 1. Todo 저장
        Todo todo = todoService.saveTodo(todoRequest.getContent(), member);

        // 2. Calendar도 같이 저장하고 연결
        Calendar calendar = Calendar.createCalendar(
                todoRequest.getContent(),
                LocalDate.now(),  // Todo 생성 날짜를 Calendar 날짜로
                member
        );
        calendar.setTodo(todo);  // 1:1 관계 설정
        calendarRepository.save(calendar);

        return ResponseEntity.ok(todo);
    }
    @GetMapping("/todoList")
    public List<TodoResponse> getTodoList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        Member member =memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        List<TodoResponse> todoList = todoService.findTodoResponses(member.getId());
        return todoList;
    }

    public Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
}