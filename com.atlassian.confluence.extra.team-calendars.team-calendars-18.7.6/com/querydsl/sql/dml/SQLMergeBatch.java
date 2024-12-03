/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.dml;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SQLMergeBatch {
    private final List<Path<?>> keys;
    private final List<Path<?>> columns;
    private final List<Expression<?>> values;
    @Nullable
    private final SubQueryExpression<?> subQuery;

    public SQLMergeBatch(List<Path<?>> k, List<Path<?>> c, List<Expression<?>> v, @Nullable SubQueryExpression<?> sq) {
        this.keys = new ArrayList(k);
        this.columns = new ArrayList(c);
        this.values = new ArrayList(v);
        this.subQuery = sq;
    }

    public List<Path<?>> getKeys() {
        return this.keys;
    }

    public List<Path<?>> getColumns() {
        return this.columns;
    }

    public List<Expression<?>> getValues() {
        return this.values;
    }

    public SubQueryExpression<?> getSubQuery() {
        return this.subQuery;
    }
}

