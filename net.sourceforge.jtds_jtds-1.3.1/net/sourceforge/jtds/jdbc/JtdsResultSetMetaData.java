/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsData;

public class JtdsResultSetMetaData
implements ResultSetMetaData {
    private final ColInfo[] columns;
    private final int columnCount;
    private final boolean useLOBs;

    JtdsResultSetMetaData(ColInfo[] columns, int columnCount, boolean useLOBs) {
        this.columns = columns;
        this.columnCount = columnCount;
        this.useLOBs = useLOBs;
    }

    ColInfo getColumn(int column) throws SQLException {
        if (column < 1 || column > this.columnCount) {
            throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(column)), "07009");
        }
        return this.columns[column - 1];
    }

    @Override
    public int getColumnCount() throws SQLException {
        return this.columnCount;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return this.getColumn((int)column).displaySize;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if (this.useLOBs) {
            return this.getColumn((int)column).jdbcType;
        }
        return Support.convertLOBType(this.getColumn((int)column).jdbcType);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return this.getColumn((int)column).precision;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return this.getColumn((int)column).scale;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return this.getColumn((int)column).nullable;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return this.getColumn((int)column).isIdentity;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return this.getColumn((int)column).isCaseSensitive;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return TdsData.isCurrency(this.getColumn(column));
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        this.getColumn(column);
        return false;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return !this.getColumn((int)column).isWriteable;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return TdsData.isSearchable(this.getColumn(column));
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return TdsData.isSigned(this.getColumn(column));
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return this.getColumn((int)column).isWriteable;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        ColInfo col = this.getColumn(column);
        return col.catalog == null ? "" : col.catalog;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        String c = Support.getClassName(this.getColumnType(column));
        if (!this.useLOBs) {
            if ("java.sql.Clob".equals(c)) {
                return "java.lang.String";
            }
            if ("java.sql.Blob".equals(c)) {
                return "[B";
            }
        }
        return c;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return this.getColumn((int)column).name;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return this.getColumn((int)column).name;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return this.getColumn((int)column).sqlType;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        ColInfo col = this.getColumn(column);
        return col.schema == null ? "" : col.schema;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        ColInfo col = this.getColumn(column);
        return col.tableName == null ? "" : col.tableName;
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }
}

