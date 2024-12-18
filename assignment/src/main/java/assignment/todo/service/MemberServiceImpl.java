package assignment.todo.service;

import assignment.todo.config.SecurityConfig;
import assignment.todo.dto.SignUpRequest;
import assignment.todo.entity.Member;
import assignment.todo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final SecurityConfig securityConfig;
    @Override
    public Member signUp(SignUpRequest signUpRequest) {
        String encPassword = securityConfig.passwordEncoder().encode(signUpRequest.getPassword());
        Member memberEntity = Member.createUser(signUpRequest.getUsername(), encPassword);

        return memberRepository.save(memberEntity);
    }

    @Override
    public Member login(SignUpRequest loginRequest) {
        Optional<Member> member = memberRepository.findByUsername(loginRequest.getUsername());
        if(!securityConfig.passwordEncoder().matches(loginRequest.getPassword(),member.get().getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }else{
            return member.get();
        }
    }
}
