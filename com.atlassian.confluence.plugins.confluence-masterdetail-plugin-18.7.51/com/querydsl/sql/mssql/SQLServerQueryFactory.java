/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.mssql;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.mssql.SQLServerQuery;
import java.sql.Connection;
import javax.inject.Provider;

public class SQLServerQueryFactory
extends AbstractSQLQueryFactory<SQLServerQuery<?>> {
    public SQLServerQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public SQLServerQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new SQLServerTemplates()), connection);
    }

    public SQLServerQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    @Override
    public SQLServerQuery<?> query() {
        return new SQLServerQuery((Provider<Connection>)this.connection, this.configuration);
    }

    public <T> SQLServerQuery<T> select(Expression<T> expr) {
        return ((SQLServerQuery)this.query()).select((Expression)expr);
    }

    public SQLServerQuery<Tuple> select(Expression<?> ... exprs) {
        return ((SQLServerQuery)this.query()).select((Expression[])exprs);
    }

    public <T> SQLServerQuery<T> selectDistinct(Expression<T> expr) {
        return (SQLServerQuery)((QueryBase)((Object)((SQLServerQuery)this.query()).select((Expression)expr))).distinct();
    }

    public SQLServerQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (SQLServerQuery)((QueryBase)((Object)((SQLServerQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public SQLServerQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public SQLServerQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> SQLServerQuery<T> selectFrom(RelationalPath<T> expr) {
        return (SQLServerQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }
}

