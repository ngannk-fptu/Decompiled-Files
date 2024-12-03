/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import net.sourceforge.jtds.jdbc.CharsetInfo;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsDatabaseMetaData;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.MSSqlServerInfo;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.ProcEntry;
import net.sourceforge.jtds.jdbc.SQLDiagnostic;
import net.sourceforge.jtds.jdbc.SQLParser;
import net.sourceforge.jtds.jdbc.SavepointImpl;
import net.sourceforge.jtds.jdbc.Semaphore;
import net.sourceforge.jtds.jdbc.SharedLocalNamedPipe;
import net.sourceforge.jtds.jdbc.SharedNamedPipe;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsCore;
import net.sourceforge.jtds.jdbc.TdsData;
import net.sourceforge.jtds.jdbc.cache.ProcedureCache;
import net.sourceforge.jtds.jdbc.cache.StatementCache;
import net.sourceforge.jtds.util.Logger;
import net.sourceforge.jtds.util.TimerThread;

public class JtdsConnection
implements Connection {
    private static final String SYBASE_SERVER_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select value from master.dbo.sysconfigures where config=131)";
    private static final String SQL_SERVER_65_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select csid from master.dbo.syscharsets, master.dbo.sysconfigures where config=1123 and id = value)";
    private static final String SYBASE_INITIAL_SQL = "SET TRANSACTION ISOLATION LEVEL 1\r\nSET CHAINED OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    private static final String SQL_SERVER_INITIAL_SQL = "SELECT @@MAX_PRECISION\r\nSET TRANSACTION ISOLATION LEVEL READ COMMITTED\r\nSET IMPLICIT_TRANSACTIONS OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    public static final int TRANSACTION_SNAPSHOT = 4096;
    private final String url;
    private String serverName;
    private int portNumber;
    private int serverType;
    private String instanceName;
    private String databaseName;
    private String currentDatabase;
    private String domainName;
    private String user;
    private String password;
    private String serverCharset;
    private String appName;
    private String progName;
    private String wsid;
    private String language;
    private String macAddress;
    private int tdsVersion;
    private final SharedSocket socket;
    private final TdsCore baseTds;
    private int netPacketSize = 512;
    private int packetSize;
    private byte[] collation;
    private boolean charsetSpecified;
    private String databaseProductName;
    private String databaseProductVersion;
    private int databaseMajorVersion;
    private int databaseMinorVersion;
    private boolean closed;
    private boolean readOnly;
    private final ArrayList statements = new ArrayList();
    private int transactionIsolation = 2;
    private boolean autoCommit = true;
    private final SQLDiagnostic messages;
    private int rowCount;
    private int textSize;
    private int maxPrecision = 38;
    private int spSequenceNo = 1;
    private int cursorSequenceNo = 1;
    private final ArrayList procInTran = new ArrayList();
    private CharsetInfo charsetInfo;
    private int prepareSql;
    private long lobBuffer;
    private int maxStatements;
    private StatementCache statementCache;
    private boolean useUnicode = true;
    private boolean namedPipe;
    private boolean lastUpdateCount;
    private boolean tcpNoDelay = true;
    private int loginTimeout;
    private int sybaseInfo;
    private boolean xaTransaction;
    private int xaState;
    private Object xid;
    private boolean xaEmulation = true;
    private final Semaphore mutex = new Semaphore(1L);
    private int socketTimeout;
    private boolean socketKeepAlive;
    private static Integer processId;
    private String ssl;
    private int batchSize;
    private boolean useMetadataCache;
    private boolean useCursors;
    private File bufferDir;
    private int bufferMaxMemory;
    private int bufferMinPackets;
    private boolean useLOBs;
    private TdsCore cachedTds;
    private String bindAddress;
    private boolean useJCIFS;
    private boolean useNTLMv2 = false;
    private boolean useKerberos = false;
    private static int[] connections;
    private ArrayList savepoints;
    private Map savepointProcInTran;
    private int savepointId;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JtdsConnection() {
        int[] nArray = connections;
        synchronized (connections) {
            connections[0] = connections[0] + 1;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.url = null;
            this.socket = null;
            this.baseTds = null;
            this.messages = null;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    JtdsConnection(String url, Properties info) throws SQLException {
        int[] nArray = connections;
        synchronized (connections) {
            SQLWarning warn;
            block28: {
                Object timer;
                block27: {
                    connections[0] = connections[0] + 1;
                    // ** MonitorExit[var3_3] (shouldn't be in output)
                    this.url = url;
                    this.unpackProperties(info);
                    this.messages = new SQLDiagnostic(this.serverType);
                    if (this.instanceName.length() > 0 && !this.namedPipe) {
                        block26: {
                            try {
                                MSSqlServerInfo msInfo = new MSSqlServerInfo(this.serverName);
                                this.portNumber = msInfo.getPortForInstance(this.instanceName);
                            }
                            catch (SQLException e) {
                                if (this.portNumber > 0) break block26;
                                throw e;
                            }
                        }
                        if (this.portNumber == -1) {
                            throw new SQLException(Messages.get("error.msinfo.badinst", this.serverName, this.instanceName), "08003");
                        }
                    }
                    SharedSocket.setMemoryBudget(this.bufferMaxMemory * 1024);
                    SharedSocket.setMinMemPkts(this.bufferMinPackets);
                    timer = null;
                    boolean loginError = false;
                    try {
                        if (this.loginTimeout > 0) {
                            timer = TimerThread.getInstance().setTimer(this.loginTimeout * 1000, new TimerThread.TimerListener(){

                                @Override
                                public void timerExpired() {
                                    if (JtdsConnection.this.socket != null) {
                                        JtdsConnection.this.socket.forceClose();
                                    }
                                }
                            });
                        }
                        this.socket = this.namedPipe ? this.createNamedPipe(this) : new SharedSocket(this);
                        if (this.macAddress.equals("000000000000")) {
                            String mac = this.socket.getMAC();
                            String string = this.macAddress = mac != null ? mac : this.macAddress;
                        }
                        if (timer != null && TimerThread.getInstance().hasExpired(timer)) {
                            this.socket.forceClose();
                            throw new IOException("Login timed out");
                        }
                        if (this.charsetSpecified) {
                            this.loadCharset(this.serverCharset);
                        } else {
                            this.loadCharset("iso_1");
                            this.serverCharset = "";
                        }
                        this.baseTds = new TdsCore(this, this.messages);
                        if (this.tdsVersion >= 4 && !this.namedPipe) {
                            this.baseTds.negotiateSSL(this.instanceName, this.ssl);
                        }
                        this.baseTds.login(this.serverName, this.databaseName, this.user, this.password, this.domainName, this.serverCharset, this.appName, this.progName, this.wsid, this.language, this.macAddress, this.packetSize);
                        warn = this.messages.warnings;
                        this.tdsVersion = this.baseTds.getTdsVersion();
                        if (this.tdsVersion < 3 && this.databaseName.length() > 0) {
                            this.setCatalog(this.databaseName);
                        }
                        if ((this.serverCharset == null || this.serverCharset.length() == 0) && this.collation == null) {
                            this.loadCharset(this.determineServerCharset());
                        }
                        if (this.serverType == 2) {
                            this.baseTds.submitSQL(SYBASE_INITIAL_SQL);
                        } else {
                            Statement stmt = this.createStatement();
                            ResultSet rs = stmt.executeQuery(SQL_SERVER_INITIAL_SQL);
                            if (rs.next()) {
                                this.maxPrecision = rs.getByte(1);
                            }
                            rs.close();
                            stmt.close();
                        }
                        if (!loginError) break block27;
                    }
                    catch (UnknownHostException e) {
                        try {
                            loginError = true;
                            throw Support.linkException(new SQLException(Messages.get("error.connection.badhost", e.getMessage()), "08S03"), (Throwable)e);
                            catch (IOException e2) {
                                loginError = true;
                                if (this.loginTimeout <= 0) throw Support.linkException(new SQLException(Messages.get("error.connection.ioerror", e2.getMessage()), "08S01"), (Throwable)e2);
                                if (e2.getMessage().indexOf("timed out") < 0) throw Support.linkException(new SQLException(Messages.get("error.connection.ioerror", e2.getMessage()), "08S01"), (Throwable)e2);
                                throw Support.linkException(new SQLException(Messages.get("error.connection.timeout"), "HYT01"), (Throwable)e2);
                            }
                            catch (SQLException e3) {
                                loginError = true;
                                if (this.loginTimeout <= 0) throw e3;
                                if (e3.getMessage().indexOf("socket closed") < 0) throw e3;
                                throw Support.linkException(new SQLException(Messages.get("error.connection.timeout"), "HYT01"), (Throwable)e3);
                            }
                            catch (RuntimeException e4) {
                                loginError = true;
                                throw e4;
                            }
                        }
                        catch (Throwable throwable) {
                            if (loginError) {
                                this.close();
                                throw throwable;
                            }
                            if (timer == null) throw throwable;
                            TimerThread.getInstance().cancelTimer(timer);
                            throw throwable;
                        }
                    }
                    this.close();
                    break block28;
                }
                if (timer != null) {
                    TimerThread.getInstance().cancelTimer(timer);
                }
            }
            this.messages.warnings = warn;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }

    private SharedSocket createNamedPipe(JtdsConnection connection) throws IOException {
        long loginTimeout = connection.getLoginTimeout();
        long retryTimeout = (loginTimeout > 0L ? loginTimeout : 20L) * 1000L;
        long startLoginTimeout = System.currentTimeMillis();
        Random random = new Random(startLoginTimeout);
        boolean isWindowsOS = Support.isWindowsOS();
        SharedSocket socket = null;
        IOException lastIOException = null;
        int exceptionCount = 0;
        do {
            try {
                if (isWindowsOS && !connection.getUseJCIFS()) {
                    socket = new SharedLocalNamedPipe(connection);
                    continue;
                }
                socket = new SharedNamedPipe(connection);
            }
            catch (IOException ioe) {
                ++exceptionCount;
                lastIOException = ioe;
                if (ioe.getMessage().toLowerCase().indexOf("all pipe instances are busy") >= 0) {
                    int randomWait = random.nextInt(800) + 200;
                    if (Logger.isActive()) {
                        Logger.println("Retry #" + exceptionCount + " Wait " + randomWait + " ms: " + ioe.getMessage());
                    }
                    try {
                        Thread.sleep(randomWait);
                    }
                    catch (InterruptedException ie) {}
                    continue;
                }
                throw ioe;
            }
        } while (socket == null && System.currentTimeMillis() - startLoginTimeout < retryTimeout);
        if (socket == null) {
            IOException ioException = new IOException("Connection timed out to named pipe");
            Support.linkException(ioException, (Throwable)lastIOException);
            throw ioException;
        }
        return socket;
    }

    SharedSocket getSocket() {
        return this.socket;
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    String getProcName() {
        String seq = "000000" + Integer.toHexString(this.spSequenceNo++).toUpperCase();
        return "#jtds" + seq.substring(seq.length() - 6, seq.length());
    }

    synchronized String getCursorName() {
        String seq = "000000" + Integer.toHexString(this.cursorSequenceNo++).toUpperCase();
        return "_jtds" + seq.substring(seq.length() - 6, seq.length());
    }

    synchronized String prepareSQL(JtdsPreparedStatement pstmt, String sql, ParamInfo[] params, boolean returnKeys, boolean cursorNeeded) throws SQLException {
        if (this.prepareSql == 0 || this.prepareSql == 2) {
            return null;
        }
        if (this.serverType == 2) {
            if (this.tdsVersion != 2) {
                return null;
            }
            if (returnKeys) {
                return null;
            }
            if (cursorNeeded) {
                return null;
            }
        }
        for (int i = 0; i < params.length; ++i) {
            if (!params[i].isSet) {
                throw new SQLException(Messages.get("error.prepare.paramnotset", Integer.toString(i + 1)), "07000");
            }
            TdsData.getNativeType(this, params[i]);
            if (this.serverType != 2 || !"text".equals(params[i].sqlType) && !"image".equals(params[i].sqlType)) continue;
            return null;
        }
        String key = Support.getStatementKey(sql, params, this.serverType, this.getCatalog(), this.autoCommit, cursorNeeded);
        ProcEntry proc = (ProcEntry)this.statementCache.get(key);
        if (proc != null) {
            if (pstmt.handles != null && pstmt.handles.contains(proc)) {
                proc.release();
            }
            pstmt.setColMetaData(proc.getColMetaData());
            if (this.serverType == 2) {
                pstmt.setParamMetaData(proc.getParamMetaData());
            }
        } else {
            proc = new ProcEntry();
            if (this.serverType == 1) {
                proc.setName(this.baseTds.microsoftPrepare(sql, params, cursorNeeded, pstmt.getResultSetType(), pstmt.getResultSetConcurrency()));
                if (proc.toString() == null) {
                    proc.setType(4);
                } else if (this.prepareSql == 1) {
                    proc.setType(1);
                } else {
                    proc.setType(cursorNeeded ? 3 : 2);
                    proc.setColMetaData(this.baseTds.getColumns());
                    pstmt.setColMetaData(proc.getColMetaData());
                }
            } else {
                proc.setName(this.baseTds.sybasePrepare(sql, params));
                if (proc.toString() == null) {
                    proc.setType(4);
                } else {
                    proc.setType(1);
                }
                proc.setColMetaData(this.baseTds.getColumns());
                proc.setParamMetaData(this.baseTds.getParameters());
                pstmt.setColMetaData(proc.getColMetaData());
                pstmt.setParamMetaData(proc.getParamMetaData());
            }
            this.addCachedProcedure(key, proc);
        }
        if (pstmt.handles == null) {
            pstmt.handles = new HashSet(10);
        }
        pstmt.handles.add(proc);
        return proc.toString();
    }

    void addCachedProcedure(String key, ProcEntry proc) {
        this.statementCache.put(key, proc);
        if (!this.autoCommit && proc.getType() == 1 && this.serverType == 1) {
            this.procInTran.add(key);
        }
        if (this.getServerType() == 1 && proc.getType() == 1) {
            this.addCachedProcedure(key);
        }
    }

    void removeCachedProcedure(String key) {
        this.statementCache.remove(key);
        if (!this.autoCommit) {
            this.procInTran.remove(key);
        }
    }

    int getMaxStatements() {
        return this.maxStatements;
    }

    public int getServerType() {
        return this.serverType;
    }

    void setNetPacketSize(int size) {
        this.netPacketSize = size;
    }

    int getNetPacketSize() {
        return this.netPacketSize;
    }

    int getRowCount() {
        return this.rowCount;
    }

    void setRowCount(int count) {
        this.rowCount = count;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    boolean getLastUpdateCount() {
        return this.lastUpdateCount;
    }

    int getMaxPrecision() {
        return this.maxPrecision;
    }

    long getLobBuffer() {
        return this.lobBuffer;
    }

    int getPrepareSql() {
        return this.prepareSql;
    }

    int getBatchSize() {
        return this.batchSize;
    }

    boolean getUseMetadataCache() {
        return this.useMetadataCache;
    }

    boolean getUseCursors() {
        return this.useCursors;
    }

    boolean getUseLOBs() {
        return this.useLOBs;
    }

    boolean getUseNTLMv2() {
        return this.useNTLMv2;
    }

    boolean getUseKerberos() {
        return this.useKerberos;
    }

    String getAppName() {
        return this.appName;
    }

    String getBindAddress() {
        return this.bindAddress;
    }

    File getBufferDir() {
        return this.bufferDir;
    }

    int getBufferMaxMemory() {
        return this.bufferMaxMemory;
    }

    int getBufferMinPackets() {
        return this.bufferMinPackets;
    }

    String getDatabaseName() {
        return this.databaseName;
    }

    String getDomainName() {
        return this.domainName;
    }

    String getInstanceName() {
        return this.instanceName;
    }

    int getLoginTimeout() {
        return this.loginTimeout;
    }

    int getSocketTimeout() {
        return this.socketTimeout;
    }

    boolean getSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    int getProcessId() {
        return processId;
    }

    String getMacAddress() {
        return this.macAddress;
    }

    boolean getNamedPipe() {
        return this.namedPipe;
    }

    int getPacketSize() {
        return this.packetSize;
    }

    String getPassword() {
        return this.password;
    }

    int getPortNumber() {
        return this.portNumber;
    }

    String getProgName() {
        return this.progName;
    }

    String getServerName() {
        return this.serverName;
    }

    boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    boolean getUseJCIFS() {
        return this.useJCIFS;
    }

    String getUser() {
        return this.user;
    }

    String getWsid() {
        return this.wsid;
    }

    protected void unpackProperties(Properties info) throws SQLException {
        Integer parsedTdsVersion;
        this.serverName = info.getProperty(Messages.get("prop.servername"));
        this.portNumber = JtdsConnection.parseIntegerProperty(info, "prop.portnumber");
        this.serverType = JtdsConnection.parseIntegerProperty(info, "prop.servertype");
        this.databaseName = info.getProperty(Messages.get("prop.databasename"));
        this.instanceName = info.getProperty(Messages.get("prop.instance"));
        this.domainName = info.getProperty(Messages.get("prop.domain"));
        this.user = info.getProperty(Messages.get("prop.user"));
        this.password = info.getProperty(Messages.get("prop.password"));
        this.macAddress = info.getProperty(Messages.get("prop.macaddress"));
        this.appName = info.getProperty(Messages.get("prop.appname"));
        this.progName = info.getProperty(Messages.get("prop.progname"));
        this.wsid = info.getProperty(Messages.get("prop.wsid"));
        this.serverCharset = info.getProperty(Messages.get("prop.charset"));
        this.language = info.getProperty(Messages.get("prop.language"));
        this.bindAddress = info.getProperty(Messages.get("prop.bindaddress"));
        this.lastUpdateCount = JtdsConnection.parseBooleanProperty(info, "prop.lastupdatecount");
        this.useUnicode = JtdsConnection.parseBooleanProperty(info, "prop.useunicode");
        this.namedPipe = JtdsConnection.parseBooleanProperty(info, "prop.namedpipe");
        this.tcpNoDelay = JtdsConnection.parseBooleanProperty(info, "prop.tcpnodelay");
        this.useCursors = this.serverType == 1 && JtdsConnection.parseBooleanProperty(info, "prop.usecursors");
        this.useLOBs = JtdsConnection.parseBooleanProperty(info, "prop.uselobs");
        this.useMetadataCache = JtdsConnection.parseBooleanProperty(info, "prop.cachemetadata");
        this.xaEmulation = JtdsConnection.parseBooleanProperty(info, "prop.xaemulation");
        this.useJCIFS = JtdsConnection.parseBooleanProperty(info, "prop.usejcifs");
        this.charsetSpecified = this.serverCharset.length() > 0;
        this.useNTLMv2 = JtdsConnection.parseBooleanProperty(info, "prop.usentlmv2");
        this.useKerberos = JtdsConnection.parseBooleanProperty(info, "prop.usekerberos");
        if (this.domainName != null) {
            this.domainName = this.domainName.toUpperCase();
        }
        if ((parsedTdsVersion = DefaultProperties.getTdsVersion(info.getProperty(Messages.get("prop.tds")))) == null) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get("prop.tds")), "08001");
        }
        this.tdsVersion = parsedTdsVersion;
        this.packetSize = JtdsConnection.parseIntegerProperty(info, "prop.packetsize");
        if (this.packetSize < 512) {
            if (this.tdsVersion >= 3) {
                this.packetSize = this.packetSize == 0 ? 0 : 4096;
            } else if (this.tdsVersion == 1) {
                this.packetSize = 512;
            }
        }
        if (this.packetSize > 32768) {
            this.packetSize = 32768;
        }
        this.packetSize = this.packetSize / 512 * 512;
        this.loginTimeout = JtdsConnection.parseIntegerProperty(info, "prop.logintimeout");
        this.socketTimeout = JtdsConnection.parseIntegerProperty(info, "prop.sotimeout");
        this.socketKeepAlive = JtdsConnection.parseBooleanProperty(info, "prop.sokeepalive");
        this.autoCommit = JtdsConnection.parseBooleanProperty(info, "prop.autocommit");
        String pid = info.getProperty(Messages.get("prop.processid"));
        if ("compute".equals(pid)) {
            if (processId == null) {
                processId = new Integer(new Random(System.currentTimeMillis()).nextInt(32768));
            }
        } else if (pid.length() > 0) {
            processId = new Integer(JtdsConnection.parseIntegerProperty(info, "prop.processid"));
        }
        this.lobBuffer = JtdsConnection.parseLongProperty(info, "prop.lobbuffer");
        this.maxStatements = JtdsConnection.parseIntegerProperty(info, "prop.maxstatements");
        this.statementCache = new ProcedureCache(this.maxStatements);
        this.prepareSql = JtdsConnection.parseIntegerProperty(info, "prop.preparesql");
        if (this.prepareSql < 0) {
            this.prepareSql = 0;
        } else if (this.prepareSql > 3) {
            this.prepareSql = 3;
        }
        if (this.tdsVersion < 3 && this.prepareSql == 3) {
            this.prepareSql = 2;
        }
        if (this.tdsVersion < 2 && this.prepareSql == 2) {
            this.prepareSql = 1;
        }
        this.ssl = info.getProperty(Messages.get("prop.ssl"));
        this.batchSize = JtdsConnection.parseIntegerProperty(info, "prop.batchsize");
        if (this.batchSize < 0) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get("prop.batchsize")), "08001");
        }
        this.bufferDir = new File(info.getProperty(Messages.get("prop.bufferdir")));
        if (!this.bufferDir.isDirectory() && !this.bufferDir.mkdirs()) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get("prop.bufferdir")), "08001");
        }
        this.bufferMaxMemory = JtdsConnection.parseIntegerProperty(info, "prop.buffermaxmemory");
        if (this.bufferMaxMemory < 0) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get("prop.buffermaxmemory")), "08001");
        }
        this.bufferMinPackets = JtdsConnection.parseIntegerProperty(info, "prop.bufferminpackets");
        if (this.bufferMinPackets < 1) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get("prop.bufferminpackets")), "08001");
        }
    }

    private static boolean parseBooleanProperty(Properties info, String key) throws SQLException {
        String propertyName = Messages.get(key);
        String prop = info.getProperty(propertyName);
        if (prop != null && !"true".equalsIgnoreCase(prop) && !"false".equalsIgnoreCase(prop)) {
            throw new SQLException(Messages.get("error.connection.badprop", propertyName), "08001");
        }
        return "true".equalsIgnoreCase(prop);
    }

    private static int parseIntegerProperty(Properties info, String key) throws SQLException {
        String propertyName = Messages.get(key);
        try {
            return Integer.parseInt(info.getProperty(propertyName));
        }
        catch (NumberFormatException e) {
            throw new SQLException(Messages.get("error.connection.badprop", propertyName), "08001");
        }
    }

    private static long parseLongProperty(Properties info, String key) throws SQLException {
        String propertyName = Messages.get(key);
        try {
            return Long.parseLong(info.getProperty(propertyName));
        }
        catch (NumberFormatException e) {
            throw new SQLException(Messages.get("error.connection.badprop", propertyName), "08001");
        }
    }

    protected String getCharset() {
        return this.charsetInfo.getCharset();
    }

    protected boolean isWideChar() {
        return this.charsetInfo.isWideChars();
    }

    protected CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    protected boolean getUseUnicode() {
        return this.useUnicode;
    }

    protected boolean getSybaseInfo(int flag) {
        return (this.sybaseInfo & flag) != 0;
    }

    protected void setSybaseInfo(int mask) {
        this.sybaseInfo = mask;
    }

    protected void setServerCharset(String charset) throws SQLException {
        if (this.charsetSpecified) {
            Logger.println("Server charset " + charset + ". Ignoring as user requested " + this.serverCharset + '.');
            return;
        }
        if (!charset.equals(this.serverCharset)) {
            this.loadCharset(charset);
            if (Logger.isActive()) {
                Logger.println("Set charset to " + this.serverCharset + '/' + this.charsetInfo);
            }
        }
    }

    private void loadCharset(String charset) throws SQLException {
        CharsetInfo tmp;
        if (this.getServerType() == 1 && charset.equalsIgnoreCase("iso_1")) {
            charset = "Cp1252";
        }
        if ((tmp = CharsetInfo.getCharset(charset)) == null) {
            throw new SQLException(Messages.get("error.charset.nomapping", charset), "2C000");
        }
        this.loadCharset(tmp, charset);
        this.serverCharset = charset;
    }

    private void loadCharset(CharsetInfo ci, String ref) throws SQLException {
        try {
            "This is a test".getBytes(ci.getCharset());
            this.charsetInfo = ci;
        }
        catch (UnsupportedEncodingException ex) {
            throw new SQLException(Messages.get("error.charset.invalid", ref, ci.getCharset()), "2C000");
        }
        this.socket.setCharsetInfo(this.charsetInfo);
    }

    private String determineServerCharset() throws SQLException {
        String queryStr = null;
        switch (this.serverType) {
            case 1: {
                if (this.databaseProductVersion.indexOf("6.5") >= 0) {
                    queryStr = SQL_SERVER_65_CHARSET_QUERY;
                    break;
                }
                throw new SQLException("Please use TDS protocol version 7.0 or higher");
            }
            case 2: {
                queryStr = SYBASE_SERVER_CHARSET_QUERY;
            }
        }
        Statement stmt = this.createStatement();
        ResultSet rs = stmt.executeQuery(queryStr);
        rs.next();
        String charset = rs.getString(1);
        rs.close();
        stmt.close();
        return charset;
    }

    void setCollation(byte[] collation) throws SQLException {
        String strCollation = "0x" + Support.toHex(collation);
        if (this.charsetSpecified) {
            Logger.println("Server collation " + strCollation + ". Ignoring as user requested " + this.serverCharset + '.');
            return;
        }
        CharsetInfo tmp = CharsetInfo.getCharset(collation);
        this.loadCharset(tmp, strCollation);
        this.collation = collation;
        if (Logger.isActive()) {
            Logger.println("Set collation to " + strCollation + '/' + this.charsetInfo);
        }
    }

    byte[] getCollation() {
        return this.collation;
    }

    boolean isCharsetSpecified() {
        return this.charsetSpecified;
    }

    protected void setDatabase(String newDb, String oldDb) throws SQLException {
        if (this.currentDatabase != null && !oldDb.equalsIgnoreCase(this.currentDatabase)) {
            throw new SQLException(Messages.get("error.connection.dbmismatch", oldDb, this.databaseName), "HY096");
        }
        this.currentDatabase = newDb;
        if (Logger.isActive()) {
            Logger.println("Changed database from " + oldDb + " to " + newDb);
        }
    }

    protected void setDBServerInfo(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion, int buildNumber) {
        this.databaseProductName = databaseProductName;
        this.databaseMajorVersion = databaseMajorVersion;
        this.databaseMinorVersion = databaseMinorVersion;
        if (this.tdsVersion >= 3) {
            StringBuilder buf = new StringBuilder(10);
            if (databaseMajorVersion < 10) {
                buf.append('0');
            }
            buf.append(databaseMajorVersion).append('.');
            if (databaseMinorVersion < 10) {
                buf.append('0');
            }
            buf.append(databaseMinorVersion).append('.');
            buf.append(buildNumber);
            while (buf.length() < 10) {
                buf.insert(6, '0');
            }
            this.databaseProductVersion = buf.toString();
        } else {
            this.databaseProductVersion = databaseMajorVersion + "." + databaseMinorVersion;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void removeStatement(JtdsStatement statement) throws SQLException {
        Collection handles;
        ArrayList arrayList = this.statements;
        synchronized (arrayList) {
            for (int i = 0; i < this.statements.size(); ++i) {
                Statement stmt;
                WeakReference wr = (WeakReference)this.statements.get(i);
                if (wr == null || (stmt = (Statement)wr.get()) != null && stmt != statement) continue;
                this.statements.set(i, null);
            }
        }
        if (statement instanceof JtdsPreparedStatement && (handles = this.statementCache.getObsoleteHandles(((JtdsPreparedStatement)statement).handles)) != null) {
            if (this.serverType == 1) {
                StringBuilder cleanupSql = new StringBuilder(handles.size() * 32);
                for (ProcEntry pe : handles) {
                    pe.appendDropSQL(cleanupSql);
                }
                if (cleanupSql.length() > 0) {
                    this.baseTds.executeSQL(cleanupSql.toString(), null, null, true, 0, -1, -1, true);
                    this.baseTds.clearResponseQueue();
                }
            } else {
                for (ProcEntry pe : handles) {
                    if (pe.toString() == null) continue;
                    this.baseTds.sybaseUnPrepare(pe.toString());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addStatement(JtdsStatement statement) {
        ArrayList arrayList = this.statements;
        synchronized (arrayList) {
            for (int i = 0; i < this.statements.size(); ++i) {
                WeakReference wr = (WeakReference)this.statements.get(i);
                if (wr != null && wr.get() != null) continue;
                this.statements.set(i, new WeakReference<JtdsStatement>(statement));
                return;
            }
            this.statements.add(new WeakReference<JtdsStatement>(statement));
        }
    }

    void checkOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", "Connection"), "HY010");
        }
    }

    void checkLocal(String method) throws SQLException {
        if (this.xaTransaction) {
            throw new SQLException(Messages.get("error.connection.badxaop", method), "HY010");
        }
    }

    static void notImplemented(String method) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", method), "HYC00");
    }

    public int getDatabaseMajorVersion() {
        return this.databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return this.databaseMinorVersion;
    }

    String getDatabaseProductName() {
        return this.databaseProductName;
    }

    String getDatabaseProductVersion() {
        return this.databaseProductVersion;
    }

    String getURL() {
        return this.url;
    }

    public String getRmHost() {
        return this.serverName + ':' + this.portNumber;
    }

    void setClosed() {
        if (!this.closed) {
            this.closed = true;
            try {
                this.socket.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    synchronized byte[][] sendXaPacket(int[] args, byte[] data) throws SQLException {
        ParamInfo[] params = new ParamInfo[]{new ParamInfo(4, null, 2), new ParamInfo(4, new Integer(args[1]), 0), new ParamInfo(4, new Integer(args[2]), 0), new ParamInfo(4, new Integer(args[3]), 0), new ParamInfo(4, new Integer(args[4]), 0), new ParamInfo(-3, data, 1)};
        this.baseTds.executeSQL(null, "master..xp_jtdsxa", params, false, 0, -1, -1, true);
        ArrayList<Object> xids = new ArrayList<Object>();
        while (!this.baseTds.isEndOfResponse()) {
            if (!this.baseTds.getMoreResults()) continue;
            while (this.baseTds.getNextRow()) {
                Object[] row = this.baseTds.getRowData();
                if (row.length != 1 || !(row[0] instanceof byte[])) continue;
                xids.add(row[0]);
            }
        }
        this.messages.checkErrors();
        args[0] = params[0].getOutValue() instanceof Integer ? (Integer)params[0].getOutValue() : -7;
        if (xids.size() > 0) {
            byte[][] list = new byte[xids.size()][];
            for (int i = 0; i < xids.size(); ++i) {
                list[i] = (byte[])xids.get(i);
            }
            return list;
        }
        if (params[5].getOutValue() instanceof byte[]) {
            byte[][] cookie = new byte[][]{(byte[])params[5].getOutValue()};
            return cookie;
        }
        return null;
    }

    synchronized void enlistConnection(byte[] oleTranID) throws SQLException {
        if (oleTranID != null) {
            this.prepareSql = 2;
            this.baseTds.enlistConnection(1, oleTranID);
            this.xaTransaction = true;
        } else {
            this.baseTds.enlistConnection(1, null);
            this.xaTransaction = false;
        }
    }

    void setXid(Object xid) {
        this.xid = xid;
        this.xaTransaction = xid != null;
    }

    Object getXid() {
        return this.xid;
    }

    void setXaState(int value) {
        this.xaState = value;
    }

    int getXaState() {
        return this.xaState;
    }

    boolean isXaEmulation() {
        return this.xaEmulation;
    }

    Semaphore getMutex() {
        boolean interrupted = false;
        while (true) {
            try {
                this.mutex.acquire();
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this.mutex;
    }

    synchronized void releaseTds(TdsCore tds) throws SQLException {
        if (this.cachedTds != null) {
            tds.close();
        } else {
            tds.clearResponseQueue();
            tds.cleanUp();
            this.cachedTds = tds;
        }
    }

    synchronized TdsCore getCachedTds() {
        TdsCore result = this.cachedTds;
        this.cachedTds = null;
        return result;
    }

    @Override
    public int getHoldability() throws SQLException {
        this.checkOpen();
        return 1;
    }

    @Override
    public synchronized int getTransactionIsolation() throws SQLException {
        this.checkOpen();
        return this.transactionIsolation;
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        this.checkOpen();
        this.messages.clearWarnings();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized void close() throws SQLException {
        Object tmpList;
        if (this.closed) return;
        try {
            ArrayList arrayList = this.statements;
            synchronized (arrayList) {
                tmpList = new ArrayList(this.statements);
                this.statements.clear();
            }
            for (int i = 0; i < ((ArrayList)tmpList).size(); ++i) {
                Statement stmt;
                WeakReference wr = (WeakReference)((ArrayList)tmpList).get(i);
                if (wr == null || (stmt = (Statement)wr.get()) == null) continue;
                try {
                    stmt.close();
                    continue;
                }
                catch (SQLException ex) {
                    // empty catch block
                }
            }
            try {
                if (this.baseTds != null) {
                    this.baseTds.closeConnection();
                    this.baseTds.close();
                }
                if (this.cachedTds != null) {
                    this.cachedTds.close();
                    this.cachedTds = null;
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            if (this.socket != null) {
                this.socket.close();
            }
            this.closed = true;
        }
        catch (IOException iOException) {
            this.closed = true;
            int[] nArray = connections;
            synchronized (connections) {
                connections[0] = connections[0] - 1;
                if (connections[0] != 0) return;
                TimerThread.stopTimer();
                // ** MonitorExit[var1_6] (shouldn't be in output)
                return;
            }
            catch (Throwable throwable) {
                this.closed = true;
                int[] nArray2 = connections;
                synchronized (connections) {
                    connections[0] = connections[0] - 1;
                    if (connections[0] != 0) throw throwable;
                    TimerThread.stopTimer();
                    // ** MonitorExit[var9_14] (shouldn't be in output)
                    throw throwable;
                }
            }
        }
        tmpList = connections;
        synchronized (connections) {
            connections[0] = connections[0] - 1;
            if (connections[0] != 0) return;
            TimerThread.stopTimer();
            // ** MonitorExit[tmpList] (shouldn't be in output)
            return;
        }
    }

    @Override
    public synchronized void commit() throws SQLException {
        this.checkOpen();
        this.checkLocal("commit");
        if (this.getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.autocommit", "commit"), "25000");
        }
        this.baseTds.submitSQL("IF @@TRANCOUNT > 0 COMMIT TRAN");
        this.procInTran.clear();
        this.clearSavepoints();
    }

    @Override
    public synchronized void rollback() throws SQLException {
        this.checkOpen();
        this.checkLocal("rollback");
        if (this.getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.autocommit", "rollback"), "25000");
        }
        this.baseTds.submitSQL("IF @@TRANCOUNT > 0 ROLLBACK TRAN");
        for (int i = 0; i < this.procInTran.size(); ++i) {
            String key = (String)this.procInTran.get(i);
            if (key == null) continue;
            this.statementCache.remove(key);
        }
        this.procInTran.clear();
        this.clearSavepoints();
    }

    @Override
    public synchronized boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        return this.autoCommit;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkOpen();
        return this.readOnly;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.checkOpen();
        switch (holdability) {
            case 1: {
                break;
            }
            case 2: {
                throw new SQLException(Messages.get("error.generic.optvalue", "CLOSE_CURSORS_AT_COMMIT", "setHoldability"), "HY092");
            }
            default: {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(holdability), "holdability"), "HY092");
            }
        }
    }

    @Override
    public synchronized void setTransactionIsolation(int level) throws SQLException {
        this.checkOpen();
        if (this.transactionIsolation == level) {
            return;
        }
        String sql = "SET TRANSACTION ISOLATION LEVEL ";
        boolean sybase = this.serverType == 2;
        switch (level) {
            case 1: {
                sql = sql + (sybase ? "0" : "READ UNCOMMITTED");
                break;
            }
            case 2: {
                sql = sql + (sybase ? "1" : "READ COMMITTED");
                break;
            }
            case 4: {
                sql = sql + (sybase ? "2" : "REPEATABLE READ");
                break;
            }
            case 8: {
                sql = sql + (sybase ? "3" : "SERIALIZABLE");
                break;
            }
            case 4096: {
                if (sybase) {
                    throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_SNAPSHOT", "setTransactionIsolation"), "HY024");
                }
                sql = sql + "SNAPSHOT";
                break;
            }
            case 0: {
                throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_NONE", "setTransactionIsolation"), "HY024");
            }
            default: {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(level), "level"), "HY092");
            }
        }
        this.transactionIsolation = level;
        this.baseTds.submitSQL(sql);
    }

    @Override
    public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
        this.checkOpen();
        this.checkLocal("setAutoCommit");
        if (this.autoCommit == autoCommit) {
            return;
        }
        StringBuilder sql = new StringBuilder(70);
        if (!this.autoCommit) {
            sql.append("IF @@TRANCOUNT > 0 COMMIT TRAN\r\n");
        }
        if (this.serverType == 2) {
            if (autoCommit) {
                sql.append("SET CHAINED OFF");
            } else {
                sql.append("SET CHAINED ON");
            }
        } else if (autoCommit) {
            sql.append("SET IMPLICIT_TRANSACTIONS OFF");
        } else {
            sql.append("SET IMPLICIT_TRANSACTIONS ON");
        }
        this.baseTds.submitSQL(sql.toString());
        this.autoCommit = autoCommit;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.checkOpen();
        this.readOnly = readOnly;
    }

    @Override
    public synchronized String getCatalog() throws SQLException {
        this.checkOpen();
        return this.currentDatabase;
    }

    @Override
    public synchronized void setCatalog(String catalog) throws SQLException {
        int maxlength;
        this.checkOpen();
        if (this.currentDatabase != null && this.currentDatabase.equals(catalog)) {
            return;
        }
        int n = maxlength = this.tdsVersion >= 3 ? 128 : 30;
        if (catalog.length() > maxlength || catalog.length() < 1) {
            throw new SQLException(Messages.get("error.generic.badparam", catalog, "catalog"), "3D000");
        }
        String sql = this.tdsVersion >= 3 ? "use [" + catalog + ']' : "use " + catalog;
        this.baseTds.submitSQL(sql);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        return new JtdsDatabaseMetaData(this);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        return this.messages.getWarnings();
    }

    @Override
    public Statement createStatement() throws SQLException {
        this.checkOpen();
        return this.createStatement(1003, 1007);
    }

    @Override
    public synchronized Statement createStatement(int type, int concurrency) throws SQLException {
        this.checkOpen();
        JtdsStatement stmt = new JtdsStatement(this, type, concurrency);
        this.addStatement(stmt);
        return stmt;
    }

    @Override
    public Statement createStatement(int type, int concurrency, int holdability) throws SQLException {
        this.checkOpen();
        this.setHoldability(holdability);
        return this.createStatement(type, concurrency);
    }

    public Map getTypeMap() throws SQLException {
        this.checkOpen();
        return new HashMap();
    }

    public void setTypeMap(Map map) throws SQLException {
        this.checkOpen();
        JtdsConnection.notImplemented("Connection.setTypeMap(Map)");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.checkOpen();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        String[] result = SQLParser.parse(sql, new ArrayList(), this, false);
        return result[0];
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.checkOpen();
        return this.prepareCall(sql, 1003, 1007);
    }

    @Override
    public synchronized CallableStatement prepareCall(String sql, int type, int concurrency) throws SQLException {
        this.checkOpen();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        JtdsCallableStatement stmt = new JtdsCallableStatement(this, sql, type, concurrency);
        this.addStatement(stmt);
        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int type, int concurrency, int holdability) throws SQLException {
        this.checkOpen();
        this.setHoldability(holdability);
        return this.prepareCall(sql, type, concurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.checkOpen();
        return this.prepareStatement(sql, 1003, 1007);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        if (autoGeneratedKeys != 1 && autoGeneratedKeys != 2) {
            throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(autoGeneratedKeys), "autoGeneratedKeys"), "HY092");
        }
        JtdsPreparedStatement stmt = new JtdsPreparedStatement(this, sql, 1003, 1007, autoGeneratedKeys == 1);
        this.addStatement(stmt);
        return stmt;
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String sql, int type, int concurrency) throws SQLException {
        this.checkOpen();
        if (sql == null || sql.length() == 0) {
            throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        }
        JtdsPreparedStatement stmt = new JtdsPreparedStatement(this, sql, type, concurrency, false);
        this.addStatement(stmt);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int type, int concurrency, int holdability) throws SQLException {
        this.checkOpen();
        this.setHoldability(holdability);
        return this.prepareStatement(sql, type, concurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        if (columnIndexes == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "prepareStatement"), "HY092");
        }
        if (columnIndexes.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolindex", "prepareStatement"), "HY092");
        }
        return this.prepareStatement(sql, 1);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        if (columnNames == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", "prepareStatement"), "HY092");
        }
        if (columnNames.length != 1) {
            throw new SQLException(Messages.get("error.generic.needcolname", "prepareStatement"), "HY092");
        }
        return this.prepareStatement(sql, 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setSavepoint(SavepointImpl savepoint) throws SQLException {
        try (Statement statement = null;){
            statement = this.createStatement();
            statement.execute("IF @@TRANCOUNT=0 BEGIN SET IMPLICIT_TRANSACTIONS OFF; BEGIN TRAN; SET IMPLICIT_TRANSACTIONS ON; END SAVE TRAN jtds" + savepoint.getId());
        }
        JtdsConnection jtdsConnection = this;
        synchronized (jtdsConnection) {
            if (this.savepoints == null) {
                this.savepoints = new ArrayList();
            }
            this.savepoints.add(savepoint);
        }
    }

    private synchronized void clearSavepoints() {
        if (this.savepoints != null) {
            this.savepoints.clear();
        }
        if (this.savepointProcInTran != null) {
            this.savepointProcInTran.clear();
        }
        this.savepointId = 0;
    }

    @Override
    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.savepoints == null) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        int index = this.savepoints.indexOf(savepoint);
        if (index == -1) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        Object tmpSavepoint = this.savepoints.remove(index);
        if (this.savepointProcInTran != null) {
            List keys;
            if (index != 0 && (keys = (List)this.savepointProcInTran.get(savepoint)) != null) {
                Savepoint wrapping = (Savepoint)this.savepoints.get(index - 1);
                ArrayList wrappingKeys = (ArrayList)this.savepointProcInTran.get(wrapping);
                if (wrappingKeys == null) {
                    wrappingKeys = new ArrayList();
                }
                wrappingKeys.addAll(keys);
                this.savepointProcInTran.put(wrapping, wrappingKeys);
            }
            this.savepointProcInTran.remove(tmpSavepoint);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        this.checkLocal("rollback");
        if (this.savepoints == null) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        int index = this.savepoints.indexOf(savepoint);
        if (index == -1) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        if (this.getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenorollback"), "25000");
        }
        try (Statement statement = null;){
            statement = this.createStatement();
            statement.execute("ROLLBACK TRAN jtds" + ((SavepointImpl)savepoint).getId());
        }
        int size = this.savepoints.size();
        for (int i = size - 1; i >= index; --i) {
            List keys;
            Object tmpSavepoint = this.savepoints.remove(i);
            if (this.savepointProcInTran == null || (keys = (List)this.savepointProcInTran.get(tmpSavepoint)) == null) continue;
            for (String key : keys) {
                this.removeCachedProcedure(key);
            }
        }
        this.setSavepoint((SavepointImpl)savepoint);
    }

    @Override
    public synchronized Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        this.checkLocal("setSavepoint");
        if (this.getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        }
        SavepointImpl savepoint = new SavepointImpl(this.getNextSavepointId());
        this.setSavepoint(savepoint);
        return savepoint;
    }

    @Override
    public synchronized Savepoint setSavepoint(String name) throws SQLException {
        this.checkOpen();
        this.checkLocal("setSavepoint");
        if (this.getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        }
        if (name == null) {
            throw new SQLException(Messages.get("error.connection.savenullname", "savepoint"), "25000");
        }
        SavepointImpl savepoint = new SavepointImpl(this.getNextSavepointId(), name);
        this.setSavepoint(savepoint);
        return savepoint;
    }

    private int getNextSavepointId() {
        return ++this.savepointId;
    }

    synchronized void addCachedProcedure(String key) {
        Object savepoint;
        ArrayList<String> keys;
        if (this.savepoints == null || this.savepoints.size() == 0) {
            return;
        }
        if (this.savepointProcInTran == null) {
            this.savepointProcInTran = new HashMap();
        }
        if ((keys = (ArrayList<String>)this.savepointProcInTran.get(savepoint = this.savepoints.get(this.savepoints.size() - 1))) == null) {
            keys = new ArrayList<String>();
        }
        keys.add(key);
        this.savepointProcInTran.put(savepoint, keys);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getSchema() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new AbstractMethodError();
    }

    static {
        connections = new int[1];
    }
}

