/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ActivityCorrelator;
import com.microsoft.sqlserver.jdbc.CekTable;
import com.microsoft.sqlserver.jdbc.Column;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.microsoft.sqlserver.jdbc.ISQLServerResultSet;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.RowType;
import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerLob;
import com.microsoft.sqlserver.jdbc.SQLServerResultSetMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.ScrollWindow;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamColInfo;
import com.microsoft.sqlserver.jdbc.StreamColumns;
import com.microsoft.sqlserver.jdbc.StreamDone;
import com.microsoft.sqlserver.jdbc.StreamRetStatus;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.StreamTabName;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import com.microsoft.sqlserver.jdbc.TDSTokenHandler;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.UninterruptableTDSCommand;
import com.microsoft.sqlserver.jdbc.Util;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.sql.DateTimeOffset;

public class SQLServerResultSet
implements ISQLServerResultSet,
Serializable {
    private static final long serialVersionUID = -1624082547992040463L;
    private static final String SQLSTATE_INVALID_DESCRIPTOR_INDEX = "07009";
    private static final AtomicInteger lastResultSetID = new AtomicInteger(0);
    private static final String ACTIVITY_ID = " ActivityId: ";
    private final String traceID;
    static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerResultSet");
    static final Logger loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.ResultSet");
    private final String loggingClassName;
    private final SQLServerStatement stmt;
    private final int maxRows;
    private SQLServerResultSetMetaData metaData;
    private boolean isClosed = false;
    private final int serverCursorId;
    private int fetchDirection;
    private int fetchSize;
    private boolean isOnInsertRow = false;
    private boolean lastValueWasNull = false;
    private int lastColumnIndex;
    private boolean areNullCompressedColumnsInitialized = false;
    private RowType resultSetCurrentRowType = RowType.UNKNOWN;
    private transient Closeable activeStream;
    private SQLServerLob activeLOB;
    private final ScrollWindow scrollWindow;
    private static final int BEFORE_FIRST_ROW = 0;
    private static final int AFTER_LAST_ROW = -1;
    private static final int UNKNOWN_ROW = -2;
    private int currentRow = 0;
    private boolean updatedCurrentRow = false;
    private final Map<String, Integer> columnNames = new HashMap<String, Integer>();
    private boolean deletedCurrentRow = false;
    static final int UNKNOWN_ROW_COUNT = -3;
    private int rowCount;
    private final transient Column[] columns;
    private CekTable cekTable = null;
    private TDSReader tdsReader;
    private final transient FetchBuffer fetchBuffer;
    private SQLServerException rowErrorException = null;
    private int numFetchedRows;

    private static int nextResultSetID() {
        return lastResultSetID.incrementAndGet();
    }

    public String toString() {
        return this.traceID;
    }

    String logCursorState() {
        return " currentRow:" + this.currentRow + " numFetchedRows:" + this.numFetchedRows + " rowCount:" + this.rowCount;
    }

    String getClassNameLogging() {
        return this.loggingClassName;
    }

    protected int getServerCursorId() {
        return this.serverCursorId;
    }

    final RowType getCurrentRowType() {
        return this.resultSetCurrentRowType;
    }

    final void setCurrentRowType(RowType rowType) {
        this.resultSetCurrentRowType = rowType;
    }

    final boolean getUpdatedCurrentRow() {
        return this.updatedCurrentRow;
    }

    final void setUpdatedCurrentRow(boolean rowUpdated) {
        this.updatedCurrentRow = rowUpdated;
    }

    final boolean getDeletedCurrentRow() {
        return this.deletedCurrentRow;
    }

    final void setDeletedCurrentRow(boolean rowDeleted) {
        this.deletedCurrentRow = rowDeleted;
    }

    CekTable getCekTable() {
        return this.cekTable;
    }

    final void setColumnName(int index, String name) {
        this.columns[index - 1].setColumnName(name);
    }

    private void skipColumns(int columnsToSkip, boolean discardValues) throws SQLServerException {
        assert (this.lastColumnIndex >= 1);
        assert (0 <= columnsToSkip && columnsToSkip <= this.columns.length);
        for (int columnsSkipped = 0; columnsSkipped < columnsToSkip; ++columnsSkipped) {
            Column column = this.getColumn(this.lastColumnIndex++);
            column.skipValue(this.tdsReader, discardValues && this.isForwardOnly());
            if (!discardValues) continue;
            column.clear();
        }
    }

    TDSReader getTDSReader() {
        return this.tdsReader;
    }

    @Override
    public SensitivityClassification getSensitivityClassification() {
        return this.tdsReader.sensitivityClassification;
    }

    SQLServerResultSet(SQLServerStatement stmtIn) throws SQLServerException {
        int resultSetID = SQLServerResultSet.nextResultSetID();
        this.loggingClassName = "com.microsoft.sqlserver.jdbc.SQLServerResultSet:" + resultSetID;
        this.traceID = "SQLServerResultSet:" + resultSetID;
        this.stmt = stmtIn;
        this.maxRows = stmtIn.maxRows;
        this.fetchSize = stmtIn.nFetchSize;
        this.fetchDirection = stmtIn.nFetchDirection;
        abstract class CursorInitializer
        extends TDSTokenHandler {
            private StreamColumns columnMetaData;
            private StreamColInfo colInfo;
            private StreamTabName tabName;

            abstract int getRowCount();

            abstract int getServerCursorId();

            final Column[] buildColumns() throws SQLServerException {
                return this.columnMetaData.buildColumns(this.colInfo, this.tabName);
            }

            CursorInitializer(String name) {
                super(name);
                this.columnMetaData = null;
                this.colInfo = null;
                this.tabName = null;
            }

            @Override
            boolean onColInfo(TDSReader tdsReader) throws SQLServerException {
                this.colInfo = new StreamColInfo();
                this.colInfo.setFromTDS(tdsReader);
                return true;
            }

            @Override
            boolean onTabName(TDSReader tdsReader) throws SQLServerException {
                this.tabName = new StreamTabName();
                this.tabName.setFromTDS(tdsReader);
                return true;
            }

            @Override
            boolean onColMetaData(TDSReader tdsReader) throws SQLServerException {
                this.columnMetaData = new StreamColumns(Util.shouldHonorAEForRead(SQLServerResultSet.this.stmt.stmtColumnEncriptionSetting, SQLServerResultSet.this.stmt.connection));
                this.columnMetaData.setFromTDS(tdsReader);
                SQLServerResultSet.this.cekTable = this.columnMetaData.getCekTable();
                return true;
            }
        }
        final class ClientCursorInitializer
        extends CursorInitializer {
            private int rowCount;

            @Override
            final int getRowCount() {
                return this.rowCount;
            }

            @Override
            final int getServerCursorId() {
                return 0;
            }

            ClientCursorInitializer() {
                super("ClientCursorInitializer");
                this.rowCount = -3;
            }

            @Override
            boolean onRow(TDSReader tdsReader) throws SQLServerException {
                return false;
            }

            @Override
            boolean onNBCRow(TDSReader tdsReader) throws SQLServerException {
                return false;
            }

            @Override
            boolean onError(TDSReader tdsReader) throws SQLServerException {
                this.rowCount = 0;
                return false;
            }

            @Override
            boolean onDone(TDSReader tdsReader) throws SQLServerException {
                this.rowCount = 0;
                short status = tdsReader.peekStatusFlag();
                if ((status & 2) != 0 || (status & 0x100) != 0) {
                    StreamDone doneToken = new StreamDone();
                    doneToken.setFromTDS(tdsReader);
                    if (doneToken.isFinal()) {
                        SQLServerResultSet.this.stmt.connection.getSessionRecovery().decrementUnprocessedResponseCount();
                    }
                    SQLServerError databaseError = this.getDatabaseError();
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverError"));
                    Object[] msgArgs = new Object[]{status, databaseError != null ? databaseError.getErrorMessage() : ""};
                    if (null != databaseError) {
                        SQLServerException.makeFromDatabaseError(SQLServerResultSet.this.stmt.connection, null, form.format(msgArgs), databaseError, false);
                    } else {
                        SQLServerException.makeFromDriverError(SQLServerResultSet.this.stmt.connection, SQLServerResultSet.this.stmt, form.format(msgArgs), null, false);
                    }
                }
                return false;
            }
        }
        final class ServerCursorInitializer
        extends CursorInitializer {
            private final SQLServerStatement stmt;

            @Override
            final int getRowCount() {
                return this.stmt.getServerCursorRowCount();
            }

            @Override
            final int getServerCursorId() {
                return this.stmt.getServerCursorId();
            }

            ServerCursorInitializer(SQLServerStatement stmt) {
                super("ServerCursorInitializer");
                this.stmt = stmt;
            }

            @Override
            boolean onRetStatus(TDSReader tdsReader) throws SQLServerException {
                this.stmt.consumeExecOutParam(tdsReader);
                return true;
            }

            @Override
            boolean onRetValue(TDSReader tdsReader) throws SQLServerException {
                return false;
            }
        }
        CursorInitializer initializer = stmtIn.executedSqlDirectly ? new ClientCursorInitializer() : new ServerCursorInitializer(stmtIn);
        TDSParser.parse(stmtIn.resultsReader(), initializer);
        this.columns = initializer.buildColumns();
        this.rowCount = initializer.getRowCount();
        this.serverCursorId = initializer.getServerCursorId();
        this.tdsReader = 0 == this.serverCursorId ? stmtIn.resultsReader() : null;
        this.fetchBuffer = new FetchBuffer();
        this.scrollWindow = this.isForwardOnly() ? null : new ScrollWindow(this.fetchSize);
        this.numFetchedRows = 0;
        stmtIn.incrResultSetCount();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.toString() + " created by (" + this.stmt.toString() + ")");
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isWrapperFor");
        boolean f = iface.isInstance(this);
        loggerExternal.exiting(this.getClassNameLogging(), "isWrapperFor", f);
        return f;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        loggerExternal.entering(this.getClassNameLogging(), "unwrap");
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "unwrap", t);
        return t;
    }

    void checkClosed() throws SQLServerException {
        if (this.isClosed) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_resultsetClosed"), null, false);
        }
        this.stmt.checkClosed();
        if (null != this.rowErrorException) {
            throw this.rowErrorException;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isClosed");
        boolean result = this.isClosed || this.stmt.isClosed();
        loggerExternal.exiting(this.getClassNameLogging(), "isClosed", result);
        return result;
    }

    private void throwNotScrollable() throws SQLException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_requestedOpNotSupportedOnForward"), null, true);
    }

    protected boolean isForwardOnly() {
        return 2003 == this.stmt.getSQLResultSetType() || 2004 == this.stmt.getSQLResultSetType();
    }

    private boolean isDynamic() {
        return 0 != this.serverCursorId && 2 == this.stmt.getCursorType();
    }

    private void verifyResultSetIsScrollable() throws SQLException {
        if (this.isForwardOnly()) {
            this.throwNotScrollable();
        }
    }

    private void throwNotUpdatable() throws SQLServerException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_resultsetNotUpdatable"), null, true);
    }

    private void verifyResultSetIsUpdatable() throws SQLServerException {
        if (1007 == this.stmt.resultSetConcurrency || 0 == this.serverCursorId) {
            this.throwNotUpdatable();
        }
    }

    private boolean hasCurrentRow() {
        return 0 != this.currentRow && -1 != this.currentRow;
    }

    private void verifyResultSetHasCurrentRow() throws SQLServerException {
        if (!this.hasCurrentRow()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_resultsetNoCurrentRow"), null, true);
        }
    }

    private void verifyCurrentRowIsNotDeleted(String errResource) throws SQLServerException {
        if (this.currentRowDeleted()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString(errResource), null, true);
        }
    }

    private void verifyValidColumnIndex(int index) throws SQLServerException {
        int nCols = this.columns.length;
        if (0 != this.serverCursorId) {
            --nCols;
        }
        if (index < 1 || index > nCols) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            Object[] msgArgs = new Object[]{index};
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), SQLSTATE_INVALID_DESCRIPTOR_INDEX, false);
        }
    }

    private void verifyResultSetIsNotOnInsertRow() throws SQLServerException {
        if (this.isOnInsertRow) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_mustNotBeOnInsertRow"), null, true);
        }
    }

    private void throwUnsupportedCursorOp() throws SQLServerException {
        SQLServerException.makeFromDriverError(this.stmt.connection, this, SQLServerException.getErrString("R_unsupportedCursorOperation"), null, true);
    }

    private void closeInternal() {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        this.discardFetchBuffer();
        this.closeServerCursor();
        this.metaData = null;
        this.stmt.decrResultSetCount();
    }

    @Override
    public void close() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "close");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.closeInternal();
        loggerExternal.exiting(this.getClassNameLogging(), "close");
    }

    @Override
    public int findColumn(String userProvidedColumnName) throws SQLServerException {
        int i;
        loggerExternal.entering(this.getClassNameLogging(), "findColumn", userProvidedColumnName);
        this.checkClosed();
        Integer value = this.columnNames.get(userProvidedColumnName);
        if (null != value) {
            return value;
        }
        for (i = 0; i < this.columns.length; ++i) {
            if (!this.columns[i].getColumnName().equals(userProvidedColumnName)) continue;
            this.columnNames.put(userProvidedColumnName, i + 1);
            loggerExternal.exiting(this.getClassNameLogging(), "findColumn", i + 1);
            return i + 1;
        }
        for (i = 0; i < this.columns.length; ++i) {
            if (!this.columns[i].getColumnName().equalsIgnoreCase(userProvidedColumnName)) continue;
            this.columnNames.put(userProvidedColumnName, i + 1);
            loggerExternal.exiting(this.getClassNameLogging(), "findColumn", i + 1);
            return i + 1;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumnName"));
        Object[] msgArgs = new Object[]{userProvidedColumnName};
        SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), SQLSTATE_INVALID_DESCRIPTOR_INDEX, false);
        return 0;
    }

    final int getColumnCount() {
        int nCols = this.columns.length;
        if (0 != this.serverCursorId) {
            --nCols;
        }
        return nCols;
    }

    final Column getColumn(int columnIndex) throws SQLServerException {
        if (null != this.activeStream) {
            try {
                this.fillLOBs();
                this.activeStream.close();
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
            }
            finally {
                this.activeStream = null;
            }
        }
        return this.columns[columnIndex - 1];
    }

    private void initializeNullCompressedColumns() throws SQLServerException {
        if (this.resultSetCurrentRowType.equals((Object)RowType.NBCROW) && !this.areNullCompressedColumnsInitialized) {
            int columnNo = 0;
            int noOfBytes = (this.columns.length - 1 >> 3) + 1;
            for (int byteNo = 0; byteNo < noOfBytes; ++byteNo) {
                int byteValue = this.tdsReader.readUnsignedByte();
                if (byteValue == 0) {
                    columnNo += 8;
                    continue;
                }
                for (int bitNo = 0; bitNo < 8 && columnNo < this.columns.length; ++bitNo, ++columnNo) {
                    if ((byteValue & 1 << bitNo) == 0) continue;
                    this.columns[columnNo].initFromCompressedNull();
                }
            }
            this.areNullCompressedColumnsInitialized = true;
        }
    }

    private Column loadColumn(int index) throws SQLServerException {
        assert (1 <= index && index <= this.columns.length);
        this.initializeNullCompressedColumns();
        if (index > this.lastColumnIndex && !this.columns[index - 1].isInitialized()) {
            this.skipColumns(index - this.lastColumnIndex, false);
        }
        return this.getColumn(index);
    }

    @Override
    public void clearWarnings() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "clearWarnings");
        loggerExternal.exiting(this.getClassNameLogging(), "clearWarnings");
    }

    private void moverInit() {
        this.fillLOBs();
        this.cancelInsert();
        this.cancelUpdates();
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "relative", rows);
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " rows:" + rows + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.verifyResultSetHasCurrentRow();
        this.moverInit();
        this.moveRelative(rows);
        boolean value = this.hasCurrentRow();
        loggerExternal.exiting(this.getClassNameLogging(), "relative", value);
        return value;
    }

    private void moveRelative(int rowsToMove) throws SQLServerException {
        assert (this.hasCurrentRow());
        if (0 == rowsToMove) {
            return;
        }
        if (rowsToMove > 0) {
            this.moveForward(rowsToMove);
        } else {
            this.moveBackward(rowsToMove);
        }
    }

    private void moveForward(int rowsToMove) throws SQLServerException {
        assert (this.hasCurrentRow());
        assert (rowsToMove > 0);
        if (this.scrollWindow.getRow() + rowsToMove <= this.scrollWindow.getMaxRows()) {
            int rowsMoved = 0;
            while (rowsToMove > 0 && this.scrollWindow.next(this)) {
                ++rowsMoved;
                --rowsToMove;
            }
            this.updateCurrentRow(rowsMoved);
            if (0 == rowsToMove) {
                return;
            }
        }
        assert (rowsToMove > 0);
        if (0 == this.serverCursorId) {
            assert (-2 != this.currentRow);
            this.currentRow = this.clientMoveAbsolute(this.currentRow + rowsToMove);
            return;
        }
        if (1 == rowsToMove) {
            this.doServerFetch(2, 0, this.fetchSize);
        } else {
            this.doServerFetch(32, rowsToMove + this.scrollWindow.getRow() - 1, this.fetchSize);
        }
        if (!this.scrollWindow.next(this)) {
            this.currentRow = -1;
            return;
        }
        this.updateCurrentRow(rowsToMove);
    }

    private void moveBackward(int rowsToMove) throws SQLServerException {
        assert (this.hasCurrentRow());
        assert (rowsToMove < 0);
        if (this.scrollWindow.getRow() + rowsToMove >= 1) {
            for (int rowsMoved = 0; rowsMoved > rowsToMove; --rowsMoved) {
                this.scrollWindow.previous(this);
            }
            this.updateCurrentRow(rowsToMove);
            return;
        }
        if (0 == this.serverCursorId) {
            assert (-2 != this.currentRow);
            if (this.currentRow + rowsToMove < 1) {
                this.moveBeforeFirst();
            } else {
                this.currentRow = this.clientMoveAbsolute(this.currentRow + rowsToMove);
            }
            return;
        }
        if (-1 == rowsToMove) {
            this.doServerFetch(512, 0, this.fetchSize);
            if (!this.scrollWindow.next(this)) {
                this.currentRow = 0;
                return;
            }
            while (this.scrollWindow.next(this)) {
            }
            this.scrollWindow.previous(this);
        } else {
            this.doServerFetch(32, rowsToMove + this.scrollWindow.getRow() - 1, this.fetchSize);
            if (!this.scrollWindow.next(this)) {
                this.currentRow = 0;
                return;
            }
        }
        this.updateCurrentRow(rowsToMove);
    }

    private void updateCurrentRow(int rowsToMove) {
        if (-2 != this.currentRow) {
            assert (this.currentRow >= 1);
            this.currentRow += rowsToMove;
            assert (this.currentRow >= 1);
        }
    }

    @Override
    public boolean next() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "next");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.moverInit();
        if (-1 == this.currentRow) {
            loggerExternal.exiting(this.getClassNameLogging(), "next", false);
            return false;
        }
        if (!this.isForwardOnly()) {
            if (0 == this.currentRow) {
                this.moveFirst();
            } else {
                this.moveForward(1);
            }
            boolean value = this.hasCurrentRow();
            loggerExternal.exiting(this.getClassNameLogging(), "next", value);
            return value;
        }
        if (0 != this.serverCursorId && this.maxRows > 0 && this.currentRow == this.maxRows) {
            this.currentRow = -1;
            loggerExternal.exiting(this.getClassNameLogging(), "next", false);
            return false;
        }
        if (this.fetchBufferNext()) {
            if (0 == this.currentRow) {
                this.currentRow = 1;
            } else {
                this.updateCurrentRow(1);
            }
            assert (0 == this.maxRows || this.currentRow <= this.maxRows);
            loggerExternal.exiting(this.getClassNameLogging(), "next", true);
            return true;
        }
        if (0 != this.serverCursorId) {
            this.doServerFetch(2, 0, this.fetchSize);
            if (this.fetchBufferNext()) {
                if (0 == this.currentRow) {
                    this.currentRow = 1;
                } else {
                    this.updateCurrentRow(1);
                }
                assert (0 == this.maxRows || this.currentRow <= this.maxRows);
                loggerExternal.exiting(this.getClassNameLogging(), "next", true);
                return true;
            }
        }
        if (-3 == this.rowCount) {
            this.rowCount = this.currentRow;
        }
        if (this.stmt.resultsReader().peekTokenType() == 171) {
            this.stmt.startResults();
            this.stmt.getNextResult(false);
        }
        this.currentRow = -1;
        loggerExternal.exiting(this.getClassNameLogging(), "next", false);
        return false;
    }

    @Override
    public boolean wasNull() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "wasNull");
        this.checkClosed();
        loggerExternal.exiting(this.getClassNameLogging(), "wasNull", this.lastValueWasNull);
        return this.lastValueWasNull;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isBeforeFirst");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (0 != this.serverCursorId) {
            switch (this.stmt.getCursorType()) {
                case 4: {
                    this.throwNotScrollable();
                    break;
                }
                case 2: {
                    this.throwUnsupportedCursorOp();
                    break;
                }
                case 16: {
                    this.throwNotScrollable();
                    break;
                }
            }
        }
        if (this.isOnInsertRow) {
            return false;
        }
        if (0 != this.currentRow) {
            return false;
        }
        if (0 == this.serverCursorId) {
            return this.fetchBufferHasRows();
        }
        assert (this.rowCount >= 0);
        boolean value = this.rowCount > 0;
        loggerExternal.exiting(this.getClassNameLogging(), "isBeforeFirst", value);
        return value;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isAfterLast");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (0 != this.serverCursorId) {
            this.verifyResultSetIsScrollable();
            if (2 == this.stmt.getCursorType() && !this.isForwardOnly()) {
                this.throwUnsupportedCursorOp();
            }
        }
        if (this.isOnInsertRow) {
            return false;
        }
        assert (-1 != this.currentRow || -3 != this.rowCount);
        boolean value = -1 == this.currentRow && this.rowCount > 0;
        loggerExternal.exiting(this.getClassNameLogging(), "isAfterLast", value);
        return value;
    }

    @Override
    public boolean isFirst() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isFirst");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        if (this.isOnInsertRow) {
            return false;
        }
        assert (-2 != this.currentRow);
        boolean value = 1 == this.currentRow;
        loggerExternal.exiting(this.getClassNameLogging(), "isFirst", value);
        return value;
    }

    @Override
    public boolean isLast() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isLast");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        if (this.isOnInsertRow) {
            return false;
        }
        if (!this.hasCurrentRow()) {
            return false;
        }
        assert (this.currentRow >= 1);
        if (-3 != this.rowCount) {
            assert (this.currentRow <= this.rowCount);
            return this.currentRow == this.rowCount;
        }
        assert (0 == this.serverCursorId);
        boolean isLast = !this.next();
        this.previous();
        loggerExternal.exiting(this.getClassNameLogging(), "isLast", isLast);
        return isLast;
    }

    @Override
    public void beforeFirst() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "beforeFirst");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveBeforeFirst();
        loggerExternal.exiting(this.getClassNameLogging(), "beforeFirst");
    }

    private void moveBeforeFirst() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.fetchBufferBeforeFirst();
            this.scrollWindow.clear();
        } else {
            this.doServerFetch(1, 0, 0);
        }
        this.currentRow = 0;
    }

    @Override
    public void afterLast() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "afterLast");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveAfterLast();
        loggerExternal.exiting(this.getClassNameLogging(), "afterLast");
    }

    private void moveAfterLast() throws SQLServerException {
        assert (!this.isForwardOnly());
        if (0 == this.serverCursorId) {
            this.clientMoveAfterLast();
        } else {
            this.doServerFetch(8, 0, 0);
        }
        this.currentRow = -1;
    }

    @Override
    public boolean first() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "first");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveFirst();
        boolean value = this.hasCurrentRow();
        loggerExternal.exiting(this.getClassNameLogging(), "first", value);
        return value;
    }

    private void moveFirst() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.moveBeforeFirst();
        } else {
            this.doServerFetch(1, 0, this.fetchSize);
        }
        if (!this.scrollWindow.next(this)) {
            this.currentRow = -1;
            return;
        }
        this.currentRow = this.isDynamic() ? -2 : 1;
    }

    @Override
    public boolean last() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "last");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        this.moveLast();
        boolean value = this.hasCurrentRow();
        loggerExternal.exiting(this.getClassNameLogging(), "last", value);
        return value;
    }

    private void moveLast() throws SQLServerException {
        if (0 == this.serverCursorId) {
            this.currentRow = this.clientMoveAbsolute(-1);
            return;
        }
        this.doServerFetch(8, 0, this.fetchSize);
        if (!this.scrollWindow.next(this)) {
            this.currentRow = -1;
            return;
        }
        while (this.scrollWindow.next(this)) {
        }
        this.scrollWindow.previous(this);
        this.currentRow = this.isDynamic() ? -2 : this.rowCount;
    }

    @Override
    public int getRow() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getRow");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        if (this.isDynamic() && !this.isForwardOnly()) {
            this.throwUnsupportedCursorOp();
        }
        if (!this.hasCurrentRow() || this.isOnInsertRow) {
            return 0;
        }
        assert (this.currentRow >= 1);
        loggerExternal.exiting(this.getClassNameLogging(), "getRow", this.currentRow);
        return this.currentRow;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "absolute");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " row:" + row + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (this.isDynamic()) {
            this.throwUnsupportedCursorOp();
        }
        this.moverInit();
        this.moveAbsolute(row);
        boolean value = this.hasCurrentRow();
        loggerExternal.exiting(this.getClassNameLogging(), "absolute", value);
        return value;
    }

    private void moveAbsolute(int row) throws SQLServerException {
        assert (-2 != this.currentRow);
        assert (!this.isDynamic());
        switch (row) {
            case 0: {
                this.moveBeforeFirst();
                return;
            }
            case 1: {
                this.moveFirst();
                return;
            }
            case -1: {
                this.moveLast();
                return;
            }
        }
        if (this.hasCurrentRow()) {
            assert (this.currentRow >= 1);
            if (row > 0) {
                this.moveRelative(row - this.currentRow);
                return;
            }
            if (-3 != this.rowCount) {
                assert (row < 0);
                this.moveRelative(this.rowCount + row + 1 - this.currentRow);
                return;
            }
        }
        if (0 == this.serverCursorId) {
            this.currentRow = this.clientMoveAbsolute(row);
            return;
        }
        this.doServerFetch(16, row, this.fetchSize);
        if (!this.scrollWindow.next(this)) {
            this.currentRow = row < 0 ? 0 : -1;
            return;
        }
        if (row > 0) {
            this.currentRow = row;
        } else {
            assert (row < 0);
            assert (this.rowCount + row + 1 >= 1);
            this.currentRow = this.rowCount + row + 1;
        }
    }

    private boolean fetchBufferHasRows() throws SQLServerException {
        assert (0 == this.serverCursorId);
        assert (null != this.tdsReader);
        assert (this.lastColumnIndex >= 0);
        if (this.lastColumnIndex >= 1) {
            return true;
        }
        int tdsTokenType = this.tdsReader.peekTokenType();
        return 209 == tdsTokenType || 210 == tdsTokenType || 171 == tdsTokenType || 170 == tdsTokenType;
    }

    final void discardCurrentRow() throws SQLServerException {
        assert (this.lastColumnIndex >= 0);
        this.updatedCurrentRow = false;
        this.deletedCurrentRow = false;
        if (this.lastColumnIndex >= 1) {
            this.initializeNullCompressedColumns();
            for (int columnIndex = 1; columnIndex < this.lastColumnIndex; ++columnIndex) {
                this.getColumn(columnIndex).clear();
            }
            this.skipColumns(this.columns.length + 1 - this.lastColumnIndex, true);
        }
        this.resultSetCurrentRowType = RowType.UNKNOWN;
        this.areNullCompressedColumnsInitialized = false;
    }

    final int fetchBufferGetRow() {
        if (this.isForwardOnly()) {
            return this.numFetchedRows;
        }
        return this.scrollWindow.getRow();
    }

    final void fetchBufferBeforeFirst() throws SQLServerException {
        assert (0 == this.serverCursorId);
        assert (null != this.tdsReader);
        this.discardCurrentRow();
        this.fetchBuffer.reset();
        this.lastColumnIndex = 0;
    }

    final TDSReaderMark fetchBufferMark() {
        assert (null != this.tdsReader);
        return this.tdsReader.mark();
    }

    final void fetchBufferReset(TDSReaderMark mark) throws SQLServerException {
        assert (null != this.tdsReader);
        assert (null != mark);
        this.discardCurrentRow();
        this.tdsReader.reset(mark);
        this.lastColumnIndex = 1;
    }

    final boolean fetchBufferNext() throws SQLServerException {
        if (null == this.tdsReader) {
            return false;
        }
        this.discardCurrentRow();
        RowType fetchBufferCurrentRowType = RowType.UNKNOWN;
        try {
            fetchBufferCurrentRowType = this.fetchBuffer.nextRow();
            if (fetchBufferCurrentRowType.equals((Object)RowType.UNKNOWN)) {
                boolean bl = false;
                return bl;
            }
        }
        catch (SQLServerException e) {
            this.currentRow = -1;
            this.rowErrorException = e;
            throw e;
        }
        finally {
            this.lastColumnIndex = 0;
            this.resultSetCurrentRowType = fetchBufferCurrentRowType;
        }
        ++this.numFetchedRows;
        this.lastColumnIndex = 1;
        return true;
    }

    private void clientMoveAfterLast() throws SQLServerException {
        assert (-2 != this.currentRow);
        int rowsSkipped = 0;
        while (this.fetchBufferNext()) {
            ++rowsSkipped;
        }
        if (-3 == this.rowCount) {
            assert (-1 != this.currentRow);
            this.rowCount = (0 == this.currentRow ? 0 : this.currentRow) + rowsSkipped;
        }
    }

    private int clientMoveAbsolute(int row) throws SQLServerException {
        assert (0 == this.serverCursorId);
        this.scrollWindow.clear();
        if (row < 0) {
            if (-3 == this.rowCount) {
                this.clientMoveAfterLast();
                this.currentRow = -1;
            }
            assert (this.rowCount >= 0);
            if (this.rowCount + row < 0) {
                this.moveBeforeFirst();
                return 0;
            }
            row = this.rowCount + row + 1;
        }
        assert (row > 0);
        if (-1 == this.currentRow || row <= this.currentRow) {
            this.moveBeforeFirst();
        }
        assert (0 == this.currentRow || this.currentRow < row);
        while (this.currentRow != row) {
            if (!this.fetchBufferNext()) {
                if (-3 == this.rowCount) {
                    this.rowCount = this.currentRow;
                }
                return -1;
            }
            if (0 == this.currentRow) {
                this.currentRow = 1;
                continue;
            }
            this.updateCurrentRow(1);
        }
        return row;
    }

    @Override
    public boolean previous() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "previous");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.moverInit();
        if (0 == this.currentRow) {
            return false;
        }
        if (-1 == this.currentRow) {
            this.moveLast();
        } else {
            this.moveBackward(-1);
        }
        boolean value = this.hasCurrentRow();
        loggerExternal.exiting(this.getClassNameLogging(), "previous", value);
        return value;
    }

    private void cancelInsert() {
        if (this.isOnInsertRow) {
            this.isOnInsertRow = false;
            this.clearColumnsValues();
        }
    }

    final void clearColumnsValues() {
        for (Column column : this.columns) {
            column.cancelUpdates();
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getWarnings");
        loggerExternal.exiting(this.getClassNameLogging(), "getWarnings", null);
        return null;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "setFetchDirection", direction);
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        if (1000 != direction && 1001 != direction && 1002 != direction || 1000 != direction && (2003 == this.stmt.resultSetType || 2004 == this.stmt.resultSetType)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidFetchDirection"));
            Object[] msgArgs = new Object[]{direction};
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), null, false);
        }
        this.fetchDirection = direction;
        loggerExternal.exiting(this.getClassNameLogging(), "setFetchDirection");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getFetchDirection");
        this.checkClosed();
        loggerExternal.exiting(this.getClassNameLogging(), "getFetchDirection", this.fetchDirection);
        return this.fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "setFetchSize", rows);
        this.checkClosed();
        if (rows < 0) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_invalidFetchSize"), null, false);
        }
        this.fetchSize = 0 == rows ? this.stmt.defaultFetchSize : rows;
        loggerExternal.exiting(this.getClassNameLogging(), "setFetchSize");
    }

    @Override
    public int getFetchSize() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getFetchSize");
        this.checkClosed();
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", this.fetchSize);
        return this.fetchSize;
    }

    @Override
    public int getType() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getType");
        this.checkClosed();
        int value = this.stmt.getResultSetType();
        loggerExternal.exiting(this.getClassNameLogging(), "getType", value);
        return value;
    }

    @Override
    public int getConcurrency() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getConcurrency");
        this.checkClosed();
        int value = this.stmt.getResultSetConcurrency();
        loggerExternal.exiting(this.getClassNameLogging(), "getConcurrency", value);
        return value;
    }

    Column getterGetColumn(int index) throws SQLServerException {
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantGetColumnValueFromDeletedRow");
        this.verifyValidColumnIndex(index);
        if (this.updatedCurrentRow) {
            this.doRefreshRow();
            this.verifyResultSetHasCurrentRow();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " Getting Column:" + index);
        }
        this.fillLOBs();
        return this.loadColumn(index);
    }

    private Object getValue(int columnIndex, JDBCType jdbcType) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, null, null);
    }

    private Object getValue(int columnIndex, JDBCType jdbcType, Calendar cal) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, null, cal);
    }

    private Object getValue(int columnIndex, JDBCType jdbcType, InputStreamGetterArgs getterArgs) throws SQLServerException {
        return this.getValue(columnIndex, jdbcType, getterArgs, null);
    }

    private Object getValue(int columnIndex, JDBCType jdbcType, InputStreamGetterArgs getterArgs, Calendar cal) throws SQLServerException {
        Object o = this.getterGetColumn(columnIndex).getValue(jdbcType, getterArgs, cal, this.tdsReader, this.stmt);
        this.lastValueWasNull = null == o;
        return o;
    }

    void setInternalVariantType(int columnIndex, SqlVariant type) throws SQLServerException {
        this.getterGetColumn(columnIndex).setInternalVariant(type);
    }

    SqlVariant getVariantInternalType(int columnIndex) throws SQLServerException {
        return this.getterGetColumn(columnIndex).getInternalVariant();
    }

    private Object getStream(int columnIndex, StreamType streamType) throws SQLServerException {
        Object value = this.getValue(columnIndex, streamType.getJDBCType(), new InputStreamGetterArgs(streamType, this.stmt.getExecProps().isResponseBufferingAdaptive(), this.isForwardOnly(), this.toString()));
        this.activeStream = (Closeable)value;
        return value;
    }

    private SQLXML getSQLXMLInternal(int columnIndex) throws SQLServerException {
        SQLServerSQLXML value = (SQLServerSQLXML)this.getValue(columnIndex, JDBCType.SQLXML, new InputStreamGetterArgs(StreamType.SQLXML, this.stmt.getExecProps().isResponseBufferingAdaptive(), this.isForwardOnly(), this.toString()));
        if (null != value) {
            this.activeStream = value.getStream();
        }
        return value;
    }

    private void configureLobs(SQLServerLob lob) throws SQLServerException {
        Connection c;
        if (null != this.stmt && (c = this.stmt.getConnection()) instanceof ISQLServerConnection && null != c && !((ISQLServerConnection)c).getDelayLoadingLobs() && null != lob) {
            lob.setDelayLoadingLob();
        }
        this.activeLOB = lob;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", columnIndex);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(columnIndex, StreamType.ASCII);
        loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }

    @Override
    public InputStream getAsciiStream(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", columnName);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(this.findColumn(columnName), StreamType.ASCII);
        loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }

    @Override
    @Deprecated(since="6.5.4")
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[]{columnIndex, scale});
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    @Deprecated(since="6.5.4")
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "columnName", new Object[]{columnName, scale});
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", columnIndex);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(columnIndex, StreamType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }

    @Override
    public InputStream getBinaryStream(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", columnName);
        this.checkClosed();
        InputStream value = (InputStream)this.getStream(this.findColumn(columnName), StreamType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBoolean", columnIndex);
        this.checkClosed();
        Boolean value = (Boolean)this.getValue(columnIndex, JDBCType.BIT);
        loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value ? value : false;
    }

    @Override
    public boolean getBoolean(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBoolean", columnName);
        this.checkClosed();
        Boolean value = (Boolean)this.getValue(this.findColumn(columnName), JDBCType.BIT);
        loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value ? value : false;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getByte", columnIndex);
        this.checkClosed();
        Short value = (Short)this.getValue(columnIndex, JDBCType.TINYINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getByte", value);
        return null != value ? value.byteValue() : (byte)0;
    }

    @Override
    public byte getByte(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getByte", columnName);
        this.checkClosed();
        Short value = (Short)this.getValue(this.findColumn(columnName), JDBCType.TINYINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getByte", value);
        return null != value ? value.byteValue() : (byte)0;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBytes", columnIndex);
        this.checkClosed();
        byte[] value = (byte[])this.getValue(columnIndex, JDBCType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }

    @Override
    public byte[] getBytes(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBytes", columnName);
        this.checkClosed();
        byte[] value = (byte[])this.getValue(this.findColumn(columnName), JDBCType.BINARY);
        loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }

    @Override
    public Date getDate(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDate", columnIndex);
        this.checkClosed();
        Date value = (Date)this.getValue(columnIndex, JDBCType.DATE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDate", columnName);
        this.checkClosed();
        Date value = (Date)this.getValue(this.findColumn(columnName), JDBCType.DATE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[]{columnIndex, cal});
        }
        this.checkClosed();
        Date value = (Date)this.getValue(columnIndex, JDBCType.DATE, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public Date getDate(String colName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[]{colName, cal});
        }
        this.checkClosed();
        Date value = (Date)this.getValue(this.findColumn(colName), JDBCType.DATE, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDouble", columnIndex);
        this.checkClosed();
        Double value = (Double)this.getValue(columnIndex, JDBCType.DOUBLE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return null != value ? value : 0.0;
    }

    @Override
    public double getDouble(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDouble", columnName);
        this.checkClosed();
        Double value = (Double)this.getValue(this.findColumn(columnName), JDBCType.DOUBLE);
        loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return null != value ? value : 0.0;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        Float value = (Float)this.getValue(columnIndex, JDBCType.REAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return null != value ? value.floatValue() : 0.0f;
    }

    @Override
    public float getFloat(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        Float value = (Float)this.getValue(this.findColumn(columnName), JDBCType.REAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return null != value ? value.floatValue() : 0.0f;
    }

    @Override
    public Geometry getGeometry(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        Geometry value = (Geometry)this.getValue(columnIndex, JDBCType.GEOMETRY);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }

    @Override
    public Geometry getGeometry(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        Geometry value = (Geometry)this.getValue(this.findColumn(columnName), JDBCType.GEOMETRY);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }

    @Override
    public Geography getGeography(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnIndex);
        this.checkClosed();
        Geography value = (Geography)this.getValue(columnIndex, JDBCType.GEOGRAPHY);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }

    @Override
    public Geography getGeography(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getFloat", columnName);
        this.checkClosed();
        Geography value = (Geography)this.getValue(this.findColumn(columnName), JDBCType.GEOGRAPHY);
        loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return value;
    }

    @Override
    public int getInt(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getInt", columnIndex);
        this.checkClosed();
        Integer value = (Integer)this.getValue(columnIndex, JDBCType.INTEGER);
        loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return null != value ? value : 0;
    }

    @Override
    public int getInt(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getInt", columnName);
        this.checkClosed();
        Integer value = (Integer)this.getValue(this.findColumn(columnName), JDBCType.INTEGER);
        loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return null != value ? value : 0;
    }

    @Override
    public long getLong(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getLong", columnIndex);
        this.checkClosed();
        Long value = (Long)this.getValue(columnIndex, JDBCType.BIGINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return null != value ? value : 0L;
    }

    @Override
    public long getLong(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getLong", columnName);
        this.checkClosed();
        Long value = (Long)this.getValue(this.findColumn(columnName), JDBCType.BIGINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return null != value ? value : 0L;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        if (this.metaData == null) {
            this.metaData = new SQLServerResultSetMetaData(this.stmt.connection, this);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", this.metaData);
        return this.metaData;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", columnIndex);
        this.checkClosed();
        Object value = this.getValue(columnIndex, this.getterGetColumn(columnIndex).getTypeInfo().getSSType().getJDBCType());
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        Comparable<Comparable<ChronoLocalDate>> returnValue;
        loggerExternal.entering(this.getClassNameLogging(), "getObject", columnIndex);
        this.checkClosed();
        if (type == String.class) {
            returnValue = this.getString(columnIndex);
        } else if (type == Byte.class) {
            byte byteValue = this.getByte(columnIndex);
            returnValue = this.wasNull() ? null : Byte.valueOf(byteValue);
        } else if (type == Short.class) {
            short shortValue = this.getShort(columnIndex);
            returnValue = this.wasNull() ? null : Short.valueOf(shortValue);
        } else if (type == Integer.class) {
            int intValue = this.getInt(columnIndex);
            returnValue = this.wasNull() ? null : Integer.valueOf(intValue);
        } else if (type == Long.class) {
            long longValue = this.getLong(columnIndex);
            returnValue = this.wasNull() ? null : Long.valueOf(longValue);
        } else if (type == BigDecimal.class) {
            returnValue = this.getBigDecimal(columnIndex);
        } else if (type == Boolean.class) {
            boolean booleanValue = this.getBoolean(columnIndex);
            returnValue = this.wasNull() ? null : Boolean.valueOf(booleanValue);
        } else if (type == Date.class) {
            returnValue = this.getDate(columnIndex);
        } else if (type == Time.class) {
            returnValue = this.getTime(columnIndex);
        } else if (type == Timestamp.class) {
            returnValue = this.getTimestamp(columnIndex);
        } else if (type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class) {
            LocalDateTime ldt = this.getLocalDateTime(columnIndex);
            returnValue = null == ldt ? null : (type == LocalDateTime.class ? ldt : (type == LocalDate.class ? ldt.toLocalDate() : ldt.toLocalTime()));
        } else if (type == OffsetDateTime.class) {
            DateTimeOffset dateTimeOffset = this.getDateTimeOffset(columnIndex);
            returnValue = dateTimeOffset == null ? null : dateTimeOffset.getOffsetDateTime();
        } else if (type == OffsetTime.class) {
            DateTimeOffset dateTimeOffset = this.getDateTimeOffset(columnIndex);
            returnValue = dateTimeOffset == null ? null : dateTimeOffset.getOffsetDateTime().toOffsetTime();
        } else if (type == DateTimeOffset.class) {
            returnValue = this.getDateTimeOffset(columnIndex);
        } else if (type == UUID.class) {
            byte[] guid = this.getBytes(columnIndex);
            returnValue = guid != null ? Util.readGUIDtoUUID(guid) : null;
        } else if (type == SQLXML.class) {
            returnValue = this.getSQLXML(columnIndex);
        } else if (type == Blob.class) {
            returnValue = this.getBlob(columnIndex);
        } else if (type == Clob.class) {
            returnValue = this.getClob(columnIndex);
        } else if (type == NClob.class) {
            returnValue = this.getNClob(columnIndex);
        } else if (type == byte[].class) {
            returnValue = (Comparable<Comparable<ChronoLocalDate>>)this.getBytes(columnIndex);
        } else if (type == Float.class) {
            float floatValue = this.getFloat(columnIndex);
            returnValue = this.wasNull() ? null : Float.valueOf(floatValue);
        } else if (type == Double.class) {
            double doubleValue = this.getDouble(columnIndex);
            returnValue = this.wasNull() ? null : Double.valueOf(doubleValue);
        } else {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionTo"));
            Object[] msgArgs = new Object[]{type};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", columnIndex);
        return type.cast(returnValue);
    }

    @Override
    public Object getObject(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", columnName);
        this.checkClosed();
        Object value = this.getObject(this.findColumn(columnName));
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public <T> T getObject(String columnName, Class<T> type) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getObject", columnName);
        this.checkClosed();
        T value = this.getObject(this.findColumn(columnName), type);
        loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }

    @Override
    public short getShort(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getShort", columnIndex);
        this.checkClosed();
        Short value = (Short)this.getValue(columnIndex, JDBCType.SMALLINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return null != value ? value : (short)0;
    }

    @Override
    public short getShort(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getShort", columnName);
        this.checkClosed();
        Short value = (Short)this.getValue(this.findColumn(columnName), JDBCType.SMALLINT);
        loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return null != value ? value : (short)0;
    }

    @Override
    public String getString(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getString", columnIndex);
        this.checkClosed();
        String value = null;
        Object objectValue = this.getValue(columnIndex, JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }

    @Override
    public String getString(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getString", columnName);
        this.checkClosed();
        String value = null;
        Object objectValue = this.getValue(this.findColumn(columnName), JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNString", columnIndex);
        this.checkClosed();
        String value = (String)this.getValue(columnIndex, JDBCType.NCHAR);
        loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNString", columnLabel);
        this.checkClosed();
        String value = (String)this.getValue(this.findColumn(columnLabel), JDBCType.NCHAR);
        loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }

    @Override
    public String getUniqueIdentifier(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getUniqueIdentifier", columnIndex);
        this.checkClosed();
        String value = (String)this.getValue(columnIndex, JDBCType.GUID);
        loggerExternal.exiting(this.getClassNameLogging(), "getUniqueIdentifier", value);
        return value;
    }

    @Override
    public String getUniqueIdentifier(String columnLabel) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getUniqueIdentifier", columnLabel);
        this.checkClosed();
        String value = (String)this.getValue(this.findColumn(columnLabel), JDBCType.GUID);
        loggerExternal.exiting(this.getClassNameLogging(), "getUniqueIdentifier", value);
        return value;
    }

    @Override
    public Time getTime(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTime", columnIndex);
        this.checkClosed();
        Time value = (Time)this.getValue(columnIndex, JDBCType.TIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTime", columnName);
        this.checkClosed();
        Time value = (Time)this.getValue(this.findColumn(columnName), JDBCType.TIME);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[]{columnIndex, cal});
        }
        this.checkClosed();
        Time value = (Time)this.getValue(columnIndex, JDBCType.TIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Time getTime(String colName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[]{colName, cal});
        }
        this.checkClosed();
        Time value = (Time)this.getValue(this.findColumn(colName), JDBCType.TIME, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", columnIndex);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", columnName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[]{columnIndex, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTimeStamp", value);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String colName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[]{colName, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
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
    public Timestamp getDateTime(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTime", columnIndex);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTime", columnName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(int columnIndex, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[]{columnIndex, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getDateTime(String colName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[]{colName, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", columnIndex);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", columnName);
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(columnName), JDBCType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(int columnIndex, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[]{columnIndex, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(columnIndex, JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public Timestamp getSmallDateTime(String colName, Calendar cal) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[]{colName, cal});
        }
        this.checkClosed();
        Timestamp value = (Timestamp)this.getValue(this.findColumn(colName), JDBCType.TIMESTAMP, cal);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }

    @Override
    public DateTimeOffset getDateTimeOffset(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", columnIndex);
        this.checkClosed();
        if (!this.stmt.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        DateTimeOffset value = (DateTimeOffset)this.getValue(columnIndex, JDBCType.DATETIMEOFFSET);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }

    @Override
    public DateTimeOffset getDateTimeOffset(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", columnName);
        this.checkClosed();
        if (!this.stmt.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        DateTimeOffset value = (DateTimeOffset)this.getValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET);
        loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }

    @Override
    @Deprecated(since="6.5.4")
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getUnicodeStream", columnIndex);
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    @Deprecated(since="6.5.4")
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getUnicodeStream", columnName);
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getObject", new Object[]{i, map});
        }
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getRef");
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Blob getBlob(int i) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBlob", i);
        this.checkClosed();
        Blob value = (Blob)this.getValue(i, JDBCType.BLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public Blob getBlob(String colName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getBlob", colName);
        this.checkClosed();
        Blob value = (Blob)this.getValue(this.findColumn(colName), JDBCType.BLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getClob", columnIndex);
        this.checkClosed();
        Clob value = (Clob)this.getValue(columnIndex, JDBCType.CLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getClob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public Clob getClob(String colName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getClob", colName);
        this.checkClosed();
        Clob value = (Clob)this.getValue(this.findColumn(colName), JDBCType.CLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getClob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNClob", columnIndex);
        this.checkClosed();
        NClob value = (NClob)this.getValue(columnIndex, JDBCType.NCLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getNClob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNClob", columnLabel);
        this.checkClosed();
        NClob value = (NClob)this.getValue(this.findColumn(columnLabel), JDBCType.NCLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "getNClob", value);
        this.configureLobs((SQLServerLob)((Object)value));
        return value;
    }

    @Override
    public Array getArray(int i) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Ref getRef(String colName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public Array getArray(String colName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public String getCursorName() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getCursorName");
        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_positionedUpdatesNotSupported"), null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "getCursorName", null);
        return null;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", columnIndex);
        this.checkClosed();
        Reader value = (Reader)this.getStream(columnIndex, StreamType.CHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", value);
        return value;
    }

    @Override
    public Reader getCharacterStream(String columnName) throws SQLException {
        this.checkClosed();
        loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", columnName);
        Reader value = (Reader)this.getStream(this.findColumn(columnName), StreamType.CHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", value);
        return value;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", columnIndex);
        this.checkClosed();
        Reader value = (Reader)this.getStream(columnIndex, StreamType.NCHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", value);
        return value;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", columnLabel);
        this.checkClosed();
        Reader value = (Reader)this.getStream(this.findColumn(columnLabel), StreamType.NCHARACTER);
        loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", value);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", columnIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", columnName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }

    @Override
    public BigDecimal getMoney(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMoney", columnIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }

    @Override
    public BigDecimal getMoney(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMoney", columnName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }

    @Override
    public BigDecimal getSmallMoney(int columnIndex) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", columnIndex);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(columnIndex, JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }

    @Override
    public BigDecimal getSmallMoney(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", columnName);
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(columnName), JDBCType.DECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", columnIndex);
        SQLXML xml = this.getSQLXMLInternal(columnIndex);
        loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", xml);
        return xml;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", columnLabel);
        SQLXML xml = this.getSQLXMLInternal(this.findColumn(columnLabel));
        loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", xml);
        return xml;
    }

    @Override
    public boolean rowUpdated() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "rowUpdated");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        loggerExternal.exiting(this.getClassNameLogging(), "rowUpdated", false);
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "rowInserted");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        loggerExternal.exiting(this.getClassNameLogging(), "rowInserted", false);
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "rowDeleted");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        if (this.isOnInsertRow || !this.hasCurrentRow()) {
            return false;
        }
        boolean deleted = this.currentRowDeleted();
        loggerExternal.exiting(this.getClassNameLogging(), "rowDeleted", deleted);
        return deleted;
    }

    private boolean currentRowDeleted() throws SQLServerException {
        assert (this.hasCurrentRow());
        assert (null != this.tdsReader);
        return this.deletedCurrentRow || 0 != this.serverCursorId && 2 == this.loadColumn(this.columns.length).getInt(this.tdsReader, this.stmt);
    }

    private Column updaterGetColumn(int index) throws SQLServerException {
        this.verifyResultSetIsUpdatable();
        this.verifyValidColumnIndex(index);
        if (!this.columns[index - 1].isUpdatable()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_cantUpdateColumn"), SQLSTATE_INVALID_DESCRIPTOR_INDEX, false);
        }
        if (!this.isOnInsertRow) {
            if (!this.hasCurrentRow()) {
                SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_resultsetNoCurrentRow"), null, true);
            }
            this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        }
        return this.getColumn(index);
    }

    private void updateValue(int columnIndex, JDBCType jdbcType, Object value, JavaType javaType, boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, forceEncrypt, columnIndex);
    }

    private void updateValue(int columnIndex, JDBCType jdbcType, Object value, JavaType javaType, Calendar cal, boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, cal, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, forceEncrypt, columnIndex);
    }

    private void updateValue(int columnIndex, JDBCType jdbcType, Object value, JavaType javaType, Integer precision, Integer scale, boolean forceEncrypt) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(jdbcType, value, javaType, null, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, columnIndex);
    }

    private void updateStream(int columnIndex, StreamType streamType, Object value, JavaType javaType, long length) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(streamType.getJDBCType(), value, javaType, new StreamSetterArgs(streamType, length), null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, false, columnIndex);
    }

    private void updateSQLXMLInternal(int columnIndex, SQLXML value) throws SQLServerException {
        this.updaterGetColumn(columnIndex).updateValue(JDBCType.SQLXML, value, JavaType.SQLXML, new StreamSetterArgs(StreamType.SQLXML, -1L), null, null, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, null, false, columnIndex);
    }

    @Override
    public void updateNull(int index) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "updateNull", index);
        this.checkClosed();
        this.updateValue(index, this.updaterGetColumn(index).getTypeInfo().getSSType().getJDBCType(), null, JavaType.OBJECT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNull");
    }

    @Override
    public void updateBoolean(int index, boolean x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIT, x, JavaType.BOOLEAN, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }

    @Override
    public void updateBoolean(int index, boolean x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIT, x, JavaType.BOOLEAN, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }

    @Override
    public void updateByte(int index, byte x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TINYINT, x, JavaType.BYTE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }

    @Override
    public void updateByte(int index, byte x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TINYINT, x, JavaType.BYTE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }

    @Override
    public void updateShort(int index, short x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }

    @Override
    public void updateShort(int index, short x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }

    @Override
    public void updateInt(int index, int x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.INTEGER, x, JavaType.INTEGER, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }

    @Override
    public void updateInt(int index, int x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.INTEGER, x, JavaType.INTEGER, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }

    @Override
    public void updateLong(int index, long x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIGINT, x, JavaType.LONG, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }

    @Override
    public void updateLong(int index, long x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BIGINT, x, JavaType.LONG, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }

    @Override
    public void updateFloat(int index, float x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[]{index, Float.valueOf(x)});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.REAL, Float.valueOf(x), JavaType.FLOAT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }

    @Override
    public void updateFloat(int index, float x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[]{index, Float.valueOf(x), forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.REAL, Float.valueOf(x), JavaType.FLOAT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }

    @Override
    public void updateDouble(int index, double x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DOUBLE, x, JavaType.DOUBLE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }

    @Override
    public void updateDouble(int index, double x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DOUBLE, x, JavaType.DOUBLE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }

    @Override
    public void updateMoney(int index, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.MONEY, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }

    @Override
    public void updateMoney(int index, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.MONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }

    @Override
    public void updateMoney(String columnName, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.MONEY, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }

    @Override
    public void updateMoney(String columnName, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateMoney", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.MONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateMoney");
    }

    @Override
    public void updateSmallMoney(int index, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }

    @Override
    public void updateSmallMoney(int index, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }

    @Override
    public void updateSmallMoney(String columnName, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }

    @Override
    public void updateSmallMoney(String columnName, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallMoney", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallMoney");
    }

    @Override
    public void updateBigDecimal(int index, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateBigDecimal(int index, BigDecimal x, Integer precision, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateBigDecimal(int index, BigDecimal x, Integer precision, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateString(int columnIndex, String stringValue) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[]{columnIndex, stringValue});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.VARCHAR, stringValue, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }

    @Override
    public void updateString(int columnIndex, String stringValue, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[]{columnIndex, stringValue, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.VARCHAR, stringValue, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[]{columnIndex, nString});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NVARCHAR, nString, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }

    @Override
    public void updateNString(int columnIndex, String nString, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[]{columnIndex, nString, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NVARCHAR, nString, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[]{columnLabel, nString});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NVARCHAR, nString, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }

    @Override
    public void updateNString(String columnLabel, String nString, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNString", new Object[]{columnLabel, nString, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NVARCHAR, nString, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNString");
    }

    @Override
    public void updateBytes(int index, byte[] x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BINARY, x, JavaType.BYTEARRAY, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }

    @Override
    public void updateBytes(int index, byte[] x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.BINARY, x, JavaType.BYTEARRAY, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }

    @Override
    public void updateDate(int index, Date x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATE, x, JavaType.DATE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }

    @Override
    public void updateDate(int index, Date x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATE, x, JavaType.DATE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }

    @Override
    public void updateTime(int index, Time x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTime(int index, Time x, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTime(int index, Time x, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTimestamp(int index, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateTimestamp(int index, Timestamp x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateTimestamp(int index, Timestamp x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateDateTime(int index, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateDateTime(int index, Timestamp x, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateDateTime(int index, Timestamp x, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateSmallDateTime(int index, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateSmallDateTime(int index, Timestamp x, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateSmallDateTime(int index, Timestamp x, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateDateTimeOffset(int index, DateTimeOffset x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateDateTimeOffset(int index, DateTimeOffset x, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateDateTimeOffset(int index, DateTimeOffset x, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateUniqueIdentifier(int index, String x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[]{index, x});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.GUID, x, JavaType.STRING, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }

    @Override
    public void updateUniqueIdentifier(int index, String x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[]{index, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(index, JDBCType.GUID, x, JavaType.STRING, null, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{columnIndex, x});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateAsciiStream(int index, InputStream x, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{index, x, length});
        }
        this.checkClosed();
        this.updateStream(index, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{columnIndex, x, length});
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{columnLabel, x});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{columnName, x, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream streamValue, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateAsciiStream", new Object[]{columnName, streamValue, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.ASCII, streamValue, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateAsciiStream");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnIndex, x});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream streamValue, int length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnIndex, streamValue, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, streamValue, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnIndex, x, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnLabel, x});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateBinaryStream(String columnName, InputStream streamValue, int length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnName, streamValue, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.BINARY, streamValue, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBinaryStream", new Object[]{columnLabel, x, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBinaryStream");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnIndex, x});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, x, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader readerValue, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnIndex, readerValue, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, readerValue, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnIndex, x, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, x, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnLabel, reader});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }

    @Override
    public void updateCharacterStream(String columnName, Reader readerValue, int length) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnName, readerValue, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnName), StreamType.CHARACTER, readerValue, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateCharacterStream");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateCharacterStream", new Object[]{columnLabel, reader, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[]{columnIndex, x});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, x, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[]{columnIndex, x, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, x, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[]{columnLabel, reader});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNCharacterStream", new Object[]{columnLabel, reader, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNCharacterStream");
    }

    @Override
    public void updateObject(int index, Object obj) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, obj});
        }
        this.checkClosed();
        this.updateObject(index, obj, null, null, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(int index, Object x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(int index, Object x, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, x, scale});
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, precision, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(int index, Object x, int precision, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateObject(index, x, scale, null, precision, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    final void updateObject(int index, Object x, Integer scale, JDBCType jdbcType, Integer precision, boolean forceEncrypt) throws SQLServerException {
        Column column = this.updaterGetColumn(index);
        SSType ssType = column.getTypeInfo().getSSType();
        if (null == x) {
            if (null == jdbcType || jdbcType.isUnsupported()) {
                jdbcType = ssType.getJDBCType();
            }
            column.updateValue(jdbcType, x, JavaType.OBJECT, null, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, index);
        } else {
            JavaType javaType = JavaType.of(x);
            JDBCType objectJdbcType = javaType.getJDBCType(ssType, ssType.getJDBCType());
            if (null == jdbcType) {
                jdbcType = objectJdbcType;
            } else if (!objectJdbcType.convertsTo(jdbcType)) {
                DataTypes.throwConversionError(objectJdbcType.toString(), jdbcType.toString());
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
            column.updateValue(jdbcType, x, javaType, streamSetterArgs, null, scale, this.stmt.connection, this.stmt.stmtColumnEncriptionSetting, precision, forceEncrypt, index);
        }
    }

    @Override
    public void updateNull(String columnName) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "updateNull", columnName);
        this.checkClosed();
        int columnIndex = this.findColumn(columnName);
        this.updateValue(columnIndex, this.updaterGetColumn(columnIndex).getTypeInfo().getSSType().getJDBCType(), null, JavaType.OBJECT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNull");
    }

    @Override
    public void updateBoolean(String columnName, boolean x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIT, x, JavaType.BOOLEAN, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }

    @Override
    public void updateBoolean(String columnName, boolean x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBoolean", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIT, x, JavaType.BOOLEAN, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBoolean");
    }

    @Override
    public void updateByte(String columnName, byte x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }

    @Override
    public void updateByte(String columnName, byte x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateByte", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateByte");
    }

    @Override
    public void updateShort(String columnName, short x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLINT, x, JavaType.SHORT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }

    @Override
    public void updateShort(String columnName, short x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateShort", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLINT, x, JavaType.SHORT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateShort");
    }

    @Override
    public void updateInt(String columnName, int x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.INTEGER, x, JavaType.INTEGER, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }

    @Override
    public void updateInt(String columnName, int x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateInt", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.INTEGER, x, JavaType.INTEGER, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateInt");
    }

    @Override
    public void updateLong(String columnName, long x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIGINT, x, JavaType.LONG, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }

    @Override
    public void updateLong(String columnName, long x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateLong", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BIGINT, x, JavaType.LONG, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateLong");
    }

    @Override
    public void updateFloat(String columnName, float x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[]{columnName, Float.valueOf(x)});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.REAL, Float.valueOf(x), JavaType.FLOAT, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }

    @Override
    public void updateFloat(String columnName, float x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateFloat", new Object[]{columnName, Float.valueOf(x), forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.REAL, Float.valueOf(x), JavaType.FLOAT, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateFloat");
    }

    @Override
    public void updateDouble(String columnName, double x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DOUBLE, x, JavaType.DOUBLE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }

    @Override
    public void updateDouble(String columnName, double x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDouble", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DOUBLE, x, JavaType.DOUBLE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDouble");
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x, Integer precision, Integer scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{columnName, x, precision, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x, Integer precision, Integer scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBigDecimal", new Object[]{columnName, x, precision, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBigDecimal");
    }

    @Override
    public void updateString(String columnName, String x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.VARCHAR, x, JavaType.STRING, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }

    @Override
    public void updateString(String columnName, String x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateString", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.VARCHAR, x, JavaType.STRING, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateString");
    }

    @Override
    public void updateBytes(String columnName, byte[] x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTEARRAY, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }

    @Override
    public void updateBytes(String columnName, byte[] x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBytes", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BINARY, x, JavaType.BYTEARRAY, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBytes");
    }

    @Override
    public void updateDate(String columnName, Date x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATE, x, JavaType.DATE, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }

    @Override
    public void updateDate(String columnName, Date x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDate", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATE, x, JavaType.DATE, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDate");
    }

    @Override
    public void updateTime(String columnName, Time x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTime(String columnName, Time x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTime(String columnName, Time x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTime", new Object[]{columnName, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTime");
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateTimestamp", new Object[]{columnName, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateTimestamp");
    }

    @Override
    public void updateDateTime(String columnName, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateDateTime(String columnName, Timestamp x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateDateTime(String columnName, Timestamp x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTime", new Object[]{columnName, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTime");
    }

    @Override
    public void updateSmallDateTime(String columnName, Timestamp x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateSmallDateTime(String columnName, Timestamp x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateSmallDateTime(String columnName, Timestamp x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSmallDateTime", new Object[]{columnName, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSmallDateTime");
    }

    @Override
    public void updateDateTimeOffset(String columnName, DateTimeOffset x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateDateTimeOffset(String columnName, DateTimeOffset x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateDateTimeOffset(String columnName, DateTimeOffset x, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateDateTimeOffset", new Object[]{columnName, x, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateDateTimeOffset");
    }

    @Override
    public void updateUniqueIdentifier(String columnName, String x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.GUID, x, JavaType.STRING, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }

    @Override
    public void updateUniqueIdentifier(String columnName, String x, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateUniqueIdentifier", new Object[]{columnName, x, forceEncrypt});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.GUID, x, JavaType.STRING, null, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateUniqueIdentifier");
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, x, scale});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object x, int precision, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, x, precision, scale});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, precision, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object x, int precision, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, x, precision, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, scale, null, precision, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object x) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, x});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), x, null, null, null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSQLXML", new Object[]{columnIndex, xmlObject});
        }
        this.updateSQLXMLInternal(columnIndex, xmlObject);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSQLXML");
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateSQLXML", new Object[]{columnLabel, x});
        }
        this.updateSQLXMLInternal(this.findColumn(columnLabel), x);
        loggerExternal.exiting(this.getClassNameLogging(), "updateSQLXML");
    }

    @Override
    public int getHoldability() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "getHoldability");
        this.checkClosed();
        int holdability = 0 == this.stmt.getServerCursorId() ? 1 : this.stmt.getExecProps().getHoldability();
        loggerExternal.exiting(this.getClassNameLogging(), "getHoldability", holdability);
        return holdability;
    }

    @Override
    public void insertRow() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "insertRow");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        if (!this.isOnInsertRow) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_mustBeOnInsertRow"), null, true);
        }
        Column tableColumn = null;
        for (Column column : this.columns) {
            if (column.hasUpdates()) {
                tableColumn = column;
                break;
            }
            if (null != tableColumn || !column.isUpdatable()) continue;
            tableColumn = column;
        }
        if (null == tableColumn) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_noColumnParameterValue"), null, true);
        }
        assert (tableColumn.isUpdatable());
        assert (null != tableColumn.getTableName());
        final class InsertRowRPC
        extends TDSCommand {
            private static final long serialVersionUID = 1L;
            final String tableName;

            InsertRowRPC(String tableName) {
                super("InsertRowRPC", 0, 0);
                this.tableName = tableName;
            }

            @Override
            final boolean doExecute() throws SQLServerException {
                SQLServerResultSet.this.doInsertRowRPC(this, this.tableName);
                return true;
            }
        }
        this.stmt.executeCommand(new InsertRowRPC(tableColumn.getTableName().asEscapedString()));
        if (-3 != this.rowCount) {
            ++this.rowCount;
        }
        loggerExternal.exiting(this.getClassNameLogging(), "insertRow");
    }

    private void doInsertRowRPC(TDSCommand command, String tableName) throws SQLServerException {
        assert (0 != this.serverCursorId);
        assert (null != tableName);
        assert (tableName.length() > 0);
        TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 4, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        if (this.hasUpdatedColumns()) {
            tdsWriter.writeRPCStringUnicode(tableName);
            for (Column column : this.columns) {
                column.sendByRPC(tdsWriter, this.stmt);
            }
        } else {
            tdsWriter.writeRPCStringUnicode("");
            tdsWriter.writeRPCStringUnicode("INSERT INTO " + tableName + " DEFAULT VALUES");
        }
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }

    @Override
    public void updateRow() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "updateRow");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        if (!this.hasUpdatedColumns()) {
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, SQLServerException.getErrString("R_noColumnParameterValue"), null, true);
        }
        try {
            final class UpdateRowRPC
            extends TDSCommand {
                private static final long serialVersionUID = 1L;

                UpdateRowRPC() {
                    super("UpdateRowRPC", 0, 0);
                }

                @Override
                final boolean doExecute() throws SQLServerException {
                    SQLServerResultSet.this.doUpdateRowRPC(this);
                    return true;
                }
            }
            this.stmt.executeCommand(new UpdateRowRPC());
        }
        finally {
            this.cancelUpdates();
        }
        this.updatedCurrentRow = true;
        loggerExternal.exiting(this.getClassNameLogging(), "updateRow");
    }

    private void doUpdateRowRPC(TDSCommand command) throws SQLServerException {
        assert (0 != this.serverCursorId);
        TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 33, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        tdsWriter.writeRPCStringUnicode(this.getUpdatedColumnTableName());
        assert (this.hasUpdatedColumns());
        for (Column column : this.columns) {
            column.sendByRPC(tdsWriter, this.stmt);
        }
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }

    final boolean hasUpdatedColumns() {
        for (Column column : this.columns) {
            if (!column.hasUpdates()) continue;
            return true;
        }
        return false;
    }

    private String getUpdatedColumnTableName() throws SQLServerException {
        String columnTableName = "";
        for (Column column : this.columns) {
            if (column.hasUpdates() && columnTableName.isEmpty()) {
                columnTableName = column.getTableName().asEscapedString();
                continue;
            }
            if (!column.hasUpdates() || columnTableName.equals(column.getTableName().asEscapedString())) continue;
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_AmbiguousRowUpdate"));
            Object[] msgArgs = new Object[]{columnTableName, column.getTableName().asEscapedString()};
            SQLServerException.makeFromDriverError(this.stmt.connection, this.stmt, form.format(msgArgs), null, false);
        }
        return columnTableName;
    }

    @Override
    public void deleteRow() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "deleteRow");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        try {
            final class DeleteRowRPC
            extends TDSCommand {
                private static final long serialVersionUID = 1L;

                DeleteRowRPC() {
                    super("DeleteRowRPC", 0, 0);
                }

                @Override
                final boolean doExecute() throws SQLServerException {
                    SQLServerResultSet.this.doDeleteRowRPC(this);
                    return true;
                }
            }
            this.stmt.executeCommand(new DeleteRowRPC());
        }
        finally {
            this.cancelUpdates();
        }
        this.deletedCurrentRow = true;
        loggerExternal.exiting(this.getClassNameLogging(), "deleteRow");
    }

    private void doDeleteRowRPC(TDSCommand command) throws SQLServerException {
        assert (0 != this.serverCursorId);
        TDSWriter tdsWriter = command.startRequest((byte)3);
        tdsWriter.writeShort((short)-1);
        tdsWriter.writeShort((short)1);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeRPCInt(null, this.serverCursorId, false);
        tdsWriter.writeRPCInt(null, 34, false);
        tdsWriter.writeRPCInt(null, this.fetchBufferGetRow(), false);
        tdsWriter.writeRPCStringUnicode("");
        TDSParser.parse(command.startResponse(), command.getLogContext());
    }

    @Override
    public void refreshRow() throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "refreshRow");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsScrollable();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.verifyResultSetHasCurrentRow();
        this.verifyCurrentRowIsNotDeleted("R_cantUpdateDeletedRow");
        if (1004 == this.stmt.getResultSetType() || 0 == this.serverCursorId) {
            return;
        }
        this.cancelUpdates();
        this.doRefreshRow();
        loggerExternal.exiting(this.getClassNameLogging(), "refreshRow");
    }

    private void doRefreshRow() throws SQLServerException {
        int fetchBufferRestoredRow;
        assert (this.hasCurrentRow());
        int fetchBufferSavedRow = this.fetchBufferGetRow();
        this.doServerFetch(128, 0, 0);
        for (fetchBufferRestoredRow = 0; fetchBufferRestoredRow < fetchBufferSavedRow && (this.isForwardOnly() ? this.fetchBufferNext() : this.scrollWindow.next(this)); ++fetchBufferRestoredRow) {
        }
        if (fetchBufferRestoredRow < fetchBufferSavedRow) {
            this.currentRow = -1;
            return;
        }
        this.updatedCurrentRow = false;
    }

    private void cancelUpdates() {
        if (!this.isOnInsertRow) {
            this.clearColumnsValues();
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "cancelRowUpdates");
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.verifyResultSetIsNotOnInsertRow();
        this.cancelUpdates();
        loggerExternal.exiting(this.getClassNameLogging(), "cancelRowUpdates");
    }

    @Override
    public void moveToInsertRow() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "moveToInsertRow");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.cancelUpdates();
        this.isOnInsertRow = true;
        loggerExternal.exiting(this.getClassNameLogging(), "moveToInsertRow");
    }

    @Override
    public void moveToCurrentRow() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "moveToCurrentRow");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + this.logCursorState());
        }
        this.checkClosed();
        this.verifyResultSetIsUpdatable();
        this.cancelInsert();
        loggerExternal.exiting(this.getClassNameLogging(), "moveToCurrentRow");
    }

    @Override
    public Statement getStatement() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getStatement");
        this.checkClosed();
        loggerExternal.exiting(this.getClassNameLogging(), "getStatement", this.stmt);
        return this.stmt;
    }

    @Override
    public void updateClob(int columnIndex, Clob clobValue) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnIndex, clobValue});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.CLOB, clobValue, JavaType.CLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnIndex, reader});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnIndex, reader, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateClob(String columnName, Clob clobValue) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnName, clobValue});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.CLOB, clobValue, JavaType.CLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnLabel, reader});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnLabel, reader, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.CHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateClob");
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateClob", new Object[]{columnIndex, nClob});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.NCLOB, nClob, JavaType.NCLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[]{columnIndex, reader});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[]{columnIndex, reader, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.NCHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[]{columnLabel, nClob});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnLabel), JDBCType.NCLOB, nClob, JavaType.NCLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[]{columnLabel, reader});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateNClob", new Object[]{columnLabel, reader, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.NCHARACTER, reader, JavaType.READER, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateNClob");
    }

    @Override
    public void updateBlob(int columnIndex, Blob blobValue) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnIndex, blobValue});
        }
        this.checkClosed();
        this.updateValue(columnIndex, JDBCType.BLOB, blobValue, JavaType.BLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnIndex, inputStream});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnIndex, inputStream, length});
        }
        this.checkClosed();
        this.updateStream(columnIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateBlob(String columnName, Blob blobValue) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnName, blobValue});
        }
        this.checkClosed();
        this.updateValue(this.findColumn(columnName), JDBCType.BLOB, blobValue, JavaType.BLOB, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnLabel, inputStream});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateBlob", new Object[]{columnLabel, inputStream, length});
        }
        this.checkClosed();
        this.updateStream(this.findColumn(columnLabel), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        loggerExternal.exiting(this.getClassNameLogging(), "updateBlob");
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public void updateArray(String columnName, Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public void updateRef(String columnName, Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    @Override
    public URL getURL(String sColumn) throws SQLException {
        SQLServerException.throwNotSupportedException(this.stmt.connection, this.stmt);
        return null;
    }

    final void doServerFetch(int fetchType, int startRow, int numRows) throws SQLServerException {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " fetchType:" + fetchType + " startRow:" + startRow + " numRows:" + numRows);
        }
        this.discardFetchBuffer();
        this.fetchBuffer.init();
        CursorFetchCommand cursorFetch = new CursorFetchCommand(this.serverCursorId, fetchType, startRow, numRows);
        this.stmt.executeCommand(cursorFetch);
        this.numFetchedRows = 0;
        this.resultSetCurrentRowType = RowType.UNKNOWN;
        this.areNullCompressedColumnsInitialized = false;
        this.lastColumnIndex = 0;
        if (null != this.scrollWindow && 128 != fetchType) {
            this.scrollWindow.resize(this.fetchSize);
        }
        if (numRows < 0 || startRow < 0) {
            block8: {
                try {
                    while (this.scrollWindow != null && this.scrollWindow.next(this)) {
                    }
                }
                catch (SQLException e) {
                    if (!logger.isLoggable(Level.FINER)) break block8;
                    logger.finer(this.toString() + " Ignored exception from row error during server cursor fixup: " + e.getMessage());
                }
            }
            if (this.fetchBuffer.needsServerCursorFixup()) {
                this.doServerFetch(1, 0, 0);
                return;
            }
            if (null != this.scrollWindow) {
                this.scrollWindow.reset();
            }
        }
    }

    private void fillLOBs() {
        if (null != this.activeLOB) {
            try {
                this.activeLOB.fillFromStream();
            }
            catch (SQLException e) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + "Filling Lobs before closing: " + e.getMessage());
                }
            }
            finally {
                this.activeLOB = null;
            }
        }
    }

    private void discardFetchBuffer() {
        block4: {
            this.fillLOBs();
            this.fetchBuffer.clearStartMark();
            if (null != this.scrollWindow) {
                this.scrollWindow.clear();
            }
            try {
                while (this.fetchBufferNext()) {
                }
            }
            catch (SQLServerException e) {
                if (!logger.isLoggable(Level.FINER)) break block4;
                logger.finer(this + " Encountered exception discarding fetch buffer: " + e.getMessage());
            }
        }
    }

    final void closeServerCursor() {
        if (0 == this.serverCursorId) {
            return;
        }
        if (this.stmt.connection.isSessionUnAvailable()) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this + ": Not closing cursor:" + this.serverCursorId + "; connection is already closed.");
            }
        } else {
            block8: {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " Closing cursor:" + this.serverCursorId);
                }
                try {
                    final class CloseServerCursorCommand
                    extends UninterruptableTDSCommand {
                        private static final long serialVersionUID = 1L;

                        CloseServerCursorCommand() {
                            super("closeServerCursor");
                        }

                        @Override
                        final boolean doExecute() throws SQLServerException {
                            TDSWriter tdsWriter = this.startRequest((byte)3);
                            tdsWriter.writeShort((short)-1);
                            tdsWriter.writeShort((short)9);
                            tdsWriter.writeByte((byte)0);
                            tdsWriter.writeByte((byte)0);
                            tdsWriter.sendEnclavePackage(null, null);
                            tdsWriter.writeRPCInt(null, SQLServerResultSet.this.serverCursorId, false);
                            TDSParser.parse(this.startResponse(), this.getLogContext());
                            return true;
                        }
                    }
                    this.stmt.executeCommand(new CloseServerCursorCommand());
                }
                catch (SQLServerException e) {
                    if (!logger.isLoggable(Level.FINER)) break block8;
                    logger.finer(this.toString() + " Ignored error closing cursor:" + this.serverCursorId + " " + e.getMessage());
                }
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(this.toString() + " Closed cursor:" + this.serverCursorId);
            }
        }
    }

    @Override
    public void updateObject(int index, Object obj, SQLType targetSqlType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, obj, targetSqlType});
        }
        this.checkClosed();
        this.updateObject(index, obj, null, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(int index, Object obj, SQLType targetSqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, obj, targetSqlType, scale});
        }
        this.checkClosed();
        this.updateObject(index, obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(int index, Object obj, SQLType targetSqlType, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{index, obj, targetSqlType, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateObject(index, obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object obj, SQLType targetSqlType, int scale) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, obj, targetSqlType, scale});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object obj, SQLType targetSqlType, int scale, boolean forceEncrypt) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, obj, targetSqlType, scale, forceEncrypt});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, scale, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, forceEncrypt);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    @Override
    public void updateObject(String columnName, Object obj, SQLType targetSqlType) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "updateObject", new Object[]{columnName, obj, targetSqlType});
        }
        this.checkClosed();
        this.updateObject(this.findColumn(columnName), obj, null, JDBCType.of(targetSqlType.getVendorTypeNumber()), null, false);
        loggerExternal.exiting(this.getClassNameLogging(), "updateObject");
    }

    private final class CursorFetchCommand
    extends TDSCommand {
        private static final long serialVersionUID = 1L;
        private final int serverCursorId;
        private int fetchType;
        private int startRow;
        private int numRows;

        CursorFetchCommand(int serverCursorId, int fetchType, int startRow, int numRows) {
            super("doServerFetch", SQLServerResultSet.this.stmt.queryTimeout, SQLServerResultSet.this.stmt.cancelQueryTimeoutSeconds);
            this.serverCursorId = serverCursorId;
            this.fetchType = fetchType;
            this.startRow = startRow;
            this.numRows = numRows;
        }

        @Override
        final boolean doExecute() throws SQLServerException {
            TDSWriter tdsWriter = this.startRequest((byte)3);
            tdsWriter.writeShort((short)-1);
            tdsWriter.writeShort((short)7);
            tdsWriter.writeByte((byte)2);
            tdsWriter.writeByte((byte)0);
            tdsWriter.sendEnclavePackage(null, null);
            tdsWriter.writeRPCInt(null, this.serverCursorId, false);
            tdsWriter.writeRPCInt(null, this.fetchType, false);
            tdsWriter.writeRPCInt(null, this.startRow, false);
            tdsWriter.writeRPCInt(null, this.numRows, false);
            SQLServerResultSet.this.tdsReader = this.startResponse(SQLServerResultSet.this.isForwardOnly() && 1007 != SQLServerResultSet.this.stmt.resultSetConcurrency && SQLServerResultSet.this.stmt.getExecProps().wasResponseBufferingSet() && SQLServerResultSet.this.stmt.getExecProps().isResponseBufferingAdaptive());
            return false;
        }

        @Override
        final void processResponse(TDSReader responseTDSReader) throws SQLServerException {
            SQLServerResultSet.this.tdsReader = responseTDSReader;
            SQLServerResultSet.this.discardFetchBuffer();
        }
    }

    private final class FetchBuffer {
        private final FetchBufferTokenHandler fetchBufferTokenHandler = new FetchBufferTokenHandler();
        private TDSReaderMark startMark;
        private RowType fetchBufferCurrentRowType = RowType.UNKNOWN;
        private boolean done;
        private boolean needsServerCursorFixup;

        final void clearStartMark() {
            this.startMark = null;
        }

        final boolean needsServerCursorFixup() {
            return this.needsServerCursorFixup;
        }

        FetchBuffer() {
            this.init();
        }

        final void ensureStartMark() {
            if (null == this.startMark && !SQLServerResultSet.this.isForwardOnly()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(super.toString() + " Setting fetch buffer start mark");
                }
                this.startMark = SQLServerResultSet.this.tdsReader.mark();
            }
        }

        final void reset() {
            assert (null != SQLServerResultSet.this.tdsReader);
            assert (null != this.startMark);
            SQLServerResultSet.this.tdsReader.reset(this.startMark);
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            this.done = false;
        }

        final void init() {
            this.startMark = 0 == SQLServerResultSet.this.serverCursorId && !SQLServerResultSet.this.isForwardOnly() ? SQLServerResultSet.this.tdsReader.mark() : null;
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            this.done = false;
            this.needsServerCursorFixup = false;
        }

        final RowType nextRow() throws SQLServerException {
            this.fetchBufferCurrentRowType = RowType.UNKNOWN;
            while (null != SQLServerResultSet.this.tdsReader && !this.done && this.fetchBufferCurrentRowType.equals((Object)RowType.UNKNOWN)) {
                TDSParser.parse(SQLServerResultSet.this.tdsReader, this.fetchBufferTokenHandler);
            }
            if (null != this.fetchBufferTokenHandler.getDatabaseError()) {
                SQLServerException.makeFromDatabaseError(SQLServerResultSet.this.stmt.connection, null, this.fetchBufferTokenHandler.getDatabaseError().getErrorMessage(), this.fetchBufferTokenHandler.getDatabaseError(), false);
            }
            return this.fetchBufferCurrentRowType;
        }

        private final class FetchBufferTokenHandler
        extends TDSTokenHandler {
            FetchBufferTokenHandler() {
                super("FetchBufferTokenHandler");
            }

            @Override
            boolean onColMetaData(TDSReader tdsReader) throws SQLServerException {
                new StreamColumns(Util.shouldHonorAEForRead(SQLServerResultSet.this.stmt.stmtColumnEncriptionSetting, SQLServerResultSet.this.stmt.connection)).setFromTDS(tdsReader);
                return true;
            }

            @Override
            boolean onRow(TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                if (209 != tdsReader.readUnsignedByte()) assert (false);
                FetchBuffer.this.fetchBufferCurrentRowType = RowType.ROW;
                return false;
            }

            @Override
            boolean onNBCRow(TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                if (210 != tdsReader.readUnsignedByte()) assert (false);
                FetchBuffer.this.fetchBufferCurrentRowType = RowType.NBCROW;
                return false;
            }

            @Override
            boolean onDone(TDSReader tdsReader) throws SQLServerException {
                FetchBuffer.this.ensureStartMark();
                StreamDone doneToken = new StreamDone();
                doneToken.setFromTDS(tdsReader);
                if (doneToken.isFinal()) {
                    SQLServerResultSet.this.stmt.connection.getSessionRecovery().decrementUnprocessedResponseCount();
                }
                if (doneToken.isFinal() && doneToken.isError()) {
                    short status = tdsReader.peekStatusFlag();
                    SQLServerError databaseError = this.getDatabaseError();
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverError"));
                    Object[] msgArgs = new Object[]{status, databaseError != null ? databaseError.getErrorMessage() : ""};
                    if (null != databaseError) {
                        SQLServerException.makeFromDatabaseError(SQLServerResultSet.this.stmt.connection, null, form.format(msgArgs), databaseError, false);
                    } else {
                        SQLServerException.makeFromDriverError(SQLServerResultSet.this.stmt.connection, SQLServerResultSet.this.stmt, form.format(msgArgs), null, false);
                    }
                }
                FetchBuffer.this.done = true;
                return 0 != SQLServerResultSet.this.serverCursorId;
            }

            @Override
            boolean onRetStatus(TDSReader tdsReader) throws SQLServerException {
                StreamRetStatus retStatusToken = new StreamRetStatus();
                retStatusToken.setFromTDS(tdsReader);
                FetchBuffer.this.needsServerCursorFixup = 2 == retStatusToken.getStatus();
                return true;
            }

            @Override
            void onEOF(TDSReader tdsReader) throws SQLServerException {
                super.onEOF(tdsReader);
                FetchBuffer.this.done = true;
            }

            @Override
            boolean onDataClassification(TDSReader tdsReader) throws SQLServerException {
                if (tdsReader.getServerSupportsDataClassification()) {
                    tdsReader.trySetSensitivityClassification(new StreamColumns(Util.shouldHonorAEForRead(SQLServerResultSet.this.stmt.stmtColumnEncriptionSetting, SQLServerResultSet.this.stmt.connection)).processDataClassification(tdsReader));
                }
                return true;
            }
        }
    }
}

