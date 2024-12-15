package assignment.todo.controller;

import assignment.todo.dto.SignUpRequest;
import assignment.todo.entity.Member;
import assignment.todo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // @Controller 대신 @RestController 사용
@RequestMapping("/api")  // 슬래시 추가
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberAPIController {

    private final MemberService memberService;
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUprequest) {
        memberService.signUp(signUprequest);
        return ResponseEntity.ok("회원가입이 성공 했습니다.");  // 200 OK 응답
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignUpRequest signUprequest, HttpSession session){
        Member member =  memberService.login(signUprequest);
        return ResponseEntity.ok(member);  // 200 OK 응답
    }
}
