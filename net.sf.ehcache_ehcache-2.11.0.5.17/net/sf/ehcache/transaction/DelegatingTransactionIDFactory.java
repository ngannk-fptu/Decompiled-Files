/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.transaction.xa.Xid;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.FeaturesManager;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.TerracottaClient;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.TransactionIDFactoryImpl;
import net.sf.ehcache.transaction.TransactionIDSerializedForm;
import net.sf.ehcache.transaction.XidTransactionIDSerializedForm;
import net.sf.ehcache.transaction.xa.XidTransactionID;

public class DelegatingTransactionIDFactory
implements TransactionIDFactory {
    private final FeaturesManager featuresManager;
    private final TerracottaClient terracottaClient;
    private final String cacheManagerName;
    private volatile ClusteredInstanceFactory clusteredInstanceFactory;
    private volatile AtomicReference<TransactionIDFactory> transactionIDFactory = new AtomicReference();

    public DelegatingTransactionIDFactory(FeaturesManager featuresManager, TerracottaClient terracottaClient, String cacheManagerName) {
        this.featuresManager = featuresManager;
        this.terracottaClient = terracottaClient;
        this.cacheManagerName = cacheManagerName;
    }

    private TransactionIDFactory get() {
        ClusteredInstanceFactory cif = this.terracottaClient.getClusteredInstanceFactory();
        if (cif != null && cif != this.clusteredInstanceFactory) {
            this.transactionIDFactory.set(cif.createTransactionIDFactory(UUID.randomUUID().toString(), this.cacheManagerName));
            this.clusteredInstanceFactory = cif;
        }
        if (this.transactionIDFactory.get() == null) {
            TransactionIDFactory constructed = this.featuresManager == null ? new TransactionIDFactoryImpl() : this.featuresManager.createTransactionIDFactory();
            if (this.transactionIDFactory.compareAndSet(null, constructed)) {
                return constructed;
            }
            return this.transactionIDFactory.get();
        }
        return this.transactionIDFactory.get();
    }

    @Override
    public TransactionID createTransactionID() {
        return this.get().createTransactionID();
    }

    @Override
    public TransactionID restoreTransactionID(TransactionIDSerializedForm serializedForm) {
        return this.get().restoreTransactionID(serializedForm);
    }

    @Override
    public XidTransactionID createXidTransactionID(Xid xid, Ehcache cache) {
        return this.get().createXidTransactionID(xid, cache);
    }

    @Override
    public XidTransactionID restoreXidTransactionID(XidTransactionIDSerializedForm serializedForm) {
        return this.get().restoreXidTransactionID(serializedForm);
    }

    @Override
    public void markForCommit(TransactionID transactionID) {
        this.get().markForCommit(transactionID);
    }

    @Override
    public void markForRollback(XidTransactionID transactionID) {
        this.get().markForRollback(transactionID);
    }

    @Override
    public boolean isDecisionCommit(TransactionID transactionID) {
        return this.get().isDecisionCommit(transactionID);
    }

    @Override
    public void clear(TransactionID transactionID) {
        this.get().clear(transactionID);
    }

    @Override
    public Set<XidTransactionID> getAllXidTransactionIDsFor(Ehcache cache) {
        return this.get().getAllXidTransactionIDsFor(cache);
    }

    @Override
    public Set<TransactionID> getAllTransactionIDs() {
        return this.get().getAllTransactionIDs();
    }

    @Override
    public boolean isExpired(TransactionID transactionID) {
        return this.get().isExpired(transactionID);
    }

    @Override
    public Boolean isPersistent() {
        if (this.transactionIDFactory.get() == null) {
            return null;
        }
        return this.get().isPersistent();
    }
}

