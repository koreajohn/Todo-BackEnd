package assignment.todo.service;

import assignment.todo.dto.TodoCalendarRequest;
import assignment.todo.dto.TodoResponse;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import assignment.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService{
    private final TodoRepository todoRepository;

    @Override
    public Todo saveTodo(String content, Member member) {
        Todo todo = Todo.createTodo(content,member);

        return todoRepository.save(todo);
    }

    @Override
    public void updateDeleteYnById( Long id) {
        todoRepository.updateDeleteYnById(id);
    }

    @Override
    public void updateFinishYnById(Long id , Long memberId ) {
        todoRepository.updateFinishYnById(id,memberId);
    }

    @Override
    public void updateTodoContent(Long todoId, Long memberId, String content) {
        todoRepository.updateTodoContent(todoId,memberId,content);
    }

    @Override
    public List<TodoCalendarRequest> findTodoAndCalendarByMemberId(Long memberId) {
        return todoRepository.findTodoAndCalendarByMemberId(memberId);
    }

    @Override
    public List<Todo> findAllByMemberId(Long memberId) {
        return todoRepository.findAllByMemberId(memberId);
    }

    @Override
    public List<TodoResponse> findTodoResponses(Long memberId) {
        return todoRepository.
                findTodoResponsesByMemberId(memberId);
    }


}
