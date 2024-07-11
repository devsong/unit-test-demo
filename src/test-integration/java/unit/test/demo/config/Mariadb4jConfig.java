package unit.test.demo.config;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import unit.test.demo.common.OSInfo;

import javax.sql.DataSource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author zhisong.guan
 */
@Configuration
@Slf4j
public class Mariadb4jConfig {
    public static final String SCHEMA = "demo";
    public static final String MYSQL_DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "123456";
    public static final int START_PORT = 60000;
    public static final int RANDOM_PORT_RANGE = 1000;
    public static final int MIN_IDLE = 10;
    public static final int MAX_IMUM_POOLSIZE = 30;
    public static final int IDLE_TIMEOUT = 30;

    @Bean
    public MariaDB4jSpringService mariaDB4jSpringService() {
        MariaDB4jSpringService mariaDB4jSpringService = new MariaDB4jSpringService();
        int port = new Random().nextInt(RANDOM_PORT_RANGE) + START_PORT;
        mariaDB4jSpringService.setDefaultPort(port);
        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();
        config.addArg("--character-set-server=utf8mb4");
        config.addArg("--lower_case_table_names=1");
        config.addArg("--collation-server=utf8mb4_general_ci");
        config.addArg("--user=root");
        config.addArg("--max-connections=1024");
        config.setDeletingTemporaryBaseAndDataDirsOnShutdown(true);
        config.setBaseDir(SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/base");
        config.setDataDir(SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/data");
//        if (OSInfo.isMacOSX() || OSInfo.isMacOS()) {
//            config.setUnpackingFromClasspath(false);
//            config.setBaseDir("/opt/homebrew");
//        }
        config.setLibDir(SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/no-libs");

        log.info("mariadb4j port {}", port);
        return mariaDB4jSpringService;
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService) throws ManagedProcessException {
        mariaDB4jSpringService.getDB().createDB(SCHEMA);
        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(config.getURL(SCHEMA));
        dataSource.setDriverClassName(MYSQL_DRIVER_CLASS_NAME);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinimumIdle(MIN_IDLE);
        dataSource.setMaximumPoolSize(MAX_IMUM_POOLSIZE);
        dataSource.setIdleTimeout(TimeUnit.SECONDS.toMillis(IDLE_TIMEOUT));
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
