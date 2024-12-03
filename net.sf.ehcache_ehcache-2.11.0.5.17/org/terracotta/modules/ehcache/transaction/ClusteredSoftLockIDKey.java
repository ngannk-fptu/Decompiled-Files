/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction;

import java.io.Serializable;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.TransactionID;

public class ClusteredSoftLockIDKey
implements Serializable {
    private static final int PRIME = 31;
    private final TransactionID transactionID;
    private final Object key;

    public ClusteredSoftLockIDKey(SoftLockID softLockId) {
        this.transactionID = softLockId.getTransactionID();
        this.key = softLockId.getKey();
    }

    public int hashCode() {
        int hashCode = 31;
        hashCode *= this.transactionID.hashCode();
        return hashCode *= this.key.hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof ClusteredSoftLockIDKey) {
            ClusteredSoftLockIDKey other = (ClusteredSoftLockIDKey)object;
            if (!this.transactionID.equals(other.transactionID)) {
                return false;
            }
            return this.key.equals(other.key);
        }
        return false;
    }

    public String toString() {
        return "Clustered Soft Lock ID [transactionID: " + this.transactionID + ", key: " + this.key + "]";
    }
}

