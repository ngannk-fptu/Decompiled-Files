/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ActivityCorrelator;
import com.microsoft.sqlserver.jdbc.CekTableEntry;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DescribeParameterEncryptionResultSet1;
import com.microsoft.sqlserver.jdbc.DescribeParameterEncryptionResultSet2;
import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.ISQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterMetaDataCache;
import com.microsoft.sqlserver.jdbc.ParsedSQLCacheItem;
import com.microsoft.sqlserver.jdbc.PrepareMethod;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerParameterMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.UninterruptableTDSCommand;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import microsoft.sql.DateTimeOffset;

public class SQLServerPreparedStatement
extends SQLServerStatement
implements ISQLServerPreparedStatement {
    private static final long serialVersionUID = -6292257029445685221L;
    private static final int BATCH_STATEMENT_DELIMITER_TDS_71 = 128;
    private static final int BATCH_STATEMENT_DELIMITER_TDS_72 = 255;
    private static final String EXECUTE_BATCH_STRING = "executeBatch";
    private static final String ACTIVITY_ID = " ActivityId: ";
    static final int NBATCH_STATEMENT_DELIMITER = 255;
    private String preparedTypeDefinitions;
    final String userSQL;
    final int[] userSQLParamPositions;
    private String preparedSQL;
    private boolean isExecutedAtLeastOnce = false;
    private boolean isSpPrepareExecuted = false;
    private transient SQLServerConnection.PreparedStatementHandle cachedPreparedStatementHandle;
    private SQLServerConnection.CityHash128Key sqlTextCacheKey;
    private ArrayList<String> parameterNames;
    final boolean bReturnValueSyntax;
    private boolean useFmtOnly = this.connection.getUseFmtOnly();
    int outParamIndexAdjustment;
    ArrayList<Parameter[]> batchParamValues;
    private int prepStmtHandle = 0;
    private SQLServerStatement internalStmt = null;
    private boolean useBulkCopyForBatchInsert;
    private boolean expectPrepStmtHandle = false;
    private boolean encryptionMetadataIsRetrieved = false;
    private String localUserSQL;
    private Vector<CryptoMetadata> cryptoMetaBatch = new Vector();
    private ArrayList<byte[]> enclaveCEKs;

    private void setPreparedStatementHandle(int handle) {
        this.prepStmtHandle = handle;
    }

    @Override
    public String toString() {
        return "sp_executesql SQL: " + this.preparedSQL;
    }

    private boolean getUseBulkCopyForBatchInsert() throws SQLServerException {
        this.checkClosed();
        return this.useBulkCopyForBatchInsert;
    }

    private void setUseBulkCopyForBatchInsert(boolean useBulkCopyForBatchInsert) throws SQLServerException {
        this.checkClosed();
        this.useBulkCopyForBatchInsert = useBulkCopyForBatchInsert;
    }

    @Override
    public int getPreparedStatementHandle() throws SQLServerException {
        this.checkClosed();
        return this.prepStmtHandle;
    }

    private boolean hasPreparedStatementHandle() {
        return 0 < this.prepStmtHandle;
    }

    private boolean resetPrepStmtHandle(boolean discardCurrentCacheItem) {
        boolean statementPoolingUsed;
        boolean bl = statementPoolingUsed = null != this.cachedPreparedStatementHandle;
        if (statementPoolingUsed && discardCurrentCacheItem) {
            this.cachedPreparedStatementHandle.setIsExplicitlyDiscarded();
        }
        this.prepStmtHandle = 0;
        return statementPoolingUsed;
    }

    SQLServerPreparedStatement(SQLServerConnection conn, String sql, int nRSType, int nRSConcur, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        super(conn, nRSType, nRSConcur, stmtColEncSetting);
        if (null == sql) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
            Object[] msgArgs1 = new Object[]{"Statement SQL"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.stmtPoolable = true;
        this.sqlTextCacheKey = new SQLServerConnection.CityHash128Key(sql);
        ParsedSQLCacheItem parsedSQL = SQLServerConnection.getCachedParsedSQL(this.sqlTextCacheKey);
        if (null != parsedSQL) {
            if (null != this.connection && this.connection.isStatementPoolingEnabled()) {
                this.isExecutedAtLeastOnce = true;
            }
        } else {
            parsedSQL = SQLServerConnection.parseAndCacheSQL(this.sqlTextCacheKey, sql);
        }
        this.procedureName = parsedSQL.procedureName;
        this.bReturnValueSyntax = parsedSQL.bReturnValueSyntax;
        this.userSQL = parsedSQL.processedSQL;
        this.userSQLParamPositions = parsedSQL.parameterPositions;
        this.initParams(this.userSQLParamPositions.length);
        this.useBulkCopyForBatchInsert = conn.getUseBulkCopyForBatchInsert();
    }

    private void closePreparedHandle() {
        if (!this.hasPreparedStatementHandle()) {
            return;
        }
        if (this.connection.isSessionUnAvailable()) {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.finer(this + ": Not closing PreparedHandle:" + this.prepStmtHandle + "; connection is already closed.");
            }
        } else {
            this.isExecutedAtLeastOnce = false;
            final int handleToClose = this.prepStmtHandle;
            if (this.resetPrepStmtHandle(false)) {
                this.connection.returnCachedPreparedStatementHandle(this.cachedPreparedStatementHandle);
            } else if (this.connection.isPreparedStatementUnprepareBatchingEnabled()) {
                SQLServerConnection sQLServerConnection = this.connection;
                Objects.requireNonNull(sQLServerConnection);
                this.connection.enqueueUnprepareStatementHandle(new SQLServerConnection.PreparedStatementHandle(sQLServerConnection, null, handleToClose, this.executedSqlDirectly, true));
            } else {
                block12: {
                    if (loggerExternal.isLoggable(Level.FINER)) {
                        loggerExternal.finer(this + ": Closing PreparedHandle:" + handleToClose);
                    }
                    try {
                        final class PreparedHandleClose
                        extends UninterruptableTDSCommand {
                            private static final long serialVersionUID = -8944096664249990764L;

                            PreparedHandleClose() {
                                super("closePreparedHandle");
                            }

                            @Override
                            final boolean doExecute() throws SQLServerException {
                                TDSWriter tdsWriter = this.startRequest((byte)3);
                                tdsWriter.writeShort((short)-1);
                                tdsWriter.writeShort(SQLServerPreparedStatement.this.executedSqlDirectly ? (short)15 : 6);
                                tdsWriter.writeByte((byte)0);
                                tdsWriter.writeByte((byte)0);
                                tdsWriter.sendEnclavePackage(null, null);
                                tdsWriter.writeRPCInt(null, handleToClose, false);
                                TDSParser.parse(this.startResponse(), this.getLogContext());
                                return true;
                            }
                        }
                        this.executeCommand(new PreparedHandleClose());
                    }
                    catch (SQLServerException e) {
                        if (!loggerExternal.isLoggable(Level.FINER)) break block12;
                        loggerExternal.log(Level.FINER, this + ": Error (ignored) closing PreparedHandle:" + handleToClose, e);
                    }
                }
                if (loggerExternal.isLoggable(Level.FINER)) {
                    loggerExternal.finer(this + ": Closed PreparedHandle:" + handleToClose);
                }
            }
            this.connection.unprepareUnreferencedPreparedStatementHandles(false);
        }
    }

    @Override
    final void closeInternal() {
        super.closeInternal();
        this.closePreparedHandle();
        try {
            if (null != this.internalStmt) {
                this.internalStmt.close();
            }
        }
        catch (SQLServerException e) {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.finer("Ignored error closing internal statement: " + e.getErrorCode() + " " + e.getMessage());
            }
        }
        finally {
            this.internalStmt = null;
        }
        this.batchParamValues = null;
    }

    final void initParams(int nParams) {
        this.inOutParam = new Parameter[nParams];
        for (int i = 0; i < nParams; ++i) {
            this.inOutParam[i] = new Parameter(Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection));
        }
    }

    @Override
    public final void clearParameters() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "clearParameters");
        this.checkClosed();
        this.encryptionMetadataIsRetrieved = false;
        this.cryptoMetaBatch.clear();
        if (this.inOutParam == null) {
            return;
        }
        for (int i = 0; i < this.inOutParam.length; ++i) {
            this.inOutParam[i].clearInputValue();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "clearParameters");
    }

    private boolean buildPreparedStrings(Parameter[] params, boolean renewDefinition) throws SQLServerException {
        String newTypeDefinitions = this.buildParamTypeDefinitions(params, renewDefinition);
        if (null != this.preparedTypeDefinitions && newTypeDefinitions.equalsIgnoreCase(this.preparedTypeDefinitions)) {
            return false;
        }
        this.preparedTypeDefinitions = newTypeDefinitions;
        this.preparedSQL = this.connection.replaceParameterMarkers(this.userSQL, this.userSQLParamPositions, params, this.bReturnValueSyntax);
        if (this.bRequestedGeneratedKeys) {
            this.preparedSQL = this.preparedSQL + " select SCOPE_IDENTITY() AS GENERATED_KEYS";
        }
        return true;
    }

    private String buildParamTypeDefinitions(Parameter[] params, boolean renewDefinition) throws SQLServerException {
        int nCols = params.length;
        if (nCols == 0) {
            return "";
        }
        int stringLen = nCols * 2;
        stringLen += nCols;
        stringLen += nCols - 1;
        stringLen = nCols > 10 ? (stringLen += 10 + (nCols - 10) * 2) : (stringLen += nCols);
        String[] typeDefinitions = new String[nCols];
        for (int i = 0; i < nCols; ++i) {
            Parameter param = params[i];
            param.renewDefinition = renewDefinition;
            String typeDefinition = param.getTypeDefinition(this.connection, this.resultsReader());
            if (null == typeDefinition) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueNotSetForParameter"));
                Object[] msgArgs = new Object[]{i + 1};
                SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
            }
            typeDefinitions[i] = typeDefinition;
            stringLen += typeDefinition.length();
            stringLen += param.isOutput() ? 7 : 0;
        }
        StringBuilder sb = new StringBuilder(stringLen);
        char[] cParamName = new char[10];
        this.parameterNames = new ArrayList(nCols);
        for (int i = 0; i < nCols; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            int l = SQLServerConnection.makeParamName(i, cParamName, 0, false);
            String parameterName = String.valueOf(cParamName, 0, l);
            sb.append(parameterName);
            sb.append(' ');
            this.parameterNames.add(parameterName);
            sb.append(typeDefinitions[i]);
            if (!params[i].isOutput()) continue;
            sb.append(" OUTPUT");
        }
        return sb.toString();
    }

    @Override
    public ResultSet executeQuery() throws SQLServerException, SQLTimeoutException {
        loggerExternal.entering(this.getClassNameLogging(), "executeQuery");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 1));
        loggerExternal.exiting(this.getClassNameLogging(), "executeQuery");
        return this.resultSet;
    }

    final ResultSet executeQueryInternal() throws SQLServerException, SQLTimeoutException {
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 5));
        return this.resultSet;
    }

    @Override
    public int executeUpdate() throws SQLServerException, SQLTimeoutException {
        loggerExternal.entering(this.getClassNameLogging(), "executeUpdate");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 2));
        if (this.updateCount < Integer.MIN_VALUE || this.updateCount > Integer.MAX_VALUE) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_updateCountOutofRange"), null, true);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "executeUpdate", this.updateCount);
        return (int)this.updateCount;
    }

    @Override
    public long executeLargeUpdate() throws SQLServerException, SQLTimeoutException {
        loggerExternal.entering(this.getClassNameLogging(), "executeLargeUpdate");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 2));
        loggerExternal.exiting(this.getClassNameLogging(), "executeLargeUpdate", this.updateCount);
        return this.updateCount;
    }

    @Override
    public boolean execute() throws SQLServerException, SQLTimeoutException {
        loggerExternal.entering(this.getClassNameLogging(), "execute");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 3));
        loggerExternal.exiting(this.getClassNameLogging(), "execute", null != this.resultSet);
        return null != this.resultSet;
    }

    final void doExecutePreparedStatement(PrepStmtExecCmd command) throws SQLServerException {
        this.resetForReexecute();
        this.setMaxRowsAndMaxFieldSize();
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        boolean hasExistingTypeDefinitions = this.preparedTypeDefinitions != null;
        boolean hasNewTypeDefinitions = true;
        boolean inRetry = false;
        if (!this.encryptionMetadataIsRetrieved) {
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, false);
        }
        if (this.connection.isAEv2() && !this.isInternalEncryptionQuery) {
            this.enclaveCEKs = this.connection.initEnclaveParameters(this, this.preparedSQL, this.preparedTypeDefinitions, this.inOutParam, this.parameterNames);
            this.encryptionMetadataIsRetrieved = true;
            this.setMaxRowsAndMaxFieldSize();
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, true);
        }
        if (Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection) && 0 < this.inOutParam.length && !this.isInternalEncryptionQuery) {
            if (!this.encryptionMetadataIsRetrieved) {
                this.getParameterEncryptionMetadata(this.inOutParam);
                this.encryptionMetadataIsRetrieved = true;
                this.setMaxRowsAndMaxFieldSize();
            }
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, true);
        }
        boolean needsPrepare = true;
        for (int attempt = 1; attempt <= 2; ++attempt) {
            try {
                if (this.reuseCachedHandle(hasNewTypeDefinitions, 1 < attempt)) {
                    hasNewTypeDefinitions = false;
                }
                TDSWriter tdsWriter = command.startRequest((byte)3);
                needsPrepare = this.doPrepExec(tdsWriter, this.inOutParam, hasNewTypeDefinitions, hasExistingTypeDefinitions, command);
                this.ensureExecuteResultsReader(command.startResponse(this.getIsResponseBufferingAdaptive()));
                this.startResults();
                this.getNextResult(true);
                break;
            }
            catch (SQLException e) {
                if (this.retryBasedOnFailedReuseOfCachedHandle(e, attempt, needsPrepare, false)) continue;
                if (!inRetry && this.connection.doesServerSupportEnclaveRetry()) {
                    ParameterMetaDataCache.removeCacheEntry(this.connection, this.preparedSQL);
                    inRetry = true;
                    this.doExecutePreparedStatement(command);
                    break;
                }
                throw e;
            }
        }
        if (1 == this.executeMethod && null == this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_noResultset"), null, true);
        } else if (2 == this.executeMethod && null != this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), null, false);
        }
    }

    private boolean retryBasedOnFailedReuseOfCachedHandle(SQLException e, int attempt, boolean needsPrepare, boolean isBatch) {
        if (needsPrepare && !isBatch) {
            return false;
        }
        return 1 == attempt && (586 == e.getErrorCode() || 8179 == e.getErrorCode()) && this.connection.isStatementPoolingEnabled();
    }

    @Override
    boolean consumeExecOutParam(TDSReader tdsReader) throws SQLServerException {
        if (this.expectPrepStmtHandle || this.expectCursorOutParams) {
            final class PrepStmtExecOutParamHandler
            extends SQLServerStatement.StmtExecOutParamHandler {
                PrepStmtExecOutParamHandler(SQLServerStatement statement) {
                    super(SQLServerPreparedStatement.this, statement);
                }

                @Override
                boolean onRetValue(TDSReader tdsReader) throws SQLServerException {
                    if (!SQLServerPreparedStatement.this.expectPrepStmtHandle) {
                        return super.onRetValue(tdsReader);
                    }
                    SQLServerPreparedStatement.this.expectPrepStmtHandle = false;
                    Parameter param = new Parameter(Util.shouldHonorAEForParameters(SQLServerPreparedStatement.this.stmtColumnEncriptionSetting, SQLServerPreparedStatement.this.connection));
                    param.skipRetValStatus(tdsReader);
                    SQLServerPreparedStatement.this.setPreparedStatementHandle(param.getInt(tdsReader, this.statement));
                    if (null == SQLServerPreparedStatement.this.cachedPreparedStatementHandle && !SQLServerPreparedStatement.this.isCursorable(SQLServerPreparedStatement.this.executeMethod)) {
                        SQLServerPreparedStatement.this.cachedPreparedStatementHandle = SQLServerPreparedStatement.this.connection.registerCachedPreparedStatementHandle(new SQLServerConnection.CityHash128Key(SQLServerPreparedStatement.this.preparedSQL, SQLServerPreparedStatement.this.preparedTypeDefinitions), SQLServerPreparedStatement.this.prepStmtHandle, SQLServerPreparedStatement.this.executedSqlDirectly);
                    }
                    param.skipValue(tdsReader, true);
                    if (SQLServerPreparedStatement.this.getStatementLogger().isLoggable(Level.FINER)) {
                        SQLServerPreparedStatement.this.getStatementLogger().finer(this.toString() + ": Setting PreparedHandle:" + SQLServerPreparedStatement.this.prepStmtHandle);
                    }
                    return true;
                }
            }
            TDSParser.parse(tdsReader, new PrepStmtExecOutParamHandler((SQLServerStatement)this));
            return true;
        }
        return false;
    }

    void sendParamsByRPC(TDSWriter tdsWriter, Parameter[] params) throws SQLServerException {
        for (int index = 0; index < params.length; ++index) {
            if (JDBCType.TVP == params[index].getJdbcType()) {
                char[] cParamName = new char[10];
                int paramNameLen = SQLServerConnection.makeParamName(index, cParamName, 0, false);
                tdsWriter.writeByte((byte)paramNameLen);
                tdsWriter.writeString(new String(cParamName, 0, paramNameLen));
            }
            params[index].sendByRPC(tdsWriter, this);
        }
    }

    private void buildServerCursorPrepExecParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorprepexec: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 7;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)5);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), true);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCInt(null, 0, true);
        tdsWriter.writeRPCStringUnicode(this.preparedTypeDefinitions.length() > 0 ? this.preparedTypeDefinitions : null);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
        tdsWriter.writeRPCInt(null, this.getResultSetScrollOpt() & ~(0 == this.preparedTypeDefinitions.length() ? 4096 : 0), false);
        tdsWriter.writeRPCInt(null, this.getResultSetCCOpt(), false);
        tdsWriter.writeRPCInt(null, 0, true);
    }

    private void buildPrepParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_prepare: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 4;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)11);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), true);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCStringUnicode(this.preparedTypeDefinitions.length() > 0 ? this.preparedTypeDefinitions : null);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
        tdsWriter.writeRPCInt(null, 1, false);
    }

    private void buildPrepExecParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_prepexec: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 3;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)13);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), true);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCStringUnicode(this.preparedTypeDefinitions.length() > 0 ? this.preparedTypeDefinitions : null);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
    }

    private void buildExecSQLParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_executesql: SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 2;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)10);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
        if (this.preparedTypeDefinitions.length() > 0) {
            tdsWriter.writeRPCStringUnicode(this.preparedTypeDefinitions);
        }
    }

    private void buildServerCursorExecParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorexecute: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 5;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)4);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        assert (this.hasPreparedStatementHandle());
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), false);
        tdsWriter.writeRPCInt(null, 0, true);
        tdsWriter.writeRPCInt(null, this.getResultSetScrollOpt() & 0xFFFFEFFF, false);
        tdsWriter.writeRPCInt(null, this.getResultSetCCOpt(), false);
        tdsWriter.writeRPCInt(null, 0, true);
    }

    private void buildExecParams(TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_execute: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 1;
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)12);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        assert (this.hasPreparedStatementHandle());
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), false);
    }

    private void getParameterEncryptionMetadata(Parameter[] params) throws SQLServerException {
        assert (this.connection != null) : "Connection should not be null";
        try (CallableStatement stmt = this.connection.prepareCall("exec sp_describe_parameter_encryption ?,?");){
            if (this.getStatementLogger().isLoggable(Level.FINE)) {
                this.getStatementLogger().fine("Calling stored procedure sp_describe_parameter_encryption to get parameter encryption information.");
            }
            if (this.hasColumnEncryptionKeyStoreProvidersRegistered()) {
                ((SQLServerCallableStatement)stmt).registerColumnEncryptionKeyStoreProvidersOnStatement(this.statementColumnEncryptionKeyStoreProviders);
            }
            ((SQLServerCallableStatement)stmt).isInternalEncryptionQuery = true;
            ((SQLServerCallableStatement)stmt).setNString(1, this.preparedSQL);
            ((SQLServerCallableStatement)stmt).setNString(2, this.preparedTypeDefinitions);
            try (ResultSet rs = ((SQLServerCallableStatement)stmt).executeQueryInternal();){
                if (null == rs) {
                    return;
                }
                HashMap<Integer, CekTableEntry> cekList = new HashMap<Integer, CekTableEntry>();
                CekTableEntry cekEntry = null;
                while (rs.next()) {
                    int currentOrdinal = rs.getInt(DescribeParameterEncryptionResultSet1.KEYORDINAL.value());
                    if (!cekList.containsKey(currentOrdinal)) {
                        cekEntry = new CekTableEntry(currentOrdinal);
                        cekList.put(cekEntry.ordinal, cekEntry);
                    } else {
                        cekEntry = (CekTableEntry)cekList.get(currentOrdinal);
                    }
                    cekEntry.add(rs.getBytes(DescribeParameterEncryptionResultSet1.ENCRYPTEDKEY.value()), rs.getInt(DescribeParameterEncryptionResultSet1.DBID.value()), rs.getInt(DescribeParameterEncryptionResultSet1.KEYID.value()), rs.getInt(DescribeParameterEncryptionResultSet1.KEYVERSION.value()), rs.getBytes(DescribeParameterEncryptionResultSet1.KEYMDVERSION.value()), rs.getString(DescribeParameterEncryptionResultSet1.KEYPATH.value()), rs.getString(DescribeParameterEncryptionResultSet1.PROVIDERNAME.value()), rs.getString(DescribeParameterEncryptionResultSet1.KEYENCRYPTIONALGORITHM.value()));
                }
                if (this.getStatementLogger().isLoggable(Level.FINE)) {
                    this.getStatementLogger().fine("Matadata of CEKs is retrieved.");
                }
                if (!stmt.getMoreResults()) {
                    throw new SQLServerException((Object)this, SQLServerException.getErrString("R_UnexpectedDescribeParamFormat"), null, 0, false);
                }
                int paramCount = 0;
                try (ResultSet secondRs = stmt.getResultSet();){
                    while (secondRs.next()) {
                        ++paramCount;
                        String paramName = secondRs.getString(DescribeParameterEncryptionResultSet2.PARAMETERNAME.value());
                        int paramIndex = this.parameterNames.indexOf(paramName);
                        int cekOrdinal = secondRs.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONKEYORDINAL.value());
                        cekEntry = (CekTableEntry)cekList.get(cekOrdinal);
                        if (null != cekEntry && cekList.size() < cekOrdinal) {
                            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionKeyOrdinal"));
                            Object[] msgArgs = new Object[]{cekOrdinal, cekEntry.getSize()};
                            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                        }
                        SQLServerEncryptionType encType = SQLServerEncryptionType.of((byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONTYPE.value()));
                        if (SQLServerEncryptionType.PLAINTEXT != encType) {
                            params[paramIndex].cryptoMeta = new CryptoMetadata(cekEntry, (short)cekOrdinal, (byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.COLUMNENCRYPTIONALGORITHM.value()), null, encType.value, (byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.NORMALIZATIONRULEVERSION.value()));
                            SQLServerStatement statement = (SQLServerStatement)((Object)stmt);
                            SQLServerSecurityUtility.decryptSymmetricKey(params[paramIndex].cryptoMeta, this.connection, statement);
                            continue;
                        }
                        if (!params[paramIndex].getForceEncryption()) continue;
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumn"));
                        Object[] msgArgs = new Object[]{this.userSQL, paramIndex + 1};
                        SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, true);
                    }
                    if (this.getStatementLogger().isLoggable(Level.FINE)) {
                        this.getStatementLogger().fine("Parameter encryption metadata is set.");
                    }
                }
                if (paramCount != params.length) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_MissingParamEncryptionMetadata"));
                    Object[] msgArgs = new Object[]{this.userSQL};
                    throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
                }
            }
        }
        catch (SQLException e) {
            if (e instanceof SQLServerException) {
                throw (SQLServerException)e;
            }
            throw new SQLServerException(SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), null, 0, (Throwable)e);
        }
        this.connection.resetCurrentCommand();
    }

    private boolean reuseCachedHandle(boolean hasNewTypeDefinitions, boolean discardCurrentCacheItem) {
        SQLServerConnection.PreparedStatementHandle cachedHandle;
        if (this.isCursorable(this.executeMethod)) {
            return false;
        }
        if (discardCurrentCacheItem || hasNewTypeDefinitions) {
            if (null != this.cachedPreparedStatementHandle && (discardCurrentCacheItem || this.hasPreparedStatementHandle() && this.prepStmtHandle == this.cachedPreparedStatementHandle.getHandle())) {
                this.cachedPreparedStatementHandle.removeReference();
            }
            this.resetPrepStmtHandle(discardCurrentCacheItem);
            this.cachedPreparedStatementHandle = null;
            if (discardCurrentCacheItem) {
                return false;
            }
        }
        if (null == this.cachedPreparedStatementHandle && null != (cachedHandle = this.connection.getCachedPreparedStatementHandle(new SQLServerConnection.CityHash128Key(this.preparedSQL, this.preparedTypeDefinitions))) && (!this.connection.isColumnEncryptionSettingEnabled() || this.connection.isColumnEncryptionSettingEnabled() && this.encryptionMetadataIsRetrieved) && cachedHandle.tryAddReference()) {
            this.setPreparedStatementHandle(cachedHandle.getHandle());
            this.cachedPreparedStatementHandle = cachedHandle;
            return true;
        }
        return false;
    }

    private boolean doPrepExec(TDSWriter tdsWriter, Parameter[] params, boolean hasNewTypeDefinitions, boolean hasExistingTypeDefinitions, TDSCommand command) throws SQLServerException {
        boolean needsPrepare = hasNewTypeDefinitions && hasExistingTypeDefinitions || !this.hasPreparedStatementHandle();
        boolean isPrepareMethodSpPrepExec = this.connection.getPrepareMethod().equals(PrepareMethod.PREPEXEC.toString());
        if (this.isCursorable(this.executeMethod)) {
            if (needsPrepare) {
                this.buildServerCursorPrepExecParams(tdsWriter);
            } else {
                this.buildServerCursorExecParams(tdsWriter);
            }
        } else if (needsPrepare && !this.connection.getEnablePrepareOnFirstPreparedStatementCall() && !this.isExecutedAtLeastOnce) {
            this.buildExecSQLParams(tdsWriter);
            this.isExecutedAtLeastOnce = true;
        } else if (needsPrepare) {
            if (isPrepareMethodSpPrepExec) {
                this.buildPrepExecParams(tdsWriter);
            } else {
                this.isSpPrepareExecuted = true;
                if (this.executeMethod == 4) {
                    this.buildPrepParams(tdsWriter);
                    return needsPrepare;
                }
                this.isSpPrepareExecuted = false;
                this.doPrep(tdsWriter, command);
                command.startRequest((byte)3);
                this.buildExecParams(tdsWriter);
            }
        } else {
            this.buildExecParams(tdsWriter);
        }
        this.sendParamsByRPC(tdsWriter, params);
        return needsPrepare;
    }

    private void doPrep(TDSWriter tdsWriter, TDSCommand command) throws SQLServerException {
        this.buildPrepParams(tdsWriter);
        this.ensureExecuteResultsReader(command.startResponse(this.getIsResponseBufferingAdaptive()));
        command.processResponse(this.resultsReader());
    }

    @Override
    public final ResultSetMetaData getMetaData() throws SQLServerException, SQLTimeoutException {
        loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        boolean rsclosed = false;
        ResultSetMetaData rsmd = null;
        try {
            if (this.resultSet != null) {
                this.resultSet.checkClosed();
            }
        }
        catch (SQLServerException e) {
            rsclosed = true;
        }
        if (this.resultSet == null || rsclosed) {
            SQLServerResultSet emptyResultSet = this.buildExecuteMetaData();
            if (null != emptyResultSet) {
                rsmd = emptyResultSet.getMetaData();
            }
        } else {
            rsmd = this.resultSet.getMetaData();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", rsmd);
        return rsmd;
    }

    private SQLServerResultSet buildExecuteMetaData() throws SQLServerException, SQLTimeoutException {
        SQLServerResultSet emptyResultSet;
        block2: {
            String fmtSQL = this.userSQL;
            emptyResultSet = null;
            try {
                fmtSQL = SQLServerPreparedStatement.replaceMarkerWithNull(fmtSQL);
                this.internalStmt = (SQLServerStatement)this.connection.createStatement();
                emptyResultSet = this.internalStmt.executeQueryInternal("set fmtonly on " + fmtSQL + "\nset fmtonly off");
            }
            catch (SQLServerException sqle) {
                if (sqle.getMessage().equals(SQLServerException.getErrString("R_noResultset"))) break block2;
                throw sqle;
            }
        }
        return emptyResultSet;
    }

    final Parameter setterGetParam(int index) throws SQLServerException {
        if (index < 1 || index > this.inOutParam.length) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            Object[] msgArgs = new Object[]{index};
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", false);
        }
        return this.inOutParam[index - 1];
    }

    final void setValue(int parameterIndex, JDBCType jdbcType, Object value, JavaType javaType, String tvpName) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
    }

    final void setValue(int parameterIndex, JDBCType jdbcType, Object value, JavaType javaType, boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, null, null, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }

    final void setValue(int parameterIndex, JDBCType jdbcType, Object value, JavaType javaType, Integer precision, Integer scale, boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, precision, scale, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }

    final void setValue(int parameterIndex, JDBCType jdbcType, Object value, JavaType javaType, Calendar cal, boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, cal, null, null, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }

    final void setStream(int parameterIndex, StreamType streamType, Object streamValue, JavaType javaType, long length) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(streamType.getJDBCType(), streamValue, javaType, new StreamSetterArgs(streamType, length), null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }

    final void setSQLXMLInternal(int parameterIndex, SQLXML value) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(JDBCType.SQLXML, value, JavaType.SQLXML, new StreamSetterArgs(StreamType.SQLXML, -1L), null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }

    @Override
    public final void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{parameterIndex, x});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setAsciiStream(int n, InputStream x, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{n, x, length});
        }
        this.checkClosed();
        this.setStream(n, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{parameterIndex, x, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    @Override
    public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterIndex, x});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, (Object)x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public final void setBigDecimal(int parameterIndex, BigDecimal x, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterIndex, x, precision, scale});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public final void setBigDecimal(int parameterIndex, BigDecimal x, int precision, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{parameterIndex, x, precision, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    @Override
    public final void setMoney(int n, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.MONEY, (Object)x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }

    @Override
    public final void setMoney(int n, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.MONEY, (Object)x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }

    @Override
    public final void setSmallMoney(int n, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLMONEY, (Object)x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }

    @Override
    public final void setSmallMoney(int n, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLMONEY, (Object)x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }

    @Override
    public final void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStreaml", new Object[]{parameterIndex, x});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBinaryStream(int n, InputStream x, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{n, x, length});
        }
        this.checkClosed();
        this.setStream(n, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{parameterIndex, x, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    @Override
    public final void setBoolean(int n, boolean x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIT, (Object)x, JavaType.BOOLEAN, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }

    @Override
    public final void setBoolean(int n, boolean x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIT, (Object)x, JavaType.BOOLEAN, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }

    @Override
    public final void setByte(int n, byte x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TINYINT, (Object)x, JavaType.BYTE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }

    @Override
    public final void setByte(int n, byte x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TINYINT, (Object)x, JavaType.BYTE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }

    @Override
    public final void setBytes(int n, byte[] x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BINARY, (Object)x, JavaType.BYTEARRAY, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }

    @Override
    public final void setBytes(int n, byte[] x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BINARY, (Object)x, JavaType.BYTEARRAY, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }

    @Override
    public final void setUniqueIdentifier(int index, String guid) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[]{index, guid});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.GUID, (Object)guid, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }

    @Override
    public final void setUniqueIdentifier(int index, String guid, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[]{index, guid, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.GUID, (Object)guid, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }

    @Override
    public final void setDouble(int n, double x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DOUBLE, (Object)x, JavaType.DOUBLE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }

    @Override
    public final void setDouble(int n, double x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DOUBLE, (Object)x, JavaType.DOUBLE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }

    @Override
    public final void setFloat(int n, float x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[]{n, Float.valueOf(x)});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.REAL, (Object)Float.valueOf(x), JavaType.FLOAT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }

    @Override
    public final void setFloat(int n, float x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[]{n, Float.valueOf(x), forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.REAL, (Object)Float.valueOf(x), JavaType.FLOAT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }

    @Override
    public final void setGeometry(int n, Geometry x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setGeometry", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.GEOMETRY, (Object)x, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setGeometry");
    }

    @Override
    public final void setGeography(int n, Geography x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setGeography", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.GEOGRAPHY, (Object)x, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setGeography");
    }

    @Override
    public final void setInt(int n, int value) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[]{n, value});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.INTEGER, (Object)value, JavaType.INTEGER, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }

    @Override
    public final void setInt(int n, int value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[]{n, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.INTEGER, (Object)value, JavaType.INTEGER, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }

    @Override
    public final void setLong(int n, long x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIGINT, (Object)x, JavaType.LONG, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }

    @Override
    public final void setLong(int n, long x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIGINT, (Object)x, JavaType.LONG, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }

    @Override
    public final void setNull(int index, int jdbcType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{index, jdbcType});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(index), null, JavaType.OBJECT, JDBCType.of(jdbcType), null, null, false, index, null);
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    final void setObjectNoType(int index, Object obj, boolean forceEncrypt) throws SQLServerException {
        Parameter param = this.setterGetParam(index);
        JDBCType targetJDBCType = param.getJdbcType();
        String tvpName = null;
        if (null == obj) {
            if (JDBCType.UNKNOWN == targetJDBCType) {
                targetJDBCType = JDBCType.CHAR;
            }
            this.setObject(param, null, JavaType.OBJECT, targetJDBCType, null, null, forceEncrypt, index, null);
        } else {
            JavaType javaType = JavaType.of(obj);
            if (JavaType.TVP == javaType && null == (tvpName = this.getTVPNameFromObject(index, obj)) && obj instanceof ResultSet) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPnotWorkWithSetObjectResultSet"), null);
            }
            if (JDBCType.UNKNOWN == (targetJDBCType = javaType.getJDBCType(SSType.UNKNOWN, targetJDBCType)) && obj instanceof UUID) {
                javaType = JavaType.STRING;
                targetJDBCType = JDBCType.GUID;
            }
            this.setObject(param, obj, javaType, targetJDBCType, null, null, forceEncrypt, index, tvpName);
        }
    }

    @Override
    public final void setObject(int index, Object obj) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{index, obj});
        }
        this.checkClosed();
        this.setObjectNoType(index, obj, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setObject(int n, Object obj, int jdbcType) throws SQLServerException {
        String tvpName = null;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{n, obj, jdbcType});
        }
        this.checkClosed();
        if (-153 == jdbcType) {
            tvpName = this.getTVPNameFromObject(n, obj);
        }
        this.setObject(this.setterGetParam(n), obj, JavaType.of(obj), JDBCType.of(jdbcType), null, null, false, n, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterIndex, x, targetSqlType, scaleOrLength});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), 2 == targetSqlType || 3 == targetSqlType || 93 == targetSqlType || 92 == targetSqlType || -155 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x) ? Integer.valueOf(scaleOrLength) : null, null, false, parameterIndex, null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setObject(int parameterIndex, Object x, int targetSqlType, Integer precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterIndex, x, targetSqlType, precision, scale});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), 2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x) ? Integer.valueOf(scale) : null, precision, false, parameterIndex, null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    @Override
    public final void setObject(int parameterIndex, Object x, int targetSqlType, Integer precision, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{parameterIndex, x, targetSqlType, precision, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), 2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x) ? Integer.valueOf(scale) : null, precision, forceEncrypt, parameterIndex, null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    final void setObject(Parameter param, Object obj, JavaType javaType, JDBCType jdbcType, Integer scale, Integer precision, boolean forceEncrypt, int parameterIndex, String tvpName) throws SQLServerException {
        assert (JDBCType.UNKNOWN != jdbcType);
        if (null != obj || JavaType.TVP == javaType) {
            JDBCType objectJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
            if (!objectJDBCType.convertsTo(jdbcType)) {
                DataTypes.throwConversionError(objectJDBCType.toString(), jdbcType.toString());
            }
            StreamSetterArgs streamSetterArgs = null;
            switch (javaType) {
                case READER: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.CHARACTER, -1L);
                    break;
                }
                case INPUTSTREAM: {
                    streamSetterArgs = new StreamSetterArgs(jdbcType.isTextual() ? StreamType.CHARACTER : StreamType.BINARY, -1L);
                    break;
                }
                case SQLXML: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.SQLXML, -1L);
                    break;
                }
            }
            param.setValue(jdbcType, obj, javaType, streamSetterArgs, null, precision, scale, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
        } else {
            assert (JavaType.OBJECT == javaType);
            if (jdbcType.isUnsupported()) {
                jdbcType = JDBCType.BINARY;
            }
            param.setValue(jdbcType, null, JavaType.OBJECT, null, null, precision, scale, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
        }
    }

    @Override
    public final void setObject(int index, Object obj, SQLType jdbcType) throws SQLServerException {
        this.setObject(index, obj, jdbcType.getVendorTypeNumber());
    }

    @Override
    public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), scaleOrLength);
    }

    @Override
    public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, Integer precision, Integer scale) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), precision, (int)scale);
    }

    @Override
    public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, Integer precision, Integer scale, boolean forceEncrypt) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), precision, (int)scale, forceEncrypt);
    }

    @Override
    public final void setShort(int index, short x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[]{index, x});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.SMALLINT, (Object)x, JavaType.SHORT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }

    @Override
    public final void setShort(int index, short x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.SMALLINT, (Object)x, JavaType.SHORT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }

    @Override
    public final void setString(int index, String str) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[]{index, str});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.VARCHAR, (Object)str, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }

    @Override
    public final void setString(int index, String str, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[]{index, str, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(index, JDBCType.VARCHAR, (Object)str, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }

    @Override
    public final void setNString(int parameterIndex, String value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[]{parameterIndex, value});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NVARCHAR, (Object)value, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }

    @Override
    public final void setNString(int parameterIndex, String value, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[]{parameterIndex, value, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NVARCHAR, (Object)value, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }

    @Override
    public final void setTime(int n, Time x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, (Object)x, JavaType.TIME, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public final void setTime(int n, Time x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{n, x, scale});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public final void setTime(int n, Time x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{n, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public final void setTimestamp(int n, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, (Object)x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public final void setTimestamp(int n, Timestamp x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{n, x, scale});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public final void setTimestamp(int n, Timestamp x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{n, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public final void setDateTimeOffset(int n, DateTimeOffset x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, (Object)x, JavaType.DATETIMEOFFSET, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public final void setDateTimeOffset(int n, DateTimeOffset x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{n, x, scale});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public final void setDateTimeOffset(int n, DateTimeOffset x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{n, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    @Override
    public final void setDate(int n, Date x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, (Object)x, JavaType.DATE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public final void setDateTime(int n, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIME, (Object)x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }

    @Override
    public final void setDateTime(int n, Timestamp x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIME, (Object)x, JavaType.TIMESTAMP, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }

    @Override
    public final void setSmallDateTime(int n, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[]{n, x});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLDATETIME, (Object)x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }

    @Override
    public final void setSmallDateTime(int n, Timestamp x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[]{n, x, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLDATETIME, (Object)x, JavaType.TIMESTAMP, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }

    @Override
    public final void setStructured(int n, String tvpName, SQLServerDataTable tvpDataTable) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{n, tvpName, tvpDataTable});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, (Object)tvpDataTable, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    @Override
    public final void setStructured(int n, String tvpName, ResultSet tvpResultSet) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{n, tvpName, tvpResultSet});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, (Object)tvpResultSet, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    @Override
    public final void setStructured(int n, String tvpName, ISQLServerDataRecord tvpBulkRecord) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[]{n, tvpName, tvpBulkRecord});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, (Object)tvpBulkRecord, JavaType.TVP, tvpName);
        loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }

    String getTVPNameFromObject(int n, Object obj) throws SQLServerException {
        String tvpName = null;
        if (obj instanceof SQLServerDataTable) {
            tvpName = ((SQLServerDataTable)obj).getTvpName();
        }
        return this.getTVPNameIfNull(n, tvpName);
    }

    String getTVPNameIfNull(int n, String tvpName) throws SQLServerException {
        if ((null == tvpName || 0 == ((String)tvpName).length()) && null != this.procedureName) {
            SQLServerParameterMetaData pmd = (SQLServerParameterMetaData)this.getParameterMetaData();
            pmd.isTVP = true;
            if (!pmd.procedureIsFound) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_StoredProcedureNotFound"));
                Object[] msgArgs = new Object[]{this.procedureName};
                SQLServerException.makeFromDriverError(this.connection, pmd, form.format(msgArgs), null, false);
            }
            try {
                String tvpNameWithoutSchema = pmd.getParameterTypeName(n);
                String tvpSchema = pmd.getTVPSchemaFromStoredProcedure(n);
                tvpName = null != tvpSchema ? "[" + tvpSchema + "].[" + tvpNameWithoutSchema + "]" : tvpNameWithoutSchema;
            }
            catch (SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), null, 0, (Throwable)e);
            }
        }
        return tvpName;
    }

    @Override
    @Deprecated
    public final void setUnicodeStream(int n, InputStream x, int length) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final void addBatch() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "addBatch");
        this.checkClosed();
        if (this.batchParamValues == null) {
            this.batchParamValues = new ArrayList();
        }
        int numParams = this.inOutParam.length;
        Parameter[] paramValues = new Parameter[numParams];
        for (int i = 0; i < numParams; ++i) {
            paramValues[i] = this.inOutParam[i].cloneForBatch();
        }
        this.batchParamValues.add(paramValues);
        loggerExternal.exiting(this.getClassNameLogging(), "addBatch");
    }

    @Override
    public final void clearBatch() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "clearBatch");
        this.checkClosed();
        this.batchParamValues = null;
        loggerExternal.exiting(this.getClassNameLogging(), "clearBatch");
    }

    /*
     * Exception decompiling
     */
    @Override
    public int[] executeBatch() throws SQLServerException, BatchUpdateException, SQLTimeoutException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [10[CATCHBLOCK], 2[TRYBLOCK], 3[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    @Override
    public long[] executeLargeBatch() throws SQLServerException, BatchUpdateException, SQLTimeoutException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [10[CATCHBLOCK], 3[TRYBLOCK], 2[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void checkValidColumns(TypeInfo ti) throws SQLServerException {
        int jdbctype = ti.getSSType().getJDBCType().getIntValue();
        switch (jdbctype) {
            case -155: 
            case -151: 
            case -150: 
            case -148: 
            case -146: 
            case 91: 
            case 92: {
                String typeName = ti.getSSTypeName();
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupportedDW"));
                throw new IllegalArgumentException(form.format(new Object[]{typeName}));
            }
            case -145: 
            case -16: 
            case -15: 
            case -9: 
            case -7: 
            case -6: 
            case -5: 
            case -4: 
            case -3: 
            case -2: 
            case -1: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 12: {
                String typeName = ti.getSSTypeName();
                if ("geometry".equalsIgnoreCase(typeName) || "geography".equalsIgnoreCase(typeName)) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    throw new IllegalArgumentException(form.format(new Object[]{typeName}));
                }
            }
            case -156: 
            case 93: 
            case 2013: 
            case 2014: {
                return;
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
        String unsupportedDataType = JDBCType.of(jdbctype).toString();
        throw new IllegalArgumentException(form.format(new Object[]{unsupportedDataType}));
    }

    private void checkAdditionalQuery() {
        while (this.checkAndRemoveCommentsAndSpace(true)) {
        }
        if (this.localUserSQL.length() > 0) {
            throw new IllegalArgumentException(SQLServerException.getErrString("R_multipleQueriesNotAllowed"));
        }
    }

    private String parseUserSQLForTableNameDW(boolean hasInsertBeenFound, boolean hasIntoBeenFound, boolean hasTableBeenFound, boolean isExpectingTableName) throws SQLServerException {
        while (this.checkAndRemoveCommentsAndSpace(false)) {
        }
        StringBuilder sb = new StringBuilder();
        if (hasTableBeenFound && !isExpectingTableName) {
            if (this.checkSQLLength(1) && ".".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                sb.append(".");
                this.localUserSQL = this.localUserSQL.substring(1);
                return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, true);
            }
            return "";
        }
        if (!hasInsertBeenFound && this.checkSQLLength(6) && "insert".equalsIgnoreCase(this.localUserSQL.substring(0, 6))) {
            this.localUserSQL = this.localUserSQL.substring(6);
            return this.parseUserSQLForTableNameDW(true, hasIntoBeenFound, hasTableBeenFound, isExpectingTableName);
        }
        if (!hasIntoBeenFound && this.checkSQLLength(6) && "into".equalsIgnoreCase(this.localUserSQL.substring(0, 4))) {
            if (Character.isWhitespace(this.localUserSQL.charAt(4)) || this.localUserSQL.charAt(4) == '/' && this.localUserSQL.charAt(5) == '*') {
                this.localUserSQL = this.localUserSQL.substring(4);
                return this.parseUserSQLForTableNameDW(hasInsertBeenFound, true, hasTableBeenFound, isExpectingTableName);
            }
            return this.parseUserSQLForTableNameDW(hasInsertBeenFound, true, hasTableBeenFound, isExpectingTableName);
        }
        if (this.checkSQLLength(1) && "[".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
            int tempint = this.localUserSQL.indexOf(93, 1);
            if (tempint < 0) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
                Object[] msgArgs = new Object[]{this.localUserSQL};
                throw new IllegalArgumentException(form.format(msgArgs));
            }
            while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == ']') {
                tempint = this.localUserSQL.indexOf(93, tempint + 2);
            }
            sb.append(this.localUserSQL.substring(0, tempint + 1));
            this.localUserSQL = this.localUserSQL.substring(tempint + 1);
            return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
        }
        if (this.checkSQLLength(1) && "\"".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
            int tempint = this.localUserSQL.indexOf(34, 1);
            if (tempint < 0) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
                Object[] msgArgs = new Object[]{this.localUserSQL};
                throw new IllegalArgumentException(form.format(msgArgs));
            }
            while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == '\"') {
                tempint = this.localUserSQL.indexOf(34, tempint + 2);
            }
            sb.append(this.localUserSQL.substring(0, tempint + 1));
            this.localUserSQL = this.localUserSQL.substring(tempint + 1);
            return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
        }
        while (this.localUserSQL.length() > 0) {
            if (this.localUserSQL.charAt(0) == '.' || Character.isWhitespace(this.localUserSQL.charAt(0)) || this.checkAndRemoveCommentsAndSpace(false)) {
                return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
            }
            if (this.localUserSQL.charAt(0) == ';') {
                throw new IllegalArgumentException(SQLServerException.getErrString("R_endOfQueryDetected"));
            }
            sb.append(this.localUserSQL.charAt(0));
            this.localUserSQL = this.localUserSQL.substring(1);
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
        Object[] msgArgs = new Object[]{this.localUserSQL};
        throw new IllegalArgumentException(form.format(msgArgs));
    }

    private ArrayList<String> parseUserSQLForColumnListDW() {
        while (this.checkAndRemoveCommentsAndSpace(false)) {
        }
        if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
            this.localUserSQL = this.localUserSQL.substring(1);
            return this.parseUserSQLForColumnListDWHelper(new ArrayList<String>());
        }
        return null;
    }

    private ArrayList<String> parseUserSQLForColumnListDWHelper(ArrayList<String> listOfColumns) {
        while (this.checkAndRemoveCommentsAndSpace(false)) {
        }
        StringBuilder sb = new StringBuilder();
        block1: while (this.localUserSQL.length() > 0) {
            String tempstr;
            MessageFormat form;
            int tempint;
            while (this.checkAndRemoveCommentsAndSpace(false)) {
            }
            if (this.checkSQLLength(1) && this.localUserSQL.charAt(0) == ')') {
                this.localUserSQL = this.localUserSQL.substring(1);
                return listOfColumns;
            }
            if (this.localUserSQL.charAt(0) == ',') {
                this.localUserSQL = this.localUserSQL.substring(1);
                while (this.checkAndRemoveCommentsAndSpace(false)) {
                }
            }
            if (this.localUserSQL.charAt(0) == '[') {
                tempint = this.localUserSQL.indexOf(93, 1);
                if (tempint < 0) {
                    form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
                    Object[] msgArgs = new Object[]{this.localUserSQL};
                    throw new IllegalArgumentException(form.format(msgArgs));
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == ']') {
                    this.localUserSQL = this.localUserSQL.substring(0, tempint) + this.localUserSQL.substring(tempint + 1);
                    tempint = this.localUserSQL.indexOf(93, tempint + 1);
                }
                tempstr = this.localUserSQL.substring(1, tempint);
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                listOfColumns.add(tempstr);
                continue;
            }
            if (this.localUserSQL.charAt(0) == '\"') {
                tempint = this.localUserSQL.indexOf(34, 1);
                if (tempint < 0) {
                    form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
                    Object[] msgArgs = new Object[]{this.localUserSQL};
                    throw new IllegalArgumentException(form.format(msgArgs));
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == '\"') {
                    this.localUserSQL = this.localUserSQL.substring(0, tempint) + this.localUserSQL.substring(tempint + 1);
                    tempint = this.localUserSQL.indexOf(34, tempint + 1);
                }
                tempstr = this.localUserSQL.substring(1, tempint);
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                listOfColumns.add(tempstr);
                continue;
            }
            while (this.localUserSQL.length() > 0) {
                if (this.checkAndRemoveCommentsAndSpace(false)) continue;
                if (this.localUserSQL.charAt(0) == ',') {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    listOfColumns.add(sb.toString());
                    sb.setLength(0);
                    continue block1;
                }
                if (this.localUserSQL.charAt(0) == ')') {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    listOfColumns.add(sb.toString());
                    return listOfColumns;
                }
                sb.append(this.localUserSQL.charAt(0));
                this.localUserSQL = this.localUserSQL.substring(1);
                this.localUserSQL = this.localUserSQL.trim();
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
        Object[] msgArgs = new Object[]{this.localUserSQL};
        throw new IllegalArgumentException(form.format(msgArgs));
    }

    private ArrayList<String> parseUserSQLForValueListDW(boolean hasValuesBeenFound) {
        if (this.checkAndRemoveCommentsAndSpace(false)) {
            // empty if block
        }
        if (!hasValuesBeenFound) {
            if (this.checkSQLLength(6) && "VALUES".equalsIgnoreCase(this.localUserSQL.substring(0, 6))) {
                this.localUserSQL = this.localUserSQL.substring(6);
                while (this.checkAndRemoveCommentsAndSpace(false)) {
                }
                if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    return this.parseUserSQLForValueListDWHelper(new ArrayList<String>());
                }
            }
        } else {
            while (this.checkAndRemoveCommentsAndSpace(false)) {
            }
            if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                this.localUserSQL = this.localUserSQL.substring(1);
                return this.parseUserSQLForValueListDWHelper(new ArrayList<String>());
            }
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
        Object[] msgArgs = new Object[]{this.localUserSQL};
        throw new IllegalArgumentException(form.format(msgArgs));
    }

    private ArrayList<String> parseUserSQLForValueListDWHelper(ArrayList<String> listOfValues) {
        while (this.checkAndRemoveCommentsAndSpace(false)) {
        }
        StringBuilder sb = new StringBuilder();
        while (this.localUserSQL.length() > 0) {
            if (this.checkAndRemoveCommentsAndSpace(false)) continue;
            if (this.localUserSQL.charAt(0) == ',' || this.localUserSQL.charAt(0) == ')') {
                if (this.localUserSQL.charAt(0) == ',') {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    if (!"?".equals(sb.toString())) {
                        throw new IllegalArgumentException(SQLServerException.getErrString("R_onlyFullParamAllowed"));
                    }
                    listOfValues.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
                this.localUserSQL = this.localUserSQL.substring(1);
                listOfValues.add(sb.toString());
                return listOfValues;
            }
            sb.append(this.localUserSQL.charAt(0));
            this.localUserSQL = this.localUserSQL.substring(1);
            this.localUserSQL = this.localUserSQL.trim();
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
        Object[] msgArgs = new Object[]{this.localUserSQL};
        throw new IllegalArgumentException(form.format(msgArgs));
    }

    private boolean checkAndRemoveCommentsAndSpace(boolean checkForSemicolon) {
        this.localUserSQL = this.localUserSQL.trim();
        while (checkForSemicolon && null != this.localUserSQL && this.localUserSQL.length() > 0 && this.localUserSQL.charAt(0) == ';') {
            this.localUserSQL = this.localUserSQL.substring(1);
        }
        if (null == this.localUserSQL || this.localUserSQL.length() < 2) {
            return false;
        }
        if ("/*".equalsIgnoreCase(this.localUserSQL.substring(0, 2))) {
            int temp = this.localUserSQL.indexOf("*/") + 2;
            if (temp <= 0) {
                this.localUserSQL = "";
                return false;
            }
            this.localUserSQL = this.localUserSQL.substring(temp);
            return true;
        }
        if ("--".equalsIgnoreCase(this.localUserSQL.substring(0, 2))) {
            int temp = this.localUserSQL.indexOf(10) + 1;
            if (temp <= 0) {
                this.localUserSQL = "";
                return false;
            }
            this.localUserSQL = this.localUserSQL.substring(temp);
            return true;
        }
        return false;
    }

    private boolean checkSQLLength(int length) {
        if (null == this.localUserSQL || this.localUserSQL.length() < length) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSQL"));
            Object[] msgArgs = new Object[]{this.localUserSQL};
            throw new IllegalArgumentException(form.format(msgArgs));
        }
        return true;
    }

    final void doExecutePreparedStatementBatch(PrepStmtBatchExecCmd batchCommand) throws SQLServerException {
        this.executeMethod = 4;
        batchCommand.batchException = null;
        int numBatches = this.batchParamValues.size();
        batchCommand.updateCounts = new long[numBatches];
        for (int i = 0; i < numBatches; ++i) {
            batchCommand.updateCounts[i] = -3L;
        }
        int numBatchesPrepared = 0;
        int numBatchesExecuted = 0;
        if (this.isSelect(this.userSQL)) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_selectNotPermittedinBatch"), null, true);
        }
        this.connection.setMaxRows(0);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        Parameter[] batchParam = new Parameter[this.inOutParam.length];
        TDSWriter tdsWriter = null;
        block5: while (numBatchesExecuted < numBatches) {
            Parameter[] paramValues = this.batchParamValues.get(numBatchesPrepared);
            assert (paramValues.length == batchParam.length);
            System.arraycopy(paramValues, 0, batchParam, 0, paramValues.length);
            boolean hasExistingTypeDefinitions = this.preparedTypeDefinitions != null;
            boolean hasNewTypeDefinitions = this.buildPreparedStrings(batchParam, false);
            if (0 == numBatchesExecuted && !this.isInternalEncryptionQuery && this.connection.isAEv2() && !this.encryptionMetadataIsRetrieved) {
                this.enclaveCEKs = this.connection.initEnclaveParameters(this, this.preparedSQL, this.preparedTypeDefinitions, batchParam, this.parameterNames);
                this.encryptionMetadataIsRetrieved = true;
                this.buildPreparedStrings(batchParam, true);
                for (Parameter aBatchParam : batchParam) {
                    this.cryptoMetaBatch.add(aBatchParam.cryptoMeta);
                }
            }
            if (0 == numBatchesExecuted && Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection) && 0 < batchParam.length && !this.isInternalEncryptionQuery && !this.encryptionMetadataIsRetrieved) {
                this.encryptionMetadataIsRetrieved = true;
                this.getParameterEncryptionMetadata(batchParam);
                this.buildPreparedStrings(batchParam, true);
                for (Parameter aBatchParam : batchParam) {
                    this.cryptoMetaBatch.add(aBatchParam.cryptoMeta);
                }
            } else {
                for (int i = 0; i < this.cryptoMetaBatch.size(); ++i) {
                    batchParam[i].cryptoMeta = this.cryptoMetaBatch.get(i);
                }
            }
            boolean needsPrepare = true;
            for (int attempt = 1; attempt <= 2; ++attempt) {
                try {
                    if (batchCommand.wasInterrupted()) {
                        this.ensureExecuteResultsReader(batchCommand.startResponse(this.getIsResponseBufferingAdaptive()));
                        this.startResults();
                        this.getNextResult(true);
                        return;
                    }
                    if (this.reuseCachedHandle(hasNewTypeDefinitions, 1 < attempt)) {
                        hasNewTypeDefinitions = false;
                    }
                    if (numBatchesExecuted < numBatchesPrepared) {
                        tdsWriter.writeByte((byte)-1);
                    } else {
                        this.resetForReexecute();
                        tdsWriter = batchCommand.startRequest((byte)3);
                    }
                    if (!(needsPrepare = this.doPrepExec(tdsWriter, batchParam, hasNewTypeDefinitions, hasExistingTypeDefinitions, batchCommand)) && ++numBatchesPrepared != numBatches) continue block5;
                    this.ensureExecuteResultsReader(batchCommand.startResponse(this.getIsResponseBufferingAdaptive()));
                    boolean retry = false;
                    while (numBatchesExecuted < numBatchesPrepared) {
                        block30: {
                            this.startResults();
                            try {
                                if (!this.getNextResult(true)) {
                                    return;
                                }
                                if (this.isSpPrepareExecuted && this.hasPreparedStatementHandle()) {
                                    this.isSpPrepareExecuted = false;
                                    this.resetForReexecute();
                                    tdsWriter = batchCommand.startRequest((byte)3);
                                    this.buildExecParams(tdsWriter);
                                    this.sendParamsByRPC(tdsWriter, batchParam);
                                    this.ensureExecuteResultsReader(batchCommand.startResponse(this.getIsResponseBufferingAdaptive()));
                                    this.startResults();
                                    if (!this.getNextResult(true)) {
                                        return;
                                    }
                                }
                                if (null == this.resultSet) break block30;
                                SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), null, false);
                            }
                            catch (SQLServerException e) {
                                String sqlState;
                                if (this.connection.isSessionUnAvailable() || this.connection.rolledBackTransaction()) {
                                    throw e;
                                }
                                if (this.retryBasedOnFailedReuseOfCachedHandle(e, attempt, needsPrepare, true)) {
                                    numBatchesPrepared = numBatchesExecuted;
                                    retry = true;
                                    break;
                                }
                                this.updateCount = -3L;
                                if (null == batchCommand.batchException) {
                                    batchCommand.batchException = e;
                                }
                                if (null == (sqlState = batchCommand.batchException.getSQLState()) || !sqlState.equals(SQLState.STATEMENT_CANCELED.getSQLStateCode())) break block30;
                                this.processBatch();
                                continue;
                            }
                        }
                        batchCommand.updateCounts[numBatchesExecuted] = -1L == this.updateCount ? -2L : this.updateCount;
                        this.processBatch();
                        ++numBatchesExecuted;
                    }
                    if (retry) continue;
                    assert (numBatchesExecuted == numBatchesPrepared);
                    continue block5;
                }
                catch (SQLException e) {
                    if (this.retryBasedOnFailedReuseOfCachedHandle(e, attempt, needsPrepare, true) && this.connection.isStatementPoolingEnabled()) {
                        numBatchesPrepared = numBatchesExecuted;
                        continue;
                    }
                    if (null != batchCommand.batchException) {
                        numBatchesExecuted = numBatchesPrepared;
                        ++attempt;
                        continue;
                    }
                    throw e;
                }
            }
        }
    }

    @Override
    public final void setUseFmtOnly(boolean useFmtOnly) throws SQLServerException {
        this.checkClosed();
        this.useFmtOnly = useFmtOnly;
    }

    @Override
    public final boolean getUseFmtOnly() throws SQLServerException {
        this.checkClosed();
        return this.useFmtOnly;
    }

    @Override
    public final void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{parameterIndex, reader});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setCharacterStream(int n, Reader reader, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{n, reader, length});
        }
        this.checkClosed();
        this.setStream(n, StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{parameterIndex, reader, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    @Override
    public final void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{parameterIndex, value});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, value, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    @Override
    public final void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{parameterIndex, value, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, value, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    @Override
    public final void setRef(int i, Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final void setBlob(int i, Blob x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{i, x});
        }
        this.checkClosed();
        this.setValue(i, JDBCType.BLOB, (Object)x, JavaType.BLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public final void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{parameterIndex, inputStream});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public final void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{parameterIndex, inputStream, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    @Override
    public final void setClob(int parameterIndex, Clob clobValue) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterIndex, clobValue});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.CLOB, (Object)clobValue, JavaType.CLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setClob(int parameterIndex, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterIndex, reader});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{parameterIndex, reader, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    @Override
    public final void setNClob(int parameterIndex, NClob value) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterIndex, value});
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NCLOB, (Object)value, JavaType.NCLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setNClob(int parameterIndex, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterIndex, reader});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{parameterIndex, reader, length});
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    @Override
    public final void setArray(int i, Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final void setDate(int n, Date x, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{n, x, cal});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, x, JavaType.DATE, cal, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public final void setDate(int n, Date x, Calendar cal, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{n, x, cal, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, x, JavaType.DATE, cal, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    @Override
    public final void setTime(int n, Time x, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{n, x, cal});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, cal, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public final void setTime(int n, Time x, Calendar cal, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{n, x, cal, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, cal, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    @Override
    public final void setTimestamp(int n, Timestamp x, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{n, x, cal});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, cal, false);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public final void setTimestamp(int n, Timestamp x, Calendar cal, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{n, x, cal, forceEncrypt});
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, cal, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    @Override
    public final void setNull(int paramIndex, int sqlType, String typeName) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{paramIndex, sqlType, typeName});
        }
        this.checkClosed();
        if (-153 == sqlType) {
            this.setObject(this.setterGetParam(paramIndex), null, JavaType.TVP, JDBCType.of(sqlType), null, null, false, paramIndex, typeName);
        } else {
            this.setObject(this.setterGetParam(paramIndex), null, JavaType.OBJECT, JDBCType.of(sqlType), null, null, false, paramIndex, typeName);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    @Override
    public final ParameterMetaData getParameterMetaData(boolean forceRefresh) throws SQLServerException {
        SQLServerParameterMetaData pmd = this.connection.getCachedParameterMetadata(this.sqlTextCacheKey);
        if (!forceRefresh && null != pmd) {
            return pmd;
        }
        loggerExternal.entering(this.getClassNameLogging(), "getParameterMetaData");
        this.checkClosed();
        pmd = new SQLServerParameterMetaData(this, this.userSQL);
        this.connection.registerCachedParameterMetadata(this.sqlTextCacheKey, pmd);
        loggerExternal.exiting(this.getClassNameLogging(), "getParameterMetaData", pmd);
        return pmd;
    }

    @Override
    public final ParameterMetaData getParameterMetaData() throws SQLServerException {
        return this.getParameterMetaData(false);
    }

    @Override
    public final void setURL(int parameterIndex, URL x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final void setRowId(int parameterIndex, RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }

    @Override
    public final void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSQLXML", new Object[]{parameterIndex, xmlObject});
        }
        this.checkClosed();
        this.setSQLXMLInternal(parameterIndex, xmlObject);
        loggerExternal.exiting(this.getClassNameLogging(), "setSQLXML");
    }

    @Override
    public final int executeUpdate(String sql) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeUpdate", sql);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] msgArgs = new Object[]{"executeUpdate()"};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    @Override
    public final boolean execute(String sql) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "execute", sql);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] msgArgs = new Object[]{"execute()"};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    @Override
    public final ResultSet executeQuery(String sql) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeQuery", sql);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] msgArgs = new Object[]{"executeQuery()"};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    @Override
    public void addBatch(String sql) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "addBatch", sql);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] msgArgs = new Object[]{"addBatch()"};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    private final class PrepStmtBatchExecCmd
    extends TDSCommand {
        private static final long serialVersionUID = 5225705304799552318L;
        private final SQLServerPreparedStatement stmt;
        SQLServerException batchException;
        long[] updateCounts;

        PrepStmtBatchExecCmd(SQLServerPreparedStatement stmt) {
            super(stmt.toString() + " executeBatch", SQLServerPreparedStatement.this.queryTimeout, SQLServerPreparedStatement.this.cancelQueryTimeoutSeconds);
            this.stmt = stmt;
        }

        @Override
        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatementBatch(this);
            return true;
        }

        @Override
        final void processResponse(TDSReader tdsReader) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(tdsReader);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }

    private final class PrepStmtExecCmd
    extends TDSCommand {
        private static final long serialVersionUID = 4098801171124750861L;
        private final SQLServerPreparedStatement stmt;

        PrepStmtExecCmd(SQLServerPreparedStatement stmt, int executeMethod) {
            super(stmt.toString() + " executeXXX", SQLServerPreparedStatement.this.queryTimeout, SQLServerPreparedStatement.this.cancelQueryTimeoutSeconds);
            this.stmt = stmt;
            stmt.executeMethod = executeMethod;
        }

        @Override
        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatement(this);
            return false;
        }

        @Override
        final void processResponse(TDSReader tdsReader) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(tdsReader);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }
}

