/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.oracle;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import javax.inject.Provider;

public class OracleQuery<T>
extends AbstractSQLQuery<T, OracleQuery<T>> {
    private static final String CONNECT_BY = "\nconnect by ";
    private static final String CONNECT_BY_NOCYCLE_PRIOR = "\nconnect by nocycle prior ";
    private static final String CONNECT_BY_PRIOR = "\nconnect by prior ";
    private static final String ORDER_SIBLINGS_BY = "\norder siblings by ";
    private static final String START_WITH = "\nstart with ";

    public OracleQuery(Connection conn) {
        this(conn, OracleTemplates.DEFAULT, (QueryMetadata)new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, SQLTemplates templates) {
        this(conn, templates, (QueryMetadata)new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public OracleQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    protected OracleQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }

    public OracleQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    public OracleQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration);
    }

    public OracleQuery<T> connectByPrior(Predicate cond) {
        return (OracleQuery)this.addFlag(QueryFlag.Position.BEFORE_ORDER, CONNECT_BY_PRIOR, (Expression)cond);
    }

    public OracleQuery<T> connectBy(Predicate cond) {
        return (OracleQuery)this.addFlag(QueryFlag.Position.BEFORE_ORDER, CONNECT_BY, (Expression)cond);
    }

    public OracleQuery<T> connectByNocyclePrior(Predicate cond) {
        return (OracleQuery)this.addFlag(QueryFlag.Position.BEFORE_ORDER, CONNECT_BY_NOCYCLE_PRIOR, (Expression)cond);
    }

    public <A> OracleQuery<T> startWith(Predicate cond) {
        return (OracleQuery)this.addFlag(QueryFlag.Position.BEFORE_ORDER, START_WITH, (Expression)cond);
    }

    public OracleQuery<T> orderSiblingsBy(Expression<?> path) {
        return (OracleQuery)this.addFlag(QueryFlag.Position.BEFORE_ORDER, ORDER_SIBLINGS_BY, (Expression)path);
    }

    @Override
    public OracleQuery<T> clone(Connection conn) {
        OracleQuery<T> q = new OracleQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> OracleQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        OracleQuery newType = this;
        return newType;
    }

    public OracleQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        OracleQuery newType = this;
        return newType;
    }
}

