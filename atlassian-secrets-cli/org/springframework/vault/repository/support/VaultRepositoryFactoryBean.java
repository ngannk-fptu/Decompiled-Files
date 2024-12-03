/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.KeyValueOperations
 *  org.springframework.data.keyvalue.repository.support.KeyValueRepositoryFactoryBean
 *  org.springframework.data.repository.Repository
 *  org.springframework.data.repository.query.RepositoryQuery
 *  org.springframework.data.repository.query.parser.AbstractQueryCreator
 */
package org.springframework.vault.repository.support;

import java.io.Serializable;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.support.KeyValueRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.vault.repository.support.VaultRepositoryFactory;

public class VaultRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
extends KeyValueRepositoryFactoryBean<T, S, ID> {
    public VaultRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    protected VaultRepositoryFactory createRepositoryFactory(KeyValueOperations operations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
        return new VaultRepositoryFactory(operations, queryCreator, repositoryQueryType);
    }
}

