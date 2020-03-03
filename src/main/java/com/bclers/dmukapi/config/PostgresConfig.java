package com.bclers.dmukapi.config;

import com.bclers.dmukapi.utils.PropertyUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class PostgresConfig {
    @Bean
    public DataSource getDataSource() {
        String url;
        String username;
        String password;

        if (System.getenv("GH_POSTGRES_URL") != null)
        {
            url = System.getenv("GH_POSTGRES_URL");
            username = System.getenv("GH_POSTGRES_USERNAME");
            password = System.getenv("GH_POSTGRES_PASSWORD");
        }
        else
        {
            Properties props = PropertyUtils.loadPropertiesFile("postgres.properties");
            url = props.getProperty("spring.datasource.url");
            username = props.getProperty("spring.datasource.username");
            password = props.getProperty("spring.datasource.password");
        }

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}
