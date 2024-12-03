/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.transaction.Decision;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionIDNotFoundException;
import net.sf.ehcache.transaction.xa.XidTransactionID;

public abstract class AbstractTransactionIDFactory
implements TransactionIDFactory {
    protected abstract ConcurrentMap<TransactionID, Decision> getTransactionStates();

    @Override
    public void markForCommit(TransactionID transactionID) {
        block5: while (true) {
            Decision current;
            if ((current = (Decision)((Object)this.getTransactionStates().get(transactionID))) == null) {
                throw new TransactionIDNotFoundException("transaction state of transaction ID [" + transactionID + "] already cleaned up");
            }
            switch (current) {
                case IN_DOUBT: {
                    if (!this.getTransactionStates().replace(transactionID, Decision.IN_DOUBT, Decision.COMMIT)) continue block5;
                    return;
                }
                case ROLLBACK: {
                    throw new IllegalStateException(this + " already marked for rollback, cannot re-mark it for commit");
                }
                case COMMIT: {
                    return;
                }
            }
            break;
        }
        throw new AssertionError((Object)"unreachable code");
    }

    @Override
    public void markForRollback(XidTransactionID transactionID) {
        block5: while (true) {
            Decision current;
            if ((current = (Decision)((Object)this.getTransactionStates().get(transactionID))) == null) {
                throw new TransactionIDNotFoundException("transaction state of transaction ID [" + transactionID + "] already cleaned up");
            }
            switch (current) {
                case IN_DOUBT: {
                    if (!this.getTransactionStates().replace(transactionID, Decision.IN_DOUBT, Decision.ROLLBACK)) continue block5;
                    return;
                }
                case ROLLBACK: {
                    return;
                }
                case COMMIT: {
                    throw new IllegalStateException(this + " already marked for commit, cannot re-mark it for rollback");
                }
            }
            break;
        }
        throw new AssertionError();
    }

    @Override
    public boolean isDecisionCommit(TransactionID transactionID) {
        return Decision.COMMIT.equals(this.getTransactionStates().get(transactionID));
    }

    @Override
    public void clear(TransactionID transactionID) {
        this.getTransactionStates().remove(transactionID);
    }

    @Override
    public Set<XidTransactionID> getAllXidTransactionIDsFor(Ehcache cache) {
        String cacheName = cache.getName();
        HashSet<XidTransactionID> result = new HashSet<XidTransactionID>();
        for (TransactionID id : this.getTransactionStates().keySet()) {
            XidTransactionID xid;
            if (!(id instanceof XidTransactionID) || !cacheName.equals((xid = (XidTransactionID)id).getCacheName())) continue;
            result.add(xid);
        }
        return result;
    }

    @Override
    public Set<TransactionID> getAllTransactionIDs() {
        return Collections.unmodifiableSet(this.getTransactionStates().keySet());
    }
}

