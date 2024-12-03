/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.postgresql;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import java.sql.Connection;
import javax.inject.Provider;

public class PostgreSQLQueryFactory
extends AbstractSQLQueryFactory<PostgreSQLQuery<?>> {
    public PostgreSQLQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public PostgreSQLQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new PostgreSQLTemplates()), connection);
    }

    public PostgreSQLQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    @Override
    public PostgreSQLQuery<?> query() {
        return new PostgreSQLQuery((Provider<Connection>)this.connection, this.configuration);
    }

    public <T> PostgreSQLQuery<T> select(Expression<T> expr) {
        return ((PostgreSQLQuery)this.query()).select((Expression)expr);
    }

    public PostgreSQLQuery<Tuple> select(Expression<?> ... exprs) {
        return ((PostgreSQLQuery)this.query()).select((Expression[])exprs);
    }

    public <T> PostgreSQLQuery<T> selectDistinct(Expression<T> expr) {
        return (PostgreSQLQuery)((QueryBase)((Object)((PostgreSQLQuery)this.query()).select((Expression)expr))).distinct();
    }

    public PostgreSQLQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (PostgreSQLQuery)((QueryBase)((Object)((PostgreSQLQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public PostgreSQLQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public PostgreSQLQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> PostgreSQLQuery<T> selectFrom(RelationalPath<T> expr) {
        return (PostgreSQLQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }
}

