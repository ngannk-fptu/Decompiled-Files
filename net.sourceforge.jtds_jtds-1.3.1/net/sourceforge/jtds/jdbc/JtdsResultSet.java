/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.jtds.jdbc.CachedResultSet;
import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.DateTime;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsResultSetMetaData;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.UniqueIdentifier;

public class JtdsResultSet
implements ResultSet {
    static final int HOLD_CURSORS_OVER_COMMIT = 1;
    static final int CLOSE_CURSORS_AT_COMMIT = 2;
    protected static final int POS_BEFORE_FIRST = 0;
    protected static final int POS_AFTER_LAST = -1;
    protected static final int INITIAL_ROW_COUNT = 1000;
    protected int pos = 0;
    protected int rowsInResult;
    protected int direction = 1000;
    protected int resultSetType;
    protected int concurrency;
    protected int columnCount;
    protected ColInfo[] columns;
    protected Object[] currentRow;
    protected ArrayList rowData;
    protected int rowPtr;
    protected boolean wasNull;
    protected JtdsStatement statement;
    protected boolean closed;
    protected boolean cancelled;
    protected int fetchDirection = 1000;
    protected int fetchSize;
    protected String cursorName;
    private HashMap columnMap;
    private static NumberFormat f = NumberFormat.getInstance();

    JtdsResultSet(JtdsStatement statement, int resultSetType, int concurrency, ColInfo[] columns) throws SQLException {
        if (statement == null) {
            throw new IllegalArgumentException("Statement parameter must not be null");
        }
        this.statement = statement;
        this.resultSetType = resultSetType;
        this.concurrency = concurrency;
        this.columns = columns;
        this.fetchSize = statement.fetchSize;
        this.fetchDirection = statement.fetchDirection;
        this.cursorName = statement.cursorName;
        if (columns != null) {
            this.columnCount = JtdsResultSet.getColumnCount(columns);
            this.rowsInResult = statement.getTds().isDataInResultSet() ? 1 : 0;
        }
    }

    protected static int getColumnCount(ColInfo[] columns) {
        int i;
        for (i = columns.length - 1; i >= 0 && columns[i].isHidden; --i) {
        }
        return i + 1;
    }

    protected ColInfo[] getColumns() {
        return this.columns;
    }

    protected void setColName(int colIndex, String name) {
        if (colIndex < 1 || colIndex > this.columns.length) {
            throw new IllegalArgumentException("columnIndex " + colIndex + " invalid");
        }
        this.columns[colIndex - 1].realName = name;
    }

    protected void setColLabel(int colIndex, String name) {
        if (colIndex < 1 || colIndex > this.columns.length) {
            throw new IllegalArgumentException("columnIndex " + colIndex + " invalid");
        }
        this.columns[colIndex - 1].name = name;
    }

    protected void setColType(int colIndex, int jdbcType) {
        if (colIndex < 1 || colIndex > this.columns.length) {
            throw new IllegalArgumentException("columnIndex " + colIndex + " invalid");
        }
        this.columns[colIndex - 1].jdbcType = jdbcType;
    }

    protected Object setColValue(int colIndex, int jdbcType, Object value, int length) throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (colIndex < 1 || colIndex > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(colIndex)), "07009");
        }
        if (value instanceof Timestamp) {
            value = new DateTime((Timestamp)value);
        } else if (value instanceof Date) {
            value = new DateTime((Date)value);
        } else if (value instanceof Time) {
            value = new DateTime((Time)value);
        }
        return value;
    }

    protected void setColumnCount(int columnCount) {
        if (columnCount < 1 || columnCount > this.columns.length) {
            throw new IllegalArgumentException("columnCount " + columnCount + " is invalid");
        }
        this.columnCount = columnCount;
    }

    protected Object getColumn(int index) throws SQLException {
        this.checkOpen();
        if (index < 1 || index > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(index)), "07009");
        }
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        }
        Object data = this.currentRow[index - 1];
        this.wasNull = data == null;
        return data;
    }

    protected void checkOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", "ResultSet"), "HY010");
        }
        if (this.cancelled) {
            throw new SQLException(Messages.get("error.generic.cancelled", "ResultSet"), "HY010");
        }
    }

    protected void checkScrollable() throws SQLException {
        if (this.resultSetType == 1003) {
            throw new SQLException(Messages.get("error.resultset.fwdonly"), "24000");
        }
    }

    protected void checkUpdateable() throws SQLException {
        if (this.concurrency == 1007) {
            throw new SQLException(Messages.get("error.resultset.readonly"), "24000");
        }
    }

    protected static void notImplemented(String method) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", method), "HYC00");
    }

    protected Object[] newRow() {
        Object[] row = new Object[this.columns.length];
        return row;
    }

    protected Object[] copyRow(Object[] row) {
        Object[] copy = new Object[this.columns.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        return copy;
    }

    protected ColInfo[] copyInfo(ColInfo[] info) {
        ColInfo[] copy = new ColInfo[info.length];
        System.arraycopy(info, 0, copy, 0, info.length);
        return copy;
    }

    protected Object[] getCurrentRow() {
        return this.currentRow;
    }

    protected void cacheResultSetRows() throws SQLException {
        if (this.rowData == null) {
            this.rowData = new ArrayList(1000);
        }
        if (this.currentRow != null) {
            this.currentRow = this.copyRow(this.currentRow);
        }
        while (this.statement.getTds().getNextRow()) {
            this.rowData.add(this.copyRow(this.statement.getTds().getRowData()));
        }
        this.statement.cacheResults();
    }

    private JtdsConnection getConnection() throws SQLException {
        return (JtdsConnection)this.statement.getConnection();
    }

    @Override
    public int getConcurrency() throws SQLException {
        this.checkOpen();
        return this.concurrency;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        this.checkOpen();
        return this.fetchDirection;
    }

    @Override
    public int getFetchSize() throws SQLException {
        this.checkOpen();
        return this.fetchSize;
    }

    @Override
    public int getRow() throws SQLException {
        this.checkOpen();
        return this.pos > 0 ? this.pos : 0;
    }

    @Override
    public int getType() throws SQLException {
        this.checkOpen();
        return this.resultSetType;
    }

    @Override
    public void afterLast() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.checkOpen();
        this.statement.clearWarnings();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!this.getConnection().isClosed()) {
                    while (this.next()) {
                    }
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
    }

    @Override
    public void insertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public void refreshRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public void updateRow() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
    }

    @Override
    public boolean first() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        this.checkOpen();
        return this.pos == -1 && this.rowsInResult != 0;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        this.checkOpen();
        return this.pos == 0 && this.rowsInResult != 0;
    }

    @Override
    public boolean isFirst() throws SQLException {
        this.checkOpen();
        return this.pos == 1;
    }

    @Override
    public boolean isLast() throws SQLException {
        this.checkOpen();
        if (this.statement.getTds().isDataInResultSet()) {
            this.rowsInResult = this.pos + 1;
        }
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    @Override
    public boolean last() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return false;
    }

    @Override
    public boolean next() throws SQLException {
        this.checkOpen();
        if (this.pos == -1) {
            return false;
        }
        try {
            if (this.rowData != null) {
                if (this.rowPtr < this.rowData.size()) {
                    this.currentRow = (Object[])this.rowData.get(this.rowPtr);
                    this.rowData.set(this.rowPtr++, null);
                    ++this.pos;
                    this.rowsInResult = this.pos;
                } else {
                    this.pos = -1;
                    this.currentRow = null;
                }
            } else if (!this.statement.getTds().getNextRow()) {
                this.statement.cacheResults();
                this.pos = -1;
                this.currentRow = null;
            } else {
                this.currentRow = this.statement.getTds().getRowData();
                ++this.pos;
                this.rowsInResult = this.pos;
            }
            this.statement.getMessages().checkErrors();
        }
        catch (NullPointerException npe) {
            throw new SQLException(Messages.get("error.generic.closed", "ResultSet"), "HY010");
        }
        return this.currentRow != null;
    }

    @Override
    public boolean previous() throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        return false;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        return false;
    }

    @Override
    public boolean wasNull() throws SQLException {
        this.checkOpen();
        return this.wasNull;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return ((Integer)Support.convert(this, this.getColumn(columnIndex), -6, null)).byteValue();
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return ((Integer)Support.convert(this, this.getColumn(columnIndex), 5, null)).shortValue();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return (Integer)Support.convert(this, this.getColumn(columnIndex), 4, null);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return (Long)Support.convert(this, this.getColumn(columnIndex), -5, null);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return ((Float)Support.convert(this, this.getColumn(columnIndex), 7, null)).floatValue();
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return (Double)Support.convert(this, this.getColumn(columnIndex), 8, null);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.checkOpen();
        switch (direction) {
            case 1001: 
            case 1002: {
                if (this.resultSetType == 1003) {
                    throw new SQLException(Messages.get("error.resultset.fwdonly"), "24000");
                }
            }
            case 1000: {
                this.fetchDirection = direction;
                break;
            }
            default: {
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(direction), "direction"), "24000");
            }
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.checkOpen();
        if (rows < 0 || this.statement.getMaxRows() > 0 && rows > this.statement.getMaxRows()) {
            throw new SQLException(Messages.get("error.generic.badparam", Integer.toString(rows), "rows"), "HY092");
        }
        if (rows == 0) {
            rows = this.statement.getDefaultFetchSize();
        }
        this.fetchSize = rows;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        this.setColValue(columnIndex, 0, null, 0);
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return false;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return (Boolean)Support.convert(this, this.getColumn(columnIndex), 16, null);
    }

    @Override
    public boolean relative(int row) throws SQLException {
        this.checkOpen();
        this.checkScrollable();
        return false;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        this.checkOpen();
        return (byte[])Support.convert(this, this.getColumn(columnIndex), -2, this.getConnection().getCharset());
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        this.setColValue(columnIndex, 4, new Integer(x & 0xFF), 0);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        this.setColValue(columnIndex, 8, new Double(x), 0);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        this.setColValue(columnIndex, 7, new Float(x), 0);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        this.setColValue(columnIndex, 4, new Integer(x), 0);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        this.setColValue(columnIndex, -5, new Long(x), 0);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        this.setColValue(columnIndex, 4, new Integer(x), 0);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        this.setColValue(columnIndex, -7, x ? Boolean.TRUE : Boolean.FALSE, 0);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        this.setColValue(columnIndex, -3, x, x != null ? x.length : 0);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        Clob clob = this.getClob(columnIndex);
        if (clob == null) {
            return null;
        }
        return clob.getAsciiStream();
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        Blob blob = this.getBlob(columnIndex);
        if (blob == null) {
            return null;
        }
        return blob.getBinaryStream();
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        ClobImpl clob = (ClobImpl)this.getClob(columnIndex);
        if (clob == null) {
            return null;
        }
        return clob.getBlobBuffer().getUnicodeStream();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream inputStream, int length) throws SQLException {
        if (inputStream == null || length < 0) {
            this.updateCharacterStream(columnIndex, (Reader)null, 0);
        } else {
            try {
                this.updateCharacterStream(columnIndex, (Reader)new InputStreamReader(inputStream, "US-ASCII"), length);
            }
            catch (UnsupportedEncodingException e) {
                // empty catch block
            }
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream inputStream, int length) throws SQLException {
        if (inputStream == null || length < 0) {
            this.updateBytes(columnIndex, null);
            return;
        }
        this.setColValue(columnIndex, -3, inputStream, length);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        Clob clob = this.getClob(columnIndex);
        if (clob == null) {
            return null;
        }
        return clob.getCharacterStream();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader reader, int length) throws SQLException {
        if (reader == null || length < 0) {
            this.updateString(columnIndex, null);
            return;
        }
        this.setColValue(columnIndex, 12, reader, length);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        Object value = this.getColumn(columnIndex);
        if (value instanceof UniqueIdentifier) {
            return value.toString();
        }
        if (value instanceof DateTime) {
            return ((DateTime)value).toObject();
        }
        if (!this.getConnection().getUseLOBs()) {
            value = Support.convertLOB(value);
        }
        return value;
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        this.checkOpen();
        int length = 0;
        int jdbcType = 12;
        if (x != null) {
            jdbcType = Support.getJdbcType(x);
            if (x instanceof BigDecimal) {
                int prec = this.getConnection().getMaxPrecision();
                x = Support.normalizeBigDecimal((BigDecimal)x, prec);
            } else if (x instanceof Blob) {
                Blob blob = (Blob)x;
                x = blob.getBinaryStream();
                length = (int)blob.length();
            } else if (x instanceof Clob) {
                Clob clob = (Clob)x;
                x = clob.getCharacterStream();
                length = (int)clob.length();
            } else if (x instanceof String) {
                length = ((String)x).length();
            } else if (x instanceof byte[]) {
                length = ((byte[])x).length;
            }
            if (jdbcType == 2000) {
                if (columnIndex < 1 || columnIndex > this.columnCount) {
                    throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(columnIndex)), "07009");
                }
                ColInfo ci = this.columns[columnIndex - 1];
                throw new SQLException(Messages.get("error.convert.badtypes", x.getClass().getName(), Support.getJdbcTypeName(ci.jdbcType)), "22005");
            }
        }
        this.setColValue(columnIndex, jdbcType, x, length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        this.checkOpen();
        if (scale < 0 || scale > this.getConnection().getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
        }
        if (x instanceof BigDecimal) {
            this.updateObject(columnIndex, (Object)((BigDecimal)x).setScale(scale, 4));
        } else if (x instanceof Number) {
            NumberFormat numberFormat = f;
            synchronized (numberFormat) {
                f.setGroupingUsed(false);
                f.setMaximumFractionDigits(scale);
                this.updateObject(columnIndex, (Object)f.format(x));
            }
        } else {
            this.updateObject(columnIndex, x);
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        this.checkOpen();
        if (this.cursorName != null) {
            return this.cursorName;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        Object tmp = this.getColumn(columnIndex);
        if (tmp instanceof String) {
            return (String)tmp;
        }
        return (String)Support.convert(this, tmp, 12, this.getConnection().getCharset());
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        this.setColValue(columnIndex, 12, x, x != null ? x.length() : 0);
    }

    @Override
    public byte getByte(String columnName) throws SQLException {
        return this.getByte(this.findColumn(columnName));
    }

    @Override
    public double getDouble(String columnName) throws SQLException {
        return this.getDouble(this.findColumn(columnName));
    }

    @Override
    public float getFloat(String columnName) throws SQLException {
        return this.getFloat(this.findColumn(columnName));
    }

    @Override
    public int findColumn(String columnName) throws SQLException {
        this.checkOpen();
        if (this.columnMap == null) {
            this.columnMap = new HashMap(this.columnCount);
        } else {
            Object pos = this.columnMap.get(columnName);
            if (pos != null) {
                return (Integer)pos;
            }
        }
        for (int i = 0; i < this.columnCount; ++i) {
            if (!this.columns[i].name.equalsIgnoreCase(columnName)) continue;
            this.columnMap.put(columnName, new Integer(i + 1));
            return i + 1;
        }
        throw new SQLException(Messages.get("error.resultset.colname", columnName), "07009");
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        return this.getInt(this.findColumn(columnName));
    }

    @Override
    public long getLong(String columnName) throws SQLException {
        return this.getLong(this.findColumn(columnName));
    }

    @Override
    public short getShort(String columnName) throws SQLException {
        return this.getShort(this.findColumn(columnName));
    }

    @Override
    public void updateNull(String columnName) throws SQLException {
        this.updateNull(this.findColumn(columnName));
    }

    @Override
    public boolean getBoolean(String columnName) throws SQLException {
        return this.getBoolean(this.findColumn(columnName));
    }

    @Override
    public byte[] getBytes(String columnName) throws SQLException {
        return this.getBytes(this.findColumn(columnName));
    }

    @Override
    public void updateByte(String columnName, byte x) throws SQLException {
        this.updateByte(this.findColumn(columnName), x);
    }

    @Override
    public void updateDouble(String columnName, double x) throws SQLException {
        this.updateDouble(this.findColumn(columnName), x);
    }

    @Override
    public void updateFloat(String columnName, float x) throws SQLException {
        this.updateFloat(this.findColumn(columnName), x);
    }

    @Override
    public void updateInt(String columnName, int x) throws SQLException {
        this.updateInt(this.findColumn(columnName), x);
    }

    @Override
    public void updateLong(String columnName, long x) throws SQLException {
        this.updateLong(this.findColumn(columnName), x);
    }

    @Override
    public void updateShort(String columnName, short x) throws SQLException {
        this.updateShort(this.findColumn(columnName), x);
    }

    @Override
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        this.updateBoolean(this.findColumn(columnName), x);
    }

    @Override
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        this.updateBytes(this.findColumn(columnName), x);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return (BigDecimal)Support.convert(this, this.getColumn(columnIndex), 3, null);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal result = (BigDecimal)Support.convert(this, this.getColumn(columnIndex), 3, null);
        if (result == null) {
            return null;
        }
        return result.setScale(scale, 4);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        if (x != null) {
            int prec = this.getConnection().getMaxPrecision();
            x = Support.normalizeBigDecimal(x, prec);
        }
        this.setColValue(columnIndex, 3, x, 0);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        String url = this.getString(columnIndex);
        try {
            return new URL(url);
        }
        catch (MalformedURLException e) {
            throw new SQLException(Messages.get("error.resultset.badurl", url), "22000");
        }
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        this.checkOpen();
        JtdsResultSet.notImplemented("ResultSet.getArray()");
        return null;
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        JtdsResultSet.notImplemented("ResultSet.updateArray()");
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return (Blob)Support.convert(this, this.getColumn(columnIndex), 2004, null);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        if (x == null) {
            this.updateBinaryStream(columnIndex, (InputStream)null, 0);
        } else {
            this.updateBinaryStream(columnIndex, x.getBinaryStream(), (int)x.length());
        }
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return (Clob)Support.convert(this, this.getColumn(columnIndex), 2005, null);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        if (x == null) {
            this.updateCharacterStream(columnIndex, (Reader)null, 0);
        } else {
            this.updateCharacterStream(columnIndex, x.getCharacterStream(), (int)x.length());
        }
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return (Date)Support.convert(this, this.getColumn(columnIndex), 91, null);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        this.setColValue(columnIndex, 91, x, 0);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        this.checkOpen();
        JtdsResultSet.notImplemented("ResultSet.getRef()");
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        this.checkOpen();
        this.checkUpdateable();
        JtdsResultSet.notImplemented("ResultSet.updateRef()");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        boolean useLOBs = this instanceof CachedResultSet && this.statement.isClosed() ? false : this.getConnection().getUseLOBs();
        return new JtdsResultSetMetaData(this.columns, this.columnCount, useLOBs);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        return this.statement.getWarnings();
    }

    @Override
    public Statement getStatement() throws SQLException {
        this.checkOpen();
        return this.statement;
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return (Time)Support.convert(this, this.getColumn(columnIndex), 92, null);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        this.setColValue(columnIndex, 92, x, 0);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp)Support.convert(this, this.getColumn(columnIndex), 93, null);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        this.setColValue(columnIndex, 93, x, 0);
    }

    @Override
    public InputStream getAsciiStream(String columnName) throws SQLException {
        return this.getAsciiStream(this.findColumn(columnName));
    }

    @Override
    public InputStream getBinaryStream(String columnName) throws SQLException {
        return this.getBinaryStream(this.findColumn(columnName));
    }

    @Override
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return this.getUnicodeStream(this.findColumn(columnName));
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateAsciiStream(this.findColumn(columnName), x, length);
    }

    @Override
    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateBinaryStream(this.findColumn(columnName), x, length);
    }

    @Override
    public Reader getCharacterStream(String columnName) throws SQLException {
        return this.getCharacterStream(this.findColumn(columnName));
    }

    @Override
    public void updateCharacterStream(String columnName, Reader x, int length) throws SQLException {
        this.updateCharacterStream(this.findColumn(columnName), x, length);
    }

    @Override
    public Object getObject(String columnName) throws SQLException {
        return this.getObject(this.findColumn(columnName));
    }

    @Override
    public void updateObject(String columnName, Object x) throws SQLException {
        this.updateObject(this.findColumn(columnName), x);
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        this.updateObject(this.findColumn(columnName), x, scale);
    }

    public Object getObject(int columnIndex, Map map) throws SQLException {
        JtdsResultSet.notImplemented("ResultSet.getObject(int, Map)");
        return null;
    }

    @Override
    public String getString(String columnName) throws SQLException {
        return this.getString(this.findColumn(columnName));
    }

    @Override
    public void updateString(String columnName, String x) throws SQLException {
        this.updateString(this.findColumn(columnName), x);
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnName));
    }

    @Override
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnName), scale);
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        this.updateObject(this.findColumn(columnName), (Object)x);
    }

    @Override
    public URL getURL(String columnName) throws SQLException {
        return this.getURL(this.findColumn(columnName));
    }

    @Override
    public Array getArray(String columnName) throws SQLException {
        return this.getArray(this.findColumn(columnName));
    }

    @Override
    public void updateArray(String columnName, Array x) throws SQLException {
        this.updateArray(this.findColumn(columnName), x);
    }

    @Override
    public Blob getBlob(String columnName) throws SQLException {
        return this.getBlob(this.findColumn(columnName));
    }

    @Override
    public void updateBlob(String columnName, Blob x) throws SQLException {
        this.updateBlob(this.findColumn(columnName), x);
    }

    @Override
    public Clob getClob(String columnName) throws SQLException {
        return this.getClob(this.findColumn(columnName));
    }

    @Override
    public void updateClob(String columnName, Clob x) throws SQLException {
        this.updateClob(this.findColumn(columnName), x);
    }

    @Override
    public Date getDate(String columnName) throws SQLException {
        return this.getDate(this.findColumn(columnName));
    }

    @Override
    public void updateDate(String columnName, Date x) throws SQLException {
        this.updateDate(this.findColumn(columnName), x);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date date = this.getDate(columnIndex);
        if (date != null && cal != null) {
            date = new Date(Support.timeToZone(date, cal));
        }
        return date;
    }

    @Override
    public Ref getRef(String columnName) throws SQLException {
        return this.getRef(this.findColumn(columnName));
    }

    @Override
    public void updateRef(String columnName, Ref x) throws SQLException {
        this.updateRef(this.findColumn(columnName), x);
    }

    @Override
    public Time getTime(String columnName) throws SQLException {
        return this.getTime(this.findColumn(columnName));
    }

    @Override
    public void updateTime(String columnName, Time x) throws SQLException {
        this.updateTime(this.findColumn(columnName), x);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        this.checkOpen();
        Time time = this.getTime(columnIndex);
        if (time != null && cal != null) {
            return new Time(Support.timeToZone(time, cal));
        }
        return time;
    }

    @Override
    public Timestamp getTimestamp(String columnName) throws SQLException {
        return this.getTimestamp(this.findColumn(columnName));
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        this.updateTimestamp(this.findColumn(columnName), x);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        this.checkOpen();
        Timestamp timestamp = this.getTimestamp(columnIndex);
        if (timestamp != null && cal != null) {
            timestamp = new Timestamp(Support.timeToZone(timestamp, cal));
        }
        return timestamp;
    }

    public Object getObject(String columnName, Map map) throws SQLException {
        return this.getObject(this.findColumn(columnName), map);
    }

    @Override
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return this.getDate(this.findColumn(columnName), cal);
    }

    @Override
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return this.getTime(this.findColumn(columnName), cal);
    }

    @Override
    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return this.getTimestamp(this.findColumn(columnName), cal);
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, NClob clob) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, NClob clob) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNString(int columnIndex, String string) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNString(String columnLabel, String string) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new AbstractMethodError();
    }
}

