/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionTimeoutException;
import net.sf.ehcache.transaction.local.LocalTransactionStore;
import net.sf.ehcache.transaction.local.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTransactionContext {
    private static final Logger LOG = LoggerFactory.getLogger((String)LocalTransactionContext.class.getName());
    private boolean rollbackOnly;
    private final long expirationTimestamp;
    private final TransactionIDFactory transactionIdFactory;
    private final TransactionID transactionId;
    private final Map<String, List<SoftLock>> softLockMap = new HashMap<String, List<SoftLock>>();
    private final Map<String, LocalTransactionStore> storeMap = new HashMap<String, LocalTransactionStore>();
    private final List<TransactionListener> listeners = new ArrayList<TransactionListener>();

    public LocalTransactionContext(int transactionTimeout, TransactionIDFactory transactionIdFactory) {
        this.expirationTimestamp = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) + TimeUnit.MILLISECONDS.convert(transactionTimeout, TimeUnit.SECONDS);
        this.transactionIdFactory = transactionIdFactory;
        this.transactionId = transactionIdFactory.createTransactionID();
    }

    public boolean timedOut() {
        return this.timeBeforeTimeout() <= 0L;
    }

    public long timeBeforeTimeout() {
        return Math.max(0L, this.expirationTimestamp - TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS));
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    public void registerSoftLock(String cacheName, LocalTransactionStore store, SoftLock softLock) {
        List<SoftLock> softLocks = this.softLockMap.get(cacheName);
        if (softLocks == null) {
            softLocks = new ArrayList<SoftLock>();
            this.softLockMap.put(cacheName, softLocks);
            this.storeMap.put(cacheName, store);
        }
        softLocks.add(softLock);
    }

    public void updateSoftLock(String cacheName, SoftLock softLock) {
        List<SoftLock> softLocks = this.softLockMap.get(cacheName);
        softLocks.remove(softLock);
        softLocks.add(softLock);
    }

    public List<SoftLock> getSoftLocksForCache(String cacheName) {
        List<SoftLock> softLocks = this.softLockMap.get(cacheName);
        if (softLocks == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(softLocks);
    }

    public boolean hasLockedAnything() {
        return !this.softLockMap.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void commit(boolean ignoreTimeout) {
        if (!ignoreTimeout && this.timedOut()) {
            this.rollback();
            throw new TransactionTimeoutException("transaction timed out, rolled back on commit");
        }
        if (this.rollbackOnly) {
            this.rollback();
            throw new TransactionException("transaction was marked as rollback only, rolled back on commit");
        }
        try {
            this.fireBeforeCommitEvent();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} participating cache(s), committing transaction {}", (Object)this.softLockMap.keySet().size(), (Object)this.transactionId);
            }
            this.freeze();
            this.transactionIdFactory.markForCommit(this.transactionId);
            for (Map.Entry<String, List<SoftLock>> stringListEntry : this.softLockMap.entrySet()) {
                String cacheName = stringListEntry.getKey();
                LocalTransactionStore store = this.storeMap.get(cacheName);
                List<SoftLock> softLocks = stringListEntry.getValue();
                LOG.debug("committing soft locked values of cache {}", (Object)cacheName);
                store.commit(softLocks, this.transactionId);
            }
            LOG.debug("committed transaction {}", (Object)this.transactionId);
        }
        finally {
            try {
                this.unfreezeAndUnlock();
            }
            finally {
                this.softLockMap.clear();
                this.storeMap.clear();
                this.fireAfterCommitEvent();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rollback() {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} participating cache(s), rolling back transaction {}", (Object)this.softLockMap.keySet().size(), (Object)this.transactionId);
            }
            this.freeze();
            for (Map.Entry<String, List<SoftLock>> stringListEntry : this.softLockMap.entrySet()) {
                String cacheName = stringListEntry.getKey();
                LocalTransactionStore store = this.storeMap.get(cacheName);
                List<SoftLock> softLocks = stringListEntry.getValue();
                LOG.debug("rolling back soft locked values of cache {}", (Object)cacheName);
                store.rollback(softLocks, this.transactionId);
            }
            LOG.debug("rolled back transaction {}", (Object)this.transactionId);
        }
        finally {
            try {
                this.unfreezeAndUnlock();
            }
            finally {
                this.softLockMap.clear();
                this.storeMap.clear();
                this.fireAfterRollbackEvent();
            }
        }
    }

    public TransactionID getTransactionId() {
        return this.transactionId;
    }

    public void addListener(TransactionListener listener) {
        this.listeners.add(listener);
    }

    private void fireBeforeCommitEvent() {
        for (TransactionListener listener : this.listeners) {
            try {
                listener.beforeCommit();
            }
            catch (Exception e) {
                LOG.error("beforeCommit error", (Throwable)e);
            }
        }
    }

    private void fireAfterCommitEvent() {
        for (TransactionListener listener : this.listeners) {
            try {
                listener.afterCommit();
            }
            catch (Exception e) {
                LOG.error("afterCommit error", (Throwable)e);
            }
        }
    }

    private void fireAfterRollbackEvent() {
        for (TransactionListener listener : this.listeners) {
            try {
                listener.afterRollback();
            }
            catch (Exception e) {
                LOG.error("afterRollback error", (Throwable)e);
            }
        }
    }

    private void unfreezeAndUnlock() {
        LOG.debug("unfreezing and unlocking soft lock(s)");
        boolean success = true;
        for (Map.Entry<String, List<SoftLock>> stringListEntry : this.softLockMap.entrySet()) {
            List<SoftLock> softLocks = stringListEntry.getValue();
            for (SoftLock softLock : softLocks) {
                try {
                    softLock.unfreeze();
                    LOG.debug("unfroze {}", (Object)softLock);
                }
                catch (Exception e) {
                    success = false;
                    LOG.error("error unfreezing " + softLock, (Throwable)e);
                }
                try {
                    softLock.unlock();
                    LOG.debug("unlocked {}", (Object)softLock);
                }
                catch (Exception e) {
                    success = false;
                    LOG.error("error unlocking " + softLock, (Throwable)e);
                }
            }
        }
        if (!success) {
            throw new TransactionException("Error unfreezing/unlocking transaction with ID " + this.transactionId);
        }
    }

    private void freeze() {
        for (Map.Entry<String, List<SoftLock>> stringListEntry : this.softLockMap.entrySet()) {
            List<SoftLock> softLocks = stringListEntry.getValue();
            for (SoftLock softLock : softLocks) {
                softLock.freeze();
            }
        }
    }

    public int hashCode() {
        return this.transactionId.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LocalTransactionContext) {
            LocalTransactionContext otherCtx = (LocalTransactionContext)obj;
            return this.transactionId.equals(otherCtx.transactionId);
        }
        return false;
    }
}

