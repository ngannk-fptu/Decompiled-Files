/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class SQLServerBulkRecord
implements ISQLServerBulkRecord {
    private static final long serialVersionUID = -170992637946357449L;
    protected String[] columnNames = null;
    protected transient Map<Integer, ColumnMetadata> columnMetadata;
    protected transient DateTimeFormatter dateTimeFormatter = null;
    protected transient DateTimeFormatter timeFormatter = null;
    String loggerPackageName = "com.microsoft.jdbc.SQLServerBulkRecord";
    static Logger loggerExternal = Logger.getLogger("com.microsoft.jdbc.SQLServerBulkRecord");

    SQLServerBulkRecord() {
    }

    @Override
    public void addColumnMetadata(int positionInSource, String name, int jdbcType, int precision, int scale, DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        this.addColumnMetadataInternal(positionInSource, name, jdbcType, precision, scale, dateTimeFormatter);
    }

    @Override
    public void addColumnMetadata(int positionInSource, String name, int jdbcType, int precision, int scale) throws SQLServerException {
        this.addColumnMetadataInternal(positionInSource, name, jdbcType, precision, scale, null);
    }

    void addColumnMetadataInternal(int positionInSource, String name, int jdbcType, int precision, int scale, DateTimeFormatter dateTimeFormatter) throws SQLServerException {
    }

    @Override
    public void setTimestampWithTimezoneFormat(String dateTimeFormat) {
        loggerExternal.entering(this.loggerPackageName, "setTimestampWithTimezoneFormat", dateTimeFormat);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        loggerExternal.exiting(this.loggerPackageName, "setTimestampWithTimezoneFormat");
    }

    @Override
    public void setTimestampWithTimezoneFormat(DateTimeFormatter dateTimeFormatter) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, "setTimestampWithTimezoneFormat", new Object[]{dateTimeFormatter});
        }
        this.dateTimeFormatter = dateTimeFormatter;
        loggerExternal.exiting(this.loggerPackageName, "setTimestampWithTimezoneFormat");
    }

    @Override
    public void setTimeWithTimezoneFormat(String timeFormat) {
        loggerExternal.entering(this.loggerPackageName, "setTimeWithTimezoneFormat", timeFormat);
        this.timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        loggerExternal.exiting(this.loggerPackageName, "setTimeWithTimezoneFormat");
    }

    @Override
    public void setTimeWithTimezoneFormat(DateTimeFormatter dateTimeFormatter) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, "setTimeWithTimezoneFormat", new Object[]{dateTimeFormatter});
        }
        this.timeFormatter = dateTimeFormatter;
        loggerExternal.exiting(this.loggerPackageName, "setTimeWithTimezoneFormat");
    }

    void throwInvalidArgument(String argument) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
        Object[] msgArgs = new Object[]{argument};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
    }

    void checkDuplicateColumnName(int positionInTable, String colName) throws SQLServerException {
        if (null != colName && colName.trim().length() != 0) {
            for (Map.Entry<Integer, ColumnMetadata> entry : this.columnMetadata.entrySet()) {
                if (null == entry || entry.getKey() == positionInTable || null == entry.getValue() || !colName.trim().equalsIgnoreCase(entry.getValue().columnName)) continue;
                throw new SQLServerException(SQLServerException.getErrString("R_BulkDataDuplicateColumn"), null);
            }
        }
    }

    @Override
    public DateTimeFormatter getColumnDateTimeFormatter(int column) {
        return this.columnMetadata.get((Object)Integer.valueOf((int)column)).dateTimeFormatter;
    }

    @Override
    public Set<Integer> getColumnOrdinals() {
        return this.columnMetadata.keySet();
    }

    @Override
    public String getColumnName(int column) {
        return this.columnMetadata.get((Object)Integer.valueOf((int)column)).columnName;
    }

    @Override
    public int getColumnType(int column) {
        return this.columnMetadata.get((Object)Integer.valueOf((int)column)).columnType;
    }

    @Override
    public int getPrecision(int column) {
        return this.columnMetadata.get((Object)Integer.valueOf((int)column)).precision;
    }

    @Override
    public int getScale(int column) {
        return this.columnMetadata.get((Object)Integer.valueOf((int)column)).scale;
    }

    @Override
    public boolean isAutoIncrement(int column) {
        return false;
    }

    protected class ColumnMetadata {
        String columnName;
        int columnType;
        int precision;
        int scale;
        DateTimeFormatter dateTimeFormatter = null;

        ColumnMetadata(String name, int type, int precision, int scale, DateTimeFormatter dateTimeFormatter) {
            this.columnName = name;
            this.columnType = type;
            this.precision = precision;
            this.scale = scale;
            this.dateTimeFormatter = dateTimeFormatter;
        }
    }
}

