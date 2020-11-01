package com.github.dkurata38.spring_data_jdbc.repository_extension;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;

public class WithUpdateImpl<T> implements WithUpdate<T>{
	private final JdbcAggregateTemplate jdbcAggregateTemplate;

	public WithUpdateImpl(JdbcAggregateTemplate jdbcAggregateTemplate) {
		this.jdbcAggregateTemplate = jdbcAggregateTemplate;
	}

	@Override
	public T update(T instance) {
		return jdbcAggregateTemplate.update(instance);
	}
}
