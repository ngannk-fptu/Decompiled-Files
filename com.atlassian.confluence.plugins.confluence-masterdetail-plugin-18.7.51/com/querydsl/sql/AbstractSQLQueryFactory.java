/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLCommonQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLMergeClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.sql.Connection;
import javax.inject.Provider;

public abstract class AbstractSQLQueryFactory<Q extends SQLCommonQuery<?>>
implements SQLCommonQueryFactory<Q, SQLDeleteClause, SQLUpdateClause, SQLInsertClause, SQLMergeClause> {
    protected final Configuration configuration;
    protected final Provider<Connection> connection;

    public AbstractSQLQueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        this.configuration = configuration;
        this.connection = connProvider;
    }

    @Override
    public final SQLDeleteClause delete(RelationalPath<?> path) {
        return new SQLDeleteClause(this.connection, this.configuration, path);
    }

    @Override
    public final Q from(Expression<?> from) {
        return this.query().from(from);
    }

    @Override
    public final Q from(Expression<?> ... args) {
        return this.query().from(args);
    }

    @Override
    public final Q from(SubQueryExpression<?> subQuery, Path<?> alias) {
        return this.query().from(subQuery, alias);
    }

    @Override
    public final SQLInsertClause insert(RelationalPath<?> path) {
        return new SQLInsertClause(this.connection, this.configuration, path);
    }

    @Override
    public final SQLMergeClause merge(RelationalPath<?> path) {
        return new SQLMergeClause(this.connection, this.configuration, path);
    }

    @Override
    public final SQLUpdateClause update(RelationalPath<?> path) {
        return new SQLUpdateClause(this.connection, this.configuration, path);
    }

    public final Configuration getConfiguration() {
        return this.configuration;
    }

    public final Connection getConnection() {
        return (Connection)this.connection.get();
    }

    public abstract <T> AbstractSQLQuery<T, ?> select(Expression<T> var1);

    public abstract AbstractSQLQuery<Tuple, ?> select(Expression<?> ... var1);

    public abstract <T> AbstractSQLQuery<T, ?> selectDistinct(Expression<T> var1);

    public abstract AbstractSQLQuery<Tuple, ?> selectDistinct(Expression<?> ... var1);

    public abstract AbstractSQLQuery<Integer, ?> selectZero();

    public abstract AbstractSQLQuery<Integer, ?> selectOne();

    public abstract <T> AbstractSQLQuery<T, ?> selectFrom(RelationalPath<T> var1);
}

