/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public interface XAResource {
    public static final int TMENDRSCAN = 0x800000;
    public static final int TMFAIL = 0x20000000;
    public static final int TMJOIN = 0x200000;
    public static final int TMNOFLAGS = 0;
    public static final int TMONEPHASE = 0x40000000;
    public static final int TMRESUME = 0x8000000;
    public static final int TMSTARTRSCAN = 0x1000000;
    public static final int TMSUCCESS = 0x4000000;
    public static final int TMSUSPEND = 0x2000000;
    public static final int XA_RDONLY = 3;
    public static final int XA_OK = 0;

    public void commit(Xid var1, boolean var2) throws XAException;

    public void end(Xid var1, int var2) throws XAException;

    public void forget(Xid var1) throws XAException;

    public int getTransactionTimeout() throws XAException;

    public boolean isSameRM(XAResource var1) throws XAException;

    public int prepare(Xid var1) throws XAException;

    public Xid[] recover(int var1) throws XAException;

    public void rollback(Xid var1) throws XAException;

    public boolean setTransactionTimeout(int var1) throws XAException;

    public void start(Xid var1, int var2) throws XAException;
}

