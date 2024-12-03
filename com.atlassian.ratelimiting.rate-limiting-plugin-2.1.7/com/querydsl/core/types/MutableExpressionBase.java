/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.HashCodeVisitor;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;

public abstract class MutableExpressionBase<T>
implements Expression<T> {
    private static final long serialVersionUID = -6830426684911919114L;
    private final Class<? extends T> type;

    public MutableExpressionBase(Class<? extends T> type) {
        this.type = type;
    }

    @Override
    public final Class<? extends T> getType() {
        return this.type;
    }

    public final int hashCode() {
        return this.accept(HashCodeVisitor.DEFAULT, null);
    }

    public final String toString() {
        return this.accept(ToStringVisitor.DEFAULT, Templates.DEFAULT);
    }
}

