/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.support.ResourceHolder;

public abstract class ResourceHolderSupport
implements ResourceHolder {
    private boolean synchronizedWithTransaction = false;
    private boolean rollbackOnly = false;
    @Nullable
    private Date deadline;
    private int referenceCount = 0;
    private boolean isVoid = false;

    public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    public boolean isSynchronizedWithTransaction() {
        return this.synchronizedWithTransaction;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    public void resetRollbackOnly() {
        this.rollbackOnly = false;
    }

    public boolean isRollbackOnly() {
        return this.rollbackOnly;
    }

    public void setTimeoutInSeconds(int seconds) {
        this.setTimeoutInMillis((long)seconds * 1000L);
    }

    public void setTimeoutInMillis(long millis) {
        this.deadline = new Date(System.currentTimeMillis() + millis);
    }

    public boolean hasTimeout() {
        return this.deadline != null;
    }

    @Nullable
    public Date getDeadline() {
        return this.deadline;
    }

    public int getTimeToLiveInSeconds() {
        double diff = (double)this.getTimeToLiveInMillis() / 1000.0;
        int secs = (int)Math.ceil(diff);
        this.checkTransactionTimeout(secs <= 0);
        return secs;
    }

    public long getTimeToLiveInMillis() throws TransactionTimedOutException {
        if (this.deadline == null) {
            throw new IllegalStateException("No timeout specified for this resource holder");
        }
        long timeToLive = this.deadline.getTime() - System.currentTimeMillis();
        this.checkTransactionTimeout(timeToLive <= 0L);
        return timeToLive;
    }

    private void checkTransactionTimeout(boolean deadlineReached) throws TransactionTimedOutException {
        if (deadlineReached) {
            this.setRollbackOnly();
            throw new TransactionTimedOutException("Transaction timed out: deadline was " + this.deadline);
        }
    }

    public void requested() {
        ++this.referenceCount;
    }

    public void released() {
        --this.referenceCount;
    }

    public boolean isOpen() {
        return this.referenceCount > 0;
    }

    public void clear() {
        this.synchronizedWithTransaction = false;
        this.rollbackOnly = false;
        this.deadline = null;
    }

    @Override
    public void reset() {
        this.clear();
        this.referenceCount = 0;
    }

    @Override
    public void unbound() {
        this.isVoid = true;
    }

    @Override
    public boolean isVoid() {
        return this.isVoid;
    }
}

