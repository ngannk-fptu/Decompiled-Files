/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import javax.transaction.xa.Xid;
import net.sf.ehcache.transaction.xa.SerializableXid;
import net.sf.ehcache.transaction.xa.XidTransactionID;

public class XidTransactionIDImpl
implements XidTransactionID {
    private final SerializableXid xid;
    private final String cacheName;

    public XidTransactionIDImpl(Xid xid, String cacheName) {
        this.xid = new SerializableXid(xid);
        this.cacheName = cacheName;
    }

    @Override
    public Xid getXid() {
        return this.xid;
    }

    @Override
    public String getCacheName() {
        return this.cacheName;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof XidTransactionIDImpl) {
            XidTransactionIDImpl otherId = (XidTransactionIDImpl)obj;
            return this.xid.equals(otherId.xid);
        }
        return false;
    }

    public final int hashCode() {
        return this.xid.hashCode();
    }

    public String toString() {
        return "Unclustered " + this.xid;
    }
}

