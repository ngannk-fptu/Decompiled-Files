/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.jdbc.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.ResourceClosedException;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor;
import org.hibernate.resource.jdbc.internal.ResourceRegistryStandardImpl;
import org.hibernate.resource.jdbc.spi.JdbcObserver;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.jboss.logging.Logger;

public class LogicalConnectionManagedImpl
extends AbstractLogicalConnectionImplementor {
    private static final Logger log = Logger.getLogger(LogicalConnectionManagedImpl.class);
    private final transient JdbcConnectionAccess jdbcConnectionAccess;
    private final transient JdbcObserver observer;
    private final transient SqlExceptionHelper sqlExceptionHelper;
    private final transient PhysicalConnectionHandlingMode connectionHandlingMode;
    private transient Connection physicalConnection;
    private boolean closed;
    private boolean providerDisablesAutoCommit;
    boolean initiallyAutoCommit;

    public LogicalConnectionManagedImpl(JdbcConnectionAccess jdbcConnectionAccess, JdbcSessionContext jdbcSessionContext, ResourceRegistry resourceRegistry, JdbcServices jdbcServices) {
        this.jdbcConnectionAccess = jdbcConnectionAccess;
        this.observer = jdbcSessionContext.getObserver();
        this.resourceRegistry = resourceRegistry;
        this.connectionHandlingMode = this.determineConnectionHandlingMode(jdbcSessionContext.getPhysicalConnectionHandlingMode(), jdbcConnectionAccess);
        this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
        if (this.connectionHandlingMode.getAcquisitionMode() == ConnectionAcquisitionMode.IMMEDIATELY) {
            this.acquireConnectionIfNeeded();
        }
        this.providerDisablesAutoCommit = jdbcSessionContext.doesConnectionProviderDisableAutoCommit();
        if (this.providerDisablesAutoCommit) {
            log.debug((Object)"`hibernate.connection.provider_disables_autocommit` was enabled.  This setting should only be enabled when you are certain that the Connections given to Hibernate by the ConnectionProvider have auto-commit disabled.  Enabling this setting when the Connections do not have auto-commit disabled will lead to Hibernate executing SQL operations outside of any JDBC/SQL transaction.");
        }
    }

    private PhysicalConnectionHandlingMode determineConnectionHandlingMode(PhysicalConnectionHandlingMode connectionHandlingMode, JdbcConnectionAccess jdbcConnectionAccess) {
        if (connectionHandlingMode.getReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT && !jdbcConnectionAccess.supportsAggressiveRelease()) {
            return PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION;
        }
        return connectionHandlingMode;
    }

    private LogicalConnectionManagedImpl(JdbcConnectionAccess jdbcConnectionAccess, JdbcSessionContext jdbcSessionContext, boolean closed) {
        this(jdbcConnectionAccess, jdbcSessionContext, new ResourceRegistryStandardImpl(), jdbcSessionContext.getServiceRegistry().getService(JdbcServices.class));
        this.closed = closed;
    }

    private Connection acquireConnectionIfNeeded() {
        if (this.physicalConnection == null) {
            try {
                this.physicalConnection = this.jdbcConnectionAccess.obtainConnection();
            }
            catch (SQLException e) {
                throw this.sqlExceptionHelper.convert(e, "Unable to acquire JDBC Connection");
            }
            finally {
                this.observer.jdbcConnectionAcquisitionEnd(this.physicalConnection);
            }
        }
        return this.physicalConnection;
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public PhysicalConnectionHandlingMode getConnectionHandlingMode() {
        return this.connectionHandlingMode;
    }

    @Override
    public boolean isPhysicallyConnected() {
        return this.physicalConnection != null;
    }

    @Override
    public Connection getPhysicalConnection() {
        this.errorIfClosed();
        return this.acquireConnectionIfNeeded();
    }

    @Override
    public void afterStatement() {
        super.afterStatement();
        if (this.connectionHandlingMode.getReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT) {
            if (this.getResourceRegistry().hasRegisteredResources()) {
                log.debug((Object)"Skipping aggressive release of JDBC Connection after-statement due to held resources");
            } else {
                log.debug((Object)"Initiating JDBC connection release from afterStatement");
                this.releaseConnection();
            }
        }
    }

    @Override
    public void beforeTransactionCompletion() {
        super.beforeTransactionCompletion();
        if (this.connectionHandlingMode.getReleaseMode() == ConnectionReleaseMode.BEFORE_TRANSACTION_COMPLETION) {
            log.debug((Object)"Initiating JDBC connection release from beforeTransactionCompletion");
            this.releaseConnection();
        }
    }

    @Override
    public void afterTransaction() {
        super.afterTransaction();
        if (this.connectionHandlingMode.getReleaseMode() != ConnectionReleaseMode.ON_CLOSE) {
            log.debug((Object)"Initiating JDBC connection release from afterTransaction");
            this.releaseConnection();
        }
    }

    @Override
    public Connection manualDisconnect() {
        if (this.closed) {
            throw new ResourceClosedException("Logical connection is closed");
        }
        Connection c = this.physicalConnection;
        this.releaseConnection();
        return c;
    }

    @Override
    public void manualReconnect(Connection suppliedConnection) {
        if (this.closed) {
            throw new ResourceClosedException("Logical connection is closed");
        }
        throw new IllegalStateException("Cannot manually reconnect unless Connection was originally supplied by user");
    }

    private void releaseConnection() {
        Connection localVariableConnection = this.physicalConnection;
        if (localVariableConnection == null) {
            return;
        }
        this.physicalConnection = null;
        try {
            try {
                this.getResourceRegistry().releaseResources();
                if (!localVariableConnection.isClosed()) {
                    this.sqlExceptionHelper.logAndClearWarnings(localVariableConnection);
                }
            }
            finally {
                this.jdbcConnectionAccess.releaseConnection(localVariableConnection);
            }
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "Unable to release JDBC Connection");
        }
        finally {
            this.observer.jdbcConnectionReleaseEnd();
        }
    }

    @Override
    public LogicalConnectionImplementor makeShareableCopy() {
        this.errorIfClosed();
        return null;
    }

    @Override
    public void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeBoolean(this.closed);
    }

    public static LogicalConnectionManagedImpl deserialize(ObjectInputStream ois, JdbcConnectionAccess jdbcConnectionAccess, JdbcSessionContext jdbcSessionContext) throws IOException {
        boolean isClosed = ois.readBoolean();
        return new LogicalConnectionManagedImpl(jdbcConnectionAccess, jdbcSessionContext, isClosed);
    }

    @Override
    public Connection close() {
        if (this.closed) {
            return null;
        }
        this.getResourceRegistry().releaseResources();
        log.trace((Object)"Closing logical connection");
        try {
            this.releaseConnection();
        }
        finally {
            this.closed = true;
            log.trace((Object)"Logical connection closed");
        }
        return null;
    }

    @Override
    protected Connection getConnectionForTransactionManagement() {
        return this.getPhysicalConnection();
    }

    @Override
    public void begin() {
        this.initiallyAutoCommit = !this.doConnectionsFromProviderHaveAutoCommitDisabled() && LogicalConnectionManagedImpl.determineInitialAutoCommitMode(this.getConnectionForTransactionManagement());
        super.begin();
    }

    @Override
    protected void afterCompletion() {
        this.resetConnection(this.initiallyAutoCommit);
        this.initiallyAutoCommit = false;
        this.afterTransaction();
    }

    @Override
    protected boolean doConnectionsFromProviderHaveAutoCommitDisabled() {
        return this.providerDisablesAutoCommit;
    }
}

