package assignment.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

@RestController
@Tag(name = "테스트", description = "TestController")
@SecurityRequirement(name = "bearer-auth")  // SwaggerConfig에 정의된 것과 동일한 이름 사용
public class TestAPIController {

    @Operation(
            summary = "메인 페이지",
            description = "현재 인증된 사용자의 이름과 역할을 반환합니다.",
            security = { @SecurityRequirement(name = "bearer-auth") },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content
                    )
            }
    )
    @GetMapping("/")
    public String main2() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "main2" + username + role;
    }

    @Operation(
            summary = "헬스 체크",
            description = "서버 상태를 확인합니다.",
            security = { @SecurityRequirement(name = "bearer-auth") },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "서버 정상 작동 중",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
                    )
            }
    )
    @GetMapping("/health")
    public String health() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return "health check ok - authenticated user: " + username;
        }
        return "health check ok - no authentication";
    }
}