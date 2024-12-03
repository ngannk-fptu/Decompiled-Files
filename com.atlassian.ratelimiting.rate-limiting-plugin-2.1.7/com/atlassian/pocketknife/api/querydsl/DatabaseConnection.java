/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.sql.Connection;
import java.sql.Savepoint;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface DatabaseConnection
extends AutoCloseable {
    public <T> SQLQuery<T> select(Expression<T> var1);

    public SQLQuery<Tuple> select(Expression<?> ... var1);

    public <T> SQLQuery<T> from(RelationalPath<T> var1);

    public SQLInsertClause insert(RelationalPath<?> var1);

    public SQLDeleteClause delete(RelationalPath<?> var1);

    public SQLUpdateClause update(RelationalPath<?> var1);

    public SQLQueryFactory query();

    public Connection getJdbcConnection();

    public boolean isAutoCommit();

    public boolean isClosed();

    public boolean isInTransaction();

    public boolean isExternallyManaged();

    public void setAutoCommit(boolean var1);

    public void commit();

    public void rollback();

    public void rollback(Savepoint var1);

    public void releaseSavepoint(Savepoint var1);

    public Savepoint setSavepoint();

    public Savepoint setSavepoint(String var1);

    public DialectProvider.Config getDialectConfig();
}

