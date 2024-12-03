/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.ResultSetReturn;
import org.hibernate.engine.jdbc.spi.StatementPreparer;
import org.hibernate.jdbc.WorkExecutorVisitable;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.transaction.backend.jdbc.spi.JdbcResourceTransactionAccess;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorOwner;

public interface JdbcCoordinator
extends Serializable,
TransactionCoordinatorOwner,
JdbcResourceTransactionAccess {
    public LogicalConnectionImplementor getLogicalConnection();

    public Batch getBatch(BatchKey var1);

    public void executeBatch();

    public void abortBatch();

    public StatementPreparer getStatementPreparer();

    public ResultSetReturn getResultSetReturn();

    public void flushBeginning();

    public void flushEnding();

    public Connection close();

    public void afterTransaction();

    public void afterStatementExecution();

    public <T> T coordinateWork(WorkExecutorVisitable<T> var1);

    public void cancelLastQuery();

    public int determineRemainingTransactionTimeOutPeriod();

    public void enableReleases();

    public void disableReleases();

    public void registerLastQuery(Statement var1);

    public boolean isReadyForSerialization();

    @Deprecated
    default public ConnectionReleaseMode getConnectionReleaseMode() {
        return this.getLogicalConnection().getConnectionHandlingMode().getReleaseMode();
    }

    @Deprecated
    default public PhysicalConnectionHandlingMode getConnectionHandlingMode() {
        return this.getLogicalConnection().getConnectionHandlingMode();
    }

    @Deprecated
    default public ResourceRegistry getResourceRegistry() {
        return this.getLogicalConnection().getResourceRegistry();
    }

    public void serialize(ObjectOutputStream var1) throws IOException;
}

