/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.SQLServerDataColumn;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.Util;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import microsoft.sql.DateTimeOffset;

public final class SQLServerDataTable {
    int rowCount = 0;
    int columnCount = 0;
    Map<Integer, SQLServerDataColumn> columnMetadata = null;
    Set<String> columnNames = null;
    Map<Integer, Object[]> rows = null;
    private String tvpName = null;
    private final Lock lock = new ReentrantLock();

    public SQLServerDataTable() throws SQLServerException {
        this.columnMetadata = new LinkedHashMap<Integer, SQLServerDataColumn>();
        this.columnNames = new HashSet<String>();
        this.rows = new HashMap<Integer, Object[]>();
    }

    public void clear() {
        this.lock.lock();
        try {
            this.rowCount = 0;
            this.columnCount = 0;
            this.columnMetadata.clear();
            this.columnNames.clear();
            this.rows.clear();
        }
        finally {
            this.lock.unlock();
        }
    }

    public Iterator<Map.Entry<Integer, Object[]>> getIterator() {
        this.lock.lock();
        try {
            if (null != this.rows) {
                Iterator<Map.Entry<Integer, Object[]>> iterator = this.rows.entrySet().iterator();
                return iterator;
            }
            Iterator<Map.Entry<Integer, Object[]>> iterator = null;
            return iterator;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void addColumnMetadata(String columnName, int sqlType) throws SQLServerException {
        this.lock.lock();
        try {
            Util.checkDuplicateColumnName(columnName, this.columnNames);
            this.columnMetadata.put(this.columnCount++, new SQLServerDataColumn(columnName, sqlType));
        }
        finally {
            this.lock.unlock();
        }
    }

    public void addColumnMetadata(SQLServerDataColumn column) throws SQLServerException {
        this.lock.lock();
        try {
            Util.checkDuplicateColumnName(column.columnName, this.columnNames);
            this.columnMetadata.put(this.columnCount++, column);
        }
        finally {
            this.lock.unlock();
        }
    }

    public void addRow(Object ... values) throws SQLServerException {
        this.lock.lock();
        try {
            int count = this.columnMetadata.size();
            if (null != values && values.length > count) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_moreDataInRowThanColumnInTVP"));
                Object[] msgArgs = new Object[]{};
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            Iterator<Map.Entry<Integer, SQLServerDataColumn>> columnsIterator = this.columnMetadata.entrySet().iterator();
            Object[] rowValues = new Object[count];
            int currentColumn = 0;
            while (columnsIterator.hasNext()) {
                Object val = null;
                if (null != values && currentColumn < values.length && null != values[currentColumn]) {
                    val = values[currentColumn];
                }
                ++currentColumn;
                Map.Entry<Integer, SQLServerDataColumn> pair = columnsIterator.next();
                JDBCType jdbcType = JDBCType.of(pair.getValue().javaSqlType);
                this.internalAddrow(jdbcType, val, rowValues, pair);
            }
            this.rows.put(this.rowCount++, rowValues);
        }
        catch (NumberFormatException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e);
        }
        finally {
            this.lock.unlock();
        }
    }

    private void internalAddrow(JDBCType jdbcType, Object val, Object[] rowValues, Map.Entry<Integer, SQLServerDataColumn> pair) throws SQLServerException {
        int key = pair.getKey();
        if (null != val) {
            SQLServerDataColumn currentColumnMetadata = pair.getValue();
            switch (jdbcType) {
                case BIGINT: {
                    rowValues[key] = val instanceof Long ? val : Long.valueOf(Long.parseLong(val.toString()));
                    break;
                }
                case BIT: {
                    if (val instanceof Boolean) {
                        rowValues[key] = val;
                        break;
                    }
                    String valString = val.toString();
                    if ("0".equals(valString) || valString.equalsIgnoreCase(Boolean.FALSE.toString())) {
                        rowValues[key] = Boolean.FALSE;
                        break;
                    }
                    if ("1".equals(valString) || valString.equalsIgnoreCase(Boolean.TRUE.toString())) {
                        rowValues[key] = Boolean.TRUE;
                        break;
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPInvalidColumnValue"));
                    Object[] msgArgs = new Object[]{jdbcType};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
                case INTEGER: {
                    rowValues[key] = val instanceof Integer ? val : Integer.valueOf(Integer.parseInt(val.toString()));
                    break;
                }
                case SMALLINT: 
                case TINYINT: {
                    rowValues[key] = val instanceof Short ? val : Short.valueOf(Short.parseShort(val.toString()));
                    break;
                }
                case DECIMAL: 
                case NUMERIC: {
                    int numberOfDigitsIntegerPart;
                    BigDecimal bd = null;
                    boolean isColumnMetadataUpdated = false;
                    bd = new BigDecimal(val.toString());
                    int precision = Util.getValueLengthBaseOnJavaType(bd, JavaType.of(bd), null, null, jdbcType);
                    if (bd.scale() > currentColumnMetadata.scale) {
                        currentColumnMetadata.scale = bd.scale();
                        isColumnMetadataUpdated = true;
                    }
                    if (precision > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = precision;
                        isColumnMetadataUpdated = true;
                    }
                    if ((numberOfDigitsIntegerPart = precision - bd.scale()) > currentColumnMetadata.numberOfDigitsIntegerPart) {
                        currentColumnMetadata.numberOfDigitsIntegerPart = numberOfDigitsIntegerPart;
                        isColumnMetadataUpdated = true;
                    }
                    if (isColumnMetadataUpdated) {
                        currentColumnMetadata.precision = currentColumnMetadata.scale + currentColumnMetadata.numberOfDigitsIntegerPart;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = bd;
                    break;
                }
                case DOUBLE: {
                    rowValues[key] = val instanceof Double ? val : Double.valueOf(Double.parseDouble(val.toString()));
                    break;
                }
                case FLOAT: 
                case REAL: {
                    rowValues[key] = val instanceof Float ? val : Float.valueOf(Float.parseFloat(val.toString()));
                    break;
                }
                case TIMESTAMP_WITH_TIMEZONE: 
                case TIME_WITH_TIMEZONE: 
                case DATE: 
                case TIME: 
                case TIMESTAMP: 
                case DATETIMEOFFSET: 
                case DATETIME: 
                case SMALLDATETIME: {
                    if (val instanceof Date || val instanceof DateTimeOffset || val instanceof OffsetDateTime || val instanceof OffsetTime) {
                        rowValues[key] = val.toString();
                        break;
                    }
                    rowValues[key] = val;
                    break;
                }
                case BINARY: 
                case VARBINARY: 
                case LONGVARBINARY: {
                    int nValueLen = ((byte[])val).length;
                    if (nValueLen > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = nValueLen;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = val;
                    break;
                }
                case CHAR: 
                case VARCHAR: 
                case NCHAR: 
                case NVARCHAR: 
                case LONGVARCHAR: 
                case LONGNVARCHAR: 
                case SQLXML: {
                    int nValueLen;
                    if (val instanceof UUID) {
                        val = val.toString();
                    }
                    if ((nValueLen = 2 * ((String)val).length()) > currentColumnMetadata.precision) {
                        currentColumnMetadata.precision = nValueLen;
                        this.columnMetadata.put(pair.getKey(), currentColumnMetadata);
                    }
                    rowValues[key] = val;
                    break;
                }
                case SQL_VARIANT: {
                    JavaType javaType = JavaType.of(val);
                    JDBCType internalJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                    this.internalAddrow(internalJDBCType, val, rowValues, pair);
                    break;
                }
                default: {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedDataTypeTVP"));
                    Object[] msgArgs = new Object[]{jdbcType};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
            }
        } else {
            rowValues[key] = null;
            if (jdbcType == JDBCType.SQL_VARIANT) {
                throw new SQLServerException(SQLServerException.getErrString("R_invalidValueForTVPWithSQLVariant"), null);
            }
        }
    }

    public Map<Integer, SQLServerDataColumn> getColumnMetadata() {
        this.lock.lock();
        try {
            Map<Integer, SQLServerDataColumn> map = this.columnMetadata;
            return map;
        }
        finally {
            this.lock.unlock();
        }
    }

    public String getTvpName() {
        return this.tvpName;
    }

    public void setTvpName(String tvpName) {
        this.tvpName = tvpName;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.rowCount;
        hash = 31 * hash + this.columnCount;
        hash = 31 * hash + (null != this.columnMetadata ? this.columnMetadata.hashCode() : 0);
        hash = 31 * hash + (null != this.columnNames ? this.columnNames.hashCode() : 0);
        hash = 31 * hash + this.getRowsHashCode();
        hash = 31 * hash + (null != this.tvpName ? this.tvpName.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (null != object && object.getClass() == SQLServerDataTable.class) {
            SQLServerDataTable aSQLServerDataTable = (SQLServerDataTable)object;
            if (this.hashCode() == aSQLServerDataTable.hashCode()) {
                boolean equalColumnMetadata = this.columnMetadata.equals(aSQLServerDataTable.columnMetadata);
                boolean equalColumnNames = this.columnNames.equals(aSQLServerDataTable.columnNames);
                boolean equalRowData = this.compareRows(aSQLServerDataTable.rows);
                return this.rowCount == aSQLServerDataTable.rowCount && this.columnCount == aSQLServerDataTable.columnCount && this.tvpName == aSQLServerDataTable.tvpName && equalColumnMetadata && equalColumnNames && equalRowData;
            }
        }
        return false;
    }

    private int getRowsHashCode() {
        if (null == this.rows) {
            return 0;
        }
        int h = 0;
        for (Map.Entry<Integer, Object[]> entry : this.rows.entrySet()) {
            h += entry.getKey() ^ Arrays.hashCode(entry.getValue());
        }
        return h;
    }

    private boolean compareRows(Map<Integer, Object[]> otherRows) {
        if (this.rows == otherRows) {
            return true;
        }
        if (this.rows.size() != otherRows.size()) {
            return false;
        }
        try {
            for (Map.Entry<Integer, Object[]> e : this.rows.entrySet()) {
                Integer key = e.getKey();
                Object[] value = e.getValue();
                if (!(null == value ? null != otherRows.get(key) || !otherRows.containsKey(key) : !Arrays.equals(value, otherRows.get(key)))) continue;
                return false;
            }
        }
        catch (ClassCastException unused) {
            return false;
        }
        catch (NullPointerException unused) {
            return false;
        }
        return true;
    }
}

