/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;

public final class StringExpressions {
    public static StringExpression ltrim(Expression<String> str) {
        return Expressions.stringOperation(Ops.StringOps.LTRIM, str);
    }

    public static StringExpression rtrim(Expression<String> str) {
        return Expressions.stringOperation(Ops.StringOps.RTRIM, str);
    }

    public static StringExpression lpad(Expression<String> in, int length) {
        return Expressions.stringOperation(Ops.StringOps.LPAD, in, ConstantImpl.create(length));
    }

    public static StringExpression lpad(Expression<String> in, Expression<Integer> length) {
        return Expressions.stringOperation(Ops.StringOps.LPAD, in, length);
    }

    public static StringExpression rpad(Expression<String> in, int length) {
        return Expressions.stringOperation(Ops.StringOps.RPAD, in, ConstantImpl.create(length));
    }

    public static StringExpression rpad(Expression<String> in, Expression<Integer> length) {
        return Expressions.stringOperation(Ops.StringOps.RPAD, in, length);
    }

    public static StringExpression lpad(Expression<String> in, NumberExpression<Integer> length, char c) {
        return Expressions.stringOperation(Ops.StringOps.LPAD2, in, length, ConstantImpl.create(c));
    }

    public static StringExpression lpad(Expression<String> in, int length, char c) {
        return Expressions.stringOperation(Ops.StringOps.LPAD2, in, ConstantImpl.create(length), ConstantImpl.create(c));
    }

    public static StringExpression rpad(Expression<String> in, NumberExpression<Integer> length, char c) {
        return Expressions.stringOperation(Ops.StringOps.RPAD2, in, length, ConstantImpl.create(c));
    }

    public static StringExpression rpad(Expression<String> in, int length, char c) {
        return Expressions.stringOperation(Ops.StringOps.RPAD2, in, ConstantImpl.create(length), ConstantImpl.create(c));
    }

    private StringExpressions() {
    }
}

