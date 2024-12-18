package assignment.todo;

import assignment.todo.dto.CalendarResponse;
import assignment.todo.dto.SignUpRequest;
import assignment.todo.entity.Calendar;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import assignment.todo.repository.CalendarRepository;
import assignment.todo.repository.MemberRepository;
import assignment.todo.repository.TodoRepository;
import assignment.todo.service.MemberService;
import assignment.todo.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc  // 이 어노테이션 추가
@Slf4j
public class CalendarTest {

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
    @DisplayName("Calendar 저장 테스트")
    void calendarSaveTest() {
        // given
        Optional<Member> savedMember = memberRepository.findByUsername("testUser");
        if (savedMember.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            savedMember = Optional.of(memberService.signUp(request));
        }
        Member member = savedMember.get();

        // Todo 먼저 생성 (Calendar와 연관관계를 위해)
        String content = "TestContent";
        Todo todo = todoService.saveTodo(content, member);

        // Calendar 생성
        LocalDate today = LocalDate.now();
        Calendar calendar = Calendar.createCalendar(content, today, member);
        calendar.setTodo(todo);  // Todo와 연관관계 설정

        // when
        Calendar savedCalendar = calendarRepository.save(calendar);

        // then
        assertNotNull(savedCalendar);
        assertNotNull(savedCalendar.getId());
        assertEquals(content, savedCalendar.getContent());
        assertEquals(today, savedCalendar.getDate());
        assertEquals(member.getId(), savedCalendar.getMember().getId());
        assertEquals(todo.getId(), savedCalendar.getTodo().getId());
        assertEquals(0, savedCalendar.getFinishYn());
        assertEquals(0, savedCalendar.getDeleteYn());

        // DB에서 다시 조회해서 확인
        Calendar foundCalendar = calendarRepository.findById(savedCalendar.getId())
                .orElseThrow(() -> new AssertionError("Saved calendar not found"));
        assertEquals(content, foundCalendar.getContent());
        assertEquals(today, foundCalendar.getDate());
        assertEquals(member.getId(), foundCalendar.getMember().getId());
        assertEquals(todo.getId(), foundCalendar.getTodo().getId());
    }

    @Test
    @DisplayName("Calendar 리스트 조회 테스트")
    void getCalendarListTest() {
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

        // when
        List<CalendarResponse> calendarList = calendarRepository.findCalendarResponsesByMemberId(member.getId());

        // then
        assertEquals(10, calendarList.size(), "Calendar 리스트 크기가 10이어야 합니다");

        // 데이터 검증 및 로깅
        for(int i = 0; i < calendarList.size(); i++) {
            CalendarResponse calendar = calendarList.get(i);
            log.info("calendarResponse.getTodoId(): " + calendar.getId());
            log.info("calendarResponse.getContent(): " + calendar.getContent());
            log.info("calendarResponse.getDate(): " + calendar.getDate());

            // content 검증
            assertEquals(content + i, calendar.getContent(), "Calendar content가 일치해야 합니다");
            // 날짜가 오늘인지 확인
            assertEquals(LocalDate.now(), calendar.getDate(), "Calendar date가 오늘이어야 합니다");
            // todoId가 null이 아닌지 확인
            assertNotNull(calendar.getId(), "Calendar의 todoId가 null이 아니어야 합니다");
        }

        // 삭제된 Calendar는 조회되지 않는지 확인
        Calendar calendar = calendarRepository.findAll().get(0);
        calendarRepository.updateDeleteYnById(calendar.getId());
        List<CalendarResponse> updatedList = calendarRepository.findCalendarResponsesByMemberId(member.getId());
        assertEquals(9, updatedList.size(), "삭제된 Calendar를 제외한 9개가 조회되어야 합니다");
    }
    @Test
    @DisplayName("Calendar 내용과 날짜 업데이트 테스트")
    void updateCalendarContentTest() {
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
        LocalDate originalDate = LocalDate.now();

        // Todo와 Calendar 생성
        Todo todo = todoService.saveTodo(content, member.get());
        Calendar calendar = Calendar.createCalendar(content, originalDate, member.get());
        calendar.setTodo(todo);
        Calendar savedCalendar = calendarRepository.save(calendar);
        Long calendarId = savedCalendar.getId();

        // when
        String updatedContent = "수정된 내용";
        LocalDate updatedDate = LocalDate.now().plusDays(1);  // 하루 뒤로 수정
        calendarRepository.updateCalendarContent(calendarId, member.get().getId(), updatedContent, updatedDate);

        // then
        Calendar updatedCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertEquals(updatedContent, updatedCalendar.getContent(), "내용이 수정되어야 합니다");
        assertEquals(updatedDate, updatedCalendar.getDate(), "날짜가 수정되어야 합니다");

        // 잘못된 memberId로 수정 시도
        String failContent = "실패해야 할 내용";
        LocalDate failDate = LocalDate.now().plusDays(2);
        calendarRepository.updateCalendarContent(calendarId, 999L, failContent, failDate);

        Calendar notUpdatedCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertNotEquals(failContent, notUpdatedCalendar.getContent(), "잘못된 memberId로는 내용이 수정되지 않아야 합니다");
        assertNotEquals(failDate, notUpdatedCalendar.getDate(), "잘못된 memberId로는 날짜가 수정되지 않아야 합니다");
        assertEquals(updatedContent, notUpdatedCalendar.getContent(), "이전 수정 내용이 유지되어야 합니다");
        assertEquals(updatedDate, notUpdatedCalendar.getDate(), "이전 수정 날짜가 유지되어야 합니다");
    }

    @Test
    @DisplayName("Calendar 삭제 테스트")
    void updateDeleteYnByIdTest() {
        // given
        Optional<Member> member = memberRepository.findByUsername("testUser");
        if (member.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            member = Optional.of(memberService.signUp(request));
        }

        // Calendar 생성
        String content = "삭제될 Calendar";
        Calendar calendar = Calendar.createCalendar(content, LocalDate.now(), member.get());
        Calendar savedCalendar = calendarRepository.save(calendar);
        Long calendarId = savedCalendar.getId();

        // 초기 상태 확인
        assertEquals(0, savedCalendar.getDeleteYn(), "초기 deleteYn은 0이어야 합니다");

        // when
        int result = calendarRepository.updateDeleteYnById(calendarId);

        // then
        // 1. 정상 삭제 케이스
        assertEquals(1, result, "존재하는 Calendar 삭제 시 영향받은 행은 1이어야 합니다");

        // 삭제 상태 확인
        Calendar deletedCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertEquals(1, deletedCalendar.getDeleteYn(), "deleteYn이 1로 변경되어야 합니다");

        // 2. 존재하지 않는 ID 삭제 시도
        int notExistResult = calendarRepository.updateDeleteYnById(999L);
        assertEquals(0, notExistResult, "존재하지 않는 ID 삭제 시 영향받은 행은 0이어야 합니다");



        // 4. 삭제된 항목이 조회에서 제외되는지 확인
        List<CalendarResponse> calendarList = calendarRepository.findCalendarResponsesByMemberId(member.get().getId());
        assertTrue(calendarList.isEmpty() ||
                        calendarList.stream().noneMatch(c -> c.getId().equals(calendarId)),
                "삭제된 Calendar는 조회되지 않아야 합니다");
    }
    @Test
    @DisplayName("Calendar 완료상태 토글 테스트")
    void updateFinishYnByIdTest() {
        // given
        Optional<Member> member = memberRepository.findByUsername("testUser");
        if (member.isEmpty()) {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testUser");
            request.setPassword("password123");
            member = Optional.of(memberService.signUp(request));
        }

        // Calendar 생성
        String content = "완료상태 변경될 Calendar";
        Calendar calendar = Calendar.createCalendar(content, LocalDate.now(), member.get());
        Calendar savedCalendar = calendarRepository.save(calendar);
        Long calendarId = savedCalendar.getId();

        // 초기 상태 확인
        assertEquals(0, savedCalendar.getFinishYn(), "초기 finishYn은 0이어야 합니다");

        // when - 첫 번째 토글 (0 -> 1)
        int firstResult = calendarRepository.updateFinishYnById(calendarId);

        // then
        assertEquals(1, firstResult, "존재하는 Calendar의 첫 토글 시 영향받은 행은 1이어야 합니다");
        Calendar toggledCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertEquals(1, toggledCalendar.getFinishYn(), "finishYn이 1로 변경되어야 합니다");

        // when - 두 번째 토글 (1 -> 0)
        int secondResult = calendarRepository.updateFinishYnById(calendarId);

        // then
        assertEquals(1, secondResult, "존재하는 Calendar의 두 번째 토글 시 영향받은 행은 1이어야 합니다");
        Calendar untoggledCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertEquals(0, untoggledCalendar.getFinishYn(), "finishYn이 다시 0으로 변경되어야 합니다");

        // 존재하지 않는 ID로 토글 시도
        int notExistResult = calendarRepository.updateFinishYnById(999L);
        assertEquals(0, notExistResult, "존재하지 않는 ID 토글 시 영향받은 행은 0이어야 합니다");

        // 삭제된 Calendar의 토글 시도
        calendarRepository.updateDeleteYnById(calendarId);  // Calendar 삭제
        int deletedCalendarResult = calendarRepository.updateFinishYnById(calendarId);
        assertEquals(0, deletedCalendarResult, "삭제된 Calendar 토글 시 영향받은 행은 0이어야 합니다");
    }
}
