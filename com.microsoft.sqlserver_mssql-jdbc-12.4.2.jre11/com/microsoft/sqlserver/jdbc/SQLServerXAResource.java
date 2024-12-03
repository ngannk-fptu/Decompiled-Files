/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverIntProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.Util;
import com.microsoft.sqlserver.jdbc.XAReturnValue;
import com.microsoft.sqlserver.jdbc.XidImpl;
import java.net.SocketException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public final class SQLServerXAResource
implements XAResource {
    private int timeoutSeconds;
    static final int XA_START = 0;
    static final int XA_END = 1;
    static final int XA_PREPARE = 2;
    static final int XA_COMMIT = 3;
    static final int XA_ROLLBACK = 4;
    static final int XA_FORGET = 5;
    static final int XA_RECOVER = 6;
    static final int XA_PREPARE_EX = 7;
    static final int XA_ROLLBACK_EX = 8;
    static final int XA_FORGET_EX = 9;
    static final int XA_INIT = 10;
    private SQLServerConnection controlConnection;
    private SQLServerConnection con;
    private boolean serverInfoRetrieved;
    private String version;
    private String instanceName;
    private int architectureMSSQL;
    private int architectureOS;
    private static boolean xaInitDone;
    private static final Lock xaInitLock;
    private String sResourceManagerId;
    private int enlistedTransactionCount;
    private final Logger xaLogger;
    private static final AtomicInteger baseResourceID;
    private int tightlyCoupled = 0;
    private int isTransacrionTimeoutSet = 0;
    public static final int SSTRANSTIGHTLYCPLD = 32768;
    private SQLServerCallableStatement[] xaStatements = new SQLServerCallableStatement[]{null, null, null, null, null, null, null, null, null, null};
    private final String traceID;
    private int recoveryAttempt = 0;
    private final Lock lock = new ReentrantLock();

    public String toString() {
        return this.traceID;
    }

    SQLServerXAResource(SQLServerConnection original, SQLServerConnection control, String loginfo) {
        this.traceID = " XAResourceID:" + SQLServerXAResource.nextResourceID();
        this.xaLogger = SQLServerXADataSource.xaLogger;
        this.controlConnection = control;
        this.con = original;
        Properties p = original.activeConnectionProperties;
        this.sResourceManagerId = p == null ? "" : p.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString()) + "." + p.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString()) + "." + p.getProperty(SQLServerDriverIntProperty.PORT_NUMBER.toString());
        if (this.xaLogger.isLoggable(Level.FINE)) {
            this.xaLogger.fine(this.toString() + " created by (" + loginfo + ")");
        }
        this.serverInfoRetrieved = false;
        this.version = "0";
        this.instanceName = "";
        this.architectureMSSQL = 0;
        this.architectureOS = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SQLServerCallableStatement getXACallableStatementHandle(int number) throws SQLServerException {
        this.lock.lock();
        try {
            assert (number >= 0 && number <= 9);
            assert (number < this.xaStatements.length);
            if (null != this.xaStatements[number]) {
                SQLServerCallableStatement sQLServerCallableStatement = this.xaStatements[number];
                return sQLServerCallableStatement;
            }
            CallableStatement cs = null;
            switch (number) {
                case 0: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_start(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                    break;
                }
                case 1: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_end(?, ?, ?, ?, ?, ?, ?)}");
                    break;
                }
                case 2: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_prepare(?, ?, ?, ?, ?)}");
                    break;
                }
                case 3: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_commit(?, ?, ?, ?, ?, ?)}");
                    break;
                }
                case 4: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_rollback(?, ?, ?, ?, ?)}");
                    break;
                }
                case 5: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_forget(?, ?, ?, ?, ?)}");
                    break;
                }
                case 6: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_recover(?, ?, ?, ?)}");
                    break;
                }
                case 7: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_prepare_ex(?, ?, ?, ?, ?, ?)}");
                    break;
                }
                case 8: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_rollback_ex(?, ?, ?, ?, ?, ?)}");
                    break;
                }
                case 9: {
                    cs = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_forget_ex(?, ?, ?, ?, ?, ?)}");
                    break;
                }
                default: {
                    assert (false) : "Bad handle request:" + number;
                    break;
                }
            }
            this.xaStatements[number] = (SQLServerCallableStatement)cs;
            SQLServerCallableStatement sQLServerCallableStatement = this.xaStatements[number];
            return sQLServerCallableStatement;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void closeXAStatements() throws SQLServerException {
        this.lock.lock();
        try {
            for (int i = 0; i < this.xaStatements.length; ++i) {
                if (null == this.xaStatements[i]) continue;
                this.xaStatements[i].close();
                this.xaStatements[i] = null;
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    final void close() throws SQLServerException {
        this.lock.lock();
        try {
            block6: {
                try {
                    this.closeXAStatements();
                }
                catch (Exception e) {
                    if (!this.xaLogger.isLoggable(Level.WARNING)) break block6;
                    this.xaLogger.warning(this.toString() + "Closing exception ignored: " + e);
                }
            }
            if (null != this.controlConnection) {
                this.controlConnection.close();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private String flagsDisplay(int flags) {
        if (0 == flags) {
            return "TMNOFLAGS";
        }
        StringBuilder sb = new StringBuilder(100);
        if (0 != (0x800000 & flags)) {
            sb.append("TMENDRSCAN");
        }
        if (0 != (0x20000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMFAIL");
        }
        if (0 != (0x200000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMJOIN");
        }
        if (0 != (0x40000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMONEPHASE");
        }
        if (0 != (0x8000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMRESUME");
        }
        if (0 != (0x1000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSTARTRSCAN");
        }
        if (0 != (0x4000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSUCCESS");
        }
        if (0 != (0x2000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSUSPEND");
        }
        if (0 != (0x8000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("SSTRANSTIGHTLYCPLD");
        }
        return sb.toString();
    }

    private String cookieDisplay(byte[] cookie) {
        return Util.byteToHexDisplayString(cookie);
    }

    private String typeDisplay(int type) {
        switch (type) {
            case 0: {
                return "XA_START";
            }
            case 1: {
                return "XA_END";
            }
            case 2: {
                return "XA_PREPARE";
            }
            case 3: {
                return "XA_COMMIT";
            }
            case 4: {
                return "XA_ROLLBACK";
            }
            case 5: {
                return "XA_FORGET";
            }
            case 6: {
                return "XA_RECOVER";
            }
        }
        return "UNKNOWN" + type;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private XAReturnValue dtc_XA_interface(int nType, Xid xid, int xaFlags) throws XAException {
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " Calling XA function for type:" + this.typeDisplay(nType) + " flags:" + this.flagsDisplay(xaFlags) + " xid:" + XidImpl.xidDisplay(xid));
        }
        formatId = 0;
        gid = null;
        bid = null;
        if (xid != null) {
            formatId = xid.getFormatId();
            gid = xid.getGlobalTransactionId();
            bid = xid.getBranchQualifier();
        }
        sContext = "DTC_XA_";
        n = 1;
        nStatus = 0;
        returnStatus = new XAReturnValue();
        cs = null;
        try {
            block89: {
                this.lock.lock();
                try {
                    if (SQLServerXAResource.xaInitDone) break block89;
                    try {
                        SQLServerXAResource.xaInitLock.lock();
                        try {
                            initCS = null;
                            initCS = (SQLServerCallableStatement)this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_init_ex(?, ?,?)}");
                            initCS.registerOutParameter(1, 4);
                            initCS.registerOutParameter(2, 1);
                            initCS.registerOutParameter(3, 1);
                            try {
                                initCS.execute();
                            }
                            catch (SQLServerException eX) {
                                block90: {
                                    try {
                                        initCS.close();
                                        this.controlConnection.close();
                                    }
                                    catch (SQLException e3) {
                                        if (!this.xaLogger.isLoggable(Level.FINER)) break block90;
                                        this.xaLogger.finer(this.toString() + " Ignoring exception when closing failed execution. exception:" + e3);
                                    }
                                }
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + eX);
                                }
                                throw eX;
                            }
                            catch (SQLTimeoutException e4) {
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + e4);
                                }
                                throw new SQLServerException(e4.getMessage(), SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, null);
                            }
                            initStatus = initCS.getInt(1);
                            initErr = initCS.getString(2);
                            versionNumberXADLL = initCS.getString(3);
                            if (this.xaLogger.isLoggable(Level.FINE)) {
                                this.xaLogger.fine(this.toString() + " Server XA DLL version:" + versionNumberXADLL);
                            }
                            initCS.close();
                            if (0 != initStatus) {
                                if (!(SQLServerXAResource.$assertionsDisabled || null != initErr && initErr.length() > 1)) {
                                    throw new AssertionError();
                                }
                                this.controlConnection.close();
                                form = new MessageFormat(SQLServerException.getErrString("R_failedToInitializeXA"));
                                msgArgs = new Object[]{String.valueOf(initStatus), initErr};
                                xex = new XAException(form.format(msgArgs));
                                xex.errorCode = initStatus;
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + xex);
                                }
                                throw xex;
                            }
                        }
                        finally {
                            SQLServerXAResource.xaInitLock.unlock();
                        }
                    }
                    catch (SQLServerException e1) {
                        form = new MessageFormat(SQLServerException.getErrString("R_failedToCreateXAConnection"));
                        msgArgs = new Object[]{e1.getMessage()};
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " exception:" + form.format(msgArgs));
                        }
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                    }
                    SQLServerXAResource.xaInitDone = true;
                }
                finally {
                    this.lock.unlock();
                }
            }
            switch (nType) {
                case 0: {
                    if (this.serverInfoRetrieved) ** GOTO lbl120
                    query = "select convert(varchar(100), SERVERPROPERTY('Edition'))as edition,  convert(varchar(100), SERVERPROPERTY('InstanceName'))as instance, convert(varchar(100), SERVERPROPERTY('ProductVersion')) as version, @@VERSION;";
                    try {
                        stmt = this.controlConnection.createStatement();
                        try {
                            rs = stmt.executeQuery(query);
                            try {
                                this.serverInfoRetrieved = true;
                                rs.next();
                                edition = rs.getString(1);
                                this.architectureMSSQL = null != edition && edition.contains("(64-bit)") != false ? 64 : 32;
                                this.instanceName = rs.getString(2) == null ? "MSSQLSERVER" : rs.getString(2);
                                this.version = rs.getString(3);
                                if (null == this.version) {
                                    this.version = "0";
                                } else if (-1 != this.version.indexOf(46)) {
                                    this.version = this.version.substring(0, this.version.indexOf(46));
                                }
                                buildInfo = rs.getString(4);
                                if (null != buildInfo && (buildInfo.contains("Linux") || buildInfo.contains("Microsoft SQL Azure"))) {
                                    this.architectureOS = 64;
                                } else if (null != buildInfo) {
                                    this.architectureOS = Integer.parseInt(buildInfo.substring(buildInfo.lastIndexOf(60) + 2, buildInfo.lastIndexOf(62)));
                                }
                            }
                            finally {
                                if (rs != null) {
                                    rs.close();
                                }
                            }
                        }
                        finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                    }
                    catch (Exception e) {
                        if (!this.xaLogger.isLoggable(Level.WARNING)) ** GOTO lbl120
                        this.xaLogger.warning(this.toString() + " Cannot retrieve server information: :" + e.getMessage());
                    }
lbl120:
                    // 5 sources

                    sContext = "START:";
                    cs = this.getXACallableStatementHandle(0);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.registerOutParameter(n++, -2);
                    cs.setInt(n++, this.timeoutSeconds);
                    cs.setInt(n++, formatId);
                    cs.registerOutParameter(n++, 1);
                    cs.setInt(n++, Integer.parseInt(this.version));
                    cs.setInt(n++, this.instanceName.length());
                    cs.setBytes(n++, this.instanceName.getBytes());
                    cs.setInt(n++, this.architectureMSSQL);
                    cs.setInt(n++, this.architectureOS);
                    cs.setInt(n++, this.isTransacrionTimeoutSet);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                case 1: {
                    sContext = "END:";
                    cs = this.getXACallableStatementHandle(1);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.setInt(n++, formatId);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                case 2: {
                    sContext = "PREPARE:";
                    cs = (32768 & xaFlags) == 32768 ? this.getXACallableStatementHandle(7) : this.getXACallableStatementHandle(2);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((32768 & xaFlags) == 32768) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 3: {
                    sContext = "COMMIT:";
                    cs = this.getXACallableStatementHandle(3);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.setInt(n++, formatId);
                    break;
                }
                case 4: {
                    sContext = "ROLLBACK:";
                    cs = (32768 & xaFlags) == 32768 ? this.getXACallableStatementHandle(8) : this.getXACallableStatementHandle(4);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((32768 & xaFlags) == 32768) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 5: {
                    sContext = "FORGET:";
                    cs = (32768 & xaFlags) == 32768 ? this.getXACallableStatementHandle(9) : this.getXACallableStatementHandle(5);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((32768 & xaFlags) == 32768) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 6: {
                    sContext = "RECOVER:";
                    cs = this.getXACallableStatementHandle(6);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setInt(n++, xaFlags);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                default: {
                    if (!SQLServerXAResource.$assertionsDisabled) {
                        throw new AssertionError((Object)("Unknown execution type:" + nType));
                    }
                    break;
                }
            }
            cs.execute();
            nStatus = cs.getInt(1);
            sErr = cs.getString(2);
            if (nType == 0) {
                versionNumberXADLL = cs.getString(9);
                if (this.xaLogger.isLoggable(Level.FINE)) {
                    this.xaLogger.fine(this.toString() + " Server XA DLL version:" + versionNumberXADLL);
                    if (null != cs.getString(16)) {
                        strBuf = new StringBuffer(cs.getString(16));
                        strBuf.insert(20, '-');
                        strBuf.insert(16, '-');
                        strBuf.insert(12, '-');
                        strBuf.insert(8, '-');
                        this.xaLogger.fine(this.toString() + " XID to UoW mapping for XA type:XA_START XID: " + XidImpl.xidDisplay(xid) + " UoW: " + strBuf.toString());
                    }
                }
            }
            if (nType == 1 && this.xaLogger.isLoggable(Level.FINE) && null != cs.getString(7)) {
                strBuf = new StringBuffer(cs.getString(7));
                strBuf.insert(20, '-');
                strBuf.insert(16, '-');
                strBuf.insert(12, '-');
                strBuf.insert(8, '-');
                this.xaLogger.fine(this.toString() + " XID to UoW mapping for XA type:XA_END XID: " + XidImpl.xidDisplay(xid) + " UoW: " + strBuf.toString());
            }
            if (6 == nType && 0 != nStatus && this.recoveryAttempt < 1) {
                ++this.recoveryAttempt;
                this.dtc_XA_interface(0, xid, 0);
                return this.dtc_XA_interface(6, xid, xaFlags);
            }
            if (3 == nStatus && 1 != nType && 2 != nType || 0 != nStatus && 3 != nStatus) {
                block92: {
                    if (!(SQLServerXAResource.$assertionsDisabled || null != sErr && sErr.length() > 1)) {
                        throw new AssertionError();
                    }
                    form = new MessageFormat(SQLServerException.getErrString("R_failedFunctionXA"));
                    msgArgs = new Object[]{sContext, String.valueOf(nStatus), sErr};
                    e = new XAException(form.format(msgArgs));
                    e.errorCode = nStatus;
                    if (nType == 1 && -7 == nStatus) {
                        try {
                            if (this.xaLogger.isLoggable(Level.FINER)) {
                                this.xaLogger.finer(this.toString() + " Begin un-enlist, enlisted count:" + this.enlistedTransactionCount);
                            }
                            this.con.jtaUnenlistConnection();
                            --this.enlistedTransactionCount;
                            if (this.xaLogger.isLoggable(Level.FINER)) {
                                this.xaLogger.finer(this.toString() + " End un-enlist, enlisted count:" + this.enlistedTransactionCount);
                            }
                        }
                        catch (SQLServerException e1) {
                            if (!this.xaLogger.isLoggable(Level.FINER)) break block92;
                            this.xaLogger.finer(this.toString() + " Ignoring exception:" + e1);
                        }
                    }
                }
                throw e;
            }
            if (nType == 0) {
                transactionCookie = cs.getBytes(6);
                if (transactionCookie == null) {
                    form = new MessageFormat(SQLServerException.getErrString("R_noTransactionCookie"));
                    msgArgs = new Object[]{sContext};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                } else {
                    try {
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " Begin enlisting, cookie:" + this.cookieDisplay(transactionCookie) + " enlisted count:" + this.enlistedTransactionCount);
                        }
                        this.con.jtaEnlistConnection(transactionCookie);
                        ++this.enlistedTransactionCount;
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " End enlisting, cookie:" + this.cookieDisplay(transactionCookie) + " enlisted count:" + this.enlistedTransactionCount);
                        }
                    }
                    catch (SQLServerException e1) {
                        form = new MessageFormat(SQLServerException.getErrString("R_failedToEnlist"));
                        msgArgs = new Object[]{e1.getMessage()};
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                    }
                }
            }
            if (nType == 1) {
                try {
                    if (this.xaLogger.isLoggable(Level.FINER)) {
                        this.xaLogger.finer(this.toString() + " Begin un-enlist, enlisted count:" + this.enlistedTransactionCount);
                    }
                    this.con.jtaUnenlistConnection();
                    --this.enlistedTransactionCount;
                    if (this.xaLogger.isLoggable(Level.FINER)) {
                        this.xaLogger.finer(this.toString() + " End un-enlist, enlisted count:" + this.enlistedTransactionCount);
                    }
                }
                catch (SQLServerException e1) {
                    form = new MessageFormat(SQLServerException.getErrString("R_failedToUnEnlist"));
                    msgArgs = new Object[]{e1.getMessage()};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                }
            }
            if (nType == 6) {
                try {
                    returnStatus.bData = cs.getBytes(4);
                }
                catch (SQLServerException e1) {
                    form = new MessageFormat(SQLServerException.getErrString("R_failedToReadRecoveryXIDs"));
                    msgArgs = new Object[]{e1.getMessage()};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                }
            }
        }
        catch (SQLTimeoutException ex) {
            if (this.xaLogger.isLoggable(Level.FINER)) {
                this.xaLogger.finer(this.toString() + " exception:" + ex);
            }
            e = new XAException(ex.toString());
            e.errorCode = -7;
            throw e;
        }
        catch (SQLServerException ex) {
            if (this.xaLogger.isLoggable(Level.FINER)) {
                this.xaLogger.finer(this.toString() + " exception:" + ex);
            }
            if (ex.getMessage().equals(SQLServerException.getErrString("R_noServerResponse")) || SQLServerError.TransientError.isTransientError(ex.getSQLServerError()) || this.isResourceManagerFailure(ex)) {
                e = new XAException(ex.toString());
                e.errorCode = -7;
                throw e;
            }
            e = new XAException(ex.toString());
            e.errorCode = -3;
            throw e;
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " Status:" + nStatus);
        }
        returnStatus.nStatus = nStatus;
        return returnStatus;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        this.tightlyCoupled = flags & 0x8000;
        this.dtc_XA_interface(0, xid, flags);
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        this.dtc_XA_interface(1, xid, flags | this.tightlyCoupled);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        XAReturnValue r = this.dtc_XA_interface(2, xid, this.tightlyCoupled);
        return r.nStatus;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        this.dtc_XA_interface(3, xid, (onePhase ? 0x40000000 : 0) | this.tightlyCoupled);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        this.dtc_XA_interface(4, xid, this.tightlyCoupled);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        this.dtc_XA_interface(5, xid, this.tightlyCoupled);
    }

    @Override
    public Xid[] recover(int flags) throws XAException {
        int bidLen;
        XAReturnValue r = this.dtc_XA_interface(6, null, flags | this.tightlyCoupled);
        ArrayList<XidImpl> al = new ArrayList<XidImpl>();
        if (null == r.bData) {
            return new XidImpl[0];
        }
        for (int offset = 0; offset < r.bData.length; offset += bidLen) {
            int power = 1;
            int formatId = 0;
            for (int i = 0; i < 4; ++i) {
                int x = r.bData[offset + i] & 0xFF;
                formatId += (x *= power);
                power *= 256;
            }
            try {
                offset += 4;
                int gidLen = r.bData[offset++] & 0xFF;
                bidLen = r.bData[offset++] & 0xFF;
                byte[] gid = new byte[gidLen];
                byte[] bid = new byte[bidLen];
                System.arraycopy(r.bData, offset, gid, 0, gidLen);
                System.arraycopy(r.bData, offset += gidLen, bid, 0, bidLen);
                XidImpl xid = new XidImpl(formatId, gid, bid);
                al.add(xid);
                continue;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
                Object[] msgArgs = new Object[]{offset};
                XAException xex = new XAException(form.format(msgArgs));
                xex.errorCode = -3;
                if (this.xaLogger.isLoggable(Level.FINER)) {
                    this.xaLogger.finer(this.toString() + " exception:" + xex);
                }
                throw xex;
            }
        }
        Xid[] xids = new XidImpl[al.size()];
        for (int i = 0; i < al.size(); ++i) {
            xids[i] = (XidImpl)al.get(i);
            if (!this.xaLogger.isLoggable(Level.FINER)) continue;
            this.xaLogger.finer(this.toString() + ((XidImpl)xids[i]).toString());
        }
        return xids;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " xares:" + xares);
        }
        if (!(xares instanceof SQLServerXAResource)) {
            return false;
        }
        SQLServerXAResource jxa = (SQLServerXAResource)xares;
        return jxa.sResourceManagerId.equals(this.sResourceManagerId);
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        this.isTransacrionTimeoutSet = 1;
        this.timeoutSeconds = seconds;
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " TransactionTimeout:" + seconds);
        }
        return true;
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return this.timeoutSeconds;
    }

    private static int nextResourceID() {
        return baseResourceID.incrementAndGet();
    }

    private boolean isResourceManagerFailure(Throwable throwable) {
        Throwable root = Util.getRootCause(throwable);
        if (null == root) {
            return false;
        }
        ResourceManagerFailure err = ResourceManagerFailure.fromString(root.getMessage());
        if (null == err) {
            return false;
        }
        if (root instanceof SocketException) {
            switch (err) {
                case CONN_RESET: {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    static {
        xaInitLock = new ReentrantLock();
        baseResourceID = new AtomicInteger(0);
    }

    private static enum ResourceManagerFailure {
        CONN_RESET("Connection reset");

        private final String errString;

        private ResourceManagerFailure(String errString) {
            this.errString = errString;
        }

        public String toString() {
            return this.errString;
        }

        static ResourceManagerFailure fromString(String errString) {
            for (ResourceManagerFailure resourceManagerFailure : ResourceManagerFailure.values()) {
                if (!errString.equalsIgnoreCase(resourceManagerFailure.toString())) continue;
                return resourceManagerFailure;
            }
            return null;
        }
    }
}

