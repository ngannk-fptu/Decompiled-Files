/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCloseListener;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Provider;
import javax.sql.DataSource;

public class SQLQueryFactory
extends AbstractSQLQueryFactory<SQLQuery<?>> {
    public SQLQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    public SQLQueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        super(configuration, connProvider);
    }

    public SQLQueryFactory(Configuration configuration, DataSource dataSource) {
        this(configuration, dataSource, true);
    }

    public SQLQueryFactory(Configuration configuration, DataSource dataSource, boolean release) {
        super(configuration, new DataSourceProvider(dataSource));
        if (release) {
            configuration.addListener(SQLCloseListener.DEFAULT);
        }
    }

    @Override
    public SQLQuery<?> query() {
        return new SQLQuery(this.connection, this.configuration);
    }

    public <T> SQLQuery<T> select(Expression<T> expr) {
        return ((SQLQuery)this.query()).select((Expression)expr);
    }

    public SQLQuery<Tuple> select(Expression<?> ... exprs) {
        return ((SQLQuery)this.query()).select((Expression[])exprs);
    }

    public <T> SQLQuery<T> selectDistinct(Expression<T> expr) {
        return (SQLQuery)((QueryBase)((Object)((SQLQuery)this.query()).select((Expression)expr))).distinct();
    }

    public SQLQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (SQLQuery)((QueryBase)((Object)((SQLQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public SQLQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public SQLQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> SQLQuery<T> selectFrom(RelationalPath<T> expr) {
        return (SQLQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }

    static class DataSourceProvider
    implements Provider<Connection> {
        private final DataSource ds;

        public DataSourceProvider(DataSource ds) {
            this.ds = ds;
        }

        @Override
        public Connection get() {
            try {
                return this.ds.getConnection();
            }
            catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}

