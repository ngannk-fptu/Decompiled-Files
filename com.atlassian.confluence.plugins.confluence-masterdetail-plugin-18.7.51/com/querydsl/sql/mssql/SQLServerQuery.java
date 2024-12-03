/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.mssql;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.mssql.SQLServerGrammar;
import com.querydsl.sql.mssql.SQLServerTableHints;
import java.sql.Connection;
import javax.inject.Provider;

public class SQLServerQuery<T>
extends AbstractSQLQuery<T, SQLServerQuery<T>> {
    public SQLServerQuery(Connection conn) {
        this(conn, SQLServerTemplates.DEFAULT, (QueryMetadata)new DefaultQueryMetadata());
    }

    public SQLServerQuery(Connection conn, SQLTemplates templates) {
        this(conn, templates, (QueryMetadata)new DefaultQueryMetadata());
    }

    protected SQLServerQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }

    public SQLServerQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    public SQLServerQuery(Connection conn, Configuration configuration) {
        super(conn, configuration);
    }

    public SQLServerQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    public SQLServerQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration);
    }

    public SQLServerQuery<T> tableHints(SQLServerTableHints ... tableHints) {
        if (tableHints.length > 0) {
            String hints = SQLServerGrammar.tableHints(tableHints);
            this.addJoinFlag(hints, JoinFlag.Position.END);
        }
        return this;
    }

    @Override
    public SQLServerQuery<T> clone(Connection conn) {
        SQLServerQuery<T> q = new SQLServerQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> SQLServerQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        SQLServerQuery newType = this;
        return newType;
    }

    public SQLServerQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        SQLServerQuery newType = this;
        return newType;
    }
}

