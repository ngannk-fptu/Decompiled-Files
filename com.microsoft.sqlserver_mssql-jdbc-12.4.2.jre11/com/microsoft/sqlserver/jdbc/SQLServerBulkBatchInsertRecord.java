/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.SQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLState;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class SQLServerBulkBatchInsertRecord
extends SQLServerBulkRecord {
    private static final long serialVersionUID = -955998113956445541L;
    private transient List<Parameter[]> batchParam;
    private int batchParamIndex = -1;
    private List<String> columnList;
    private List<String> valueList;
    private static final String loggerClassName = "SQLServerBulkBatchInsertRecord";

    SQLServerBulkBatchInsertRecord(ArrayList<Parameter[]> batchParam, ArrayList<String> columnList, ArrayList<String> valueList, String encoding) throws SQLServerException {
        this.initLoggerResources();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, loggerClassName, new Object[]{batchParam, encoding});
        }
        if (null == batchParam) {
            this.throwInvalidArgument("batchParam");
        }
        if (null == valueList) {
            this.throwInvalidArgument("valueList");
        }
        this.batchParam = batchParam;
        this.columnList = columnList;
        this.valueList = valueList;
        this.columnMetadata = new HashMap();
        loggerExternal.exiting(this.loggerPackageName, loggerClassName);
    }

    private void initLoggerResources() {
        this.loggerPackageName = "com.microsoft.sqlserver.jdbc.SQLServerBulkBatchInsertRecord";
    }

    private Object convertValue(SQLServerBulkRecord.ColumnMetadata cm, Object data) throws SQLServerException {
        switch (cm.columnType) {
            case 4: {
                DecimalFormat decimalFormatter = new DecimalFormat("#");
                decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                String formatedfInput = decimalFormatter.format(Double.parseDouble(data.toString()));
                return Integer.valueOf(formatedfInput);
            }
            case -6: 
            case 5: {
                DecimalFormat decimalFormatter = new DecimalFormat("#");
                decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                String formatedfInput = decimalFormatter.format(Double.parseDouble(data.toString()));
                return Short.valueOf(formatedfInput);
            }
            case -5: {
                BigDecimal bd = new BigDecimal(data.toString().trim());
                try {
                    return bd.setScale(0, RoundingMode.DOWN).longValueExact();
                }
                catch (ArithmeticException ex) {
                    String value = "'" + data + "'";
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                    throw new SQLServerException(form.format(new Object[]{value, JDBCType.of(cm.columnType)}), null, 0, (Throwable)ex);
                }
            }
            case 2: 
            case 3: {
                BigDecimal bd = new BigDecimal(data.toString().trim());
                return bd.setScale(cm.scale, RoundingMode.HALF_UP);
            }
            case -7: {
                try {
                    return 0.0 == Double.parseDouble(data.toString()) ? Boolean.FALSE : Boolean.TRUE;
                }
                catch (NumberFormatException e) {
                    return Boolean.parseBoolean(data.toString());
                }
            }
            case 7: {
                return Float.valueOf(Float.parseFloat(data.toString()));
            }
            case 8: {
                return Double.parseDouble(data.toString());
            }
            case -4: 
            case -3: 
            case -2: 
            case 2004: {
                if (data instanceof byte[]) {
                    return data;
                }
                String binData = data.toString().trim();
                if (binData.startsWith("0x") || binData.startsWith("0X")) {
                    return binData.substring(2);
                }
                return binData;
            }
            case 2013: {
                OffsetTime offsetTimeValue = null != cm.dateTimeFormatter ? OffsetTime.parse(data.toString(), cm.dateTimeFormatter) : (this.timeFormatter != null ? OffsetTime.parse(data.toString(), this.timeFormatter) : OffsetTime.parse(data.toString()));
                return offsetTimeValue;
            }
            case 2014: {
                OffsetDateTime offsetDateTimeValue = null != cm.dateTimeFormatter ? OffsetDateTime.parse(data.toString(), cm.dateTimeFormatter) : (this.dateTimeFormatter != null ? OffsetDateTime.parse(data.toString(), this.dateTimeFormatter) : OffsetDateTime.parse(data.toString()));
                return offsetDateTimeValue;
            }
            case 0: {
                return null;
            }
        }
        return data;
    }

    private String removeSingleQuote(String s) {
        int len = s.length();
        return s.charAt(0) == '\'' && s.charAt(len - 1) == '\'' ? s.substring(1, len - 1) : s;
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        Object[] data = new Object[this.columnMetadata.size()];
        int valueIndex = 0;
        int columnListIndex = 0;
        if (null != this.columnList && this.columnList.size() != this.valueList.size()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DataSchemaMismatch"));
            Object[] msgArgs = new Object[]{};
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        for (Map.Entry pair : this.columnMetadata.entrySet()) {
            Object rowData;
            String valueData;
            int index = (Integer)pair.getKey() - 1;
            if (null == this.columnList || this.columnList.isEmpty()) {
                valueData = this.valueList.get(index);
                rowData = "?".equalsIgnoreCase(valueData) ? this.batchParam.get(this.batchParamIndex)[valueIndex++].getSetterValue() : ("null".equalsIgnoreCase(valueData) ? null : this.removeSingleQuote(valueData));
            } else if (this.columnList.size() > columnListIndex && this.columnList.get(columnListIndex).equalsIgnoreCase(((SQLServerBulkRecord.ColumnMetadata)this.columnMetadata.get((Object)Integer.valueOf((int)(index + 1)))).columnName)) {
                valueData = this.valueList.get(columnListIndex);
                rowData = "?".equalsIgnoreCase(valueData) ? this.batchParam.get(this.batchParamIndex)[valueIndex++].getSetterValue() : ("null".equalsIgnoreCase(valueData) ? null : this.removeSingleQuote(valueData));
                ++columnListIndex;
            } else {
                rowData = null;
            }
            try {
                if (null == rowData) {
                    data[index] = null;
                    continue;
                }
                if (0 == rowData.toString().length()) {
                    data[index] = "";
                    continue;
                }
                data[index] = this.convertValue((SQLServerBulkRecord.ColumnMetadata)pair.getValue(), rowData);
            }
            catch (IllegalArgumentException e) {
                String value = "'" + rowData + "'";
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                throw new SQLServerException(form.format(new Object[]{value, JDBCType.of(((SQLServerBulkRecord.ColumnMetadata)pair.getValue()).columnType)}), null, 0, (Throwable)e);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_DataSchemaMismatch"), e);
            }
        }
        return data;
    }

    @Override
    void addColumnMetadataInternal(int positionInSource, String name, int jdbcType, int precision, int scale, DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, "addColumnMetadata", new Object[]{positionInSource, name, jdbcType, precision, scale});
        }
        String colName = "";
        if (0 >= positionInSource) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumnOrdinal"));
            Object[] msgArgs = new Object[]{positionInSource};
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        if (null != name) {
            colName = name.trim();
        } else if (null != this.columnNames && this.columnNames.length >= positionInSource) {
            colName = this.columnNames[positionInSource - 1];
        }
        if (null != this.columnNames && positionInSource > this.columnNames.length) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
            Object[] msgArgs = new Object[]{positionInSource};
            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
        }
        this.checkDuplicateColumnName(positionInSource, name);
        switch (jdbcType) {
            case -155: 
            case 91: 
            case 92: 
            case 93: {
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, jdbcType, precision, scale, dateTimeFormatter));
                break;
            }
            case 2009: {
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, -16, precision, scale, dateTimeFormatter));
                break;
            }
            case 6: {
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, 8, precision, scale, dateTimeFormatter));
                break;
            }
            case 16: {
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, -7, precision, scale, dateTimeFormatter));
                break;
            }
            default: {
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, jdbcType, precision, scale, dateTimeFormatter));
            }
        }
        loggerExternal.exiting(this.loggerPackageName, "addColumnMetadata");
    }

    @Override
    public boolean next() throws SQLServerException {
        ++this.batchParamIndex;
        return this.batchParamIndex < this.batchParam.size();
    }
}

