/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import java.util.List;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.query.QueryMethod;

public interface RepositoryFactoryInformation<T, ID> {
    public EntityInformation<T, ID> getEntityInformation();

    public RepositoryInformation getRepositoryInformation();

    public PersistentEntity<?, ?> getPersistentEntity();

    public List<QueryMethod> getQueryMethods();
}

