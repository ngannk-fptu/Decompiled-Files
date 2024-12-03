/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.Driver;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbcx.JtdsObjectFactory;
import net.sourceforge.jtds.jdbcx.JtdsXAConnection;
import net.sourceforge.jtds.jdbcx.PooledConnection;

public class JtdsDataSource
implements DataSource,
ConnectionPoolDataSource,
XADataSource,
Referenceable,
Serializable {
    static final long serialVersionUID = 266241L;
    static final String DESCRIPTION = "description";
    private final HashMap _Config;
    private static final Driver _Driver = new Driver();

    JtdsDataSource(HashMap config) {
        this._Config = config;
    }

    public JtdsDataSource() {
        this._Config = new HashMap();
    }

    @Override
    public XAConnection getXAConnection() throws SQLException {
        return new JtdsXAConnection(this, this.getConnection((String)this._Config.get("prop.user"), (String)this._Config.get("prop.password")));
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        return new JtdsXAConnection(this, this.getConnection(user, password));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection((String)this._Config.get("prop.user"), (String)this._Config.get("prop.password"));
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        String url;
        String servername = (String)this._Config.get("prop.servername");
        String servertype = (String)this._Config.get("prop.servertype");
        String logfile = (String)this._Config.get("prop.logfile");
        if (servername == null || servername.length() == 0) {
            throw new SQLException(Messages.get("error.connection.nohost"), "08001");
        }
        if (this.getLogWriter() == null && logfile != null && logfile.length() > 0) {
            try {
                this.setLogWriter(new PrintWriter(new FileOutputStream(logfile), true));
            }
            catch (IOException e) {
                System.err.println("jTDS: Failed to set log file " + e);
            }
        }
        Properties props = new Properties();
        this.addNonNullProperties(props, user, password);
        try {
            int serverTypeDef = servertype == null ? 0 : Integer.parseInt(servertype);
            url = "jdbc:jtds:" + DefaultProperties.getServerTypeWithDefault(serverTypeDef) + ':';
        }
        catch (RuntimeException ex) {
            SQLException sqlException = new SQLException(Messages.get("error.connection.servertype", ex.toString()), "08001");
            Support.linkException(sqlException, (Throwable)ex);
            throw sqlException;
        }
        return _Driver.connect(url, props);
    }

    @Override
    public Reference getReference() {
        Reference ref = new Reference(this.getClass().getName(), JtdsObjectFactory.class.getName(), null);
        for (Map.Entry e : this._Config.entrySet()) {
            String key = (String)e.getKey();
            String val = (String)e.getValue();
            ref.add(new StringRefAddr(key, val));
        }
        return ref;
    }

    @Override
    public javax.sql.PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection((String)this._Config.get("prop.user"), (String)this._Config.get("prop.password"));
    }

    @Override
    public synchronized javax.sql.PooledConnection getPooledConnection(String user, String password) throws SQLException {
        return new PooledConnection(this.getConnection(user, password));
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        net.sourceforge.jtds.util.Logger.setLogWriter(out);
    }

    @Override
    public PrintWriter getLogWriter() {
        return net.sourceforge.jtds.util.Logger.getLogWriter();
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        this._Config.put("prop.logintimeout", String.valueOf(loginTimeout));
    }

    @Override
    public int getLoginTimeout() {
        return this.getIntProperty("prop.logintimeout");
    }

    public void setSocketTimeout(int socketTimeout) {
        this._Config.put("prop.sotimeout", String.valueOf(socketTimeout));
    }

    public int getSocketTimeout() {
        return this.getIntProperty("prop.sotimeout");
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this._Config.put("prop.sokeepalive", String.valueOf(socketKeepAlive));
    }

    public boolean getSocketKeepAlive() {
        return Boolean.valueOf((String)this._Config.get("prop.sokeepalive"));
    }

    public void setProcessId(String processId) {
        this._Config.put("prop.processid", processId);
    }

    public String getProcessId() {
        return (String)this._Config.get("prop.processid");
    }

    public void setDatabaseName(String databaseName) {
        this._Config.put("prop.databasename", databaseName);
    }

    public String getDatabaseName() {
        return (String)this._Config.get("prop.databasename");
    }

    public void setDescription(String description) {
        this._Config.put(DESCRIPTION, description);
    }

    public String getDescription() {
        return (String)this._Config.get(DESCRIPTION);
    }

    public void setPassword(String password) {
        this._Config.put("prop.password", password);
    }

    public String getPassword() {
        return (String)this._Config.get("prop.password");
    }

    public void setPortNumber(int portNumber) {
        this._Config.put("prop.portnumber", String.valueOf(portNumber));
    }

    public int getPortNumber() {
        return this.getIntProperty("prop.portnumber");
    }

    public void setServerName(String serverName) {
        this._Config.put("prop.servername", serverName);
    }

    public String getServerName() {
        return (String)this._Config.get("prop.servername");
    }

    public void setAutoCommit(boolean autoCommit) {
        this._Config.put("prop.autocommit", String.valueOf(autoCommit));
    }

    public boolean getAutoCommit() {
        return Boolean.valueOf((String)this._Config.get("prop.autocommit"));
    }

    public void setUser(String user) {
        this._Config.put("prop.user", user);
    }

    public String getUser() {
        return (String)this._Config.get("prop.user");
    }

    public void setTds(String tds) {
        this._Config.put("prop.tds", tds);
    }

    public String getTds() {
        return (String)this._Config.get("prop.tds");
    }

    public void setServerType(int serverType) {
        this._Config.put("prop.servertype", String.valueOf(serverType));
    }

    public int getServerType() {
        return this.getIntProperty("prop.servertype");
    }

    public void setDomain(String domain) {
        this._Config.put("prop.domain", domain);
    }

    public String getDomain() {
        return (String)this._Config.get("prop.domain");
    }

    public void setUseNTLMV2(boolean usentlmv2) {
        this._Config.put("prop.usentlmv2", String.valueOf(usentlmv2));
    }

    public boolean getUseNTLMV2() {
        return Boolean.valueOf((String)this._Config.get("prop.usentlmv2"));
    }

    public void setUseKerberos(boolean useKerberos) {
        this._Config.put("prop.usekerberos", Boolean.toString(useKerberos));
    }

    public boolean getUseKerberos() {
        return Boolean.valueOf((String)this._Config.get("prop.usekerberos"));
    }

    public void setInstance(String instance) {
        this._Config.put("prop.instance", instance);
    }

    public String getInstance() {
        return (String)this._Config.get("prop.instance");
    }

    public void setSendStringParametersAsUnicode(boolean sendStringParametersAsUnicode) {
        this._Config.put("prop.useunicode", String.valueOf(sendStringParametersAsUnicode));
    }

    public boolean getSendStringParametersAsUnicode() {
        return Boolean.valueOf((String)this._Config.get("prop.useunicode"));
    }

    public void setNamedPipe(boolean namedPipe) {
        this._Config.put("prop.namedpipe", String.valueOf(namedPipe));
    }

    public boolean getNamedPipe() {
        return Boolean.valueOf((String)this._Config.get("prop.namedpipe"));
    }

    public void setLastUpdateCount(boolean lastUpdateCount) {
        this._Config.put("prop.lastupdatecount", String.valueOf(lastUpdateCount));
    }

    public boolean getLastUpdateCount() {
        return Boolean.valueOf((String)this._Config.get("prop.lastupdatecount"));
    }

    public void setXaEmulation(boolean xaEmulation) {
        this._Config.put("prop.xaemulation", String.valueOf(xaEmulation));
    }

    public boolean getXaEmulation() {
        return Boolean.valueOf((String)this._Config.get("prop.xaemulation"));
    }

    public void setCharset(String charset) {
        this._Config.put("prop.charset", charset);
    }

    public String getCharset() {
        return (String)this._Config.get("prop.charset");
    }

    public void setLanguage(String language) {
        this._Config.put("prop.language", language);
    }

    public String getLanguage() {
        return (String)this._Config.get("prop.language");
    }

    public void setMacAddress(String macAddress) {
        this._Config.put("prop.macaddress", macAddress);
    }

    public String getMacAddress() {
        return (String)this._Config.get("prop.macaddress");
    }

    public void setPacketSize(int packetSize) {
        this._Config.put("prop.packetsize", String.valueOf(packetSize));
    }

    public int getPacketSize() {
        return this.getIntProperty("prop.packetsize");
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this._Config.put("prop.tcpnodelay", String.valueOf(tcpNoDelay));
    }

    public boolean getTcpNoDelay() {
        return Boolean.valueOf((String)this._Config.get("prop.tcpnodelay"));
    }

    public void setPrepareSql(int prepareSql) {
        this._Config.put("prop.preparesql", String.valueOf(prepareSql));
    }

    public int getPrepareSql() {
        return this.getIntProperty("prop.preparesql");
    }

    public void setLobBuffer(long lobBuffer) {
        this._Config.put("prop.lobbuffer", String.valueOf(lobBuffer));
    }

    public long getLobBuffer() {
        return this.getLongProperty("prop.lobbuffer");
    }

    public void setMaxStatements(int maxStatements) {
        this._Config.put("prop.maxstatements", String.valueOf(maxStatements));
    }

    public int getMaxStatements() {
        return this.getIntProperty("prop.maxstatements");
    }

    public void setAppName(String appName) {
        this._Config.put("prop.appname", appName);
    }

    public String getAppName() {
        return (String)this._Config.get("prop.appname");
    }

    public void setProgName(String progName) {
        this._Config.put("prop.progname", progName);
    }

    public String getProgName() {
        return (String)this._Config.get("prop.progname");
    }

    public void setWsid(String wsid) {
        this._Config.put("prop.wsid", wsid);
    }

    public String getWsid() {
        return (String)this._Config.get("prop.wsid");
    }

    public void setLogFile(String logFile) {
        this._Config.put("prop.logfile", logFile);
    }

    public String getLogFile() {
        return (String)this._Config.get("prop.logfile");
    }

    public void setSsl(String ssl) {
        this._Config.put("prop.ssl", ssl);
    }

    public String getSsl() {
        return (String)this._Config.get("prop.ssl");
    }

    public void setBatchSize(int batchSize) {
        this._Config.put("prop.batchsize", String.valueOf(batchSize));
    }

    public int getBatchSize() {
        return this.getIntProperty("prop.batchsize");
    }

    public void setBufferDir(String bufferDir) {
        this._Config.put("prop.bufferdir", bufferDir);
    }

    public String getBufferDir() {
        return (String)this._Config.get("prop.bufferdir");
    }

    public int getBufferMaxMemory() {
        return this.getIntProperty("prop.buffermaxmemory");
    }

    public void setBufferMaxMemory(int bufferMaxMemory) {
        this._Config.put("prop.buffermaxmemory", String.valueOf(bufferMaxMemory));
    }

    public void setBufferMinPackets(int bufferMinPackets) {
        this._Config.put("prop.bufferminpackets", String.valueOf(bufferMinPackets));
    }

    public int getBufferMinPackets() {
        return this.getIntProperty("prop.bufferminpackets");
    }

    public void setCacheMetaData(boolean cacheMetaData) {
        this._Config.put("prop.cachemetadata", String.valueOf(cacheMetaData));
    }

    public boolean getCacheMetaData() {
        return Boolean.valueOf((String)this._Config.get("prop.cachemetadata"));
    }

    public void setUseCursors(boolean useCursors) {
        this._Config.put("prop.usecursors", String.valueOf(useCursors));
    }

    public boolean getUseCursors() {
        return Boolean.valueOf((String)this._Config.get("prop.usecursors"));
    }

    public void setUseLOBs(boolean useLOBs) {
        this._Config.put("prop.uselobs", String.valueOf(useLOBs));
    }

    public boolean getUseLOBs() {
        return Boolean.valueOf((String)this._Config.get("prop.uselobs"));
    }

    public void setBindAddress(String bindAddress) {
        this._Config.put("prop.bindaddress", bindAddress);
    }

    public String getBindAddress() {
        return (String)this._Config.get("prop.bindaddress");
    }

    public void setUseJCIFS(boolean useJCIFS) {
        this._Config.put("prop.usejcifs", String.valueOf(useJCIFS));
    }

    public boolean getUseJCIFS() {
        return Boolean.valueOf((String)this._Config.get("prop.usejcifs"));
    }

    private void addNonNullProperties(Properties props, String user, String password) {
        for (Map.Entry e : this._Config.entrySet()) {
            String key = (String)e.getKey();
            String val = (String)e.getValue();
            if (key.equals(DESCRIPTION) || val == null) continue;
            props.setProperty(Messages.get(key), val);
        }
        if (user != null) {
            props.setProperty(Messages.get("prop.user"), user);
        }
        if (password != null) {
            props.setProperty(Messages.get("prop.password"), password);
        }
    }

    private int getIntProperty(String key) {
        return Long.valueOf(this.getLongProperty(key)).intValue();
    }

    private long getLongProperty(String key) {
        String val = (String)this._Config.get(key);
        if (val == null) {
            return 0L;
        }
        return Long.parseLong(val);
    }

    public boolean isWrapperFor(Class arg0) {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) {
        throw new AbstractMethodError();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new AbstractMethodError();
    }
}

