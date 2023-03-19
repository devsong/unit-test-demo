package unit.test.demo.truncate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import unit.test.demo.config.Mariadb4jConfig;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TruncateDatabaseService implements TruncateService {
    private static final String IGNORE_TABLE = "flyway_schema_history";
    private static final String TABLES_LIST_SQL = "select table_name from information_schema.tables where table_schema = '%s' and table_type='BASE TABLE' and table_name not in (%s)";
    private static final String TRUNCATE_TABLE_SQL = "truncate table `%s`";
    private static final String ALTER_INCREMENT_SQL = "alter table `%s` auto_increment = %s";
    private final JdbcTemplate jdbcTemplate;

    private void restartIdWith(int startId) {
        String ignoreTables = Stream.of(IGNORE_TABLE.split(",")).map(s -> "'" + s + "'").collect(Collectors.joining(","));
        String actualSql = String.format(TABLES_LIST_SQL, Mariadb4jConfig.SCHEMA, ignoreTables);
        List<String> tableNames = jdbcTemplate.queryForList(actualSql, String.class);
        jdbcTemplate.execute("set FOREIGN_KEY_CHECKS = 0");
        for (String tableName : tableNames) {
            jdbcTemplate.execute(String.format(TRUNCATE_TABLE_SQL, tableName));
            jdbcTemplate.execute(String.format(ALTER_INCREMENT_SQL, tableName, startId));
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Override
    @Transactional
    public void truncate() {
        restartIdWith(1);
    }
}
