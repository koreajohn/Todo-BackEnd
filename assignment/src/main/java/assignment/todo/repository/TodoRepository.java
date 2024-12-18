package assignment.todo.repository;

import assignment.todo.dto.TodoCalendarRequest;
import assignment.todo.dto.TodoResponse;
import assignment.todo.entity.Member;
import assignment.todo.entity.Todo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TodoRepository extends JpaRepository<Todo,Long> {

    @Modifying(clearAutomatically = true)  // clearAutomatically = true 추가
    @Query(value = "UPDATE todo SET is_deleted = 1 WHERE todo_id = :id", nativeQuery = true)
    int updateDeleteYnById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)  // clearAutomatically = true 추가
    @Transactional
    @Query(value = "UPDATE todo SET is_finished = CASE WHEN is_finished = 1 THEN 0 ELSE 1 END WHERE todo_id = :id AND member_id = :memberId", nativeQuery = true)
    int updateFinishYnById(@Param("id") Long id, @Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)  // clearAutomatically = true 추가
    @Query("UPDATE Todo t SET t.content = :content WHERE t.id = :todoId AND t.member.id = :memberId")
    void updateTodoContent(@Param("todoId") Long todoId, @Param("memberId") Long memberId, @Param("content") String content);

    @Query("SELECT t FROM Todo t WHERE t.member.id = :memberId and t.deleteYn=0")
    List<Todo> findAllByMemberId(@Param("memberId") Long memberId);


    @Query("SELECT new assignment.todo.dto.TodoCalendarRequest(c.content, t.content) " +
            "FROM Todo t JOIN Calendar c " +
            "ON t.member.id = c.member.id " +
            "WHERE c.member.id = :memberId")
    List<TodoCalendarRequest> findTodoAndCalendarByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new assignment.todo.dto.TodoResponse(t.id, t.content, t.createdAt) " +
            "FROM Todo t JOIN Calendar c ON t.id = c.todo.id " +
            "WHERE t.member.id = :memberId AND t.deleteYn = 0 AND c.deleteYn = 0")
    List<TodoResponse> findTodoResponsesByMemberId(@Param("memberId") Long memberId);
    Todo findByMemberIdAndContent(Long memberId , String content);

    Optional<Todo> findByMemberAndContent(Member member, String content);
}
