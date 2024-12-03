/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CekTable;
import com.microsoft.sqlserver.jdbc.Column;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.ISQLServerBulkData;
import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolProxy;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSTimeoutTask;
import com.microsoft.sqlserver.jdbc.TDSType;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSet;
import microsoft.sql.DateTimeOffset;

public class SQLServerBulkCopy
implements AutoCloseable,
Serializable {
    private static final long serialVersionUID = 1989903904654306244L;
    private static final String MAX = "(max)";
    private static final String loggerClassName = "com.microsoft.sqlserver.jdbc.SQLServerBulkCopy";
    private static final Logger loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy");
    private SQLServerConnection connection;
    private SQLServerBulkCopyOptions copyOptions;
    private List<ColumnMapping> columnMappings;
    private boolean ownsConnection;
    private String destinationTableName;
    private ISQLServerBulkData serverBulkData;
    private transient ResultSet sourceResultSet;
    private transient ResultSetMetaData sourceResultSetMetaData;
    private CekTable destCekTable = null;
    private SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting = SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING;
    private transient ResultSet destinationTableMetadata;
    private transient Map<Integer, BulkColumnMetaData> destColumnMetadata;
    private transient Map<Integer, BulkColumnMetaData> srcColumnMetadata;
    private int destColumnCount;
    private int srcColumnCount;
    private transient ScheduledFuture<?> timeout;
    private static final int SOURCE_BULK_RECORD_TEMPORAL_MAX_PRECISION = 50;

    public SQLServerBulkCopy(Connection connection) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "SQLServerBulkCopy", connection);
        if (null == connection || !(connection instanceof ISQLServerConnection)) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestConnection"), null, false);
        }
        if (connection instanceof SQLServerConnection) {
            this.connection = (SQLServerConnection)connection;
        } else if (connection instanceof SQLServerConnectionPoolProxy) {
            this.connection = ((SQLServerConnectionPoolProxy)connection).getWrappedConnection();
        } else {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestConnection"), null, false);
        }
        this.ownsConnection = false;
        this.copyOptions = new SQLServerBulkCopyOptions();
        this.initializeDefaults();
        loggerExternal.exiting(loggerClassName, "SQLServerBulkCopy");
    }

    public SQLServerBulkCopy(String connectionUrl) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "SQLServerBulkCopy", "connectionUrl not traced.");
        if (connectionUrl == null || "".equals(connectionUrl.trim())) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        this.ownsConnection = true;
        SQLServerDriver driver = new SQLServerDriver();
        this.connection = (SQLServerConnection)driver.connect(connectionUrl, null);
        if (null == this.connection) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_invalidConnection"), null, 0, false);
        }
        this.copyOptions = new SQLServerBulkCopyOptions();
        this.initializeDefaults();
        loggerExternal.exiting(loggerClassName, "SQLServerBulkCopy");
    }

    public void addColumnMapping(int sourceColumn, int destinationColumn) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(loggerClassName, "addColumnMapping", new Object[]{sourceColumn, destinationColumn});
        }
        if (0 >= sourceColumn) {
            this.throwInvalidArgument("sourceColumn");
        } else if (0 >= destinationColumn) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn));
        loggerExternal.exiting(loggerClassName, "addColumnMapping");
    }

    public void addColumnMapping(int sourceColumn, String destinationColumn) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(loggerClassName, "addColumnMapping", new Object[]{sourceColumn, destinationColumn});
        }
        if (0 >= sourceColumn) {
            this.throwInvalidArgument("sourceColumn");
        } else if (null == destinationColumn || destinationColumn.isEmpty()) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn));
        loggerExternal.exiting(loggerClassName, "addColumnMapping");
    }

    public void addColumnMapping(String sourceColumn, int destinationColumn) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(loggerClassName, "addColumnMapping", new Object[]{sourceColumn, destinationColumn});
        }
        if (0 >= destinationColumn) {
            this.throwInvalidArgument("destinationColumn");
        } else if (null == sourceColumn || sourceColumn.isEmpty()) {
            this.throwInvalidArgument("sourceColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn));
        loggerExternal.exiting(loggerClassName, "addColumnMapping");
    }

    public void addColumnMapping(String sourceColumn, String destinationColumn) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(loggerClassName, "addColumnMapping", new Object[]{sourceColumn, destinationColumn});
        }
        if (null == sourceColumn || sourceColumn.isEmpty()) {
            this.throwInvalidArgument("sourceColumn");
        } else if (null == destinationColumn || destinationColumn.isEmpty()) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn));
        loggerExternal.exiting(loggerClassName, "addColumnMapping");
    }

    public void clearColumnMappings() {
        loggerExternal.entering(loggerClassName, "clearColumnMappings");
        this.columnMappings.clear();
        loggerExternal.exiting(loggerClassName, "clearColumnMappings");
    }

    @Override
    public void close() {
        loggerExternal.entering(loggerClassName, "close");
        if (this.ownsConnection) {
            try {
                this.connection.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        loggerExternal.exiting(loggerClassName, "close");
    }

    public String getDestinationTableName() {
        return this.destinationTableName;
    }

    public void setDestinationTableName(String tableName) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "setDestinationTableName", tableName);
        if (null == tableName || 0 == tableName.trim().length()) {
            this.throwInvalidArgument("tableName");
        }
        this.destinationTableName = tableName.trim();
        loggerExternal.exiting(loggerClassName, "setDestinationTableName");
    }

    public SQLServerBulkCopyOptions getBulkCopyOptions() {
        return this.copyOptions;
    }

    public void setBulkCopyOptions(SQLServerBulkCopyOptions copyOptions) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "updateBulkCopyOptions", copyOptions);
        if (null != copyOptions) {
            if (!this.ownsConnection && copyOptions.isUseInternalTransaction()) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidTransactionOption"), null, false);
            }
            this.copyOptions = copyOptions;
        }
        loggerExternal.exiting(loggerClassName, "updateBulkCopyOptions");
    }

    public void writeToServer(ResultSet sourceData) throws SQLServerException {
        this.writeResultSet(sourceData, false);
    }

    public void writeToServer(RowSet sourceData) throws SQLServerException {
        this.writeResultSet(sourceData, true);
    }

    private void writeResultSet(ResultSet sourceData, boolean isRowSet) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "writeToServer");
        if (null == sourceData) {
            this.throwInvalidArgument("sourceData");
        }
        try {
            if (isRowSet) {
                if (!sourceData.isBeforeFirst()) {
                    sourceData.beforeFirst();
                }
            } else if (sourceData.isClosed()) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_resultsetClosed"), null, false);
            }
        }
        catch (SQLException e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.sourceResultSet = sourceData;
        this.serverBulkData = null;
        try {
            this.sourceResultSetMetaData = this.sourceResultSet.getMetaData();
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
        }
        this.writeToServer();
        loggerExternal.exiting(loggerClassName, "writeToServer");
    }

    public void writeToServer(ISQLServerBulkData sourceData) throws SQLServerException {
        loggerExternal.entering(loggerClassName, "writeToServer");
        if (null == sourceData) {
            this.throwInvalidArgument("sourceData");
        }
        this.serverBulkData = sourceData;
        this.sourceResultSet = null;
        this.writeToServer();
        loggerExternal.exiting(loggerClassName, "writeToServer");
    }

    private void initializeDefaults() {
        this.columnMappings = new ArrayList<ColumnMapping>();
        this.destinationTableName = null;
        this.serverBulkData = null;
        this.sourceResultSet = null;
        this.sourceResultSetMetaData = null;
        this.srcColumnCount = 0;
        this.srcColumnMetadata = null;
        this.destColumnMetadata = null;
        this.destColumnCount = 0;
    }

    private void sendBulkLoadBCP() throws SQLServerException {
        final class InsertBulk
        extends TDSCommand {
            private static final long serialVersionUID = 6714118105257791547L;

            InsertBulk() {
                super("InsertBulk", 0, 0);
            }

            @Override
            final boolean doExecute() throws SQLServerException {
                int timeoutSeconds = SQLServerBulkCopy.this.copyOptions.getBulkCopyTimeout();
                if (timeoutSeconds > 0) {
                    SQLServerBulkCopy.this.connection.checkClosed();
                    SQLServerBulkCopy.this.timeout = SQLServerBulkCopy.this.connection.getSharedTimer().schedule(new TDSTimeoutTask(this, SQLServerBulkCopy.this.connection), timeoutSeconds);
                }
                try {
                    while (SQLServerBulkCopy.this.doInsertBulk(this)) {
                    }
                }
                catch (SQLServerException topLevelException) {
                    SQLException sqlEx;
                    Throwable rootCause = topLevelException;
                    while (null != rootCause.getCause()) {
                        rootCause = rootCause.getCause();
                    }
                    if (rootCause instanceof SQLException && SQLServerBulkCopy.this.timeout != null && SQLServerBulkCopy.this.timeout.isDone() && (sqlEx = (SQLException)rootCause).getSQLState() != null && sqlEx.getSQLState().equals(SQLState.STATEMENT_CANCELED.getSQLStateCode())) {
                        if (SQLServerBulkCopy.this.copyOptions.isUseInternalTransaction()) {
                            SQLServerBulkCopy.this.connection.rollback();
                        }
                        throw new SQLServerException(SQLServerException.getErrString("R_queryTimedOut"), SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, (Throwable)sqlEx);
                    }
                    throw topLevelException;
                }
                if (SQLServerBulkCopy.this.timeout != null) {
                    SQLServerBulkCopy.this.timeout.cancel(true);
                    SQLServerBulkCopy.this.timeout = null;
                }
                return true;
            }
        }
        this.connection.executeCommand(new InsertBulk());
    }

    private void writeColumnMetaDataColumnData(TDSWriter tdsWriter, int idx) throws SQLServerException {
        byte[] userType = new byte[]{0, 0, 0, 0};
        tdsWriter.writeBytes(userType);
        int destColumnIndex = this.columnMappings.get((int)idx).destinationColumnOrdinal;
        int srcColumnIndex = this.columnMappings.get((int)idx).sourceColumnOrdinal;
        byte[] flags = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).flags;
        if (null == this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColumnIndex)).cryptoMeta && null == this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).cryptoMeta && this.copyOptions.isAllowEncryptedValueModifications() && 1 == (flags[1] >> 3 & 1)) {
            flags[1] = (byte)(flags[1] - 8);
        }
        tdsWriter.writeBytes(flags);
        int bulkJdbcType = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColumnIndex)).jdbcType;
        int bulkPrecision = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColumnIndex)).precision;
        int bulkScale = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColumnIndex)).scale;
        boolean srcNullable = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColumnIndex)).isNullable;
        SSType destSSType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).ssType;
        int destPrecision = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).precision;
        bulkPrecision = this.validateSourcePrecision(bulkPrecision, bulkJdbcType, destPrecision);
        SQLCollation collation = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).collation;
        if (null == collation) {
            collation = this.connection.getDatabaseCollation();
        }
        boolean isStreaming = -15 == bulkJdbcType || -9 == bulkJdbcType || -16 == bulkJdbcType ? 4000 < bulkPrecision || 4000 < destPrecision : 8000 < bulkPrecision || 8000 < destPrecision;
        CryptoMetadata destCryptoMeta = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).cryptoMeta;
        if (this.sourceResultSet instanceof SQLServerResultSet && this.connection.isColumnEncryptionSettingEnabled() && null != destCryptoMeta) {
            bulkJdbcType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).jdbcType;
            bulkPrecision = destPrecision;
            bulkScale = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).scale;
        }
        if (null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).encryptionType && this.copyOptions.isAllowEncryptedValueModifications() || null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColumnIndex)).cryptoMeta) {
            tdsWriter.writeByte((byte)-91);
            if (isStreaming) {
                tdsWriter.writeShort((short)-1);
            } else {
                tdsWriter.writeShort((short)bulkPrecision);
            }
        } else if (!(1 != bulkJdbcType && 12 != bulkJdbcType && -1 != bulkJdbcType || SSType.BINARY != destSSType && SSType.VARBINARY != destSSType && SSType.VARBINARYMAX != destSSType && SSType.IMAGE != destSSType)) {
            if (isStreaming) {
                tdsWriter.writeByte((byte)-91);
            } else {
                tdsWriter.writeByte((byte)(SSType.BINARY == destSSType ? 173 : 165));
            }
            tdsWriter.writeShort((short)bulkPrecision);
        } else {
            this.writeTypeInfo(tdsWriter, bulkJdbcType, bulkScale, bulkPrecision, destSSType, collation, isStreaming, srcNullable, false);
        }
        if (null != destCryptoMeta) {
            int baseDestJDBCType = destCryptoMeta.baseTypeInfo.getSSType().getJDBCType().asJavaSqlType();
            int baseDestPrecision = destCryptoMeta.baseTypeInfo.getPrecision();
            isStreaming = -15 == baseDestJDBCType || -9 == baseDestJDBCType || -16 == baseDestJDBCType ? 4000 < baseDestPrecision : 8000 < baseDestPrecision;
            tdsWriter.writeShort(destCryptoMeta.getOrdinal());
            tdsWriter.writeBytes(userType);
            this.writeTypeInfo(tdsWriter, baseDestJDBCType, destCryptoMeta.baseTypeInfo.getScale(), baseDestPrecision, destCryptoMeta.baseTypeInfo.getSSType(), collation, isStreaming, srcNullable, true);
            tdsWriter.writeByte(destCryptoMeta.cipherAlgorithmId);
            tdsWriter.writeByte(destCryptoMeta.encryptionType.getValue());
            tdsWriter.writeByte(destCryptoMeta.normalizationRuleVersion);
        }
        int destColNameLen = this.columnMappings.get((int)idx).destinationColumnName.length();
        String destColName = this.columnMappings.get((int)idx).destinationColumnName;
        byte[] colName = new byte[2 * destColNameLen];
        for (int i = 0; i < destColNameLen; ++i) {
            char c = destColName.charAt(i);
            colName[2 * i] = (byte)(c & 0xFF);
            colName[2 * i + 1] = (byte)(c >> 8 & 0xFF);
        }
        tdsWriter.writeByte((byte)destColNameLen);
        tdsWriter.writeBytes(colName);
    }

    private void writeTypeInfo(TDSWriter tdsWriter, int srcJdbcType, int srcScale, int srcPrecision, SSType destSSType, SQLCollation collation, boolean isStreaming, boolean srcNullable, boolean isBaseType) throws SQLServerException {
        block0 : switch (srcJdbcType) {
            case 4: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.INT4.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.INTN.byteValue());
                tdsWriter.writeByte((byte)4);
                break;
            }
            case -5: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.INT8.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.INTN.byteValue());
                tdsWriter.writeByte((byte)8);
                break;
            }
            case -7: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.BIT1.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.BITN.byteValue());
                tdsWriter.writeByte((byte)1);
                break;
            }
            case 5: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.INT2.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.INTN.byteValue());
                tdsWriter.writeByte((byte)2);
                break;
            }
            case -6: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.INT1.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.INTN.byteValue());
                tdsWriter.writeByte((byte)1);
                break;
            }
            case 6: 
            case 8: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.FLOAT8.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                tdsWriter.writeByte((byte)8);
                break;
            }
            case 7: {
                if (!srcNullable) {
                    tdsWriter.writeByte(TDSType.FLOAT4.byteValue());
                    break;
                }
                tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                tdsWriter.writeByte((byte)4);
                break;
            }
            case -148: 
            case -146: {
                tdsWriter.writeByte(TDSType.MONEYN.byteValue());
                if (SSType.MONEY == destSSType) {
                    tdsWriter.writeByte((byte)8);
                    break;
                }
                tdsWriter.writeByte((byte)4);
                break;
            }
            case 2: 
            case 3: {
                if (destSSType == SSType.MONEY) {
                    tdsWriter.writeByte(TDSType.MONEYN.byteValue());
                    tdsWriter.writeByte((byte)8);
                    break;
                }
                if (destSSType == SSType.SMALLMONEY) {
                    tdsWriter.writeByte(TDSType.MONEYN.byteValue());
                    tdsWriter.writeByte((byte)4);
                    break;
                }
                byte byteType = 3 == srcJdbcType ? TDSType.DECIMALN.byteValue() : TDSType.NUMERICN.byteValue();
                tdsWriter.writeByte(byteType);
                tdsWriter.writeByte((byte)17);
                tdsWriter.writeByte((byte)srcPrecision);
                tdsWriter.writeByte((byte)srcScale);
                break;
            }
            case -145: 
            case 1: {
                if (isBaseType && SSType.GUID == destSSType) {
                    tdsWriter.writeByte(TDSType.GUID.byteValue());
                    tdsWriter.writeByte((byte)16);
                    break;
                }
                if (this.unicodeConversionRequired(srcJdbcType, destSSType)) {
                    tdsWriter.writeByte(TDSType.NCHAR.byteValue());
                    tdsWriter.writeShort(isBaseType ? (short)srcPrecision : (short)(2 * srcPrecision));
                } else {
                    tdsWriter.writeByte(TDSType.BIGCHAR.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                }
                collation.writeCollation(tdsWriter);
                break;
            }
            case -15: {
                tdsWriter.writeByte(TDSType.NCHAR.byteValue());
                tdsWriter.writeShort(isBaseType ? (short)srcPrecision : (short)(2 * srcPrecision));
                collation.writeCollation(tdsWriter);
                break;
            }
            case -1: 
            case 12: {
                if (this.unicodeConversionRequired(srcJdbcType, destSSType)) {
                    tdsWriter.writeByte(TDSType.NVARCHAR.byteValue());
                    if (isStreaming) {
                        tdsWriter.writeShort((short)-1);
                    } else {
                        tdsWriter.writeShort(isBaseType ? (short)srcPrecision : (short)(2 * srcPrecision));
                    }
                } else {
                    tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    if (isStreaming) {
                        tdsWriter.writeShort((short)-1);
                    } else {
                        tdsWriter.writeShort((short)srcPrecision);
                    }
                }
                collation.writeCollation(tdsWriter);
                break;
            }
            case -16: 
            case -9: {
                tdsWriter.writeByte(TDSType.NVARCHAR.byteValue());
                if (isStreaming) {
                    tdsWriter.writeShort((short)-1);
                } else {
                    tdsWriter.writeShort(isBaseType ? (short)srcPrecision : (short)(2 * srcPrecision));
                }
                collation.writeCollation(tdsWriter);
                break;
            }
            case -2: {
                tdsWriter.writeByte(TDSType.BIGBINARY.byteValue());
                tdsWriter.writeShort((short)srcPrecision);
                break;
            }
            case -4: 
            case -3: {
                tdsWriter.writeByte(TDSType.BIGVARBINARY.byteValue());
                if (isStreaming) {
                    tdsWriter.writeShort((short)-1);
                    break;
                }
                tdsWriter.writeShort((short)srcPrecision);
                break;
            }
            case -151: 
            case -150: 
            case 93: {
                if (!isBaseType && null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                    collation.writeCollation(tdsWriter);
                    break;
                }
                switch (destSSType) {
                    case SMALLDATETIME: {
                        if (!srcNullable) {
                            tdsWriter.writeByte(TDSType.DATETIME4.byteValue());
                            break block0;
                        }
                        tdsWriter.writeByte(TDSType.DATETIMEN.byteValue());
                        tdsWriter.writeByte((byte)4);
                        break block0;
                    }
                    case DATETIME: {
                        if (!srcNullable) {
                            tdsWriter.writeByte(TDSType.DATETIME8.byteValue());
                            break block0;
                        }
                        tdsWriter.writeByte(TDSType.DATETIMEN.byteValue());
                        tdsWriter.writeByte((byte)8);
                        break block0;
                    }
                }
                tdsWriter.writeByte(TDSType.DATETIME2N.byteValue());
                tdsWriter.writeByte((byte)srcScale);
                break;
            }
            case 91: {
                if (!isBaseType && null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                    collation.writeCollation(tdsWriter);
                    break;
                }
                tdsWriter.writeByte(TDSType.DATEN.byteValue());
                break;
            }
            case 92: {
                if (!isBaseType && null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                    collation.writeCollation(tdsWriter);
                    break;
                }
                tdsWriter.writeByte(TDSType.TIMEN.byteValue());
                tdsWriter.writeByte((byte)srcScale);
                break;
            }
            case 2013: 
            case 2014: {
                tdsWriter.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
                tdsWriter.writeByte((byte)srcScale);
                break;
            }
            case -155: {
                if (!isBaseType && null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                    collation.writeCollation(tdsWriter);
                    break;
                }
                tdsWriter.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
                tdsWriter.writeByte((byte)srcScale);
                break;
            }
            case -156: {
                tdsWriter.writeByte(TDSType.SQL_VARIANT.byteValue());
                tdsWriter.writeInt(8009);
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                String unsupportedDataType = JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH);
                throw new SQLServerException(form.format(new Object[]{unsupportedDataType}), null, 0, null);
            }
        }
    }

    private void writeCekTable(TDSWriter tdsWriter) throws SQLServerException {
        if (this.connection.getServerSupportsColumnEncryption()) {
            if (null != this.destCekTable && 0 < this.destCekTable.getSize()) {
                tdsWriter.writeShort((short)this.destCekTable.getSize());
                for (int cekIndx = 0; cekIndx < this.destCekTable.getSize(); ++cekIndx) {
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry((int)cekIndx).getColumnEncryptionKeyValues().get((int)0).databaseId);
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry((int)cekIndx).getColumnEncryptionKeyValues().get((int)0).cekId);
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry((int)cekIndx).getColumnEncryptionKeyValues().get((int)0).cekVersion);
                    tdsWriter.writeBytes(this.destCekTable.getCekTableEntry((int)cekIndx).getColumnEncryptionKeyValues().get((int)0).cekMdVersion);
                    tdsWriter.writeByte((byte)0);
                }
            } else {
                tdsWriter.writeShort((short)0);
            }
        }
    }

    private void writeColumnMetaData(TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeByte((byte)-127);
        byte[] count = new byte[]{(byte)(this.columnMappings.size() & 0xFF), (byte)(this.columnMappings.size() >> 8 & 0xFF)};
        tdsWriter.writeBytes(count);
        this.writeCekTable(tdsWriter);
        for (int i = 0; i < this.columnMappings.size(); ++i) {
            this.writeColumnMetaDataColumnData(tdsWriter, i);
        }
    }

    private void validateDataTypeConversions(int srcColOrdinal, int destColOrdinal) throws SQLServerException {
        SSType destSSType;
        CryptoMetadata sourceCryptoMeta = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).cryptoMeta;
        CryptoMetadata destCryptoMeta = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).cryptoMeta;
        JDBCType srcJdbcType = null != sourceCryptoMeta ? sourceCryptoMeta.baseTypeInfo.getSSType().getJDBCType() : JDBCType.of(this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).jdbcType);
        SSType sSType = destSSType = null != destCryptoMeta ? destCryptoMeta.baseTypeInfo.getSSType() : this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).ssType;
        if (!srcJdbcType.convertsTo(destSSType)) {
            DataTypes.throwConversionError(srcJdbcType.toString(), destSSType.toString());
        }
    }

    private String getDestTypeFromSrcType(int srcColIndx, int destColIndx, TDSWriter tdsWriter) throws SQLServerException {
        boolean isStreaming;
        int srcPrecision;
        SSType destSSType = null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).cryptoMeta ? this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).cryptoMeta.baseTypeInfo.getSSType() : this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).ssType;
        int bulkJdbcType = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColIndx)).jdbcType;
        int bulkPrecision = srcPrecision = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColIndx)).precision;
        int destPrecision = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).precision;
        int bulkScale = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColIndx)).scale;
        CryptoMetadata destCryptoMeta = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).cryptoMeta;
        if (null != destCryptoMeta || null == destCryptoMeta && this.copyOptions.isAllowEncryptedValueModifications()) {
            tdsWriter.setCryptoMetaData(this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).cryptoMeta);
            if (this.sourceResultSet instanceof SQLServerResultSet && this.connection.isColumnEncryptionSettingEnabled() && null != destCryptoMeta) {
                bulkJdbcType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).jdbcType;
                bulkPrecision = destPrecision;
                bulkScale = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).scale;
            }
            if (8000 < destPrecision) {
                return SSType.VARBINARY.toString() + MAX;
            }
            return SSType.VARBINARY.toString() + "(" + this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).precision + ")";
        }
        if (null != this.sourceResultSet && null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColIndx)).encryptionType && this.copyOptions.isAllowEncryptedValueModifications()) {
            return "varbinary(" + bulkPrecision + ")";
        }
        bulkPrecision = this.validateSourcePrecision(srcPrecision, bulkJdbcType, destPrecision);
        if (-15 == bulkJdbcType || -9 == bulkJdbcType || -16 == bulkJdbcType) {
            isStreaming = 4000 < srcPrecision || 4000 < destPrecision;
        } else {
            boolean bl = isStreaming = 8000 < srcPrecision || 8000 < destPrecision;
        }
        if (Util.isCharType(bulkJdbcType) && Util.isBinaryType(destSSType).booleanValue()) {
            if (isStreaming) {
                return SSType.VARBINARY.toString() + MAX;
            }
            return destSSType.toString() + "(" + (Serializable)(8000 < destPrecision ? "max" : Integer.valueOf(destPrecision)) + ")";
        }
        switch (bulkJdbcType) {
            case 4: {
                return SSType.INTEGER.toString();
            }
            case 5: {
                return SSType.SMALLINT.toString();
            }
            case -5: {
                return SSType.BIGINT.toString();
            }
            case -7: {
                return SSType.BIT.toString();
            }
            case -6: {
                return SSType.TINYINT.toString();
            }
            case 6: 
            case 8: {
                return SSType.FLOAT.toString();
            }
            case 7: {
                return SSType.REAL.toString();
            }
            case -148: {
                return SSType.MONEY.toString();
            }
            case -146: {
                return SSType.SMALLMONEY.toString();
            }
            case 3: {
                if (destSSType == SSType.MONEY) {
                    return SSType.MONEY.toString();
                }
                if (destSSType == SSType.SMALLMONEY) {
                    return SSType.SMALLMONEY.toString();
                }
                return SSType.DECIMAL.toString() + "(" + bulkPrecision + ", " + bulkScale + ")";
            }
            case 2: {
                if (destSSType == SSType.MONEY) {
                    return SSType.MONEY.toString();
                }
                if (destSSType == SSType.SMALLMONEY) {
                    return SSType.SMALLMONEY.toString();
                }
                return SSType.NUMERIC.toString() + "(" + bulkPrecision + ", " + bulkScale + ")";
            }
            case -145: {
                return SSType.CHAR.toString() + "(" + bulkPrecision + ")";
            }
            case 1: {
                if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                    return SSType.NCHAR.toString() + "(" + bulkPrecision + ")";
                }
                return SSType.CHAR.toString() + "(" + bulkPrecision + ")";
            }
            case -15: {
                return SSType.NCHAR.toString() + "(" + bulkPrecision + ")";
            }
            case -1: 
            case 12: {
                if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                    if (isStreaming) {
                        return SSType.NVARCHAR.toString() + MAX;
                    }
                    return SSType.NVARCHAR.toString() + "(" + bulkPrecision + ")";
                }
                if (isStreaming) {
                    return SSType.VARCHAR.toString() + MAX;
                }
                return SSType.VARCHAR.toString() + "(" + bulkPrecision + ")";
            }
            case -16: 
            case -9: {
                if (isStreaming) {
                    return SSType.NVARCHAR.toString() + MAX;
                }
                return SSType.NVARCHAR.toString() + "(" + bulkPrecision + ")";
            }
            case -2: {
                return SSType.BINARY.toString() + "(" + bulkPrecision + ")";
            }
            case -4: 
            case -3: {
                if (isStreaming) {
                    return SSType.VARBINARY.toString() + MAX;
                }
                return SSType.VARBINARY.toString() + "(" + bulkPrecision + ")";
            }
            case -151: 
            case -150: 
            case 93: {
                switch (destSSType) {
                    case SMALLDATETIME: {
                        if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                            return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? 50 : bulkPrecision) + ")";
                        }
                        return SSType.SMALLDATETIME.toString();
                    }
                    case DATETIME: {
                        if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                            return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? 50 : bulkPrecision) + ")";
                        }
                        return SSType.DATETIME.toString();
                    }
                }
                if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? destPrecision : bulkPrecision) + ")";
                }
                return SSType.DATETIME2.toString() + "(" + bulkScale + ")";
            }
            case 91: {
                if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? destPrecision : bulkPrecision) + ")";
                }
                return SSType.DATE.toString();
            }
            case 92: {
                if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? destPrecision : bulkPrecision) + ")";
                }
                return SSType.TIME.toString() + "(" + bulkScale + ")";
            }
            case 2013: 
            case 2014: {
                return SSType.DATETIMEOFFSET.toString() + "(" + bulkScale + ")";
            }
            case -155: {
                if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
                    return SSType.VARCHAR.toString() + "(" + (0 == bulkPrecision ? destPrecision : bulkPrecision) + ")";
                }
                return SSType.DATETIMEOFFSET.toString() + "(" + bulkScale + ")";
            }
            case -156: {
                return SSType.SQL_VARIANT.toString();
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
        Object[] msgArgs = new Object[]{JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH)};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return null;
    }

    private String createInsertBulkCommand(TDSWriter tdsWriter) throws SQLServerException {
        Iterator it;
        StringBuilder bulkCmd = new StringBuilder();
        ArrayList<Object> bulkOptions = new ArrayList<Object>();
        String endColumn = " , ";
        bulkCmd.append("INSERT BULK ").append(this.destinationTableName).append(" (");
        for (int i = 0; i < this.columnMappings.size(); ++i) {
            if (i == this.columnMappings.size() - 1) {
                endColumn = " ) ";
            }
            ColumnMapping colMapping = this.columnMappings.get(i);
            String columnCollation = this.destColumnMetadata.get((Object)Integer.valueOf((int)this.columnMappings.get((int)i).destinationColumnOrdinal)).collationName;
            Object addCollate = "";
            String destType = this.getDestTypeFromSrcType(colMapping.sourceColumnOrdinal, colMapping.destinationColumnOrdinal, tdsWriter).toUpperCase(Locale.ENGLISH);
            if (null != columnCollation && columnCollation.trim().length() > 0 && null != destType && (destType.toLowerCase(Locale.ENGLISH).trim().startsWith("char") || destType.toLowerCase(Locale.ENGLISH).trim().startsWith("varchar"))) {
                addCollate = " COLLATE " + columnCollation;
            }
            if (colMapping.destinationColumnName.contains("]")) {
                String escapedColumnName = colMapping.destinationColumnName.replaceAll("]", "]]");
                bulkCmd.append("[").append(escapedColumnName).append("] ").append(destType).append((String)addCollate).append(endColumn);
                continue;
            }
            bulkCmd.append("[").append(colMapping.destinationColumnName).append("] ").append(destType).append((String)addCollate).append(endColumn);
        }
        if (this.copyOptions.isCheckConstraints()) {
            bulkOptions.add("CHECK_CONSTRAINTS");
        }
        if (this.copyOptions.isFireTriggers()) {
            bulkOptions.add("FIRE_TRIGGERS");
        }
        if (this.copyOptions.isKeepNulls()) {
            bulkOptions.add("KEEP_NULLS");
        }
        if (this.copyOptions.getBatchSize() > 0) {
            bulkOptions.add("ROWS_PER_BATCH = " + this.copyOptions.getBatchSize());
        }
        if (this.copyOptions.isTableLock()) {
            bulkOptions.add("TABLOCK");
        }
        if (this.copyOptions.isAllowEncryptedValueModifications()) {
            bulkOptions.add("ALLOW_ENCRYPTED_VALUE_MODIFICATIONS");
        }
        if ((it = bulkOptions.iterator()).hasNext()) {
            bulkCmd.append(" with (");
            while (it.hasNext()) {
                bulkCmd.append((String)it.next());
                if (!it.hasNext()) continue;
                bulkCmd.append(", ");
            }
            bulkCmd.append(")");
        }
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer(this.toString() + " TDSCommand: " + bulkCmd);
        }
        return bulkCmd.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doInsertBulk(TDSCommand command) throws SQLServerException {
        if (this.copyOptions.isUseInternalTransaction()) {
            this.connection.setAutoCommit(false);
        }
        boolean insertRowByRow = false;
        if (null != this.sourceResultSet && this.sourceResultSet instanceof SQLServerResultSet) {
            SQLServerStatement srcStmt = (SQLServerStatement)((SQLServerResultSet)this.sourceResultSet).getStatement();
            int resultSetServerCursorId = ((SQLServerResultSet)this.sourceResultSet).getServerCursorId();
            if (this.connection.equals(srcStmt.getConnection()) && 0 != resultSetServerCursorId) {
                insertRowByRow = true;
            }
            if (((SQLServerResultSet)this.sourceResultSet).isForwardOnly()) {
                try {
                    this.sourceResultSet.setFetchSize(1);
                }
                catch (SQLException e) {
                    SQLServerException.makeFromDriverError(this.connection, this.sourceResultSet, e.getMessage(), e.getSQLState(), true);
                }
            }
        }
        TDSWriter tdsWriter = null;
        boolean moreDataAvailable = false;
        try {
            if (!insertRowByRow) {
                tdsWriter = this.sendBulkCopyCommand(command);
            }
            try {
                moreDataAvailable = this.writeBatchData(tdsWriter, command, insertRowByRow);
            }
            finally {
                tdsWriter = command.getTDSWriter();
            }
        }
        finally {
            if (null == tdsWriter) {
                tdsWriter = command.getTDSWriter();
            }
            tdsWriter.setCryptoMetaData(null);
        }
        if (!insertRowByRow) {
            this.writePacketDataDone(tdsWriter);
            TDSParser.parse(command.startResponse(), command.getLogContext());
        }
        if (this.copyOptions.isUseInternalTransaction()) {
            this.connection.commit();
        }
        return moreDataAvailable;
    }

    private TDSWriter sendBulkCopyCommand(TDSCommand command) throws SQLServerException {
        TDSWriter tdsWriter = command.startRequest((byte)1);
        String bulkCmd = this.createInsertBulkCommand(tdsWriter);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeString(bulkCmd);
        TDSParser.parse(command.startResponse(), command.getLogContext());
        tdsWriter = command.startRequest((byte)7);
        this.writeColumnMetaData(tdsWriter);
        return tdsWriter;
    }

    private void writePacketDataDone(TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeByte((byte)-3);
        tdsWriter.writeLong(0L);
        tdsWriter.writeInt(0);
    }

    private void throwInvalidArgument(String argument) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
        Object[] msgArgs = new Object[]{argument};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
    }

    private void writeToServer() throws SQLServerException {
        if (this.connection.isClosed()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08003", false);
        }
        long start = System.currentTimeMillis();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer(this.toString() + " Start writeToServer: " + start);
        }
        this.getDestinationMetadata();
        this.getSourceMetadata();
        this.validateColumnMappings();
        this.sendBulkLoadBCP();
        long end = System.currentTimeMillis();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer(this.toString() + " End writeToServer: " + end);
            int seconds = (int)((end - start) / 1000L);
            loggerExternal.finer(this.toString() + "Time elapsed: " + seconds + " seconds");
        }
    }

    private void validateStringBinaryLengths(Object colValue, int srcCol, int destCol) throws SQLServerException {
        int destPrecision = this.destColumnMetadata.get((Object)Integer.valueOf((int)destCol)).precision;
        int srcJdbcType = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcCol)).jdbcType;
        SSType destSSType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destCol)).ssType;
        if (Util.isCharType(srcJdbcType) && Util.isCharType(destSSType).booleanValue() || Util.isBinaryType(srcJdbcType).booleanValue() && Util.isBinaryType(destSSType).booleanValue()) {
            int sourcePrecision;
            if (colValue instanceof String) {
                sourcePrecision = Util.isBinaryType(destSSType).booleanValue() ? ((String)colValue).getBytes().length / 2 : ((String)colValue).length();
            } else if (colValue instanceof byte[]) {
                sourcePrecision = ((byte[])colValue).length;
            } else {
                return;
            }
            if (sourcePrecision > destPrecision) {
                String srcType = JDBCType.of(srcJdbcType) + "(" + sourcePrecision + ")";
                String destType = destSSType.toString() + "(" + destPrecision + ")";
                String destName = this.destColumnMetadata.get((Object)Integer.valueOf((int)destCol)).columnName;
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                Object[] msgArgs = new Object[]{srcType, destType, destName};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
        }
    }

    private void getDestinationMetadata() throws SQLServerException {
        if (null == this.destinationTableName) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestinationTable"), null, false);
        }
        String escapedDestinationTableName = Util.escapeSingleQuotes(this.destinationTableName);
        SQLServerResultSet rs = null;
        SQLServerStatement stmt = null;
        String metaDataQuery = null;
        try {
            if (null != this.destinationTableMetadata) {
                rs = (SQLServerResultSet)this.destinationTableMetadata;
            } else {
                stmt = (SQLServerStatement)this.connection.createStatement(1003, 1007, this.connection.getHoldability(), this.stmtColumnEncriptionSetting);
                rs = stmt.executeQueryInternal("sp_executesql N'SET FMTONLY ON SELECT * FROM " + escapedDestinationTableName + " '");
            }
            this.destColumnCount = rs.getMetaData().getColumnCount();
            this.destColumnMetadata = new HashMap<Integer, BulkColumnMetaData>();
            this.destCekTable = rs.getCekTable();
            metaDataQuery = !this.connection.getServerSupportsColumnEncryption() ? "select collation_name from sys.columns where object_id=OBJECT_ID('" + escapedDestinationTableName + "') order by column_id ASC" : "select collation_name, encryption_type from sys.columns where object_id=OBJECT_ID('" + escapedDestinationTableName + "') order by column_id ASC";
            try (SQLServerStatement statementMoreMetadata = (SQLServerStatement)this.connection.createStatement();
                 SQLServerResultSet rsMoreMetaData = statementMoreMetadata.executeQueryInternal(metaDataQuery);){
                for (int i = 1; i <= this.destColumnCount; ++i) {
                    if (rsMoreMetaData.next()) {
                        String bulkCopyEncryptionType = null;
                        if (this.connection.getServerSupportsColumnEncryption()) {
                            bulkCopyEncryptionType = rsMoreMetaData.getString("encryption_type");
                        }
                        this.destColumnMetadata.put(i, new BulkColumnMetaData(rs.getColumn(i), rsMoreMetaData.getString("collation_name"), bulkCopyEncryptionType));
                        continue;
                    }
                    this.destColumnMetadata.put(i, new BulkColumnMetaData(rs.getColumn(i)));
                }
            }
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
        }
        finally {
            if (null != rs) {
                rs.close();
            }
            if (null != stmt) {
                stmt.close();
            }
        }
    }

    private void getSourceMetadata() throws SQLServerException {
        this.srcColumnMetadata = new HashMap<Integer, BulkColumnMetaData>();
        if (null != this.sourceResultSet) {
            try {
                this.srcColumnCount = this.sourceResultSetMetaData.getColumnCount();
                for (int i = 1; i <= this.srcColumnCount; ++i) {
                    this.srcColumnMetadata.put(i, new BulkColumnMetaData(this.sourceResultSetMetaData.getColumnName(i), 0 != this.sourceResultSetMetaData.isNullable(i), this.sourceResultSetMetaData.getPrecision(i), this.sourceResultSetMetaData.getScale(i), this.sourceResultSetMetaData.getColumnType(i), null));
                }
            }
            catch (SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
            }
        } else if (null != this.serverBulkData) {
            Set<Integer> columnOrdinals = this.serverBulkData.getColumnOrdinals();
            if (null == columnOrdinals || columnOrdinals.isEmpty()) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), null);
            }
            this.srcColumnCount = columnOrdinals.size();
            for (Integer columnOrdinal : columnOrdinals) {
                int currentColumn = columnOrdinal;
                this.srcColumnMetadata.put(currentColumn, new BulkColumnMetaData(this.serverBulkData.getColumnName(currentColumn), true, this.serverBulkData.getPrecision(currentColumn), this.serverBulkData.getScale(currentColumn), this.serverBulkData.getColumnType(currentColumn), this.serverBulkData instanceof SQLServerBulkCSVFileRecord ? ((SQLServerBulkCSVFileRecord)this.serverBulkData).getColumnDateTimeFormatter(currentColumn) : null));
            }
        } else {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), null);
        }
    }

    private int validateSourcePrecision(int srcPrecision, int srcJdbcType, int destPrecision) {
        if (1 > srcPrecision && Util.isCharType(srcJdbcType)) {
            srcPrecision = destPrecision;
        }
        return srcPrecision;
    }

    private void validateColumnMappings() throws SQLServerException {
        block28: {
            try {
                boolean foundColumn;
                ColumnMapping cm;
                int i;
                if (this.columnMappings.isEmpty()) {
                    if (this.destColumnCount != this.srcColumnCount) {
                        throw new SQLServerException(SQLServerException.getErrString("R_schemaMismatch"), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                    }
                    for (int i2 = 1; i2 <= this.srcColumnCount; ++i2) {
                        if (this.destColumnMetadata.get((Object)Integer.valueOf((int)i2)).isIdentity && !this.copyOptions.isKeepIdentity()) continue;
                        ColumnMapping cm2 = new ColumnMapping(i2, i2);
                        cm2.destinationColumnName = this.destColumnMetadata.get((Object)Integer.valueOf((int)i2)).columnName;
                        this.columnMappings.add(cm2);
                    }
                    if (null == this.serverBulkData) break block28;
                    Set<Integer> columnOrdinals = this.serverBulkData.getColumnOrdinals();
                    Iterator<Integer> columnsIterator = columnOrdinals.iterator();
                    int j = 1;
                    while (columnsIterator.hasNext()) {
                        int currentOrdinal = columnsIterator.next();
                        if (j != currentOrdinal) {
                            ArrayList<Integer> sortedList = new ArrayList<Integer>(columnOrdinals);
                            Collections.sort(sortedList);
                            columnsIterator = sortedList.iterator();
                            j = 1;
                            while (columnsIterator.hasNext()) {
                                currentOrdinal = columnsIterator.next();
                                if (j != currentOrdinal) {
                                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                                    Object[] msgArgs = new Object[]{currentOrdinal};
                                    throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                                }
                                ++j;
                            }
                            break block28;
                        }
                        ++j;
                    }
                    break block28;
                }
                int numMappings = this.columnMappings.size();
                for (i = 0; i < numMappings; ++i) {
                    cm = this.columnMappings.get(i);
                    if (-1 == cm.destinationColumnOrdinal) {
                        foundColumn = false;
                        for (int j = 1; j <= this.destColumnCount; ++j) {
                            if (!this.destColumnMetadata.get((Object)Integer.valueOf((int)j)).columnName.equals(cm.destinationColumnName)) continue;
                            foundColumn = true;
                            cm.destinationColumnOrdinal = j;
                            break;
                        }
                        if (foundColumn) continue;
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                        Object[] msgArgs = new Object[]{cm.destinationColumnName};
                        throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                    }
                    if (0 > cm.destinationColumnOrdinal || this.destColumnCount < cm.destinationColumnOrdinal) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                        Object[] msgArgs = new Object[]{cm.destinationColumnOrdinal};
                        throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                    }
                    cm.destinationColumnName = this.destColumnMetadata.get((Object)Integer.valueOf((int)cm.destinationColumnOrdinal)).columnName;
                }
                for (i = 0; i < numMappings; ++i) {
                    cm = this.columnMappings.get(i);
                    if (-1 == cm.sourceColumnOrdinal) {
                        foundColumn = false;
                        if (null != this.sourceResultSet) {
                            int columns = this.sourceResultSetMetaData.getColumnCount();
                            for (int j = 1; j <= columns; ++j) {
                                if (!this.sourceResultSetMetaData.getColumnName(j).equals(cm.sourceColumnName)) continue;
                                foundColumn = true;
                                cm.sourceColumnOrdinal = j;
                                break;
                            }
                        } else {
                            Set<Integer> columnOrdinals = this.serverBulkData.getColumnOrdinals();
                            for (Integer currentColumn : columnOrdinals) {
                                if (!this.serverBulkData.getColumnName(currentColumn).equals(cm.sourceColumnName)) continue;
                                foundColumn = true;
                                cm.sourceColumnOrdinal = currentColumn;
                                break;
                            }
                        }
                        if (!foundColumn) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            Object[] msgArgs = new Object[]{cm.sourceColumnName};
                            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                    } else {
                        boolean columnOutOfRange = true;
                        if (null != this.sourceResultSet) {
                            int columns = this.sourceResultSetMetaData.getColumnCount();
                            if (0 < cm.sourceColumnOrdinal && columns >= cm.sourceColumnOrdinal) {
                                columnOutOfRange = false;
                            }
                        } else if (this.srcColumnMetadata.containsKey(cm.sourceColumnOrdinal)) {
                            columnOutOfRange = false;
                        }
                        if (columnOutOfRange) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            Object[] msgArgs = new Object[]{cm.sourceColumnOrdinal};
                            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                    }
                    if (!this.destColumnMetadata.get((Object)Integer.valueOf((int)cm.destinationColumnOrdinal)).isIdentity || this.copyOptions.isKeepIdentity()) continue;
                    this.columnMappings.remove(i);
                    --numMappings;
                    --i;
                }
            }
            catch (SQLException e) {
                if (e instanceof SQLServerException && null != e.getSQLState() && e.getSQLState().equals(SQLState.COL_NOT_FOUND.getSQLStateCode())) {
                    throw (SQLServerException)e;
                }
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
            }
        }
        if (this.columnMappings.isEmpty()) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_BulkColumnMappingsIsEmpty"), null, 0, false);
        }
    }

    private void writeNullToTdsWriter(TDSWriter tdsWriter, int srcJdbcType, boolean isStreaming) throws SQLServerException {
        switch (srcJdbcType) {
            case -145: 
            case -16: 
            case -15: 
            case -9: 
            case -4: 
            case -3: 
            case -2: 
            case -1: 
            case 1: 
            case 12: {
                if (isStreaming) {
                    tdsWriter.writeLong(-1L);
                } else {
                    tdsWriter.writeByte((byte)-1);
                    tdsWriter.writeByte((byte)-1);
                }
                return;
            }
            case -155: 
            case -148: 
            case -146: 
            case -7: 
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 91: 
            case 92: 
            case 93: 
            case 2013: 
            case 2014: {
                tdsWriter.writeByte((byte)0);
                return;
            }
            case -156: {
                tdsWriter.writeInt(0);
                return;
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
        Object[] msgArgs = new Object[]{JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH)};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
    }

    private void writeColumnToTdsWriter(TDSWriter tdsWriter, int bulkPrecision, int bulkScale, int bulkJdbcType, boolean bulkNullable, int srcColOrdinal, int destColOrdinal, boolean isStreaming, Object colValue) throws SQLServerException {
        SSType destSSType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).ssType;
        bulkPrecision = this.validateSourcePrecision(bulkPrecision, bulkJdbcType, this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).precision);
        CryptoMetadata sourceCryptoMeta = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).cryptoMeta;
        if (null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).encryptionType && this.copyOptions.isAllowEncryptedValueModifications() || null != this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).cryptoMeta) {
            bulkJdbcType = -3;
        } else if (null != sourceCryptoMeta) {
            bulkJdbcType = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).jdbcType;
            bulkScale = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).scale;
        } else if (null != this.serverBulkData && this.connection.getSendTemporalDataTypesAsStringForBulkCopy()) {
            switch (bulkJdbcType) {
                case -155: 
                case 91: 
                case 92: 
                case 93: {
                    bulkJdbcType = 12;
                    break;
                }
            }
        }
        try {
            block9 : switch (bulkJdbcType) {
                case 4: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)4);
                    }
                    tdsWriter.writeInt((Integer)colValue);
                    break;
                }
                case 5: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)2);
                    }
                    tdsWriter.writeShort(((Number)colValue).shortValue());
                    break;
                }
                case -5: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeLong((Long)colValue);
                    break;
                }
                case -7: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)1);
                    }
                    tdsWriter.writeByte((byte)((Boolean)colValue != false ? 1 : 0));
                    break;
                }
                case -6: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)1);
                    }
                    tdsWriter.writeByte((byte)(((Number)colValue).shortValue() & 0xFF));
                    break;
                }
                case 6: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeDouble(((Float)colValue).floatValue());
                    break;
                }
                case 8: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeDouble((Double)colValue);
                    break;
                }
                case 7: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)4);
                    }
                    tdsWriter.writeReal(((Float)colValue).floatValue());
                    break;
                }
                case -148: 
                case -146: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkPrecision < Util.getValueLengthBaseOnJavaType(colValue, JavaType.of(colValue), null, null, JDBCType.of(bulkJdbcType))) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                        Object[] msgArgs = new Object[]{destSSType};
                        throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET, null);
                    }
                    tdsWriter.writeMoney((BigDecimal)colValue, bulkJdbcType);
                    break;
                }
                case 2: 
                case 3: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkPrecision < Util.getValueLengthBaseOnJavaType(colValue, JavaType.of(colValue), null, null, JDBCType.of(bulkJdbcType))) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                        Object[] msgArgs = new Object[]{destSSType};
                        throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET, null);
                    }
                    if (destSSType == SSType.MONEY) {
                        tdsWriter.writeMoney((BigDecimal)colValue, -148);
                        break;
                    }
                    if (destSSType == SSType.SMALLMONEY) {
                        tdsWriter.writeMoney((BigDecimal)colValue, -146);
                        break;
                    }
                    tdsWriter.writeBigDecimal((BigDecimal)colValue, bulkJdbcType, bulkPrecision, bulkScale);
                    break;
                }
                case -145: 
                case -1: 
                case 1: 
                case 12: {
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            Reader reader = colValue instanceof Reader ? (Reader)colValue : new StringReader(colValue.toString());
                            if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                                tdsWriter.writeReader(reader, -1L, true);
                            } else {
                                tdsWriter.writeNonUnicodeReader(reader, -1L, SSType.BINARY == destSSType || SSType.VARBINARY == destSSType || SSType.VARBINARYMAX == destSSType || SSType.IMAGE == destSSType);
                            }
                            reader.close();
                            break;
                        }
                        catch (IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    String colValueStr = colValue instanceof LocalDateTime ? ((LocalDateTime)colValue).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : (colValue instanceof LocalTime ? ((LocalTime)colValue).format(DateTimeFormatter.ISO_LOCAL_TIME) : colValue.toString());
                    if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                        int stringLength = colValueStr.length();
                        byte[] typevarlen = new byte[]{(byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF)};
                        tdsWriter.writeBytes(typevarlen);
                        tdsWriter.writeString(colValueStr);
                        break;
                    }
                    if (SSType.BINARY == destSSType || SSType.VARBINARY == destSSType) {
                        byte[] bytes = null;
                        try {
                            bytes = ParameterUtils.hexToBin(colValueStr);
                        }
                        catch (SQLServerException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                        tdsWriter.writeShort((short)bytes.length);
                        tdsWriter.writeBytes(bytes);
                        break;
                    }
                    SQLCollation destCollation = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation;
                    if (null != destCollation) {
                        byte[] value = colValueStr.getBytes(this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation.getCharset());
                        tdsWriter.writeShort((short)value.length);
                        tdsWriter.writeBytes(value);
                        break;
                    }
                    tdsWriter.writeShort((short)colValueStr.length());
                    tdsWriter.writeBytes(colValueStr.getBytes());
                    break;
                }
                case -16: 
                case -15: 
                case -9: {
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            Reader reader = colValue instanceof Reader ? (Reader)colValue : new StringReader(colValue.toString());
                            tdsWriter.writeReader(reader, -1L, true);
                            reader.close();
                            break;
                        }
                        catch (IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    int stringLength = colValue.toString().length();
                    byte[] typevarlen = new byte[]{(byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF)};
                    tdsWriter.writeBytes(typevarlen);
                    tdsWriter.writeString(colValue.toString());
                    break;
                }
                case -4: 
                case -3: 
                case -2: {
                    byte[] srcBytes;
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            InputStream iStream = colValue instanceof InputStream ? (InputStream)colValue : (colValue instanceof byte[] ? new ByteArrayInputStream((byte[])colValue) : new ByteArrayInputStream(ParameterUtils.hexToBin(colValue.toString())));
                            tdsWriter.writeStream(iStream, -1L, true);
                            iStream.close();
                            break;
                        }
                        catch (IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (colValue instanceof byte[]) {
                        srcBytes = (byte[])colValue;
                    } else {
                        try {
                            srcBytes = ParameterUtils.hexToBin(colValue.toString());
                        }
                        catch (SQLServerException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    tdsWriter.writeShort((short)srcBytes.length);
                    tdsWriter.writeBytes(srcBytes);
                    break;
                }
                case -151: 
                case -150: 
                case 93: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    switch (destSSType) {
                        case SMALLDATETIME: {
                            if (bulkNullable) {
                                tdsWriter.writeByte((byte)4);
                            }
                            tdsWriter.writeSmalldatetime(colValue.toString());
                            break block9;
                        }
                        case DATETIME: {
                            if (bulkNullable) {
                                tdsWriter.writeByte((byte)8);
                            }
                            tdsWriter.writeDatetime(colValue.toString());
                            break block9;
                        }
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)6);
                    } else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)7);
                    } else {
                        tdsWriter.writeByte((byte)8);
                    }
                    String timeStampValue = colValue.toString();
                    tdsWriter.writeTime(Timestamp.valueOf(timeStampValue), bulkScale);
                    tdsWriter.writeDate(timeStampValue.substring(0, timeStampValue.lastIndexOf(32)));
                    break;
                }
                case 91: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    tdsWriter.writeByte((byte)3);
                    tdsWriter.writeDate(colValue.toString());
                    break;
                }
                case 92: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)3);
                    } else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)4);
                    } else {
                        tdsWriter.writeByte((byte)5);
                    }
                    if (colValue instanceof String) {
                        Timestamp ts = new Timestamp(0L);
                        int nanos = 0;
                        int decimalIndex = ((String)colValue).indexOf(46);
                        if (decimalIndex != -1) {
                            nanos = Integer.parseInt(((String)colValue).substring(decimalIndex + 1));
                            colValue = ((String)colValue).substring(0, decimalIndex);
                        }
                        ts.setTime(Time.valueOf(colValue.toString()).getTime());
                        ts.setNanos(nanos);
                        tdsWriter.writeTime(ts, bulkScale);
                        break;
                    }
                    tdsWriter.writeTime((Timestamp)colValue, bulkScale);
                    break;
                }
                case 2013: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    } else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    } else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeOffsetTimeWithTimezone((OffsetTime)colValue, bulkScale);
                    break;
                }
                case 2014: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    } else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    } else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeOffsetDateTimeWithTimezone((OffsetDateTime)colValue, bulkScale);
                    break;
                }
                case -155: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    } else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    } else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeDateTimeOffset(colValue, bulkScale, destSSType);
                    break;
                }
                case -156: {
                    boolean isShiloh;
                    boolean bl = isShiloh = 8 >= this.connection.getServerMajorVersion();
                    if (isShiloh) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_SQLVariantSupport"));
                        throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                    }
                    this.writeSqlVariant(tdsWriter, colValue, this.sourceResultSet, srcColOrdinal, destColOrdinal, bulkJdbcType, bulkScale, isStreaming);
                    break;
                }
                default: {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    Object[] msgArgs = new Object[]{JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH)};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                }
            }
        }
        catch (ClassCastException ex) {
            if (null == colValue) {
                this.throwInvalidArgument("colValue");
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
            Object[] msgArgs = new Object[]{colValue.getClass().getSimpleName(), JDBCType.of(bulkJdbcType)};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, (Throwable)ex);
        }
    }

    private void writeSqlVariant(TDSWriter tdsWriter, Object colValue, ResultSet sourceResultSet, int srcColOrdinal, int destColOrdinal, int bulkJdbcType, int bulkScale, boolean isStreaming) throws SQLServerException {
        if (null == colValue) {
            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
            return;
        }
        SqlVariant variantType = ((SQLServerResultSet)sourceResultSet).getVariantInternalType(srcColOrdinal);
        int baseType = variantType.getBaseType();
        if (TDSType.TIMEN == TDSType.valueOf(baseType)) {
            variantType.setIsBaseTypeTimeValue(true);
            ((SQLServerResultSet)sourceResultSet).setInternalVariantType(srcColOrdinal, variantType);
            colValue = ((SQLServerResultSet)sourceResultSet).getObject(srcColOrdinal);
        }
        switch (TDSType.valueOf(baseType)) {
            case INT8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.INT8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeLong(Long.valueOf(colValue.toString()));
                break;
            }
            case INT4: {
                this.writeBulkCopySqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeInt(Integer.valueOf(colValue.toString()));
                break;
            }
            case INT2: {
                this.writeBulkCopySqlVariantHeader(4, TDSType.INT2.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeShort(Short.valueOf(colValue.toString()));
                break;
            }
            case INT1: {
                this.writeBulkCopySqlVariantHeader(3, TDSType.INT1.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeByte(Byte.valueOf(colValue.toString()));
                break;
            }
            case FLOAT8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.FLOAT8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDouble(Double.valueOf(colValue.toString()));
                break;
            }
            case FLOAT4: {
                this.writeBulkCopySqlVariantHeader(6, TDSType.FLOAT4.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeReal(Float.valueOf(colValue.toString()).floatValue());
                break;
            }
            case MONEY8: {
                this.writeBulkCopySqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeByte((byte)38);
                tdsWriter.writeByte((byte)4);
                tdsWriter.writeSqlVariantInternalBigDecimal((BigDecimal)colValue, bulkJdbcType);
                break;
            }
            case MONEY4: {
                this.writeBulkCopySqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeByte((byte)38);
                tdsWriter.writeByte((byte)4);
                tdsWriter.writeSqlVariantInternalBigDecimal((BigDecimal)colValue, bulkJdbcType);
                break;
            }
            case BIT1: {
                this.writeBulkCopySqlVariantHeader(3, TDSType.BIT1.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeByte((byte)((Boolean)colValue != false ? 1 : 0));
                break;
            }
            case DATEN: {
                this.writeBulkCopySqlVariantHeader(5, TDSType.DATEN.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDate(colValue.toString());
                break;
            }
            case TIMEN: {
                int timeBulkScale = variantType.getScale();
                int timeHeaderLength = 2 >= timeBulkScale ? 6 : (4 >= timeBulkScale ? 7 : 8);
                this.writeBulkCopySqlVariantHeader(timeHeaderLength, TDSType.TIMEN.byteValue(), (byte)1, tdsWriter);
                tdsWriter.writeByte((byte)timeBulkScale);
                tdsWriter.writeTime((Timestamp)colValue, timeBulkScale);
                break;
            }
            case DATETIME8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDatetime(colValue.toString());
                break;
            }
            case DATETIME4: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDatetime(colValue.toString());
                break;
            }
            case DATETIME2N: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME2N.byteValue(), (byte)1, tdsWriter);
                tdsWriter.writeByte((byte)3);
                String timeStampValue = colValue.toString();
                tdsWriter.writeTime(Timestamp.valueOf(timeStampValue), 3);
                tdsWriter.writeDate(timeStampValue.substring(0, timeStampValue.lastIndexOf(32)));
                break;
            }
            case BIGCHAR: {
                int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                SQLCollation destCollation = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case BIGVARCHAR: {
                int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGVARCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                SQLCollation destCollation = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case NCHAR: {
                int length = colValue.toString().length() * 2;
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.NCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                int stringLength = colValue.toString().length();
                byte[] typevarlen = new byte[]{(byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF)};
                tdsWriter.writeBytes(typevarlen);
                tdsWriter.writeString(colValue.toString());
                break;
            }
            case NVARCHAR: {
                int length = colValue.toString().length() * 2;
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.NVARCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                int stringLength = colValue.toString().length();
                byte[] typevarlen = new byte[]{(byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF)};
                tdsWriter.writeBytes(typevarlen);
                tdsWriter.writeString(colValue.toString());
                break;
            }
            case GUID: {
                int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGCHAR.byteValue(), (byte)7, tdsWriter);
                SQLCollation collation = null != this.destColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).collation ? this.destColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).collation : this.connection.getDatabaseCollation();
                variantType.setCollation(collation);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                SQLCollation destCollation = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case BIGBINARY: {
                byte[] srcBytes;
                byte[] b = (byte[])colValue;
                int length = b.length;
                this.writeBulkCopySqlVariantHeader(4 + length, TDSType.BIGVARBINARY.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeShort((short)variantType.getMaxLength());
                if (colValue instanceof byte[]) {
                    srcBytes = (byte[])colValue;
                } else {
                    try {
                        srcBytes = ParameterUtils.hexToBin(colValue.toString());
                    }
                    catch (SQLServerException e) {
                        throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                    }
                }
                tdsWriter.writeBytes(srcBytes);
                break;
            }
            case BIGVARBINARY: {
                byte[] srcBytes;
                byte[] b = (byte[])colValue;
                int length = b.length;
                this.writeBulkCopySqlVariantHeader(4 + length, TDSType.BIGVARBINARY.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeShort((short)variantType.getMaxLength());
                if (colValue instanceof byte[]) {
                    srcBytes = (byte[])colValue;
                } else {
                    try {
                        srcBytes = ParameterUtils.hexToBin(colValue.toString());
                    }
                    catch (SQLServerException e) {
                        throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                    }
                }
                tdsWriter.writeBytes(srcBytes);
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                Object[] msgArgs = new Object[]{JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH)};
                SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
            }
        }
    }

    private void writeBulkCopySqlVariantHeader(int length, byte tdsType, byte probBytes, TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeInt(length);
        tdsWriter.writeByte(tdsType);
        tdsWriter.writeByte(probBytes);
    }

    private Object readColumnFromResultSet(int srcColOrdinal, int srcJdbcType, boolean isStreaming, boolean isDestEncrypted) throws SQLServerException {
        CryptoMetadata srcCryptoMeta = null;
        if (this.sourceResultSet instanceof SQLServerResultSet && null != (srcCryptoMeta = ((SQLServerResultSet)this.sourceResultSet).getterGetColumn(srcColOrdinal).getCryptoMetadata())) {
            srcJdbcType = srcCryptoMeta.baseTypeInfo.getSSType().getJDBCType().asJavaSqlType();
            BulkColumnMetaData temp = this.srcColumnMetadata.get(srcColOrdinal);
            this.srcColumnMetadata.put(srcColOrdinal, new BulkColumnMetaData(temp, srcCryptoMeta));
        }
        try {
            switch (srcJdbcType) {
                case -7: 
                case -6: 
                case -5: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: {
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
                case -148: 
                case -146: 
                case 2: 
                case 3: {
                    return this.sourceResultSet.getBigDecimal(srcColOrdinal);
                }
                case -145: 
                case -1: 
                case 1: 
                case 12: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getCharacterStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getString(srcColOrdinal);
                }
                case -16: 
                case -15: 
                case -9: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getNCharacterStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
                case -4: 
                case -3: 
                case -2: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getBinaryStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getBytes(srcColOrdinal);
                }
                case -151: 
                case -150: 
                case 92: 
                case 93: {
                    return this.sourceResultSet.getTimestamp(srcColOrdinal);
                }
                case 91: {
                    return this.sourceResultSet.getDate(srcColOrdinal);
                }
                case -155: {
                    return this.sourceResultSet.getObject(srcColOrdinal, DateTimeOffset.class);
                }
                case -156: {
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
            Object[] msgArgs = new Object[]{JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH)};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
            return null;
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
        }
    }

    private void writeColumn(TDSWriter tdsWriter, int srcColOrdinal, int destColOrdinal, Object colValue) throws SQLServerException {
        String destName = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).columnName;
        SSType destSSType = null;
        int srcPrecision = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).precision;
        int srcScale = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).scale;
        int srcJdbcType = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).jdbcType;
        boolean srcNullable = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).isNullable;
        int destPrecision = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).precision;
        boolean isStreaming = -15 == srcJdbcType || -9 == srcJdbcType || -16 == srcJdbcType ? 4000 < srcPrecision || 4000 < destPrecision : 8000 < srcPrecision || 8000 < destPrecision;
        CryptoMetadata destCryptoMeta = this.destColumnMetadata.get((Object)Integer.valueOf((int)destColOrdinal)).cryptoMeta;
        if (null != destCryptoMeta) {
            destSSType = destCryptoMeta.baseTypeInfo.getSSType();
        }
        if (null != this.sourceResultSet) {
            colValue = this.readColumnFromResultSet(srcColOrdinal, srcJdbcType, isStreaming, null != destCryptoMeta);
            this.validateStringBinaryLengths(colValue, srcColOrdinal, destColOrdinal);
            if (!(this.copyOptions.isAllowEncryptedValueModifications() || null != destCryptoMeta && null != colValue)) {
                this.validateDataTypeConversions(srcColOrdinal, destColOrdinal);
            }
        } else if (null != this.serverBulkData && null == destCryptoMeta) {
            this.validateStringBinaryLengths(colValue, srcColOrdinal, destColOrdinal);
        } else if (null != this.serverBulkData && null != destCryptoMeta) {
            if (91 == srcJdbcType || 92 == srcJdbcType || 93 == srcJdbcType || -155 == srcJdbcType || 2013 == srcJdbcType || 2014 == srcJdbcType) {
                colValue = this.getTemporalObjectFromCSV(colValue, srcJdbcType, srcColOrdinal);
            } else if (2 == srcJdbcType || 3 == srcJdbcType) {
                int baseDestPrecision = destCryptoMeta.baseTypeInfo.getPrecision();
                int baseDestScale = destCryptoMeta.baseTypeInfo.getScale();
                if (srcScale != baseDestScale || srcPrecision != baseDestPrecision) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    String src = JDBCType.of(srcJdbcType) + "(" + srcPrecision + "," + srcScale + ")";
                    String dest = destSSType + "(" + baseDestPrecision + "," + baseDestScale + ")";
                    Object[] msgArgs = new Object[]{src, dest, destName};
                    throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                }
            }
        }
        CryptoMetadata srcCryptoMeta = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).cryptoMeta;
        if (null != destCryptoMeta && null != colValue) {
            JDBCType baseSrcJdbcType;
            JDBCType jDBCType = baseSrcJdbcType = null != srcCryptoMeta ? this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).cryptoMeta.baseTypeInfo.getSSType().getJDBCType() : JDBCType.of(srcJdbcType);
            if (JDBCType.TIMESTAMP == baseSrcJdbcType) {
                if (SSType.DATETIME == destSSType) {
                    baseSrcJdbcType = JDBCType.DATETIME;
                } else if (SSType.SMALLDATETIME == destSSType) {
                    baseSrcJdbcType = JDBCType.SMALLDATETIME;
                }
            }
            if (!(SSType.MONEY == destSSType && JDBCType.DECIMAL == baseSrcJdbcType || SSType.SMALLMONEY == destSSType && JDBCType.DECIMAL == baseSrcJdbcType || SSType.GUID == destSSType && JDBCType.CHAR == baseSrcJdbcType || Util.isCharType(destSSType).booleanValue() && Util.isCharType(srcJdbcType) || this.sourceResultSet instanceof SQLServerResultSet || baseSrcJdbcType.normalizationCheck(destSSType))) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionAE"));
                Object[] msgArgs = new Object[]{baseSrcJdbcType, destSSType};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
            if (baseSrcJdbcType == JDBCType.DATE || baseSrcJdbcType == JDBCType.TIMESTAMP || baseSrcJdbcType == JDBCType.TIME || baseSrcJdbcType == JDBCType.DATETIMEOFFSET || baseSrcJdbcType == JDBCType.DATETIME || baseSrcJdbcType == JDBCType.SMALLDATETIME) {
                colValue = this.getEncryptedTemporalBytes(tdsWriter, baseSrcJdbcType, colValue, destCryptoMeta.baseTypeInfo.getScale());
            } else {
                TypeInfo destTypeInfo = destCryptoMeta.getBaseTypeInfo();
                JDBCType destJdbcType = destTypeInfo.getSSType().getJDBCType();
                if (!Util.isBinaryType(destJdbcType.getIntValue()).booleanValue() && colValue instanceof byte[]) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    Object[] msgArgs = new Object[]{baseSrcJdbcType, destJdbcType, destName};
                    throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                }
                colValue = SQLServerSecurityUtility.encryptWithKey(this.normalizedValue(destJdbcType, colValue, baseSrcJdbcType, destTypeInfo.getPrecision(), destTypeInfo.getScale(), destName), destCryptoMeta, this.connection, null);
            }
        }
        this.writeColumnToTdsWriter(tdsWriter, srcPrecision, srcScale, srcJdbcType, srcNullable, srcColOrdinal, destColOrdinal, isStreaming, colValue);
    }

    protected Object getTemporalObjectFromCSVWithFormatter(String valueStrUntrimmed, int srcJdbcType, int srcColOrdinal, DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        try {
            TemporalAccessor ta = dateTimeFormatter.parse(valueStrUntrimmed);
            int taOffsetSec = 0;
            int taNano = 0;
            int taDay = 0;
            int taMonth = 0;
            int taYear = 0;
            int taSec = 0;
            int taMin = 0;
            int taHour = 0;
            if (ta.isSupported(ChronoField.NANO_OF_SECOND)) {
                taNano = ta.get(ChronoField.NANO_OF_SECOND);
            }
            if (ta.isSupported(ChronoField.OFFSET_SECONDS)) {
                taOffsetSec = ta.get(ChronoField.OFFSET_SECONDS);
            }
            if (ta.isSupported(ChronoField.HOUR_OF_DAY)) {
                taHour = ta.get(ChronoField.HOUR_OF_DAY);
            }
            if (ta.isSupported(ChronoField.MINUTE_OF_HOUR)) {
                taMin = ta.get(ChronoField.MINUTE_OF_HOUR);
            }
            if (ta.isSupported(ChronoField.SECOND_OF_MINUTE)) {
                taSec = ta.get(ChronoField.SECOND_OF_MINUTE);
            }
            if (ta.isSupported(ChronoField.DAY_OF_MONTH)) {
                taDay = ta.get(ChronoField.DAY_OF_MONTH);
            }
            if (ta.isSupported(ChronoField.MONTH_OF_YEAR)) {
                taMonth = ta.get(ChronoField.MONTH_OF_YEAR);
            }
            if (ta.isSupported(ChronoField.YEAR)) {
                taYear = ta.get(ChronoField.YEAR);
            }
            GregorianCalendar cal = new GregorianCalendar(new SimpleTimeZone(taOffsetSec * 1000, ""));
            cal.clear();
            cal.set(11, taHour);
            cal.set(12, taMin);
            cal.set(13, taSec);
            cal.set(5, taDay);
            cal.set(2, taMonth - 1);
            cal.set(1, taYear);
            int fractionalSecondsLength = Integer.toString(taNano).length();
            for (int i = 0; i < 9 - fractionalSecondsLength; ++i) {
                taNano *= 10;
            }
            Timestamp ts = new Timestamp(cal.getTimeInMillis());
            ts.setNanos(taNano);
            switch (srcJdbcType) {
                case 93: {
                    return ts;
                }
                case 92: {
                    cal.set(this.connection.baseYear(), 0, 1);
                    ts = new Timestamp(cal.getTimeInMillis());
                    ts.setNanos(taNano);
                    return new Timestamp(ts.getTime());
                }
                case 91: {
                    return new Date(ts.getTime());
                }
                case -155: {
                    return DateTimeOffset.valueOf(ts, taOffsetSec / 60);
                }
            }
            return valueStrUntrimmed;
        }
        catch (ArithmeticException | DateTimeException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            Object[] msgArgs = new Object[]{JDBCType.of(srcJdbcType)};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
    }

    private Object getTemporalObjectFromCSV(Object value, int srcJdbcType, int srcColOrdinal) throws SQLServerException {
        DateTimeFormatter dateTimeFormatter;
        if (2013 == srcJdbcType) {
            MessageFormat form1 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
            Object[] msgArgs1 = new Object[]{"TIME_WITH_TIMEZONE"};
            throw new SQLServerException((Object)this, form1.format(msgArgs1), null, 0, false);
        }
        if (2014 == srcJdbcType) {
            MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
            Object[] msgArgs2 = new Object[]{"TIMESTAMP_WITH_TIMEZONE"};
            throw new SQLServerException((Object)this, form2.format(msgArgs2), null, 0, false);
        }
        String valueStr = null;
        String valueStrUntrimmed = null;
        if (null != value && value instanceof String) {
            valueStrUntrimmed = (String)value;
            valueStr = valueStrUntrimmed.trim();
        }
        if (null == valueStr) {
            switch (srcJdbcType) {
                case -155: 
                case 91: 
                case 92: 
                case 93: {
                    return null;
                }
            }
        }
        if (null != (dateTimeFormatter = this.srcColumnMetadata.get((Object)Integer.valueOf((int)srcColOrdinal)).dateTimeFormatter)) {
            return this.getTemporalObjectFromCSVWithFormatter(valueStrUntrimmed, srcJdbcType, srcColOrdinal, dateTimeFormatter);
        }
        try {
            switch (srcJdbcType) {
                case 93: {
                    return Timestamp.valueOf(valueStr);
                }
                case 92: {
                    String time = this.connection.baseYear() + "-01-01 " + valueStr;
                    return Timestamp.valueOf(time);
                }
                case 91: {
                    return Date.valueOf(valueStr);
                }
                case -155: {
                    int seconds;
                    int endIndx = valueStr.indexOf(45, 0);
                    int year = Integer.parseInt(valueStr.substring(0, endIndx));
                    int startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(45, startIndx);
                    int month = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(32, startIndx);
                    int day = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(58, startIndx);
                    int hour = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(58, startIndx);
                    int minute = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(46, startIndx);
                    int totalOffset = 0;
                    int fractionalSeconds = 0;
                    boolean isNegativeOffset = false;
                    boolean hasTimeZone = false;
                    int fractionalSecondsLength = 0;
                    if (-1 != endIndx) {
                        seconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        ++endIndx;
                        startIndx = endIndx;
                        if (-1 != (endIndx = valueStr.indexOf(32, startIndx))) {
                            fractionalSeconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                            fractionalSecondsLength = endIndx - startIndx;
                            hasTimeZone = true;
                        } else {
                            fractionalSeconds = Integer.parseInt(valueStr.substring(startIndx));
                            fractionalSecondsLength = valueStr.length() - startIndx;
                        }
                    } else {
                        endIndx = valueStr.indexOf(32, startIndx);
                        if (-1 != endIndx) {
                            hasTimeZone = true;
                            seconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        } else {
                            seconds = Integer.parseInt(valueStr.substring(startIndx));
                            ++endIndx;
                        }
                    }
                    if (hasTimeZone) {
                        if ('+' == valueStr.charAt(startIndx = ++endIndx)) {
                            ++startIndx;
                        } else if ('-' == valueStr.charAt(startIndx)) {
                            isNegativeOffset = true;
                            ++startIndx;
                        }
                        endIndx = valueStr.indexOf(58, startIndx);
                        int offsethour = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        startIndx = ++endIndx;
                        int offsetMinute = Integer.parseInt(valueStr.substring(startIndx));
                        totalOffset = offsethour * 60 + offsetMinute;
                        if (isNegativeOffset) {
                            totalOffset = -totalOffset;
                        }
                    }
                    GregorianCalendar cal = new GregorianCalendar(new SimpleTimeZone(totalOffset * 60 * 1000, ""), Locale.US);
                    cal.clear();
                    cal.set(11, hour);
                    cal.set(12, minute);
                    cal.set(13, seconds);
                    cal.set(5, day);
                    cal.set(2, month - 1);
                    cal.set(1, year);
                    for (int i = 0; i < 9 - fractionalSecondsLength; ++i) {
                        fractionalSeconds *= 10;
                    }
                    Timestamp ts = new Timestamp(cal.getTimeInMillis());
                    ts.setNanos(fractionalSeconds);
                    return DateTimeOffset.valueOf(ts, totalOffset);
                }
            }
        }
        catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            Object[] msgArgs = new Object[]{JDBCType.of(srcJdbcType)};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        return value;
    }

    private byte[] getEncryptedTemporalBytes(TDSWriter tdsWriter, JDBCType srcTemporalJdbcType, Object colValue, int scale) throws SQLServerException {
        switch (srcTemporalJdbcType) {
            case DATE: {
                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                calendar.setTimeInMillis(((Date)colValue).getTime());
                return tdsWriter.writeEncryptedScaledTemporal(calendar, 0, 0, SSType.DATE, (short)0, null);
            }
            case TIME: {
                int subSecondNanos;
                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                if (colValue instanceof Timestamp) {
                    subSecondNanos = ((Timestamp)colValue).getNanos();
                } else {
                    subSecondNanos = 1000000 * (int)(utcMillis % 1000L);
                    if (subSecondNanos < 0) {
                        subSecondNanos += 1000000000;
                    }
                }
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.TIME, (short)0, null);
            }
            case TIMESTAMP: {
                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                int subSecondNanos = ((Timestamp)colValue).getNanos();
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIME2, (short)0, null);
            }
            case DATETIME: 
            case SMALLDATETIME: {
                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                int subSecondNanos = ((Timestamp)colValue).getNanos();
                return tdsWriter.getEncryptedDateTimeAsBytes(calendar, subSecondNanos, srcTemporalJdbcType, null);
            }
            case DATETIMEOFFSET: {
                DateTimeOffset dtoValue = (DateTimeOffset)colValue;
                long utcMillis = dtoValue.getTimestamp().getTime();
                int subSecondNanos = dtoValue.getTimestamp().getNanos();
                int minutesOffset = dtoValue.getMinutesOffset();
                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                calendar.setLenient(true);
                calendar.clear();
                calendar.setTimeInMillis(utcMillis);
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET, (short)minutesOffset, null);
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
        Object[] msgArgs = new Object[]{srcTemporalJdbcType};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    private byte[] normalizedValue(JDBCType destJdbcType, Object value, JDBCType srcJdbcType, int destPrecision, int destScale, String destName) throws SQLServerException {
        Long longValue = null;
        byte[] byteValue = null;
        try {
            switch (destJdbcType) {
                case BIT: {
                    longValue = (Boolean)value != false ? 1 : 0;
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case TINYINT: 
                case SMALLINT: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (Boolean)value != false ? 1 : 0;
                            break;
                        }
                        default: {
                            if (value instanceof Integer) {
                                int intValue = (Integer)value;
                                short shortValue = (short)intValue;
                                longValue = shortValue;
                                break;
                            }
                            longValue = (long)((Short)value);
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case INTEGER: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (Boolean)value != false ? 1 : 0;
                            break;
                        }
                        case TINYINT: 
                        case SMALLINT: {
                            longValue = (long)((Short)value);
                            break;
                        }
                        default: {
                            longValue = (long)((Integer)value);
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case BIGINT: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (Boolean)value != false ? 1 : 0;
                            break;
                        }
                        case TINYINT: 
                        case SMALLINT: {
                            longValue = (long)((Short)value);
                            break;
                        }
                        case INTEGER: {
                            longValue = (long)((Integer)value);
                            break;
                        }
                        default: {
                            longValue = (long)((Long)value);
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case BINARY: 
                case VARBINARY: 
                case LONGVARBINARY: {
                    byte[] byteArrayValue = value instanceof String ? ParameterUtils.hexToBin((String)value) : (byte[])value;
                    if (byteArrayValue.length > destPrecision) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
                        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                    }
                    return byteArrayValue;
                }
                case GUID: {
                    return Util.asGuidByteArray(UUID.fromString((String)value));
                }
                case CHAR: 
                case VARCHAR: 
                case LONGVARCHAR: {
                    if (((String)value).length() > destPrecision) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
                        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                    }
                    return ((String)value).getBytes(StandardCharsets.UTF_8);
                }
                case NCHAR: 
                case NVARCHAR: 
                case LONGNVARCHAR: {
                    if (((String)value).length() > destPrecision) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
                        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                    }
                    return ((String)value).getBytes(StandardCharsets.UTF_16LE);
                }
                case REAL: {
                    Float floatValue = Float.valueOf(value instanceof String ? Float.parseFloat((String)value) : ((Float)value).floatValue());
                    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatValue.floatValue()).array();
                }
                case FLOAT: 
                case DOUBLE: {
                    Double doubleValue = value instanceof String ? Double.parseDouble((String)value) : (Double)value;
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(doubleValue).array();
                }
                case NUMERIC: 
                case DECIMAL: {
                    int srcDataScale = ((BigDecimal)value).scale();
                    int srcDataPrecision = ((BigDecimal)value).precision();
                    BigDecimal bigDataValue = (BigDecimal)value;
                    if (srcDataPrecision > destPrecision || srcDataScale > destScale) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
                        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                    }
                    if (srcDataScale < destScale) {
                        bigDataValue = bigDataValue.setScale(destScale);
                    }
                    byteValue = DDC.convertBigDecimalToBytes(bigDataValue, bigDataValue.scale());
                    byte[] decimalbyteValue = new byte[16];
                    System.arraycopy(byteValue, 2, decimalbyteValue, 0, byteValue.length - 2);
                    return decimalbyteValue;
                }
                case SMALLMONEY: 
                case MONEY: {
                    BigDecimal bdValue = (BigDecimal)value;
                    Util.validateMoneyRange(bdValue, destJdbcType);
                    int digitCount = bdValue.precision() - bdValue.scale() + 4;
                    long moneyVal = ((BigDecimal)value).multiply(new BigDecimal(10000), new MathContext(digitCount, RoundingMode.HALF_UP)).longValue();
                    ByteBuffer bbuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
                    bbuf.putInt((int)(moneyVal >> 32)).array();
                    bbuf.putInt((int)moneyVal).array();
                    return bbuf.array();
                }
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
            Object[] msgArgs = new Object[]{destJdbcType};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        catch (NumberFormatException ex) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        catch (IllegalArgumentException ex) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        catch (ClassCastException ex) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            Object[] msgArgs = new Object[]{srcJdbcType, destJdbcType, destName};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
    }

    private boolean goToNextRow() throws SQLServerException {
        try {
            if (null != this.sourceResultSet) {
                return this.sourceResultSet.next();
            }
            return this.serverBulkData.next();
        }
        catch (SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
        }
    }

    private boolean writeBatchData(TDSWriter tdsWriter, TDSCommand command, boolean insertRowByRow) throws SQLServerException {
        int batchsize = this.copyOptions.getBatchSize();
        int row = 0;
        while (0 == batchsize || row < batchsize) {
            if (!this.goToNextRow()) {
                return false;
            }
            if (insertRowByRow) {
                ((SQLServerResultSet)this.sourceResultSet).getTDSReader().readPacket();
                tdsWriter = this.sendBulkCopyCommand(command);
            }
            tdsWriter.writeByte((byte)-47);
            if (null != this.sourceResultSet) {
                for (ColumnMapping columnMapping : this.columnMappings) {
                    this.writeColumn(tdsWriter, columnMapping.sourceColumnOrdinal, columnMapping.destinationColumnOrdinal, null);
                }
            } else {
                Object[] rowObjects;
                try {
                    rowObjects = this.serverBulkData.getRowData();
                }
                catch (Exception ex) {
                    throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), ex);
                }
                for (ColumnMapping columnMapping : this.columnMappings) {
                    this.writeColumn(tdsWriter, columnMapping.sourceColumnOrdinal, columnMapping.destinationColumnOrdinal, rowObjects[columnMapping.sourceColumnOrdinal - 1]);
                }
            }
            ++row;
            if (!insertRowByRow) continue;
            this.writePacketDataDone(tdsWriter);
            tdsWriter.setCryptoMetaData(null);
            TDSParser.parse(command.startResponse(), command.getLogContext());
        }
        return true;
    }

    void setStmtColumnEncriptionSetting(SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting) {
        this.stmtColumnEncriptionSetting = stmtColumnEncriptionSetting;
    }

    void setDestinationTableMetadata(SQLServerResultSet rs) {
        this.destinationTableMetadata = rs;
    }

    private boolean unicodeConversionRequired(int jdbcType, SSType ssType) {
        return !(1 != jdbcType && 12 != jdbcType && -16 != jdbcType || SSType.NCHAR != ssType && SSType.NVARCHAR != ssType && SSType.NVARCHARMAX != ssType);
    }

    class BulkColumnMetaData {
        String columnName;
        SSType ssType = null;
        int jdbcType;
        int precision;
        int scale;
        SQLCollation collation;
        byte[] flags = new byte[2];
        boolean isIdentity = false;
        boolean isNullable;
        String collationName;
        CryptoMetadata cryptoMeta = null;
        DateTimeFormatter dateTimeFormatter = null;
        String encryptionType = null;

        BulkColumnMetaData(Column column) {
            this.cryptoMeta = column.getCryptoMetadata();
            TypeInfo typeInfo = column.getTypeInfo();
            this.columnName = column.getColumnName();
            this.ssType = typeInfo.getSSType();
            this.flags = typeInfo.getFlags();
            this.isIdentity = typeInfo.isIdentity();
            this.isNullable = typeInfo.isNullable();
            this.precision = typeInfo.getPrecision();
            this.scale = typeInfo.getScale();
            this.collation = typeInfo.getSQLCollation();
            this.jdbcType = this.ssType.getJDBCType().getIntValue();
        }

        BulkColumnMetaData(String colName, boolean isNullable, int precision, int scale, int jdbcType, DateTimeFormatter dateTimeFormatter) {
            this.columnName = colName;
            this.isNullable = isNullable;
            this.precision = precision;
            this.scale = scale;
            this.jdbcType = jdbcType;
            this.dateTimeFormatter = dateTimeFormatter;
        }

        BulkColumnMetaData(Column column, String collationName, String encryptionType) {
            this(column);
            this.collationName = collationName;
            this.encryptionType = encryptionType;
        }

        BulkColumnMetaData(BulkColumnMetaData bulkColumnMetaData, CryptoMetadata cryptoMeta) {
            this.columnName = bulkColumnMetaData.columnName;
            this.isNullable = bulkColumnMetaData.isNullable;
            this.precision = bulkColumnMetaData.precision;
            this.scale = bulkColumnMetaData.scale;
            this.jdbcType = bulkColumnMetaData.jdbcType;
            this.cryptoMeta = cryptoMeta;
        }
    }

    private class ColumnMapping
    implements Serializable {
        private static final long serialVersionUID = 6428337550654423919L;
        String sourceColumnName = null;
        int sourceColumnOrdinal = -1;
        String destinationColumnName = null;
        int destinationColumnOrdinal = -1;

        ColumnMapping(String source, String dest) {
            this.sourceColumnName = source;
            this.destinationColumnName = dest;
        }

        ColumnMapping(String source, int dest) {
            this.sourceColumnName = source;
            this.destinationColumnOrdinal = dest;
        }

        ColumnMapping(int source, String dest) {
            this.sourceColumnOrdinal = source;
            this.destinationColumnName = dest;
        }

        ColumnMapping(int source, int dest) {
            this.sourceColumnOrdinal = source;
            this.destinationColumnOrdinal = dest;
        }
    }
}

