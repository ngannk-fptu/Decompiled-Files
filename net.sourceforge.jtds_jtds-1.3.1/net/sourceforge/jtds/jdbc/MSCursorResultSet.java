/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLWarning;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.JtdsResultSet;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsCore;
import net.sourceforge.jtds.jdbc.TdsData;

public class MSCursorResultSet
extends JtdsResultSet {
    private static final Integer FETCH_FIRST = new Integer(1);
    private static final Integer FETCH_NEXT = new Integer(2);
    private static final Integer FETCH_PREVIOUS = new Integer(4);
    private static final Integer FETCH_LAST = new Integer(8);
    private static final Integer FETCH_ABSOLUTE = new Integer(16);
    private static final Integer FETCH_RELATIVE = new Integer(32);
    private static final Integer FETCH_REPEAT = new Integer(128);
    private static final Integer FETCH_INFO = new Integer(256);
    private static final int CURSOR_TYPE_KEYSET = 1;
    private static final int CURSOR_TYPE_DYNAMIC = 2;
    private static final int CURSOR_TYPE_FORWARD = 4;
    private static final int CURSOR_TYPE_STATIC = 8;
    private static final int CURSOR_TYPE_FASTFORWARDONLY = 16;
    private static final int CURSOR_TYPE_PARAMETERIZED = 4096;
    private static final int CURSOR_TYPE_AUTO_FETCH = 8192;
    private static final int CURSOR_CONCUR_READ_ONLY = 1;
    private static final int CURSOR_CONCUR_SCROLL_LOCKS = 2;
    private static final int CURSOR_CONCUR_OPTIMISTIC = 4;
    private static final int CURSOR_CONCUR_OPTIMISTIC_VALUES = 8;
    private static final Integer CURSOR_OP_INSERT = new Integer(4);
    private static final Integer CURSOR_OP_UPDATE = new Integer(33);
    private static final Integer CURSOR_OP_DELETE = new Integer(34);
    private static final Integer SQL_ROW_DIRTY = new Integer(0);
    private static final Integer SQL_ROW_SUCCESS = new Integer(1);
    private static final Integer SQL_ROW_DELETED = new Integer(2);
    private boolean onInsertRow;
    private ParamInfo[] insertRow;
    private ParamInfo[] updateRow;
    private Object[][] rowCache;
    private int cursorPos;
    private boolean asyncCursor;
    private final ParamInfo PARAM_CURSOR_HANDLE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_FETCHTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_NUMROWS_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_NUMROWS_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_OPTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM = new ParamInfo(4, new Integer(1), 0);
    private final ParamInfo PARAM_TABLE = new ParamInfo(12, "", 4);

    MSCursorResultSet(JtdsStatement statement, String sql, String procName, ParamInfo[] procedureParams, int resultSetType, int concurrency) throws SQLException {
        super(statement, resultSetType, concurrency, null);
        this.PARAM_NUMROWS_IN.value = new Integer(this.fetchSize);
        this.rowCache = new Object[this.fetchSize][];
        this.cursorCreate(sql, procName, procedureParams);
        if (this.asyncCursor) {
            this.cursorFetch(FETCH_REPEAT, 0);
        }
    }

    @Override
    protected Object setColValue(int colIndex, int jdbcType, Object value, int length) throws SQLException {
        ParamInfo pi;
        value = super.setColValue(colIndex, jdbcType, value, length);
        if (!this.onInsertRow && this.getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        ColInfo ci = this.columns[--colIndex];
        if (this.onInsertRow) {
            pi = this.insertRow[colIndex];
        } else {
            if (this.updateRow == null) {
                this.updateRow = new ParamInfo[this.columnCount];
            }
            pi = this.updateRow[colIndex];
        }
        if (pi == null) {
            pi = new ParamInfo(-1, TdsData.isUnicode(ci));
            pi.name = '@' + ci.realName;
            pi.collation = ci.collation;
            pi.charsetInfo = ci.charsetInfo;
            if (this.onInsertRow) {
                this.insertRow[colIndex] = pi;
            } else {
                this.updateRow[colIndex] = pi;
            }
        }
        if (value == null) {
            pi.value = null;
            pi.length = 0;
            pi.jdbcType = ci.jdbcType;
            pi.isSet = true;
            pi.scale = pi.jdbcType == 2 || pi.jdbcType == 3 ? 10 : 0;
        } else {
            pi.value = value;
            pi.length = length;
            pi.isSet = true;
            pi.jdbcType = jdbcType;
            pi.isUnicode = "ntext".equals(ci.sqlType) || "nchar".equals(ci.sqlType) || "nvarchar".equals(ci.sqlType);
            pi.scale = pi.value instanceof BigDecimal ? ((BigDecimal)pi.value).scale() : 0;
        }
        return value;
    }

    @Override
    protected Object getColumn(int index) throws SQLException {
        Object data;
        Object[] currentRow;
        this.checkOpen();
        if (index < 1 || index > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(index)), "07009");
        }
        if (this.onInsertRow || (currentRow = this.getCurrentRow()) == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
            this.cursorFetch(FETCH_REPEAT, 0);
            currentRow = this.getCurrentRow();
        }
        this.wasNull = (data = currentRow[index - 1]) == null;
        return data;
    }

    static int getCursorScrollOpt(int resultSetType, int resultSetConcurrency, boolean parameterized) {
        int scrollOpt;
        switch (resultSetType) {
            case 1004: {
                scrollOpt = 8;
                break;
            }
            case 1005: {
                scrollOpt = 1;
                break;
            }
            case 1006: {
                scrollOpt = 2;
                break;
            }
            default: {
                int n = scrollOpt = resultSetConcurrency == 1007 ? 8208 : 4;
            }
        }
        if (parameterized) {
            scrollOpt |= 0x1000;
        }
        return scrollOpt;
    }

    static int getCursorConcurrencyOpt(int resultSetConcurrency) {
        switch (resultSetConcurrency) {
            case 1008: {
                return 4;
            }
            case 1009: {
                return 2;
            }
            case 1010: {
                return 8;
            }
        }
        return 1;
    }

    private void cursorCreate(String sql, String procName, ParamInfo[] parameters) throws SQLException {
        Integer retVal;
        ParamInfo[] params;
        TdsCore tds = this.statement.getTds();
        int prepareSql = this.statement.connection.getPrepareSql();
        Integer prepStmtHandle = null;
        if (this.cursorName != null && this.resultSetType == 1003 && this.concurrency == 1007) {
            this.concurrency = 1008;
        }
        if (parameters != null && parameters.length == 0) {
            parameters = null;
        }
        if (tds.getTdsVersion() == 1) {
            prepareSql = 0;
            if (parameters != null) {
                procName = null;
            }
        }
        if (parameters != null && prepareSql == 0) {
            sql = Support.substituteParameters(sql, parameters, this.statement.connection);
            parameters = null;
        }
        if (!(parameters == null || procName != null && procName.startsWith("#jtds"))) {
            sql = Support.substituteParamMarkers(sql, parameters);
        }
        if (procName != null) {
            if (procName.startsWith("#jtds")) {
                StringBuilder buf = new StringBuilder(procName.length() + 16 + (parameters != null ? parameters.length * 5 : 0));
                buf.append("EXEC ").append(procName).append(' ');
                for (int i = 0; parameters != null && i < parameters.length; ++i) {
                    if (i != 0) {
                        buf.append(',');
                    }
                    if (parameters[i].name != null) {
                        buf.append(parameters[i].name);
                        continue;
                    }
                    buf.append("@P").append(i);
                }
                sql = buf.toString();
            } else if (TdsCore.isPreparedProcedureName(procName)) {
                try {
                    prepStmtHandle = new Integer(procName);
                }
                catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid prepared statement handle: " + procName);
                }
            }
        }
        int scrollOpt = MSCursorResultSet.getCursorScrollOpt(this.resultSetType, this.concurrency, parameters != null);
        int ccOpt = MSCursorResultSet.getCursorConcurrencyOpt(this.concurrency);
        ParamInfo pScrollOpt = new ParamInfo(4, new Integer(scrollOpt), 1);
        ParamInfo pConCurOpt = new ParamInfo(4, new Integer(ccOpt), 1);
        ParamInfo pRowCount = new ParamInfo(4, new Integer(this.fetchSize), 1);
        ParamInfo pCursor = new ParamInfo(4, null, 1);
        ParamInfo pStmtHand = null;
        if (prepareSql == 3) {
            pStmtHand = new ParamInfo(4, prepStmtHandle, 1);
        }
        ParamInfo pParamDef = null;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                TdsData.getNativeType(this.statement.connection, parameters[i]);
            }
            pParamDef = new ParamInfo(-1, Support.getParameterDefinitions(parameters), 4);
        }
        ParamInfo pSQL = new ParamInfo(-1, sql, 4);
        if (prepareSql == 3 && prepStmtHandle != null) {
            procName = "sp_cursorexecute";
            if (parameters == null) {
                parameters = new ParamInfo[5];
            } else {
                params = new ParamInfo[5 + parameters.length];
                System.arraycopy(parameters, 0, params, 5, parameters.length);
                parameters = params;
            }
            pStmtHand.isOutput = false;
            pStmtHand.value = prepStmtHandle;
            parameters[0] = pStmtHand;
            parameters[1] = pCursor;
            pScrollOpt.value = new Integer(scrollOpt & 0xFFFFEFFF);
        } else {
            procName = "sp_cursoropen";
            if (parameters == null) {
                parameters = new ParamInfo[5];
            } else {
                params = new ParamInfo[6 + parameters.length];
                System.arraycopy(parameters, 0, params, 6, parameters.length);
                parameters = params;
                parameters[5] = pParamDef;
            }
            parameters[0] = pCursor;
            parameters[1] = pSQL;
        }
        parameters[2] = pScrollOpt;
        parameters[3] = pConCurOpt;
        parameters[4] = pRowCount;
        tds.executeSQL(null, procName, parameters, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
        this.processOutput(tds, true);
        if ((scrollOpt & 0x2000) != 0) {
            this.cursorPos = 1;
        }
        if ((retVal = tds.getReturnStatus()) == null || retVal != 0 && retVal != 2) {
            throw new SQLException(Messages.get("error.resultset.openfail"), "24000");
        }
        this.asyncCursor = retVal == 2;
        this.PARAM_CURSOR_HANDLE.value = pCursor.getOutValue();
        int actualScroll = (Integer)pScrollOpt.getOutValue();
        int actualCc = (Integer)pConCurOpt.getOutValue();
        this.rowsInResult = (Integer)pRowCount.getOutValue();
        if (this.cursorName != null) {
            ParamInfo[] params2 = new ParamInfo[3];
            params2[0] = this.PARAM_CURSOR_HANDLE;
            this.PARAM_OPTYPE.value = new Integer(2);
            params2[1] = this.PARAM_OPTYPE;
            params2[2] = new ParamInfo(12, this.cursorName, 4);
            tds.executeSQL(null, "sp_cursoroption", params2, true, 0, -1, -1, true);
            tds.clearResponseQueue();
            if (tds.getReturnStatus() != 0) {
                this.statement.getMessages().addException(new SQLException(Messages.get("error.resultset.openfail"), "24000"));
            }
            this.statement.getMessages().checkErrors();
        }
        if (actualScroll != (scrollOpt & 0xFFF) || actualCc != ccOpt) {
            boolean downgradeWarning = false;
            if (actualScroll != scrollOpt) {
                int resultSetType;
                switch (actualScroll) {
                    case 4: 
                    case 16: {
                        resultSetType = 1003;
                        break;
                    }
                    case 8: {
                        resultSetType = 1004;
                        break;
                    }
                    case 1: {
                        resultSetType = 1005;
                        break;
                    }
                    case 2: {
                        resultSetType = 1006;
                        break;
                    }
                    default: {
                        resultSetType = this.resultSetType;
                        this.statement.getMessages().addWarning(new SQLWarning(Messages.get("warning.cursortype", Integer.toString(actualScroll)), "01000"));
                    }
                }
                downgradeWarning = resultSetType < this.resultSetType;
                this.resultSetType = resultSetType;
            }
            if (actualCc != ccOpt) {
                int concurrency;
                switch (actualCc) {
                    case 1: {
                        concurrency = 1007;
                        break;
                    }
                    case 4: {
                        concurrency = 1008;
                        break;
                    }
                    case 2: {
                        concurrency = 1009;
                        break;
                    }
                    case 8: {
                        concurrency = 1010;
                        break;
                    }
                    default: {
                        concurrency = this.concurrency;
                        this.statement.getMessages().addWarning(new SQLWarning(Messages.get("warning.concurrtype", Integer.toString(actualCc)), "01000"));
                    }
                }
                downgradeWarning = concurrency < this.concurrency;
                this.concurrency = concurrency;
            }
            if (downgradeWarning) {
                this.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", this.resultSetType + "/" + this.concurrency), "01000"));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean cursorFetch(Integer fetchType, int rowNum) throws SQLException {
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        if (fetchType != FETCH_ABSOLUTE && fetchType != FETCH_RELATIVE) {
            rowNum = 1;
        }
        ParamInfo[] param = new ParamInfo[4];
        param[0] = this.PARAM_CURSOR_HANDLE;
        this.PARAM_FETCHTYPE.value = fetchType;
        param[1] = this.PARAM_FETCHTYPE;
        this.PARAM_ROWNUM_IN.value = new Integer(rowNum);
        param[2] = this.PARAM_ROWNUM_IN;
        if ((Integer)this.PARAM_NUMROWS_IN.value != this.fetchSize) {
            this.PARAM_NUMROWS_IN.value = new Integer(this.fetchSize);
            this.rowCache = new Object[this.fetchSize][];
        }
        param[3] = this.PARAM_NUMROWS_IN;
        TdsCore tdsCore = tds;
        synchronized (tdsCore) {
            tds.executeSQL(null, "sp_cursorfetch", param, true, 0, 0, this.statement.getMaxFieldSize(), false);
            this.PARAM_FETCHTYPE.value = FETCH_INFO;
            param[1] = this.PARAM_FETCHTYPE;
            this.PARAM_ROWNUM_OUT.clearOutValue();
            param[2] = this.PARAM_ROWNUM_OUT;
            this.PARAM_NUMROWS_OUT.clearOutValue();
            param[3] = this.PARAM_NUMROWS_OUT;
            tds.executeSQL(null, "sp_cursorfetch", param, true, this.statement.getQueryTimeout(), -1, -1, true);
        }
        this.processOutput(tds, false);
        this.cursorPos = (Integer)this.PARAM_ROWNUM_OUT.getOutValue();
        if (fetchType != FETCH_REPEAT) {
            this.pos = this.cursorPos;
        }
        this.rowsInResult = (Integer)this.PARAM_NUMROWS_OUT.getOutValue();
        if (this.rowsInResult < 0) {
            this.rowsInResult = 0 - this.rowsInResult;
        }
        return this.getCurrentRow() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cursor(Integer opType, ParamInfo[] row) throws SQLException {
        ParamInfo[] param;
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        if (opType == CURSOR_OP_DELETE) {
            param = new ParamInfo[3];
        } else {
            if (row == null) {
                throw new SQLException(Messages.get("error.resultset.update"), "24000");
            }
            param = new ParamInfo[4 + this.columnCount];
        }
        param[0] = this.PARAM_CURSOR_HANDLE;
        this.PARAM_OPTYPE.value = opType;
        param[1] = this.PARAM_OPTYPE;
        this.PARAM_ROWNUM.value = new Integer(this.pos - this.cursorPos + 1);
        param[2] = this.PARAM_ROWNUM;
        if (row != null) {
            param[3] = this.PARAM_TABLE;
            int colCnt = this.columnCount;
            int crtCol = 4;
            String tableName = null;
            for (int i = 0; i < colCnt; ++i) {
                ParamInfo pi = row[i];
                ColInfo col = this.columns[i];
                if (pi != null && pi.isSet) {
                    if (!col.isWriteable) {
                        throw new SQLException(Messages.get("error.resultset.insert", Integer.toString(i + 1), col.realName), "24000");
                    }
                    param[crtCol++] = pi;
                }
                if (tableName != null || col.tableName == null) continue;
                tableName = col.catalog != null || col.schema != null ? (col.catalog != null ? col.catalog : "") + '.' + (col.schema != null ? col.schema : "") + '.' + col.tableName : col.tableName;
            }
            if (crtCol == 4) {
                if (opType == CURSOR_OP_INSERT) {
                    param[crtCol] = new ParamInfo(12, "insert " + tableName + " default values", 4);
                    ++crtCol;
                } else {
                    return;
                }
            }
            if (crtCol != colCnt + 4) {
                ParamInfo[] newParam = new ParamInfo[crtCol];
                System.arraycopy(param, 0, newParam, 0, crtCol);
                param = newParam;
            }
        }
        TdsCore colCnt = tds;
        synchronized (colCnt) {
            tds.executeSQL(null, "sp_cursor", param, false, 0, -1, -1, false);
            if (param.length != 4) {
                param = new ParamInfo[4];
                param[0] = this.PARAM_CURSOR_HANDLE;
            }
            this.PARAM_FETCHTYPE.value = FETCH_INFO;
            param[1] = this.PARAM_FETCHTYPE;
            this.PARAM_ROWNUM_OUT.clearOutValue();
            param[2] = this.PARAM_ROWNUM_OUT;
            this.PARAM_NUMROWS_OUT.clearOutValue();
            param[3] = this.PARAM_NUMROWS_OUT;
            tds.executeSQL(null, "sp_cursorfetch", param, true, this.statement.getQueryTimeout(), -1, -1, true);
        }
        tds.consumeOneResponse();
        this.statement.getMessages().checkErrors();
        Integer retVal = tds.getReturnStatus();
        if (retVal != 0) {
            throw new SQLException(Messages.get("error.resultset.cursorfail"), "24000");
        }
        if (row != null) {
            for (int i = 0; i < row.length; ++i) {
                if (row[i] == null) continue;
                row[i].clearInValue();
            }
        }
        tds.clearResponseQueue();
        this.statement.getMessages().checkErrors();
        this.cursorPos = (Integer)this.PARAM_ROWNUM_OUT.getOutValue();
        this.rowsInResult = (Integer)this.PARAM_NUMROWS_OUT.getOutValue();
        if (opType == CURSOR_OP_DELETE || opType == CURSOR_OP_UPDATE) {
            Object[] currentRow = this.getCurrentRow();
            if (currentRow == null) {
                throw new SQLException(Messages.get("error.resultset.updatefail"), "24000");
            }
            currentRow[this.columns.length - 1] = opType == CURSOR_OP_DELETE ? SQL_ROW_DELETED : SQL_ROW_DIRTY;
        }
    }

    private void cursorClose() throws SQLException {
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        tds.clearResponseQueue();
        SQLException ex = this.statement.getMessages().exceptions;
        ParamInfo[] param = new ParamInfo[]{this.PARAM_CURSOR_HANDLE};
        tds.executeSQL(null, "sp_cursorclose", param, false, this.statement.getQueryTimeout(), -1, -1, true);
        tds.clearResponseQueue();
        if (ex != null) {
            ex.setNextException(this.statement.getMessages().exceptions);
            throw ex;
        }
        this.statement.getMessages().checkErrors();
    }

    private void processOutput(TdsCore tds, boolean setMeta) throws SQLException {
        while (!tds.getMoreResults() && !tds.isEndOfResponse()) {
        }
        int i = 0;
        if (tds.isResultSet()) {
            if (setMeta) {
                this.columns = this.copyInfo(tds.getColumns());
                this.columnCount = MSCursorResultSet.getColumnCount(this.columns);
            }
            if (tds.isRowData() || tds.getNextRow()) {
                do {
                    this.rowCache[i++] = this.copyRow(tds.getRowData());
                } while (tds.getNextRow());
            }
        } else if (setMeta) {
            this.statement.getMessages().addException(new SQLException(Messages.get("error.statement.noresult"), "24000"));
        }
        while (i < this.rowCache.length) {
            this.rowCache[i] = null;
            ++i;
        }
        tds.clearResponseQueue();
        this.statement.messages.checkErrors();
    }

    @Override
    public void afterLast() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        if (this.pos != -1) {
            this.cursorFetch(FETCH_ABSOLUTE, Integer.MAX_VALUE);
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        if (this.pos != 0) {
            this.cursorFetch(FETCH_ABSOLUTE, 0);
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        for (int i = 0; this.updateRow != null && i < this.updateRow.length; ++i) {
            if (this.updateRow[i] == null) continue;
            this.updateRow[i].clearInValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!this.statement.getConnection().isClosed()) {
                    this.cursorClose();
                }
            }
            finally {
                this.closed = true;
                this.statement = null;
            }
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (this.getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        this.cursor(CURSOR_OP_DELETE, null);
    }

    @Override
    public void insertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (!this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.notinsrow"), "24000");
        }
        this.cursor(CURSOR_OP_INSERT, this.insertRow);
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        this.onInsertRow = false;
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (this.insertRow == null) {
            this.insertRow = new ParamInfo[this.columnCount];
        }
        this.onInsertRow = true;
    }

    @Override
    public void refreshRow() throws SQLException {
        this.checkOpen();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        this.cursorFetch(FETCH_REPEAT, 0);
    }

    @Override
    public void updateRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (this.getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        if (this.updateRow != null) {
            this.cursor(CURSOR_OP_UPDATE, this.updateRow);
        }
    }

    @Override
    public boolean first() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        this.pos = 1;
        if (this.getCurrentRow() == null) {
            return this.cursorFetch(FETCH_FIRST, 0);
        }
        return true;
    }

    @Override
    public boolean isLast() throws SQLException {
        this.checkOpen();
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    @Override
    public boolean last() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        this.pos = this.rowsInResult;
        if (this.asyncCursor || this.getCurrentRow() == null) {
            if (this.cursorFetch(FETCH_LAST, 0)) {
                this.pos = this.rowsInResult;
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean next() throws SQLException {
        this.checkOpen();
        ++this.pos;
        if (this.getCurrentRow() == null) {
            return this.cursorFetch(FETCH_NEXT, 0);
        }
        return true;
    }

    @Override
    public boolean previous() throws SQLException {
        int initPos;
        this.checkOpen();
        this.checkScrollable();
        if (this.pos == 0) {
            return false;
        }
        if ((initPos = this.pos--) == -1 || this.getCurrentRow() == null) {
            boolean res = this.cursorFetch(FETCH_PREVIOUS, 0);
            this.pos = initPos == -1 ? this.rowsInResult : initPos - 1;
            return res;
        }
        return true;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        this.checkOpen();
        Object[] currentRow = this.getCurrentRow();
        if (currentRow == null) {
            return false;
        }
        if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
            this.cursorFetch(FETCH_REPEAT, 0);
            currentRow = this.getCurrentRow();
        }
        return SQL_ROW_DELETED.equals(currentRow[this.columns.length - 1]);
    }

    @Override
    public boolean rowInserted() throws SQLException {
        this.checkOpen();
        return false;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        this.checkOpen();
        return false;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        int n = this.pos = row >= 0 ? row : this.rowsInResult - row + 1;
        if (this.getCurrentRow() == null) {
            boolean result = this.cursorFetch(FETCH_ABSOLUTE, row);
            if (this.cursorPos == 1 && row + this.rowsInResult < 0) {
                this.pos = 0;
                result = false;
            }
            return result;
        }
        return true;
    }

    @Override
    public boolean relative(int row) throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        int n = this.pos = this.pos == -1 ? this.rowsInResult + 1 + row : this.pos + row;
        if (this.getCurrentRow() == null) {
            if (this.pos < this.cursorPos) {
                int savePos = this.pos;
                boolean result = this.cursorFetch(FETCH_RELATIVE, this.pos - this.cursorPos - this.fetchSize + 1);
                this.pos = result ? savePos : 0;
                return result;
            }
            return this.cursorFetch(FETCH_RELATIVE, this.pos - this.cursorPos);
        }
        return true;
    }

    @Override
    protected Object[] getCurrentRow() {
        if (this.pos < this.cursorPos || this.pos >= this.cursorPos + this.rowCache.length) {
            return null;
        }
        return this.rowCache[this.pos - this.cursorPos];
    }
}

