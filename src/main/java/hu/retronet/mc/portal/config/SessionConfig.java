package hu.retronet.mc.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.SaveMode;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@EnableJdbcHttpSession(saveMode = SaveMode.ON_SET_ATTRIBUTE)
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
