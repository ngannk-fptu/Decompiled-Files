/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package net.sf.ehcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionTimeoutException;
import net.sf.ehcache.transaction.local.LocalRecoveryManager;
import net.sf.ehcache.transaction.local.LocalTransactionContext;
import net.sf.ehcache.util.lang.VicariousThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class TransactionController {
    private static final Logger LOG = LoggerFactory.getLogger((String)TransactionController.class.getName());
    private static final String MDC_KEY = "ehcache-txid";
    private final VicariousThreadLocal<TransactionID> currentTransactionIdThreadLocal = new VicariousThreadLocal();
    private final ConcurrentMap<TransactionID, LocalTransactionContext> contextMap = new ConcurrentHashMap<TransactionID, LocalTransactionContext>();
    private final TransactionIDFactory transactionIDFactory;
    private final LocalRecoveryManager localRecoveryManager;
    private volatile int defaultTransactionTimeout;
    private final TransactionControllerStatistics statistics = new TransactionControllerStatistics();

    TransactionController(TransactionIDFactory transactionIDFactory, int defaultTransactionTimeoutInSeconds) {
        this.transactionIDFactory = transactionIDFactory;
        this.localRecoveryManager = new LocalRecoveryManager(transactionIDFactory);
        this.defaultTransactionTimeout = defaultTransactionTimeoutInSeconds;
    }

    public int getDefaultTransactionTimeout() {
        return this.defaultTransactionTimeout;
    }

    public void setDefaultTransactionTimeout(int defaultTransactionTimeoutSeconds) {
        if (defaultTransactionTimeoutSeconds < 0) {
            throw new IllegalArgumentException("timeout cannot be < 0");
        }
        this.defaultTransactionTimeout = defaultTransactionTimeoutSeconds;
    }

    public void begin() {
        this.begin(this.defaultTransactionTimeout);
    }

    public void begin(int transactionTimeoutSeconds) {
        TransactionID txId = this.currentTransactionIdThreadLocal.get();
        if (txId != null) {
            throw new TransactionException("transaction already started");
        }
        LocalTransactionContext newTx = new LocalTransactionContext(transactionTimeoutSeconds, this.transactionIDFactory);
        this.contextMap.put(newTx.getTransactionId(), newTx);
        this.currentTransactionIdThreadLocal.set(newTx.getTransactionId());
        MDC.put((String)MDC_KEY, (String)newTx.getTransactionId().toString());
        LOG.debug("begun transaction {}", (Object)newTx.getTransactionId());
    }

    public void commit() {
        this.commit(false);
    }

    public void commit(boolean ignoreTimeout) {
        TransactionID txId = this.currentTransactionIdThreadLocal.get();
        if (txId == null) {
            throw new TransactionException("no transaction started");
        }
        LocalTransactionContext currentTx = (LocalTransactionContext)this.contextMap.get(txId);
        try {
            currentTx.commit(ignoreTimeout);
            this.statistics.transactionCommitted();
        }
        catch (TransactionTimeoutException tte) {
            this.statistics.transactionTimedOut();
            this.statistics.transactionRolledBack();
            throw tte;
        }
        catch (TransactionException te) {
            this.statistics.transactionRolledBack();
            throw te;
        }
        finally {
            this.contextMap.remove(txId);
            this.transactionIDFactory.clear(txId);
            this.currentTransactionIdThreadLocal.remove();
            MDC.remove((String)MDC_KEY);
        }
    }

    public void rollback() {
        TransactionID txId = this.currentTransactionIdThreadLocal.get();
        if (txId == null) {
            throw new TransactionException("no transaction started");
        }
        LocalTransactionContext currentTx = (LocalTransactionContext)this.contextMap.get(txId);
        try {
            currentTx.rollback();
            this.statistics.transactionRolledBack();
        }
        finally {
            this.contextMap.remove(txId);
            this.transactionIDFactory.clear(txId);
            this.currentTransactionIdThreadLocal.remove();
            MDC.remove((String)MDC_KEY);
        }
    }

    public void setRollbackOnly() {
        TransactionID txId = this.currentTransactionIdThreadLocal.get();
        if (txId == null) {
            throw new TransactionException("no transaction started");
        }
        LocalTransactionContext currentTx = (LocalTransactionContext)this.contextMap.get(txId);
        currentTx.setRollbackOnly();
    }

    public LocalTransactionContext getCurrentTransactionContext() {
        TransactionID txId = this.currentTransactionIdThreadLocal.get();
        if (txId == null) {
            return null;
        }
        return (LocalTransactionContext)this.contextMap.get(txId);
    }

    public long getTransactionCommittedCount() {
        return this.statistics.getTransactionCommittedCount();
    }

    public long getTransactionRolledBackCount() {
        return this.statistics.getTransactionRolledBackCount();
    }

    public long getTransactionTimedOutCount() {
        return this.statistics.getTransactionTimedOutCount();
    }

    public LocalRecoveryManager getRecoveryManager() {
        return this.localRecoveryManager;
    }

    private static class TransactionControllerStatistics {
        private final AtomicLong committed = new AtomicLong();
        private final AtomicLong rolledBack = new AtomicLong();
        private final AtomicLong timedOut = new AtomicLong();

        private TransactionControllerStatistics() {
        }

        void transactionCommitted() {
            this.committed.incrementAndGet();
        }

        void transactionRolledBack() {
            this.rolledBack.incrementAndGet();
        }

        void transactionTimedOut() {
            this.timedOut.incrementAndGet();
        }

        long getTransactionCommittedCount() {
            return this.committed.get();
        }

        long getTransactionRolledBackCount() {
            return this.rolledBack.get();
        }

        long getTransactionTimedOutCount() {
            return this.timedOut.get();
        }
    }
}

