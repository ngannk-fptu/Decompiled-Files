/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.TransactionException;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.internal.ResultSetReturnImpl;
import org.hibernate.engine.jdbc.internal.StatementPreparerImpl;
import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.JdbcWrapper;
import org.hibernate.engine.jdbc.spi.ResultSetReturn;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.StatementPreparer;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.WorkExecutor;
import org.hibernate.jdbc.WorkExecutorVisitable;
import org.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor;
import org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl;
import org.hibernate.resource.jdbc.internal.LogicalConnectionProvidedImpl;
import org.hibernate.resource.jdbc.internal.ResourceRegistryStandardImpl;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.transaction.backend.jdbc.spi.JdbcResourceTransaction;

public class JdbcCoordinatorImpl
implements JdbcCoordinator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JdbcCoordinatorImpl.class);
    private transient LogicalConnectionImplementor logicalConnection;
    private transient JdbcSessionOwner owner;
    private transient JdbcServices jdbcServices;
    private transient Batch currentBatch;
    private transient long transactionTimeOutInstant = -1L;
    private Statement lastQuery;
    private final boolean isUserSuppliedConnection;
    private boolean releasesEnabled = true;
    private int flushDepth;
    private transient StatementPreparer statementPreparer;
    private transient ResultSetReturn resultSetExtractor;

    public JdbcCoordinatorImpl(Connection userSuppliedConnection, JdbcSessionOwner owner, JdbcServices jdbcServices) {
        this.isUserSuppliedConnection = userSuppliedConnection != null;
        ResourceRegistryStandardImpl resourceRegistry = new ResourceRegistryStandardImpl(owner.getJdbcSessionContext().getObserver());
        this.logicalConnection = this.isUserSuppliedConnection ? new LogicalConnectionProvidedImpl(userSuppliedConnection, resourceRegistry) : new LogicalConnectionManagedImpl(owner.getJdbcConnectionAccess(), owner.getJdbcSessionContext(), resourceRegistry, jdbcServices);
        this.owner = owner;
        this.jdbcServices = jdbcServices;
    }

    private JdbcCoordinatorImpl(LogicalConnectionImplementor logicalConnection, boolean isUserSuppliedConnection, JdbcSessionOwner owner) {
        this.logicalConnection = logicalConnection;
        this.isUserSuppliedConnection = isUserSuppliedConnection;
        this.owner = owner;
        this.jdbcServices = owner.getJdbcSessionContext().getServiceRegistry().getService(JdbcServices.class);
    }

    @Override
    public LogicalConnectionImplementor getLogicalConnection() {
        return this.logicalConnection;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.owner.getJdbcSessionContext().getSessionFactory();
    }

    protected BatchBuilder batchBuilder() {
        return this.sessionFactory().getServiceRegistry().getService(BatchBuilder.class);
    }

    public SqlExceptionHelper sqlExceptionHelper() {
        return this.jdbcServices.getSqlExceptionHelper();
    }

    @Override
    public void flushBeginning() {
        if (this.flushDepth == 0) {
            this.releasesEnabled = false;
        }
        ++this.flushDepth;
    }

    @Override
    public void flushEnding() {
        --this.flushDepth;
        if (this.flushDepth < 0) {
            throw new HibernateException("Mismatched flush handling");
        }
        if (this.flushDepth == 0) {
            this.releasesEnabled = true;
        }
        this.afterStatementExecution();
    }

    @Override
    public Connection close() {
        Connection connection;
        LOG.tracev("Closing JDBC container [{0}]", this);
        try {
            if (this.currentBatch != null) {
                LOG.closingUnreleasedBatch();
                this.currentBatch.release();
            }
        }
        finally {
            connection = this.logicalConnection.close();
        }
        return connection;
    }

    @Override
    public Batch getBatch(BatchKey key) {
        if (this.currentBatch != null) {
            if (this.currentBatch.getKey().equals(key)) {
                return this.currentBatch;
            }
            this.currentBatch.execute();
            this.currentBatch.release();
        }
        this.currentBatch = this.batchBuilder().buildBatch(key, this);
        return this.currentBatch;
    }

    @Override
    public void executeBatch() {
        if (this.currentBatch != null) {
            this.currentBatch.execute();
            this.currentBatch.release();
        }
    }

    @Override
    public void abortBatch() {
        if (this.currentBatch != null) {
            this.currentBatch.release();
        }
    }

    @Override
    public StatementPreparer getStatementPreparer() {
        if (this.statementPreparer == null) {
            this.statementPreparer = new StatementPreparerImpl(this, this.jdbcServices);
        }
        return this.statementPreparer;
    }

    @Override
    public ResultSetReturn getResultSetReturn() {
        if (this.resultSetExtractor == null) {
            this.resultSetExtractor = new ResultSetReturnImpl(this, this.jdbcServices);
        }
        return this.resultSetExtractor;
    }

    @Override
    public void setTransactionTimeOut(int seconds) {
        this.transactionTimeOutInstant = System.currentTimeMillis() + (long)(seconds * 1000);
    }

    @Override
    public void flushBeforeTransactionCompletion() {
        this.getJdbcSessionOwner().flushBeforeTransactionCompletion();
    }

    @Override
    public int determineRemainingTransactionTimeOutPeriod() {
        if (this.transactionTimeOutInstant < 0L) {
            return -1;
        }
        int secondsRemaining = (int)((this.transactionTimeOutInstant - System.currentTimeMillis()) / 1000L);
        if (secondsRemaining <= 0) {
            throw new TransactionException("transaction timeout expired");
        }
        return secondsRemaining;
    }

    @Override
    public void afterStatementExecution() {
        ConnectionReleaseMode connectionReleaseMode = this.getConnectionReleaseMode();
        LOG.tracev("Starting after statement execution processing [{0}]", (Object)connectionReleaseMode);
        if (connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT) {
            if (!this.releasesEnabled) {
                LOG.debug("Skipping aggressive release due to manual disabling");
                return;
            }
            if (this.hasRegisteredResources()) {
                LOG.debug("Skipping aggressive release due to registered resources");
                return;
            }
            this.getLogicalConnection().afterStatement();
        }
    }

    @Override
    public void afterTransaction() {
        this.transactionTimeOutInstant = -1L;
        if (this.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT || this.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION || this.getConnectionReleaseMode() == ConnectionReleaseMode.BEFORE_TRANSACTION_COMPLETION) {
            this.logicalConnection.afterTransaction();
        }
    }

    private void releaseResources() {
        this.getResourceRegistry().releaseResources();
    }

    private boolean hasRegisteredResources() {
        return this.getResourceRegistry().hasRegisteredResources();
    }

    private ConnectionReleaseMode determineConnectionReleaseMode(JdbcConnectionAccess jdbcConnectionAccess, boolean isUserSuppliedConnection, ConnectionReleaseMode connectionReleaseMode) {
        if (isUserSuppliedConnection) {
            return ConnectionReleaseMode.ON_CLOSE;
        }
        if (connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT && !jdbcConnectionAccess.supportsAggressiveRelease()) {
            LOG.debug("Connection provider reports to not support aggressive release; overriding");
            return ConnectionReleaseMode.AFTER_TRANSACTION;
        }
        return connectionReleaseMode;
    }

    @Override
    public <T> T coordinateWork(WorkExecutorVisitable<T> work) {
        Connection connection = this.getLogicalConnection().getPhysicalConnection();
        try {
            T result = work.accept(new WorkExecutor(), connection);
            this.afterStatementExecution();
            return result;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper().convert(e, "error executing work");
        }
    }

    @Override
    public boolean isReadyForSerialization() {
        return this.isUserSuppliedConnection ? !this.getLogicalConnection().isPhysicallyConnected() : !this.hasRegisteredResources();
    }

    @Override
    public void registerLastQuery(Statement statement) {
        LOG.tracev("Registering last query statement [{0}]", statement);
        if (statement instanceof JdbcWrapper) {
            JdbcWrapper wrapper = (JdbcWrapper)((Object)statement);
            this.registerLastQuery((Statement)wrapper.getWrappedObject());
            return;
        }
        this.lastQuery = statement;
    }

    @Override
    public void cancelLastQuery() {
        try {
            if (this.lastQuery != null) {
                this.lastQuery.cancel();
            }
        }
        catch (SQLException sqle) {
            SqlExceptionHelper sqlExceptionHelper = this.jdbcServices.getSqlExceptionHelper();
            if (sqlExceptionHelper != null) {
                sqlExceptionHelper = new SqlExceptionHelper(false);
            }
            throw sqlExceptionHelper.convert(sqle, "Cannot cancel query");
        }
        finally {
            this.lastQuery = null;
        }
    }

    @Override
    public void enableReleases() {
        this.releasesEnabled = true;
    }

    @Override
    public void disableReleases() {
        this.releasesEnabled = false;
    }

    protected void close(Statement statement) {
        LOG.tracev("Closing prepared statement [{0}]", statement);
        this.sqlExceptionHelper().logAndClearWarnings(statement);
        if (statement instanceof InvalidatableWrapper) {
            InvalidatableWrapper wrapper = (InvalidatableWrapper)((Object)statement);
            this.close((Statement)wrapper.getWrappedObject());
            wrapper.invalidate();
            return;
        }
        try {
            try {
                if (statement.getMaxRows() != 0) {
                    statement.setMaxRows(0);
                }
                if (statement.getQueryTimeout() != 0) {
                    statement.setQueryTimeout(0);
                }
            }
            catch (SQLException sqle) {
                if (LOG.isDebugEnabled()) {
                    LOG.debugf("Exception clearing maxRows/queryTimeout [%s]", sqle.getMessage());
                }
                return;
            }
            statement.close();
            if (this.lastQuery == statement) {
                this.lastQuery = null;
            }
        }
        catch (SQLException e) {
            LOG.debugf("Unable to release JDBC statement [%s]", e.getMessage());
        }
        catch (Exception e) {
            LOG.debugf("Unable to release JDBC statement [%s]", e.getMessage());
        }
    }

    protected void close(ResultSet resultSet) {
        LOG.tracev("Closing result set [{0}]", resultSet);
        if (resultSet instanceof InvalidatableWrapper) {
            InvalidatableWrapper wrapper = (InvalidatableWrapper)((Object)resultSet);
            this.close((ResultSet)wrapper.getWrappedObject());
            wrapper.invalidate();
            return;
        }
        try {
            resultSet.close();
        }
        catch (SQLException e) {
            LOG.debugf("Unable to release JDBC result set [%s]", e.getMessage());
        }
        catch (Exception e) {
            LOG.debugf("Unable to release JDBC result set [%s]", e.getMessage());
        }
    }

    @Override
    public boolean isActive() {
        return !this.sessionFactory().isClosed();
    }

    @Override
    public void afterTransactionBegin() {
        this.owner.afterTransactionBegin();
    }

    @Override
    public void beforeTransactionCompletion() {
        this.owner.beforeTransactionCompletion();
        this.logicalConnection.beforeTransactionCompletion();
    }

    @Override
    public void afterTransactionCompletion(boolean successful, boolean delayed) {
        this.afterTransaction();
        this.owner.afterTransactionCompletion(successful, delayed);
    }

    @Override
    public JdbcSessionOwner getJdbcSessionOwner() {
        return this.owner;
    }

    @Override
    public JdbcResourceTransaction getResourceLocalTransaction() {
        return this.logicalConnection.getPhysicalJdbcTransaction();
    }

    @Override
    public void serialize(ObjectOutputStream oos) throws IOException {
        if (!this.isReadyForSerialization()) {
            throw new HibernateException("Cannot serialize Session while connected");
        }
        oos.writeBoolean(this.isUserSuppliedConnection);
        this.logicalConnection.serialize(oos);
    }

    public static JdbcCoordinatorImpl deserialize(ObjectInputStream ois, JdbcSessionOwner owner) throws IOException, ClassNotFoundException {
        boolean isUserSuppliedConnection = ois.readBoolean();
        AbstractLogicalConnectionImplementor logicalConnection = isUserSuppliedConnection ? LogicalConnectionProvidedImpl.deserialize(ois) : LogicalConnectionManagedImpl.deserialize(ois, owner.getJdbcConnectionAccess(), owner.getJdbcSessionContext());
        return new JdbcCoordinatorImpl(logicalConnection, isUserSuppliedConnection, owner);
    }
}

