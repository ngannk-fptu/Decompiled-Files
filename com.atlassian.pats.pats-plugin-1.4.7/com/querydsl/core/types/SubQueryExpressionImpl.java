/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Visitor;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SubQueryExpressionImpl<T>
extends ExpressionBase<T>
implements SubQueryExpression<T> {
    private static final long serialVersionUID = 6775967804458163L;
    private final QueryMetadata metadata;

    public SubQueryExpressionImpl(Class<? extends T> type, QueryMetadata metadata) {
        super(type);
        this.metadata = metadata;
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SubQueryExpression) {
            SubQueryExpression s = (SubQueryExpression)o;
            return s.getMetadata().equals(this.metadata);
        }
        return false;
    }

    @Override
    public final QueryMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}

