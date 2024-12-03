/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.io.Serializable;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.transaction.TransactionException;

public class TransactionIDSerializedForm
implements Serializable {
    private final String cacheManagerName;
    private final String clusterUUID;
    private final String ownerID;
    private final long creationTime;
    private final int id;

    public TransactionIDSerializedForm(String cacheManagerName, String clusterUUID, String ownerID, long creationTime, int id) {
        this.cacheManagerName = cacheManagerName;
        this.clusterUUID = clusterUUID;
        this.ownerID = ownerID;
        this.creationTime = creationTime;
        this.id = id;
    }

    public String getCacheManagerName() {
        return this.cacheManagerName;
    }

    public String getClusterUUID() {
        return this.clusterUUID;
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public int getId() {
        return this.id;
    }

    private Object readResolve() {
        CacheManager cacheManager = CacheManager.getCacheManager(this.cacheManagerName);
        if (cacheManager == null) {
            throw new TransactionException("unable to restore transaction ID from " + this.cacheManagerName);
        }
        return cacheManager.getOrCreateTransactionIDFactory().restoreTransactionID(this);
    }
}

