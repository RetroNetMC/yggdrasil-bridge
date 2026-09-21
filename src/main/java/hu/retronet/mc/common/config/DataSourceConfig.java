package hu.retronet.mc.common.config;

import hu.retronet.mc.common.model.DatabaseProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.MissingResourceException;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        String dbProvider = System.getenv("DB_ENGINE_PROVIDER");
        if (dbProvider == null) {
            throw new MissingResourceException("Missing DB_ENGINE_PROVIDER environment variable", DataSource.class.getName(), "DB_ENGINE_PROVIDER");
        }
        var provider = searchEnum(DatabaseProvider.class, dbProvider);
        if (provider != null) {
            String driver = null, url = null;

            String dbHost = System.getenv().getOrDefault("DB_ENGINE_HOSTPORT", "db");
            String dbName = System.getenv("DB_ENGINE_DATABASE");
            switch (provider) {
                case MYSQL:
                    driver = "com.mysql.cj.jdbc.Driver";
                    url = "jdbc:mysql://%s/%s?serverTimezone=UTC".formatted(dbHost, dbName);
                    break;
                case MARIADB:
                    driver="org.mariadb.jdbc.Driver";
                    url = "jdbc:mariadb://%s/%s?useSSL=false&serverTimezone=UTC".formatted(dbHost, dbName);
                    break;
                case POSTGRESQL:
                    driver = "org.postgresql.Driver";
                    url = "jdbc:postgresql://%s/%s".formatted(dbHost, dbName);
                    break;
                case MSSQL:
                    driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                    url = "jdbc:sqlserver://%s;databaseName=%s".formatted(dbHost, dbName);
                    break;
                case NONE:
                    throw new MissingResourceException("Database provider not set", DatabaseProvider.class.getName(), "DB_ENGINE_PROVIDER");
            }
            log.debug(url);
            return DataSourceBuilder.create()
                            .driverClassName(driver)
                            .url(url)
                            .username(System.getenv("DB_ENGINE_USERNAME"))
                            .password(System.getenv("DB_ENGINE_PASSWORD"))
                            .build();
        }
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url("jdbc:sqlite:memory:yggdproxy?cache=shared")
                .build();
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }
}
