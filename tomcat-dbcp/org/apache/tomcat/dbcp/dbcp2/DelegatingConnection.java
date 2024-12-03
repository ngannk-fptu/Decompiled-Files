/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.ClientInfoStatus;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.apache.tomcat.dbcp.dbcp2.AbandonedTrace;
import org.apache.tomcat.dbcp.dbcp2.DelegatingCallableStatement;
import org.apache.tomcat.dbcp.dbcp2.DelegatingDatabaseMetaData;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.DelegatingStatement;
import org.apache.tomcat.dbcp.dbcp2.Jdbc41Bridge;
import org.apache.tomcat.dbcp.dbcp2.SQLExceptionList;
import org.apache.tomcat.dbcp.dbcp2.Utils;

public class DelegatingConnection<C extends Connection>
extends AbandonedTrace
implements Connection {
    private static final Map<String, ClientInfoStatus> EMPTY_FAILED_PROPERTIES = Collections.emptyMap();
    private volatile C connection;
    private volatile boolean closed;
    private boolean cacheState = true;
    private Boolean cachedAutoCommit;
    private Boolean cachedReadOnly;
    private String cachedCatalog;
    private String cachedSchema;
    private Duration defaultQueryTimeoutDuration;

    public DelegatingConnection(C connection) {
        this.connection = connection;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        try {
            Jdbc41Bridge.abort(this.connection, executor);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    protected void activate() {
        this.closed = false;
        this.setLastUsed();
        if (this.connection instanceof DelegatingConnection) {
            ((DelegatingConnection)this.connection).activate();
        }
    }

    protected void checkOpen() throws SQLException {
        if (this.closed) {
            if (null != this.connection) {
                String label;
                try {
                    label = this.connection.toString();
                }
                catch (Exception e) {
                    label = "";
                }
                throw new SQLException("Connection " + label + " is closed.");
            }
            throw new SQLException("Connection is null.");
        }
    }

    public void clearCachedState() {
        this.cachedAutoCommit = null;
        this.cachedReadOnly = null;
        this.cachedSchema = null;
        this.cachedCatalog = null;
        if (this.connection instanceof DelegatingConnection) {
            ((DelegatingConnection)this.connection).clearCachedState();
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.checkOpen();
        try {
            this.connection.clearWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            this.closeInternal();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    protected final void closeInternal() throws SQLException {
        try {
            this.passivate();
        }
        finally {
            if (this.connection != null) {
                try {
                    connectionIsClosed = this.connection.isClosed();
                }
                catch (SQLException e) {
                    connectionIsClosed = false;
                }
                try {
                    if (connectionIsClosed) ** GOTO lbl19
                    this.connection.close();
                }
                finally {
                    this.closed = true;
                }
            } else {
                this.closed = true;
            }
lbl19:
            // 3 sources

        }
    }

    @Override
    public void commit() throws SQLException {
        this.checkOpen();
        try {
            this.connection.commit();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createArrayOf(typeName, elements);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Blob createBlob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createBlob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createClob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public NClob createNClob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createNClob();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createSQLXML();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingStatement(this, this.connection.createStatement()));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingStatement(this, this.connection.createStatement(resultSetType, resultSetConcurrency)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingStatement(this, this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createStruct(typeName, attributes);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.cachedAutoCommit != null) {
            return this.cachedAutoCommit;
        }
        try {
            this.cachedAutoCommit = this.connection.getAutoCommit();
            return this.cachedAutoCommit;
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    public boolean getCacheState() {
        return this.cacheState;
    }

    @Override
    public String getCatalog() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.cachedCatalog != null) {
            return this.cachedCatalog;
        }
        try {
            this.cachedCatalog = this.connection.getCatalog();
            return this.cachedCatalog;
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getClientInfo();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getClientInfo(name);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Deprecated
    public Integer getDefaultQueryTimeout() {
        return this.defaultQueryTimeoutDuration == null ? null : Integer.valueOf((int)this.defaultQueryTimeoutDuration.getSeconds());
    }

    public Duration getDefaultQueryTimeoutDuration() {
        return this.defaultQueryTimeoutDuration;
    }

    public C getDelegate() {
        return this.getDelegateInternal();
    }

    protected final C getDelegateInternal() {
        return this.connection;
    }

    @Override
    public int getHoldability() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getHoldability();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    public Connection getInnermostDelegate() {
        return this.getInnermostDelegateInternal();
    }

    public final Connection getInnermostDelegateInternal() {
        C conn = this.connection;
        while (conn instanceof DelegatingConnection) {
            if (this != (conn = ((DelegatingConnection)conn).getDelegateInternal())) continue;
            return null;
        }
        return conn;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingDatabaseMetaData(this, this.connection.getMetaData());
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        this.checkOpen();
        try {
            return Jdbc41Bridge.getNetworkTimeout(this.connection);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public String getSchema() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.cachedSchema != null) {
            return this.cachedSchema;
        }
        try {
            this.cachedSchema = Jdbc41Bridge.getSchema(this.connection);
            return this.cachedSchema;
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getTransactionIsolation();
        }
        catch (SQLException e) {
            this.handleException(e);
            return -1;
        }
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getTypeMap();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    protected void handleException(SQLException e) throws SQLException {
        throw e;
    }

    protected <T extends Throwable> T handleExceptionNoThrow(T e) {
        return e;
    }

    private <T extends DelegatingStatement> T init(T delegatingStatement) throws SQLException {
        if (this.defaultQueryTimeoutDuration != null && this.defaultQueryTimeoutDuration.getSeconds() != (long)delegatingStatement.getQueryTimeout()) {
            delegatingStatement.setQueryTimeout((int)this.defaultQueryTimeoutDuration.getSeconds());
        }
        return delegatingStatement;
    }

    public boolean innermostDelegateEquals(Connection c) {
        Connection innerCon = this.getInnermostDelegateInternal();
        if (innerCon == null) {
            return c == null;
        }
        return innerCon.equals(c);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.closed || this.connection == null || this.connection.isClosed();
    }

    protected boolean isClosedInternal() {
        return this.closed;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.cachedReadOnly != null) {
            return this.cachedReadOnly;
        }
        try {
            this.cachedReadOnly = this.connection.isReadOnly();
            return this.cachedReadOnly;
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    public boolean isValid(Duration timeout) throws SQLException {
        if (this.isClosed()) {
            return false;
        }
        try {
            return this.connection.isValid((int)timeout.getSeconds());
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    @Deprecated
    public boolean isValid(int timeoutSeconds) throws SQLException {
        return this.isValid(Duration.ofSeconds(timeoutSeconds));
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return true;
        }
        if (iface.isAssignableFrom(this.connection.getClass())) {
            return true;
        }
        return this.connection.isWrapperFor(iface);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.nativeSQL(sql);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    protected void passivate() throws SQLException {
        List<AbandonedTrace> traceList = this.getTrace();
        if (!Utils.isEmpty(traceList)) {
            ArrayList thrownList = new ArrayList();
            traceList.forEach(trace -> trace.close(thrownList::add));
            this.clearTrace();
            if (!thrownList.isEmpty()) {
                throw new SQLExceptionList(thrownList);
            }
        }
        this.setLastUsed(Instant.EPOCH);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingCallableStatement(this, this.connection.prepareCall(sql)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingCallableStatement(this, this.connection.prepareCall(sql, resultSetType, resultSetConcurrency)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingCallableStatement(this, this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, autoGeneratedKeys)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, columnIndexes)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.checkOpen();
        try {
            return this.init(new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, columnNames)));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this.connection.releaseSavepoint(savepoint);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void rollback() throws SQLException {
        this.checkOpen();
        try {
            this.connection.rollback();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this.connection.rollback(savepoint);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setAutoCommit(autoCommit);
            if (this.cacheState) {
                this.cachedAutoCommit = this.connection.getAutoCommit();
            }
        }
        catch (SQLException e) {
            this.cachedAutoCommit = null;
            this.handleException(e);
        }
    }

    public void setCacheState(boolean cacheState) {
        this.cacheState = cacheState;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setCatalog(catalog);
            if (this.cacheState) {
                this.cachedCatalog = this.connection.getCatalog();
            }
        }
        catch (SQLException e) {
            this.cachedCatalog = null;
            this.handleException(e);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this.connection.setClientInfo(properties);
        }
        catch (SQLClientInfoException e) {
            throw e;
        }
        catch (SQLException e) {
            throw new SQLClientInfoException("Connection is closed.", EMPTY_FAILED_PROPERTIES, (Throwable)e);
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this.connection.setClientInfo(name, value);
        }
        catch (SQLClientInfoException e) {
            throw e;
        }
        catch (SQLException e) {
            throw new SQLClientInfoException("Connection is closed.", EMPTY_FAILED_PROPERTIES, (Throwable)e);
        }
    }

    protected void setClosedInternal(boolean closed) {
        this.closed = closed;
    }

    public void setDefaultQueryTimeout(Duration defaultQueryTimeoutDuration) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutDuration;
    }

    @Deprecated
    public void setDefaultQueryTimeout(Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutSeconds == null ? null : Duration.ofSeconds(defaultQueryTimeoutSeconds.intValue());
    }

    public void setDelegate(C connection) {
        this.connection = connection;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setHoldability(holdability);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.checkOpen();
        try {
            Jdbc41Bridge.setNetworkTimeout(this.connection, executor, milliseconds);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setReadOnly(readOnly);
            if (this.cacheState) {
                this.cachedReadOnly = this.connection.isReadOnly();
            }
        }
        catch (SQLException e) {
            this.cachedReadOnly = null;
            this.handleException(e);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.setSavepoint();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.setSavepoint(name);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.checkOpen();
        try {
            Jdbc41Bridge.setSchema(this.connection, schema);
            if (this.cacheState) {
                this.cachedSchema = this.connection.getSchema();
            }
        }
        catch (SQLException e) {
            this.cachedSchema = null;
            this.handleException(e);
        }
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setTransactionIsolation(level);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setTypeMap(map);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    public synchronized String toString() {
        String str = null;
        Connection conn = this.getInnermostDelegateInternal();
        if (conn != null) {
            try {
                if (conn.isClosed()) {
                    str = "connection is closed";
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.hashCode());
                    DatabaseMetaData meta = conn.getMetaData();
                    if (meta != null) {
                        sb.append(", URL=");
                        sb.append(meta.getURL());
                        sb.append(", ");
                        sb.append(meta.getDriverName());
                        str = sb.toString();
                    }
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        return str != null ? str : super.toString();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.connection.getClass())) {
            return iface.cast(this.connection);
        }
        return this.connection.unwrap(iface);
    }
}

