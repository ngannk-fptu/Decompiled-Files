/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.sql.WindowFirstLast;
import com.querydsl.sql.WindowFunction;

public class WindowOver<T>
extends SimpleOperation<T> {
    private static final long serialVersionUID = 464583892898579544L;

    public WindowOver(Class<? extends T> type, Operator op) {
        super(type, op, ImmutableList.of());
    }

    public WindowOver(Class<? extends T> type, Operator op, Expression<?> arg) {
        super(type, op, ImmutableList.of(arg));
    }

    public WindowOver(Class<? extends T> type, Operator op, Expression<?> arg1, Expression<?> arg2) {
        super(type, op, ImmutableList.of(arg1, arg2));
    }

    public WindowFirstLast<T> keepFirst() {
        return new WindowFirstLast(this, true);
    }

    public WindowFirstLast<T> keepLast() {
        return new WindowFirstLast(this, false);
    }

    public WindowFunction<T> over() {
        return new WindowFunction(this);
    }
}

