/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import net.java.ao.DelegatePreparedStatement;
import net.java.ao.sql.LoggingInterceptor;

class DelegateLoggingPreparedStatement
extends DelegatePreparedStatement {
    private String query;

    DelegateLoggingPreparedStatement(PreparedStatement statement, LoggingInterceptor logger, String query) {
        super(statement, logger);
        this.query = query;
    }

    private void delegateSetParam(int index, String value, DelegateVoidBlock block) throws SQLException {
        try {
            block.invoke();
            this.params.put(index, value);
        }
        catch (SQLException e) {
            this.logger.onException(e);
            throw e;
        }
    }

    @Override
    public boolean execute() throws SQLException {
        return this.delegateExecute(this.query, this.preparedStatement::execute);
    }

    @Override
    public void addBatch() throws SQLException {
        this.preparedStatement.addBatch();
        if (this.batchQueryBuffer.length() > 0) {
            this.batchQueryBuffer.append("\n");
        }
        this.batchQueryBuffer.append(this.query);
        if (!this.params.isEmpty()) {
            this.batchQueryBuffer.append(" ").append(this.params.toString());
        }
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        return this.delegateExecute(this.query, this.preparedStatement::executeLargeUpdate);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.delegateExecute(this.query, this.preparedStatement::executeQuery);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return this.delegateExecute(this.query, this.preparedStatement::executeUpdate);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.preparedStatement.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.preparedStatement.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setDate(parameterIndex, x, cal));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setTime(parameterIndex, x, cal));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setTimestamp(parameterIndex, x, cal));
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.delegateSetParam(parameterIndex, "null", () -> this.preparedStatement.setNull(parameterIndex, sqlType, typeName));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setURL(parameterIndex, x));
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.preparedStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setRowId(parameterIndex, x));
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        this.delegateSetParam(parameterIndex, value, () -> this.preparedStatement.setNString(parameterIndex, value));
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        this.delegateSetParam(parameterIndex, xmlObject.getString(), () -> this.preparedStatement.setSQLXML(parameterIndex, xmlObject));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength));
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength));
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setObject(parameterIndex, x, targetSqlType));
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.delegateSetParam(parameterIndex, "null", () -> this.preparedStatement.setNull(parameterIndex, sqlType));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setBoolean(parameterIndex, x));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setShort(parameterIndex, x));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setInt(parameterIndex, x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setLong(parameterIndex, x));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setFloat(parameterIndex, x));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setDouble(parameterIndex, x));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setBigDecimal(parameterIndex, x));
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.delegateSetParam(parameterIndex, x, () -> this.preparedStatement.setString(parameterIndex, x));
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setDate(parameterIndex, x));
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setTime(parameterIndex, x));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setTimestamp(parameterIndex, x));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setObject(parameterIndex, x, targetSqlType));
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.delegateSetParam(parameterIndex, String.valueOf(x), () -> this.preparedStatement.setObject(parameterIndex, x));
    }

    @Override
    public void clearParameters() throws SQLException {
        this.preparedStatement.clearParameters();
    }

    @FunctionalInterface
    protected static interface DelegateVoidBlock {
        public void invoke() throws SQLException;
    }
}

