/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.PersistenceExceptionTranslationRepositoryProxyPostProcessor;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.repository.core.support.TransactionalRepositoryProxyPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class TransactionalRepositoryFactoryBeanSupport<T extends Repository<S, ID>, S, ID>
extends RepositoryFactoryBeanSupport<T, S, ID>
implements BeanFactoryAware {
    private String transactionManagerName = "transactionManager";
    @Nullable
    private RepositoryProxyPostProcessor txPostProcessor;
    @Nullable
    private RepositoryProxyPostProcessor exceptionPostProcessor;
    private boolean enableDefaultTransactions = true;

    protected TransactionalRepositoryFactoryBeanSupport(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    public void setTransactionManager(String transactionManager) {
        this.transactionManagerName = transactionManager == null ? "transactionManager" : transactionManager;
    }

    public void setEnableDefaultTransactions(boolean enableDefaultTransactions) {
        this.enableDefaultTransactions = enableDefaultTransactions;
    }

    @Override
    protected final RepositoryFactorySupport createRepositoryFactory() {
        RepositoryProxyPostProcessor txPostProcessor;
        RepositoryFactorySupport factory = this.doCreateRepositoryFactory();
        RepositoryProxyPostProcessor exceptionPostProcessor = this.exceptionPostProcessor;
        if (exceptionPostProcessor != null) {
            factory.addRepositoryProxyPostProcessor(exceptionPostProcessor);
        }
        if ((txPostProcessor = this.txPostProcessor) != null) {
            factory.addRepositoryProxyPostProcessor(txPostProcessor);
        }
        return factory;
    }

    protected abstract RepositoryFactorySupport doCreateRepositoryFactory();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf(ListableBeanFactory.class, (Object)beanFactory);
        super.setBeanFactory(beanFactory);
        ListableBeanFactory listableBeanFactory = (ListableBeanFactory)beanFactory;
        this.txPostProcessor = new TransactionalRepositoryProxyPostProcessor(listableBeanFactory, this.transactionManagerName, this.enableDefaultTransactions);
        this.exceptionPostProcessor = new PersistenceExceptionTranslationRepositoryProxyPostProcessor(listableBeanFactory);
    }
}

