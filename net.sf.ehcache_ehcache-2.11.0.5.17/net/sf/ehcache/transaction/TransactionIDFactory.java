/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.Set;
import javax.transaction.xa.Xid;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDSerializedForm;
import net.sf.ehcache.transaction.XidTransactionIDSerializedForm;
import net.sf.ehcache.transaction.xa.XidTransactionID;

public interface TransactionIDFactory {
    public TransactionID createTransactionID();

    public TransactionID restoreTransactionID(TransactionIDSerializedForm var1);

    public XidTransactionID createXidTransactionID(Xid var1, Ehcache var2);

    public XidTransactionID restoreXidTransactionID(XidTransactionIDSerializedForm var1);

    public void markForCommit(TransactionID var1);

    public void markForRollback(XidTransactionID var1);

    public boolean isDecisionCommit(TransactionID var1);

    public void clear(TransactionID var1);

    public Set<XidTransactionID> getAllXidTransactionIDsFor(Ehcache var1);

    public Set<TransactionID> getAllTransactionIDs();

    public Boolean isPersistent();

    public boolean isExpired(TransactionID var1);
}

