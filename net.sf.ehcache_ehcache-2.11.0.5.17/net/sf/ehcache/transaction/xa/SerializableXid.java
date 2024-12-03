/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import java.io.Serializable;
import java.util.Arrays;
import javax.transaction.xa.Xid;

public class SerializableXid
implements Xid,
Serializable {
    private final int formatId;
    private final byte[] globalTransactionId;
    private final byte[] branchQualifier;

    public SerializableXid(Xid xid) {
        this.formatId = xid.getFormatId();
        this.globalTransactionId = xid.getGlobalTransactionId();
        this.branchQualifier = xid.getBranchQualifier();
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifier;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTransactionId;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SerializableXid)) {
            return false;
        }
        SerializableXid otherXid = (SerializableXid)obj;
        return this.formatId == otherXid.getFormatId() && Arrays.equals(this.globalTransactionId, otherXid.getGlobalTransactionId()) && Arrays.equals(this.branchQualifier, otherXid.branchQualifier);
    }

    public int hashCode() {
        int hashCode = this.formatId;
        if (this.globalTransactionId != null) {
            hashCode += Arrays.hashCode(this.globalTransactionId);
        }
        if (this.branchQualifier != null) {
            hashCode += Arrays.hashCode(this.branchQualifier);
        }
        return hashCode;
    }

    public String toString() {
        return "SerializableXid{formatId=" + this.formatId + ", globalTxId=" + Arrays.toString(this.globalTransactionId) + ", branchQualifier=" + Arrays.toString(this.branchQualifier) + "}";
    }
}

