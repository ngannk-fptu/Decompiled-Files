/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package com.querydsl.sql.mysql;

import com.google.common.base.Joiner;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import java.io.File;
import java.sql.Connection;
import javax.inject.Provider;

public class MySQLQuery<T>
extends AbstractSQLQuery<T, MySQLQuery<T>> {
    private static final String WITH_ROLLUP = "\nwith rollup ";
    private static final String STRAIGHT_JOIN = "straight_join ";
    private static final String SQL_SMALL_RESULT = "sql_small_result ";
    private static final String SQL_NO_CACHE = "sql_no_cache ";
    private static final String LOCK_IN_SHARE_MODE = "\nlock in share mode ";
    private static final String HIGH_PRIORITY = "high_priority ";
    private static final String SQL_CALC_FOUND_ROWS = "sql_calc_found_rows ";
    private static final String SQL_CACHE = "sql_cache ";
    private static final String SQL_BUFFER_RESULT = "sql_buffer_result ";
    private static final String SQL_BIG_RESULT = "sql_big_result ";
    private static final Joiner JOINER = Joiner.on((String)", ");

    public MySQLQuery(Connection conn) {
        this(conn, new Configuration(MySQLTemplates.DEFAULT), (QueryMetadata)new DefaultQueryMetadata());
    }

    public MySQLQuery(Connection conn, SQLTemplates templates) {
        this(conn, new Configuration(templates), (QueryMetadata)new DefaultQueryMetadata());
    }

    public MySQLQuery(Connection conn, Configuration configuration) {
        this(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public MySQLQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    public MySQLQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(connProvider, configuration, metadata);
    }

    public MySQLQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration);
    }

    public MySQLQuery<T> bigResult() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_BIG_RESULT);
    }

    public MySQLQuery<T> bufferResult() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_BUFFER_RESULT);
    }

    public MySQLQuery<T> cache() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_CACHE);
    }

    public MySQLQuery<T> calcFoundRows() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_CALC_FOUND_ROWS);
    }

    public MySQLQuery<T> highPriority() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, HIGH_PRIORITY);
    }

    public MySQLQuery<T> into(String var) {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.END, "\ninto " + var);
    }

    public MySQLQuery<T> intoDumpfile(File file) {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.END, "\ninto dumpfile '" + file.getPath() + "'");
    }

    public MySQLQuery<T> intoOutfile(File file) {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.END, "\ninto outfile '" + file.getPath() + "'");
    }

    public MySQLQuery<T> lockInShareMode() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.END, LOCK_IN_SHARE_MODE);
    }

    public MySQLQuery<T> noCache() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_NO_CACHE);
    }

    public MySQLQuery<T> smallResult() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, SQL_SMALL_RESULT);
    }

    public MySQLQuery<T> straightJoin() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_SELECT, STRAIGHT_JOIN);
    }

    public MySQLQuery<T> forceIndex(String ... indexes) {
        return (MySQLQuery)this.addJoinFlag(" force index (" + JOINER.join((Object[])indexes) + ")", JoinFlag.Position.END);
    }

    public MySQLQuery<T> ignoreIndex(String ... indexes) {
        return (MySQLQuery)this.addJoinFlag(" ignore index (" + JOINER.join((Object[])indexes) + ")", JoinFlag.Position.END);
    }

    public MySQLQuery<T> useIndex(String ... indexes) {
        return (MySQLQuery)this.addJoinFlag(" use index (" + JOINER.join((Object[])indexes) + ")", JoinFlag.Position.END);
    }

    public MySQLQuery<T> withRollup() {
        return (MySQLQuery)this.addFlag(QueryFlag.Position.AFTER_GROUP_BY, WITH_ROLLUP);
    }

    @Override
    public MySQLQuery<T> clone(Connection conn) {
        MySQLQuery<T> q = new MySQLQuery<T>(conn, this.getConfiguration(), this.getMetadata().clone());
        q.clone(this);
        return q;
    }

    public <U> MySQLQuery<U> select(Expression<U> expr) {
        this.queryMixin.setProjection(expr);
        MySQLQuery newType = this;
        return newType;
    }

    public MySQLQuery<Tuple> select(Expression<?> ... exprs) {
        this.queryMixin.setProjection(exprs);
        MySQLQuery newType = this;
        return newType;
    }
}

