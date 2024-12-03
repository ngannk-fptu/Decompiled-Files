/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsRepositoryFactory;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ActiveObjectsRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
extends RepositoryFactoryBeanSupport<T, S, ID> {
    @Nullable
    private ActiveObjects entityManager;
    private EntityPathResolver entityPathResolver;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    @Nullable
    private DatabaseAccessor databaseAccessor;
    private PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType = PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION;

    public ActiveObjectsRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Autowired
    public void setEntityManager(ActiveObjects entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setDatabaseAccessor(ObjectProvider<DatabaseAccessor> databaseAccessor) {
        this.databaseAccessor = (DatabaseAccessor)databaseAccessor.getIfAvailable(() -> null);
    }

    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
    }

    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        this.entityPathResolver = (EntityPathResolver)resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    protected RepositoryFactorySupport createRepositoryFactory(ActiveObjects entityManager) {
        ActiveObjectsRepositoryFactory aoRepositoryFactory = new ActiveObjectsRepositoryFactory(entityManager, this.databaseAccessor);
        aoRepositoryFactory.setEntityPathResolver(this.entityPathResolver);
        aoRepositoryFactory.setEscapeCharacter(this.escapeCharacter);
        aoRepositoryFactory.setTransactionType(this.transactionType);
        return aoRepositoryFactory;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.state((this.entityManager != null ? 1 : 0) != 0, (String)"EntityManager must not be null!");
        super.afterPropertiesSet();
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        Assert.state((this.entityManager != null ? 1 : 0) != 0, (String)"EntityManager must not be null!");
        return this.createRepositoryFactory(this.entityManager);
    }

    public void setEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
    }

    public void setTransactionType(PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

