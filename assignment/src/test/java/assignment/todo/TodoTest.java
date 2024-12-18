package assignment.todo;

import assignment.todo.dto.SignUpRequest;
import assignment.todo.dto.TodoResponse;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import assignment.todo.repository.CalendarRepository;
import assignment.todo.repository.MemberRepository;
import assignment.todo.repository.TodoRepository;
import assignment.todo.service.MemberService;
import assignment.todo.service.TodoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc  // 이 어노테이션 추가
@Slf4j
@Transactional
public class TodoTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    private Member testMember;
    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();  // 각 테스트 전에 Todo 데이터 정리
        calendarRepository.deleteAll(); // 각 테스트 전에 Calendar 데이터 정리

    }


    @Test
    @DisplayName("Todo 저장 테스트")
    void todoSaveTest() {
        // given

        Optional<Member> savedMember = memberRepository.findByUsername("testUser");
        if (savedMember.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            savedMember = Optional.of(memberService.signUp(request));
        }

        String content = "TestContent";
        Member member = savedMember.get();

        // when
        Todo savedTodo = todoService.saveTodo(content, member);

        // then
        assertNotNull(savedTodo);
        assertEquals(content, savedTodo.getContent());
        assertEquals(member.getId(), savedTodo.getMember().getId());

        // DB에서 다시 조회해서 확인
        Todo foundTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new AssertionError("Saved todo not found"));
        assertEquals(content, foundTodo.getContent());
        assertEquals(member.getId(), foundTodo.getMember().getId());
    }

    @Test
    @DisplayName("Todo 및 캘린더 목록 불러오기")
    void todoGetListTest() {
        // given
        String username = "testUser";
        String password = "password123";
        String content = "TestContent";

        // 테스트 회원 생성
        Member member = memberRepository.findByUsername(username)
                .orElseGet(() -> {
                    SignUpRequest request = new SignUpRequest();
                    request.setUsername(username);
                    request.setPassword(password);
                    return memberService.signUp(request);
                });

        // Todo와 Calendar 함께 생성
        for(int i = 0; i < 10; i++) {
            Todo todo = todoService.saveTodo(content + i, member);
            Calendar calendar = Calendar.createCalendar(content + i, LocalDate.now(), member);
            calendar.setTodo(todo);
            calendarRepository.save(calendar);
        }

        List<TodoResponse> list = todoRepository.findTodoResponsesByMemberId(member.getId());

        assertEquals(10, list.size());

        // 데이터 검증 및 로깅
        for(int i = 0; i < list.size(); i++) {
            TodoResponse todo = list.get(i);
            log.info("todoResponse.getId(): " + todo.getId());
            log.info("todoResponse.getContent(): " + todo.getContent());
            log.info("todoResponse.getCreatedAt(): " + todo.getCreatedAt());

            // content 검증
            assertEquals(content + i, todo.getContent());
            // 생성일자가 null이 아닌지 확인
            assertNotNull(todo.getCreatedAt());
        }
    }
    @Test
    @DisplayName("Todo 내용 업데이트 테스트")
    void updateTodoContentTest() {
        // given
        Optional<Member> member = memberRepository.findByUsername("testUser");
        if (member.isEmpty()) {
            // 테스트용 회원 생성
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            member = Optional.of(memberService.signUp(request));
        }

        String content = "원본 내용";
        // Todo 생성
        Todo todo = todoService.saveTodo(content, member.get());
        Long todoId = todo.getId();

        // when
        String updatedContent = "수정된 내용";
        todoRepository.updateTodoContent(todoId, member.get().getId(), updatedContent);

        // then
        Todo updatedTodo = todoRepository.findById(todoId).orElseThrow();
        assertEquals(updatedContent, updatedTodo.getContent());

        // 잘못된 memberId로 수정 시도
        String failContent = "실패해야 할 내용";
        todoRepository.updateTodoContent(todoId, 999L, failContent);
        Todo notUpdatedTodo = todoRepository.findById(todoId).orElseThrow();
        assertNotEquals(failContent, notUpdatedTodo.getContent());
        assertEquals(updatedContent, notUpdatedTodo.getContent());
    }

    @Test
    @DisplayName("Todo 논리적 삭제 테스트")
    void updateDeleteYnByIdTest() {
        // given
        Optional<Member> member = memberRepository.findByUsername("testUser");
        if (member.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            member = Optional.of(memberService.signUp(request));
        }

        // Todo 생성
        String content = "삭제될 Todo";
        Todo todo = todoService.saveTodo(content, member.get());
        Long todoId = todo.getId();

        // 생성된 Todo의 초기 상태 확인
        assertEquals(0, todo.getDeleteYn(), "초기 deleteYn은 0이어야 합니다");

        // when
        int result = todoRepository.updateDeleteYnById(todoId);

        // then
        // 업데이트 성공 확인
        assertEquals(1, result, "업데이트 영향받은 행이 1이어야 합니다");

        // 삭제 상태 확인
        Todo deletedTodo = todoRepository.findById(todoId).orElseThrow();
        assertEquals(1, deletedTodo.getDeleteYn(), "deleteYn이 1로 변경되어야 합니다");

        // 삭제된 Todo는 일반 조회에서 제외되는지 확인
        List<Todo> todos = todoRepository.findAllByMemberId(member.get().getId());
        assertTrue(todos.stream().noneMatch(t -> t.getId().equals(todoId)),
                "삭제된 Todo는 조회 결과에 포함되지 않아야 합니다");

        // 존재하지 않는 todoId로 삭제 시도
        int failResult = todoRepository.updateDeleteYnById(999L);
        assertEquals(0, failResult, "존재하지 않는 ID 삭제 시 영향받은 행이 0이어야 합니다");
    }
    @Test
    @DisplayName("Todo 완료상태 토글 테스트")
    void updateFinishYnByIdTest() {
        // given
        Optional<Member> member = memberRepository.findByUsername("testUser");
        if (member.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            member = Optional.of(memberService.signUp(request));
        }

        // Todo 생성
        String content = "완료상태 변경될 Todo";
        Todo todo = todoService.saveTodo(content, member.get());
        Long todoId = todo.getId();

        // 초기 상태 확인 (미완료 = 0)
        assertEquals(0, todo.getFinishYn(), "초기 finishYn은 0이어야 합니다");

        // when - 첫 번째 토글 (미완료 -> 완료)
        int firstResult = todoRepository.updateFinishYnById(todoId, member.get().getId());

        // then
        assertEquals(1, firstResult, "업데이트 영향받은 행이 1이어야 합니다");
        Todo completedTodo = todoRepository.findById(todoId).orElseThrow();
        assertEquals(1, completedTodo.getFinishYn(), "finishYn이 1로 변경되어야 합니다");

        // when - 두 번째 토글 (완료 -> 미완료)
        int secondResult = todoRepository.updateFinishYnById(todoId, member.get().getId());

        // then
        assertEquals(1, secondResult, "업데이트 영향받은 행이 1이어야 합니다");
        Todo uncompletedTodo = todoRepository.findById(todoId).orElseThrow();
        assertEquals(0, uncompletedTodo.getFinishYn(), "finishYn이 0으로 변경되어야 합니다");

        // 잘못된 memberId로 토글 시도
        int failResult = todoRepository.updateFinishYnById(todoId, 999L);
        assertEquals(0, failResult, "잘못된 memberId로 시도 시 영향받은 행이 0이어야 합니다");

        // 존재하지 않는 todoId로 토글 시도
        int nonExistResult = todoRepository.updateFinishYnById(999L, member.get().getId());
        assertEquals(0, nonExistResult, "존재하지 않는 todoId로 시도 시 영향받은 행이 0이어야 합니다");
    }


}
