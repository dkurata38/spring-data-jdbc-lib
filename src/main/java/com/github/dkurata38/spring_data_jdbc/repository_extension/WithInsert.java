package com.github.dkurata38.spring_data_jdbc.repository_extension;

public interface WithInsert<T> {
	T insert(T entity);
}
