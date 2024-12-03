/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction;

import java.util.concurrent.atomic.AtomicInteger;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDSerializedForm;
import org.terracotta.modules.ehcache.transaction.ClusteredID;

public class ClusteredTransactionID
implements TransactionID,
ClusteredID {
    private static final AtomicInteger idGenerator = new AtomicInteger();
    private final String clusterUUID;
    private final String ownerID;
    private final String cacheManagerName;
    private final long creationTime;
    private final int id;

    public ClusteredTransactionID(String ownerId, String clusterUUID, String cacheManagerName) {
        this(ownerId, clusterUUID, cacheManagerName, System.currentTimeMillis(), idGenerator.getAndIncrement());
    }

    public ClusteredTransactionID(TransactionIDSerializedForm serializedForm) {
        this(serializedForm.getOwnerID(), serializedForm.getClusterUUID(), serializedForm.getCacheManagerName(), serializedForm.getCreationTime(), serializedForm.getId());
    }

    public ClusteredTransactionID(String ownerId, String clusterUUID, String cacheManagerName, long creationTime, int id) {
        this.clusterUUID = clusterUUID;
        this.ownerID = ownerId;
        this.cacheManagerName = cacheManagerName;
        this.creationTime = creationTime;
        this.id = id;
    }

    @Override
    public String getOwnerID() {
        return this.ownerID;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof ClusteredTransactionID) {
            ClusteredTransactionID otherId = (ClusteredTransactionID)obj;
            return this.id == otherId.id && this.clusterUUID.equals(otherId.clusterUUID) && this.creationTime == otherId.creationTime;
        }
        return false;
    }

    public final int hashCode() {
        return (this.id + (int)this.creationTime) * 31 ^ this.clusterUUID.hashCode();
    }

    public String toString() {
        return this.id + ":" + this.ownerID + ":" + this.creationTime + "@" + this.clusterUUID;
    }

    private Object writeReplace() {
        return new TransactionIDSerializedForm(this.cacheManagerName, this.clusterUUID, this.ownerID, this.creationTime, this.id);
    }
}

