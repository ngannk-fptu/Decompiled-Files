/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.AuthenticationScheme;
import com.microsoft.sqlserver.jdbc.DriverJDBCVersion;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDriverBooleanProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerPooledConnection;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.microsoft.sqlserver.jdbc.SQLServerXAResource;
import com.microsoft.sqlserver.jdbc.Util;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

public final class SQLServerXAConnection
extends SQLServerPooledConnection
implements XAConnection {
    private static final long serialVersionUID = -8154621218821899459L;
    private volatile transient SQLServerXAResource xaResource;
    private SQLServerConnection physicalControlConnection;
    private transient Logger xaLogger;
    private final transient Lock lock = new ReentrantLock();

    SQLServerXAConnection(SQLServerDataSource ds, String user, String pwd) throws SQLException {
        super(ds, user, pwd);
        Properties urlProps;
        String clientKeyPassword;
        String clientCertificate;
        String trustStorePassword;
        this.xaLogger = SQLServerXADataSource.xaLogger;
        SQLServerConnection con = this.getPhysicalConnection();
        Properties controlConnectionProperties = (Properties)con.activeConnectionProperties.clone();
        controlConnectionProperties.setProperty(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), "true");
        controlConnectionProperties.remove(SQLServerDriverStringProperty.SELECT_METHOD.toString());
        String auth = controlConnectionProperties.getProperty(SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString());
        if (null != auth && AuthenticationScheme.NTLM == AuthenticationScheme.valueOfString(auth)) {
            controlConnectionProperties.setProperty(SQLServerDriverStringProperty.PASSWORD.toString(), pwd);
        }
        if (null == (trustStorePassword = ds.getTrustStorePassword())) {
            Properties urlProps2 = Util.parseUrl(ds.getURL(), this.xaLogger);
            trustStorePassword = urlProps2.getProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
        }
        if (null != trustStorePassword) {
            controlConnectionProperties.setProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), trustStorePassword);
        }
        if (null != (clientCertificate = ds.getClientCertificate()) && clientCertificate.length() > 0 && null != (clientKeyPassword = (urlProps = Util.parseUrl(ds.getURL(), this.xaLogger)).getProperty(SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString()))) {
            controlConnectionProperties.setProperty(SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString(), clientKeyPassword);
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer("Creating an internal control connection for" + this.toString());
        }
        this.physicalControlConnection = null;
        this.physicalControlConnection = DriverJDBCVersion.getSQLServerConnection(this.toString());
        this.physicalControlConnection.connect(controlConnectionProperties, null);
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer("Created an internal control connection" + this.physicalControlConnection.toString() + " for " + this.toString() + " Physical connection:" + this.getPhysicalConnection().toString());
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(ds.toString() + " user:" + user);
        }
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        SQLServerXAResource result = this.xaResource;
        if (result == null) {
            this.lock.lock();
            try {
                result = this.xaResource;
                if (result == null) {
                    this.xaResource = result = new SQLServerXAResource(this.getPhysicalConnection(), this.physicalControlConnection, this.toString());
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        return result;
    }

    @Override
    public void close() throws SQLException {
        this.lock.lock();
        try {
            if (this.xaResource != null) {
                this.xaResource.close();
                this.xaResource = null;
            }
            if (null != this.physicalControlConnection) {
                this.physicalControlConnection.close();
                this.physicalControlConnection = null;
            }
        }
        finally {
            this.lock.unlock();
        }
        super.close();
    }
}

