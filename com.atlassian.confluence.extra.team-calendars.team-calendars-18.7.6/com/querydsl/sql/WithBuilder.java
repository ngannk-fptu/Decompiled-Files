/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.sql.SQLOps;

public class WithBuilder<R> {
    private final QueryMixin<R> queryMixin;
    private final Expression<?> alias;

    public WithBuilder(QueryMixin<R> queryMixin, Expression<?> alias) {
        this.queryMixin = queryMixin;
        this.alias = alias;
    }

    public R as(Expression<?> expr) {
        Operation<?> flag = ExpressionUtils.operation(this.alias.getType(), (Operator)SQLOps.WITH_ALIAS, this.alias, expr);
        return this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, flag));
    }
}

