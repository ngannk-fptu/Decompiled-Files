/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.batch.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.batch.internal.AbstractBatchImpl;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.jdbc.spi.JdbcObserver;
import org.jboss.logging.Logger;

public class BatchingBatch
extends AbstractBatchImpl {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)BatchingBatch.class.getName());
    private int batchSize;
    private final int configuredBatchSize;
    private int batchPosition;
    private boolean batchExecuted;
    private int statementPosition;
    private String currentStatementSql;
    private PreparedStatement currentStatement;

    public BatchingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator, int batchSize) {
        super(key, jdbcCoordinator);
        if (!key.getExpectation().canBeBatched()) {
            throw new HibernateException("attempting to batch an operation which cannot be batched");
        }
        this.batchSize = batchSize;
        this.configuredBatchSize = batchSize;
    }

    @Override
    public PreparedStatement getBatchStatement(String sql, boolean callable) {
        this.currentStatementSql = sql;
        int previousBatchSize = this.getStatements().size();
        this.currentStatement = super.getBatchStatement(sql, callable);
        int currentBatchSize = this.getStatements().size();
        if (currentBatchSize > previousBatchSize) {
            this.batchSize = this.configuredBatchSize * currentBatchSize;
        }
        return this.currentStatement;
    }

    @Override
    public void addToBatch() {
        try {
            this.currentStatement.addBatch();
        }
        catch (SQLException e) {
            this.abortBatch(e);
            LOG.debugf("SQLException escaped proxy", e);
            throw this.sqlExceptionHelper().convert(e, "could not perform addBatch", this.currentStatementSql);
        }
        catch (RuntimeException e) {
            this.abortBatch(e);
            throw e;
        }
        ++this.statementPosition;
        if (this.statementPosition >= this.getKey().getBatchedStatementCount()) {
            ++this.batchPosition;
            if (this.batchPosition == this.batchSize) {
                this.notifyObserversImplicitExecution();
                this.performExecution();
                this.batchPosition = 0;
                this.batchExecuted = true;
            }
            this.statementPosition = 0;
        }
    }

    @Override
    protected void doExecuteBatch() {
        if (this.batchPosition == 0) {
            if (!this.batchExecuted) {
                LOG.debug("No batched statements to execute");
            }
        } else {
            this.performExecution();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void performExecution() {
        LOG.debugf("Executing batch size: %s", this.batchPosition);
        JdbcObserver observer = this.getJdbcCoordinator().getJdbcSessionOwner().getJdbcSessionContext().getObserver();
        try {
            for (Map.Entry<String, PreparedStatement> entry : this.getStatements().entrySet()) {
                String sql = entry.getKey();
                try {
                    int[] rowCounts;
                    PreparedStatement statement = entry.getValue();
                    try {
                        observer.jdbcExecuteBatchStart();
                        rowCounts = statement.executeBatch();
                    }
                    finally {
                        observer.jdbcExecuteBatchEnd();
                    }
                    this.checkRowCounts(rowCounts, statement, sql);
                }
                catch (SQLException e) {
                    this.abortBatch(e);
                    LOG.unableToExecuteBatch(e, sql);
                    throw this.sqlExceptionHelper().convert(e, "could not execute batch", sql);
                }
                catch (RuntimeException re) {
                    this.abortBatch(re);
                    LOG.unableToExecuteBatch(re, sql);
                    throw re;
                    return;
                }
            }
        }
        finally {
            this.batchPosition = 0;
        }
    }

    private void checkRowCounts(int[] rowCounts, PreparedStatement ps, String statementSQL) throws SQLException, HibernateException {
        int numberOfRowCounts = rowCounts.length;
        if (this.batchPosition != 0 && numberOfRowCounts != this.batchPosition / this.getStatements().size()) {
            LOG.unexpectedRowCounts();
        }
        for (int i = 0; i < numberOfRowCounts; ++i) {
            this.getKey().getExpectation().verifyOutcome(rowCounts[i], ps, i, statementSQL);
        }
    }
}

