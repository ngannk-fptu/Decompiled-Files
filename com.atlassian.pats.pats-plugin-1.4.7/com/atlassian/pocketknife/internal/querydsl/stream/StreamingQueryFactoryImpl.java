/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.stream;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.atlassian.pocketknife.api.querydsl.stream.ClosePromise;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.atlassian.pocketknife.api.querydsl.stream.StreamingQueryFactory;
import com.atlassian.pocketknife.internal.querydsl.stream.CloseableIterableImpl;
import com.atlassian.pocketknife.internal.querydsl.util.fp.Fp;
import com.google.common.collect.ImmutableList;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.StatementOptions;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class StreamingQueryFactoryImpl
implements StreamingQueryFactory {
    public static final int DEFAULT_FETCH_SIZE = 1000;

    @Override
    public <Q> CloseableIterable<Q> stream(DatabaseConnection connection, ClosePromise closePromise, Supplier<SQLQuery<Q>> querySupplier) {
        return this.streamImpl(connection, closePromise, querySupplier);
    }

    @Override
    public <Q> CloseableIterable<Q> stream(DatabaseConnection connection, Supplier<SQLQuery<Q>> querySupplier) {
        return this.streamImpl(connection, ClosePromise.NOOP(), querySupplier);
    }

    private <Q> CloseableIterable<Q> streamImpl(DatabaseConnection connection, ClosePromise closeEffect, Supplier<SQLQuery<Q>> function) {
        this.assertIsNotAutoCommit(connection);
        try {
            SQLQuery<Q> sqlQuery = function.get();
            sqlQuery = this.applyStreamingParameters(connection, sqlQuery);
            CloseableIterator iterator = sqlQuery.iterate();
            return new CloseableIterableImpl(iterator, Fp.identity(), closeEffect);
        }
        catch (RuntimeException rte) {
            closeEffect.close();
            throw rte;
        }
    }

    private <Q> SQLQuery<Q> applyStreamingParameters(DatabaseConnection connection, SQLQuery<Q> query) {
        int fetchSize = 1000;
        if (this.isMySQL(connection)) {
            fetchSize = Integer.MIN_VALUE;
        }
        StatementOptions statementOptions = StatementOptions.builder().setFetchSize(fetchSize).build();
        query.setStatementOptions(statementOptions);
        return query;
    }

    private boolean isMySQL(DatabaseConnection connection) {
        return connection.getDialectConfig().getDatabaseInfo().getSupportedDatabase() == DialectProvider.SupportedDatabase.MYSQL;
    }

    private void assertIsNotAutoCommit(DatabaseConnection connection) {
        if (connection.isAutoCommit()) {
            throw new IllegalStateException("The database connection for streamy operations MUST be NOT be in auto-commit mode");
        }
    }

    @Override
    public <Q, T> List<T> streamyMap(DatabaseConnection connection, StreamingQueryFactory.StreamyMapClosure<Q, T> closure) {
        Function queryFunction = closure.getQuery();
        CloseableIterable<Q> stream = this.stream(connection, () -> (SQLQuery)queryFunction.apply(connection));
        try (CloseableIterable<T> iterable = stream.map(closure.getMapFunction());){
            ImmutableList immutableList = ImmutableList.copyOf(iterable);
            return immutableList;
        }
    }

    @Override
    public <Q, T> T streamyFold(DatabaseConnection connection, T initial, StreamingQueryFactory.StreamyFoldClosure<Q, T> closure) {
        Function queryFunction = closure.getQuery();
        CloseableIterable<Q> stream = this.stream(connection, () -> (SQLQuery)queryFunction.apply(connection));
        return stream.foldLeft(initial, closure.getFoldFunction());
    }
}

