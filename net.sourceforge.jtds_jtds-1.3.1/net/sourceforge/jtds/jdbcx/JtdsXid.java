/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.Support;

public class JtdsXid
implements Xid {
    public static final int XID_SIZE = 140;
    private final byte[] gtran;
    private final byte[] bqual;
    public final int fmtId;
    public int hash;

    public JtdsXid(byte[] buf, int pos) {
        this.fmtId = buf[pos] & 0xFF | (buf[pos + 1] & 0xFF) << 8 | (buf[pos + 2] & 0xFF) << 16 | (buf[pos + 3] & 0xFF) << 24;
        byte t = buf[pos + 4];
        byte b = buf[pos + 8];
        this.gtran = new byte[t];
        this.bqual = new byte[b];
        System.arraycopy(buf, 12 + pos, this.gtran, 0, t);
        System.arraycopy(buf, 12 + t + pos, this.bqual, 0, b);
        this.calculateHash();
    }

    public JtdsXid(byte[] global, byte[] branch) {
        this.fmtId = 0;
        this.gtran = global;
        this.bqual = branch;
        this.calculateHash();
    }

    public JtdsXid(Xid xid) {
        this.fmtId = xid.getFormatId();
        this.gtran = new byte[xid.getGlobalTransactionId().length];
        System.arraycopy(xid.getGlobalTransactionId(), 0, this.gtran, 0, this.gtran.length);
        this.bqual = new byte[xid.getBranchQualifier().length];
        System.arraycopy(xid.getBranchQualifier(), 0, this.bqual, 0, this.bqual.length);
        this.calculateHash();
    }

    private void calculateHash() {
        String x = Integer.toString(this.fmtId) + new String(this.gtran) + new String(this.bqual);
        this.hash = x.hashCode();
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JtdsXid) {
            JtdsXid xobj = (JtdsXid)obj;
            if (this.gtran.length + this.bqual.length == xobj.gtran.length + xobj.bqual.length && this.fmtId == xobj.fmtId) {
                int i;
                for (i = 0; i < this.gtran.length; ++i) {
                    if (this.gtran[i] == xobj.gtran[i]) continue;
                    return false;
                }
                for (i = 0; i < this.bqual.length; ++i) {
                    if (this.bqual[i] == xobj.bqual[i]) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getFormatId() {
        return this.fmtId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.gtran;
    }

    public String toString() {
        StringBuilder txt = new StringBuilder(256);
        txt.append("XID[Format=").append(this.fmtId).append(", Global=0x");
        txt.append(Support.toHex(this.gtran)).append(", Branch=0x");
        txt.append(Support.toHex(this.bqual)).append(']');
        return txt.toString();
    }
}

