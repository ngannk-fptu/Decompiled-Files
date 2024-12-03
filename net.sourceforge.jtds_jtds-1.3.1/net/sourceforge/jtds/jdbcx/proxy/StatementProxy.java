/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;

public class StatementProxy
implements Statement {
    private ConnectionProxy _connection;
    private JtdsStatement _statement;

    StatementProxy(ConnectionProxy connection, JtdsStatement statement) {
        this._connection = connection;
        this._statement = statement;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeQuery(sql);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeUpdate(sql);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void close() throws SQLException {
        this.validateConnection();
        try {
            this._statement.close();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getMaxFieldSize();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setMaxFieldSize(max);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int getMaxRows() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getMaxRows();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setMaxRows(max);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setEscapeProcessing(enable);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getQueryTimeout();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setQueryTimeout(seconds);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void cancel() throws SQLException {
        this.validateConnection();
        try {
            this._statement.cancel();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getWarnings();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.validateConnection();
        try {
            this._statement.clearWarnings();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setCursorName(name);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.execute(sql);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getResultSet();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public int getUpdateCount() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getUpdateCount();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getMoreResults();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setFetchDirection(direction);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getFetchDirection();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.validateConnection();
        try {
            this._statement.setFetchSize(rows);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getFetchSize();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getResultSetConcurrency();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int getResultSetType() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getResultSetType();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        this.validateConnection();
        try {
            this._statement.addBatch(sql);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        this.validateConnection();
        try {
            this._statement.clearBatch();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeBatch();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getConnection();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getMoreResults(current);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getGeneratedKeys();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeUpdate(sql, autoGeneratedKeys);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeUpdate(sql, columnIndexes);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.executeUpdate(sql, columnNames);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.execute(sql, autoGeneratedKeys);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.execute(sql, columnIndexes);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        this.validateConnection();
        try {
            return this._statement.execute(sql, columnNames);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        this.validateConnection();
        try {
            return this._statement.getResultSetHoldability();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    protected void validateConnection() throws SQLException {
        if (this._connection.isClosed()) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    protected void processSQLException(SQLException sqlException) throws SQLException {
        this._connection.processSQLException(sqlException);
        throw sqlException;
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }
}

