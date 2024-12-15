package assignment.todo.dto;

import lombok.Data;

@Data  // lombok annotation
public class SignUpRequest {
    private String username;
    private String password;
}