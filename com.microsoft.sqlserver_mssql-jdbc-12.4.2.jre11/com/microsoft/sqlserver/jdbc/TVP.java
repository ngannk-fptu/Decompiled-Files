/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.SQLServerDataColumn;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerSortOrder;
import com.microsoft.sqlserver.jdbc.TVPType;
import com.microsoft.sqlserver.jdbc.Util;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class TVP {
    String tvpName;
    String tvpOwningSchema;
    String tvpDbName;
    ResultSet sourceResultSet = null;
    SQLServerDataTable sourceDataTable = null;
    Map<Integer, SQLServerMetaData> columnMetadata = null;
    Iterator<Map.Entry<Integer, Object[]>> sourceDataTableRowIterator = null;
    ISQLServerDataRecord sourceRecord = null;
    TVPType tvpType = null;
    Set<String> columnNames = null;

    void initTVP(TVPType type, String tvpPartName) throws SQLServerException {
        this.tvpType = type;
        this.columnMetadata = new LinkedHashMap<Integer, SQLServerMetaData>();
        this.parseTypeName(tvpPartName);
    }

    TVP(String tvpPartName) throws SQLServerException {
        this.initTVP(TVPType.NULL, tvpPartName);
    }

    TVP(String tvpPartName, SQLServerDataTable tvpDataTable) throws SQLServerException {
        if (tvpPartName == null) {
            tvpPartName = tvpDataTable.getTvpName();
        }
        this.initTVP(TVPType.SQLSERVERDATATABLE, tvpPartName);
        this.sourceDataTable = tvpDataTable;
        this.sourceDataTableRowIterator = this.sourceDataTable.getIterator();
        this.populateMetadataFromDataTable();
    }

    TVP(String tvpPartName, ResultSet tvpResultSet) throws SQLServerException {
        this.initTVP(TVPType.RESULTSET, tvpPartName);
        this.sourceResultSet = tvpResultSet;
        this.populateMetadataFromResultSet();
    }

    TVP(String tvpPartName, ISQLServerDataRecord tvpRecord) throws SQLServerException {
        this.initTVP(TVPType.ISQLSERVERDATARECORD, tvpPartName);
        this.sourceRecord = tvpRecord;
        this.columnNames = new HashSet<String>();
        this.populateMetadataFromDataRecord();
        this.validateOrderProperty();
    }

    boolean isNull() {
        return TVPType.NULL == this.tvpType;
    }

    Object[] getRowData() throws SQLServerException {
        if (TVPType.RESULTSET == this.tvpType) {
            int colCount = this.columnMetadata.size();
            Object[] rowData = new Object[colCount];
            for (int i = 0; i < colCount; ++i) {
                try {
                    if (92 == this.sourceResultSet.getMetaData().getColumnType(i + 1)) {
                        rowData[i] = this.sourceResultSet.getTimestamp(i + 1);
                        continue;
                    }
                    rowData[i] = this.sourceResultSet.getObject(i + 1);
                    continue;
                }
                catch (SQLException e) {
                    throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                }
            }
            return rowData;
        }
        if (TVPType.SQLSERVERDATATABLE == this.tvpType) {
            Map.Entry<Integer, Object[]> rowPair = this.sourceDataTableRowIterator.next();
            return rowPair.getValue();
        }
        return this.sourceRecord.getRowData();
    }

    boolean next() throws SQLServerException {
        if (TVPType.RESULTSET == this.tvpType) {
            try {
                return this.sourceResultSet.next();
            }
            catch (SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
            }
        }
        if (TVPType.SQLSERVERDATATABLE == this.tvpType) {
            return this.sourceDataTableRowIterator.hasNext();
        }
        if (null != this.sourceRecord) {
            return this.sourceRecord.next();
        }
        return false;
    }

    void populateMetadataFromDataTable() throws SQLServerException {
        if (null != this.sourceDataTable) {
            Map<Integer, SQLServerDataColumn> dataTableMetaData = this.sourceDataTable.getColumnMetadata();
            if (null == dataTableMetaData || dataTableMetaData.isEmpty()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), null);
            }
            dataTableMetaData.entrySet().forEach(e -> this.columnMetadata.put((Integer)e.getKey(), new SQLServerMetaData(((SQLServerDataColumn)e.getValue()).columnName, ((SQLServerDataColumn)e.getValue()).javaSqlType, ((SQLServerDataColumn)e.getValue()).precision, ((SQLServerDataColumn)e.getValue()).scale)));
        }
    }

    void populateMetadataFromResultSet() throws SQLServerException {
        if (null != this.sourceResultSet) {
            try {
                ResultSetMetaData rsmd = this.sourceResultSet.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); ++i) {
                    SQLServerMetaData columnMetaData = new SQLServerMetaData(rsmd.getColumnName(i + 1), rsmd.getColumnType(i + 1), rsmd.getPrecision(i + 1), rsmd.getScale(i + 1));
                    this.columnMetadata.put(i, columnMetaData);
                }
            }
            catch (SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
            }
        }
    }

    void populateMetadataFromDataRecord() throws SQLServerException {
        if (null != this.sourceRecord) {
            if (0 >= this.sourceRecord.getColumnCount()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), null);
            }
            for (int i = 0; i < this.sourceRecord.getColumnCount(); ++i) {
                Util.checkDuplicateColumnName(this.sourceRecord.getColumnMetaData((int)(i + 1)).columnName, this.columnNames);
                SQLServerMetaData metaData = new SQLServerMetaData(this.sourceRecord.getColumnMetaData(i + 1));
                this.columnMetadata.put(i, metaData);
            }
        }
    }

    void validateOrderProperty() throws SQLServerException {
        int columnCount = this.columnMetadata.size();
        boolean[] sortOrdinalSpecified = new boolean[columnCount];
        int maxSortOrdinal = -1;
        int sortCount = 0;
        for (Map.Entry<Integer, SQLServerMetaData> columnPair : this.columnMetadata.entrySet()) {
            SQLServerSortOrder columnSortOrder = columnPair.getValue().sortOrder;
            int columnSortOrdinal = columnPair.getValue().sortOrdinal;
            if (SQLServerSortOrder.UNSPECIFIED == columnSortOrder) continue;
            if (columnCount <= columnSortOrdinal) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPSortOrdinalGreaterThanFieldCount"));
                throw new SQLServerException(form.format(new Object[]{columnSortOrdinal, columnPair.getKey()}), null, 0, null);
            }
            if (sortOrdinalSpecified[columnSortOrdinal]) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPDuplicateSortOrdinal"));
                throw new SQLServerException(form.format(new Object[]{columnSortOrdinal}), null, 0, null);
            }
            sortOrdinalSpecified[columnSortOrdinal] = true;
            if (columnSortOrdinal > maxSortOrdinal) {
                maxSortOrdinal = columnSortOrdinal;
            }
            ++sortCount;
        }
        if (0 < sortCount && maxSortOrdinal >= sortCount) {
            int i;
            for (i = 0; i < sortCount && sortOrdinalSpecified[i]; ++i) {
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPMissingSortOrdinal"));
            throw new SQLServerException(form.format(new Object[]{i}), null, 0, null);
        }
    }

    void parseTypeName(String name) throws SQLServerException {
        String leftQuote = "[\"";
        String rightQuote = "]\"";
        char separator = '.';
        int limit = 3;
        String[] parsedNames = new String[limit];
        int stringCount = 0;
        if (null == name || 0 == name.length()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTVPName"));
            Object[] msgArgs = new Object[]{};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        StringBuilder sb = new StringBuilder(name.length());
        StringBuilder whitespaceSB = null;
        char rightQuoteChar = ' ';
        MPIState state = MPIState.MPI_VALUE;
        block12: for (int index = 0; index < name.length(); ++index) {
            char testchar = name.charAt(index);
            switch (state) {
                case MPI_VALUE: {
                    if (Character.isWhitespace(testchar)) continue block12;
                    if (testchar == separator) {
                        parsedNames[stringCount] = "";
                        ++stringCount;
                        continue block12;
                    }
                    int quoteIndex = leftQuote.indexOf(testchar);
                    if (-1 != quoteIndex) {
                        rightQuoteChar = rightQuote.charAt(quoteIndex);
                        sb.setLength(0);
                        state = MPIState.MPI_PARSE_QUOTE;
                        continue block12;
                    }
                    if (-1 != rightQuote.indexOf(testchar)) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                    }
                    sb.setLength(0);
                    sb.append(testchar);
                    state = MPIState.MPI_PARSE_NONQUOTE;
                    continue block12;
                }
                case MPI_PARSE_NONQUOTE: {
                    if (testchar == separator) {
                        parsedNames[stringCount] = sb.toString();
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_VALUE;
                        continue block12;
                    }
                    if (-1 != rightQuote.indexOf(testchar) || -1 != leftQuote.indexOf(testchar)) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                    }
                    if (Character.isWhitespace(testchar)) {
                        parsedNames[stringCount] = sb.toString();
                        if (null == whitespaceSB) {
                            whitespaceSB = new StringBuilder();
                        }
                        whitespaceSB.setLength(0);
                        whitespaceSB.append(testchar);
                        state = MPIState.MPI_LOOK_FOR_NEXT_CHAR_OR_SEPARATOR;
                        continue block12;
                    }
                    sb.append(testchar);
                    continue block12;
                }
                case MPI_LOOK_FOR_NEXT_CHAR_OR_SEPARATOR: {
                    if (!Character.isWhitespace(testchar)) {
                        if (testchar == separator) {
                            stringCount = this.incrementStringCount(parsedNames, stringCount);
                            state = MPIState.MPI_VALUE;
                            continue block12;
                        }
                        sb.append((CharSequence)whitespaceSB);
                        sb.append(testchar);
                        parsedNames[stringCount] = sb.toString();
                        state = MPIState.MPI_PARSE_NONQUOTE;
                        continue block12;
                    }
                    if (null == whitespaceSB) {
                        whitespaceSB = new StringBuilder();
                    }
                    whitespaceSB.append(testchar);
                    continue block12;
                }
                case MPI_PARSE_QUOTE: {
                    if (testchar == rightQuoteChar) {
                        state = MPIState.MPI_RIGHT_QUOTE;
                        continue block12;
                    }
                    sb.append(testchar);
                    continue block12;
                }
                case MPI_RIGHT_QUOTE: {
                    if (testchar == rightQuoteChar) {
                        sb.append(testchar);
                        state = MPIState.MPI_PARSE_QUOTE;
                        continue block12;
                    }
                    if (testchar == separator) {
                        parsedNames[stringCount] = sb.toString();
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_VALUE;
                        continue block12;
                    }
                    if (!Character.isWhitespace(testchar)) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                        throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                    }
                    parsedNames[stringCount] = sb.toString();
                    state = MPIState.MPI_LOOK_FOR_SEPARATOR;
                    continue block12;
                }
                case MPI_LOOK_FOR_SEPARATOR: {
                    if (Character.isWhitespace(testchar)) continue block12;
                    if (testchar == separator) {
                        stringCount = this.incrementStringCount(parsedNames, stringCount);
                        state = MPIState.MPI_VALUE;
                        continue block12;
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                    throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                }
            }
        }
        if (stringCount > limit - 1) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
        }
        switch (state) {
            case MPI_VALUE: 
            case MPI_LOOK_FOR_NEXT_CHAR_OR_SEPARATOR: 
            case MPI_LOOK_FOR_SEPARATOR: {
                break;
            }
            case MPI_PARSE_NONQUOTE: 
            case MPI_RIGHT_QUOTE: {
                parsedNames[stringCount] = sb.toString();
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
                throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
            }
        }
        if (parsedNames[0] == null) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
        }
        int offset = limit - stringCount - 1;
        if (offset > 0) {
            for (int x = limit - 1; x >= offset; --x) {
                parsedNames[x] = parsedNames[x - offset];
                parsedNames[x - offset] = null;
            }
        }
        this.tvpName = parsedNames[2];
        this.tvpOwningSchema = parsedNames[1];
        this.tvpDbName = parsedNames[0];
    }

    private int incrementStringCount(String[] ary, int position) throws SQLServerException {
        int limit = ary.length;
        if (++position >= limit) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidThreePartName"));
            throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
        }
        ary[position] = new String();
        return position;
    }

    String getTVPName() {
        return this.tvpName;
    }

    String getDbNameTVP() {
        return this.tvpDbName;
    }

    String getOwningSchemaNameTVP() {
        return this.tvpOwningSchema;
    }

    int getTVPColumnCount() {
        return this.columnMetadata.size();
    }

    Map<Integer, SQLServerMetaData> getColumnMetadata() {
        return this.columnMetadata;
    }

    static enum MPIState {
        MPI_VALUE,
        MPI_PARSE_NONQUOTE,
        MPI_LOOK_FOR_SEPARATOR,
        MPI_LOOK_FOR_NEXT_CHAR_OR_SEPARATOR,
        MPI_PARSE_QUOTE,
        MPI_RIGHT_QUOTE;

    }
}

