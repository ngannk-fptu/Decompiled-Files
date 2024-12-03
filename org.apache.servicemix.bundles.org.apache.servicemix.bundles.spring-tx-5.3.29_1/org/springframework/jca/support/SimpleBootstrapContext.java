/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.BootstrapContext
 *  javax.resource.spi.UnavailableException
 *  javax.resource.spi.XATerminator
 *  javax.resource.spi.work.WorkContext
 *  javax.resource.spi.work.WorkManager
 *  javax.transaction.TransactionSynchronizationRegistry
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.support;

import java.util.Timer;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.XATerminator;
import javax.resource.spi.work.WorkContext;
import javax.resource.spi.work.WorkManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SimpleBootstrapContext
implements BootstrapContext {
    @Nullable
    private WorkManager workManager;
    @Nullable
    private XATerminator xaTerminator;
    @Nullable
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    public SimpleBootstrapContext(@Nullable WorkManager workManager) {
        this.workManager = workManager;
    }

    public SimpleBootstrapContext(@Nullable WorkManager workManager, @Nullable XATerminator xaTerminator) {
        this.workManager = workManager;
        this.xaTerminator = xaTerminator;
    }

    public SimpleBootstrapContext(@Nullable WorkManager workManager, @Nullable XATerminator xaTerminator, @Nullable TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        this.workManager = workManager;
        this.xaTerminator = xaTerminator;
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }

    public WorkManager getWorkManager() {
        Assert.state((this.workManager != null ? 1 : 0) != 0, (String)"No WorkManager available");
        return this.workManager;
    }

    @Nullable
    public XATerminator getXATerminator() {
        return this.xaTerminator;
    }

    public Timer createTimer() throws UnavailableException {
        return new Timer();
    }

    public boolean isContextSupported(Class<? extends WorkContext> workContextClass) {
        return false;
    }

    @Nullable
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return this.transactionSynchronizationRegistry;
    }
}

