/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.HashCodeVisitor;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import javax.annotation.Nullable;

public abstract class ExpressionBase<T>
implements Expression<T> {
    private static final long serialVersionUID = -8862014178653364345L;
    private final Class<? extends T> type;
    @Nullable
    private volatile transient String toString;
    @Nullable
    private volatile transient Integer hashCode;

    public ExpressionBase(Class<? extends T> type) {
        this.type = type;
    }

    @Override
    public final Class<? extends T> getType() {
        return this.type;
    }

    public final int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.accept(HashCodeVisitor.DEFAULT, null);
        }
        return this.hashCode;
    }

    public final String toString() {
        if (this.toString == null) {
            this.toString = this.accept(ToStringVisitor.DEFAULT, Templates.DEFAULT);
        }
        return this.toString;
    }
}

