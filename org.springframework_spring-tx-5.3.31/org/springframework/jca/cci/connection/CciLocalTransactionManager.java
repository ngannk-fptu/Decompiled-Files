/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.NotSupportedException
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.spi.LocalTransactionException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.connection;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.LocalTransactionException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jca.cci.connection.ConnectionFactoryUtils;
import org.springframework.jca.cci.connection.ConnectionHolder;
import org.springframework.jca.cci.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Deprecated
public class CciLocalTransactionManager
extends AbstractPlatformTransactionManager
implements ResourceTransactionManager,
InitializingBean {
    @Nullable
    private ConnectionFactory connectionFactory;

    public CciLocalTransactionManager() {
    }

    public CciLocalTransactionManager(ConnectionFactory connectionFactory) {
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    public void setConnectionFactory(@Nullable ConnectionFactory cf) {
        this.connectionFactory = cf instanceof TransactionAwareConnectionFactoryProxy ? ((TransactionAwareConnectionFactoryProxy)cf).getTargetConnectionFactory() : cf;
    }

    @Nullable
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    private ConnectionFactory obtainConnectionFactory() {
        ConnectionFactory connectionFactory = this.getConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No ConnectionFactory set");
        return connectionFactory;
    }

    public void afterPropertiesSet() {
        if (this.getConnectionFactory() == null) {
            throw new IllegalArgumentException("Property 'connectionFactory' is required");
        }
    }

    @Override
    public Object getResourceFactory() {
        return this.obtainConnectionFactory();
    }

    @Override
    protected Object doGetTransaction() {
        CciLocalTransactionObject txObject = new CciLocalTransactionObject();
        ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(this.obtainConnectionFactory());
        txObject.setConnectionHolder(conHolder);
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)transaction;
        return txObject.hasConnectionHolder();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)transaction;
        ConnectionFactory connectionFactory = this.obtainConnectionFactory();
        Connection con = null;
        try {
            con = connectionFactory.getConnection();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Acquired Connection [" + con + "] for local CCI transaction"));
            }
            ConnectionHolder connectionHolder = new ConnectionHolder(con);
            connectionHolder.setSynchronizedWithTransaction(true);
            con.getLocalTransaction().begin();
            int timeout = this.determineTimeout(definition);
            if (timeout != -1) {
                connectionHolder.setTimeoutInSeconds(timeout);
            }
            txObject.setConnectionHolder(connectionHolder);
            TransactionSynchronizationManager.bindResource(connectionFactory, connectionHolder);
        }
        catch (NotSupportedException ex) {
            ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
            throw new CannotCreateTransactionException("CCI Connection does not support local transactions", ex);
        }
        catch (LocalTransactionException ex) {
            ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
            throw new CannotCreateTransactionException("Could not begin local CCI transaction", ex);
        }
        catch (Throwable ex) {
            ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
            throw new TransactionSystemException("Unexpected failure on begin of CCI local transaction", ex);
        }
    }

    @Override
    protected Object doSuspend(Object transaction) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)transaction;
        txObject.setConnectionHolder(null);
        return TransactionSynchronizationManager.unbindResource(this.obtainConnectionFactory());
    }

    @Override
    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        ConnectionHolder conHolder = (ConnectionHolder)suspendedResources;
        TransactionSynchronizationManager.bindResource(this.obtainConnectionFactory(), conHolder);
    }

    protected boolean isRollbackOnly(Object transaction) throws TransactionException {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)transaction;
        return txObject.getConnectionHolder().isRollbackOnly();
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (status.isDebug()) {
            this.logger.debug((Object)("Committing CCI local transaction on Connection [" + con + "]"));
        }
        try {
            con.getLocalTransaction().commit();
        }
        catch (LocalTransactionException ex) {
            throw new TransactionSystemException("Could not commit CCI local transaction", ex);
        }
        catch (ResourceException ex) {
            throw new TransactionSystemException("Unexpected failure on commit of CCI local transaction", ex);
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (status.isDebug()) {
            this.logger.debug((Object)("Rolling back CCI local transaction on Connection [" + con + "]"));
        }
        try {
            con.getLocalTransaction().rollback();
        }
        catch (LocalTransactionException ex) {
            throw new TransactionSystemException("Could not roll back CCI local transaction", ex);
        }
        catch (ResourceException ex) {
            throw new TransactionSystemException("Unexpected failure on rollback of CCI local transaction", ex);
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)status.getTransaction();
        if (status.isDebug()) {
            this.logger.debug((Object)("Setting CCI local transaction [" + txObject.getConnectionHolder().getConnection() + "] rollback-only"));
        }
        txObject.getConnectionHolder().setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        CciLocalTransactionObject txObject = (CciLocalTransactionObject)transaction;
        ConnectionFactory connectionFactory = this.obtainConnectionFactory();
        TransactionSynchronizationManager.unbindResource(connectionFactory);
        txObject.getConnectionHolder().clear();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Releasing CCI Connection [" + con + "] after transaction"));
        }
        ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
    }

    private static class CciLocalTransactionObject {
        @Nullable
        private ConnectionHolder connectionHolder;

        private CciLocalTransactionObject() {
        }

        public void setConnectionHolder(@Nullable ConnectionHolder connectionHolder) {
            this.connectionHolder = connectionHolder;
        }

        public ConnectionHolder getConnectionHolder() {
            Assert.state((this.connectionHolder != null ? 1 : 0) != 0, (String)"No ConnectionHolder available");
            return this.connectionHolder;
        }

        public boolean hasConnectionHolder() {
            return this.connectionHolder != null;
        }
    }
}

