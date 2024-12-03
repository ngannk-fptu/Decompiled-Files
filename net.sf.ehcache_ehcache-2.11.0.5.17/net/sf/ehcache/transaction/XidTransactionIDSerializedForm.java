/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.io.Serializable;
import javax.transaction.xa.Xid;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.transaction.TransactionException;
import net.sf.ehcache.transaction.xa.SerializableXid;

public class XidTransactionIDSerializedForm
implements Serializable {
    private final String cacheManagerName;
    private final String cacheName;
    private final String ownerID;
    private final Xid xid;

    public XidTransactionIDSerializedForm(String cacheManagerName, String cacheName, String ownerID, Xid xid) {
        this.cacheManagerName = cacheManagerName;
        this.cacheName = cacheName;
        this.ownerID = ownerID;
        this.xid = new SerializableXid(xid);
    }

    public String getCacheManagerName() {
        return this.cacheManagerName;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public Xid getXid() {
        return this.xid;
    }

    private Object readResolve() {
        CacheManager cacheManager = XidTransactionIDSerializedForm.getCacheManager(this.cacheManagerName);
        if (cacheManager == null) {
            throw new TransactionException("unable to restore XID transaction ID from " + this.cacheManagerName);
        }
        return cacheManager.getOrCreateTransactionIDFactory().restoreXidTransactionID(this);
    }

    private static CacheManager getCacheManager(String cacheManagerName) {
        for (CacheManager cacheManager : CacheManager.ALL_CACHE_MANAGERS) {
            if (!cacheManager.getName().equals(cacheManagerName)) continue;
            return cacheManager;
        }
        return null;
    }
}

