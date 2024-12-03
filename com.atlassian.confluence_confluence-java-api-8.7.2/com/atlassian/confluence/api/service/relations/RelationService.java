/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.relations;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

@ExperimentalApi
public interface RelationService {
    public <S extends Relatable, T extends Relatable> RelationInstance<S, T> create(RelationInstance<S, T> var1) throws ServiceException;

    public <S extends Relatable, T extends Relatable> void delete(RelationInstance<S, T> var1) throws ServiceException;

    public <S extends Relatable, T extends Relatable> boolean isRelated(S var1, RelationDescriptor<S, T> var2, T var3);

    public Validator validator();

    public <S extends Relatable, T extends Relatable> RelatableFinder<T> findTargets(S var1, RelationDescriptor<S, T> var2);

    public <S extends Relatable, T extends Relatable> RelatableFinder<S> findSources(T var1, RelationDescriptor<S, T> var2);

    public <S extends Relatable, T extends Relatable> void removeAllRelationsFromEntityWithType(RelationDescriptor<S, T> var1, Relatable var2);

    public static interface RelatableFinder<R extends Relatable> {
        public PageResponse<R> fetchMany(PageRequest var1, Expansion ... var2) throws ServiceException;

        public int fetchCount();
    }

    public static interface Validator {
        public <S extends Relatable, T extends Relatable> ValidationResult validateCreate(S var1, RelationDescriptor<S, T> var2, T var3);

        public <S extends Relatable, T extends Relatable> ValidationResult validateDelete(S var1, RelationDescriptor<S, T> var2, T var3);

        public <S extends Relatable, T extends Relatable> ValidationResult validateFetch(S var1, RelationDescriptor<S, T> var2, T var3);

        public <S extends Relatable, T extends Relatable> ValidationResult validateDeleteAllWithType(Relatable var1, RelationDescriptor<S, T> var2);
    }
}

