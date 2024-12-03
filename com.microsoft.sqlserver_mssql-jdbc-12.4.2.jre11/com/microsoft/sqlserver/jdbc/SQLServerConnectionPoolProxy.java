/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

class SQLServerConnectionPoolProxy
implements ISQLServerConnection,
Serializable {
    private static final long serialVersionUID = 5752599482349578127L;
    private SQLServerConnection wrappedConnection;
    private boolean bIsOpen;
    private static final AtomicInteger baseConnectionID = new AtomicInteger(0);
    private final String traceID = " ProxyConnectionID:" + SQLServerConnectionPoolProxy.nextConnectionID();
    private static final String CALL_ABORT_PERM = "callAbort";

    private static int nextConnectionID() {
        return baseConnectionID.incrementAndGet();
    }

    public String toString() {
        return this.traceID;
    }

    SQLServerConnectionPoolProxy(SQLServerConnection con) {
        this.wrappedConnection = con;
        con.setAssociatedProxy(this);
        this.bIsOpen = true;
    }

    SQLServerConnection getWrappedConnection() {
        return this.wrappedConnection;
    }

    void checkClosed() throws SQLServerException {
        if (!this.bIsOpen) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08006", false);
        }
    }

    @Override
    public Statement createStatement() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean newAutoCommitMode) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setAutoCommit(newAutoCommitMode);
    }

    @Override
    public boolean getAutoCommit() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.commit();
    }

    @Override
    public void rollback() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.rollback();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        SecurityManager secMgr;
        if (!this.bIsOpen || null == this.wrappedConnection) {
            return;
        }
        if (null == executor) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            Object[] msgArgs = new Object[]{"executor"};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
        if ((secMgr = System.getSecurityManager()) != null) {
            try {
                SQLPermission perm = new SQLPermission(CALL_ABORT_PERM);
                secMgr.checkPermission(perm);
            }
            catch (SecurityException ex) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                Object[] msgArgs = new Object[]{CALL_ABORT_PERM};
                throw new SQLServerException(form.format(msgArgs), null, 0, (Throwable)ex);
            }
        }
        this.bIsOpen = false;
        if (null == executor) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            Object[] msgArgs = new Object[]{"executor"};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    if (SQLServerConnectionPoolProxy.this.wrappedConnection.getConnectionLogger().isLoggable(Level.FINER)) {
                        SQLServerConnectionPoolProxy.this.wrappedConnection.getConnectionLogger().finer(this.toString() + " Connection proxy aborted ");
                    }
                    try {
                        SQLServerConnectionPoolProxy.this.wrappedConnection.poolCloseEventNotify();
                        SQLServerConnectionPoolProxy.this.wrappedConnection = null;
                    }
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @Override
    public void close() throws SQLServerException {
        if (this.bIsOpen && null != this.wrappedConnection) {
            if (this.wrappedConnection.getConnectionLogger().isLoggable(Level.FINER)) {
                this.wrappedConnection.getConnectionLogger().finer(this.toString() + " Connection proxy closed ");
            }
            this.wrappedConnection.poolCloseEventNotify();
            this.wrappedConnection = null;
        }
        this.bIsOpen = false;
    }

    void internalClose() {
        this.bIsOpen = false;
        this.wrappedConnection = null;
    }

    @Override
    public boolean isClosed() throws SQLServerException {
        return !this.bIsOpen;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sSql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sSql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setTypeMap(map);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getTypeMap();
    }

    @Override
    public Statement createStatement(int nType, int nConcur, int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(nType, nConcur, nHold);
    }

    @Override
    public Statement createStatement(int nType, int nConcur, int nHold, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(nType, nConcur, nHold, stmtColEncSetting);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int nType, int nConcur, int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, nType, nConcur, nHold);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int nType, int nConcur, int nHold, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, nType, nConcur, nHold, stmtColEncSetting);
    }

    @Override
    public CallableStatement prepareCall(String sql, int nType, int nConcur, int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, nType, nConcur, nHold);
    }

    @Override
    public CallableStatement prepareCall(String sql, int nType, int nConcur, int nHold, SQLServerStatementColumnEncryptionSetting stmtColEncSetiing) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, nType, nConcur, nHold, stmtColEncSetiing);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int flag) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, flag);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int flag, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, flag, stmtColEncSetting);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnIndexes, stmtColEncSetting);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnNames, stmtColEncSetting);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Savepoint setSavepoint(String sName) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.setSavepoint(sName);
    }

    @Override
    public Savepoint setSavepoint() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.setSavepoint();
    }

    @Override
    public void rollback(Savepoint s) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.rollback(s);
    }

    @Override
    public int getHoldability() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getHoldability();
    }

    @Override
    public void setHoldability(int nNewHold) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setHoldability(nNewHold);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getNetworkTimeout();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int timeout) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setNetworkTimeout(executor, timeout);
    }

    @Override
    public String getSchema() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setSchema(schema);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createSQLXML();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createStruct(typeName, attributes);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getClientInfo(name);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(name, value);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.isValid(timeout);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        this.wrappedConnection.getConnectionLogger().entering(this.toString(), "isWrapperFor", iface);
        boolean f = iface.isInstance(this);
        this.wrappedConnection.getConnectionLogger().exiting(this.toString(), "isWrapperFor", f);
        return f;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        this.wrappedConnection.getConnectionLogger().entering(this.toString(), "unwrap", iface);
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        this.wrappedConnection.getConnectionLogger().exiting(this.toString(), "unwrap", t);
        return t;
    }

    @Override
    public UUID getClientConnectionId() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getClientConnectionId();
    }

    @Override
    public void setSendTimeAsDatetime(boolean sendTimeAsDateTimeValue) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setSendTimeAsDatetime(sendTimeAsDateTimeValue);
    }

    @Override
    public boolean getSendTimeAsDatetime() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getSendTimeAsDatetime();
    }

    @Override
    public void setDatetimeParameterType(String datetimeParameterTypeValue) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setDatetimeParameterType(datetimeParameterTypeValue);
    }

    @Override
    public String getDatetimeParameterType() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getDatetimeParameterType();
    }

    @Override
    public int getDiscardedServerPreparedStatementCount() {
        return this.wrappedConnection.getDiscardedServerPreparedStatementCount();
    }

    @Override
    public void closeUnreferencedPreparedStatementHandles() {
        this.wrappedConnection.closeUnreferencedPreparedStatementHandles();
    }

    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        return this.wrappedConnection.getEnablePrepareOnFirstPreparedStatementCall();
    }

    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(boolean value) {
        this.wrappedConnection.setEnablePrepareOnFirstPreparedStatementCall(value);
    }

    @Override
    public String getPrepareMethod() {
        return this.wrappedConnection.getPrepareMethod();
    }

    @Override
    public void setPrepareMethod(String prepareMethod) {
        this.wrappedConnection.setPrepareMethod(prepareMethod);
    }

    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        return this.wrappedConnection.getServerPreparedStatementDiscardThreshold();
    }

    @Override
    public void setServerPreparedStatementDiscardThreshold(int value) {
        this.wrappedConnection.setServerPreparedStatementDiscardThreshold(value);
    }

    @Override
    public void setStatementPoolingCacheSize(int value) {
        this.wrappedConnection.setStatementPoolingCacheSize(value);
    }

    @Override
    public int getStatementPoolingCacheSize() {
        return this.wrappedConnection.getStatementPoolingCacheSize();
    }

    @Override
    public boolean isStatementPoolingEnabled() {
        return this.wrappedConnection.isStatementPoolingEnabled();
    }

    @Override
    public int getStatementHandleCacheEntryCount() {
        return this.wrappedConnection.getStatementHandleCacheEntryCount();
    }

    @Override
    public void setDisableStatementPooling(boolean value) {
        this.wrappedConnection.setDisableStatementPooling(value);
    }

    @Override
    public boolean getDisableStatementPooling() {
        return this.wrappedConnection.getDisableStatementPooling();
    }

    @Override
    public void setUseFmtOnly(boolean useFmtOnly) {
        this.wrappedConnection.setUseFmtOnly(useFmtOnly);
    }

    @Override
    public boolean getUseFmtOnly() {
        return this.wrappedConnection.getUseFmtOnly();
    }

    @Override
    public boolean getDelayLoadingLobs() {
        return this.wrappedConnection.getDelayLoadingLobs();
    }

    @Override
    public void setDelayLoadingLobs(boolean delayLoadingLobs) {
        this.wrappedConnection.setDelayLoadingLobs(delayLoadingLobs);
    }

    @Override
    public void setIPAddressPreference(String iPAddressPreference) {
        this.wrappedConnection.setIPAddressPreference(iPAddressPreference);
    }

    @Override
    public String getIPAddressPreference() {
        return this.wrappedConnection.getIPAddressPreference();
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public int getMsiTokenCacheTtl() {
        return 0;
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMsiTokenCacheTtl(int timeToLive) {
    }

    @Override
    public String getAccessTokenCallbackClass() {
        return this.wrappedConnection.getAccessTokenCallbackClass();
    }

    @Override
    public void setAccessTokenCallbackClass(String accessTokenCallbackClass) {
        this.wrappedConnection.setAccessTokenCallbackClass(accessTokenCallbackClass);
    }
}

