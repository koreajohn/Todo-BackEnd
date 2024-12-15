package assignment.todo.service;

import assignment.todo.entity.Member;
import assignment.todo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(Member getMember){
        Member member = Member.createUser(
           getMember.getUsername(),
           passwordEncoder.encode(getMember.getPassword())
        );
        memberRepository.save(member);
    }

}
