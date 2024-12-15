package assignment.todo.service;


import assignment.todo.dto.SignUpRequest;
import assignment.todo.entity.Member;
import org.springframework.data.repository.query.Param;

public interface MemberService {
     Member signUp(SignUpRequest signUprequest);
     Member login(SignUpRequest loginRequest);
}
