/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 */
package org.springframework.data.repository.support;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.support.ReflectionRepositoryInvoker;

class CrudRepositoryInvoker
extends ReflectionRepositoryInvoker {
    private final CrudRepository<Object, Object> repository;
    private final boolean customSaveMethod;
    private final boolean customFindOneMethod;
    private final boolean customFindAllMethod;
    private final boolean customDeleteMethod;

    public CrudRepositoryInvoker(CrudRepository<Object, Object> repository, RepositoryMetadata metadata, ConversionService conversionService) {
        super(repository, metadata, conversionService);
        CrudMethods crudMethods = metadata.getCrudMethods();
        this.customSaveMethod = CrudRepositoryInvoker.isRedeclaredMethod(crudMethods.getSaveMethod());
        this.customFindOneMethod = CrudRepositoryInvoker.isRedeclaredMethod(crudMethods.getFindOneMethod());
        this.customDeleteMethod = CrudRepositoryInvoker.isRedeclaredMethod(crudMethods.getDeleteMethod());
        this.customFindAllMethod = CrudRepositoryInvoker.isRedeclaredMethod(crudMethods.getFindAllMethod());
        this.repository = repository;
    }

    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return this.customFindAllMethod ? super.invokeFindAll(sort) : this.repository.findAll();
    }

    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return this.customFindAllMethod ? super.invokeFindAll(pageable) : this.repository.findAll();
    }

    @Override
    public <T> Optional<T> invokeFindById(Object id) {
        return this.customFindOneMethod ? super.invokeFindById(id) : this.repository.findById(this.convertId(id));
    }

    @Override
    public <T> T invokeSave(T entity) {
        return this.customSaveMethod ? super.invokeSave(entity) : this.repository.save(entity);
    }

    @Override
    public void invokeDeleteById(Object id) {
        if (this.customDeleteMethod) {
            super.invokeDeleteById(id);
        } else {
            this.repository.deleteById(this.convertId(id));
        }
    }

    private static boolean isRedeclaredMethod(Optional<Method> method) {
        return method.map(it -> !it.getDeclaringClass().equals(CrudRepository.class)).orElse(false);
    }
}

