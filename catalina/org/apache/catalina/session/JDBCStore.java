/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;

@Deprecated
public class JDBCStore
extends StoreBase {
    private String name = null;
    protected static final String storeName = "JDBCStore";
    protected static final String threadName = "JDBCStore";
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    private Connection dbConnection = null;
    protected Driver driver = null;
    protected String driverName = null;
    protected String dataSourceName = null;
    private boolean localDataSource = false;
    protected DataSource dataSource = null;
    protected String sessionTable = "tomcat$sessions";
    protected String sessionAppCol = "app";
    protected String sessionIdCol = "id";
    protected String sessionDataCol = "data";
    protected String sessionValidCol = "valid";
    protected String sessionMaxInactiveCol = "maxinactive";
    protected String sessionLastAccessedCol = "lastaccess";
    protected PreparedStatement preparedSizeSql = null;
    protected PreparedStatement preparedSaveSql = null;
    protected PreparedStatement preparedClearSql = null;
    protected PreparedStatement preparedRemoveSql = null;
    protected PreparedStatement preparedLoadSql = null;

    public String getName() {
        if (this.name == null) {
            Context container = this.manager.getContext();
            String contextName = container.getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            String hostName = "";
            String engineName = "";
            if (container.getParent() != null) {
                Container host = container.getParent();
                hostName = host.getName();
                if (host.getParent() != null) {
                    engineName = host.getParent().getName();
                }
            }
            this.name = "/" + engineName + "/" + hostName + contextName;
        }
        return this.name;
    }

    public String getThreadName() {
        return "JDBCStore";
    }

    @Override
    public String getStoreName() {
        return "JDBCStore";
    }

    public void setDriverName(String driverName) {
        String oldDriverName = this.driverName;
        this.driverName = driverName;
        this.support.firePropertyChange("driverName", oldDriverName, this.driverName);
        this.driverName = driverName;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionURL(String connectionURL) {
        String oldConnString = this.connectionURL;
        this.connectionURL = connectionURL;
        this.support.firePropertyChange("connectionURL", oldConnString, this.connectionURL);
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setSessionTable(String sessionTable) {
        String oldSessionTable = this.sessionTable;
        this.sessionTable = sessionTable;
        this.support.firePropertyChange("sessionTable", oldSessionTable, this.sessionTable);
    }

    public String getSessionTable() {
        return this.sessionTable;
    }

    public void setSessionAppCol(String sessionAppCol) {
        String oldSessionAppCol = this.sessionAppCol;
        this.sessionAppCol = sessionAppCol;
        this.support.firePropertyChange("sessionAppCol", oldSessionAppCol, this.sessionAppCol);
    }

    public String getSessionAppCol() {
        return this.sessionAppCol;
    }

    public void setSessionIdCol(String sessionIdCol) {
        String oldSessionIdCol = this.sessionIdCol;
        this.sessionIdCol = sessionIdCol;
        this.support.firePropertyChange("sessionIdCol", oldSessionIdCol, this.sessionIdCol);
    }

    public String getSessionIdCol() {
        return this.sessionIdCol;
    }

    public void setSessionDataCol(String sessionDataCol) {
        String oldSessionDataCol = this.sessionDataCol;
        this.sessionDataCol = sessionDataCol;
        this.support.firePropertyChange("sessionDataCol", oldSessionDataCol, this.sessionDataCol);
    }

    public String getSessionDataCol() {
        return this.sessionDataCol;
    }

    public void setSessionValidCol(String sessionValidCol) {
        String oldSessionValidCol = this.sessionValidCol;
        this.sessionValidCol = sessionValidCol;
        this.support.firePropertyChange("sessionValidCol", oldSessionValidCol, this.sessionValidCol);
    }

    public String getSessionValidCol() {
        return this.sessionValidCol;
    }

    public void setSessionMaxInactiveCol(String sessionMaxInactiveCol) {
        String oldSessionMaxInactiveCol = this.sessionMaxInactiveCol;
        this.sessionMaxInactiveCol = sessionMaxInactiveCol;
        this.support.firePropertyChange("sessionMaxInactiveCol", oldSessionMaxInactiveCol, this.sessionMaxInactiveCol);
    }

    public String getSessionMaxInactiveCol() {
        return this.sessionMaxInactiveCol;
    }

    public void setSessionLastAccessedCol(String sessionLastAccessedCol) {
        String oldSessionLastAccessedCol = this.sessionLastAccessedCol;
        this.sessionLastAccessedCol = sessionLastAccessedCol;
        this.support.firePropertyChange("sessionLastAccessedCol", oldSessionLastAccessedCol, this.sessionLastAccessedCol);
    }

    public String getSessionLastAccessedCol() {
        return this.sessionLastAccessedCol;
    }

    public void setDataSourceName(String dataSourceName) {
        if (dataSourceName == null || dataSourceName.trim().isEmpty()) {
            this.manager.getContext().getLogger().warn((Object)sm.getString(this.getStoreName() + ".missingDataSourceName"));
            return;
        }
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public boolean getLocalDataSource() {
        return this.localDataSource;
    }

    public void setLocalDataSource(boolean localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public String[] expiredKeys() throws IOException {
        return this.keys(true);
    }

    @Override
    public String[] keys() throws IOException {
        return this.keys(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String[] keys(boolean expiredOnly) throws IOException {
        String[] keys = null;
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return new String[0];
                }
                try {
                    String keysSql = "SELECT " + this.sessionIdCol + " FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                    if (expiredOnly) {
                        keysSql = keysSql + " AND (" + this.sessionLastAccessedCol + " + " + this.sessionMaxInactiveCol + " * 1000 < ?)";
                    }
                    try (PreparedStatement preparedKeysSql = _conn.prepareStatement(keysSql);){
                        preparedKeysSql.setString(1, this.getName());
                        if (expiredOnly) {
                            preparedKeysSql.setLong(2, System.currentTimeMillis());
                        }
                        try (ResultSet rst = preparedKeysSql.executeQuery();){
                            ArrayList<String> tmpkeys = new ArrayList<String>();
                            if (rst != null) {
                                while (rst.next()) {
                                    tmpkeys.add(rst.getString(1));
                                }
                            }
                            keys = tmpkeys.toArray(new String[0]);
                            numberOfTries = 0;
                            continue;
                        }
                    }
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    keys = new String[]{};
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
        return keys;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSize() throws IOException {
        int size = 0;
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return size;
                }
                try {
                    if (this.preparedSizeSql == null) {
                        String sizeSql = "SELECT COUNT(" + this.sessionIdCol + ") FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                        this.preparedSizeSql = _conn.prepareStatement(sizeSql);
                    }
                    this.preparedSizeSql.setString(1, this.getName());
                    try (ResultSet rst = this.preparedSizeSql.executeQuery();){
                        if (rst.next()) {
                            size = rst.getInt(1);
                        }
                        numberOfTries = 0;
                        continue;
                    }
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
        return size;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        StandardSession _session = null;
        Context context = this.getManager().getContext();
        Log contextLog = context.getLogger();
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return null;
                }
                ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
                try {
                    if (this.preparedLoadSql == null) {
                        String loadSql = "SELECT " + this.sessionIdCol + ", " + this.sessionDataCol + " FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ? AND " + this.sessionAppCol + " = ?";
                        this.preparedLoadSql = _conn.prepareStatement(loadSql);
                    }
                    this.preparedLoadSql.setString(1, id);
                    this.preparedLoadSql.setString(2, this.getName());
                    try (ResultSet rst = this.preparedLoadSql.executeQuery();){
                        if (rst.next()) {
                            try (ObjectInputStream ois = this.getObjectInputStream(rst.getBinaryStream(2));){
                                if (contextLog.isDebugEnabled()) {
                                    contextLog.debug((Object)sm.getString(this.getStoreName() + ".loading", new Object[]{id, this.sessionTable}));
                                }
                                _session = (StandardSession)this.manager.createEmptySession();
                                _session.readObjectData(ois);
                                _session.setManager(this.manager);
                            }
                        } else if (context.getLogger().isDebugEnabled()) {
                            contextLog.debug((Object)(this.getStoreName() + ": No persisted data object found"));
                        }
                        numberOfTries = 0;
                        continue;
                    }
                }
                catch (SQLException e) {
                    contextLog.error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                finally {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    this.release(_conn);
                }
            }
        }
        return _session;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(String id) throws IOException {
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    this.remove(id, _conn);
                    numberOfTries = 0;
                    continue;
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".removing", new Object[]{id, this.sessionTable}));
        }
    }

    private void remove(String id, Connection _conn) throws SQLException {
        if (this.preparedRemoveSql == null) {
            String removeSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ?  AND " + this.sessionAppCol + " = ?";
            this.preparedRemoveSql = _conn.prepareStatement(removeSql);
        }
        this.preparedRemoveSql.setString(1, id);
        this.preparedRemoveSql.setString(2, this.getName());
        this.preparedRemoveSql.execute();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() throws IOException {
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    if (this.preparedClearSql == null) {
                        String clearSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                        this.preparedClearSql = _conn.prepareStatement(clearSql);
                    }
                    this.preparedClearSql.setString(1, this.getName());
                    this.preparedClearSql.execute();
                    numberOfTries = 0;
                    continue;
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save(Session session) throws IOException {
        ByteArrayOutputStream bos = null;
        JDBCStore jDBCStore = this;
        synchronized (jDBCStore) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    this.remove(session.getIdInternal(), _conn);
                    bos = new ByteArrayOutputStream();
                    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));){
                        ((StandardSession)session).writeObjectData(oos);
                    }
                    byte[] obs = bos.toByteArray();
                    int size = obs.length;
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(obs, 0, size);
                         BufferedInputStream in = new BufferedInputStream(bis, size);){
                        if (this.preparedSaveSql == null) {
                            String saveSql = "INSERT INTO " + this.sessionTable + " (" + this.sessionIdCol + ", " + this.sessionAppCol + ", " + this.sessionDataCol + ", " + this.sessionValidCol + ", " + this.sessionMaxInactiveCol + ", " + this.sessionLastAccessedCol + ") VALUES (?, ?, ?, ?, ?, ?)";
                            this.preparedSaveSql = _conn.prepareStatement(saveSql);
                        }
                        this.preparedSaveSql.setString(1, session.getIdInternal());
                        this.preparedSaveSql.setString(2, this.getName());
                        this.preparedSaveSql.setBinaryStream(3, (InputStream)in, size);
                        this.preparedSaveSql.setString(4, session.isValid() ? "1" : "0");
                        this.preparedSaveSql.setInt(5, session.getMaxInactiveInterval());
                        this.preparedSaveSql.setLong(6, session.getLastAccessedTime());
                        this.preparedSaveSql.execute();
                        numberOfTries = 0;
                        continue;
                    }
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    if (this.dbConnection == null) continue;
                    this.close(this.dbConnection);
                    continue;
                }
                catch (IOException iOException) {
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".saving", new Object[]{session.getIdInternal(), this.sessionTable}));
        }
    }

    protected Connection getConnection() {
        Connection conn = null;
        try {
            conn = this.open();
            if (conn == null || conn.isClosed()) {
                this.manager.getContext().getLogger().info((Object)sm.getString(this.getStoreName() + ".checkConnectionDBClosed"));
                conn = this.open();
                if (conn == null || conn.isClosed()) {
                    this.manager.getContext().getLogger().info((Object)sm.getString(this.getStoreName() + ".checkConnectionDBReOpenFail"));
                }
            }
        }
        catch (SQLException ex) {
            this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".checkConnectionSQLException", new Object[]{ex.toString()}));
        }
        return conn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Connection open() throws SQLException {
        if (this.dbConnection != null) {
            return this.dbConnection;
        }
        if (this.dataSourceName != null && this.dataSource == null) {
            Context context = this.getManager().getContext();
            ClassLoader oldThreadContextCL = null;
            if (this.localDataSource) {
                oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            }
            try {
                InitialContext initCtx = new InitialContext();
                javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
                this.dataSource = (DataSource)envCtx.lookup(this.dataSourceName);
            }
            catch (NamingException e) {
                context.getLogger().error((Object)sm.getString(this.getStoreName() + ".wrongDataSource", new Object[]{this.dataSourceName}), (Throwable)e);
            }
            finally {
                if (this.localDataSource) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                }
            }
        }
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".checkConnectionClassNotFoundException", new Object[]{e.toString()}));
                throw new SQLException(e);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put("user", this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put("password", this.connectionPassword);
        }
        this.dbConnection = this.driver.connect(this.connectionURL, props);
        if (this.dbConnection == null) {
            throw new SQLException(sm.getString(this.getStoreName() + ".connectError", new Object[]{this.connectionURL}));
        }
        this.dbConnection.setAutoCommit(true);
        return this.dbConnection;
    }

    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            this.preparedSizeSql.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedSizeSql = null;
        try {
            this.preparedSaveSql.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedSaveSql = null;
        try {
            this.preparedClearSql.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        try {
            this.preparedRemoveSql.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedRemoveSql = null;
        try {
            this.preparedLoadSql.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedLoadSql = null;
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        }
        catch (SQLException e) {
            this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".commitSQLException"), (Throwable)e);
        }
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".close", new Object[]{e.toString()}));
        }
        finally {
            this.dbConnection = null;
        }
    }

    protected void release(Connection conn) {
        if (this.dataSource != null) {
            this.close(conn);
        }
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        if (this.dataSourceName == null) {
            this.dbConnection = this.getConnection();
        }
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.dbConnection != null) {
            try {
                this.dbConnection.commit();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            this.close(this.dbConnection);
        }
    }
}

