/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.ActiveObjectsRepository;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryLookupStrategy;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethodFactory;
import com.atlassian.data.activeobjects.repository.query.DefaultActiveObjectsQueryMethodFactory;
import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityInformation;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsPersistableEntityInformation;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeCrudQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslPocketKnifeCrudPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslPocketKnifeReadOnlyPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslUtils;
import com.atlassian.data.activeobjects.repository.support.SimpleActiveObjectsRepository;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import java.util.Optional;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ActiveObjectsRepositoryFactory
extends RepositoryFactorySupport {
    private final ActiveObjects entityManager;
    private EntityPathResolver entityPathResolver;
    private ActiveObjectsQueryMethodFactory queryMethodFactory;
    private final DatabaseAccessor databaseAccessor;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    private PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType = PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION;

    public ActiveObjectsRepositoryFactory(ActiveObjects entityManager, DatabaseAccessor databaseAccessor) {
        Assert.notNull((Object)entityManager, (String)"ActiveObjects must not be null!");
        this.entityManager = entityManager;
        this.databaseAccessor = databaseAccessor;
        this.entityPathResolver = SimpleEntityPathResolver.INSTANCE;
        this.queryMethodFactory = new DefaultActiveObjectsQueryMethodFactory();
    }

    public void setEntityPathResolver(EntityPathResolver entityPathResolver) {
        this.entityPathResolver = entityPathResolver;
    }

    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public void setQueryMethodFactory(ActiveObjectsQueryMethodFactory queryMethodFactory) {
        Assert.notNull((Object)queryMethodFactory, (String)"QueryMethodFactory must not be null!");
        this.queryMethodFactory = queryMethodFactory;
    }

    @Override
    protected final Object getTargetRepository(RepositoryInformation information) {
        return this.getTargetRepository(information, this.entityManager);
    }

    protected Object getTargetRepository(RepositoryInformation information, ActiveObjects entityManager) {
        EntityInformation entityInformation = this.getEntityInformation((Class)information.getDomainType());
        Object repository = this.getTargetRepositoryViaReflection(information, entityInformation, entityManager);
        Assert.isInstanceOf(ActiveObjectsRepository.class, repository);
        return repository;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleActiveObjectsRepository.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(ActiveObjectsQueryLookupStrategy.create(this.entityManager, this.queryMethodFactory, key, evaluationContextProvider, this.databaseAccessor, this.entityPathResolver, this.escapeCharacter));
    }

    public <T, ID> ActiveObjectsEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        Assert.notNull(domainClass, (String)"Domain class must not be null!");
        return new ActiveObjectsPersistableEntityInformation(domainClass);
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        boolean isQueryDslRepository;
        RepositoryComposition.RepositoryFragments fragments = RepositoryComposition.RepositoryFragments.empty();
        boolean bl = isQueryDslRepository = QuerydslUtils.QUERY_DSL_PRESENT && QuerydslPredicateExecutor.class.isAssignableFrom(metadata.getRepositoryInterface());
        if (isQueryDslRepository) {
            if (metadata.isReactiveRepository()) {
                throw new InvalidDataAccessApiUsageException("Cannot combine Querydsl and reactive repository support in a single interface");
            }
            EnhancedRelationalPathBase entityPath = (EnhancedRelationalPathBase)this.entityPathResolver.createPath(metadata.getDomainType());
            Class qdslClassImpl = PocketKnifeCrudQuerydslPredicateExecutor.class.isAssignableFrom(metadata.getRepositoryInterface()) ? QuerydslPocketKnifeCrudPredicateExecutor.class : QuerydslPocketKnifeReadOnlyPredicateExecutor.class;
            Object querydslFragment = this.getTargetRepositoryViaReflection(qdslClassImpl, new Object[]{entityPath, this.databaseAccessor, this.transactionType});
            fragments = fragments.append(RepositoryFragment.implemented(querydslFragment));
        }
        return fragments;
    }

    public void setTransactionType(PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

