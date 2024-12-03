/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.spi.ApplicationServerInternalException
 *  javax.resource.spi.UnavailableException
 *  javax.resource.spi.endpoint.MessageEndpoint
 *  javax.resource.spi.endpoint.MessageEndpointFactory
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.endpoint;

import java.lang.reflect.Method;
import javax.resource.ResourceException;
import javax.resource.spi.ApplicationServerInternalException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.SimpleTransactionFactory;
import org.springframework.transaction.jta.TransactionFactory;
import org.springframework.util.Assert;

public abstract class AbstractMessageEndpointFactory
implements MessageEndpointFactory,
BeanNameAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private TransactionFactory transactionFactory;
    @Nullable
    private String transactionName;
    private int transactionTimeout = -1;
    @Nullable
    private String beanName;

    public void setTransactionManager(Object transactionManager) {
        if (transactionManager instanceof TransactionFactory) {
            this.transactionFactory = (TransactionFactory)transactionManager;
        } else if (transactionManager instanceof TransactionManager) {
            this.transactionFactory = new SimpleTransactionFactory((TransactionManager)transactionManager);
        } else {
            throw new IllegalArgumentException("Transaction manager [" + transactionManager + "] is neither a [org.springframework.transaction.jta.TransactionFactory} nor a [javax.transaction.TransactionManager]");
        }
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public void setTransactionTimeout(int transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Nullable
    public String getActivationName() {
        return this.beanName;
    }

    @Nullable
    public Class<?> getEndpointClass() {
        return null;
    }

    public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
        return this.transactionFactory != null;
    }

    public MessageEndpoint createEndpoint(XAResource xaResource) throws UnavailableException {
        AbstractMessageEndpoint endpoint = this.createEndpointInternal();
        endpoint.initXAResource(xaResource);
        return endpoint;
    }

    public MessageEndpoint createEndpoint(XAResource xaResource, long timeout) throws UnavailableException {
        AbstractMessageEndpoint endpoint = this.createEndpointInternal();
        endpoint.initXAResource(xaResource);
        return endpoint;
    }

    protected abstract AbstractMessageEndpoint createEndpointInternal() throws UnavailableException;

    private class TransactionDelegate {
        @Nullable
        private final XAResource xaResource;
        @Nullable
        private Transaction transaction;
        private boolean rollbackOnly;

        public TransactionDelegate(XAResource xaResource) {
            if (xaResource == null && AbstractMessageEndpointFactory.this.transactionFactory != null && !AbstractMessageEndpointFactory.this.transactionFactory.supportsResourceAdapterManagedTransactions()) {
                throw new IllegalStateException("ResourceAdapter-provided XAResource is required for transaction management. Check your ResourceAdapter's configuration.");
            }
            this.xaResource = xaResource;
        }

        public void beginTransaction() throws Exception {
            if (AbstractMessageEndpointFactory.this.transactionFactory != null && this.xaResource != null) {
                this.transaction = AbstractMessageEndpointFactory.this.transactionFactory.createTransaction(AbstractMessageEndpointFactory.this.transactionName, AbstractMessageEndpointFactory.this.transactionTimeout);
                this.transaction.enlistResource(this.xaResource);
            }
        }

        public void setRollbackOnly() {
            if (this.transaction != null) {
                this.rollbackOnly = true;
            }
        }

        public void endTransaction() throws Exception {
            if (this.transaction != null) {
                try {
                    if (this.rollbackOnly) {
                        this.transaction.rollback();
                    } else {
                        this.transaction.commit();
                    }
                }
                finally {
                    this.transaction = null;
                    this.rollbackOnly = false;
                }
            }
        }
    }

    protected abstract class AbstractMessageEndpoint
    implements MessageEndpoint {
        @Nullable
        private TransactionDelegate transactionDelegate;
        private boolean beforeDeliveryCalled = false;
        @Nullable
        private ClassLoader previousContextClassLoader;

        protected AbstractMessageEndpoint() {
        }

        void initXAResource(XAResource xaResource) {
            this.transactionDelegate = new TransactionDelegate(xaResource);
        }

        public void beforeDelivery(@Nullable Method method) throws ResourceException {
            this.beforeDeliveryCalled = true;
            Assert.state((this.transactionDelegate != null ? 1 : 0) != 0, (String)"Not initialized");
            try {
                this.transactionDelegate.beginTransaction();
            }
            catch (Throwable ex) {
                throw new ApplicationServerInternalException("Failed to begin transaction", ex);
            }
            Thread currentThread = Thread.currentThread();
            this.previousContextClassLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(this.getEndpointClassLoader());
        }

        protected abstract ClassLoader getEndpointClassLoader();

        protected final boolean hasBeforeDeliveryBeenCalled() {
            return this.beforeDeliveryCalled;
        }

        protected void onEndpointException(Throwable ex) {
            Assert.state((this.transactionDelegate != null ? 1 : 0) != 0, (String)"Not initialized");
            this.transactionDelegate.setRollbackOnly();
            AbstractMessageEndpointFactory.this.logger.debug((Object)"Transaction marked as rollback-only after endpoint exception", ex);
        }

        public void afterDelivery() throws ResourceException {
            Assert.state((this.transactionDelegate != null ? 1 : 0) != 0, (String)"Not initialized");
            this.beforeDeliveryCalled = false;
            Thread.currentThread().setContextClassLoader(this.previousContextClassLoader);
            this.previousContextClassLoader = null;
            try {
                this.transactionDelegate.endTransaction();
            }
            catch (Throwable ex) {
                AbstractMessageEndpointFactory.this.logger.warn((Object)"Failed to complete transaction after endpoint delivery", ex);
                throw new ApplicationServerInternalException("Failed to complete transaction", ex);
            }
        }

        public void release() {
            if (this.transactionDelegate != null) {
                try {
                    this.transactionDelegate.setRollbackOnly();
                    this.transactionDelegate.endTransaction();
                }
                catch (Throwable ex) {
                    AbstractMessageEndpointFactory.this.logger.warn((Object)"Could not complete unfinished transaction on endpoint release", ex);
                }
            }
        }
    }
}

