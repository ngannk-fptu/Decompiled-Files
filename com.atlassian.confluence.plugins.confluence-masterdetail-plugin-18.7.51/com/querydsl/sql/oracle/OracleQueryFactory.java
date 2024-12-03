/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.oracle;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.oracle.OracleQuery;
import java.sql.Connection;
import javax.inject.Provider;

public class OracleQueryFactory
extends AbstractSQLQueryFactory<OracleQuery<?>> {
    public OracleQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public OracleQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new OracleTemplates()), connection);
    }

    public OracleQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    @Override
    public OracleQuery<?> query() {
        return new OracleQuery((Provider<Connection>)this.connection, this.configuration);
    }

    public <T> OracleQuery<T> select(Expression<T> expr) {
        return ((OracleQuery)this.query()).select((Expression)expr);
    }

    public OracleQuery<Tuple> select(Expression<?> ... exprs) {
        return ((OracleQuery)this.query()).select((Expression[])exprs);
    }

    public <T> OracleQuery<T> selectDistinct(Expression<T> expr) {
        return (OracleQuery)((QueryBase)((Object)((OracleQuery)this.query()).select((Expression)expr))).distinct();
    }

    public OracleQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (OracleQuery)((QueryBase)((Object)((OracleQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public OracleQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public OracleQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> OracleQuery<T> selectFrom(RelationalPath<T> expr) {
        return (OracleQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }
}

