package assignment.todo.repository;

import assignment.todo.dto.CalendarResponse;
import assignment.todo.entity.Calendar;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface CalendarRepository extends JpaRepository<Calendar ,Long> {

    @Query(value = "SELECT * FROM calendar WHERE created_date BETWEEN :startDate AND :endDate AND member_id = :memberId", nativeQuery = true)
    List<Calendar> findByCreatedDateBetweenAndMemberId(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("memberId") Long memberId
    );
    @Modifying
    @Query(value = "UPDATE calendar SET is_deleted = 1 WHERE id = :id", nativeQuery = true)
    int updateDeleteYnById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Calendar c SET c.content = :content, c.date = :date WHERE c.id = :calendarId AND c.member.id = :memberId")
    void updateCalendarContent(
            @Param("calendarId") Long calendarId,
            @Param("memberId") Long memberId,
            @Param("content") String content,
            @Param("date") LocalDate date
    );

    @Query("SELECT new assignment.todo.dto.CalendarResponse(t.id, c.content, c.date) " +
            "FROM Calendar c JOIN c.todo t " +
            "WHERE c.member.id = :memberId AND c.deleteYn = 0")
    List<CalendarResponse> findCalendarResponsesByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query(value = "UPDATE calendar SET is_finished = CASE WHEN is_finished = 1 THEN 0 ELSE 1 END WHERE id = :id AND is_deleted = 0", nativeQuery = true)
    int updateFinishYnById(@Param("id") Long id);


    Calendar findByMemberId(Long memberId);

}
