/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.postgresql;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import javax.inject.Provider;

public class PostgreSQLQuery<T>
extends AbstractSQLQuery<T, PostgreSQLQuery<T>> {
    public PostgreSQLQuery(Connection conn) {
        this(conn, new Configuration(PostgreSQLTemplates.DEFAULT), (QueryMetadata)new DefaultQueryMetadata());
    }

    public PostgreSQLQuery(Connection conn, SQLTemplates templates) {
        this(conn, new Configuration(templates), (QueryMetadata)new DefaultQueryMetadata());
    }

    public PostgreSQLQuery(Connection conn, Configuration configuration) {
        this(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public PostgreSQLQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    public PostgreSQLQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    public PostgreSQLQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration);
    }

    @Override
    public PostgreSQLQuery<T> forShare() {
        return (PostgreSQLQuery)super.forShare();
    }

    public PostgreSQLQuery<T> noWait() {
        QueryFlag noWaitFlag = this.configuration.getTemplates().getNoWaitFlag();
        return (PostgreSQLQuery)this.addFlag(noWaitFlag);
    }

    public PostgreSQLQuery<T> of(RelationalPath<?> ... paths) {
        StringBuilder builder = new StringBuilder(" of ");
        for (RelationalPath<?> path : paths) {
            if (builder.length() > 4) {
                builder.append(", ");
            }
            builder.append(this.getConfiguration().getTemplates().quoteIdentifier(path.getTableName()));
        }
        return (PostgreSQLQuery)this.addFlag(QueryFlag.Position.END, builder.toString());
    }

    public PostgreSQLQuery<T> distinctOn(Expression<?> ... exprs) {
        return (PostgreSQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Object.class, "distinct on({0}) ", ExpressionUtils.list(Object.class, exprs)));
    }

    @Override
    public PostgreSQLQuery<T> clone(Connection conn) {
        PostgreSQLQuery<T> q = new PostgreSQLQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> PostgreSQLQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        PostgreSQLQuery newType = this;
        return newType;
    }

    public PostgreSQLQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        PostgreSQLQuery newType = this;
        return newType;
    }
}

