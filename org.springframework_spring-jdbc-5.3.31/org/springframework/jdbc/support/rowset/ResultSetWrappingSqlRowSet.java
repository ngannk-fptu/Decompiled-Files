/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.jdbc.support.rowset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSetMetaData;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public class ResultSetWrappingSqlRowSet
implements SqlRowSet {
    private static final long serialVersionUID = -4688694393146734764L;
    private final ResultSet resultSet;
    private final SqlRowSetMetaData rowSetMetaData;
    private final Map<String, Integer> columnLabelMap;

    public ResultSetWrappingSqlRowSet(ResultSet resultSet) throws InvalidResultSetAccessException {
        this.resultSet = resultSet;
        try {
            this.rowSetMetaData = new ResultSetWrappingSqlRowSetMetaData(resultSet.getMetaData());
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            if (rsmd != null) {
                int columnCount = rsmd.getColumnCount();
                this.columnLabelMap = CollectionUtils.newHashMap((int)columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    String key = rsmd.getColumnLabel(i);
                    if (this.columnLabelMap.containsKey(key)) continue;
                    this.columnLabelMap.put(key, i);
                }
            } else {
                this.columnLabelMap = Collections.emptyMap();
            }
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    public final ResultSet getResultSet() {
        return this.resultSet;
    }

    @Override
    public final SqlRowSetMetaData getMetaData() {
        return this.rowSetMetaData;
    }

    @Override
    public int findColumn(String columnLabel) throws InvalidResultSetAccessException {
        Integer columnIndex = this.columnLabelMap.get(columnLabel);
        if (columnIndex != null) {
            return columnIndex;
        }
        try {
            return this.resultSet.findColumn(columnLabel);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public BigDecimal getBigDecimal(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getBigDecimal(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public BigDecimal getBigDecimal(String columnLabel) throws InvalidResultSetAccessException {
        return this.getBigDecimal(this.findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getBoolean(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean getBoolean(String columnLabel) throws InvalidResultSetAccessException {
        return this.getBoolean(this.findColumn(columnLabel));
    }

    @Override
    public byte getByte(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getByte(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public byte getByte(String columnLabel) throws InvalidResultSetAccessException {
        return this.getByte(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Date getDate(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getDate(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Date getDate(String columnLabel) throws InvalidResultSetAccessException {
        return this.getDate(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Date getDate(int columnIndex, Calendar cal) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getDate(columnIndex, cal);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Date getDate(String columnLabel, Calendar cal) throws InvalidResultSetAccessException {
        return this.getDate(this.findColumn(columnLabel), cal);
    }

    @Override
    public double getDouble(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getDouble(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public double getDouble(String columnLabel) throws InvalidResultSetAccessException {
        return this.getDouble(this.findColumn(columnLabel));
    }

    @Override
    public float getFloat(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getFloat(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public float getFloat(String columnLabel) throws InvalidResultSetAccessException {
        return this.getFloat(this.findColumn(columnLabel));
    }

    @Override
    public int getInt(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getInt(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getInt(String columnLabel) throws InvalidResultSetAccessException {
        return this.getInt(this.findColumn(columnLabel));
    }

    @Override
    public long getLong(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getLong(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public long getLong(String columnLabel) throws InvalidResultSetAccessException {
        return this.getLong(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public String getNString(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getNString(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public String getNString(String columnLabel) throws InvalidResultSetAccessException {
        return this.getNString(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Object getObject(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getObject(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Object getObject(String columnLabel) throws InvalidResultSetAccessException {
        return this.getObject(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getObject(columnIndex, map);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws InvalidResultSetAccessException {
        return this.getObject(this.findColumn(columnLabel), map);
    }

    @Override
    @Nullable
    public <T> T getObject(int columnIndex, Class<T> type) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getObject(columnIndex, type);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public <T> T getObject(String columnLabel, Class<T> type) throws InvalidResultSetAccessException {
        return this.getObject(this.findColumn(columnLabel), type);
    }

    @Override
    public short getShort(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getShort(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public short getShort(String columnLabel) throws InvalidResultSetAccessException {
        return this.getShort(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public String getString(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getString(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public String getString(String columnLabel) throws InvalidResultSetAccessException {
        return this.getString(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Time getTime(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getTime(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Time getTime(String columnLabel) throws InvalidResultSetAccessException {
        return this.getTime(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Time getTime(int columnIndex, Calendar cal) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getTime(columnIndex, cal);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Time getTime(String columnLabel, Calendar cal) throws InvalidResultSetAccessException {
        return this.getTime(this.findColumn(columnLabel), cal);
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(int columnIndex) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getTimestamp(columnIndex);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(String columnLabel) throws InvalidResultSetAccessException {
        return this.getTimestamp(this.findColumn(columnLabel));
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getTimestamp(columnIndex, cal);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws InvalidResultSetAccessException {
        return this.getTimestamp(this.findColumn(columnLabel), cal);
    }

    @Override
    public boolean absolute(int row) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.absolute(row);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public void afterLast() throws InvalidResultSetAccessException {
        try {
            this.resultSet.afterLast();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public void beforeFirst() throws InvalidResultSetAccessException {
        try {
            this.resultSet.beforeFirst();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean first() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.first();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getRow() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.getRow();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isAfterLast() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.isAfterLast();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isBeforeFirst() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.isBeforeFirst();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isFirst() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.isFirst();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isLast() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.isLast();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean last() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.last();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean next() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.next();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean previous() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.previous();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean relative(int rows) throws InvalidResultSetAccessException {
        try {
            return this.resultSet.relative(rows);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean wasNull() throws InvalidResultSetAccessException {
        try {
            return this.resultSet.wasNull();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }
}

