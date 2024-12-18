package assignment.todo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@Slf4j
public class DbTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("DB 연결 테스트")
    void connectionTest() {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());

            DatabaseMetaData metaData = connection.getMetaData();
            log.info("DB URL: " + metaData.getURL());
            log.info("DB Username: " + metaData.getUserName());
            log.info("DB Product Name: " + metaData.getDatabaseProductName());
            log.info("DB Product Version: " + metaData.getDatabaseProductVersion());

        } catch (SQLException e) {
            fail("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("DB 쿼리 실행 테스트")
    void queryTest() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // 간단한 쿼리 실행 (예: 현재 시간 조회)
            ResultSet resultSet = statement.executeQuery("SELECT NOW()");
            assertTrue(resultSet.next());
            assertNotNull(resultSet.getTimestamp(1));
            log.info("Current DB Time: " + resultSet.getTimestamp(1));

        } catch (SQLException e) {
            fail("쿼리 실행 실패: " + e.getMessage());
        }
    }


}
