/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction.xa;

import javax.transaction.xa.Xid;
import net.sf.ehcache.transaction.XidTransactionIDSerializedForm;
import net.sf.ehcache.transaction.xa.XidTransactionID;
import org.terracotta.modules.ehcache.transaction.ClusteredID;
import org.terracotta.modules.ehcache.transaction.xa.XidClustered;

public class ClusteredXidTransactionID
implements XidTransactionID,
ClusteredID {
    private final Xid xid;
    private final String ownerID;
    private final String cacheName;
    private final String cacheManagerName;

    public ClusteredXidTransactionID(XidTransactionIDSerializedForm serializedForm) {
        this.xid = new XidClustered(serializedForm.getXid());
        this.ownerID = serializedForm.getOwnerID();
        this.cacheManagerName = serializedForm.getCacheManagerName();
        this.cacheName = serializedForm.getCacheName();
    }

    public ClusteredXidTransactionID(Xid xid, String cacheManagerName, String cacheName, String ownerID) {
        this.cacheManagerName = cacheManagerName;
        this.cacheName = cacheName;
        this.ownerID = ownerID;
        this.xid = new XidClustered(xid);
    }

    @Override
    public Xid getXid() {
        return this.xid;
    }

    @Override
    public String getCacheName() {
        return this.cacheName;
    }

    @Override
    public String getOwnerID() {
        return this.ownerID;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof ClusteredXidTransactionID) {
            ClusteredXidTransactionID otherId = (ClusteredXidTransactionID)obj;
            return this.xid.equals(otherId.xid);
        }
        return false;
    }

    public final int hashCode() {
        return this.xid.hashCode();
    }

    public String toString() {
        return "Clustered [" + this.xid + "]";
    }

    private Object writeReplace() {
        return new XidTransactionIDSerializedForm(this.cacheManagerName, this.cacheName, this.ownerID, this.xid);
    }
}

