/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.LiteralExpression;
import javax.annotation.Nullable;

public abstract class BooleanExpression
extends LiteralExpression<Boolean>
implements Predicate {
    private static final long serialVersionUID = 3797956062512074164L;
    @Nullable
    private volatile transient BooleanExpression eqTrue;
    @Nullable
    private volatile transient BooleanExpression eqFalse;
    @Nullable
    private volatile transient BooleanExpression not;

    public BooleanExpression(Expression<Boolean> mixin) {
        super(mixin);
    }

    public BooleanExpression as(Path<Boolean> alias) {
        return Expressions.booleanOperation(Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public BooleanExpression as(String alias) {
        return this.as((Path)ExpressionUtils.path(Boolean.class, alias));
    }

    public BooleanExpression and(@Nullable Predicate right) {
        if ((right = (Predicate)ExpressionUtils.extract(right)) != null) {
            return Expressions.booleanOperation(Ops.AND, this.mixin, right);
        }
        return this;
    }

    public BooleanExpression andAnyOf(Predicate ... predicates) {
        return this.and(ExpressionUtils.anyOf(predicates));
    }

    @Override
    public BooleanExpression not() {
        if (this.not == null) {
            this.not = Expressions.booleanOperation(Ops.NOT, this);
        }
        return this.not;
    }

    public BooleanExpression or(@Nullable Predicate right) {
        if ((right = (Predicate)ExpressionUtils.extract(right)) != null) {
            return Expressions.booleanOperation(Ops.OR, this.mixin, right);
        }
        return this;
    }

    public BooleanExpression orAllOf(Predicate ... predicates) {
        return this.or(ExpressionUtils.allOf(predicates));
    }

    public BooleanExpression isTrue() {
        return this.eq(true);
    }

    public BooleanExpression isFalse() {
        return this.eq(false);
    }

    @Override
    public BooleanExpression eq(Boolean right) {
        if (right.booleanValue()) {
            if (this.eqTrue == null) {
                this.eqTrue = super.eq(true);
            }
            return this.eqTrue;
        }
        if (this.eqFalse == null) {
            this.eqFalse = super.eq(false);
        }
        return this.eqFalse;
    }
}

