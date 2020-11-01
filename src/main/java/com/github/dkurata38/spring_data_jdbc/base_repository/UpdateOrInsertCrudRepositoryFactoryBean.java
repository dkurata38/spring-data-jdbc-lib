package com.github.dkurata38.spring_data_jdbc.base_repository;

import java.io.Serializable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

public class UpdateOrInsertCrudRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
		extends JdbcRepositoryFactoryBean<T, S, ID> {
	private ApplicationEventPublisher publisher;
	private BeanFactory beanFactory;
	private RelationalMappingContext mappingContext;
	private JdbcConverter converter;
	private DataAccessStrategy dataAccessStrategy;
	private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
	private NamedParameterJdbcOperations operations;
	private EntityCallbacks entityCallbacks;
	private Dialect dialect;

	/**
	 * Creates a new {@link JdbcRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	protected UpdateOrInsertCrudRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#setApplicationEventPublisher(org.springframework.context.ApplicationEventPublisher)
	 */
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		super.setApplicationEventPublisher(publisher);
		this.publisher = publisher;
	}

	/**
	 * Creates the actual {@link RepositoryFactorySupport} instance.
	 */
	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {

		UpdateOrInsertCrudRepositoryFactory<T, ID> updateOrInsertCrudRepositoryFactory = new UpdateOrInsertCrudRepositoryFactory<>(dataAccessStrategy, mappingContext,
				converter, dialect, publisher, operations);
		updateOrInsertCrudRepositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
		updateOrInsertCrudRepositoryFactory.setEntityCallbacks(entityCallbacks);

		return updateOrInsertCrudRepositoryFactory;
	}

	@Autowired
	protected void setMappingContext(RelationalMappingContext mappingContext) {

		Assert.notNull(mappingContext, "MappingContext must not be null");

		super.setMappingContext(mappingContext);
		this.mappingContext = mappingContext;
	}

	@Autowired
	protected void setDialect(Dialect dialect) {

		Assert.notNull(dialect, "Dialect must not be null");
		super.setDialect(dialect);
		this.dialect = dialect;
	}

	/**
	 * @param dataAccessStrategy can be {@literal null}.
	 */
	public void setDataAccessStrategy(DataAccessStrategy dataAccessStrategy) {

		Assert.notNull(dataAccessStrategy, "DataAccessStrategy must not be null");
		super.setDataAccessStrategy(dataAccessStrategy);
		this.dataAccessStrategy = dataAccessStrategy;
	}

	/**
	 * @param queryMappingConfiguration can be {@literal null}. {@link #afterPropertiesSet()} defaults to
	 *          {@link QueryMappingConfiguration#EMPTY} if {@literal null}.
	 */
	@Autowired(required = false)
	public void setQueryMappingConfiguration(QueryMappingConfiguration queryMappingConfiguration) {

		Assert.notNull(queryMappingConfiguration, "QueryMappingConfiguration must not be null");
		super.setQueryMappingConfiguration(queryMappingConfiguration);
		this.queryMappingConfiguration = queryMappingConfiguration;
	}

	public void setJdbcOperations(NamedParameterJdbcOperations operations) {

		Assert.notNull(operations, "NamedParameterJdbcOperations must not be null");
		super.setJdbcOperations(operations);
		this.operations = operations;
	}

	@Autowired
	public void setConverter(JdbcConverter converter) {

		Assert.notNull(converter, "JdbcConverter must not be null");
		super.setConverter(converter);
		this.converter = converter;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {

		super.setBeanFactory(beanFactory);

		this.beanFactory = beanFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {

		Assert.state(this.mappingContext != null, "MappingContext is required and must not be null!");
		Assert.state(this.converter != null, "RelationalConverter is required and must not be null!");

		if (this.operations == null) {

			Assert.state(beanFactory != null, "If no JdbcOperations are set a BeanFactory must be available.");

			this.operations = beanFactory.getBean(NamedParameterJdbcOperations.class);
		}

		if (this.dataAccessStrategy == null) {

			Assert.state(beanFactory != null, "If no DataAccessStrategy is set a BeanFactory must be available.");

			this.dataAccessStrategy = this.beanFactory.getBeanProvider(DataAccessStrategy.class) //
					.getIfAvailable(() -> {

						Assert.state(this.dialect != null, "Dialect is required and must not be null!");

						SqlGeneratorSource sqlGeneratorSource = new SqlGeneratorSource(this.mappingContext, this.converter,
								this.dialect);
						return new DefaultDataAccessStrategy(sqlGeneratorSource, this.mappingContext, this.converter,
								this.operations);
					});
		}

		if (this.queryMappingConfiguration == null) {
			this.queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
		}

		if (beanFactory != null) {
			entityCallbacks = EntityCallbacks.create(beanFactory);
		}

		super.afterPropertiesSet();
	}

	private static class UpdateOrInsertCrudRepositoryFactory<T, ID extends Serializable> extends JdbcRepositoryFactory {
		private final DataAccessStrategy dataAccessStrategy;
		private final RelationalMappingContext context;
		private final JdbcConverter converter;
		private final ApplicationEventPublisher publisher;
		private EntityCallbacks entityCallbacks;

		public UpdateOrInsertCrudRepositoryFactory(DataAccessStrategy dataAccessStrategy, RelationalMappingContext context, JdbcConverter converter, Dialect dialect, ApplicationEventPublisher publisher, NamedParameterJdbcOperations operations) {
			super(dataAccessStrategy, context, converter, dialect, publisher, operations);
			this.dataAccessStrategy = dataAccessStrategy;
			this.context = context;
			this.converter = converter;
			this.publisher = publisher;
		}

		@Override
		protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
			JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, dataAccessStrategy);

			ExplicitInsertUpdateRepository<?, Object> repository = new ExplicitInsertUpdateRepositoryImpl<>(template,
					context.getRequiredPersistentEntity(repositoryInformation.getDomainType()));

			if (entityCallbacks != null) {
				template.setEntityCallbacks(entityCallbacks);
			}

			return repository;
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
			return ExplicitInsertUpdateRepositoryImpl.class;
		}

		@Override
		public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
			super.setEntityCallbacks(entityCallbacks);
			this.entityCallbacks = entityCallbacks;
		}
	}
}
