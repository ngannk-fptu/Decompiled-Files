/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;

public interface SQLListener {
    public void notifyQuery(QueryMetadata var1);

    public void notifyDelete(RelationalPath<?> var1, QueryMetadata var2);

    public void notifyDeletes(RelationalPath<?> var1, List<QueryMetadata> var2);

    public void notifyMerge(RelationalPath<?> var1, QueryMetadata var2, List<Path<?>> var3, List<Path<?>> var4, List<Expression<?>> var5, SubQueryExpression<?> var6);

    public void notifyMerges(RelationalPath<?> var1, QueryMetadata var2, List<SQLMergeBatch> var3);

    public void notifyInsert(RelationalPath<?> var1, QueryMetadata var2, List<Path<?>> var3, List<Expression<?>> var4, SubQueryExpression<?> var5);

    public void notifyInserts(RelationalPath<?> var1, QueryMetadata var2, List<SQLInsertBatch> var3);

    public void notifyUpdate(RelationalPath<?> var1, QueryMetadata var2, Map<Path<?>, Expression<?>> var3);

    public void notifyUpdates(RelationalPath<?> var1, List<SQLUpdateBatch> var2);
}

