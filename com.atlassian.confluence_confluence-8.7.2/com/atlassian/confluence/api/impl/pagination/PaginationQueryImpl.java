/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.confluence.api.impl.pagination.PaginationQuery;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PaginationQueryImpl<F, T>
implements PaginationQuery<F, T> {
    private List<Predicate<F>> predicates = new ArrayList<Predicate<F>>();
    private Function<F, T> modelConverter;

    @Override
    public PaginationQuery<F, T> createWithPredicate(Predicate<F> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    @Override
    @Deprecated
    public PaginationQuery<F, T> withModelConverter(Function<F, T> modelConverter) {
        this.modelConverter = modelConverter;
        return this;
    }

    @Override
    public List<Predicate<F>> predicates() {
        return ImmutableList.copyOf(this.predicates);
    }

    @Override
    @Deprecated
    public Function<F, T> getModelConverter() {
        return this.modelConverter;
    }

    public static <F, T> PaginationQuery<F, T> newQuery() {
        return new PaginationQueryImpl<F, T>();
    }

    @Deprecated
    public static <F, T> PaginationQuery<F, T> newQuery(Function<F, T> modelConverter) {
        return new PaginationQueryImpl<Object, Object>().createWithModelConverter(arg_0 -> modelConverter.apply(arg_0));
    }

    public static <F, T> PaginationQuery<F, T> createNewQuery(java.util.function.Function<F, T> modelConverter) {
        return PaginationQueryImpl.newQuery(modelConverter::apply);
    }

    public static <T> PaginationQuery<T, T> newIdentityQuery() {
        return new PaginationQueryImpl().withModelConverter(Functions.identity());
    }

    public static <T> PaginationQuery<T, T> newIdentityQuery(Class<T> clazz) {
        return PaginationQueryImpl.newIdentityQuery();
    }
}

