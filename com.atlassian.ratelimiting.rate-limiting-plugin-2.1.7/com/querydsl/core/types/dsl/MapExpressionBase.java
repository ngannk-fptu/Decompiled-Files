/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class MapExpressionBase<K, V, Q extends SimpleExpression<? super V>>
extends DslExpression<Map<K, V>>
implements MapExpression<K, V> {
    private static final long serialVersionUID = 2856001983312366841L;
    @Nullable
    private volatile transient NumberExpression<Integer> size;
    @Nullable
    private volatile transient BooleanExpression empty;

    public MapExpressionBase(Expression<Map<K, V>> mixin) {
        super(mixin);
    }

    public final BooleanExpression contains(K key, V value) {
        return ((SimpleExpression)this.get(key)).eq(value);
    }

    public final BooleanExpression contains(Expression<K> key, Expression<V> value) {
        return ((SimpleExpression)this.get((K)key)).eq(value);
    }

    public final BooleanExpression containsKey(Expression<K> key) {
        return Expressions.booleanOperation(Ops.CONTAINS_KEY, this.mixin, key);
    }

    public final BooleanExpression containsKey(K key) {
        return Expressions.booleanOperation(Ops.CONTAINS_KEY, this.mixin, ConstantImpl.create(key));
    }

    public final BooleanExpression containsValue(Expression<V> value) {
        return Expressions.booleanOperation(Ops.CONTAINS_VALUE, this.mixin, value);
    }

    public final BooleanExpression containsValue(V value) {
        return Expressions.booleanOperation(Ops.CONTAINS_VALUE, this.mixin, ConstantImpl.create(value));
    }

    public abstract Q get(Expression<K> var1);

    public abstract Q get(K var1);

    public final BooleanExpression isEmpty() {
        if (this.empty == null) {
            this.empty = Expressions.booleanOperation(Ops.MAP_IS_EMPTY, this.mixin);
        }
        return this.empty;
    }

    public final BooleanExpression isNotEmpty() {
        return this.isEmpty().not();
    }

    public final NumberExpression<Integer> size() {
        if (this.size == null) {
            this.size = Expressions.numberOperation(Integer.class, Ops.MAP_SIZE, this.mixin);
        }
        return this.size;
    }
}

