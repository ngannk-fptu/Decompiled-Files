/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.api.impl.pagination;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.stream.Collectors;

public interface PaginationQuery<F, T> {
    @Deprecated
    default public PaginationQuery<F, T> withPredicate(Predicate<F> predicate) {
        return this.createWithPredicate((java.util.function.Predicate<F>)predicate);
    }

    public PaginationQuery<F, T> createWithPredicate(java.util.function.Predicate<F> var1);

    @Deprecated
    public PaginationQuery<F, T> withModelConverter(Function<F, T> var1);

    default public PaginationQuery<F, T> createWithModelConverter(java.util.function.Function<F, T> modelConverter) {
        return this.withModelConverter(modelConverter::apply);
    }

    @Deprecated
    default public List<Predicate<F>> getPredicates() {
        return this.predicates().stream().map(p -> p::test).collect(Collectors.toList());
    }

    public List<java.util.function.Predicate<F>> predicates();

    @Deprecated
    public Function<F, T> getModelConverter();

    default public java.util.function.Function<F, T> modelConverter() {
        return arg_0 -> this.getModelConverter().apply(arg_0);
    }
}

