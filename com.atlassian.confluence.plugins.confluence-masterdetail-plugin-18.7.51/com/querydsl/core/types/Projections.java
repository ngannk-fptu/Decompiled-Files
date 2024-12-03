/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.AppendingFactoryExpression;
import com.querydsl.core.types.ArrayConstructorExpression;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.QList;
import com.querydsl.core.types.QMap;
import com.querydsl.core.types.QTuple;
import java.util.Map;

public final class Projections {
    public static <T> ArrayConstructorExpression<T> array(Class<T[]> type, Expression<T> ... exprs) {
        return new ArrayConstructorExpression<T>(type, exprs);
    }

    public static <T> AppendingFactoryExpression<T> appending(Expression<T> base, Expression<?> ... rest) {
        return new AppendingFactoryExpression<T>(base, rest);
    }

    public static <T> QBean<T> bean(Class<? extends T> type, Expression<?> ... exprs) {
        return new QBean<T>(type, exprs);
    }

    public static <T> QBean<T> bean(Path<? extends T> type, Expression<?> ... exprs) {
        return new QBean(type.getType(), exprs);
    }

    public static <T> QBean<T> bean(Path<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        return new QBean(type.getType(), bindings);
    }

    public static <T> QBean<T> bean(Class<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        return new QBean<T>(type, bindings);
    }

    public static <T> ConstructorExpression<T> constructor(Class<? extends T> type, Expression<?> ... exprs) {
        return new ConstructorExpression<T>(type, exprs);
    }

    public static <T> ConstructorExpression<T> constructor(Class<? extends T> type, Class<?>[] paramTypes, Expression<?> ... exprs) {
        return new ConstructorExpression<T>(type, paramTypes, exprs);
    }

    public static <T> ConstructorExpression<T> constructor(Class<? extends T> type, Class<?>[] paramTypes, ImmutableList<Expression<?>> exprs) {
        return new ConstructorExpression<T>(type, paramTypes, exprs);
    }

    public static <T> QBean<T> fields(Class<? extends T> type, Expression<?> ... exprs) {
        return new QBean<T>(type, true, exprs);
    }

    public static <T> QBean<T> fields(Path<? extends T> type, Expression<?> ... exprs) {
        return new QBean(type.getType(), true, exprs);
    }

    public static <T> QBean<T> fields(Path<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        return new QBean(type.getType(), true, bindings);
    }

    public static <T> QBean<T> fields(Class<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        return new QBean<T>(type, true, bindings);
    }

    public static QList list(Expression<?> ... args) {
        return new QList(args);
    }

    public static QList list(ImmutableList<Expression<?>> args) {
        return new QList(args);
    }

    public static QList list(Expression<?>[] ... args) {
        return new QList(args);
    }

    public static QMap map(Expression<?> ... exprs) {
        return new QMap(exprs);
    }

    public static QTuple tuple(Expression<?> ... exprs) {
        return new QTuple(exprs);
    }

    public static QTuple tuple(ImmutableList<Expression<?>> exprs) {
        return new QTuple(exprs);
    }

    public static QTuple tuple(Expression<?>[] ... exprs) {
        return new QTuple(exprs);
    }

    private Projections() {
    }
}

