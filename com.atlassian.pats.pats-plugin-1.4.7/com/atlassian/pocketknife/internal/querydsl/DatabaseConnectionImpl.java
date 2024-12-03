/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.inject.Provider
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.google.common.base.Preconditions;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Provider;

@ParametersAreNonnullByDefault
public class DatabaseConnectionImpl
implements DatabaseConnection {
    private final DialectProvider.Config dialectConfig;
    private final Connection connection;
    private final boolean managedConnection;
    private final AtomicBoolean closed;

    DatabaseConnectionImpl(DialectProvider.Config dialectConfig, Connection connection, boolean managedConnection) {
        this.dialectConfig = (DialectProvider.Config)Preconditions.checkNotNull((Object)dialectConfig);
        this.connection = (Connection)Preconditions.checkNotNull((Object)connection);
        this.managedConnection = managedConnection;
        this.closed = new AtomicBoolean(false);
    }

    @Override
    public <T> SQLQuery<T> select(Expression<T> expression) {
        return this.queryFactory().select((Expression)expression);
    }

    @Override
    public SQLQuery<Tuple> select(Expression<?> ... expressions) {
        return this.queryFactory().select((Expression[])expressions);
    }

    @Override
    public <T> SQLQuery<T> from(RelationalPath<T> path) {
        return this.queryFactory().selectFrom((RelationalPath)path);
    }

    @Override
    public SQLInsertClause insert(RelationalPath<?> entity) {
        return this.query().insert(entity);
    }

    @Override
    public SQLDeleteClause delete(RelationalPath<?> entity) {
        return this.query().delete(entity);
    }

    @Override
    public SQLUpdateClause update(RelationalPath<?> entity) {
        return this.query().update(entity);
    }

    @Override
    public SQLQueryFactory query() {
        return this.queryFactory();
    }

    private SQLQueryFactory queryFactory() {
        return new SQLQueryFactory(this.dialectConfig.getConfiguration(), (Provider<Connection>)((Provider)() -> this.connection));
    }

    @Override
    public Connection getJdbcConnection() {
        return this.connection;
    }

    @Override
    public DialectProvider.Config getDialectConfig() {
        return this.dialectConfig;
    }

    @Override
    public boolean isInTransaction() {
        return this.managedConnection;
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public boolean isExternallyManaged() {
        return this.managedConnection;
    }

    @Override
    public boolean isAutoCommit() {
        try {
            return this.connection.getAutoCommit();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        if (this.managedConnection) {
            throw new UnsupportedOperationException("This connection has a managed transaction");
        }
        try {
            this.connection.setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        if (this.managedConnection) {
            throw new UnsupportedOperationException("This connection has a managed transaction");
        }
        try {
            this.connection.commit();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        if (this.managedConnection) {
            throw new UnsupportedOperationException("This connection has a managed transaction");
        }
        try {
            this.connection.rollback();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (!this.closed.getAndSet(true)) {
            this.connection.close();
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) {
        try {
            this.connection.releaseSavepoint(savepoint);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback(Savepoint savepoint) {
        try {
            this.connection.rollback(savepoint);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Savepoint setSavepoint() {
        try {
            return this.connection.setSavepoint();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Savepoint setSavepoint(String name) {
        try {
            return this.connection.setSavepoint(name);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

