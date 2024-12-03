/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.dbcp2.managed.XAConnectionFactory;

public class DataSourceXAConnectionFactory
implements XAConnectionFactory {
    private final TransactionRegistry transactionRegistry;
    private final XADataSource xaDataSource;
    private String userName;
    private char[] userPassword;

    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource) {
        this(transactionManager, xaDataSource, null, null, null);
    }

    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource, String userName, char[] userPassword) {
        this(transactionManager, xaDataSource, userName, userPassword, null);
    }

    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource, String userName, char[] userPassword, TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        Objects.requireNonNull(transactionManager, "transactionManager");
        Objects.requireNonNull(xaDataSource, "xaDataSource");
        this.transactionRegistry = new TransactionRegistry(transactionManager, transactionSynchronizationRegistry);
        this.xaDataSource = xaDataSource;
        this.userName = userName;
        this.userPassword = Utils.clone(userPassword);
    }

    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource, String userName, String userPassword) {
        this(transactionManager, xaDataSource, userName, Utils.toCharArray(userPassword), null);
    }

    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource, TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        this(transactionManager, xaDataSource, null, null, transactionSynchronizationRegistry);
    }

    @Override
    public Connection createConnection() throws SQLException {
        XAConnection xaConnection = this.userName == null ? this.xaDataSource.getXAConnection() : this.xaDataSource.getXAConnection(this.userName, Utils.toString(this.userPassword));
        Connection connection = xaConnection.getConnection();
        XAResource xaResource = xaConnection.getXAResource();
        this.transactionRegistry.registerConnection(connection, xaResource);
        xaConnection.addConnectionEventListener(new XAConnectionEventListener());
        return connection;
    }

    @Override
    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    @Deprecated
    public String getUsername() {
        return this.userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public char[] getUserPassword() {
        return Utils.clone(this.userPassword);
    }

    public XADataSource getXaDataSource() {
        return this.xaDataSource;
    }

    public void setPassword(char[] userPassword) {
        this.userPassword = Utils.clone(userPassword);
    }

    public void setPassword(String userPassword) {
        this.userPassword = Utils.toCharArray(userPassword);
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    private static final class XAConnectionEventListener
    implements ConnectionEventListener {
        private XAConnectionEventListener() {
        }

        @Override
        public void connectionClosed(ConnectionEvent event) {
            PooledConnection pc = (PooledConnection)event.getSource();
            pc.removeConnectionEventListener(this);
            try {
                pc.close();
            }
            catch (SQLException e) {
                System.err.println("Failed to close XAConnection");
                e.printStackTrace();
            }
        }

        @Override
        public void connectionErrorOccurred(ConnectionEvent event) {
            this.connectionClosed(event);
        }
    }
}

