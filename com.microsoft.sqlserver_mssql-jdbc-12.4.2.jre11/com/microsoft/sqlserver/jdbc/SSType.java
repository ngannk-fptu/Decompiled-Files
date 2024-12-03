/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.EnumSet;

enum SSType {
    UNKNOWN(Category.UNKNOWN, "unknown", JDBCType.UNKNOWN),
    TINYINT(Category.NUMERIC, "tinyint", JDBCType.TINYINT),
    BIT(Category.NUMERIC, "bit", JDBCType.BIT),
    SMALLINT(Category.NUMERIC, "smallint", JDBCType.SMALLINT),
    INTEGER(Category.NUMERIC, "int", JDBCType.INTEGER),
    BIGINT(Category.NUMERIC, "bigint", JDBCType.BIGINT),
    FLOAT(Category.NUMERIC, "float", JDBCType.DOUBLE),
    REAL(Category.NUMERIC, "real", JDBCType.REAL),
    SMALLDATETIME(Category.DATETIME, "smalldatetime", JDBCType.SMALLDATETIME),
    DATETIME(Category.DATETIME, "datetime", JDBCType.DATETIME),
    DATE(Category.DATE, "date", JDBCType.DATE),
    TIME(Category.TIME, "time", JDBCType.TIME),
    DATETIME2(Category.DATETIME2, "datetime2", JDBCType.TIMESTAMP),
    DATETIMEOFFSET(Category.DATETIMEOFFSET, "datetimeoffset", JDBCType.DATETIMEOFFSET),
    SMALLMONEY(Category.NUMERIC, "smallmoney", JDBCType.SMALLMONEY),
    MONEY(Category.NUMERIC, "money", JDBCType.MONEY),
    CHAR(Category.CHARACTER, "char", JDBCType.CHAR),
    VARCHAR(Category.CHARACTER, "varchar", JDBCType.VARCHAR),
    VARCHARMAX(Category.LONG_CHARACTER, "varchar", JDBCType.LONGVARCHAR),
    TEXT(Category.LONG_CHARACTER, "text", JDBCType.LONGVARCHAR),
    NCHAR(Category.NCHARACTER, "nchar", JDBCType.NCHAR),
    NVARCHAR(Category.NCHARACTER, "nvarchar", JDBCType.NVARCHAR),
    NVARCHARMAX(Category.LONG_NCHARACTER, "nvarchar", JDBCType.LONGNVARCHAR),
    NTEXT(Category.LONG_NCHARACTER, "ntext", JDBCType.LONGNVARCHAR),
    BINARY(Category.BINARY, "binary", JDBCType.BINARY),
    VARBINARY(Category.BINARY, "varbinary", JDBCType.VARBINARY),
    VARBINARYMAX(Category.LONG_BINARY, "varbinary", JDBCType.LONGVARBINARY),
    IMAGE(Category.LONG_BINARY, "image", JDBCType.LONGVARBINARY),
    DECIMAL(Category.NUMERIC, "decimal", JDBCType.DECIMAL),
    NUMERIC(Category.NUMERIC, "numeric", JDBCType.NUMERIC),
    GUID(Category.GUID, "uniqueidentifier", JDBCType.GUID),
    SQL_VARIANT(Category.SQL_VARIANT, "sql_variant", JDBCType.SQL_VARIANT),
    UDT(Category.UDT, "udt", JDBCType.VARBINARY),
    XML(Category.XML, "xml", JDBCType.LONGNVARCHAR),
    TIMESTAMP(Category.TIMESTAMP, "timestamp", JDBCType.BINARY),
    GEOMETRY(Category.UDT, "geometry", JDBCType.GEOMETRY),
    GEOGRAPHY(Category.UDT, "geography", JDBCType.GEOGRAPHY);

    final Category category;
    private final String name;
    private final JDBCType jdbcType;
    private static final SSType[] VALUES;
    static final BigDecimal MAX_VALUE_MONEY;
    static final BigDecimal MIN_VALUE_MONEY;
    static final BigDecimal MAX_VALUE_SMALLMONEY;
    static final BigDecimal MIN_VALUE_SMALLMONEY;

    private SSType(Category category, String name, JDBCType jdbcType) {
        this.category = category;
        this.name = name;
        this.jdbcType = jdbcType;
    }

    public String toString() {
        return this.name;
    }

    final JDBCType getJDBCType() {
        return this.jdbcType;
    }

    static SSType of(String typeName) throws SQLServerException {
        for (SSType ssType : VALUES) {
            if (!ssType.name.equalsIgnoreCase(typeName)) continue;
            return ssType;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownSSType"));
        Object[] msgArgs = new Object[]{typeName};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return UNKNOWN;
    }

    boolean convertsTo(JDBCType jdbcType) {
        return GetterConversion.converts(this, jdbcType);
    }

    static {
        VALUES = SSType.values();
        MAX_VALUE_MONEY = new BigDecimal("922337203685477.5807");
        MIN_VALUE_MONEY = new BigDecimal("-922337203685477.5808");
        MAX_VALUE_SMALLMONEY = new BigDecimal("214748.3647");
        MIN_VALUE_SMALLMONEY = new BigDecimal("-214748.3648");
    }

    static enum GetterConversion {
        NUMERIC(Category.NUMERIC, EnumSet.of(JDBCType.Category.NUMERIC, JDBCType.Category.CHARACTER, JDBCType.Category.BINARY)),
        DATETIME(Category.DATETIME, EnumSet.of(JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER, JDBCType.Category.BINARY)),
        DATETIME2(Category.DATETIME2, EnumSet.of(JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER)),
        DATE(Category.DATE, EnumSet.of(JDBCType.Category.DATE, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER)),
        TIME(Category.TIME, EnumSet.of(JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER)),
        DATETIMEOFFSET(Category.DATETIMEOFFSET, EnumSet.of(JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.DATETIMEOFFSET, JDBCType.Category.CHARACTER)),
        CHARACTER(Category.CHARACTER, EnumSet.of(JDBCType.Category.NUMERIC, new JDBCType.Category[]{JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.BINARY, JDBCType.Category.GUID})),
        LONG_CHARACTER(Category.LONG_CHARACTER, EnumSet.of(JDBCType.Category.NUMERIC, new JDBCType.Category[]{JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.BINARY, JDBCType.Category.CLOB})),
        NCHARACTER(Category.NCHARACTER, EnumSet.of(JDBCType.Category.NUMERIC, new JDBCType.Category[]{JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.NCHARACTER, JDBCType.Category.LONG_NCHARACTER, JDBCType.Category.BINARY, JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP})),
        LONG_NCHARACTER(Category.LONG_NCHARACTER, EnumSet.of(JDBCType.Category.NUMERIC, new JDBCType.Category[]{JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.NCHARACTER, JDBCType.Category.LONG_NCHARACTER, JDBCType.Category.BINARY, JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.TIMESTAMP, JDBCType.Category.CLOB, JDBCType.Category.NCLOB})),
        BINARY(Category.BINARY, EnumSet.of(JDBCType.Category.BINARY, JDBCType.Category.LONG_BINARY, JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.GUID)),
        LONG_BINARY(Category.LONG_BINARY, EnumSet.of(JDBCType.Category.BINARY, JDBCType.Category.LONG_BINARY, JDBCType.Category.CHARACTER, JDBCType.Category.LONG_CHARACTER, JDBCType.Category.BLOB)),
        TIMESTAMP(Category.TIMESTAMP, EnumSet.of(JDBCType.Category.BINARY, JDBCType.Category.LONG_BINARY, JDBCType.Category.CHARACTER)),
        XML(Category.XML, EnumSet.of(JDBCType.Category.CHARACTER, new JDBCType.Category[]{JDBCType.Category.LONG_CHARACTER, JDBCType.Category.CLOB, JDBCType.Category.NCHARACTER, JDBCType.Category.LONG_NCHARACTER, JDBCType.Category.NCLOB, JDBCType.Category.BINARY, JDBCType.Category.LONG_BINARY, JDBCType.Category.BLOB, JDBCType.Category.SQLXML})),
        UDT(Category.UDT, EnumSet.of(JDBCType.Category.BINARY, JDBCType.Category.LONG_BINARY, JDBCType.Category.CHARACTER, JDBCType.Category.GEOMETRY, JDBCType.Category.GEOGRAPHY)),
        GUID(Category.GUID, EnumSet.of(JDBCType.Category.BINARY, JDBCType.Category.CHARACTER)),
        SQL_VARIANT(Category.SQL_VARIANT, EnumSet.of(JDBCType.Category.CHARACTER, new JDBCType.Category[]{JDBCType.Category.SQL_VARIANT, JDBCType.Category.NUMERIC, JDBCType.Category.DATE, JDBCType.Category.TIME, JDBCType.Category.BINARY, JDBCType.Category.TIMESTAMP, JDBCType.Category.NCHARACTER, JDBCType.Category.GUID}));

        private final Category from;
        private final EnumSet<JDBCType.Category> to;
        private static final GetterConversion[] VALUES;
        private static final EnumMap<Category, EnumSet<JDBCType.Category>> conversionMap;

        private GetterConversion(Category from, EnumSet<JDBCType.Category> to) {
            this.from = from;
            this.to = to;
        }

        static final boolean converts(SSType fromSSType, JDBCType toJDBCType) {
            return conversionMap.get((Object)fromSSType.category).contains((Object)toJDBCType.category);
        }

        static {
            VALUES = GetterConversion.values();
            conversionMap = new EnumMap(Category.class);
            for (Category category : Category.VALUES) {
                conversionMap.put(category, EnumSet.noneOf(JDBCType.Category.class));
            }
            for (Enum enum_ : VALUES) {
                conversionMap.get((Object)((GetterConversion)enum_).from).addAll(((GetterConversion)enum_).to);
            }
        }
    }

    static enum Category {
        BINARY,
        CHARACTER,
        DATE,
        DATETIME,
        DATETIME2,
        DATETIMEOFFSET,
        GUID,
        LONG_BINARY,
        LONG_CHARACTER,
        LONG_NCHARACTER,
        NCHARACTER,
        NUMERIC,
        UNKNOWN,
        TIME,
        TIMESTAMP,
        UDT,
        SQL_VARIANT,
        XML;

        private static final Category[] VALUES;

        static {
            VALUES = Category.values();
        }
    }
}

