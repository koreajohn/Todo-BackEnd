package assignment.todo.service;

import assignment.todo.dto.TodoCalendarRequest;
import assignment.todo.dto.TodoRequest;
import assignment.todo.dto.TodoResponse;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;

import java.util.List;

public interface TodoService {
    public Todo saveTodo(String content, Member member);
    void updateDeleteYnById(Long id);
    void updateFinishYnById(Long id, Long memberId );
    void updateTodoContent(Long todoId, Long memberId, String content);
    List<TodoCalendarRequest> findTodoAndCalendarByMemberId(Long memberId);
    List<Todo> findAllByMemberId(Long memberId);
    List<TodoResponse> findTodoResponses(Long memberId);

}
