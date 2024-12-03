/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.connection;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jca.cci.CannotGetCciConnectionException;
import org.springframework.jca.cci.connection.ConnectionHolder;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Deprecated
public abstract class ConnectionFactoryUtils {
    private static final Log logger = LogFactory.getLog(ConnectionFactoryUtils.class);

    public static Connection getConnection(ConnectionFactory cf) throws CannotGetCciConnectionException {
        return ConnectionFactoryUtils.getConnection(cf, null);
    }

    public static Connection getConnection(ConnectionFactory cf, @Nullable ConnectionSpec spec) throws CannotGetCciConnectionException {
        try {
            if (spec != null) {
                Assert.notNull((Object)cf, (String)"No ConnectionFactory specified");
                return cf.getConnection(spec);
            }
            return ConnectionFactoryUtils.doGetConnection(cf);
        }
        catch (ResourceException ex) {
            throw new CannotGetCciConnectionException("Could not get CCI Connection", ex);
        }
    }

    public static Connection doGetConnection(ConnectionFactory cf) throws ResourceException {
        Assert.notNull((Object)cf, (String)"No ConnectionFactory specified");
        ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(cf);
        if (conHolder != null) {
            return conHolder.getConnection();
        }
        logger.debug((Object)"Opening CCI Connection");
        Connection con = cf.getConnection();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            conHolder = new ConnectionHolder(con);
            conHolder.setSynchronizedWithTransaction(true);
            TransactionSynchronizationManager.registerSynchronization(new ConnectionSynchronization(conHolder, cf));
            TransactionSynchronizationManager.bindResource(cf, conHolder);
        }
        return con;
    }

    public static boolean isConnectionTransactional(Connection con, @Nullable ConnectionFactory cf) {
        if (cf == null) {
            return false;
        }
        ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(cf);
        return conHolder != null && conHolder.getConnection() == con;
    }

    public static void releaseConnection(@Nullable Connection con, @Nullable ConnectionFactory cf) {
        try {
            ConnectionFactoryUtils.doReleaseConnection(con, cf);
        }
        catch (ResourceException ex) {
            logger.debug((Object)"Could not close CCI Connection", (Throwable)ex);
        }
        catch (Throwable ex) {
            logger.debug((Object)"Unexpected exception on closing CCI Connection", ex);
        }
    }

    public static void doReleaseConnection(@Nullable Connection con, @Nullable ConnectionFactory cf) throws ResourceException {
        if (con == null || ConnectionFactoryUtils.isConnectionTransactional(con, cf)) {
            return;
        }
        con.close();
    }

    private static class ConnectionSynchronization
    extends ResourceHolderSynchronization<ConnectionHolder, ConnectionFactory> {
        public ConnectionSynchronization(ConnectionHolder connectionHolder, ConnectionFactory connectionFactory) {
            super(connectionHolder, connectionFactory);
        }

        @Override
        protected void releaseResource(ConnectionHolder resourceHolder, ConnectionFactory resourceKey) {
            ConnectionFactoryUtils.releaseConnection(resourceHolder.getConnection(), resourceKey);
        }
    }
}

