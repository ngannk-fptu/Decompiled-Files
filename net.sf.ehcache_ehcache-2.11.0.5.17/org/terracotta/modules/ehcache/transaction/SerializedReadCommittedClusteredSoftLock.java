/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction;

import java.io.Serializable;
import net.sf.ehcache.transaction.TransactionID;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLock;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLockFactory;

public class SerializedReadCommittedClusteredSoftLock
implements Serializable {
    private static final long serialVersionUID = -766870846218858666L;
    private final TransactionID transactionID;
    private final Object deserializedKey;
    private volatile transient ReadCommittedClusteredSoftLock softLock;

    public SerializedReadCommittedClusteredSoftLock(TransactionID transactionID, Object deserializedKey) {
        this.transactionID = transactionID;
        this.deserializedKey = deserializedKey;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ReadCommittedClusteredSoftLock getSoftLock(ToolkitInstanceFactory toolkitInstanceFactory, ReadCommittedClusteredSoftLockFactory factory) {
        ReadCommittedClusteredSoftLock rv = this.softLock;
        if (rv != null) {
            return rv;
        }
        SerializedReadCommittedClusteredSoftLock serializedReadCommittedClusteredSoftLock = this;
        synchronized (serializedReadCommittedClusteredSoftLock) {
            rv = this.softLock = new ReadCommittedClusteredSoftLock(toolkitInstanceFactory, factory, this.transactionID, this.deserializedKey);
        }
        return rv;
    }

    public boolean equals(Object object) {
        if (object instanceof SerializedReadCommittedClusteredSoftLock) {
            SerializedReadCommittedClusteredSoftLock other = (SerializedReadCommittedClusteredSoftLock)object;
            if (!this.transactionID.equals(other.transactionID)) {
                return false;
            }
            return this.deserializedKey.equals(other.deserializedKey);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = 31;
        hashCode *= this.transactionID.hashCode();
        return hashCode *= this.deserializedKey.hashCode();
    }
}

