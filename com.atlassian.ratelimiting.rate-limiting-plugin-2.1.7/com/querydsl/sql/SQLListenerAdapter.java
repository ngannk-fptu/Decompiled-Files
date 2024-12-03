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
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListenerContext;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;

class SQLListenerAdapter
implements SQLDetailedListener {
    private final SQLListener sqlListener;
    private final SQLDetailedListener detailedListener;

    SQLListenerAdapter(SQLListener sqlListener) {
        this.detailedListener = sqlListener instanceof SQLDetailedListener ? (SQLDetailedListener)sqlListener : null;
        this.sqlListener = sqlListener;
    }

    public SQLListener getSqlListener() {
        return this.sqlListener;
    }

    @Override
    public void start(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.start(context);
        }
    }

    @Override
    public void preRender(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.preRender(context);
        }
    }

    @Override
    public void rendered(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.rendered(context);
        }
    }

    @Override
    public void prePrepare(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.prePrepare(context);
        }
    }

    @Override
    public void prepared(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.prepared(context);
        }
    }

    @Override
    public void preExecute(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.preExecute(context);
        }
    }

    @Override
    public void executed(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.executed(context);
        }
    }

    @Override
    public void end(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.end(context);
        }
    }

    @Override
    public void exception(SQLListenerContext context) {
        if (this.detailedListener != null) {
            this.detailedListener.exception(context);
        }
    }

    @Override
    public void notifyQuery(QueryMetadata md) {
        this.sqlListener.notifyQuery(md);
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        this.sqlListener.notifyDelete(entity, md);
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        this.sqlListener.notifyDeletes(entity, batches);
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        this.sqlListener.notifyMerge(entity, md, keys, columns, values, subQuery);
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
        this.sqlListener.notifyMerges(entity, md, batches);
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        this.sqlListener.notifyInsert(entity, md, columns, values, subQuery);
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
        this.sqlListener.notifyInserts(entity, md, batches);
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        this.sqlListener.notifyUpdate(entity, md, updates);
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        this.sqlListener.notifyUpdates(entity, batches);
    }
}

