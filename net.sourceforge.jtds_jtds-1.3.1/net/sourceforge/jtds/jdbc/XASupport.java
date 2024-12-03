/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.JtdsXid;
import net.sourceforge.jtds.util.Logger;

public class XASupport {
    private static final int XA_RMID = 1;
    private static final String TM_ID = "TM=JTDS,RmRecoveryGuid=434CDE1A-F747-4942-9584-04937455CAB4";
    private static final int XA_OPEN = 1;
    private static final int XA_CLOSE = 2;
    private static final int XA_START = 3;
    private static final int XA_END = 4;
    private static final int XA_ROLLBACK = 5;
    private static final int XA_PREPARE = 6;
    private static final int XA_COMMIT = 7;
    private static final int XA_RECOVER = 8;
    private static final int XA_FORGET = 9;
    private static final int XA_COMPLETE = 10;
    private static final int XA_TRACE = 0;

    public static int xa_open(Connection connection) throws SQLException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            Logger.println("xa_open: emulating distributed transaction support");
            if (con.getXid() != null) {
                throw new SQLException(Messages.get("error.xasupport.activetran", "xa_open"), "HY000");
            }
            con.setXaState(1);
            return 0;
        }
        if (((JtdsConnection)connection).getServerType() != 1 || ((JtdsConnection)connection).getTdsVersion() < 4) {
            throw new SQLException(Messages.get("error.xasupport.nodist"), "HY000");
        }
        Logger.println("xa_open: Using SQL2000 MSDTC to support distributed transactions");
        int[] args = new int[5];
        args[1] = 1;
        args[2] = 0;
        args[3] = 1;
        args[4] = 0;
        byte[][] id = ((JtdsConnection)connection).sendXaPacket(args, TM_ID.getBytes());
        if (args[0] != 0 || id == null || id[0] == null || id[0].length != 4) {
            throw new SQLException(Messages.get("error.xasupport.badopen"), "HY000");
        }
        return id[0][0] & 0xFF | (id[0][1] & 0xFF) << 8 | (id[0][2] & 0xFF) << 16 | (id[0][3] & 0xFF) << 24;
    }

    public static void xa_close(Connection connection, int xaConId) throws SQLException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            con.setXaState(0);
            if (con.getXid() != null) {
                con.setXid(null);
                try {
                    con.rollback();
                }
                catch (SQLException e) {
                    Logger.println("xa_close: rollback() returned " + e);
                }
                try {
                    con.setAutoCommit(true);
                }
                catch (SQLException e) {
                    Logger.println("xa_close: setAutoCommit() returned " + e);
                }
                throw new SQLException(Messages.get("error.xasupport.activetran", "xa_close"), "HY000");
            }
            return;
        }
        int[] args = new int[5];
        args[1] = 2;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = 0;
        ((JtdsConnection)connection).sendXaPacket(args, TM_ID.getBytes());
    }

    public static void xa_start(Connection connection, int xaConId, Xid xid, int flags) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid tran;
            JtdsXid lxid = new JtdsXid(xid);
            if (con.getXaState() == 0) {
                XASupport.raiseXAException(-6);
            }
            if ((tran = (JtdsXid)con.getXid()) != null) {
                if (tran.equals(lxid)) {
                    XASupport.raiseXAException(-8);
                } else {
                    XASupport.raiseXAException(-6);
                }
            }
            if (flags != 0) {
                XASupport.raiseXAException(-5);
            }
            try {
                connection.setAutoCommit(false);
            }
            catch (SQLException e) {
                XASupport.raiseXAException(-3);
            }
            con.setXid(lxid);
            con.setXaState(3);
            return;
        }
        int[] args = new int[5];
        args[1] = 3;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = flags;
        try {
            byte[][] cookie = ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
            if (args[0] == 0 && cookie != null) {
                ((JtdsConnection)connection).enlistConnection(cookie[0]);
            }
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0) {
            XASupport.raiseXAException(args[0]);
        }
    }

    public static void xa_end(Connection connection, int xaConId, Xid xid, int flags) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid tran;
            JtdsXid lxid = new JtdsXid(xid);
            if (con.getXaState() != 3) {
                XASupport.raiseXAException(-6);
            }
            if ((tran = (JtdsXid)con.getXid()) == null || !tran.equals(lxid)) {
                XASupport.raiseXAException(-4);
            }
            if (flags != 0x4000000 && flags != 0x20000000) {
                XASupport.raiseXAException(-5);
            }
            con.setXaState(4);
            return;
        }
        int[] args = new int[5];
        args[1] = 4;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = flags;
        try {
            ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
            ((JtdsConnection)connection).enlistConnection(null);
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0) {
            XASupport.raiseXAException(args[0]);
        }
    }

    public static int xa_prepare(Connection connection, int xaConId, Xid xid) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid tran;
            JtdsXid lxid = new JtdsXid(xid);
            if (con.getXaState() != 4) {
                XASupport.raiseXAException(-6);
            }
            if ((tran = (JtdsXid)con.getXid()) == null || !tran.equals(lxid)) {
                XASupport.raiseXAException(-4);
            }
            con.setXaState(6);
            Logger.println("xa_prepare: Warning: Two phase commit not available in XA emulation mode.");
            return 0;
        }
        int[] args = new int[5];
        args[1] = 6;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = 0;
        try {
            ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0 && args[0] != 3) {
            XASupport.raiseXAException(args[0]);
        }
        return args[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void xa_commit(Connection connection, int xaConId, Xid xid, boolean onePhase) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid tran = (JtdsXid)con.getXid();
            if (tran == null) {
                try {
                    connection.setAutoCommit(false);
                }
                catch (SQLException e) {
                    XASupport.raiseXAException(-3);
                }
            } else {
                JtdsXid lxid;
                if (con.getXaState() != 4 && con.getXaState() != 6) {
                    XASupport.raiseXAException(-6);
                }
                if (!tran.equals(lxid = new JtdsXid(xid))) {
                    XASupport.raiseXAException(-4);
                }
            }
            con.setXid(null);
            try {
                con.commit();
            }
            catch (SQLException e) {
                XASupport.raiseXAException(e);
            }
            finally {
                try {
                    con.setAutoCommit(true);
                }
                catch (SQLException e) {
                    Logger.println("xa_close: setAutoCommit() returned " + e);
                }
                con.setXaState(1);
            }
            return;
        }
        int[] args = new int[5];
        args[1] = 7;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = onePhase ? 0x40000000 : 0;
        try {
            ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0) {
            XASupport.raiseXAException(args[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void xa_rollback(Connection connection, int xaConId, Xid xid) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid tran;
            JtdsXid lxid = new JtdsXid(xid);
            if (con.getXaState() != 4 && con.getXaState() != 6) {
                XASupport.raiseXAException(-6);
            }
            if ((tran = (JtdsXid)con.getXid()) == null || !tran.equals(lxid)) {
                XASupport.raiseXAException(-4);
            }
            con.setXid(null);
            try {
                con.rollback();
            }
            catch (SQLException e) {
                XASupport.raiseXAException(e);
            }
            finally {
                try {
                    con.setAutoCommit(true);
                }
                catch (SQLException e) {
                    Logger.println("xa_close: setAutoCommit() returned " + e);
                }
                con.setXaState(1);
            }
            return;
        }
        int[] args = new int[5];
        args[1] = 5;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = 0;
        try {
            ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0) {
            XASupport.raiseXAException(args[0]);
        }
    }

    public static Xid[] xa_recover(Connection connection, int xaConId, int flags) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            if (flags != 0x1000000 && flags != 0x800000 && flags != 0x1800000 && flags != 0) {
                XASupport.raiseXAException(-5);
            }
            return new JtdsXid[0];
        }
        int[] args = new int[5];
        args[1] = 8;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = 0;
        JtdsXid[] list = null;
        if (flags != 0x1000000) {
            return new JtdsXid[0];
        }
        try {
            byte[][] buffer = ((JtdsConnection)connection).sendXaPacket(args, null);
            if (args[0] >= 0) {
                int n = buffer.length;
                list = new JtdsXid[n];
                for (int i = 0; i < n; ++i) {
                    list[i] = new JtdsXid(buffer[i], 0);
                }
            }
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] < 0) {
            XASupport.raiseXAException(args[0]);
        }
        if (list == null) {
            list = new JtdsXid[]{};
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void xa_forget(Connection connection, int xaConId, Xid xid) throws XAException {
        JtdsConnection con = (JtdsConnection)connection;
        if (con.isXaEmulation()) {
            JtdsXid lxid = new JtdsXid(xid);
            JtdsXid tran = (JtdsXid)con.getXid();
            if (tran != null && !tran.equals(lxid)) {
                XASupport.raiseXAException(-4);
            }
            if (con.getXaState() != 4 && con.getXaState() != 6) {
                XASupport.raiseXAException(-6);
            }
            con.setXid(null);
            try {
                con.rollback();
            }
            catch (SQLException e) {
                XASupport.raiseXAException(e);
            }
            finally {
                try {
                    con.setAutoCommit(true);
                }
                catch (SQLException e) {
                    Logger.println("xa_close: setAutoCommit() returned " + e);
                }
                con.setXaState(1);
            }
            return;
        }
        int[] args = new int[5];
        args[1] = 9;
        args[2] = xaConId;
        args[3] = 1;
        args[4] = 0;
        try {
            ((JtdsConnection)connection).sendXaPacket(args, XASupport.toBytesXid(xid));
        }
        catch (SQLException e) {
            XASupport.raiseXAException(e);
        }
        if (args[0] != 0) {
            XASupport.raiseXAException(args[0]);
        }
    }

    public static void raiseXAException(SQLException sqle) throws XAException {
        XAException e = new XAException(sqle.getMessage());
        e.errorCode = -7;
        Logger.println("XAException: " + e.getMessage());
        throw e;
    }

    public static void raiseXAException(int errorCode) throws XAException {
        String err = "xaerunknown";
        switch (errorCode) {
            case 100: {
                err = "xarbrollback";
                break;
            }
            case 101: {
                err = "xarbcommfail";
                break;
            }
            case 102: {
                err = "xarbdeadlock";
                break;
            }
            case 103: {
                err = "xarbintegrity";
                break;
            }
            case 104: {
                err = "xarbother";
                break;
            }
            case 105: {
                err = "xarbproto";
                break;
            }
            case 106: {
                err = "xarbtimeout";
                break;
            }
            case 107: {
                err = "xarbtransient";
                break;
            }
            case 9: {
                err = "xanomigrate";
                break;
            }
            case 8: {
                err = "xaheurhaz";
                break;
            }
            case 7: {
                err = "xaheurcom";
                break;
            }
            case 6: {
                err = "xaheurrb";
                break;
            }
            case 5: {
                err = "xaheurmix";
                break;
            }
            case 4: {
                err = "xaretry";
                break;
            }
            case 3: {
                err = "xardonly";
                break;
            }
            case -2: {
                err = "xaerasync";
                break;
            }
            case -4: {
                err = "xaernota";
                break;
            }
            case -5: {
                err = "xaerinval";
                break;
            }
            case -6: {
                err = "xaerproto";
                break;
            }
            case -3: {
                err = "xaerrmerr";
                break;
            }
            case -7: {
                err = "xaerrmfail";
                break;
            }
            case -8: {
                err = "xaerdupid";
                break;
            }
            case -9: {
                err = "xaeroutside";
            }
        }
        XAException e = new XAException(Messages.get("error.xaexception." + err));
        e.errorCode = errorCode;
        Logger.println("XAException: " + e.getMessage());
        throw e;
    }

    private static byte[] toBytesXid(Xid xid) {
        byte[] buffer = new byte[12 + xid.getGlobalTransactionId().length + xid.getBranchQualifier().length];
        int fmt = xid.getFormatId();
        buffer[0] = (byte)fmt;
        buffer[1] = (byte)(fmt >> 8);
        buffer[2] = (byte)(fmt >> 16);
        buffer[3] = (byte)(fmt >> 24);
        buffer[4] = (byte)xid.getGlobalTransactionId().length;
        buffer[8] = (byte)xid.getBranchQualifier().length;
        System.arraycopy(xid.getGlobalTransactionId(), 0, buffer, 12, buffer[4]);
        System.arraycopy(xid.getBranchQualifier(), 0, buffer, 12 + buffer[4], buffer[8]);
        return buffer;
    }

    private XASupport() {
    }
}

