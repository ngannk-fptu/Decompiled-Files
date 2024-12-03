/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction.xa;

import java.io.Serializable;
import java.util.Arrays;
import javax.transaction.xa.Xid;

public final class XidClustered
implements Xid,
Serializable {
    private final int formatId;
    private final byte[] globalTxId;
    private final byte[] branchQualifier;

    public XidClustered(Xid xid) {
        this.formatId = xid.getFormatId();
        this.globalTxId = xid.getGlobalTransactionId();
        this.branchQualifier = xid.getBranchQualifier();
    }

    public XidClustered(int formatId, byte[] globalTxId, byte[] branchQualifier) {
        this.formatId = formatId;
        this.globalTxId = globalTxId;
        this.branchQualifier = branchQualifier;
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTxId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XidClustered that = (XidClustered)o;
        return this.formatId == that.formatId && Arrays.equals(this.branchQualifier, that.branchQualifier) && Arrays.equals(this.globalTxId, that.globalTxId);
    }

    public int hashCode() {
        int result = this.formatId;
        result = 31 * result + (this.globalTxId != null ? Arrays.hashCode(this.globalTxId) : 0);
        result = 31 * result + (this.branchQualifier != null ? Arrays.hashCode(this.branchQualifier) : 0);
        return result;
    }

    public String toString() {
        return "XidClustered{formatId=" + this.formatId + ", globalTxId=" + Arrays.toString(this.globalTxId) + ", branchQualifier=" + Arrays.toString(this.branchQualifier) + "}";
    }
}

