/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Util;
import javax.transaction.xa.Xid;

final class XidImpl
implements Xid {
    private final int formatId;
    private final byte[] gtrid;
    private final byte[] bqual;
    private final String traceID;

    public XidImpl(int formatId, byte[] gtrid, byte[] bqual) {
        this.formatId = formatId;
        this.gtrid = gtrid;
        this.bqual = bqual;
        this.traceID = " XID:" + XidImpl.xidDisplay(this);
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.gtrid;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    public String toString() {
        return this.traceID;
    }

    static String xidDisplay(Xid xid) {
        if (null == xid) {
            return "(null)";
        }
        StringBuilder sb = new StringBuilder(300);
        sb.append("formatId=");
        sb.append(xid.getFormatId());
        sb.append(" gtrid=");
        sb.append(Util.byteToHexDisplayString(xid.getGlobalTransactionId()));
        sb.append(" bqual=");
        sb.append(Util.byteToHexDisplayString(xid.getBranchQualifier()));
        return sb.toString();
    }
}

