/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.pats.rest;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.Collection;
import java.util.function.Function;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class OptionalBooleanBuilder {
    private final BooleanExpression predicate;

    public OptionalBooleanBuilder(BooleanExpression predicate) {
        this.predicate = predicate;
    }

    public <T> OptionalBooleanBuilder notNullAnd(Function<T, BooleanExpression> expressionFunction, T value) {
        return value != null ? new OptionalBooleanBuilder(this.predicate.and(expressionFunction.apply(value))) : this;
    }

    public OptionalBooleanBuilder notEmptyAnd(Function<String, BooleanExpression> expressionFunction, String value) {
        return !StringUtils.isEmpty((Object)value) ? new OptionalBooleanBuilder(this.predicate.and(expressionFunction.apply(value))) : this;
    }

    public <T> OptionalBooleanBuilder notEmptyAnd(Function<Collection<T>, BooleanExpression> expressionFunction, Collection<T> collection) {
        return !CollectionUtils.isEmpty(collection) ? new OptionalBooleanBuilder(this.predicate.and(expressionFunction.apply(collection))) : this;
    }

    public <T> OptionalBooleanBuilder notNullAndValueExistenceCheck(SimpleExpression<T> fieldToCheck, Boolean shouldBeSet) {
        return shouldBeSet != null ? new OptionalBooleanBuilder(this.predicate.and(shouldBeSet != false ? fieldToCheck.isNotNull() : fieldToCheck.isNull())) : this;
    }

    public BooleanExpression build() {
        return this.predicate;
    }
}

