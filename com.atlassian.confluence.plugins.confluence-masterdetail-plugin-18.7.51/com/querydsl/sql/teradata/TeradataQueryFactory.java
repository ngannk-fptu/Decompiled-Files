/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.teradata;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.TeradataTemplates;
import com.querydsl.sql.teradata.TeradataQuery;
import java.sql.Connection;
import javax.inject.Provider;

public class TeradataQueryFactory
extends AbstractSQLQueryFactory<TeradataQuery<?>> {
    public TeradataQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public TeradataQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new TeradataTemplates()), connection);
    }

    public TeradataQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    @Override
    public TeradataQuery<?> query() {
        return new TeradataQuery((Provider<Connection>)this.connection, this.configuration);
    }

    public <T> TeradataQuery<T> select(Expression<T> expr) {
        return ((TeradataQuery)this.query()).select((Expression)expr);
    }

    public TeradataQuery<Tuple> select(Expression<?> ... exprs) {
        return ((TeradataQuery)this.query()).select((Expression[])exprs);
    }

    public <T> TeradataQuery<T> selectDistinct(Expression<T> expr) {
        return (TeradataQuery)((QueryBase)((Object)((TeradataQuery)this.query()).select((Expression)expr))).distinct();
    }

    public TeradataQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (TeradataQuery)((QueryBase)((Object)((TeradataQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public TeradataQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public TeradataQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> TeradataQuery<T> selectFrom(RelationalPath<T> expr) {
        return (TeradataQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }
}

