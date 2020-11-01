package com.github.dkurata38.spring_data_jdbc.repository_extension;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;

public class WithInsertImpl<T> implements WithInsert<T> {
	private final JdbcAggregateTemplate jdbcAggregateTemplate;

	public WithInsertImpl(JdbcAggregateTemplate jdbcAggregateTemplate) {
		this.jdbcAggregateTemplate = jdbcAggregateTemplate;
	}

	@Override
	public T insert(T instance) {
		return jdbcAggregateTemplate.insert(instance);
	}
}
