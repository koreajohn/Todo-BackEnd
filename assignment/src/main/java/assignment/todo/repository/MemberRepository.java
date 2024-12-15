package assignment.todo.repository;

import assignment.todo.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);
    @Modifying
    @Query(value = "UPDATE member SET is_deleted = 1 WHERE id = :id", nativeQuery = true)
    int updateDeleteYnById(@Param("id") Long id);

}
