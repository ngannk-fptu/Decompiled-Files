/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLState;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SQLServerBulkCSVFileRecord
extends SQLServerBulkRecord
implements AutoCloseable {
    private static final long serialVersionUID = 1546487135640225989L;
    private transient BufferedReader fileReader;
    private transient InputStreamReader sr;
    private transient FileInputStream fis;
    private String currentLine = null;
    private final String delimiter;
    private boolean escapeDelimiters;
    private static final String ESCAPE_SPLIT_PATTERN = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String loggerClassName = "SQLServerBulkCSVFileRecord";

    public SQLServerBulkCSVFileRecord(String fileToParse, String encoding, String delimiter, boolean firstLineIsColumnNames) throws SQLServerException {
        this.initLoggerResources();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, loggerClassName, new Object[]{fileToParse, encoding, delimiter, firstLineIsColumnNames});
        }
        if (null == fileToParse) {
            this.throwInvalidArgument("fileToParse");
        } else if (null == delimiter) {
            this.throwInvalidArgument("delimiter");
        }
        this.delimiter = delimiter;
        try {
            this.fis = new FileInputStream(fileToParse);
            this.sr = null == encoding || 0 == encoding.length() ? new InputStreamReader(this.fis) : new InputStreamReader((InputStream)this.fis, encoding);
            this.initFileReader(this.sr, encoding, delimiter, firstLineIsColumnNames);
        }
        catch (UnsupportedEncodingException unsupportedEncoding) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedEncoding"));
            throw new SQLServerException(form.format(new Object[]{encoding}), null, 0, (Throwable)unsupportedEncoding);
        }
        catch (Exception e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.columnMetadata = new HashMap();
        loggerExternal.exiting(this.loggerPackageName, loggerClassName);
    }

    public SQLServerBulkCSVFileRecord(InputStream fileToParse, String encoding, String delimiter, boolean firstLineIsColumnNames) throws SQLServerException {
        this.initLoggerResources();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggerPackageName, loggerClassName, new Object[]{fileToParse, encoding, delimiter, firstLineIsColumnNames});
        }
        if (null == fileToParse) {
            this.throwInvalidArgument("fileToParse");
        } else if (null == delimiter) {
            this.throwInvalidArgument("delimiter");
        }
        this.delimiter = delimiter;
        try {
            this.sr = null == encoding || 0 == encoding.length() ? new InputStreamReader(fileToParse) : new InputStreamReader(fileToParse, encoding);
            this.initFileReader(this.sr, encoding, delimiter, firstLineIsColumnNames);
        }
        catch (UnsupportedEncodingException unsupportedEncoding) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedEncoding"));
            throw new SQLServerException(form.format(new Object[]{encoding}), null, 0, (Throwable)unsupportedEncoding);
        }
        catch (Exception e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.columnMetadata = new HashMap();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggerPackageName, loggerClassName);
        }
    }

    public SQLServerBulkCSVFileRecord(String fileToParse, String encoding, boolean firstLineIsColumnNames) throws SQLServerException {
        this(fileToParse, encoding, ",", firstLineIsColumnNames);
    }

    public SQLServerBulkCSVFileRecord(String fileToParse, boolean firstLineIsColumnNames) throws SQLServerException {
        this(fileToParse, null, ",", firstLineIsColumnNames);
    }

    private void initFileReader(InputStreamReader sr, String encoding, String demlimeter, boolean firstLineIsColumnNames) throws SQLServerException, IOException {
        this.fileReader = new BufferedReader(sr);
        if (firstLineIsColumnNames) {
            this.currentLine = this.fileReader.readLine();
            if (null != this.currentLine) {
                this.columnNames = this.escapeDelimiters && this.currentLine.contains("\"") ? SQLServerBulkCSVFileRecord.escapeQuotesRFC4180(this.currentLine.split(this.delimiter + ESCAPE_SPLIT_PATTERN, -1)) : this.currentLine.split(this.delimiter, -1);
            }
        }
    }

    private void initLoggerResources() {
        this.loggerPackageName = "com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord";
    }

    @Override
    public void close() throws SQLServerException {
        loggerExternal.entering(this.loggerPackageName, "close");
        if (this.fileReader != null) {
            try {
                this.fileReader.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.sr != null) {
            try {
                this.sr.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.fis != null) {
            try {
                this.fis.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        loggerExternal.exiting(this.loggerPackageName, "close");
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        if (null == this.currentLine) {
            return null;
        }
        String[] data = this.escapeDelimiters && this.currentLine.contains("\"") ? SQLServerBulkCSVFileRecord.escapeQuotesRFC4180(this.currentLine.split(this.delimiter + ESCAPE_SPLIT_PATTERN, -1)) : this.currentLine.split(this.delimiter, -1);
        Object[] dataRow = new Object[data.length];
        for (Map.Entry pair : this.columnMetadata.entrySet()) {
            Object[] msgArgs;
            MessageFormat form;
            SQLServerBulkRecord.ColumnMetadata cm = (SQLServerBulkRecord.ColumnMetadata)pair.getValue();
            if (data.length < (Integer)pair.getKey() - 1) {
                form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                msgArgs = new Object[]{pair.getKey()};
                throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
            }
            if (this.columnNames != null && this.columnNames.length > data.length) {
                form = new MessageFormat(SQLServerException.getErrString("R_DataSchemaMismatch"));
                msgArgs = new Object[]{};
                throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
            }
            try {
                if (0 == data[(Integer)pair.getKey() - 1].length()) {
                    dataRow[((Integer)pair.getKey()).intValue() - 1] = null;
                    continue;
                }
                switch (cm.columnType) {
                    case 4: {
                        DecimalFormat decimalFormatter = new DecimalFormat("#");
                        decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                        String formatedfInput = decimalFormatter.format(Double.parseDouble(data[(Integer)pair.getKey() - 1]));
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = Integer.valueOf(formatedfInput);
                        break;
                    }
                    case -6: 
                    case 5: {
                        DecimalFormat decimalFormatter = new DecimalFormat("#");
                        decimalFormatter.setRoundingMode(RoundingMode.DOWN);
                        String formatedfInput = decimalFormatter.format(Double.parseDouble(data[(Integer)pair.getKey() - 1]));
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = Short.valueOf(formatedfInput);
                        break;
                    }
                    case -5: {
                        BigDecimal bd = new BigDecimal(data[(Integer)pair.getKey() - 1].trim());
                        try {
                            dataRow[((Integer)pair.getKey()).intValue() - 1] = bd.setScale(0, RoundingMode.DOWN).longValueExact();
                            break;
                        }
                        catch (ArithmeticException ex) {
                            String value = "'" + data[(Integer)pair.getKey() - 1] + "'";
                            MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                            throw new SQLServerException(form2.format(new Object[]{value, JDBCType.of(cm.columnType)}), null, 0, (Throwable)ex);
                        }
                    }
                    case -148: 
                    case -146: 
                    case 2: 
                    case 3: {
                        BigDecimal bd = new BigDecimal(data[(Integer)pair.getKey() - 1].trim());
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = bd.setScale(cm.scale, RoundingMode.HALF_UP);
                        break;
                    }
                    case -7: {
                        try {
                            dataRow[((Integer)pair.getKey()).intValue() - 1] = 0.0 == Double.parseDouble(data[(Integer)pair.getKey() - 1]) ? Boolean.FALSE : Boolean.TRUE;
                        }
                        catch (NumberFormatException e) {
                            dataRow[((Integer)pair.getKey()).intValue() - 1] = Boolean.parseBoolean(data[(Integer)pair.getKey() - 1]);
                        }
                        break;
                    }
                    case 7: {
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = Float.valueOf(Float.parseFloat(data[(Integer)pair.getKey() - 1]));
                        break;
                    }
                    case 8: {
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = Double.parseDouble(data[(Integer)pair.getKey() - 1]);
                        break;
                    }
                    case -4: 
                    case -3: 
                    case -2: 
                    case 2004: {
                        String binData = data[(Integer)pair.getKey() - 1].trim();
                        if (binData.startsWith("0x") || binData.startsWith("0X")) {
                            dataRow[((Integer)pair.getKey()).intValue() - 1] = binData.substring(2);
                            break;
                        }
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = binData;
                        break;
                    }
                    case 2013: {
                        OffsetTime offsetTimeValue = null != cm.dateTimeFormatter ? OffsetTime.parse(data[(Integer)pair.getKey() - 1], cm.dateTimeFormatter) : (this.timeFormatter != null ? OffsetTime.parse(data[(Integer)pair.getKey() - 1], this.timeFormatter) : OffsetTime.parse(data[(Integer)pair.getKey() - 1]));
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = offsetTimeValue;
                        break;
                    }
                    case 2014: {
                        OffsetDateTime offsetDateTimeValue = null != cm.dateTimeFormatter ? OffsetDateTime.parse(data[(Integer)pair.getKey() - 1], cm.dateTimeFormatter) : (this.dateTimeFormatter != null ? OffsetDateTime.parse(data[(Integer)pair.getKey() - 1], this.dateTimeFormatter) : OffsetDateTime.parse(data[(Integer)pair.getKey() - 1]));
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = offsetDateTimeValue;
                        break;
                    }
                    case 0: {
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = null;
                        break;
                    }
                    default: {
                        dataRow[((Integer)pair.getKey()).intValue() - 1] = data[(Integer)pair.getKey() - 1];
                        break;
                    }
                }
            }
            catch (IllegalArgumentException e) {
                String value = "'" + data[(Integer)pair.getKey() - 1] + "'";
                MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                throw new SQLServerException(form3.format(new Object[]{value, JDBCType.of(cm.columnType)}), null, 0, (Throwable)e);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_DataSchemaMismatch"), e);
            }
        }
        return dataRow;
    }

    @Override
    void addColumnMetadataInternal(int positionInSource, String name, int jdbcType, int precision, int scale, DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        loggerExternal.entering(this.loggerPackageName, "addColumnMetadata", new Object[]{positionInSource, name, jdbcType, precision, scale});
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
                this.columnMetadata.put(positionInSource, new SQLServerBulkRecord.ColumnMetadata(this, colName, jdbcType, 50, scale, dateTimeFormatter));
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
        try {
            this.currentLine = this.fileReader.readLine();
        }
        catch (IOException e) {
            throw new SQLServerException(e.getMessage(), null, 0, (Throwable)e);
        }
        return null != this.currentLine;
    }

    public boolean isEscapeColumnDelimitersCSV() {
        return this.escapeDelimiters;
    }

    public void setEscapeColumnDelimitersCSV(boolean escapeDelimiters) {
        this.escapeDelimiters = escapeDelimiters;
    }

    private static String[] escapeQuotesRFC4180(String[] tokens) throws SQLServerException {
        if (null == tokens) {
            return tokens;
        }
        for (int i = 0; i < tokens.length; ++i) {
            boolean escaped = false;
            int j = 0;
            StringBuilder sb = new StringBuilder();
            long quoteCount = tokens[i].chars().filter(ch -> ch == 34).count();
            if (quoteCount > 0L) {
                tokens[i] = tokens[i].trim();
            }
            if (0L != quoteCount % 2L || quoteCount > 0L && ('\"' != tokens[i].charAt(0) || '\"' != tokens[i].charAt(tokens[i].length() - 1))) {
                throw new SQLServerException(SQLServerException.getErrString("R_InvalidCSVQuotes"), null, 0, null);
            }
            while (j < tokens[i].length()) {
                if ('\"' == tokens[i].charAt(j)) {
                    if (!escaped) {
                        escaped = true;
                    } else if (j < tokens[i].length() - 1 && '\"' == tokens[i].charAt(j + 1)) {
                        sb.append('\"');
                        ++j;
                    }
                } else {
                    sb.append(tokens[i].charAt(j));
                }
                ++j;
            }
            tokens[i] = sb.toString();
        }
        return tokens;
    }
}

