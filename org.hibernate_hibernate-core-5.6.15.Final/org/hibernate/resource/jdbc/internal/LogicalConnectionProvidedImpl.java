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
import org.hibernate.resource.jdbc.LogicalConnection;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor;
import org.hibernate.resource.jdbc.internal.ResourceRegistryStandardImpl;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.jboss.logging.Logger;

public class LogicalConnectionProvidedImpl
extends AbstractLogicalConnectionImplementor {
    private static final Logger log = Logger.getLogger(LogicalConnection.class);
    private transient Connection providedConnection;
    private final boolean initiallyAutoCommit;
    private boolean closed;

    public LogicalConnectionProvidedImpl(Connection providedConnection, ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
        if (providedConnection == null) {
            throw new IllegalArgumentException("Provided Connection cannot be null");
        }
        this.providedConnection = providedConnection;
        this.initiallyAutoCommit = LogicalConnectionProvidedImpl.determineInitialAutoCommitMode(providedConnection);
    }

    private LogicalConnectionProvidedImpl(boolean closed, boolean initiallyAutoCommit) {
        this.resourceRegistry = new ResourceRegistryStandardImpl();
        this.closed = closed;
        this.initiallyAutoCommit = initiallyAutoCommit;
    }

    @Override
    public PhysicalConnectionHandlingMode getConnectionHandlingMode() {
        return PhysicalConnectionHandlingMode.IMMEDIATE_ACQUISITION_AND_HOLD;
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public Connection close() {
        log.trace((Object)"Closing logical connection");
        this.getResourceRegistry().releaseResources();
        try {
            Connection connection = this.providedConnection;
            return connection;
        }
        finally {
            this.providedConnection = null;
            this.closed = true;
            log.trace((Object)"Logical connection closed");
        }
    }

    @Override
    public boolean isPhysicallyConnected() {
        return this.providedConnection != null;
    }

    @Override
    public Connection getPhysicalConnection() {
        this.errorIfClosed();
        return this.providedConnection;
    }

    @Override
    public LogicalConnectionImplementor makeShareableCopy() {
        this.errorIfClosed();
        return new LogicalConnectionProvidedImpl(this.providedConnection, new ResourceRegistryStandardImpl());
    }

    @Override
    public void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeBoolean(this.closed);
        oos.writeBoolean(this.initiallyAutoCommit);
    }

    public static LogicalConnectionProvidedImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        boolean isClosed = ois.readBoolean();
        boolean initiallyAutoCommit = ois.readBoolean();
        return new LogicalConnectionProvidedImpl(isClosed, initiallyAutoCommit);
    }

    @Override
    public Connection manualDisconnect() {
        this.errorIfClosed();
        try {
            this.resourceRegistry.releaseResources();
            Connection connection = this.providedConnection;
            return connection;
        }
        finally {
            this.providedConnection = null;
        }
    }

    @Override
    public void manualReconnect(Connection connection) {
        this.errorIfClosed();
        if (connection == null) {
            throw new IllegalArgumentException("cannot reconnect using a null connection");
        }
        if (connection == this.providedConnection) {
            log.debug((Object)"reconnecting the same connection that is already connected; should this connection have been disconnected?");
        } else if (this.providedConnection != null) {
            throw new IllegalArgumentException("cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting.");
        }
        this.providedConnection = connection;
        log.debug((Object)"Manually reconnected logical connection");
    }

    @Override
    protected Connection getConnectionForTransactionManagement() {
        return this.providedConnection;
    }

    @Override
    protected void afterCompletion() {
        this.afterTransaction();
        this.resetConnection(this.initiallyAutoCommit);
    }
}

