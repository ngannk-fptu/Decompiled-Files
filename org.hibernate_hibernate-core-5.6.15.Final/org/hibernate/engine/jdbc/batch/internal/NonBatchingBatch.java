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
import org.hibernate.engine.jdbc.batch.internal.AbstractBatchImpl;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class NonBatchingBatch
extends AbstractBatchImpl {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)NonBatchingBatch.class.getName());
    private JdbcCoordinator jdbcCoordinator;

    protected NonBatchingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        super(key, jdbcCoordinator);
        this.jdbcCoordinator = jdbcCoordinator;
    }

    @Override
    public void addToBatch() {
        this.notifyObserversImplicitExecution();
        for (Map.Entry<String, PreparedStatement> entry : this.getStatements().entrySet()) {
            String statementSQL = entry.getKey();
            try {
                PreparedStatement statement = entry.getValue();
                int rowCount = this.jdbcCoordinator.getResultSetReturn().executeUpdate(statement);
                this.getKey().getExpectation().verifyOutcome(rowCount, statement, 0, statementSQL);
                this.jdbcCoordinator.getResourceRegistry().release(statement);
                this.jdbcCoordinator.afterStatementExecution();
            }
            catch (SQLException e) {
                this.abortBatch(e);
                throw this.sqlExceptionHelper().convert(e, "could not execute non-batched batch statement", statementSQL);
            }
            catch (RuntimeException e) {
                this.abortBatch(e);
                throw e;
            }
        }
        this.getStatements().clear();
    }

    @Override
    protected void clearBatch(PreparedStatement statement) {
    }

    @Override
    protected void doExecuteBatch() {
    }
}

