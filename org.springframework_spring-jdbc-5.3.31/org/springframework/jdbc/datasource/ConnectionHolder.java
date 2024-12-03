/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.ResourceHolderSupport
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

public class ConnectionHolder
extends ResourceHolderSupport {
    public static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
    @Nullable
    private ConnectionHandle connectionHandle;
    @Nullable
    private Connection currentConnection;
    private boolean transactionActive = false;
    @Nullable
    private Boolean savepointsSupported;
    private int savepointCounter = 0;

    public ConnectionHolder(ConnectionHandle connectionHandle) {
        Assert.notNull((Object)connectionHandle, (String)"ConnectionHandle must not be null");
        this.connectionHandle = connectionHandle;
    }

    public ConnectionHolder(Connection connection) {
        this.connectionHandle = new SimpleConnectionHandle(connection);
    }

    public ConnectionHolder(Connection connection, boolean transactionActive) {
        this(connection);
        this.transactionActive = transactionActive;
    }

    @Nullable
    public ConnectionHandle getConnectionHandle() {
        return this.connectionHandle;
    }

    protected boolean hasConnection() {
        return this.connectionHandle != null;
    }

    protected void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    protected boolean isTransactionActive() {
        return this.transactionActive;
    }

    protected void setConnection(@Nullable Connection connection) {
        if (this.currentConnection != null) {
            if (this.connectionHandle != null) {
                this.connectionHandle.releaseConnection(this.currentConnection);
            }
            this.currentConnection = null;
        }
        this.connectionHandle = connection != null ? new SimpleConnectionHandle(connection) : null;
    }

    public Connection getConnection() {
        Assert.notNull((Object)this.connectionHandle, (String)"Active Connection is required");
        if (this.currentConnection == null) {
            this.currentConnection = this.connectionHandle.getConnection();
        }
        return this.currentConnection;
    }

    public boolean supportsSavepoints() throws SQLException {
        if (this.savepointsSupported == null) {
            this.savepointsSupported = this.getConnection().getMetaData().supportsSavepoints();
        }
        return this.savepointsSupported;
    }

    public Savepoint createSavepoint() throws SQLException {
        ++this.savepointCounter;
        return this.getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + this.savepointCounter);
    }

    public void released() {
        super.released();
        if (!this.isOpen() && this.currentConnection != null) {
            if (this.connectionHandle != null) {
                this.connectionHandle.releaseConnection(this.currentConnection);
            }
            this.currentConnection = null;
        }
    }

    public void clear() {
        super.clear();
        this.transactionActive = false;
        this.savepointsSupported = null;
        this.savepointCounter = 0;
    }
}

