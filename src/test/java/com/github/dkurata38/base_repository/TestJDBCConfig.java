package com.github.dkurata38.explicit_decide_update;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@EnableJdbcRepositories(
		basePackages = "",
		repositoryFactoryBeanClass = UpdateOrInsertCrudRepositoryFactoryBean.class
)
@Configuration
public class TestJDBCConfig {

}
