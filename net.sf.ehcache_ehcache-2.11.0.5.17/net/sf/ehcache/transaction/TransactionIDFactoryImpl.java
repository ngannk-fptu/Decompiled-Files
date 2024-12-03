/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.transaction.xa.Xid;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.transaction.AbstractTransactionIDFactory;
import net.sf.ehcache.transaction.Decision;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDImpl;
import net.sf.ehcache.transaction.TransactionIDSerializedForm;
import net.sf.ehcache.transaction.XidTransactionIDSerializedForm;
import net.sf.ehcache.transaction.xa.XidTransactionID;
import net.sf.ehcache.transaction.xa.XidTransactionIDImpl;

public class TransactionIDFactoryImpl
extends AbstractTransactionIDFactory {
    private final ConcurrentMap<TransactionID, Decision> transactionStates = new ConcurrentHashMap<TransactionID, Decision>();

    @Override
    public TransactionID createTransactionID() {
        TransactionIDImpl id = new TransactionIDImpl();
        this.getTransactionStates().putIfAbsent(id, Decision.IN_DOUBT);
        return id;
    }

    @Override
    public TransactionID restoreTransactionID(TransactionIDSerializedForm serializedForm) {
        throw new UnsupportedOperationException("unclustered transaction IDs are directly deserializable!");
    }

    @Override
    public XidTransactionID createXidTransactionID(Xid xid, Ehcache cache) {
        XidTransactionIDImpl id = new XidTransactionIDImpl(xid, cache.getName());
        this.getTransactionStates().putIfAbsent(id, Decision.IN_DOUBT);
        return id;
    }

    @Override
    public XidTransactionID restoreXidTransactionID(XidTransactionIDSerializedForm serializedForm) {
        throw new UnsupportedOperationException("unclustered transaction IDs are directly deserializable!");
    }

    @Override
    protected ConcurrentMap<TransactionID, Decision> getTransactionStates() {
        return this.transactionStates;
    }

    @Override
    public Boolean isPersistent() {
        return Boolean.FALSE;
    }

    @Override
    public boolean isExpired(TransactionID transactionID) {
        return false;
    }
}

