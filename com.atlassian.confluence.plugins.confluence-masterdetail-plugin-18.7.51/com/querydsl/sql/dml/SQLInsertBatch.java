/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.sql.dml;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SQLInsertBatch {
    private final List<Path<?>> columns;
    private final List<Expression<?>> values;
    @Nullable
    private final SubQueryExpression<?> subQuery;

    public SQLInsertBatch(List<Path<?>> c, List<Expression<?>> v, @Nullable SubQueryExpression<?> sq) {
        this.columns = new ArrayList(c);
        this.values = new ArrayList(v);
        this.subQuery = sq;
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

