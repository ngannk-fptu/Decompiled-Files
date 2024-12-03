/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.ISQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.StreamDone;
import com.microsoft.sqlserver.jdbc.StreamRetValue;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSTokenHandler;
import com.microsoft.sqlserver.jdbc.ThreePartName;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import microsoft.sql.DateTimeOffset;

public class SQLServerCallableStatement
extends SQLServerPreparedStatement
implements ISQLServerCallableStatement {
    private static final long serialVersionUID = 5044984771674532350L;
    private static final String GET_TIMESTAMP = "getTimestamp";
    private static final String SQLSTATE_07009 = "07009";
    private HashMap<String, Integer> parameterNames;
    private TreeMap<String, Integer> insensitiveParameterNames;
    int nOutParams = 0;
    int nOutParamsAssigned = 0;
    private int outParamIndex = -1;
    private transient Parameter lastParamAccessed;
    private transient Closeable activeStream;
    private Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();
    AtomicInteger ai = new AtomicInteger(0);

    SQLServerCallableStatement(SQLServerConnection connection, String sql, int nRSType, int nRSConcur, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        super(connection, sql, nRSType, nRSConcur, stmtColEncSetting);
    }

    @Override
    public void registerOutParameter(int index, int sqlType) throws SQLServerException {
        Object[] msgArgs;
        MessageFormat form;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{index, sqlType});
        }
        this.checkClosed();
        if (index < 1 || index > this.inOutParam.length) {
            form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            msgArgs = new Object[]{index};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "7009", false);
        }
        if (2012 == sqlType) {
            form = new MessageFormat(SQLServerException.getErrString("R_featureNotSupported"));
            msgArgs = new Object[]{"REF_CURSOR"};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
        }
        JDBCType jdbcType = JDBCType.of(sqlType);
        this.discardLastExecutionResults();
        if (jdbcType.isUnsupported()) {
            jdbcType = JDBCType.BINARY;
        }
        Parameter param = this.inOutParam[index - 1];
        assert (null != param);
        if (!param.isOutput()) {
            ++this.nOutParams;
        }
        param.registerForOutput(jdbcType, this.connection);
        switch (sqlType) {
            case -151: {
                param.setOutScale(3);
                break;
            }
            case -155: 
            case 92: 
            case 93: {
                param.setOutScale(7);
                break;
            }
        }
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    private Parameter getOutParameter(int i) throws SQLServerException {
        this.processResults();
        if (this.inOutParam[i - 1] == this.lastParamAccessed || this.inOutParam[i - 1].isValueGotten()) {
            return this.inOutParam[i - 1];
        }
        while (this.outParamIndex != i - 1) {
            this.skipOutParameters(1, false);
        }
        return this.inOutParam[i - 1];
    }

    @Override
    void startResults() {
        super.startResults();
        this.outParamIndex = -1;
        this.nOutParamsAssigned = 0;
        this.lastParamAccessed = null;
        assert (null == this.activeStream);
    }

    @Override
    void processBatch() throws SQLServerException {
        this.processResults();
        assert (this.nOutParams >= 0);
        if (this.nOutParams > 0) {
            this.processOutParameters();
            this.processBatchRemainder();
        }
    }

    final void processOutParameters() throws SQLServerException {
        assert (this.nOutParams > 0);
        assert (null != this.inOutParam);
        this.closeActiveStream();
        if (this.outParamIndex >= 0) {
            for (int index = 0; index < this.inOutParam.length; ++index) {
                if (index == this.outParamIndex || !this.inOutParam[index].isValueGotten()) continue;
                assert (this.inOutParam[index].isOutput());
                this.inOutParam[index].resetOutputValue();
            }
        }
        assert (this.nOutParamsAssigned <= this.nOutParams);
        if (this.nOutParamsAssigned < this.nOutParams) {
            this.skipOutParameters(this.nOutParams - this.nOutParamsAssigned, true);
        }
        if (this.outParamIndex >= 0) {
            this.inOutParam[this.outParamIndex].skipValue(this.resultsReader(), true);
            this.inOutParam[this.outParamIndex].resetOutputValue();
            this.outParamIndex = -1;
        }
    }

    private void processBatchRemainder() throws SQLServerException {
        final class ExecDoneHandler
        extends TDSTokenHandler {
            ExecDoneHandler() {
                super("ExecDoneHandler");
            }

            @Override
            boolean onDone(TDSReader tdsReader) throws SQLServerException {
                StreamDone doneToken = new StreamDone();
                doneToken.setFromTDS(tdsReader);
                if (doneToken.isFinal()) {
                    SQLServerCallableStatement.this.connection.getSessionRecovery().decrementUnprocessedResponseCount();
                }
                if (doneToken.wasRPCInBatch()) {
                    SQLServerCallableStatement.this.startResults();
                    return false;
                }
                return true;
            }
        }
        ExecDoneHandler execDoneHandler = new ExecDoneHandler();
        TDSParser.parse(this.resultsReader(), execDoneHandler);
    }

    private void skipOutParameters(int numParamsToSkip, boolean discardValues) throws SQLServerException {
        final class OutParamHandler
        extends TDSTokenHandler {
            final StreamRetValue srv;
            private boolean foundParam;

            final boolean foundParam() {
                return this.foundParam;
            }

            OutParamHandler() {
                super("OutParamHandler");
                this.srv = new StreamRetValue();
            }

            final void reset() {
                this.foundParam = false;
            }

            @Override
            boolean onRetValue(TDSReader tdsReader) throws SQLServerException {
                this.srv.setFromTDS(tdsReader);
                this.foundParam = true;
                return false;
            }
        }
        OutParamHandler outParamHandler = new OutParamHandler();
        assert (numParamsToSkip <= this.nOutParams - this.nOutParamsAssigned);
        for (int paramsSkipped = 0; paramsSkipped < numParamsToSkip; ++paramsSkipped) {
            if (-1 != this.outParamIndex) {
                this.inOutParam[this.outParamIndex].skipValue(this.resultsReader(), discardValues);
                if (discardValues) {
                    this.inOutParam[this.outParamIndex].resetOutputValue();
                }
            }
            outParamHandler.reset();
            TDSParser.parse(this.resultsReader(), outParamHandler);
            if (!outParamHandler.foundParam()) {
                if (discardValues) break;
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueNotSetForParameter"));
                Object[] msgArgs = new Object[]{this.outParamIndex + 1};
                SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
            }
            this.outParamIndex = outParamHandler.srv.getOrdinalOrLength();
            this.outParamIndex -= this.outParamIndexAdjustment;
            if (this.outParamIndex < 0 || this.outParamIndex >= this.inOutParam.length || !this.inOutParam[this.outParamIndex].isOutput()) {
                if (this.getStatementLogger().isLoggable(Level.INFO)) {
                    this.getStatementLogger().info(this.toString() + " Unexpected outParamIndex: " + this.outParamIndex + "; adjustment: " + this.outParamIndexAdjustment);
                }
                this.connection.throwInvalidTDS();
            }
            ++this.nOutParamsAssigned;
        }
    }

    @Override
    public void registerOutParameter(int index, int sqlType, String typeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{index, sqlType, typeName});
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int index, int sqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{index, sqlType, scale});
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        this.inOutParam[index - 1].setOutScale(scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int index, int sqlType, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{index, sqlType, scale, precision});
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        this.inOutParam[index - 1].setValueLength(precision);
        this.inOutParam[index - 1].setOutScale(scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    private Parameter getterGetParam(int index) throws SQLServerException {
        Object[] msgArgs;
        MessageFormat form;
        this.checkClosed();
        if (index < 1 || index > this.inOutParam.length) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidOutputParameter"));
            msgArgs = new Object[]{index};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), SQLSTATE_07009, false);
        }
        if (!this.inOutParam[index - 1].isOutput()) {
            form = new MessageFormat(SQLServerException.getErrString("R_outputParameterNotRegisteredForOutput"));
            msgArgs = new Object[]{index};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), SQLSTATE_07009, true);
        }
        if (!this.wasExecuted()) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_statementMustBeExecuted"), SQLSTATE_07009, false);
        }
        this.resultsReader().getCommand().checkForInterrupt();
        this.closeActiveStream();
        if (this.getStatementLogger().isLoggable(Level.FINER)) {
            this.getStatementLogger().finer(this.toString() + " Getting Param:" + index);
        }
        this.lastParamAccessed = this.getOutParameter(index);
        return this.lastParamAccessed;
    }

    private Object getValue(int parameterIndex, JDBCType jdbcType) throws SQLServerException {
        return this.getterGetParam(parameterIndex).getValue(jdbcType, null, null, this.resultsReader(), this);
    }

    private Object getValue(int parameterIndex, JDBCType jdbcType, Calendar cal) throws SQLServerException {
        return this.getterGetParam(parameterIndex).getValue(jdbcType, null, cal, this.resultsReader(), this);
    }

    private Object getStream(int parameterIndex, StreamType streamType) throws SQLServerException {
        Object value = this.getterGetParam(parameterIndex).getValue(streamType.getJDBCType(), new InputStreamGetterArgs(streamType, this.getIsResponseBufferingAdaptive(), this.getIsResponseBufferingAdaptive(), this.toString()), null, this.resultsReader(), this);
        this.activeStream = (Closeable)value;
        return value;
    }

    private Object getSQLXMLInternal(int parameterIndex) throws SQLServerException {
        SQLServerSQLXML value = (SQLServerSQLXML)this.getterGetParam(parameterIndex).getValue(JDBCType.SQLXML, new InputStreamGetterArgs(StreamType.SQLXML, this.getIsResponseBufferingAdaptive(), this.getIsResponseBufferingAdaptive(), this.toString()), null, this.resultsReader(), this);
        if (null != value) {
            this.activeStream = value.getStream();
        }
        return value;
    }

    @Override
    public int getInt(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getInt", index);
        this.checkClosed();
        Integer value = (Integer)this.getValue(index, JDBCType.INTEGER);
        loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return null != value ? value : 0;
    }

    @Override
    public int getInt(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getInt", parameterName);
        this.checkClosed();
        Integer value = (Integer)this.getValue(this.findColumn(parameterName), JDBCType.INTEGER);
        loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return null != value ? value : 0;
    }

    @Override
    public String getString(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getString", index);
        this.checkClosed();
        String value = null;
        Object objectValue = this.getValue(index, JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }

    @Override
    public String getString(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getString", parameterName);
        this.checkClosed();
        String value = null;
        Object objectValue = this.getValue(this.findColumn(parameterName), JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }

    @Override
    public final String getNString(int parameterIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNString", parameterIndex);
        this.checkClosed();
        String value = (String)this.getValue(parameterIndex, JDBCType.NCHAR);
        loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }

    @Override
    public final String getNString(String parameterName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNString", parameterName);
        this.checkClosed();
        String value = (String)this.getValue(this.findColumn(parameterName), JDBCType.NCHAR);
        loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }

    @Override
    @Deprecated(since="6.5.4")
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[]{parameterIndex, scale});
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    @Deprecated(since="6.5.4")
    public BigDecimal getBigDecimal(String parameterName, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[]{parameterName, scale});
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public boolean getBoolean(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBoolean", index);
        this.checkClosed();
        Boolean value = (Boolean)this.getValue(index, JDBCType.BIT);
        loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value ? value : false;
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBoolean", parameterName);
        this.checkClosed();
        Boolean value = (Boolean)this.getValue(this.findColumn(parameterName), JDBCType.BIT);
        loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value ? value : false;
    }

    @Override
    public byte getByte(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getByte", index);
        this.checkClosed();
        Short shortValue = (Short)this.getValue(index, JDBCType.TINYINT);
        byte byteValue = null != shortValue ? shortValue.byteValue() : (byte)0;
        loggerExternal.exiting(this.getClassNameLogging(), "getByte", byteValue);
        return byteValue;
    }

    @Override
    public byte getByte(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getByte", parameterName);
        this.checkClosed();
        Short shortValue = (Short)this.getValue(this.findColumn(parameterName), JDBCType.TINYINT);
        byte byteValue = null != shortValue ? shortValue.byteValue() : (byte)0;
        loggerExternal.exiting(this.getClassNameLogging(), "getByte", byteValue);
        return byteValue;
    }

    @Override
    public byte[] getBytes(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBytes", index);
        this.checkClosed();
        byte[] value = (byte[])this.getValue(index, JDBCType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBytes", parameterName);
        this.checkClosed();
        byte[] value = (byte[])this.getValue(this.findColumn(parameterName), JDBCType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }

    @Override
    public Date getDate(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDate", index);
        this.checkClosed();
        Date value = (Date)this.getValue(index, JDBCType.DATE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDate", parameterName);
        this.checkClosed();
        Date value = (Date)this.getValue(this.findColumn(parameterName), JDBCType.DATE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(int index, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[]{index, cal});
        }
        this.checkClosed();
        Date value = (Date)this.getValue(index, JDBCType.DATE, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[]{parameterName, cal});
        }
        this.checkClosed();
        Date value = (Date)this.getValue(this.findColumn(parameterName), JDBCType.DATE, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public double getDouble(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDouble", index);
        this.checkClosed();
        Double value = (Double)this.getValue(index, JDBCType.DOUBLE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return null != value ? value : 0.0;
    }

    @Override
    public double getDouble(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDouble", parameterName);
        this.checkClosed();
        Double value = (Double)this.getValue(this.findColumn(parameterName), JDBCType.DOUBLE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return null != value ? value : 0.0;
    }

    @Override
    public float getFloat(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", index);
        this.checkClosed();
        Float value = (Float)this.getValue(index, JDBCType.REAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return null != value ? value.floatValue() : 0.0f;
    }

    @Override
    public float getFloat(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", parameterName);
        this.checkClosed();
        Float value = (Float)this.getValue(this.findColumn(parameterName), JDBCType.REAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return null != value ? value.floatValue() : 0.0f;
    }

    @Override
    public long getLong(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getLong", index);
        this.checkClosed();
        Long value = (Long)this.getValue(index, JDBCType.BIGINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return null != value ? value : 0L;
    }

    @Override
    public long getLong(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getLong", parameterName);
        this.checkClosed();
        Long value = (Long)this.getValue(this.findColumn(parameterName), JDBCType.BIGINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return null != value ? value : 0L;
    }

    @Override
    public Object getObject(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", index);
        this.checkClosed();
        Object value = this.getValue(index, null != this.getterGetParam(index).getJdbcTypeSetByUser() ? this.getterGetParam(index).getJdbcTypeSetByUser() : this.getterGetParam(index).getJdbcType());
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public <T> T getObject(int index, Class<T> type) throws SQLException {
        Comparable<Comparable<ChronoLocalDate>> returnValue;
        loggerExternal.entering(this.getClassNameLogging(), "getObject", index);
        this.checkClosed();
        if (type == String.class) {
            returnValue = this.getString(index);
        } else if (type == Byte.class) {
            byte byteValue = this.getByte(index);
            returnValue = this.wasNull() ? null : Byte.valueOf(byteValue);
        } else if (type == Short.class) {
            short shortValue = this.getShort(index);
            returnValue = this.wasNull() ? null : Short.valueOf(shortValue);
        } else if (type == Integer.class) {
            int intValue = this.getInt(index);
            returnValue = this.wasNull() ? null : Integer.valueOf(intValue);
        } else if (type == Long.class) {
            long longValue = this.getLong(index);
            returnValue = this.wasNull() ? null : Long.valueOf(longValue);
        } else if (type == BigDecimal.class) {
            returnValue = this.getBigDecimal(index);
        } else if (type == Boolean.class) {
            boolean booleanValue = this.getBoolean(index);
            returnValue = this.wasNull() ? null : Boolean.valueOf(booleanValue);
        } else if (type == Date.class) {
            returnValue = this.getDate(index);
        } else if (type == Time.class) {
            returnValue = this.getTime(index);
        } else if (type == Timestamp.class) {
            returnValue = this.getTimestamp(index);
        } else if (type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class) {
            LocalDateTime ldt = this.getLocalDateTime(index);
            returnValue = null == ldt ? null : (type == LocalDateTime.class ? ldt : (type == LocalDate.class ? ldt.toLocalDate() : ldt.toLocalTime()));
        } else if (type == OffsetDateTime.class) {
            DateTimeOffset dateTimeOffset = this.getDateTimeOffset(index);
            returnValue = dateTimeOffset == null ? null : dateTimeOffset.getOffsetDateTime();
        } else if (type == OffsetTime.class) {
            DateTimeOffset dateTimeOffset = this.getDateTimeOffset(index);
            returnValue = dateTimeOffset == null ? null : dateTimeOffset.getOffsetDateTime().toOffsetTime();
        } else if (type == DateTimeOffset.class) {
            returnValue = this.getDateTimeOffset(index);
        } else if (type == UUID.class) {
            byte[] guid = this.getBytes(index);
            returnValue = null != guid ? Util.readGUIDtoUUID(guid) : null;
        } else if (type == SQLXML.class) {
            returnValue = this.getSQLXML(index);
        } else if (type == Blob.class) {
            returnValue = this.getBlob(index);
        } else if (type == Clob.class) {
            returnValue = this.getClob(index);
        } else if (type == NClob.class) {
            returnValue = this.getNClob(index);
        } else if (type == byte[].class) {
            returnValue = (Comparable<Comparable<ChronoLocalDate>>)this.getBytes(index);
        } else if (type == Float.class) {
            float floatValue = this.getFloat(index);
            returnValue = this.wasNull() ? null : Float.valueOf(floatValue);
        } else if (type == Double.class) {
            double doubleValue = this.getDouble(index);
            returnValue = this.wasNull() ? null : Double.valueOf(doubleValue);
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionTo"));
            Object[] msgArgs = new Object[]{type};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", index);
        return type.cast(returnValue);
    }

    @Override
    public Object getObject(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", parameterName);
        this.checkClosed();
        int parameterIndex = this.findColumn(parameterName);
        Object value = this.getValue(parameterIndex, null != this.getterGetParam(parameterIndex).getJdbcTypeSetByUser() ? this.getterGetParam(parameterIndex).getJdbcTypeSetByUser() : this.getterGetParam(parameterIndex).getJdbcType());
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", parameterName);
        this.checkClosed();
        int parameterIndex = this.findColumn(parameterName);
        T value = this.getObject(parameterIndex, type);
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public short getShort(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getShort", index);
        this.checkClosed();
        Short value = (Short)this.getValue(index, JDBCType.SMALLINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return null != value ? value : (short)0;
    }

    @Override
    public short getShort(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getShort", parameterName);
        this.checkClosed();
        Short value = (Short)this.getValue(this.findColumn(parameterName), JDBCType.SMALLINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return null != value ? value : (short)0;
    }

    @Override
    public Time getTime(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTime", index);
        this.checkClosed();
        Time value = (Time)this.getValue(index, JDBCType.TIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTime", parameterName);
        this.checkClosed();
        Time value = (Time)this.getValue(this.findColumn(parameterName), JDBCType.TIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(int index, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[]{index, cal});
        }
        this.checkClosed();
        Time value = (Time)this.getValue(index, JDBCType.TIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[]{parameterName, cal});
        }
        this.checkClosed();
        Time value = (Time)this.getValue(this.findColumn(parameterName), JDBCType.TIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int index) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), GET_TIMESTAMP, index);
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), GET_TIMESTAMP, value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), GET_TIMESTAMP, parameterName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), GET_TIMESTAMP, value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int index, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), GET_TIMESTAMP, new Object[]{index, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), GET_TIMESTAMP, value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String name, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), GET_TIMESTAMP, new Object[]{name, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), GET_TIMESTAMP, value);
        return value;
    }

    LocalDateTime getLocalDateTime(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getLocalDateTime", columnIndex);
        this.checkClosed();
        LocalDateTime value = (LocalDateTime)this.getValue(columnIndex, JDBCType.LOCALDATETIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getLocalDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(int index) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTime", index);
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.DATETIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTime", parameterName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.DATETIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(int index, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[]{index, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.DATETIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(String name, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[]{name, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.DATETIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(int index) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", index);
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.SMALLDATETIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", parameterName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(int index, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[]{index, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(index, JDBCType.SMALLDATETIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(String name, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[]{name, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.SMALLDATETIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public DateTimeOffset getDateTimeOffset(int index) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", index);
        }
        this.checkClosed();
        if (!this.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        DateTimeOffset value = (DateTimeOffset)this.getValue(index, JDBCType.DATETIMEOFFSET);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }

    @Override
    public DateTimeOffset getDateTimeOffset(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", parameterName);
        this.checkClosed();
        if (!this.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        DateTimeOffset value = (DateTimeOffset)this.getValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }

    @Override
    public boolean wasNull() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "wasNull");
        this.checkClosed();
        boolean bWasNull = false;
        if (null != this.lastParamAccessed) {
            bWasNull = this.lastParamAccessed.isNull();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "wasNull", bWasNull);
        return bWasNull;
    }

    @Override
    public final InputStream getAsciiStream(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", parameterIndex);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(parameterIndex, StreamType.ASCII);
        loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }

    @Override
    public final InputStream getAsciiStream(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", parameterName);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(this.findColumn(parameterName), StreamType.ASCII);
        loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", parameterIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", parameterName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public BigDecimal getMoney(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMoney", parameterIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.MONEY);
        loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }

    @Override
    public BigDecimal getMoney(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMoney", parameterName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.MONEY);
        loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }

    @Override
    public BigDecimal getSmallMoney(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", parameterIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.SMALLMONEY);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }

    @Override
    public BigDecimal getSmallMoney(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", parameterName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.SMALLMONEY);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }

    @Override
    public final InputStream getBinaryStream(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", parameterIndex);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(parameterIndex, StreamType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }

    @Override
    public final InputStream getBinaryStream(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", parameterName);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(this.findColumn(parameterName), StreamType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBlob", parameterIndex);
        this.checkClosed();
        Blob value = (Blob)this.getValue(parameterIndex, JDBCType.BLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        return value;
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBlob", parameterName);
        this.checkClosed();
        Blob value = (Blob)this.getValue(this.findColumn(parameterName), JDBCType.BLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        return value;
    }

    @Override
    public final Reader getCharacterStream(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", parameterIndex);
        this.checkClosed();
        Reader reader = (Reader)this.getStream(parameterIndex, StreamType.CHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", reader);
        return reader;
    }

    @Override
    public final Reader getCharacterStream(String parameterName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", parameterName);
        this.checkClosed();
        Reader reader = (Reader)this.getStream(this.findColumn(parameterName), StreamType.CHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getCharacterSream", reader);
        return reader;
    }

    @Override
    public final Reader getNCharacterStream(int parameterIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", parameterIndex);
        this.checkClosed();
        Reader reader = (Reader)this.getStream(parameterIndex, StreamType.NCHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", reader);
        return reader;
    }

    @Override
    public final Reader getNCharacterStream(String parameterName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", parameterName);
        this.checkClosed();
        Reader reader = (Reader)this.getStream(this.findColumn(parameterName), StreamType.NCHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", reader);
        return reader;
    }

    void closeActiveStream() throws SQLServerException {
        if (null != this.activeStream) {
            try {
                this.activeStream.close();
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
            }
            finally {
                this.activeStream = null;
            }
        }
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getClob", parameterIndex);
        this.checkClosed();
        Clob clob = (Clob)this.getValue(parameterIndex, JDBCType.CLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getClob", clob);
        return clob;
    }

    @Override
    public Clob getClob(String parameterName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getClob", parameterName);
        this.checkClosed();
        Clob clob = (Clob)this.getValue(this.findColumn(parameterName), JDBCType.CLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getClob", clob);
        return clob;
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNClob", parameterIndex);
        this.checkClosed();
        NClob nClob = (NClob)this.getValue(parameterIndex, JDBCType.NCLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getNClob", nClob);
        return nClob;
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNClob", parameterName);
        this.checkClosed();
        NClob nClob = (NClob)this.getValue(this.findColumn(parameterName), JDBCType.NCLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getNClob", nClob);
        return nClob;
    }

    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> m) throws SQLException {
        this.checkClosed();
        return this.getObject(this.findColumn(parameterName), m);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        this.checkClosed();
        return this.getRef(this.findColumn(parameterName));
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        this.checkClosed();
        return this.getArray(this.findColumn(parameterName));
    }

    private int findColumn(String columnName) throws SQLServerException {
        Integer matchPos;
        if (null == this.parameterNames) {
            try (SQLServerStatement s = (SQLServerStatement)this.connection.createStatement();){
                ThreePartName threePartName = ThreePartName.parse(this.procedureName);
                StringBuilder metaQuery = new StringBuilder("exec sp_sproc_columns ");
                if (null != threePartName.getDatabasePart()) {
                    metaQuery.append("@procedure_qualifier=");
                    metaQuery.append(threePartName.getDatabasePart());
                    metaQuery.append(", ");
                }
                if (null != threePartName.getOwnerPart()) {
                    metaQuery.append("@procedure_owner=");
                    metaQuery.append(threePartName.getOwnerPart());
                    metaQuery.append(", ");
                }
                if (null != threePartName.getProcedurePart()) {
                    metaQuery.append("@procedure_name=");
                    metaQuery.append(threePartName.getProcedurePart());
                    metaQuery.append(" , @ODBCVer=3, @fUsePattern=0");
                } else {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_parameterNotDefinedForProcedure"));
                    Object[] msgArgs = new Object[]{columnName, ""};
                    SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), SQLSTATE_07009, false);
                }
                try (SQLServerResultSet rs = s.executeQueryInternal(metaQuery.toString());){
                    this.parameterNames = new HashMap();
                    this.insensitiveParameterNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
                    int columnIndex = 0;
                    while (rs.next()) {
                        String p = rs.getString(4).trim();
                        this.parameterNames.put(p, columnIndex);
                        this.insensitiveParameterNames.put(p, columnIndex++);
                    }
                }
            }
            catch (SQLException e) {
                SQLServerException.makeFromDriverError(this.connection, this, e.toString(), null, false);
            }
        }
        if (null != this.parameterNames && this.parameterNames.size() <= 1) {
            return this.map.computeIfAbsent(columnName, ifAbsent -> this.ai.incrementAndGet());
        }
        Object columnNameWithSign = columnName.startsWith("@") ? columnName : "@" + columnName;
        Integer n = matchPos = this.parameterNames != null ? this.parameterNames.get(columnNameWithSign) : null;
        if (null == matchPos) {
            matchPos = this.insensitiveParameterNames.get(columnNameWithSign);
        }
        if (null == matchPos) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_parameterNotDefinedForProcedure"));
            Object[] msgArgs = new Object[]{columnName, this.procedureName};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), SQLSTATE_07009, false);
        }
        if (this.bReturnValueSyntax) {
            return matchPos + 1;
        }
        return matchPos;
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp value, Calendar calendar) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimeStamp", new Object[]{parameterName, value, calendar});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, calendar, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimeStamp");
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp value, Calendar calendar, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimeStamp", new Object[]{parameterName, value, calendar, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, calendar, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimeStamp");
    }

    @Override
    public void setTime(String parameterName, Time value, Calendar calendar) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{parameterName, value, calendar});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, calendar, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public void setTime(String parameterName, Time value, Calendar calendar, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{parameterName, value, calendar, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, calendar, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public void setDate(String parameterName, Date value, Calendar calendar) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{parameterName, value, calendar});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, value, JavaType.DATE, calendar, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public void setDate(String parameterName, Date value, Calendar calendar, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{parameterName, value, calendar, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, value, JavaType.DATE, calendar, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public final void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{parameterName, reader});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setCharacterStream(String parameterName, Reader value, int length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, value, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{parameterName, reader, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, value, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    @Override
    public final void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, value, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    @Override
    public final void setClob(String parameterName, Clob value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.CLOB, (Object)value, JavaType.CLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setClob(String parameterName, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterName, reader});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setClob(String parameterName, Reader value, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, value, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setNClob(String parameterName, NClob value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NCLOB, (Object)value, JavaType.NCLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setNClob(String parameterName, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterName, reader});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterName, reader, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setNString(String parameterName, String value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NVARCHAR, (Object)value, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }

    @Override
    public final void setNString(String parameterName, String value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NVARCHAR, (Object)value, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }

    @Override
    public void setObject(String parameterName, Object value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setObjectNoType(this.findColumn(parameterName), value, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void setObject(String parameterName, Object value, int sqlType) throws SQLServerException {
        String tvpName = null;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, sqlType});
        }
        this.checkClosed();
        if (-153 == sqlType) {
            tvpName = this.getTVPNameFromObject(this.findColumn(parameterName), value);
            this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.TVP, JDBCType.TVP, null, null, false, this.findColumn(parameterName), tvpName);
        } else {
            this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), null, null, false, this.findColumn(parameterName), tvpName);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void setObject(String parameterName, Object value, int sqlType, int decimals) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, sqlType, decimals});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), decimals, null, false, this.findColumn(parameterName), null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void setObject(String parameterName, Object value, int sqlType, int decimals, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, sqlType, decimals, forceEncrypt});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), 2 == sqlType || 3 == sqlType ? Integer.valueOf(decimals) : null, null, forceEncrypt, this.findColumn(parameterName), null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setObject(String parameterName, Object value, int targetSqlType, Integer precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, targetSqlType, precision, scale});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(targetSqlType), 2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(value) || Reader.class.isInstance(value) ? Integer.valueOf(scale) : null, precision, false, this.findColumn(parameterName), null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setAsciiStream(String parameterName, InputStream value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setAsciiStream(String parameterName, InputStream value, int length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setAsciiStream(String parameterName, InputStream value, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setBinaryStream(String parameterName, InputStream value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBinaryStream(String parameterName, InputStream value, int length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBinaryStream(String parameterName, InputStream value, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{parameterName, value, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBlob(String parameterName, Blob inputStream) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{parameterName, inputStream});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BLOB, (Object)inputStream, JavaType.BLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public final void setBlob(String parameterName, InputStream value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public final void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{parameterName, inputStream, length});
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, (Object)value, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp value, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp value, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public void setDateTimeOffset(String parameterName, DateTimeOffset value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, (Object)value, JavaType.DATETIMEOFFSET, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public void setDateTimeOffset(String parameterName, DateTimeOffset value, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, value, JavaType.DATETIMEOFFSET, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public void setDateTimeOffset(String parameterName, DateTimeOffset value, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, value, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public void setDate(String parameterName, Date value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, (Object)value, JavaType.DATE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public void setTime(String parameterName, Time value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, (Object)value, JavaType.TIME, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public void setTime(String parameterName, Time value, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public void setTime(String parameterName, Time value, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public void setDateTime(String parameterName, Timestamp value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIME, (Object)value, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }

    @Override
    public void setDateTime(String parameterName, Timestamp value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIME, (Object)value, JavaType.TIMESTAMP, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }

    @Override
    public void setSmallDateTime(String parameterName, Timestamp value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME, (Object)value, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }

    @Override
    public void setSmallDateTime(String parameterName, Timestamp value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME, (Object)value, JavaType.TIMESTAMP, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }

    @Override
    public void setUniqueIdentifier(String parameterName, String guid) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[]{parameterName, guid});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.GUID, (Object)guid, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }

    @Override
    public void setUniqueIdentifier(String parameterName, String guid, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[]{parameterName, guid, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.GUID, (Object)guid, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }

    @Override
    public void setBytes(String parameterName, byte[] value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BINARY, (Object)value, JavaType.BYTEARRAY, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }

    @Override
    public void setBytes(String parameterName, byte[] value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BINARY, (Object)value, JavaType.BYTEARRAY, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }

    @Override
    public void setByte(String parameterName, byte value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TINYINT, (Object)value, JavaType.BYTE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }

    @Override
    public void setByte(String parameterName, byte value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TINYINT, (Object)value, JavaType.BYTE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }

    @Override
    public void setString(String parameterName, String value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.VARCHAR, (Object)value, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }

    @Override
    public void setString(String parameterName, String value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.VARCHAR, (Object)value, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }

    @Override
    public void setMoney(String parameterName, BigDecimal value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.MONEY, (Object)value, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }

    @Override
    public void setMoney(String parameterName, BigDecimal value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.MONEY, (Object)value, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }

    @Override
    public void setSmallMoney(String parameterName, BigDecimal value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLMONEY, (Object)value, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }

    @Override
    public void setSmallMoney(String parameterName, BigDecimal value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLMONEY, (Object)value, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, (Object)value, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal value, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterName, value, precision, scale});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, value, JavaType.BIGDECIMAL, precision, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal value, int precision, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterName, value, precision, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, value, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public void setDouble(String parameterName, double value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DOUBLE, (Object)value, JavaType.DOUBLE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }

    @Override
    public void setDouble(String parameterName, double value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DOUBLE, (Object)value, JavaType.DOUBLE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }

    @Override
    public void setFloat(String parameterName, float value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[]{parameterName, Float.valueOf(value)});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.REAL, (Object)Float.valueOf(value), JavaType.FLOAT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }

    @Override
    public void setFloat(String parameterName, float value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[]{parameterName, Float.valueOf(value), forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.REAL, (Object)Float.valueOf(value), JavaType.FLOAT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }

    @Override
    public void setInt(String parameterName, int value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.INTEGER, (Object)value, JavaType.INTEGER, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }

    @Override
    public void setInt(String parameterName, int value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.INTEGER, (Object)value, JavaType.INTEGER, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }

    @Override
    public void setLong(String parameterName, long value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIGINT, (Object)value, JavaType.LONG, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }

    @Override
    public void setLong(String parameterName, long value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIGINT, (Object)value, JavaType.LONG, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }

    @Override
    public void setShort(String parameterName, short value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLINT, (Object)value, JavaType.SHORT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }

    @Override
    public void setShort(String parameterName, short value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLINT, (Object)value, JavaType.SHORT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }

    @Override
    public void setBoolean(String parameterName, boolean value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[]{parameterName, value});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIT, (Object)value, JavaType.BOOLEAN, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }

    @Override
    public void setBoolean(String parameterName, boolean value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[]{parameterName, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIT, (Object)value, JavaType.BOOLEAN, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }

    @Override
    public void setNull(String parameterName, int nType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{parameterName, nType});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), null, JavaType.OBJECT, JDBCType.of(nType), null, null, false, this.findColumn(parameterName), null);
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    @Override
    public void setNull(String parameterName, int nType, String sTypeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{parameterName, nType, sTypeName});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), null, JavaType.OBJECT, JDBCType.of(nType), null, null, false, this.findColumn(parameterName), sTypeName);
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    @Override
    public void setURL(String parameterName, URL url) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "setURL", parameterName);
        this.checkClosed();
        this.setURL(this.findColumn(parameterName), url);
        loggerExternal.exiting(this.getClassNameLogging(), "setURL");
    }

    @Override
    public final void setStructured(String parameterName, String tvpName, SQLServerDataTable tvpDataTable) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{parameterName, tvpName, tvpDataTable});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, (Object)tvpDataTable, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    @Override
    public final void setStructured(String parameterName, String tvpName, ResultSet tvpResultSet) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{parameterName, tvpName, tvpResultSet});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, (Object)tvpResultSet, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    @Override
    public final void setStructured(String parameterName, String tvpName, ISQLServerDataRecord tvpDataRecord) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{parameterName, tvpName, tvpDataRecord});
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, (Object)tvpDataRecord, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public final void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSQLXML", new Object[]{parameterName, xmlObject});
        }
        this.checkClosed();
        this.setSQLXMLInternal(this.findColumn(parameterName), xmlObject);
        loggerExternal.exiting(this.getClassNameLogging(), "setSQLXML");
    }

    @Override
    public final SQLXML getSQLXML(int parameterIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", parameterIndex);
        this.checkClosed();
        SQLServerSQLXML value = (SQLServerSQLXML)this.getSQLXMLInternal(parameterIndex);
        loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", value);
        return value;
    }

    @Override
    public final SQLXML getSQLXML(String parameterName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", parameterName);
        this.checkClosed();
        SQLServerSQLXML value = (SQLServerSQLXML)this.getSQLXMLInternal(this.findColumn(parameterName));
        loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", value);
        return value;
    }

    @Override
    public final void setRowId(String parameterName, RowId value) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final RowId getRowId(int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public final RowId getRowId(String parameterName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, typeName});
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, typeName);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, scale});
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, scale});
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, precision, scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType});
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterIndex, sqlType});
        }
        this.registerOutParameter(parameterIndex, (int)sqlType.getVendorTypeNumber());
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterIndex, sqlType, typeName});
        }
        this.registerOutParameter(parameterIndex, (int)sqlType.getVendorTypeNumber(), typeName);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterIndex, sqlType, scale});
        }
        this.registerOutParameter(parameterIndex, (int)sqlType.getVendorTypeNumber(), scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterIndex, sqlType, scale});
        }
        this.registerOutParameter(parameterIndex, (int)sqlType.getVendorTypeNumber(), precision, scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void setObject(String parameterName, Object value, SQLType jdbcType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, jdbcType});
        }
        this.setObject(parameterName, value, (int)jdbcType.getVendorTypeNumber());
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void setObject(String parameterName, Object value, SQLType jdbcType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, jdbcType, scale});
        }
        this.setObject(parameterName, value, (int)jdbcType.getVendorTypeNumber(), scale);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void setObject(String parameterName, Object value, SQLType jdbcType, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterName, value, jdbcType, scale, forceEncrypt});
        }
        this.setObject(parameterName, value, (int)jdbcType.getVendorTypeNumber(), scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, typeName});
        }
        this.registerOutParameter(parameterName, (int)sqlType.getVendorTypeNumber(), typeName);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, scale});
        }
        this.registerOutParameter(parameterName, (int)sqlType.getVendorTypeNumber(), scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType, scale});
        }
        this.registerOutParameter(parameterName, (int)sqlType.getVendorTypeNumber(), precision, scale);
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[]{parameterName, sqlType});
        }
        this.registerOutParameter(parameterName, (int)sqlType.getVendorTypeNumber());
        loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
}

