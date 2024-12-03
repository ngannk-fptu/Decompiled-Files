/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import java.util.Collection;
import javax.annotation.Nullable;

public abstract class CollectionExpressionBase<T extends Collection<E>, E>
extends DslExpression<T>
implements CollectionExpression<T, E> {
    private static final long serialVersionUID = 691230660037162054L;
    @Nullable
    private volatile transient BooleanExpression empty;
    @Nullable
    private volatile transient NumberExpression<Integer> size;

    public CollectionExpressionBase(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public DslExpression<E> as(EntityPath<E> alias) {
        return Expressions.dslOperation(this.getElementType(), Ops.ALIAS, this.mixin, alias);
    }

    public final BooleanExpression contains(E child) {
        return this.contains((Expression<E>)ConstantImpl.create(child));
    }

    public final BooleanExpression contains(Expression<E> child) {
        return Expressions.booleanOperation(Ops.IN, child, this.mixin);
    }

    public abstract Class<E> getElementType();

    public final BooleanExpression isEmpty() {
        if (this.empty == null) {
            this.empty = Expressions.booleanOperation(Ops.COL_IS_EMPTY, this.mixin);
        }
        return this.empty;
    }

    public final BooleanExpression isNotEmpty() {
        return this.isEmpty().not();
    }

    public final NumberExpression<Integer> size() {
        if (this.size == null) {
            this.size = Expressions.numberOperation(Integer.class, Ops.COL_SIZE, this.mixin);
        }
        return this.size;
    }
}

