/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.local;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.local.LocalTransactionStore;

public class LocalRecoveryManager {
    private final TransactionIDFactory transactionIdFactory;
    private final List<LocalTransactionStore> localTransactionStores = new CopyOnWriteArrayList<LocalTransactionStore>();
    private volatile Set<TransactionID> previouslyRecoveredTransactionIDs = Collections.emptySet();

    public LocalRecoveryManager(TransactionIDFactory transactionIdFactory) {
        this.transactionIdFactory = transactionIdFactory;
    }

    void register(LocalTransactionStore localTransactionStore) {
        this.localTransactionStores.add(localTransactionStore);
    }

    void unregister(LocalTransactionStore localTransactionStore) {
        this.localTransactionStores.remove(localTransactionStore);
    }

    public Set<TransactionID> recover() {
        HashSet<TransactionID> recovered = new HashSet<TransactionID>();
        for (LocalTransactionStore localTransactionStore : this.localTransactionStores) {
            recovered.addAll(localTransactionStore.recover());
        }
        for (TransactionID transactionId : recovered) {
            this.transactionIdFactory.clear(transactionId);
        }
        this.previouslyRecoveredTransactionIDs = recovered;
        return recovered;
    }

    public Set<TransactionID> getPreviouslyRecoveredTransactionIDs() {
        return this.previouslyRecoveredTransactionIDs;
    }
}

