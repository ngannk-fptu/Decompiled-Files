/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.support;

import java.lang.reflect.UndeclaredThrowableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.util.Assert;

public class TransactionTemplate
extends DefaultTransactionDefinition
implements TransactionOperations,
InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private PlatformTransactionManager transactionManager;

    public TransactionTemplate() {
    }

    public TransactionTemplate(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public TransactionTemplate(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
        super(transactionDefinition);
        this.transactionManager = transactionManager;
    }

    public void setTransactionManager(@Nullable PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Nullable
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void afterPropertiesSet() {
        if (this.transactionManager == null) {
            throw new IllegalArgumentException("Property 'transactionManager' is required");
        }
    }

    @Override
    @Nullable
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        T result;
        Assert.state((this.transactionManager != null ? 1 : 0) != 0, (String)"No PlatformTransactionManager set");
        if (this.transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
            return ((CallbackPreferringPlatformTransactionManager)this.transactionManager).execute(this, action);
        }
        TransactionStatus status = this.transactionManager.getTransaction(this);
        try {
            result = action.doInTransaction(status);
        }
        catch (Error | RuntimeException ex) {
            this.rollbackOnException(status, ex);
            throw ex;
        }
        catch (Throwable ex) {
            this.rollbackOnException(status, ex);
            throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
        }
        this.transactionManager.commit(status);
        return result;
    }

    private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
        Assert.state((this.transactionManager != null ? 1 : 0) != 0, (String)"No PlatformTransactionManager set");
        this.logger.debug((Object)"Initiating transaction rollback on application exception", ex);
        try {
            this.transactionManager.rollback(status);
        }
        catch (TransactionSystemException ex2) {
            this.logger.error((Object)"Application exception overridden by rollback exception", ex);
            ex2.initApplicationException(ex);
            throw ex2;
        }
        catch (Error | RuntimeException ex2) {
            this.logger.error((Object)"Application exception overridden by rollback exception", ex);
            throw ex2;
        }
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other || super.equals(other) && (!(other instanceof TransactionTemplate) || this.getTransactionManager() == ((TransactionTemplate)other).getTransactionManager());
    }
}

