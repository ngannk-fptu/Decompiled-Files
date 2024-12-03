/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.io.Serializable;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.TransactionID;

public final class SoftLockID
implements Serializable {
    private static final int PRIME = 31;
    private final TransactionID transactionID;
    private final Object key;
    private final Element newElement;
    private final Element oldElement;

    public SoftLockID(TransactionID transactionID, Object key, Element newElement, Element oldElement) {
        this.transactionID = transactionID;
        this.key = key;
        this.newElement = newElement;
        this.oldElement = oldElement;
    }

    public TransactionID getTransactionID() {
        return this.transactionID;
    }

    public Object getKey() {
        return this.key;
    }

    public Element getNewElement() {
        return this.newElement;
    }

    public Element getOldElement() {
        return this.oldElement;
    }

    public int hashCode() {
        int hashCode = 31;
        hashCode *= this.transactionID.hashCode();
        return hashCode *= this.key.hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof SoftLockID) {
            SoftLockID other = (SoftLockID)object;
            if (!this.transactionID.equals(other.transactionID)) {
                return false;
            }
            return this.key.equals(other.key);
        }
        return false;
    }

    public String toString() {
        return "Soft Lock ID [transactionID: " + this.transactionID + ", key: " + this.key + "]";
    }
}

