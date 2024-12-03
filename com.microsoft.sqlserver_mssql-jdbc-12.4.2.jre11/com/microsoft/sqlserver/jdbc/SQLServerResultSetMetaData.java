/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.ISQLServerResultSetMetaData;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SQLServerResultSetMetaData
implements ISQLServerResultSetMetaData {
    private static final long serialVersionUID = -5747558730471411712L;
    private SQLServerConnection con;
    private final SQLServerResultSet rs;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerResultSetMetaData");
    private static final AtomicInteger baseID = new AtomicInteger(0);
    private final String traceID = " SQLServerResultSetMetaData:" + SQLServerResultSetMetaData.nextInstanceID();

    private static int nextInstanceID() {
        return baseID.incrementAndGet();
    }

    public final String toString() {
        return this.traceID;
    }

    SQLServerResultSetMetaData(SQLServerConnection con, SQLServerResultSet rs) {
        this.con = con;
        this.rs = rs;
        assert (rs != null);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.toString() + " created by (" + rs.toString() + ")");
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        return t;
    }

    @Override
    public String getCatalogName(int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getDatabaseName();
    }

    @Override
    public int getColumnCount() throws SQLServerException {
        return this.rs.getColumnCount();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getDisplaySize();
        }
        return this.rs.getColumn(column).getTypeInfo().getDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLServerException {
        return this.rs.getColumn(column).getColumnName();
    }

    @Override
    public String getColumnName(int column) throws SQLServerException {
        return this.rs.getColumn(column).getColumnName();
    }

    @Override
    public int getColumnType(int column) throws SQLServerException {
        TypeInfo typeInfo = this.rs.getColumn(column).getTypeInfo();
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            typeInfo = cryptoMetadata.getBaseTypeInfo();
        }
        JDBCType jdbcType = typeInfo.getSSType().getJDBCType();
        SSType sqlType = typeInfo.getSSType();
        if (SSType.SQL_VARIANT == sqlType) {
            jdbcType = JDBCType.SQL_VARIANT;
        }
        if (SSType.UDT == sqlType) {
            if (typeInfo.getSSTypeName().equalsIgnoreCase(SSType.GEOMETRY.name())) {
                jdbcType = JDBCType.GEOMETRY;
            }
            if (typeInfo.getSSTypeName().equalsIgnoreCase(SSType.GEOGRAPHY.name())) {
                jdbcType = JDBCType.GEOGRAPHY;
            }
        }
        int r = jdbcType.asJavaSqlType();
        if (this.con.isKatmaiOrLater()) {
            switch (sqlType) {
                case VARCHARMAX: {
                    r = SSType.VARCHAR.getJDBCType().asJavaSqlType();
                    break;
                }
                case NVARCHARMAX: {
                    r = SSType.NVARCHAR.getJDBCType().asJavaSqlType();
                    break;
                }
                case VARBINARYMAX: {
                    r = SSType.VARBINARY.getJDBCType().asJavaSqlType();
                    break;
                }
                case DATETIME: 
                case SMALLDATETIME: {
                    r = SSType.DATETIME2.getJDBCType().asJavaSqlType();
                    break;
                }
                case MONEY: 
                case SMALLMONEY: {
                    r = SSType.DECIMAL.getJDBCType().asJavaSqlType();
                    break;
                }
                case GUID: {
                    r = SSType.CHAR.getJDBCType().asJavaSqlType();
                    break;
                }
            }
        }
        return r;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSTypeName();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSTypeName();
    }

    @Override
    public int getPrecision(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getPrecision();
        }
        return this.rs.getColumn(column).getTypeInfo().getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getScale();
        }
        return this.rs.getColumn(column).getTypeInfo().getScale();
    }

    @Override
    public String getSchemaName(int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getSchemaName();
    }

    @Override
    public String getTableName(int column) throws SQLServerException {
        return this.rs.getColumn(column).getTableName().getObjectName();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isIdentity();
        }
        return this.rs.getColumn(column).getTypeInfo().isIdentity();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isCaseSensitive();
        }
        return this.rs.getColumn(column).getTypeInfo().isCaseSensitive();
    }

    @Override
    public boolean isCurrency(int column) throws SQLServerException {
        SSType ssType = this.rs.getColumn(column).getTypeInfo().getSSType();
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            ssType = cryptoMetadata.getBaseTypeInfo().getSSType();
        }
        return SSType.MONEY == ssType || SSType.SMALLMONEY == ssType;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return 1 == cryptoMetadata.getBaseTypeInfo().getUpdatability();
        }
        return 1 == this.rs.getColumn(column).getTypeInfo().getUpdatability();
    }

    @Override
    public int isNullable(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isNullable() ? 1 : 0;
        }
        return this.rs.getColumn(column).getTypeInfo().isNullable() ? 1 : 0;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return 0 == cryptoMetadata.getBaseTypeInfo().getUpdatability();
        }
        return 0 == this.rs.getColumn(column).getTypeInfo().getUpdatability();
    }

    @Override
    public boolean isSearchable(int column) throws SQLServerException {
        SSType ssType = null;
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        ssType = null != cryptoMetadata ? cryptoMetadata.getBaseTypeInfo().getSSType() : this.rs.getColumn(column).getTypeInfo().getSSType();
        switch (ssType) {
            case IMAGE: 
            case TEXT: 
            case NTEXT: 
            case UDT: 
            case XML: {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSigned(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().isSigned();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSType().getJDBCType().isSigned();
    }

    @Override
    public boolean isSparseColumnSet(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().isSparseColumnSet();
        }
        return this.rs.getColumn(column).getTypeInfo().isSparseColumnSet();
    }

    @Override
    public boolean isWritable(int column) throws SQLServerException {
        int updatability = -1;
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        updatability = null != cryptoMetadata ? cryptoMetadata.getBaseTypeInfo().getUpdatability() : this.rs.getColumn(column).getTypeInfo().getUpdatability();
        return 1 == updatability || 2 == updatability;
    }

    @Override
    public String getColumnClassName(int column) throws SQLServerException {
        CryptoMetadata cryptoMetadata = this.rs.getColumn(column).getCryptoMetadata();
        if (null != cryptoMetadata) {
            return cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().className();
        }
        return this.rs.getColumn(column).getTypeInfo().getSSType().getJDBCType().className();
    }
}

