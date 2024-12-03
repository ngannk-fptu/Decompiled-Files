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
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.support.CrudRepositoryInvoker;

class PagingAndSortingRepositoryInvoker
extends CrudRepositoryInvoker {
    private final PagingAndSortingRepository<Object, Object> repository;
    private final boolean customFindAll;

    public PagingAndSortingRepositoryInvoker(PagingAndSortingRepository<Object, Object> repository, RepositoryMetadata metadata, ConversionService conversionService) {
        super(repository, metadata, conversionService);
        CrudMethods crudMethods = metadata.getCrudMethods();
        this.repository = repository;
        this.customFindAll = PagingAndSortingRepositoryInvoker.isRedeclaredMethod(crudMethods.getFindAllMethod());
    }

    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return this.customFindAll ? this.invokeFindAllReflectively(sort) : this.repository.findAll(sort);
    }

    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return this.customFindAll ? this.invokeFindAllReflectively(pageable) : this.repository.findAll(pageable);
    }

    private static boolean isRedeclaredMethod(Optional<Method> method) {
        return method.map(it -> !it.getDeclaringClass().equals(PagingAndSortingRepository.class)).orElse(false);
    }
}

