/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 */
package com.querydsl.sql.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.mysql.MySQLQuery;
import com.querydsl.sql.mysql.MySQLReplaceClause;
import java.sql.Connection;
import javax.inject.Provider;

public class MySQLQueryFactory
extends AbstractSQLQueryFactory<MySQLQuery<?>> {
    public MySQLQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public MySQLQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new MySQLTemplates()), connection);
    }

    public MySQLQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    public SQLInsertClause insertIgnore(RelationalPath<?> entity) {
        SQLInsertClause insert = this.insert(entity);
        insert.addFlag(QueryFlag.Position.START_OVERRIDE, "insert ignore into ");
        return insert;
    }

    public SQLInsertClause insertOnDuplicateKeyUpdate(RelationalPath<?> entity, String clause) {
        SQLInsertClause insert = this.insert(entity);
        insert.addFlag(QueryFlag.Position.END, " on duplicate key update " + clause);
        return insert;
    }

    public SQLInsertClause insertOnDuplicateKeyUpdate(RelationalPath<?> entity, Expression<?> clause) {
        SQLInsertClause insert = this.insert(entity);
        insert.addFlag(QueryFlag.Position.END, ExpressionUtils.template(String.class, " on duplicate key update {0}", clause));
        return insert;
    }

    public SQLInsertClause insertOnDuplicateKeyUpdate(RelationalPath<?> entity, Expression<?> ... clauses) {
        SQLInsertClause insert = this.insert(entity);
        StringBuilder flag = new StringBuilder(" on duplicate key update ");
        for (int i = 0; i < clauses.length; ++i) {
            flag.append(i > 0 ? ", " : "").append("{" + i + "}");
        }
        insert.addFlag(QueryFlag.Position.END, ExpressionUtils.template(String.class, flag.toString(), (Object[])clauses));
        return insert;
    }

    @Override
    public MySQLQuery<?> query() {
        return new MySQLQuery((Provider<Connection>)this.connection, this.configuration);
    }

    public MySQLReplaceClause replace(RelationalPath<?> entity) {
        return new MySQLReplaceClause((Connection)this.connection.get(), this.configuration, entity);
    }

    public <T> MySQLQuery<T> select(Expression<T> expr) {
        return ((MySQLQuery)this.query()).select((Expression)expr);
    }

    public MySQLQuery<Tuple> select(Expression<?> ... exprs) {
        return ((MySQLQuery)this.query()).select((Expression[])exprs);
    }

    public <T> MySQLQuery<T> selectDistinct(Expression<T> expr) {
        return (MySQLQuery)((QueryBase)((Object)((MySQLQuery)this.query()).select((Expression)expr))).distinct();
    }

    public MySQLQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (MySQLQuery)((QueryBase)((Object)((MySQLQuery)this.query()).select((Expression[])exprs))).distinct();
    }

    public MySQLQuery<Integer> selectZero() {
        return this.select((Expression)Expressions.ZERO);
    }

    public MySQLQuery<Integer> selectOne() {
        return this.select((Expression)Expressions.ONE);
    }

    public <T> MySQLQuery<T> selectFrom(RelationalPath<T> expr) {
        return (MySQLQuery)this.select((Expression)expr).from((Expression<?>)expr);
    }
}

