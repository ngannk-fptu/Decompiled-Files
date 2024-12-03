/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.teradata;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.TeradataTemplates;
import java.sql.Connection;
import javax.inject.Provider;

public class TeradataQuery<T>
extends AbstractSQLQuery<T, TeradataQuery<T>> {
    public TeradataQuery(Connection conn) {
        this(conn, new Configuration(TeradataTemplates.DEFAULT), (QueryMetadata)new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, SQLTemplates templates) {
        this(conn, new Configuration(templates), (QueryMetadata)new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, Configuration configuration) {
        this(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public TeradataQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    public TeradataQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    public TeradataQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration);
    }

    public TeradataQuery<T> qualify(Predicate predicate) {
        predicate = ExpressionUtils.predicate((Operator)SQLOps.QUALIFY, predicate);
        return (TeradataQuery)this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.BEFORE_ORDER, predicate));
    }

    @Override
    public TeradataQuery<T> clone(Connection conn) {
        TeradataQuery<T> q = new TeradataQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> TeradataQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        TeradataQuery newType = this;
        return newType;
    }

    public TeradataQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        TeradataQuery newType = this;
        return newType;
    }
}

