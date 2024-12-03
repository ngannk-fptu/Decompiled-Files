/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.KeyValueOperations
 *  org.springframework.data.keyvalue.repository.query.KeyValuePartTreeQuery
 *  org.springframework.data.keyvalue.repository.support.KeyValueRepositoryFactory
 *  org.springframework.data.repository.core.EntityInformation
 *  org.springframework.data.repository.query.RepositoryQuery
 *  org.springframework.data.repository.query.parser.AbstractQueryCreator
 */
package org.springframework.vault.repository.support;

import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.query.KeyValuePartTreeQuery;
import org.springframework.data.keyvalue.repository.support.KeyValueRepositoryFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.vault.repository.core.MappingVaultEntityInformation;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.query.VaultQueryCreator;

public class VaultRepositoryFactory
extends KeyValueRepositoryFactory {
    private final KeyValueOperations operations;

    public VaultRepositoryFactory(KeyValueOperations keyValueOperations) {
        this(keyValueOperations, VaultQueryCreator.class);
    }

    public VaultRepositoryFactory(KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator) {
        this(keyValueOperations, queryCreator, KeyValuePartTreeQuery.class);
    }

    public VaultRepositoryFactory(KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
        super(keyValueOperations, queryCreator, repositoryQueryType);
        this.operations = keyValueOperations;
    }

    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        VaultPersistentEntity entity = (VaultPersistentEntity)this.operations.getMappingContext().getPersistentEntity(domainClass);
        return new MappingVaultEntityInformation(entity);
    }
}

