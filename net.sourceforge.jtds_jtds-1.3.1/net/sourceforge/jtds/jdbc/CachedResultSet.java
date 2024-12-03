/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashSet;
import net.sourceforge.jtds.jdbc.BlobImpl;
import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsResultSet;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.SQLParser;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsCore;
import net.sourceforge.jtds.jdbc.TdsData;

public class CachedResultSet
extends JtdsResultSet {
    protected boolean onInsertRow;
    protected ParamInfo[] insertRow;
    protected ParamInfo[] updateRow;
    protected boolean rowUpdated;
    protected boolean rowDeleted;
    protected final boolean tempResultSet;
    protected final TdsCore cursorTds;
    protected final TdsCore updateTds;
    protected boolean isSybase;
    protected boolean sizeChanged;
    protected String sql;
    protected final String procName;
    protected final ParamInfo[] procedureParams;
    protected boolean isKeyed;
    protected String tableName;
    protected JtdsConnection connection;

    CachedResultSet(JtdsStatement statement, String sql, String procName, ParamInfo[] procedureParams, int resultSetType, int concurrency) throws SQLException {
        super(statement, resultSetType, concurrency, null);
        this.connection = (JtdsConnection)statement.getConnection();
        this.cursorTds = statement.getTds();
        this.sql = sql;
        this.procName = procName;
        this.procedureParams = procedureParams;
        this.updateTds = resultSetType == 1003 && concurrency != 1007 && this.cursorName != null ? new TdsCore(this.connection, statement.getMessages()) : this.cursorTds;
        this.isSybase = 2 == this.connection.getServerType();
        this.tempResultSet = false;
        this.cursorCreate();
    }

    CachedResultSet(JtdsStatement statement, String[] colName, int[] colType) throws SQLException {
        super(statement, 1003, 1008, null);
        this.columns = new ColInfo[colName.length];
        for (int i = 0; i < colName.length; ++i) {
            ColInfo ci = new ColInfo();
            ci.name = colName[i];
            ci.realName = colName[i];
            ci.jdbcType = colType[i];
            ci.isCaseSensitive = false;
            ci.isIdentity = false;
            ci.isWriteable = false;
            ci.nullable = 2;
            ci.scale = 0;
            TdsData.fillInType(ci);
            this.columns[i] = ci;
        }
        this.columnCount = CachedResultSet.getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    CachedResultSet(JtdsResultSet rs, boolean load) throws SQLException {
        super((JtdsStatement)rs.getStatement(), rs.getStatement().getResultSetType(), rs.getStatement().getResultSetConcurrency(), null);
        JtdsStatement stmt = (JtdsStatement)rs.getStatement();
        if (this.concurrency != 1007) {
            this.concurrency = 1007;
            stmt.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", "CONCUR_READ_ONLY"), "01000"));
        }
        if (this.resultSetType >= 1005) {
            this.resultSetType = 1004;
            stmt.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", "TYPE_SCROLL_INSENSITIVE"), "01000"));
        }
        this.columns = rs.getColumns();
        this.columnCount = CachedResultSet.getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
        if (load) {
            while (rs.next()) {
                this.rowData.add(this.copyRow(rs.getCurrentRow()));
            }
            this.rowsInResult = this.rowData.size();
        }
    }

    CachedResultSet(JtdsStatement statement, ColInfo[] columns, Object[] data) throws SQLException {
        super(statement, 1003, 1007, null);
        this.columns = columns;
        this.columnCount = CachedResultSet.getColumnCount(columns);
        this.rowData = new ArrayList(1);
        this.rowsInResult = 1;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.rowData.add(this.copyRow(data));
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    void addRow(Object[] data) {
        ++this.rowsInResult;
        this.rowData.add(this.copyRow(data));
    }

    void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    private void cursorCreate() throws SQLException {
        SQLException ex;
        boolean isSelect = false;
        int requestedConcurrency = this.concurrency;
        int requestedType = this.resultSetType;
        if (this.cursorName == null && this.connection.getUseCursors() && this.resultSetType == 1003 && this.concurrency == 1007) {
            this.cursorName = this.connection.getCursorName();
        }
        if (this.resultSetType != 1003 || this.concurrency != 1007 || this.cursorName != null) {
            String[] tmp = SQLParser.parse(this.sql, new ArrayList(), (JtdsConnection)this.statement.getConnection(), true);
            if ("select".equals(tmp[2])) {
                isSelect = true;
                if (tmp[3] != null && tmp[3].length() > 0) {
                    this.tableName = tmp[3];
                } else {
                    this.concurrency = 1007;
                }
            } else {
                this.cursorName = null;
                this.concurrency = 1007;
                if (this.resultSetType != 1003) {
                    this.resultSetType = 1004;
                }
            }
        }
        if (this.cursorName != null) {
            StringBuilder cursorSQL = new StringBuilder(this.sql.length() + this.cursorName.length() + 128);
            cursorSQL.append("DECLARE ").append(this.cursorName).append(" CURSOR FOR ");
            ParamInfo[] parameters = this.procedureParams;
            if (this.procedureParams != null && this.procedureParams.length > 0) {
                parameters = new ParamInfo[this.procedureParams.length];
                int offset = cursorSQL.length();
                for (int i = 0; i < parameters.length; ++i) {
                    parameters[i] = (ParamInfo)this.procedureParams[i].clone();
                    parameters[i].markerPos += offset;
                }
            }
            cursorSQL.append(this.sql);
            this.cursorTds.executeSQL(cursorSQL.toString(), null, parameters, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            this.cursorTds.clearResponseQueue();
            this.cursorTds.getMessages().checkErrors();
            cursorSQL.setLength(0);
            cursorSQL.append("\r\nOPEN ").append(this.cursorName);
            if (this.fetchSize > 1 && this.isSybase) {
                cursorSQL.append("\r\nSET CURSOR ROWS ").append(this.fetchSize);
                cursorSQL.append(" FOR ").append(this.cursorName);
            }
            cursorSQL.append("\r\nFETCH ").append(this.cursorName);
            this.cursorTds.executeSQL(cursorSQL.toString(), null, null, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
            }
            if (!this.cursorTds.isResultSet()) {
                SQLException ex2 = new SQLException(Messages.get("error.statement.noresult"), "24000");
                ex2.setNextException(this.statement.getMessages().exceptions);
                throw ex2;
            }
            this.columns = this.cursorTds.getColumns();
            if (this.connection.getServerType() == 1 && this.columns.length > 0) {
                this.columns[this.columns.length - 1].isHidden = true;
            }
            this.columnCount = CachedResultSet.getColumnCount(this.columns);
            this.rowsInResult = this.cursorTds.isDataInResultSet() ? 1 : 0;
        } else if (isSelect && (this.concurrency != 1007 || this.resultSetType >= 1005)) {
            this.cursorTds.executeSQL(this.sql + " FOR BROWSE", null, this.procedureParams, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
            }
            if (!this.cursorTds.isResultSet()) {
                ex = new SQLException(Messages.get("error.statement.noresult"), "24000");
                ex.setNextException(this.statement.getMessages().exceptions);
                throw ex;
            }
            this.columns = this.cursorTds.getColumns();
            this.columnCount = CachedResultSet.getColumnCount(this.columns);
            this.rowData = new ArrayList(1000);
            this.cacheResultSetRows();
            this.rowsInResult = this.rowData.size();
            this.pos = 0;
            if (!this.isCursorUpdateable()) {
                this.concurrency = 1007;
                if (this.resultSetType != 1003) {
                    this.resultSetType = 1004;
                }
            }
        } else {
            this.cursorTds.executeSQL(this.sql, this.procName, this.procedureParams, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
            }
            if (!this.cursorTds.isResultSet()) {
                ex = new SQLException(Messages.get("error.statement.noresult"), "24000");
                ex.setNextException(this.statement.getMessages().exceptions);
                throw ex;
            }
            this.columns = this.cursorTds.getColumns();
            this.columnCount = CachedResultSet.getColumnCount(this.columns);
            this.rowData = new ArrayList(1000);
            this.cacheResultSetRows();
            this.rowsInResult = this.rowData.size();
            this.pos = 0;
        }
        if (this.concurrency < requestedConcurrency) {
            this.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", "CONCUR_READ_ONLY"), "01000"));
        }
        if (this.resultSetType < requestedType) {
            this.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", "TYPE_SCROLL_INSENSITIVE"), "01000"));
        }
        this.statement.getMessages().checkErrors();
    }

    boolean isCursorUpdateable() throws SQLException {
        int i;
        this.isKeyed = false;
        HashSet<String> tableSet = new HashSet<String>();
        for (i = 0; i < this.columns.length; ++i) {
            ColInfo ci = this.columns[i];
            if (ci.isKey) {
                if ("text".equals(ci.sqlType) || "image".equals(ci.sqlType)) {
                    ci.isKey = false;
                } else {
                    this.isKeyed = true;
                }
            } else if (ci.isIdentity) {
                ci.isKey = true;
                this.isKeyed = true;
            }
            StringBuilder key = new StringBuilder();
            if (ci.tableName == null || ci.tableName.length() <= 0) continue;
            key.setLength(0);
            if (ci.catalog != null) {
                key.append(ci.catalog).append('.');
                if (ci.schema == null) {
                    key.append('.');
                }
            }
            if (ci.schema != null) {
                key.append(ci.schema).append('.');
            }
            key.append(ci.tableName);
            this.tableName = key.toString();
            tableSet.add(this.tableName);
        }
        if (this.tableName.startsWith("#") && this.cursorTds.getTdsVersion() >= 3) {
            StringBuilder sql = new StringBuilder(1024);
            sql.append("SELECT ");
            for (int i2 = 1; i2 <= 8; ++i2) {
                if (i2 > 1) {
                    sql.append(',');
                }
                sql.append("index_col('tempdb..").append(this.tableName);
                sql.append("', indid, ").append(i2).append(')');
            }
            sql.append(" FROM tempdb..sysindexes WHERE id = object_id('tempdb..");
            sql.append(this.tableName).append("') AND indid > 0 AND ");
            sql.append("(status & 2048) = 2048");
            this.cursorTds.executeSQL(sql.toString(), null, null, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
            }
            if (this.cursorTds.isResultSet() && this.cursorTds.getNextRow()) {
                Object[] row = this.cursorTds.getRowData();
                block3: for (int i3 = 0; i3 < row.length; ++i3) {
                    String name = (String)row[i3];
                    if (name == null) continue;
                    for (int c = 0; c < this.columns.length; ++c) {
                        if (this.columns[c].realName == null || !this.columns[c].realName.equalsIgnoreCase(name)) continue;
                        this.columns[c].isKey = true;
                        this.isKeyed = true;
                        continue block3;
                    }
                }
            }
            this.statement.getMessages().checkErrors();
        }
        if (!this.isKeyed) {
            for (i = 0; i < this.columns.length; ++i) {
                String type = this.columns[i].sqlType;
                if ("ntext".equals(type) || "text".equals(type) || "image".equals(type) || "timestamp".equals(type) || this.columns[i].tableName == null) continue;
                this.columns[i].isKey = true;
                this.isKeyed = true;
            }
        }
        return tableSet.size() == 1 && this.isKeyed;
    }

    private boolean cursorFetch(int rowNum) throws SQLException {
        this.rowUpdated = false;
        if (this.cursorName != null) {
            if (!this.cursorTds.getNextRow()) {
                StringBuilder sql = new StringBuilder(128);
                if (this.isSybase && this.sizeChanged) {
                    sql.append("SET CURSOR ROWS ").append(this.fetchSize);
                    sql.append(" FOR ").append(this.cursorName);
                    sql.append("\r\n");
                }
                sql.append("FETCH ").append(this.cursorName);
                this.cursorTds.executeSQL(sql.toString(), null, null, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
                }
                this.sizeChanged = false;
                if (!this.cursorTds.isResultSet() || !this.cursorTds.getNextRow()) {
                    this.pos = -1;
                    this.currentRow = null;
                    this.statement.getMessages().checkErrors();
                    return false;
                }
            }
            this.currentRow = this.statement.getTds().getRowData();
            ++this.pos;
            this.rowsInResult = this.pos;
            this.statement.getMessages().checkErrors();
            return this.currentRow != null;
        }
        if (this.rowsInResult == 0) {
            this.pos = 0;
            this.currentRow = null;
            return false;
        }
        if (rowNum == this.pos) {
            return true;
        }
        if (rowNum < 1) {
            this.currentRow = null;
            this.pos = 0;
            return false;
        }
        if (rowNum > this.rowsInResult) {
            this.currentRow = null;
            this.pos = -1;
            return false;
        }
        this.pos = rowNum;
        this.currentRow = (Object[])this.rowData.get(rowNum - 1);
        boolean bl = this.rowDeleted = this.currentRow == null;
        if (this.resultSetType >= 1005 && this.currentRow != null) {
            this.refreshRow();
        }
        return true;
    }

    private void cursorClose() throws SQLException {
        if (this.cursorName != null) {
            this.statement.clearWarnings();
            String sql = this.isSybase ? "CLOSE " + this.cursorName + "\r\nDEALLOCATE CURSOR " + this.cursorName : "CLOSE " + this.cursorName + "\r\nDEALLOCATE " + this.cursorName;
            this.cursorTds.submitSQL(sql);
        }
        this.rowData = null;
    }

    protected static ParamInfo buildParameter(int pos, ColInfo info, Object value, boolean isUnicode) throws SQLException {
        int length = 0;
        if (value instanceof String) {
            length = ((String)value).length();
        } else if (value instanceof byte[]) {
            length = ((byte[])value).length;
        } else if (value instanceof BlobImpl) {
            BlobImpl blob = (BlobImpl)value;
            value = blob.getBinaryStream();
            length = (int)blob.length();
        } else if (value instanceof ClobImpl) {
            ClobImpl clob = (ClobImpl)value;
            value = clob.getCharacterStream();
            length = (int)clob.length();
        }
        ParamInfo param = new ParamInfo(info, null, value, length);
        param.isUnicode = "nvarchar".equals(info.sqlType) || "nchar".equals(info.sqlType) || "ntext".equals(info.sqlType) || isUnicode;
        param.markerPos = pos;
        return param;
    }

    @Override
    protected Object setColValue(int colIndex, int jdbcType, Object value, int length) throws SQLException {
        ParamInfo pi;
        value = super.setColValue(colIndex, jdbcType, value, length);
        if (!this.onInsertRow && this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        ColInfo ci = this.columns[--colIndex];
        boolean isUnicode = TdsData.isUnicode(ci);
        if (this.onInsertRow) {
            pi = this.insertRow[colIndex];
            if (pi == null) {
                pi = new ParamInfo(-1, isUnicode);
                pi.collation = ci.collation;
                pi.charsetInfo = ci.charsetInfo;
                this.insertRow[colIndex] = pi;
            }
        } else {
            if (this.updateRow == null) {
                this.updateRow = new ParamInfo[this.columnCount];
            }
            if ((pi = this.updateRow[colIndex]) == null) {
                pi = new ParamInfo(-1, isUnicode);
                pi.collation = ci.collation;
                pi.charsetInfo = ci.charsetInfo;
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
            pi.scale = pi.value instanceof BigDecimal ? ((BigDecimal)pi.value).scale() : 0;
        }
        return value;
    }

    ParamInfo[] buildWhereClause(StringBuilder sql, ArrayList params, boolean select) throws SQLException {
        sql.append(" WHERE ");
        if (this.cursorName != null) {
            sql.append(" CURRENT OF ").append(this.cursorName);
        } else {
            int count = 0;
            for (int i = 0; i < this.columns.length; ++i) {
                if (this.currentRow[i] == null) {
                    if ("text".equals(this.columns[i].sqlType) || "ntext".equals(this.columns[i].sqlType) || "image".equals(this.columns[i].sqlType) || this.columns[i].tableName == null) continue;
                    if (count > 0) {
                        sql.append(" AND ");
                    }
                    sql.append(this.columns[i].realName);
                    sql.append(" IS NULL");
                    continue;
                }
                if (this.isKeyed && select) {
                    if (!this.columns[i].isKey) continue;
                    if (count > 0) {
                        sql.append(" AND ");
                    }
                    sql.append(this.columns[i].realName);
                    sql.append("=?");
                    ++count;
                    params.add(CachedResultSet.buildParameter(sql.length() - 1, this.columns[i], this.currentRow[i], this.connection.getUseUnicode()));
                    continue;
                }
                if ("text".equals(this.columns[i].sqlType) || "ntext".equals(this.columns[i].sqlType) || "image".equals(this.columns[i].sqlType) || this.columns[i].tableName == null) continue;
                if (count > 0) {
                    sql.append(" AND ");
                }
                sql.append(this.columns[i].realName);
                sql.append("=?");
                ++count;
                params.add(CachedResultSet.buildParameter(sql.length() - 1, this.columns[i], this.currentRow[i], this.connection.getUseUnicode()));
            }
        }
        return params.toArray(new ParamInfo[params.size()]);
    }

    protected void refreshKeyedRows() throws SQLException {
        StringBuilder sql = new StringBuilder(100 + this.columns.length * 10);
        sql.append("SELECT ");
        int count = 0;
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.columns[i].isKey || this.columns[i].tableName == null) continue;
            if (count > 0) {
                sql.append(',');
            }
            sql.append(this.columns[i].realName);
            ++count;
        }
        if (count == 0) {
            return;
        }
        sql.append(" FROM ");
        sql.append(this.tableName);
        ArrayList params = new ArrayList();
        this.buildWhereClause(sql, params, true);
        ParamInfo[] parameters = params.toArray(new ParamInfo[params.size()]);
        TdsCore tds = this.statement.getTds();
        tds.executeSQL(sql.toString(), null, parameters, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
        if (!tds.isEndOfResponse()) {
            if (tds.getMoreResults() && tds.getNextRow()) {
                Object[] col = tds.getRowData();
                count = 0;
                for (int i = 0; i < this.columns.length; ++i) {
                    if (this.columns[i].isKey) continue;
                    this.currentRow[i] = col[count++];
                }
            } else {
                this.currentRow = null;
            }
        } else {
            this.currentRow = null;
        }
        tds.clearResponseQueue();
        this.statement.getMessages().checkErrors();
        if (this.currentRow == null) {
            this.rowData.set(this.pos - 1, null);
            this.rowDeleted = true;
        }
    }

    protected void refreshReRead() throws SQLException {
        int savePos = this.pos;
        this.cursorCreate();
        this.absolute(savePos);
    }

    @Override
    public void setFetchSize(int size) throws SQLException {
        this.sizeChanged = size != this.fetchSize;
        super.setFetchSize(size);
    }

    @Override
    public void afterLast() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        if (this.pos != -1) {
            this.cursorFetch(this.rowsInResult + 1);
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        if (this.pos != 0) {
            this.cursorFetch(0);
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        if (this.updateRow != null) {
            this.rowUpdated = false;
            for (int i = 0; i < this.updateRow.length; ++i) {
                if (this.updateRow[i] == null) continue;
                this.updateRow[i].clearInValue();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            try {
                this.cursorClose();
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
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        StringBuilder sql = new StringBuilder(128);
        ArrayList params = new ArrayList();
        sql.append("DELETE FROM ");
        sql.append(this.tableName);
        ParamInfo[] parameters = this.buildWhereClause(sql, params, false);
        this.updateTds.executeSQL(sql.toString(), null, parameters, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
        int updateCount = 0;
        while (!this.updateTds.isEndOfResponse()) {
            if (this.updateTds.getMoreResults() || !this.updateTds.isUpdateCount()) continue;
            updateCount = this.updateTds.getUpdateCount();
        }
        this.updateTds.clearResponseQueue();
        this.statement.getMessages().checkErrors();
        if (updateCount == 0) {
            throw new SQLException(Messages.get("error.resultset.deletefail"), "24000");
        }
        this.rowDeleted = true;
        this.currentRow = null;
        if (this.resultSetType != 1003) {
            this.rowData.set(this.pos - 1, null);
        }
    }

    @Override
    public void insertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (!this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.notinsrow"), "24000");
        }
        if (!this.tempResultSet) {
            int i;
            StringBuilder sql = new StringBuilder(128);
            ArrayList<ParamInfo> params = new ArrayList<ParamInfo>();
            sql.append("INSERT INTO ");
            sql.append(this.tableName);
            int sqlLen = sql.length();
            sql.append(" (");
            int count = 0;
            for (i = 0; i < this.columnCount; ++i) {
                if (this.insertRow[i] == null) continue;
                if (count > 0) {
                    sql.append(", ");
                }
                sql.append(this.columns[i].realName);
                ++count;
            }
            sql.append(") VALUES(");
            count = 0;
            for (i = 0; i < this.columnCount; ++i) {
                if (this.insertRow[i] == null) continue;
                if (count > 0) {
                    sql.append(", ");
                }
                sql.append('?');
                this.insertRow[i].markerPos = sql.length() - 1;
                params.add(this.insertRow[i]);
                ++count;
            }
            sql.append(')');
            if (count == 0) {
                sql.setLength(sqlLen);
                if (this.isSybase) {
                    sql.append(" VALUES()");
                } else {
                    sql.append(" DEFAULT VALUES");
                }
            }
            ParamInfo[] parameters = params.toArray(new ParamInfo[params.size()]);
            this.updateTds.executeSQL(sql.toString(), null, parameters, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            int updateCount = 0;
            while (!this.updateTds.isEndOfResponse()) {
                if (this.updateTds.getMoreResults() || !this.updateTds.isUpdateCount()) continue;
                updateCount = this.updateTds.getUpdateCount();
            }
            this.updateTds.clearResponseQueue();
            this.statement.getMessages().checkErrors();
            if (updateCount < 1) {
                throw new SQLException(Messages.get("error.resultset.insertfail"), "24000");
            }
        }
        if (this.resultSetType >= 1005 || this.resultSetType == 1003 && this.cursorName == null) {
            JtdsConnection con = (JtdsConnection)this.statement.getConnection();
            Object[] row = this.newRow();
            for (int i = 0; i < this.insertRow.length; ++i) {
                if (this.insertRow[i] == null) continue;
                row[i] = Support.convert(con, this.insertRow[i].value, this.columns[i].jdbcType, con.getCharset());
            }
            this.rowData.add(row);
        }
        ++this.rowsInResult;
        for (int i = 0; this.insertRow != null && i < this.insertRow.length; ++i) {
            if (this.insertRow[i] == null) continue;
            this.insertRow[i].clearInValue();
        }
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        this.insertRow = null;
        this.onInsertRow = false;
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        this.insertRow = new ParamInfo[this.columnCount];
        this.onInsertRow = true;
    }

    @Override
    public void refreshRow() throws SQLException {
        this.checkOpen();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        if (this.concurrency != 1007) {
            this.cancelRowUpdates();
            this.rowUpdated = false;
        }
        if (this.resultSetType == 1003 || this.currentRow == null) {
            return;
        }
        if (this.isKeyed) {
            this.refreshKeyedRows();
        } else {
            this.refreshReRead();
        }
    }

    @Override
    public void updateRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        this.rowUpdated = false;
        this.rowDeleted = false;
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        if (this.updateRow == null) {
            return;
        }
        boolean keysChanged = false;
        StringBuilder sql = new StringBuilder(128);
        ArrayList<ParamInfo> params = new ArrayList<ParamInfo>();
        sql.append("UPDATE ");
        sql.append(this.tableName);
        sql.append(" SET ");
        int count = 0;
        for (int i = 0; i < this.columnCount; ++i) {
            if (this.updateRow[i] == null) continue;
            if (count > 0) {
                sql.append(", ");
            }
            sql.append(this.columns[i].realName);
            sql.append("=?");
            this.updateRow[i].markerPos = sql.length() - 1;
            params.add(this.updateRow[i]);
            ++count;
            if (!this.columns[i].isKey) continue;
            keysChanged = true;
        }
        if (count == 0) {
            return;
        }
        ParamInfo[] parameters = this.buildWhereClause(sql, params, false);
        this.updateTds.executeSQL(sql.toString(), null, parameters, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
        int updateCount = 0;
        while (!this.updateTds.isEndOfResponse()) {
            if (this.updateTds.getMoreResults() || !this.updateTds.isUpdateCount()) continue;
            updateCount = this.updateTds.getUpdateCount();
        }
        this.updateTds.clearResponseQueue();
        this.statement.getMessages().checkErrors();
        if (updateCount == 0) {
            throw new SQLException(Messages.get("error.resultset.updatefail"), "24000");
        }
        if (this.resultSetType != 1004) {
            JtdsConnection con = (JtdsConnection)this.statement.getConnection();
            for (int i = 0; i < this.updateRow.length; ++i) {
                if (this.updateRow[i] == null) continue;
                if (this.updateRow[i].value instanceof byte[] && (this.columns[i].jdbcType == 1 || this.columns[i].jdbcType == 12 || this.columns[i].jdbcType == -1)) {
                    try {
                        this.currentRow[i] = new String((byte[])this.updateRow[i].value, con.getCharset());
                    }
                    catch (UnsupportedEncodingException e) {
                        this.currentRow[i] = new String((byte[])this.updateRow[i].value);
                    }
                    continue;
                }
                this.currentRow[i] = Support.convert(con, this.updateRow[i].value, this.columns[i].jdbcType, con.getCharset());
            }
        }
        if (keysChanged && this.resultSetType >= 1005) {
            this.rowData.add(this.currentRow);
            this.rowsInResult = this.rowData.size();
            this.rowData.set(this.pos - 1, null);
            this.currentRow = null;
            this.rowDeleted = true;
        } else {
            this.rowUpdated = true;
        }
        this.cancelRowUpdates();
    }

    @Override
    public boolean first() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return this.cursorFetch(1);
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
        return this.cursorFetch(this.rowsInResult);
    }

    @Override
    public boolean next() throws SQLException {
        this.checkOpen();
        if (this.pos != -1) {
            return this.cursorFetch(this.pos + 1);
        }
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        if (this.pos == -1) {
            this.pos = this.rowsInResult + 1;
        }
        return this.cursorFetch(this.pos - 1);
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        this.checkOpen();
        return this.rowDeleted;
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
        if (row < 1) {
            row = this.rowsInResult + 1 + row;
        }
        return this.cursorFetch(row);
    }

    @Override
    public boolean relative(int row) throws SQLException {
        this.checkScrollable();
        if (this.pos == -1) {
            return this.absolute(this.rowsInResult + 1 + row);
        }
        return this.absolute(this.pos + row);
    }

    @Override
    public String getCursorName() throws SQLException {
        this.checkOpen();
        if (this.cursorName != null && !this.cursorName.startsWith("_jtds")) {
            return this.cursorName;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }
}

