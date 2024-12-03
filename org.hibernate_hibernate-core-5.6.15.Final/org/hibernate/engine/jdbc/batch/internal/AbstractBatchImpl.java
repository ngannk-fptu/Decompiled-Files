/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.batch.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.batch.spi.BatchObserver;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.jboss.logging.Logger;

public abstract class AbstractBatchImpl
implements Batch {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)AbstractBatchImpl.class.getName());
    private final BatchKey key;
    private final JdbcCoordinator jdbcCoordinator;
    private final SqlStatementLogger sqlStatementLogger;
    private final SqlExceptionHelper sqlExceptionHelper;
    private LinkedHashMap<String, PreparedStatement> statements = new LinkedHashMap();
    private LinkedHashSet<BatchObserver> observers = new LinkedHashSet();

    protected AbstractBatchImpl(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        if (key == null) {
            throw new IllegalArgumentException("batch key cannot be null");
        }
        if (jdbcCoordinator == null) {
            throw new IllegalArgumentException("JDBC coordinator cannot be null");
        }
        this.key = key;
        this.jdbcCoordinator = jdbcCoordinator;
        JdbcServices jdbcServices = jdbcCoordinator.getJdbcSessionOwner().getJdbcSessionContext().getServiceRegistry().getService(JdbcServices.class);
        this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
        this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
    }

    protected JdbcCoordinator getJdbcCoordinator() {
        return this.jdbcCoordinator;
    }

    protected abstract void doExecuteBatch();

    protected SqlExceptionHelper sqlExceptionHelper() {
        return this.sqlExceptionHelper;
    }

    protected SqlStatementLogger sqlStatementLogger() {
        return this.sqlStatementLogger;
    }

    protected void abortBatch(Exception cause) {
        try {
            this.jdbcCoordinator.abortBatch();
        }
        catch (RuntimeException e) {
            cause.addSuppressed(e);
        }
    }

    protected LinkedHashMap<String, PreparedStatement> getStatements() {
        return this.statements;
    }

    @Override
    public final BatchKey getKey() {
        return this.key;
    }

    @Override
    public void addObserver(BatchObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public PreparedStatement getBatchStatement(String sql, boolean callable) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must be non-null.");
        }
        PreparedStatement statement = this.statements.get(sql);
        if (statement == null) {
            statement = this.buildBatchStatement(sql, callable);
            this.statements.put(sql, statement);
        } else {
            LOG.debug("Reusing batch statement");
            this.sqlStatementLogger().logStatement(sql);
        }
        return statement;
    }

    private PreparedStatement buildBatchStatement(String sql, boolean callable) {
        return this.jdbcCoordinator.getStatementPreparer().prepareStatement(sql, callable);
    }

    @Override
    public final void execute() {
        this.notifyObserversExplicitExecution();
        if (this.getStatements().isEmpty()) {
            return;
        }
        try {
            this.doExecuteBatch();
        }
        finally {
            this.releaseStatements();
        }
    }

    protected void releaseStatements() {
        LinkedHashMap<String, PreparedStatement> statements = this.getStatements();
        ResourceRegistry resourceRegistry = this.jdbcCoordinator.getResourceRegistry();
        for (PreparedStatement statement : statements.values()) {
            this.clearBatch(statement);
            resourceRegistry.release(statement);
        }
        statements.clear();
        this.jdbcCoordinator.afterStatementExecution();
    }

    protected void clearBatch(PreparedStatement statement) {
        try {
            if (!statement.isClosed()) {
                statement.clearBatch();
            }
        }
        catch (SQLException e) {
            LOG.unableToReleaseBatchStatement();
        }
    }

    protected final void notifyObserversExplicitExecution() {
        for (BatchObserver observer : this.observers) {
            observer.batchExplicitlyExecuted();
        }
    }

    protected final void notifyObserversImplicitExecution() {
        for (BatchObserver observer : this.observers) {
            observer.batchImplicitlyExecuted();
        }
    }

    @Override
    public void release() {
        if (this.getStatements() != null && !this.getStatements().isEmpty()) {
            LOG.batchContainedStatementsOnRelease();
        }
        this.releaseStatements();
        this.observers.clear();
    }
}

