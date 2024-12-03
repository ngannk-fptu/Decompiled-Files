/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.querydsl.sql;

import com.google.common.collect.Sets;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLDetailedListener;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListenerAdapter;
import com.querydsl.sql.SQLListenerContext;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class SQLListeners
implements SQLDetailedListener {
    @Nullable
    private final SQLDetailedListener parent;
    private final Set<SQLDetailedListener> listeners = Sets.newLinkedHashSet();

    public SQLListeners(SQLListener parent) {
        this.parent = new SQLListenerAdapter(parent);
    }

    public SQLListeners() {
        this.parent = null;
    }

    public void add(SQLListener listener) {
        if (listener instanceof SQLListeners) {
            for (SQLListener sQLListener : ((SQLListeners)listener).listeners) {
                this.add(sQLListener);
            }
        } else if (listener instanceof SQLDetailedListener) {
            this.listeners.add((SQLDetailedListener)listener);
        } else {
            this.listeners.add(new SQLListenerAdapter(listener));
        }
    }

    @Override
    public void notifyQuery(QueryMetadata md) {
        if (this.parent != null) {
            this.parent.notifyQuery(md);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyQuery(md);
        }
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        if (this.parent != null) {
            this.parent.notifyDelete(entity, md);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyDelete(entity, md);
        }
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        if (this.parent != null) {
            this.parent.notifyDeletes(entity, batches);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyDeletes(entity, batches);
        }
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        if (this.parent != null) {
            this.parent.notifyMerge(entity, md, keys, columns, values, subQuery);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyMerge(entity, md, keys, columns, values, subQuery);
        }
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
        if (this.parent != null) {
            this.parent.notifyMerges(entity, md, batches);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyMerges(entity, md, batches);
        }
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        if (this.parent != null) {
            this.parent.notifyInsert(entity, md, columns, values, subQuery);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyInsert(entity, md, columns, values, subQuery);
        }
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
        if (this.parent != null) {
            this.parent.notifyInserts(entity, md, batches);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyInserts(entity, md, batches);
        }
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        if (this.parent != null) {
            this.parent.notifyUpdate(entity, md, updates);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyUpdate(entity, md, updates);
        }
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        if (this.parent != null) {
            this.parent.notifyUpdates(entity, batches);
        }
        for (SQLListener sQLListener : this.listeners) {
            sQLListener.notifyUpdates(entity, batches);
        }
    }

    @Override
    public void start(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.start(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.start(context);
        }
    }

    @Override
    public void preRender(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.preRender(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.preRender(context);
        }
    }

    @Override
    public void rendered(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.rendered(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.rendered(context);
        }
    }

    @Override
    public void prePrepare(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.prePrepare(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.prePrepare(context);
        }
    }

    @Override
    public void prepared(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.prepared(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.prepared(context);
        }
    }

    @Override
    public void preExecute(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.preExecute(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.preExecute(context);
        }
    }

    @Override
    public void executed(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.executed(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.executed(context);
        }
    }

    @Override
    public void end(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.end(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.end(context);
        }
    }

    @Override
    public void exception(SQLListenerContext context) {
        if (this.parent != null) {
            this.parent.exception(context);
        }
        for (SQLDetailedListener listener : this.listeners) {
            listener.exception(context);
        }
    }

    public Set<SQLDetailedListener> getListeners() {
        return this.listeners;
    }
}

