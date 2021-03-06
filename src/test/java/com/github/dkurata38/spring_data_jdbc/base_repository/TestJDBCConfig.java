package com.github.dkurata38.spring_data_jdbc.base_repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@EnableJdbcRepositories(
		basePackages = "",
		repositoryFactoryBeanClass = UpdateOrInsertCrudRepositoryFactoryBean.class
)
@Configuration
public class TestJDBCConfig {

}
