/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLMergeClause;
import com.querydsl.sql.dml.SQLUpdateClause;

public interface SQLCommonQueryFactory<Q extends SQLCommonQuery<?>, D extends SQLDeleteClause, U extends SQLUpdateClause, I extends SQLInsertClause, M extends SQLMergeClause>
extends QueryFactory<Q> {
    public D delete(RelationalPath<?> var1);

    public Q from(Expression<?> var1);

    public Q from(Expression<?> ... var1);

    public Q from(SubQueryExpression<?> var1, Path<?> var2);

    public I insert(RelationalPath<?> var1);

    public M merge(RelationalPath<?> var1);

    public U update(RelationalPath<?> var1);

    @Override
    public Q query();
}

