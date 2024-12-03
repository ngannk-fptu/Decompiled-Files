/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.PooledConnection;
import net.sourceforge.jtds.jdbcx.proxy.CallableStatementProxy;
import net.sourceforge.jtds.jdbcx.proxy.PreparedStatementProxy;
import net.sourceforge.jtds.jdbcx.proxy.StatementProxy;

public class ConnectionProxy
implements Connection {
    private PooledConnection _pooledConnection;
    private JtdsConnection _connection;
    private boolean _closed;

    public ConnectionProxy(PooledConnection pooledConnection, Connection connection) {
        this._pooledConnection = pooledConnection;
        this._connection = (JtdsConnection)connection;
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.validateConnection();
        try {
            this._connection.clearWarnings();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void close() {
        if (this._closed) {
            return;
        }
        this._pooledConnection.fireConnectionEvent(true, null);
        this._closed = true;
    }

    @Override
    public void commit() throws SQLException {
        this.validateConnection();
        try {
            this._connection.commit();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        this.validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement)this._connection.createStatement());
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement)this._connection.createStatement(resultSetType, resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement)this._connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getAutoCommit();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getCatalog();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getHoldability();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getTransactionIsolation();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    public Map getTypeMap() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getTypeMap();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getWarnings();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.getMetaData();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (this._closed) {
            return true;
        }
        try {
            return this._connection.isClosed();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return this._closed;
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.isReadOnly();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.validateConnection();
        try {
            return this._connection.nativeSQL(sql);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement)this._connection.prepareCall(sql));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement)this._connection.prepareCall(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement)this._connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql, autoGeneratedKeys));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql, columnIndexes));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql, columnNames));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement)this._connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.validateConnection();
        try {
            this._connection.releaseSavepoint(savepoint);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void rollback() throws SQLException {
        this.validateConnection();
        try {
            this._connection.rollback();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.validateConnection();
        try {
            this._connection.rollback(savepoint);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setAutoCommit(autoCommit);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setCatalog(catalog);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setHoldability(holdability);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setReadOnly(readOnly);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.validateConnection();
        try {
            return this._connection.setSavepoint();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        this.validateConnection();
        try {
            return this._connection.setSavepoint(name);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setTransactionIsolation(level);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    public void setTypeMap(Map map) throws SQLException {
        this.validateConnection();
        try {
            this._connection.setTypeMap(map);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    private void validateConnection() throws SQLException {
        if (this._closed) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    void processSQLException(SQLException sqlException) throws SQLException {
        this._pooledConnection.fireConnectionEvent(false, sqlException);
        throw sqlException;
    }

    protected void finalize() {
        this.close();
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
}

