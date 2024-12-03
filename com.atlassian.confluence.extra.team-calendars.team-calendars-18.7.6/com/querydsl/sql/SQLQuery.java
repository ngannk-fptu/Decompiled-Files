/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import javax.inject.Provider;

public class SQLQuery<T>
extends AbstractSQLQuery<T, SQLQuery<T>> {
    public SQLQuery() {
        super((Connection)null, Configuration.DEFAULT, (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLQuery(SQLTemplates templates) {
        super((Connection)null, new Configuration(templates), (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLQuery(Connection conn, SQLTemplates templates) {
        super(conn, new Configuration(templates), (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }

    public SQLQuery(Configuration configuration) {
        this((Connection)null, configuration);
    }

    public SQLQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    public SQLQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    @Override
    public SQLQuery<T> clone(Connection conn) {
        SQLQuery<T> q = new SQLQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> SQLQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        SQLQuery newType = this;
        return newType;
    }

    public SQLQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        SQLQuery newType = this;
        return newType;
    }
}

