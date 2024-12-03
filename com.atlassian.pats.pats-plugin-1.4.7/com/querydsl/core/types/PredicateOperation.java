/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PredicateOperation
extends OperationImpl<Boolean>
implements Predicate {
    private static final long serialVersionUID = -5371430939203772072L;
    @Nullable
    private volatile transient Predicate not;

    protected PredicateOperation(Operator operator, ImmutableList<Expression<?>> args) {
        super(Boolean.class, operator, args);
    }

    @Override
    public Predicate not() {
        if (this.not == null) {
            this.not = ExpressionUtils.predicate((Operator)Ops.NOT, this);
        }
        return this.not;
    }
}

