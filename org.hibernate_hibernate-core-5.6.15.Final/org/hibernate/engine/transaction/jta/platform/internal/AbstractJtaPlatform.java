/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.util.Map;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.transaction.jta.platform.internal.JtaSynchronizationStrategy;
import org.hibernate.engine.transaction.jta.platform.internal.TransactionManagerAccess;
import org.hibernate.engine.transaction.jta.platform.internal.TransactionManagerBasedSynchronizationStrategy;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public abstract class AbstractJtaPlatform
implements JtaPlatform,
Configurable,
ServiceRegistryAwareService,
TransactionManagerAccess {
    private boolean cacheTransactionManager;
    private boolean cacheUserTransaction;
    private ServiceRegistryImplementor serviceRegistry;
    private final JtaSynchronizationStrategy tmSynchronizationStrategy = new TransactionManagerBasedSynchronizationStrategy(this);
    private TransactionManager transactionManager;
    private UserTransaction userTransaction;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    protected ServiceRegistry serviceRegistry() {
        return this.serviceRegistry;
    }

    protected JndiService jndiService() {
        return this.serviceRegistry().getService(JndiService.class);
    }

    protected abstract TransactionManager locateTransactionManager();

    protected abstract UserTransaction locateUserTransaction();

    @Override
    public void configure(Map configValues) {
        this.cacheTransactionManager = ConfigurationHelper.getBoolean("hibernate.jta.cacheTransactionManager", configValues, this.canCacheTransactionManagerByDefault());
        this.cacheUserTransaction = ConfigurationHelper.getBoolean("hibernate.jta.cacheUserTransaction", configValues, this.canCacheUserTransactionByDefault());
    }

    protected boolean canCacheTransactionManagerByDefault() {
        return true;
    }

    protected boolean canCacheUserTransactionByDefault() {
        return false;
    }

    protected boolean canCacheTransactionManager() {
        return this.cacheTransactionManager;
    }

    protected boolean canCacheUserTransaction() {
        return this.cacheUserTransaction;
    }

    @Override
    public TransactionManager retrieveTransactionManager() {
        if (this.canCacheTransactionManager()) {
            if (this.transactionManager == null) {
                this.transactionManager = this.locateTransactionManager();
            }
            return this.transactionManager;
        }
        return this.locateTransactionManager();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.retrieveTransactionManager();
    }

    @Override
    public UserTransaction retrieveUserTransaction() {
        if (this.canCacheUserTransaction()) {
            if (this.userTransaction == null) {
                this.userTransaction = this.locateUserTransaction();
            }
            return this.userTransaction;
        }
        return this.locateUserTransaction();
    }

    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

    protected JtaSynchronizationStrategy getSynchronizationStrategy() {
        return this.tmSynchronizationStrategy;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        this.getSynchronizationStrategy().registerSynchronization(synchronization);
    }

    @Override
    public boolean canRegisterSynchronization() {
        return this.getSynchronizationStrategy().canRegisterSynchronization();
    }

    @Override
    public int getCurrentStatus() throws SystemException {
        return this.retrieveTransactionManager().getStatus();
    }
}

