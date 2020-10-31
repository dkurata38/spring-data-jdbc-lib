package com.github.dkurata38.explicit_decide_update;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class ExplicitInsertUpdateRepositoryImpl<T, ID> implements BulkExecutableRepository<T, ID> {
	private final JdbcAggregateOperations jdbcAggregateOperations;
	private final PersistentEntity<T, ?> entity;

	public ExplicitInsertUpdateRepositoryImpl(JdbcAggregateOperations jdbcAggregateOperations, PersistentEntity<T, ?> entity) {
		this.jdbcAggregateOperations = jdbcAggregateOperations;
		this.entity = entity;
	}

	@Override
	public <S extends T> S insert(S instance) {
		return jdbcAggregateOperations.insert(instance);
	}

	@Override
	public <S extends T> Iterable<S> insertAll(Iterable<S> entities) {
		return Streamable.of(entities)
				.stream()
				.map(this::insert)
				.collect(Collectors.toList());
	}

	@Override
	public <S extends T> S update(S instance) {
		return jdbcAggregateOperations.update(instance);
	}

	@Override
	public <S extends T> Iterable<S> updateAll(Iterable<S> entities) {
		return Streamable.of(entities)
				.stream()
				.map(this::update)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(jdbcAggregateOperations.findById(id, entity.getType()));
	}

	@Override
	public boolean existsById(ID id) {
		return jdbcAggregateOperations.existsById(id, entity.getType());
	}

	@Override
	public Iterable<T> findAll() {
		return jdbcAggregateOperations.findAll(entity.getType());
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		return jdbcAggregateOperations.findAllById(ids, entity.getType());
	}

	@Override
	public long count() {
		return jdbcAggregateOperations.count(entity.getType());
	}

	@Override
	public void deleteById(ID id) {
		jdbcAggregateOperations.deleteById(id, entity.getType());
	}

	@Override
	public void delete(T instance) {
		jdbcAggregateOperations.delete(instance, entity.getType());
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		Streamable.of(entities)
				.stream()
				.forEach(this::delete);
	}

	@Override
	public void deleteAll() {
		jdbcAggregateOperations.deleteAll(entity.getType());
	}
}
