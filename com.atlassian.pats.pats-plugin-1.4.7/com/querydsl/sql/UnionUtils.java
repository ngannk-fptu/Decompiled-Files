/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.SQLOps;
import java.util.List;

final class UnionUtils {
    public static <T> Expression<T> union(List<SubQueryExpression<T>> union, boolean unionAll) {
        SQLOps operator = unionAll ? SQLOps.UNION_ALL : SQLOps.UNION;
        Operation rv = (Operation)((Object)union.get(0));
        for (int i = 1; i < union.size(); ++i) {
            rv = ExpressionUtils.operation(rv.getType(), (Operator)operator, rv, union.get(i));
        }
        return rv;
    }

    public static <T> Expression<T> union(List<SubQueryExpression<T>> union, Path<T> alias, boolean unionAll) {
        Expression<T> rv = UnionUtils.union(union, unionAll);
        return ExpressionUtils.as(rv, alias);
    }

    private UnionUtils() {
    }
}

