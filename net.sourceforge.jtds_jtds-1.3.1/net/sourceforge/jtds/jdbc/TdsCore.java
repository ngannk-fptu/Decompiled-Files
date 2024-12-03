/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.MSCursorResultSet;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.NtlmAuth;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.ProtocolException;
import net.sourceforge.jtds.jdbc.RequestStream;
import net.sourceforge.jtds.jdbc.ResponseStream;
import net.sourceforge.jtds.jdbc.SQLDiagnostic;
import net.sourceforge.jtds.jdbc.Semaphore;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsData;
import net.sourceforge.jtds.util.Logger;
import net.sourceforge.jtds.util.SSPIJNIClient;
import net.sourceforge.jtds.util.TimerThread;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class TdsCore {
    public static final int MIN_PKT_SIZE = 512;
    public static final int DEFAULT_MIN_PKT_SIZE_TDS70 = 4096;
    public static final int MAX_PKT_SIZE = 32768;
    public static final int PKT_HDR_LEN = 8;
    public static final byte QUERY_PKT = 1;
    public static final byte LOGIN_PKT = 2;
    public static final byte RPC_PKT = 3;
    public static final byte REPLY_PKT = 4;
    public static final byte CANCEL_PKT = 6;
    public static final byte MSDTC_PKT = 14;
    public static final byte SYBQUERY_PKT = 15;
    public static final byte MSLOGIN_PKT = 16;
    public static final byte NTLMAUTH_PKT = 17;
    public static final byte PRELOGIN_PKT = 18;
    public static final int SSL_ENCRYPT_LOGIN = 0;
    public static final int SSL_CLIENT_FORCE_ENCRYPT = 1;
    public static final int SSL_NO_ENCRYPT = 2;
    public static final int SSL_SERVER_FORCE_ENCRYPT = 3;
    private static final byte TDS5_PARAMFMT2_TOKEN = 32;
    private static final byte TDS_LANG_TOKEN = 33;
    private static final byte TDS5_WIDE_RESULT = 97;
    private static final byte TDS_CLOSE_TOKEN = 113;
    private static final byte TDS_OFFSETS_TOKEN = 120;
    private static final byte TDS_RETURNSTATUS_TOKEN = 121;
    private static final byte TDS_PROCID = 124;
    private static final byte TDS7_RESULT_TOKEN = -127;
    private static final byte ALTMETADATA_TOKEN = -120;
    private static final byte TDS_COLNAME_TOKEN = -96;
    private static final byte TDS_COLFMT_TOKEN = -95;
    private static final byte TDS_TABNAME_TOKEN = -92;
    private static final byte TDS_COLINFO_TOKEN = -91;
    private static final byte TDS_COMP_NAMES_TOKEN = -89;
    private static final byte TDS_COMP_RESULT_TOKEN = -88;
    private static final byte TDS_ORDER_TOKEN = -87;
    private static final byte TDS_ERROR_TOKEN = -86;
    private static final byte TDS_INFO_TOKEN = -85;
    private static final byte TDS_PARAM_TOKEN = -84;
    private static final byte TDS_LOGINACK_TOKEN = -83;
    private static final byte TDS_CONTROL_TOKEN = -82;
    private static final byte TDS_ROW_TOKEN = -47;
    private static final byte TDS_ALTROW = -45;
    private static final byte TDS5_PARAMS_TOKEN = -41;
    private static final byte TDS_CAP_TOKEN = -30;
    private static final byte TDS_ENVCHANGE_TOKEN = -29;
    private static final byte TDS_MSG50_TOKEN = -27;
    private static final byte TDS_DBRPC_TOKEN = -26;
    private static final byte TDS5_DYNAMIC_TOKEN = -25;
    private static final byte TDS5_PARAMFMT_TOKEN = -20;
    private static final byte TDS_AUTH_TOKEN = -19;
    private static final byte TDS_RESULT_TOKEN = -18;
    private static final byte TDS_DONE_TOKEN = -3;
    private static final byte TDS_DONEPROC_TOKEN = -2;
    private static final byte TDS_DONEINPROC_TOKEN = -1;
    private static final byte TDS_ENV_DATABASE = 1;
    private static final byte TDS_ENV_LANG = 2;
    private static final byte TDS_ENV_CHARSET = 3;
    private static final byte TDS_ENV_PACKSIZE = 4;
    private static final byte TDS_ENV_LCID = 5;
    private static final byte TDS_ENV_SQLCOLLATION = 7;
    private static final ParamInfo[] EMPTY_PARAMETER_INFO = new ParamInfo[0];
    private static final byte DONE_MORE_RESULTS = 1;
    private static final byte DONE_ERROR = 2;
    private static final byte DONE_ROW_COUNT = 16;
    static final byte DONE_CANCEL = 32;
    private static final byte DONE_END_OF_RESPONSE = -128;
    public static final int UNPREPARED = 0;
    public static final int TEMPORARY_STORED_PROCEDURES = 1;
    public static final int EXECUTE_SQL = 2;
    public static final int PREPARE = 3;
    static final int SYB_LONGDATA = 1;
    static final int SYB_DATETIME = 2;
    static final int SYB_BITNULL = 4;
    static final int SYB_EXTCOLINFO = 8;
    static final int SYB_UNICODE = 16;
    static final int SYB_UNITEXT = 32;
    static final int SYB_BIGINT = 64;
    private static final int ASYNC_CANCEL = 0;
    private static final int TIMEOUT_CANCEL = 1;
    private static HashMap tds8SpNames = new HashMap();
    private static String hostName;
    private static SSPIJNIClient sspiJNIClient;
    private final JtdsConnection connection;
    private int tdsVersion;
    private final int serverType;
    private final SharedSocket socket;
    private final RequestStream out;
    private final ResponseStream in;
    private boolean endOfResponse = true;
    private boolean endOfResults = true;
    private ColInfo[] columns;
    private ColInfo[] computedColumns;
    private Object[] rowData;
    private Object[] computedRowData;
    private TableMetaData[] tables;
    private final TdsToken currentToken = new TdsToken();
    private Integer returnStatus;
    private ParamInfo returnParam;
    private ParamInfo[] parameters;
    private int nextParam = -1;
    private final SQLDiagnostic messages;
    private boolean isClosed;
    private boolean ntlmAuthSSO;
    private boolean fatalError;
    private Semaphore connectionLock;
    private boolean inBatch;
    private int sslMode = 2;
    private boolean cancelPending;
    private final int[] cancelMonitor = new int[1];
    private boolean _ErrorReceived;
    byte[] nonce;
    byte[] ntlmMessage;
    byte[] ntlmTarget;
    private GSSContext _gssContext;

    TdsCore(JtdsConnection connection, SQLDiagnostic messages) {
        this.connection = connection;
        this.socket = connection.getSocket();
        this.messages = messages;
        this.serverType = connection.getServerType();
        this.tdsVersion = this.socket.getTdsVersion();
        this.out = this.socket.getRequestStream(connection.getNetPacketSize(), connection.getMaxPrecision());
        this.in = this.socket.getResponseStream(this.out, connection.getNetPacketSize());
    }

    private void checkOpen() throws SQLException {
        if (this.connection.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", "Connection"), "HY010");
        }
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    ColInfo[] getColumns() {
        return this.columns;
    }

    void setColumns(ColInfo[] columns) {
        this.columns = columns;
        this.rowData = new Object[columns.length];
        this.tables = null;
    }

    ParamInfo[] getParameters() {
        if (this.currentToken.dynamParamInfo != null) {
            ParamInfo[] params = new ParamInfo[this.currentToken.dynamParamInfo.length];
            for (int i = 0; i < params.length; ++i) {
                ColInfo ci = this.currentToken.dynamParamInfo[i];
                params[i] = new ParamInfo(ci, ci.realName, null, 0);
            }
            return params;
        }
        return EMPTY_PARAMETER_INFO;
    }

    Object[] getRowData() {
        return this.rowData;
    }

    void negotiateSSL(String instance, String ssl) throws IOException, SQLException {
        if (!ssl.equalsIgnoreCase("off")) {
            if (ssl.equalsIgnoreCase("require") || ssl.equalsIgnoreCase("authenticate")) {
                this.sendPreLoginPacket(instance, true);
                this.sslMode = this.readPreLoginPacket();
                if (this.sslMode != 1 && this.sslMode != 3) {
                    throw new SQLException(Messages.get("error.ssl.encryptionoff"), "08S01");
                }
            } else {
                this.sendPreLoginPacket(instance, false);
                this.sslMode = this.readPreLoginPacket();
            }
            if (this.sslMode != 2) {
                this.socket.enableEncryption(ssl);
            }
        }
    }

    void login(String serverName, String database, String user, String password, String domain, String charset, String appName, String progName, String wsid, String language, String macAddress, int packetSize) throws SQLException {
        try {
            if (wsid.length() == 0) {
                wsid = TdsCore.getHostName();
            }
            if (this.tdsVersion >= 3) {
                this.sendMSLoginPkt(serverName, database, user, password, domain, appName, progName, wsid, language, macAddress, packetSize);
            } else if (this.tdsVersion == 2) {
                this.send50LoginPkt(serverName, user, password, charset, appName, progName, wsid, language, packetSize);
            } else {
                this.send42LoginPkt(serverName, user, password, charset, appName, progName, wsid, language, packetSize);
            }
            if (this.sslMode == 0) {
                this.socket.disableEncryption();
            }
            this.nextToken();
            while (!this.endOfResponse) {
                if (this.currentToken.isAuthToken()) {
                    if (this._gssContext != null) {
                        this.sendGssToken();
                    } else {
                        this.sendNtlmChallengeResponse(user, password, domain);
                    }
                }
                this.nextToken();
            }
            this.messages.checkErrors();
        }
        catch (IOException ioe) {
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
        }
    }

    boolean getMoreResults() throws SQLException {
        this.checkOpen();
        this.nextToken();
        while (!(this.endOfResponse || this.currentToken.isUpdateCount() || this.currentToken.isResultSet())) {
            this.nextToken();
        }
        if (this.currentToken.isResultSet()) {
            byte saveToken = this.currentToken.token;
            try {
                byte x = (byte)this.in.peek();
                while (x == -92 || x == -91 || x == -82) {
                    this.nextToken();
                    x = (byte)this.in.peek();
                }
            }
            catch (IOException e) {
                this.connection.setClosed();
                throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), (Throwable)e);
            }
            this.currentToken.token = saveToken;
        }
        return this.currentToken.isResultSet();
    }

    boolean isResultSet() {
        return this.currentToken.isResultSet();
    }

    boolean isRowData() {
        return this.currentToken.isRowData();
    }

    boolean isUpdateCount() {
        return this.currentToken.isUpdateCount();
    }

    int getUpdateCount() {
        if (this.currentToken.isEndToken()) {
            return this.currentToken.updateCount;
        }
        return -1;
    }

    boolean isEndOfResponse() {
        return this.endOfResponse;
    }

    void clearResponseQueue() throws SQLException {
        this.checkOpen();
        while (!this.endOfResponse) {
            this.nextToken();
        }
    }

    void consumeOneResponse() throws SQLException {
        this.checkOpen();
        while (!this.endOfResponse) {
            this.nextToken();
            if (!this.currentToken.isEndToken() || (this.currentToken.status & 0xFFFFFF80) == 0) continue;
            return;
        }
    }

    boolean getNextRow() throws SQLException {
        if (this.endOfResponse || this.endOfResults) {
            return false;
        }
        this.checkOpen();
        this.nextToken();
        while (!this.currentToken.isRowData() && !this.currentToken.isEndToken()) {
            this.nextToken();
        }
        if (this.endOfResults) {
            return false;
        }
        return this.currentToken.isRowData();
    }

    boolean isDataInResultSet() throws SQLException {
        this.checkOpen();
        try {
            int x = (byte)(this.endOfResponse ? -3 : (byte)this.in.peek());
            byte by = (byte)x;
            while (x != -47 && x != -45 && x != -3 && x != -1 && x != -2) {
                this.nextToken();
                x = (byte)this.in.peek();
            }
            this.messages.checkErrors();
            return x == -47 || x == -45;
        }
        catch (IOException e) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), (Throwable)e);
        }
    }

    Integer getReturnStatus() {
        return this.returnStatus;
    }

    synchronized void closeConnection() {
        try {
            if (this.tdsVersion == 2) {
                this.socket.setTimeout(1000);
                this.out.setPacketType((byte)15);
                this.out.write((byte)113);
                this.out.write((byte)0);
                this.out.flush();
                this.endOfResponse = false;
                this.clearResponseQueue();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close() throws SQLException {
        if (!this.isClosed) {
            try {
                this.clearResponseQueue();
                this.out.close();
                this.in.close();
            }
            finally {
                this.isClosed = true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void cancel(boolean timeout) {
        Semaphore mutex = null;
        try {
            mutex = this.connection.getMutex();
            int[] nArray = this.cancelMonitor;
            synchronized (this.cancelMonitor) {
                if (!this.cancelPending && !this.endOfResponse) {
                    this.cancelPending = this.socket.cancel(this.out.getVirtualSocket());
                }
                if (this.cancelPending) {
                    this.cancelMonitor[0] = timeout ? 1 : 0;
                    this.endOfResponse = false;
                }
                // ** MonitorExit[var3_3] (shouldn't be in output)
            }
        }
        finally {
            if (mutex != null) {
                mutex.release();
            }
        }
        {
            return;
        }
    }

    void submitSQL(String sql) throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
        if (sql.length() == 0) {
            throw new IllegalArgumentException("submitSQL() called with empty SQL String");
        }
        this.executeSQL(sql, null, null, false, 0, -1, -1, true);
        this.clearResponseQueue();
        this.messages.checkErrors();
    }

    void startBatch() {
        this.inBatch = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void executeSQL(String sql, String procName, ParamInfo[] parameters, boolean noMetaData, int timeOut, int maxRows, int maxFieldSize, boolean sendNow) throws SQLException {
        boolean sendFailed = true;
        this._ErrorReceived = false;
        try {
            if (this.connectionLock == null) {
                this.connectionLock = this.connection.getMutex();
            }
            this.clearResponseQueue();
            this.messages.exceptions = null;
            this.setRowCountAndTextSize(maxRows, maxFieldSize);
            this.messages.clearWarnings();
            this.returnStatus = null;
            if (parameters != null && parameters.length == 0) {
                parameters = null;
            }
            this.parameters = parameters;
            if (procName != null && procName.length() == 0) {
                procName = null;
            }
            if (parameters != null && parameters[0].isRetVal) {
                this.returnParam = parameters[0];
                this.nextParam = 0;
            } else {
                this.returnParam = null;
                this.nextParam = -1;
            }
            if (parameters != null) {
                int i;
                if (procName == null && sql.startsWith("EXECUTE ")) {
                    for (i = 0; i < parameters.length; ++i) {
                        if (parameters[i].isRetVal || !parameters[i].isOutput) continue;
                        throw new SQLException(Messages.get("error.prepare.nooutparam", Integer.toString(i + 1)), "07000");
                    }
                    sql = Support.substituteParameters(sql, parameters, this.connection);
                    parameters = null;
                } else {
                    for (i = 0; i < parameters.length; ++i) {
                        if (!parameters[i].isSet && !parameters[i].isOutput) {
                            throw new SQLException(Messages.get("error.prepare.paramnotset", Integer.toString(i + 1)), "07000");
                        }
                        parameters[i].clearOutValue();
                        TdsData.getNativeType(this.connection, parameters[i]);
                    }
                }
            }
            try {
                switch (this.tdsVersion) {
                    case 1: {
                        this.executeSQL42(sql, procName, parameters, noMetaData, sendNow);
                        break;
                    }
                    case 2: {
                        this.executeSQL50(sql, procName, parameters);
                        break;
                    }
                    case 3: 
                    case 4: 
                    case 5: {
                        this.executeSQL70(sql, procName, parameters, noMetaData, sendNow);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown TDS version " + this.tdsVersion);
                    }
                }
                if (sendNow) {
                    this.out.flush();
                    this.connectionLock.release();
                    this.connectionLock = null;
                    sendFailed = false;
                    this.endOfResponse = false;
                    this.endOfResults = true;
                    this.wait(timeOut);
                } else {
                    sendFailed = false;
                }
            }
            catch (IOException ioe) {
                this.connection.setClosed();
                throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
            }
        }
        finally {
            if ((sendNow || sendFailed) && this.connectionLock != null) {
                this.connectionLock.release();
                this.connectionLock = null;
            }
            if (sendNow) {
                this.inBatch = false;
            }
        }
    }

    String microsoftPrepare(String sql, ParamInfo[] params, boolean needCursor, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
        int prepareSql = this.connection.getPrepareSql();
        if (prepareSql == 1) {
            StringBuilder spSql = new StringBuilder(sql.length() + 32 + params.length * 15);
            String procName = this.connection.getProcName();
            spSql.append("create proc ");
            spSql.append(procName);
            spSql.append(' ');
            for (int i = 0; i < params.length; ++i) {
                spSql.append("@P");
                spSql.append(i);
                spSql.append(' ');
                spSql.append(params[i].sqlType);
                if (i + 1 >= params.length) continue;
                spSql.append(',');
            }
            spSql.append(" as ");
            spSql.append(Support.substituteParamMarkers(sql, params));
            try {
                this.submitSQL(spSql.toString());
                return procName;
            }
            catch (SQLException e) {
                if ("08S01".equals(e.getSQLState())) {
                    throw e;
                }
                this.messages.addWarning(Support.linkException(new SQLWarning(Messages.get("error.prepare.prepfailed", e.getMessage()), e.getSQLState(), e.getErrorCode()), (Throwable)e));
            }
        } else if (prepareSql == 3) {
            ParamInfo[] prepParam = new ParamInfo[needCursor ? 6 : 4];
            prepParam[0] = new ParamInfo(4, null, 1);
            prepParam[1] = new ParamInfo(-1, Support.getParameterDefinitions(params), 4);
            prepParam[2] = new ParamInfo(-1, Support.substituteParamMarkers(sql, params), 4);
            prepParam[3] = new ParamInfo(4, new Integer(1), 0);
            if (needCursor) {
                int scrollOpt = MSCursorResultSet.getCursorScrollOpt(resultSetType, resultSetConcurrency, true);
                int ccOpt = MSCursorResultSet.getCursorConcurrencyOpt(resultSetConcurrency);
                prepParam[4] = new ParamInfo(4, new Integer(scrollOpt), 1);
                prepParam[5] = new ParamInfo(4, new Integer(ccOpt), 1);
            }
            this.columns = null;
            try {
                Integer prepareHandle;
                this.executeSQL(null, needCursor ? "sp_cursorprepare" : "sp_prepare", prepParam, false, 0, -1, -1, true);
                int resultCount = 0;
                while (!this.endOfResponse) {
                    this.nextToken();
                    if (!this.isResultSet()) continue;
                    ++resultCount;
                }
                if (resultCount != 1) {
                    this.columns = null;
                }
                if ((prepareHandle = (Integer)prepParam[0].getOutValue()) != null) {
                    return prepareHandle.toString();
                }
                this.messages.checkErrors();
            }
            catch (SQLException e) {
                if ("08S01".equals(e.getSQLState())) {
                    throw e;
                }
                this.messages.addWarning(Support.linkException(new SQLWarning(Messages.get("error.prepare.prepfailed", e.getMessage()), e.getSQLState(), e.getErrorCode()), (Throwable)e));
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized String sybasePrepare(String sql, ParamInfo[] params) throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
        if (sql == null || sql.length() == 0) {
            throw new IllegalArgumentException("sql parameter must be at least 1 character long.");
        }
        String procName = this.connection.getProcName();
        if (procName == null || procName.length() != 11) {
            throw new IllegalArgumentException("procName parameter must be 11 characters long.");
        }
        for (int i = 0; i < params.length; ++i) {
            if (!"text".equals(params[i].sqlType) && !"unitext".equals(params[i].sqlType) && !"image".equals(params[i].sqlType)) continue;
            return null;
        }
        Semaphore mutex = null;
        try {
            mutex = this.connection.getMutex();
            this.out.setPacketType((byte)15);
            this.out.write((byte)-25);
            byte[] buf = Support.encodeString(this.connection.getCharset(), sql);
            this.out.write((short)(buf.length + 41));
            this.out.write((byte)1);
            this.out.write((byte)0);
            this.out.write((byte)10);
            this.out.writeAscii(procName.substring(1));
            this.out.write((short)(buf.length + 26));
            this.out.writeAscii("create proc ");
            this.out.writeAscii(procName.substring(1));
            this.out.writeAscii(" as ");
            this.out.write(buf);
            this.out.flush();
            this.endOfResponse = false;
            this.clearResponseQueue();
            this.messages.checkErrors();
            String string = procName;
            return string;
        }
        catch (IOException ioe) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
        }
        catch (SQLException e) {
            if ("08S01".equals(e.getSQLState())) {
                throw e;
            }
            String string = null;
            return string;
        }
        finally {
            if (mutex != null) {
                mutex.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void sybaseUnPrepare(String procName) throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
        if (procName == null || procName.length() != 11) {
            throw new IllegalArgumentException("procName parameter must be 11 characters long.");
        }
        Semaphore mutex = null;
        try {
            mutex = this.connection.getMutex();
            this.out.setPacketType((byte)15);
            this.out.write((byte)-25);
            this.out.write((short)15);
            this.out.write((byte)4);
            this.out.write((byte)0);
            this.out.write((byte)10);
            this.out.writeAscii(procName.substring(1));
            this.out.write((short)0);
            this.out.flush();
            this.endOfResponse = false;
            this.clearResponseQueue();
            this.messages.checkErrors();
        }
        catch (IOException ioe) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
        }
        catch (SQLException e) {
            if ("08S01".equals(e.getSQLState())) {
                throw e;
            }
        }
        finally {
            if (mutex != null) {
                mutex.release();
            }
        }
    }

    synchronized byte[] enlistConnection(int type, byte[] oleTranID) throws SQLException {
        Object x;
        Semaphore mutex = null;
        try {
            mutex = this.connection.getMutex();
            this.out.setPacketType((byte)14);
            this.out.write((short)type);
            switch (type) {
                case 0: {
                    this.out.write((short)0);
                    break;
                }
                case 1: {
                    if (oleTranID != null) {
                        this.out.write((short)oleTranID.length);
                        this.out.write(oleTranID);
                        break;
                    }
                    this.out.write((short)0);
                }
            }
            this.out.flush();
            this.endOfResponse = false;
            this.endOfResults = true;
        }
        catch (IOException ioe) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
        }
        finally {
            if (mutex != null) {
                mutex.release();
            }
        }
        byte[] tmAddress = null;
        if (this.getMoreResults() && this.getNextRow() && this.rowData.length == 1 && (x = this.rowData[0]) instanceof byte[]) {
            tmAddress = (byte[])x;
        }
        this.clearResponseQueue();
        this.messages.checkErrors();
        return tmAddress;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SQLException getBatchCounts(ArrayList counts, SQLException sqlEx) throws SQLException {
        Integer lastCount = JtdsStatement.SUCCESS_NO_INFO;
        try {
            this.checkOpen();
            while (!this.endOfResponse) {
                this.nextToken();
                if (this.currentToken.isResultSet()) {
                    throw new SQLException(Messages.get("error.statement.batchnocount"), "07000");
                }
                switch (this.currentToken.token) {
                    case -3: {
                        if ((this.currentToken.status & 2) != 0 || lastCount == JtdsStatement.EXECUTE_FAILED) {
                            counts.add(JtdsStatement.EXECUTE_FAILED);
                        } else if (this.currentToken.isUpdateCount()) {
                            counts.add(new Integer(this.currentToken.updateCount));
                        } else {
                            counts.add(lastCount);
                        }
                        lastCount = JtdsStatement.SUCCESS_NO_INFO;
                        break;
                    }
                    case -1: {
                        if ((this.currentToken.status & 2) != 0) {
                            lastCount = JtdsStatement.EXECUTE_FAILED;
                            break;
                        }
                        if (!this.currentToken.isUpdateCount()) break;
                        lastCount = new Integer(this.currentToken.updateCount);
                        break;
                    }
                    case -2: {
                        if ((this.currentToken.status & 2) != 0 || lastCount == JtdsStatement.EXECUTE_FAILED) {
                            counts.add(JtdsStatement.EXECUTE_FAILED);
                        } else {
                            counts.add(lastCount);
                        }
                        lastCount = JtdsStatement.SUCCESS_NO_INFO;
                    }
                }
            }
            this.messages.checkErrors();
        }
        catch (SQLException e) {
            if (sqlEx != null) {
                sqlEx.setNextException(e);
            }
            sqlEx = e;
            return sqlEx;
        }
        finally {
            while (!this.endOfResponse) {
                try {
                    this.nextToken();
                }
                catch (SQLException ex) {
                    this.checkOpen();
                    if (sqlEx != null) {
                        sqlEx.setNextException(ex);
                        continue;
                    }
                    sqlEx = ex;
                }
            }
        }
        return sqlEx;
    }

    ColInfo[] getComputedColumns() {
        return this.computedColumns;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object[] getComputedRowData() {
        try {
            Object[] objectArray = this.computedRowData;
            return objectArray;
        }
        finally {
            this.computedRowData = null;
        }
    }

    private void putLoginString(String txt, int len) throws IOException {
        byte[] tmp = Support.encodeString(this.connection.getCharset(), txt);
        this.out.write(tmp, 0, len);
        this.out.write((byte)(tmp.length < len ? tmp.length : len));
    }

    private void sendPreLoginPacket(String instance, boolean forceEncryption) throws IOException {
        this.out.setPacketType((byte)18);
        this.out.write((short)0);
        this.out.write((short)21);
        this.out.write((byte)6);
        this.out.write((short)1);
        this.out.write((short)27);
        this.out.write((byte)1);
        this.out.write((short)2);
        this.out.write((short)28);
        this.out.write((byte)(instance.length() + 1));
        this.out.write((short)3);
        this.out.write((short)(28 + instance.length() + 1));
        this.out.write((byte)4);
        this.out.write((byte)-1);
        this.out.write(new byte[]{8, 0, 1, 85, 0, 0});
        this.out.write((byte)(forceEncryption ? 1 : 0));
        this.out.writeAscii(instance);
        this.out.write((byte)0);
        this.out.write(new byte[]{1, 2, 0, 0});
        this.out.flush();
    }

    private int readPreLoginPacket() throws IOException {
        int i;
        byte[][] list = new byte[8][];
        byte[][] data = new byte[8][];
        int recordCount = 0;
        byte[] record = new byte[5];
        record[0] = (byte)this.in.read();
        while ((record[0] & 0xFF) != 255) {
            if (recordCount == list.length) {
                throw new IOException("Pre Login packet has more than 8 entries");
            }
            this.in.read(record, 1, 4);
            list[recordCount++] = record;
            record = new byte[5];
            record[0] = (byte)this.in.read();
        }
        for (i = 0; i < recordCount; ++i) {
            byte[] value = new byte[list[i][4]];
            this.in.read(value);
            data[i] = value;
        }
        if (Logger.isActive()) {
            Logger.println("PreLogin server response");
            for (i = 0; i < recordCount; ++i) {
                Logger.println("Record " + i + " = " + Support.toHex(data[i]));
            }
        }
        if (recordCount > 1) {
            return data[1][0];
        }
        return 2;
    }

    private void send42LoginPkt(String serverName, String user, String password, String charset, String appName, String progName, String wsid, String language, int packetSize) throws IOException {
        byte[] empty = new byte[]{};
        this.out.setPacketType((byte)2);
        this.putLoginString(wsid, 30);
        this.putLoginString(user, 30);
        this.putLoginString(password, 30);
        this.putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write((byte)3);
        this.out.write((byte)1);
        this.out.write((byte)6);
        this.out.write((byte)10);
        this.out.write((byte)9);
        this.out.write((byte)1);
        this.out.write((byte)1);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write(empty, 0, 7);
        this.putLoginString(appName, 30);
        this.putLoginString(serverName, 30);
        this.out.write((byte)0);
        this.out.write((byte)password.length());
        byte[] tmp = Support.encodeString(this.connection.getCharset(), password);
        this.out.write(tmp, 0, 253);
        this.out.write((byte)(tmp.length + 2));
        this.out.write((byte)4);
        this.out.write((byte)2);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.putLoginString(progName, 10);
        this.out.write((byte)6);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)13);
        this.out.write((byte)17);
        this.putLoginString(language, 30);
        this.out.write((byte)1);
        this.out.write((short)0);
        this.out.write((byte)0);
        this.out.write(empty, 0, 8);
        this.out.write((short)0);
        this.putLoginString(charset, 30);
        this.out.write((byte)1);
        this.putLoginString(String.valueOf(packetSize), 6);
        this.out.write(empty, 0, 8);
        this.out.flush();
        this.endOfResponse = false;
    }

    private void send50LoginPkt(String serverName, String user, String password, String charset, String appName, String progName, String wsid, String language, int packetSize) throws IOException {
        byte[] empty = new byte[]{};
        this.out.setPacketType((byte)2);
        this.putLoginString(wsid, 30);
        this.putLoginString(user, 30);
        this.putLoginString(password, 30);
        this.putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write((byte)3);
        this.out.write((byte)1);
        this.out.write((byte)6);
        this.out.write((byte)10);
        this.out.write((byte)9);
        this.out.write((byte)1);
        this.out.write((byte)1);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write(empty, 0, 7);
        this.putLoginString(appName, 30);
        this.putLoginString(serverName, 30);
        this.out.write((byte)0);
        this.out.write((byte)password.length());
        byte[] tmp = Support.encodeString(this.connection.getCharset(), password);
        this.out.write(tmp, 0, 253);
        this.out.write((byte)(tmp.length + 2));
        this.out.write((byte)5);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.putLoginString(progName, 10);
        this.out.write((byte)5);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write((byte)13);
        this.out.write((byte)17);
        this.putLoginString(language, 30);
        this.out.write((byte)1);
        this.out.write((short)0);
        this.out.write((byte)0);
        this.out.write(empty, 0, 8);
        this.out.write((short)0);
        this.putLoginString(charset, 30);
        this.out.write((byte)1);
        if (packetSize > 0) {
            this.putLoginString(String.valueOf(packetSize), 6);
        } else {
            this.putLoginString(String.valueOf(512), 6);
        }
        this.out.write(empty, 0, 4);
        byte[] capString = new byte[]{1, 11, 79, -1, -123, -18, -17, 101, 127, -1, -1, -1, -42, 2, 10, 0, 2, 4, 6, -128, 6, 72, 0, 0, 12};
        if (packetSize == 0) {
            capString[17] = 0;
        }
        this.out.write((byte)-30);
        this.out.write((short)capString.length);
        this.out.write(capString);
        this.out.flush();
        this.endOfResponse = false;
    }

    private void sendMSLoginPkt(String serverName, String database, String user, String password, String domain, String appName, String progName, String wsid, String language, String macAddress, int netPacketSize) throws IOException, SQLException {
        short authLen;
        this.ntlmMessage = null;
        byte[] empty = new byte[]{};
        boolean ntlmAuth = false;
        boolean useKerberos = this.connection.getUseKerberos();
        if (useKerberos || user == null || user.length() == 0) {
            this.ntlmAuthSSO = true;
            ntlmAuth = true;
        } else if (domain != null && domain.length() > 0) {
            ntlmAuth = true;
        }
        if (this.ntlmAuthSSO && Support.isWindowsOS() && !useKerberos) {
            try {
                sspiJNIClient = SSPIJNIClient.getInstance();
                this.ntlmMessage = sspiJNIClient.invokePrepareSSORequest();
                Logger.println("Using native SSO library for Windows Authentication.");
            }
            catch (Exception e) {
                throw new IOException("SSO Failed: " + e.getMessage());
            }
        }
        if (this.ntlmAuthSSO) {
            try {
                this.ntlmMessage = this.createGssToken();
                Logger.println("Using Kerberos GSS authentication.");
            }
            catch (GSSException gsse) {
                throw new IOException("GSS Failed: " + gsse.getMessage());
            }
        }
        short packSize = (short)(86 + 2 * (wsid.length() + appName.length() + serverName.length() + progName.length() + database.length() + language.length()));
        if (ntlmAuth) {
            authLen = this.ntlmAuthSSO && this.ntlmMessage != null ? (short)this.ntlmMessage.length : (short)(32 + domain.length());
            packSize = (short)(packSize + authLen);
        } else {
            authLen = 0;
            packSize = (short)(packSize + 2 * (user.length() + password.length()));
        }
        this.out.setPacketType((byte)16);
        this.out.write((int)packSize);
        if (this.tdsVersion == 3) {
            this.out.write(0x70000000);
        } else {
            this.out.write(0x71000001);
        }
        this.out.write(netPacketSize);
        this.out.write(7);
        this.out.write(this.connection.getProcessId());
        this.out.write(0);
        byte flags = -32;
        this.out.write(flags);
        flags = 3;
        if (ntlmAuth) {
            flags = (byte)(flags | 0x80);
        }
        this.out.write(flags);
        this.out.write((byte)0);
        this.out.write((byte)0);
        this.out.write(empty, 0, 4);
        this.out.write(empty, 0, 4);
        short curPos = 86;
        this.out.write(curPos);
        this.out.write((short)wsid.length());
        curPos = (short)(curPos + wsid.length() * 2);
        if (!ntlmAuth) {
            this.out.write(curPos);
            this.out.write((short)user.length());
            curPos = (short)(curPos + user.length() * 2);
            this.out.write(curPos);
            this.out.write((short)password.length());
            curPos = (short)(curPos + password.length() * 2);
        } else {
            this.out.write(curPos);
            this.out.write((short)0);
            this.out.write(curPos);
            this.out.write((short)0);
        }
        this.out.write(curPos);
        this.out.write((short)appName.length());
        curPos = (short)(curPos + appName.length() * 2);
        this.out.write(curPos);
        this.out.write((short)serverName.length());
        curPos = (short)(curPos + serverName.length() * 2);
        this.out.write((short)0);
        this.out.write((short)0);
        this.out.write(curPos);
        this.out.write((short)progName.length());
        curPos = (short)(curPos + progName.length() * 2);
        this.out.write(curPos);
        this.out.write((short)language.length());
        curPos = (short)(curPos + language.length() * 2);
        this.out.write(curPos);
        this.out.write((short)database.length());
        curPos = (short)(curPos + database.length() * 2);
        this.out.write(TdsCore.getMACAddress(macAddress));
        this.out.write(curPos);
        this.out.write(authLen);
        this.out.write((int)packSize);
        this.out.write(wsid);
        if (!ntlmAuth) {
            String scrambledPw = TdsCore.tds7CryptPass(password);
            this.out.write(user);
            this.out.write(scrambledPw);
        }
        this.out.write(appName);
        this.out.write(serverName);
        this.out.write(progName);
        this.out.write(language);
        this.out.write(database);
        if (ntlmAuth) {
            if (this.ntlmAuthSSO) {
                this.out.write(this.ntlmMessage);
            } else {
                byte[] domainBytes = domain.getBytes("UTF8");
                byte[] header = new byte[]{78, 84, 76, 77, 83, 83, 80, 0};
                this.out.write(header);
                this.out.write(1);
                if (this.connection.getUseNTLMv2()) {
                    this.out.write(569861);
                } else {
                    this.out.write(45569);
                }
                this.out.write((short)domainBytes.length);
                this.out.write((short)domainBytes.length);
                this.out.write(32);
                this.out.write((short)0);
                this.out.write((short)0);
                this.out.write(32);
                this.out.write(domainBytes);
            }
        }
        this.out.flush();
        this.endOfResponse = false;
    }

    private void tdsGssToken() throws IOException {
        short pktLen = this.in.readShort();
        this.ntlmMessage = new byte[pktLen];
        this.in.read(this.ntlmMessage);
        Logger.println("GSS: Received token (length: " + this.ntlmMessage.length + ")");
    }

    private void sendGssToken() throws IOException {
        try {
            byte[] gssMessage = this._gssContext.initSecContext(this.ntlmMessage, 0, this.ntlmMessage.length);
            if (this._gssContext.isEstablished()) {
                Logger.println("GSS: Security context established.");
            }
            if (gssMessage != null) {
                Logger.println("GSS: Sending token (length: " + this.ntlmMessage.length + ")");
                this.out.setPacketType((byte)17);
                this.out.write(gssMessage);
                this.out.flush();
            }
        }
        catch (GSSException e) {
            throw new IOException("GSS failure: " + e.getMessage());
        }
    }

    private void sendNtlmChallengeResponse(String user, String password, String domain) throws IOException {
        this.out.setPacketType((byte)17);
        if (this.ntlmAuthSSO) {
            try {
                this.ntlmMessage = sspiJNIClient.invokePrepareSSOSubmit(this.ntlmMessage);
            }
            catch (Exception e) {
                throw new IOException("SSO Failed: " + e.getMessage());
            }
            this.out.write(this.ntlmMessage);
        } else {
            byte[] ntAnswer;
            byte[] lmAnswer;
            if (this.connection.getUseNTLMv2()) {
                byte[] clientNonce = new byte[8];
                new Random().nextBytes(clientNonce);
                lmAnswer = NtlmAuth.answerLmv2Challenge(domain, user, password, this.nonce, clientNonce);
                ntAnswer = NtlmAuth.answerNtlmv2Challenge(domain, user, password, this.nonce, this.ntlmTarget, clientNonce);
            } else {
                lmAnswer = NtlmAuth.answerLmChallenge(password, this.nonce);
                ntAnswer = NtlmAuth.answerNtChallenge(password, this.nonce);
            }
            byte[] header = new byte[]{78, 84, 76, 77, 83, 83, 80, 0};
            this.out.write(header);
            this.out.write(3);
            int domainLenInBytes = domain.length() * 2;
            int userLenInBytes = user.length() * 2;
            boolean hostLenInBytes = false;
            int pos = 64 + domainLenInBytes + userLenInBytes + 0;
            this.out.write((short)lmAnswer.length);
            this.out.write((short)lmAnswer.length);
            this.out.write(pos);
            this.out.write((short)ntAnswer.length);
            this.out.write((short)ntAnswer.length);
            this.out.write(pos += lmAnswer.length);
            pos = 64;
            this.out.write((short)domainLenInBytes);
            this.out.write((short)domainLenInBytes);
            this.out.write(pos);
            this.out.write((short)userLenInBytes);
            this.out.write((short)userLenInBytes);
            this.out.write(pos += domainLenInBytes);
            this.out.write((short)0);
            this.out.write((short)0);
            this.out.write(pos += userLenInBytes);
            this.out.write((short)0);
            this.out.write((short)0);
            this.out.write(pos += 0);
            if (this.connection.getUseNTLMv2()) {
                this.out.write(557569);
            } else {
                this.out.write(33281);
            }
            this.out.write(domain);
            this.out.write(user);
            this.out.write(lmAnswer);
            this.out.write(ntAnswer);
        }
        this.out.flush();
    }

    private void nextToken() throws SQLException {
        this.checkOpen();
        if (this.endOfResponse) {
            this.currentToken.token = (byte)-3;
            this.currentToken.status = 0;
            return;
        }
        try {
            if (this.computedColumns != null) {
                switch ((byte)this.in.peek()) {
                    case -45: {
                        if (this.endOfResults) break;
                        this.endOfResults = true;
                        return;
                    }
                    case -47: {
                        if (!this.endOfResults) break;
                        this.endOfResults = false;
                        return;
                    }
                }
            }
            this.currentToken.token = (byte)this.in.read();
            switch (this.currentToken.token) {
                case 32: {
                    this.tds5ParamFmt2Token();
                    break;
                }
                case 33: {
                    this.tdsInvalidToken();
                    break;
                }
                case 97: {
                    this.tds5WideResultToken();
                    break;
                }
                case 113: {
                    this.tdsInvalidToken();
                    break;
                }
                case 121: {
                    this.tdsReturnStatusToken();
                    break;
                }
                case 124: {
                    this.tdsProcIdToken();
                    break;
                }
                case 120: {
                    this.tdsOffsetsToken();
                    break;
                }
                case -127: {
                    this.tds7ResultToken();
                    break;
                }
                case -120: {
                    this.tdsComputedResultToken();
                    break;
                }
                case -96: {
                    this.tds4ColNamesToken();
                    break;
                }
                case -95: {
                    this.tds4ColFormatToken();
                    break;
                }
                case -92: {
                    this.tdsTableNameToken();
                    break;
                }
                case -91: {
                    this.tdsColumnInfoToken();
                    break;
                }
                case -89: {
                    this.tdsInvalidToken();
                    break;
                }
                case -88: {
                    this.tdsInvalidToken();
                    break;
                }
                case -87: {
                    this.tdsOrderByToken();
                    break;
                }
                case -86: 
                case -85: {
                    this.tdsErrorToken();
                    break;
                }
                case -84: {
                    this.tdsOutputParamToken();
                    break;
                }
                case -83: {
                    this.tdsLoginAckToken();
                    break;
                }
                case -82: {
                    this.tdsControlToken();
                    break;
                }
                case -47: {
                    this.tdsRowToken();
                    break;
                }
                case -45: {
                    this.tdsComputedRowToken();
                    break;
                }
                case -41: {
                    this.tds5ParamsToken();
                    break;
                }
                case -30: {
                    this.tdsCapabilityToken();
                    break;
                }
                case -29: {
                    this.tdsEnvChangeToken();
                    break;
                }
                case -27: {
                    this.tds5ErrorToken();
                    break;
                }
                case -25: {
                    this.tds5DynamicToken();
                    break;
                }
                case -20: {
                    this.tds5ParamFmtToken();
                    break;
                }
                case -19: {
                    if (this._gssContext != null) {
                        this.tdsGssToken();
                        break;
                    }
                    this.tdsNtlmAuthToken();
                    break;
                }
                case -18: {
                    this.tds5ResultToken();
                    break;
                }
                case -3: 
                case -2: 
                case -1: {
                    this.tdsDoneToken();
                    break;
                }
                default: {
                    throw new ProtocolException("Invalid packet type 0x" + Integer.toHexString(this.currentToken.token & 0xFF));
                }
            }
        }
        catch (IOException ioe) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01"), (Throwable)ioe);
        }
        catch (ProtocolException pe) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.tdserror", pe.getMessage()), "08S01"), (Throwable)pe);
        }
        catch (OutOfMemoryError err) {
            this.in.skipToEnd();
            this.endOfResponse = true;
            this.endOfResults = true;
            this.cancelPending = false;
            throw err;
        }
    }

    private void tdsInvalidToken() throws IOException, ProtocolException {
        this.in.skip(this.in.readShort());
        throw new ProtocolException("Unsupported TDS token: 0x" + Integer.toHexString(this.currentToken.token & 0xFF));
    }

    private void tds5ParamFmt2Token() throws IOException, ProtocolException {
        this.in.readInt();
        int paramCnt = this.in.readShort();
        ColInfo[] params = new ColInfo[paramCnt];
        for (int i = 0; i < paramCnt; ++i) {
            ColInfo col = new ColInfo();
            int colNameLen = this.in.read();
            col.realName = this.in.readNonUnicodeString(colNameLen);
            int column_flags = this.in.readInt();
            col.isCaseSensitive = false;
            col.nullable = (column_flags & 0x20) != 0 ? 1 : 0;
            col.isWriteable = (column_flags & 0x10) != 0;
            col.isIdentity = (column_flags & 0x40) != 0;
            col.isKey = (column_flags & 2) != 0;
            col.isHidden = (column_flags & 1) != 0;
            col.userType = this.in.readInt();
            TdsData.readType(this.in, col);
            this.in.skip(1);
            params[i] = col;
        }
        this.currentToken.dynamParamInfo = params;
        this.currentToken.dynamParamData = new Object[paramCnt];
    }

    private void tds5WideResultToken() throws IOException, ProtocolException {
        this.in.readInt();
        int colCnt = this.in.readShort();
        this.columns = new ColInfo[colCnt];
        this.rowData = new Object[colCnt];
        this.tables = null;
        for (int colNum = 0; colNum < colCnt; ++colNum) {
            ColInfo col = new ColInfo();
            int nameLen = this.in.read();
            col.name = this.in.readNonUnicodeString(nameLen);
            nameLen = this.in.read();
            col.catalog = this.in.readNonUnicodeString(nameLen);
            nameLen = this.in.read();
            col.schema = this.in.readNonUnicodeString(nameLen);
            nameLen = this.in.read();
            col.tableName = this.in.readNonUnicodeString(nameLen);
            nameLen = this.in.read();
            col.realName = this.in.readNonUnicodeString(nameLen);
            if (col.name == null || col.name.length() == 0) {
                col.name = col.realName;
            }
            int column_flags = this.in.readInt();
            col.isCaseSensitive = false;
            col.nullable = (column_flags & 0x20) != 0 ? 1 : 0;
            col.isWriteable = (column_flags & 0x10) != 0;
            col.isIdentity = (column_flags & 0x40) != 0;
            col.isKey = (column_flags & 2) != 0;
            col.isHidden = (column_flags & 1) != 0;
            col.userType = this.in.readInt();
            TdsData.readType(this.in, col);
            this.in.skip(1);
            this.columns[colNum] = col;
        }
        this.endOfResults = false;
    }

    private void tdsReturnStatusToken() throws IOException, SQLException {
        this.returnStatus = new Integer(this.in.readInt());
        if (this.returnParam != null) {
            this.returnParam.setOutValue(Support.convert(this.connection, this.returnStatus, this.returnParam.jdbcType, this.connection.getCharset()));
        }
    }

    private void tdsProcIdToken() throws IOException {
        this.in.skip(8);
    }

    private void tdsOffsetsToken() throws IOException {
        this.in.read();
        this.in.read();
        this.in.readShort();
    }

    private void tds7ResultToken() throws IOException, ProtocolException, SQLException {
        this.endOfResults = false;
        int colCnt = this.in.readShort();
        if (colCnt < 0) {
            return;
        }
        this.columns = new ColInfo[colCnt];
        this.rowData = new Object[colCnt];
        this.tables = null;
        for (int i = 0; i < colCnt; ++i) {
            ColInfo col = new ColInfo();
            col.userType = this.in.readShort();
            short flags = this.in.readShort();
            col.nullable = (flags & 1) != 0 ? 1 : 0;
            col.isCaseSensitive = (flags & 2) != 0;
            col.isIdentity = (flags & 0x10) != 0;
            col.isWriteable = (flags & 0xC) != 0;
            TdsData.readType(this.in, col);
            if (this.tdsVersion >= 4 && col.collation != null) {
                TdsData.setColumnCharset(col, this.connection);
            }
            int clen = this.in.read();
            col.name = col.realName = this.in.readUnicodeString(clen);
            this.columns[i] = col;
        }
    }

    private void tds4ColNamesToken() throws IOException {
        ArrayList<ColInfo> colList = new ArrayList<ColInfo>();
        int pktLen = this.in.readShort();
        this.tables = null;
        int bytesRead = 0;
        while (bytesRead < pktLen) {
            ColInfo col = new ColInfo();
            int nameLen = this.in.read();
            String name = this.in.readNonUnicodeString(nameLen);
            bytesRead = bytesRead + 1 + nameLen;
            col.realName = name;
            col.name = name;
            colList.add(col);
        }
        int colCnt = colList.size();
        this.columns = colList.toArray(new ColInfo[colCnt]);
        this.rowData = new Object[colCnt];
    }

    private void tds4ColFormatToken() throws IOException, ProtocolException {
        int pktLen = this.in.readShort();
        int bytesRead = 0;
        int numColumns = 0;
        while (bytesRead < pktLen) {
            if (numColumns > this.columns.length) {
                throw new ProtocolException("Too many columns in TDS_COL_FMT packet");
            }
            ColInfo col = this.columns[numColumns];
            if (this.serverType == 1) {
                col.userType = this.in.readShort();
                short flags = this.in.readShort();
                col.nullable = (flags & 1) != 0 ? 1 : 0;
                col.isCaseSensitive = (flags & 2) != 0;
                col.isWriteable = (flags & 0xC) != 0;
                col.isIdentity = (flags & 0x10) != 0;
            } else {
                col.isCaseSensitive = false;
                col.isWriteable = true;
                if (col.nullable == 0) {
                    col.nullable = 2;
                }
                col.userType = this.in.readInt();
            }
            bytesRead += 4;
            bytesRead += TdsData.readType(this.in, col);
            ++numColumns;
        }
        if (numColumns != this.columns.length) {
            throw new ProtocolException("Too few columns in TDS_COL_FMT packet");
        }
        this.endOfResults = false;
    }

    private void tdsTableNameToken() throws IOException, ProtocolException {
        int pktLen = this.in.readShort();
        int bytesRead = 0;
        ArrayList<TableMetaData> tableList = new ArrayList<TableMetaData>();
        while (bytesRead < pktLen) {
            int nameLen;
            TableMetaData table;
            if (this.tdsVersion >= 5) {
                table = new TableMetaData();
                ++bytesRead;
                int tableNameToken = this.in.read();
                switch (tableNameToken) {
                    case 4: {
                        nameLen = this.in.readShort();
                        bytesRead += nameLen * 2 + 2;
                        this.in.readUnicodeString(nameLen);
                    }
                    case 3: {
                        nameLen = this.in.readShort();
                        bytesRead += nameLen * 2 + 2;
                        table.catalog = this.in.readUnicodeString(nameLen);
                    }
                    case 2: {
                        nameLen = this.in.readShort();
                        bytesRead += nameLen * 2 + 2;
                        table.schema = this.in.readUnicodeString(nameLen);
                    }
                    case 1: {
                        nameLen = this.in.readShort();
                        bytesRead += nameLen * 2 + 2;
                        table.name = this.in.readUnicodeString(nameLen);
                    }
                    case 0: {
                        break;
                    }
                    default: {
                        throw new ProtocolException("Invalid table TAB_NAME_TOKEN: " + tableNameToken);
                    }
                }
            } else {
                String tabName;
                if (this.tdsVersion >= 3) {
                    nameLen = this.in.readShort();
                    bytesRead += nameLen * 2 + 2;
                    tabName = this.in.readUnicodeString(nameLen);
                } else {
                    nameLen = this.in.read();
                    ++bytesRead;
                    if (nameLen == 0) continue;
                    tabName = this.in.readNonUnicodeString(nameLen);
                    bytesRead += nameLen;
                }
                table = new TableMetaData();
                int dotPos = tabName.lastIndexOf(46);
                if (dotPos > 0) {
                    table.name = tabName.substring(dotPos + 1);
                    int nextPos = tabName.lastIndexOf(46, dotPos - 1);
                    if (nextPos + 1 < dotPos) {
                        table.schema = tabName.substring(nextPos + 1, dotPos);
                    }
                    if ((nextPos = tabName.lastIndexOf(46, (dotPos = nextPos) - 1)) + 1 < dotPos) {
                        table.catalog = tabName.substring(nextPos + 1, dotPos);
                    }
                } else {
                    table.name = tabName;
                }
            }
            tableList.add(table);
        }
        if (tableList.size() > 0) {
            this.tables = tableList.toArray(new TableMetaData[tableList.size()]);
        }
    }

    private void tdsColumnInfoToken() throws IOException, ProtocolException {
        int pktLen = this.in.readShort();
        int bytesRead = 0;
        int columnIndex = 0;
        while (bytesRead < pktLen) {
            this.in.read();
            if (columnIndex >= this.columns.length) {
                throw new ProtocolException("Column index " + (columnIndex + 1) + " invalid in TDS_COLINFO packet");
            }
            ColInfo col = this.columns[columnIndex++];
            int tableIndex = this.in.read();
            if (this.tables != null && tableIndex > this.tables.length) {
                throw new ProtocolException("Table index " + tableIndex + " invalid in TDS_COLINFO packet");
            }
            byte flags = (byte)this.in.read();
            bytesRead += 3;
            if (tableIndex != 0 && this.tables != null) {
                TableMetaData table = this.tables[tableIndex - 1];
                col.catalog = table.catalog;
                col.schema = table.schema;
                col.tableName = table.name;
            }
            col.isKey = (flags & 8) != 0;
            boolean bl = col.isHidden = (flags & 0x10) != 0;
            if ((flags & 0x20) == 0) continue;
            int nameLen = this.in.read();
            ++bytesRead;
            String colName = this.in.readString(nameLen);
            bytesRead += this.tdsVersion >= 3 ? nameLen * 2 : nameLen;
            col.realName = colName;
        }
    }

    private void tdsOrderByToken() throws IOException {
        short pktLen = this.in.readShort();
        this.in.skip(pktLen);
    }

    private void tdsErrorToken() throws IOException {
        short pktLen = this.in.readShort();
        int sizeSoFar = 6;
        int number = this.in.readInt();
        int state = this.in.read();
        int severity = this.in.read();
        int msgLen = this.in.readShort();
        String message = this.in.readString(msgLen);
        sizeSoFar += 2 + (this.tdsVersion >= 3 ? msgLen * 2 : msgLen);
        int srvNameLen = this.in.read();
        String server = this.in.readString(srvNameLen);
        sizeSoFar += 1 + (this.tdsVersion >= 3 ? srvNameLen * 2 : srvNameLen);
        int procNameLen = this.in.read();
        String procName = this.in.readString(procNameLen);
        sizeSoFar += 1 + (this.tdsVersion >= 3 ? procNameLen * 2 : procNameLen);
        short line = this.in.readShort();
        if (pktLen - (sizeSoFar += 2) > 0) {
            this.in.skip(pktLen - sizeSoFar);
        }
        if (this.currentToken.token == -86) {
            this._ErrorReceived = true;
            if (severity < 10) {
                severity = 11;
            }
            if (severity >= 20) {
                this.fatalError = true;
            }
        } else if (severity > 9) {
            severity = 9;
        }
        this.messages.addDiagnostic(number, state, severity, message, server, procName, line);
    }

    private void tdsOutputParamToken() throws IOException, ProtocolException, SQLException {
        block5: {
            Object value;
            ColInfo col;
            block6: {
                this.in.readShort();
                String name = this.in.readString(this.in.read());
                boolean funcReturnVal = this.in.read() == 2;
                this.in.read();
                this.in.skip(3);
                col = new ColInfo();
                TdsData.readType(this.in, col);
                if (this.tdsVersion >= 4 && col.collation != null) {
                    TdsData.setColumnCharset(col, this.connection);
                }
                value = TdsData.readData(this.connection, this.in, col);
                if (this.parameters == null || name.length() != 0 && !name.startsWith("@")) break block5;
                if (this.tdsVersion < 4 || !funcReturnVal) break block6;
                if (this.returnParam == null) break block5;
                if (value != null) {
                    this.returnParam.setOutValue(Support.convert(this.connection, value, this.returnParam.jdbcType, this.connection.getCharset()));
                    this.returnParam.collation = col.collation;
                    this.returnParam.charsetInfo = col.charsetInfo;
                } else {
                    this.returnParam.setOutValue(null);
                }
                break block5;
            }
            while (++this.nextParam < this.parameters.length) {
                if (!this.parameters[this.nextParam].isOutput) continue;
                if (value != null) {
                    this.parameters[this.nextParam].setOutValue(Support.convert(this.connection, value, this.parameters[this.nextParam].jdbcType, this.connection.getCharset()));
                    this.parameters[this.nextParam].collation = col.collation;
                    this.parameters[this.nextParam].charsetInfo = col.charsetInfo;
                    break;
                }
                this.parameters[this.nextParam].setOutValue(null);
                break;
            }
        }
    }

    private void tdsLoginAckToken() throws IOException {
        int minor;
        int major;
        int build = 0;
        this.in.readShort();
        int ack = this.in.read();
        this.tdsVersion = TdsData.getTdsVersion(this.in.read() << 24 | this.in.read() << 16 | this.in.read() << 8 | this.in.read());
        this.socket.setTdsVersion(this.tdsVersion);
        String product = this.in.readString(this.in.read());
        if (this.tdsVersion >= 3) {
            major = this.in.read();
            minor = this.in.read();
            build = this.in.read() << 8;
            build += this.in.read();
        } else {
            if (product.toLowerCase().startsWith("microsoft")) {
                this.in.skip(1);
                major = this.in.read();
                minor = this.in.read();
            } else {
                major = this.in.read();
                minor = this.in.read() * 10;
                minor += this.in.read();
            }
            this.in.skip(1);
        }
        if (product.length() > 1 && -1 != product.indexOf(0)) {
            product = product.substring(0, product.indexOf(0));
        }
        this.connection.setDBServerInfo(product, major, minor, build);
        if (this.tdsVersion == 2 && ack != 5) {
            this.messages.addDiagnostic(4002, 0, 14, "Login failed", "", "", 0);
            this.currentToken.token = (byte)-86;
        } else {
            this.messages.clearWarnings();
            for (SQLException ex = this.messages.exceptions; ex != null; ex = ex.getNextException()) {
                this.messages.addWarning(new SQLWarning(ex.getMessage(), ex.getSQLState(), ex.getErrorCode()));
            }
            this.messages.exceptions = null;
        }
    }

    private void tdsControlToken() throws IOException {
        short pktLen = this.in.readShort();
        this.in.skip(pktLen);
    }

    private void tdsRowToken() throws IOException, ProtocolException {
        for (int i = 0; i < this.columns.length; ++i) {
            this.rowData[i] = TdsData.readData(this.connection, this.in, this.columns[i]);
        }
        this.endOfResults = false;
    }

    private void tds5ParamsToken() throws IOException, ProtocolException, SQLException {
        if (this.currentToken.dynamParamInfo == null) {
            throw new ProtocolException("TDS 5 Param results token (0xD7) not preceded by param format (0xEC or 0X20).");
        }
        block0: for (int i = 0; i < this.currentToken.dynamParamData.length; ++i) {
            this.currentToken.dynamParamData[i] = TdsData.readData(this.connection, this.in, this.currentToken.dynamParamInfo[i]);
            String name = this.currentToken.dynamParamInfo[i].realName;
            if (this.parameters == null || name.length() != 0 && !name.startsWith("@")) continue;
            while (++this.nextParam < this.parameters.length) {
                if (!this.parameters[this.nextParam].isOutput) continue;
                Object value = this.currentToken.dynamParamData[i];
                if (value != null) {
                    this.parameters[this.nextParam].setOutValue(Support.convert(this.connection, value, this.parameters[this.nextParam].jdbcType, this.connection.getCharset()));
                    continue block0;
                }
                this.parameters[this.nextParam].setOutValue(null);
                continue block0;
            }
        }
    }

    private void tdsCapabilityToken() throws IOException, ProtocolException {
        this.in.readShort();
        if (this.in.read() != 1) {
            throw new ProtocolException("TDS_CAPABILITY: expected request string");
        }
        int capLen = this.in.read();
        if (capLen != 11 && capLen != 0) {
            throw new ProtocolException("TDS_CAPABILITY: byte count not 11");
        }
        byte[] capRequest = new byte[11];
        if (capLen == 0) {
            Logger.println("TDS_CAPABILITY: Invalid request length");
        } else {
            this.in.read(capRequest);
        }
        if (this.in.read() != 2) {
            throw new ProtocolException("TDS_CAPABILITY: expected response string");
        }
        capLen = this.in.read();
        if (capLen != 10 && capLen != 0) {
            throw new ProtocolException("TDS_CAPABILITY: byte count not 10");
        }
        byte[] capResponse = new byte[10];
        if (capLen == 0) {
            Logger.println("TDS_CAPABILITY: Invalid response length");
        } else {
            this.in.read(capResponse);
        }
        int capMask = 0;
        if ((capRequest[0] & 2) == 2) {
            capMask |= 0x20;
        }
        if ((capRequest[1] & 3) == 3) {
            capMask |= 2;
        }
        if ((capRequest[2] & 0x80) == 128) {
            capMask |= 0x10;
        }
        if ((capRequest[3] & 2) == 2) {
            capMask |= 8;
        }
        if ((capRequest[2] & 1) == 1) {
            capMask |= 0x40;
        }
        if ((capRequest[4] & 4) == 4) {
            capMask |= 4;
        }
        if ((capRequest[7] & 0x30) == 48) {
            capMask |= 1;
        }
        this.connection.setSybaseInfo(capMask);
    }

    private void tdsEnvChangeToken() throws IOException, SQLException {
        short len = this.in.readShort();
        int type = this.in.read();
        switch (type) {
            case 1: {
                int clen = this.in.read();
                String newDb = this.in.readString(clen);
                clen = this.in.read();
                String oldDb = this.in.readString(clen);
                this.connection.setDatabase(newDb, oldDb);
                break;
            }
            case 2: {
                int clen = this.in.read();
                String language = this.in.readString(clen);
                clen = this.in.read();
                String oldLang = this.in.readString(clen);
                if (!Logger.isActive()) break;
                Logger.println("Language changed from " + oldLang + " to " + language);
                break;
            }
            case 3: {
                int clen = this.in.read();
                String charset = this.in.readString(clen);
                if (this.tdsVersion >= 3) {
                    this.in.skip(len - 2 - clen * 2);
                } else {
                    this.in.skip(len - 2 - clen);
                }
                this.connection.setServerCharset(charset);
                break;
            }
            case 4: {
                int clen = this.in.read();
                int blocksize = Integer.parseInt(this.in.readString(clen));
                if (this.tdsVersion >= 3) {
                    this.in.skip(len - 2 - clen * 2);
                } else {
                    this.in.skip(len - 2 - clen);
                }
                this.connection.setNetPacketSize(blocksize);
                this.out.setBufferSize(blocksize);
                if (!Logger.isActive()) break;
                Logger.println("Changed blocksize to " + blocksize);
                break;
            }
            case 5: {
                this.in.skip(len - 1);
                break;
            }
            case 7: {
                int clen = this.in.read();
                byte[] collation = new byte[5];
                if (clen == 5) {
                    this.in.read(collation);
                    this.connection.setCollation(collation);
                } else {
                    this.in.skip(clen);
                }
                clen = this.in.read();
                this.in.skip(clen);
                break;
            }
            default: {
                if (Logger.isActive()) {
                    Logger.println("Unknown environment change type 0x" + Integer.toHexString(type));
                }
                this.in.skip(len - 1);
            }
        }
    }

    private void tds5ErrorToken() throws IOException {
        short pktLen = this.in.readShort();
        int sizeSoFar = 6;
        int number = this.in.readInt();
        int state = this.in.read();
        int severity = this.in.read();
        int stateLen = this.in.read();
        this.in.readNonUnicodeString(stateLen);
        this.in.read();
        this.in.readShort();
        sizeSoFar += 4 + stateLen;
        short msgLen = this.in.readShort();
        String message = this.in.readNonUnicodeString(msgLen);
        sizeSoFar += 2 + msgLen;
        int srvNameLen = this.in.read();
        String server = this.in.readNonUnicodeString(srvNameLen);
        sizeSoFar += 1 + srvNameLen;
        int procNameLen = this.in.read();
        String procName = this.in.readNonUnicodeString(procNameLen);
        sizeSoFar += 1 + procNameLen;
        short line = this.in.readShort();
        if (pktLen - (sizeSoFar += 2) > 0) {
            this.in.skip(pktLen - sizeSoFar);
        }
        if (severity > 10) {
            this.messages.addDiagnostic(number, state, severity, message, server, procName, line);
        } else {
            this.messages.addDiagnostic(number, state, severity, message, server, procName, line);
        }
    }

    private void tds5DynamicToken() throws IOException {
        int pktLen = this.in.readShort();
        byte type = (byte)this.in.read();
        this.in.read();
        pktLen -= 2;
        if (type == 32) {
            int len = this.in.read();
            this.in.skip(len);
            pktLen -= len + 1;
        }
        this.in.skip(pktLen);
    }

    private void tds5ParamFmtToken() throws IOException, ProtocolException {
        this.in.readShort();
        int paramCnt = this.in.readShort();
        ColInfo[] params = new ColInfo[paramCnt];
        for (int i = 0; i < paramCnt; ++i) {
            ColInfo col = new ColInfo();
            int colNameLen = this.in.read();
            col.realName = this.in.readNonUnicodeString(colNameLen);
            int column_flags = this.in.read();
            col.isCaseSensitive = false;
            col.nullable = (column_flags & 0x20) != 0 ? 1 : 0;
            col.isWriteable = (column_flags & 0x10) != 0;
            col.isIdentity = (column_flags & 0x40) != 0;
            col.isKey = (column_flags & 2) != 0;
            col.isHidden = (column_flags & 1) != 0;
            col.userType = this.in.readInt();
            if ((byte)this.in.peek() == -3) {
                this.currentToken.dynamParamInfo = null;
                this.currentToken.dynamParamData = null;
                this.messages.addDiagnostic(9999, 0, 16, "Prepare failed", "", "", 0);
                return;
            }
            TdsData.readType(this.in, col);
            this.in.skip(1);
            params[i] = col;
        }
        this.currentToken.dynamParamInfo = params;
        this.currentToken.dynamParamData = new Object[paramCnt];
    }

    private void tdsNtlmAuthToken() throws IOException, ProtocolException {
        short hdrLen;
        short pktLen = this.in.readShort();
        if (pktLen < (hdrLen = 40)) {
            throw new ProtocolException("NTLM challenge: packet is too small:" + pktLen);
        }
        this.ntlmMessage = new byte[pktLen];
        this.in.read(this.ntlmMessage);
        int seq = TdsCore.getIntFromBuffer(this.ntlmMessage, 8);
        if (seq != 2) {
            throw new ProtocolException("NTLM challenge: got unexpected sequence number:" + seq);
        }
        int flags = TdsCore.getIntFromBuffer(this.ntlmMessage, 20);
        int headerOffset = 40;
        int size = TdsCore.getShortFromBuffer(this.ntlmMessage, 40);
        int offset = TdsCore.getIntFromBuffer(this.ntlmMessage, 44);
        this.ntlmTarget = new byte[size];
        System.arraycopy(this.ntlmMessage, offset, this.ntlmTarget, 0, size);
        this.nonce = new byte[8];
        System.arraycopy(this.ntlmMessage, 24, this.nonce, 0, 8);
    }

    private static int getIntFromBuffer(byte[] buf, int offset) {
        int b1 = buf[offset] & 0xFF;
        int b2 = (buf[offset + 1] & 0xFF) << 8;
        int b3 = (buf[offset + 2] & 0xFF) << 16;
        int b4 = (buf[offset + 3] & 0xFF) << 24;
        return b4 | b3 | b2 | b1;
    }

    private static int getShortFromBuffer(byte[] buf, int offset) {
        int b1 = buf[offset] & 0xFF;
        int b2 = (buf[offset + 1] & 0xFF) << 8;
        return b2 | b1;
    }

    private void tds5ResultToken() throws IOException, ProtocolException {
        this.in.readShort();
        int colCnt = this.in.readShort();
        this.columns = new ColInfo[colCnt];
        this.rowData = new Object[colCnt];
        this.tables = null;
        for (int colNum = 0; colNum < colCnt; ++colNum) {
            ColInfo col = new ColInfo();
            int colNameLen = this.in.read();
            col.name = col.realName = this.in.readNonUnicodeString(colNameLen);
            int column_flags = this.in.read();
            col.isCaseSensitive = false;
            col.nullable = (column_flags & 0x20) != 0 ? 1 : 0;
            col.isWriteable = (column_flags & 0x10) != 0;
            col.isIdentity = (column_flags & 0x40) != 0;
            col.isKey = (column_flags & 2) != 0;
            col.isHidden = (column_flags & 1) != 0;
            col.userType = this.in.readInt();
            TdsData.readType(this.in, col);
            this.in.skip(1);
            this.columns[colNum] = col;
        }
        this.endOfResults = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tdsDoneToken() throws IOException {
        this.currentToken.status = (byte)this.in.read();
        this.in.skip(1);
        this.currentToken.operation = (byte)this.in.read();
        this.in.skip(1);
        this.currentToken.updateCount = this.in.readInt();
        if (!this.endOfResults) {
            this.currentToken.status = (byte)(this.currentToken.status & 0xFFFFFFEF);
            this.endOfResults = true;
        }
        if ((this.currentToken.status & 0x20) != 0) {
            int[] nArray = this.cancelMonitor;
            synchronized (this.cancelMonitor) {
                this.cancelPending = false;
                if (this.cancelMonitor[0] == 0) {
                    this.messages.addException(new SQLException(Messages.get("error.generic.cancelled", "Statement"), "HY008"));
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
            }
        } else if (!this._ErrorReceived && (this.currentToken.status & 2) != 0) {
            this.messages.addException(new SQLException(Messages.get("error.generic.unspecified"), "HY000"));
        }
        {
            this._ErrorReceived = false;
            if ((this.currentToken.status & 1) == 0) {
                boolean bl = this.endOfResponse = !this.cancelPending;
                if (this.fatalError) {
                    this.connection.setClosed();
                }
            }
            if (this.serverType == 1 && this.currentToken.operation == -63) {
                this.currentToken.status = (byte)(this.currentToken.status & 0xFFFFFFEF);
            }
            return;
        }
    }

    private void executeSQL42(String sql, String procName, ParamInfo[] parameters, boolean noMetaData, boolean sendNow) throws IOException, SQLException {
        if (procName != null) {
            this.out.setPacketType((byte)3);
            byte[] buf = Support.encodeString(this.connection.getCharset(), procName);
            this.out.write((byte)buf.length);
            this.out.write(buf);
            this.out.write((short)(noMetaData ? 2 : 0));
            if (parameters != null) {
                for (int i = this.nextParam + 1; i < parameters.length; ++i) {
                    if (parameters[i].name != null) {
                        buf = Support.encodeString(this.connection.getCharset(), parameters[i].name);
                        this.out.write((byte)buf.length);
                        this.out.write(buf);
                    } else {
                        this.out.write((byte)0);
                    }
                    this.out.write((byte)(parameters[i].isOutput ? 1 : 0));
                    TdsData.writeParam(this.out, this.connection.getCharsetInfo(), null, parameters[i]);
                }
            }
            if (!sendNow) {
                this.out.write((byte)-128);
            }
        } else if (sql.length() > 0) {
            if (parameters != null) {
                sql = Support.substituteParameters(sql, parameters, this.connection);
            }
            this.out.setPacketType((byte)1);
            this.out.write(sql);
            if (!sendNow) {
                this.out.write(" ");
            }
        }
    }

    private void executeSQL50(String sql, String procName, ParamInfo[] parameters) throws IOException, SQLException {
        boolean haveParams = parameters != null;
        boolean useParamNames = false;
        this.currentToken.dynamParamInfo = null;
        this.currentToken.dynamParamData = null;
        for (int i = 0; haveParams && i < parameters.length; ++i) {
            if (!"text".equals(parameters[i].sqlType) && !"image".equals(parameters[i].sqlType) && !"unitext".equals(parameters[i].sqlType)) continue;
            if (procName != null && procName.length() > 0) {
                if ("text".equals(parameters[i].sqlType) || "unitext".equals(parameters[i].sqlType)) {
                    throw new SQLException(Messages.get("error.chartoolong"), "HY000");
                }
                throw new SQLException(Messages.get("error.bintoolong"), "HY000");
            }
            if (parameters[i].tdsType == 36) continue;
            sql = Support.substituteParameters(sql, parameters, this.connection);
            haveParams = false;
            procName = null;
            break;
        }
        this.out.setPacketType((byte)15);
        if (procName == null) {
            this.out.write((byte)33);
            if (haveParams) {
                sql = Support.substituteParamMarkers(sql, parameters);
            }
            if (this.connection.isWideChar()) {
                byte[] buf = Support.encodeString(this.connection.getCharset(), sql);
                this.out.write(buf.length + 1);
                this.out.write((byte)(haveParams ? 1 : 0));
                this.out.write(buf);
            } else {
                this.out.write(sql.length() + 1);
                this.out.write((byte)(haveParams ? 1 : 0));
                this.out.write(sql);
            }
        } else if (procName.startsWith("#jtds")) {
            this.out.write((byte)-25);
            this.out.write((short)(procName.length() + 4));
            this.out.write((byte)2);
            this.out.write((byte)(haveParams ? 1 : 0));
            this.out.write((byte)(procName.length() - 1));
            this.out.write(procName.substring(1));
            this.out.write((short)0);
        } else {
            byte[] buf = Support.encodeString(this.connection.getCharset(), procName);
            this.out.write((byte)-26);
            this.out.write((short)(buf.length + 3));
            this.out.write((byte)buf.length);
            this.out.write(buf);
            this.out.write((short)(haveParams ? 2 : 0));
            useParamNames = true;
        }
        if (haveParams) {
            int i;
            this.out.write((byte)-20);
            int len = 2;
            for (i = this.nextParam + 1; i < parameters.length; ++i) {
                len += TdsData.getTds5ParamSize(this.connection.getCharset(), this.connection.isWideChar(), parameters[i], useParamNames);
            }
            this.out.write((short)len);
            this.out.write((short)(this.nextParam < 0 ? parameters.length : parameters.length - 1));
            for (i = this.nextParam + 1; i < parameters.length; ++i) {
                TdsData.writeTds5ParamFmt(this.out, this.connection.getCharset(), this.connection.isWideChar(), parameters[i], useParamNames);
            }
            this.out.write((byte)-41);
            for (i = this.nextParam + 1; i < parameters.length; ++i) {
                TdsData.writeTds5Param(this.out, this.connection.getCharsetInfo(), parameters[i]);
            }
        }
    }

    public static boolean isPreparedProcedureName(String procName) {
        return procName != null && procName.length() > 0 && Character.isDigit(procName.charAt(0));
    }

    private void executeSQL70(String sql, String procName, ParamInfo[] parameters, boolean noMetaData, boolean sendNow) throws IOException, SQLException {
        ParamInfo[] params;
        int prepareSql = this.connection.getPrepareSql();
        if (parameters == null && prepareSql == 2) {
            prepareSql = 0;
        }
        if (this.inBatch) {
            prepareSql = 2;
        }
        if (procName == null) {
            if (parameters != null) {
                if (prepareSql == 0) {
                    sql = Support.substituteParameters(sql, parameters, this.connection);
                } else {
                    params = new ParamInfo[2 + parameters.length];
                    System.arraycopy(parameters, 0, params, 2, parameters.length);
                    params[0] = new ParamInfo(-1, Support.substituteParamMarkers(sql, parameters), 4);
                    TdsData.getNativeType(this.connection, params[0]);
                    params[1] = new ParamInfo(-1, Support.getParameterDefinitions(parameters), 4);
                    TdsData.getNativeType(this.connection, params[1]);
                    parameters = params;
                    procName = "sp_executesql";
                }
            }
        } else if (TdsCore.isPreparedProcedureName(procName)) {
            if (parameters != null) {
                params = new ParamInfo[1 + parameters.length];
                System.arraycopy(parameters, 0, params, 1, parameters.length);
            } else {
                params = new ParamInfo[]{new ParamInfo(4, new Integer(procName), 0)};
            }
            TdsData.getNativeType(this.connection, params[0]);
            parameters = params;
            procName = "sp_execute";
        }
        if (procName != null) {
            Integer shortcut;
            this.out.setPacketType((byte)3);
            if (this.tdsVersion >= 4 && (shortcut = (Integer)tds8SpNames.get(procName)) != null) {
                this.out.write((short)-1);
                this.out.write(shortcut.shortValue());
            } else {
                this.out.write((short)procName.length());
                this.out.write(procName);
            }
            this.out.write((short)(noMetaData ? 2 : 0));
            if (parameters != null) {
                for (int i = this.nextParam + 1; i < parameters.length; ++i) {
                    if (parameters[i].name != null) {
                        this.out.write((byte)parameters[i].name.length());
                        this.out.write(parameters[i].name);
                    } else {
                        this.out.write((byte)0);
                    }
                    this.out.write((byte)(parameters[i].isOutput ? 1 : 0));
                    TdsData.writeParam(this.out, this.connection.getCharsetInfo(), this.connection.getCollation(), parameters[i]);
                }
            }
            if (!sendNow) {
                this.out.write((byte)-128);
            }
        } else if (sql.length() > 0) {
            this.out.setPacketType((byte)1);
            this.out.write(sql);
            if (!sendNow) {
                this.out.write(" ");
            }
        }
    }

    private void setRowCountAndTextSize(int rowCount, int textSize) throws SQLException {
        boolean newTextSize;
        boolean newRowCount = rowCount >= 0 && rowCount != this.connection.getRowCount();
        boolean bl = newTextSize = textSize >= 0 && textSize != this.connection.getTextSize();
        if (newRowCount || newTextSize) {
            try {
                StringBuilder query = new StringBuilder(64);
                if (newRowCount) {
                    query.append("SET ROWCOUNT ").append(rowCount);
                }
                if (newTextSize) {
                    query.append(" SET TEXTSIZE ").append(textSize == 0 ? Integer.MAX_VALUE : textSize);
                }
                this.out.setPacketType((byte)1);
                this.out.write(query.toString());
                this.out.flush();
                this.endOfResponse = false;
                this.endOfResults = true;
                this.wait(0);
                this.clearResponseQueue();
                this.messages.checkErrors();
                this.connection.setRowCount(rowCount);
                this.connection.setTextSize(textSize);
            }
            catch (IOException ioe) {
                throw new SQLException(Messages.get("error.generic.ioerror", ioe.getMessage()), "08S01");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void wait(int timeOut) throws IOException, SQLException {
        Object timer = null;
        try {
            if (timeOut > 0) {
                timer = TimerThread.getInstance().setTimer(timeOut * 1000, new TimerThread.TimerListener(){

                    @Override
                    public void timerExpired() {
                        TdsCore.this.cancel(true);
                    }
                });
            }
            this.in.peek();
        }
        finally {
            if (timer != null && !TimerThread.getInstance().cancelTimer(timer)) {
                throw new SQLTimeoutException(Messages.get("error.generic.timeout"), "HYT00");
            }
        }
    }

    public void cleanUp() {
        if (this.endOfResponse) {
            this.returnParam = null;
            this.parameters = null;
            this.columns = null;
            this.rowData = null;
            this.tables = null;
            this.computedColumns = null;
            this.computedRowData = null;
            this.messages.clearWarnings();
        }
    }

    public SQLDiagnostic getMessages() {
        return this.messages;
    }

    private static byte[] getMACAddress(String macString) {
        byte[] mac = new byte[6];
        boolean ok = false;
        if (macString != null && macString.length() == 12) {
            try {
                int i = 0;
                int j = 0;
                while (i < 6) {
                    mac[i] = (byte)Integer.parseInt(macString.substring(j, j + 2), 16);
                    ++i;
                    j += 2;
                }
                ok = true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (!ok) {
            Arrays.fill(mac, (byte)0);
        }
        return mac;
    }

    private static String getHostName() {
        String name;
        if (hostName != null) {
            return hostName;
        }
        try {
            name = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
            return hostName;
        }
        int pos = name.indexOf(46);
        if (pos >= 0) {
            name = name.substring(0, pos);
        }
        if (name.length() == 0) {
            hostName = "UNKNOWN";
            return hostName;
        }
        try {
            Integer.parseInt(name);
            hostName = "UNKNOWN";
            return hostName;
        }
        catch (NumberFormatException numberFormatException) {
            hostName = name;
            return name;
        }
    }

    private static String tds7CryptPass(String pw) {
        int xormask = 23130;
        int len = pw.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; ++i) {
            int c = pw.charAt(i) ^ 0x5A5A;
            int m1 = c >> 4 & 0xF0F;
            int m2 = c << 4 & 0xF0F0;
            chars[i] = (char)(m1 | m2);
        }
        return new String(chars);
    }

    private void tdsComputedResultToken() throws IOException, ProtocolException {
        int ciolumns = this.in.readShort();
        this.computedColumns = new ColInfo[ciolumns];
        short id = this.in.readShort();
        int computeByCount = this.in.read();
        this.in.skip(2 * computeByCount);
        for (int i = 0; i < ciolumns; ++i) {
            ColInfo col;
            this.computedColumns[i] = col = new ColInfo();
            int type = this.in.read();
            switch (type) {
                case 9: {
                    col.name = "count_big";
                    break;
                }
                case 48: {
                    col.name = "stdev";
                    break;
                }
                case 49: {
                    col.name = "stdevp";
                    break;
                }
                case 50: {
                    col.name = "var";
                    break;
                }
                case 51: {
                    col.name = "varp";
                    break;
                }
                case 75: {
                    col.name = "count";
                    break;
                }
                case 77: {
                    col.name = "sum";
                    break;
                }
                case 79: {
                    col.name = "avg";
                    break;
                }
                case 81: {
                    col.name = "min";
                    break;
                }
                case 82: {
                    col.name = "max";
                    break;
                }
                default: {
                    throw new ProtocolException("unsupported aggregation type 0x" + Integer.toHexString(type));
                }
            }
            int columnIndex = this.in.readShort() - 1;
            col.realName = col.name = col.name + "(" + this.columns[columnIndex].name + ")";
            col.tableName = this.columns[columnIndex].tableName;
            col.catalog = this.columns[columnIndex].catalog;
            col.schema = this.columns[columnIndex].schema;
            col.userType = this.in.readShort();
            short flags = this.in.readShort();
            col.nullable = (flags & 1) != 0 ? 1 : 0;
            col.isCaseSensitive = (flags & 2) != 0;
            col.isIdentity = (flags & 0x10) != 0;
            col.isWriteable = (flags & 0xC) != 0;
            TdsData.readType(this.in, col);
            int clen = this.in.read();
            this.in.readString(clen);
        }
    }

    private void tdsComputedRowToken() throws IOException, ProtocolException, SQLException {
        short id = this.in.readShort();
        this.computedRowData = new Object[this.computedColumns.length];
        for (int i = 0; i < this.computedRowData.length; ++i) {
            this.computedRowData[i] = TdsData.readData(this.connection, this.in, this.computedColumns[i]);
        }
    }

    static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private byte[] createGssToken() throws GSSException, UnknownHostException {
        GSSManager manager = GSSManager.getInstance();
        Oid mech = new Oid("1.2.840.113554.1.2.2");
        Oid nameType = new Oid("1.2.840.113554.1.2.2.1");
        String host = InetAddress.getByName(this.socket.getHost()).getCanonicalHostName();
        int port = this.socket.getPort();
        GSSName serverName = manager.createName("MSSQLSvc/" + host + ":" + port, nameType);
        Logger.println("GSS: Using SPN " + serverName);
        this._gssContext = manager.createContext(serverName, mech, null, 0);
        this._gssContext.requestMutualAuth(true);
        byte[] token = this._gssContext.initSecContext(new byte[0], 0, 0);
        Logger.println("GSS: Created GSS token (length: " + token.length + ")");
        return token;
    }

    static {
        tds8SpNames.put("sp_cursor", new Integer(1));
        tds8SpNames.put("sp_cursoropen", new Integer(2));
        tds8SpNames.put("sp_cursorprepare", new Integer(3));
        tds8SpNames.put("sp_cursorexecute", new Integer(4));
        tds8SpNames.put("sp_cursorprepexec", new Integer(5));
        tds8SpNames.put("sp_cursorunprepare", new Integer(6));
        tds8SpNames.put("sp_cursorfetch", new Integer(7));
        tds8SpNames.put("sp_cursoroption", new Integer(8));
        tds8SpNames.put("sp_cursorclose", new Integer(9));
        tds8SpNames.put("sp_executesql", new Integer(10));
        tds8SpNames.put("sp_prepare", new Integer(11));
        tds8SpNames.put("sp_execute", new Integer(12));
        tds8SpNames.put("sp_prepexec", new Integer(13));
        tds8SpNames.put("sp_prepexecrpc", new Integer(14));
        tds8SpNames.put("sp_unprepare", new Integer(15));
    }

    private static class TableMetaData {
        String catalog;
        String schema;
        String name;

        private TableMetaData() {
        }
    }

    private static class TdsToken {
        byte token;
        byte status;
        byte operation;
        int updateCount;
        ColInfo[] dynamParamInfo;
        Object[] dynamParamData;

        private TdsToken() {
        }

        boolean isUpdateCount() {
            return (this.token == -3 || this.token == -1) && (this.status & 0x10) != 0;
        }

        boolean isEndToken() {
            return this.token == -3 || this.token == -1 || this.token == -2;
        }

        boolean isAuthToken() {
            return this.token == -19;
        }

        boolean resultsPending() {
            return !this.isEndToken() || (this.status & 1) != 0;
        }

        boolean isResultSet() {
            return this.token == -95 || this.token == -127 || this.token == -18 || this.token == 97 || this.token == -91 || this.token == -47 || this.token == -120 || this.token == -45;
        }

        public boolean isRowData() {
            return this.token == -47 || this.token == -45;
        }
    }
}

