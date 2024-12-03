/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.querydsl.core;

import com.google.common.base.Objects;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import javax.annotation.Nullable;

public final class BooleanBuilder
implements Predicate,
Cloneable {
    private static final long serialVersionUID = -4129485177345542519L;
    @Nullable
    private Predicate predicate;

    public BooleanBuilder() {
    }

    public BooleanBuilder(Predicate initial) {
        this.predicate = (Predicate)ExpressionUtils.extract(initial);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        if (this.predicate != null) {
            return this.predicate.accept(v, context);
        }
        return null;
    }

    public BooleanBuilder and(@Nullable Predicate right) {
        if (right != null) {
            this.predicate = this.predicate == null ? right : ExpressionUtils.and(this.predicate, right);
        }
        return this;
    }

    public BooleanBuilder andAnyOf(Predicate ... args) {
        if (args.length > 0) {
            this.and(ExpressionUtils.anyOf(args));
        }
        return this;
    }

    public BooleanBuilder andNot(Predicate right) {
        return this.and(right.not());
    }

    public BooleanBuilder clone() throws CloneNotSupportedException {
        return (BooleanBuilder)super.clone();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BooleanBuilder) {
            return Objects.equal((Object)((BooleanBuilder)o).getValue(), (Object)this.predicate);
        }
        return false;
    }

    @Nullable
    public Predicate getValue() {
        return this.predicate;
    }

    public int hashCode() {
        return this.predicate != null ? this.predicate.hashCode() : 0;
    }

    public boolean hasValue() {
        return this.predicate != null;
    }

    @Override
    public BooleanBuilder not() {
        if (this.predicate != null) {
            this.predicate = this.predicate.not();
        }
        return this;
    }

    public BooleanBuilder or(@Nullable Predicate right) {
        if (right != null) {
            this.predicate = this.predicate == null ? right : ExpressionUtils.or(this.predicate, right);
        }
        return this;
    }

    public BooleanBuilder orAllOf(Predicate ... args) {
        if (args.length > 0) {
            this.or(ExpressionUtils.allOf(args));
        }
        return this;
    }

    public BooleanBuilder orNot(Predicate right) {
        return this.or(right.not());
    }

    @Override
    public Class<? extends Boolean> getType() {
        return Boolean.class;
    }

    public String toString() {
        return this.predicate != null ? this.predicate.toString() : super.toString();
    }
}

