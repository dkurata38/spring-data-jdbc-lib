package com.github.dkurata38.spring_data_jdbc.repository_extension;

public interface WithUpdate<T> {
	T update(T entity);
}
