package assignment.todo;


import assignment.todo.config.SecurityConfig;
import assignment.todo.dto.SignUpRequest;
import assignment.todo.entity.Member;
import assignment.todo.repository.MemberRepository;
import assignment.todo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MemberTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원가입 테스트")
    @Test
    void signUpTest() {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        // when
        Member result = memberService.signUp(request);

        // then
        assertThat(result.getUsername()).isEqualTo("testUser");

    }
    @DisplayName("로그인 테스트")
    @Test
    void loginTest() {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        // when
        Member result = memberService.login(request);

        // then
        assertThat(result.getUsername()).isEqualTo("testUser");


    }
}