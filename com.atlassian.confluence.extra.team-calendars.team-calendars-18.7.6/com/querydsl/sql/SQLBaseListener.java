/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLDetailedListener;
import com.querydsl.sql.SQLListenerContext;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;

public class SQLBaseListener
implements SQLDetailedListener {
    @Override
    public void start(SQLListenerContext context) {
    }

    @Override
    public void preRender(SQLListenerContext context) {
    }

    @Override
    public void rendered(SQLListenerContext context) {
    }

    @Override
    public void prePrepare(SQLListenerContext context) {
    }

    @Override
    public void prepared(SQLListenerContext context) {
    }

    @Override
    public void preExecute(SQLListenerContext context) {
    }

    @Override
    public void executed(SQLListenerContext context) {
    }

    @Override
    public void exception(SQLListenerContext context) {
    }

    @Override
    public void end(SQLListenerContext context) {
    }

    @Override
    public void notifyQuery(QueryMetadata md) {
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
    }
}

