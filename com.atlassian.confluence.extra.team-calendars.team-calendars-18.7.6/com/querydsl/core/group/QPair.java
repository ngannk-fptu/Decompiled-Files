/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;

public final class QPair<K, V>
extends ConstructorExpression<Pair<K, V>> {
    private static final long serialVersionUID = -1943990903548916056L;

    public static <K, V> QPair<K, V> create(Expression<K> key, Expression<V> value) {
        return new QPair<K, V>(key, value);
    }

    public QPair(Expression<K> key, Expression<V> value) {
        super(Pair.class, new Class[]{Object.class, Object.class}, key, value);
    }

    public boolean equals(Expression<?> keyExpr, Expression<?> valueExpr) {
        return this.getArgs().get(0).equals(keyExpr) && this.getArgs().get(1).equals(valueExpr);
    }

    public boolean equals(Expression<?> keyExpr, Class<?> valueType) {
        return this.getArgs().get(0).equals(keyExpr) && valueType.isAssignableFrom(this.getArgs().get(1).getType());
    }
}

