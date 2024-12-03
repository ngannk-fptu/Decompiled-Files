/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.TVP;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import microsoft.sql.DateTimeOffset;

enum JavaType {
    INTEGER(Integer.class, JDBCType.INTEGER),
    STRING(String.class, JDBCType.CHAR),
    DATE(Date.class, JDBCType.DATE),
    TIME(Time.class, JDBCType.TIME),
    TIMESTAMP(Timestamp.class, JDBCType.TIMESTAMP),
    UTILDATE(java.util.Date.class, JDBCType.TIMESTAMP),
    CALENDAR(Calendar.class, JDBCType.TIMESTAMP),
    LOCALDATE(JavaType.getJavaClass("LocalDate"), JDBCType.DATE),
    LOCALTIME(JavaType.getJavaClass("LocalTime"), JDBCType.TIME),
    LOCALDATETIME(JavaType.getJavaClass("LocalDateTime"), JDBCType.TIMESTAMP),
    OFFSETTIME(JavaType.getJavaClass("OffsetTime"), JDBCType.TIME_WITH_TIMEZONE),
    OFFSETDATETIME(JavaType.getJavaClass("OffsetDateTime"), JDBCType.TIMESTAMP_WITH_TIMEZONE),
    DATETIMEOFFSET(DateTimeOffset.class, JDBCType.DATETIMEOFFSET),
    BOOLEAN(Boolean.class, JDBCType.BIT),
    BIGDECIMAL(BigDecimal.class, JDBCType.DECIMAL),
    DOUBLE(Double.class, JDBCType.DOUBLE),
    FLOAT(Float.class, JDBCType.REAL),
    SHORT(Short.class, JDBCType.SMALLINT),
    LONG(Long.class, JDBCType.BIGINT),
    BIGINTEGER(BigInteger.class, JDBCType.BIGINT),
    BYTE(Byte.class, JDBCType.TINYINT),
    BYTEARRAY(byte[].class, JDBCType.BINARY),
    NCLOB(NClob.class, JDBCType.NCLOB),
    CLOB(Clob.class, JDBCType.CLOB),
    BLOB(Blob.class, JDBCType.BLOB),
    TVP(TVP.class, JDBCType.TVP),
    GEOMETRY(Geometry.class, JDBCType.GEOMETRY),
    GEOGRAPHY(Geography.class, JDBCType.GEOGRAPHY),
    INPUTSTREAM((Class)InputStream.class, JDBCType.UNKNOWN){

        @Override
        JDBCType getJDBCType(SSType ssType, JDBCType jdbcTypeFromApp) {
            JDBCType jdbcType;
            if (SSType.UNKNOWN != ssType) {
                switch (ssType) {
                    case CHAR: 
                    case VARCHAR: 
                    case VARCHARMAX: 
                    case TEXT: 
                    case NCHAR: 
                    case NVARCHAR: 
                    case NVARCHARMAX: 
                    case NTEXT: {
                        jdbcType = JDBCType.LONGVARCHAR;
                        break;
                    }
                    default: {
                        jdbcType = JDBCType.LONGVARBINARY;
                        break;
                    }
                }
            } else {
                JDBCType jDBCType = jdbcType = jdbcTypeFromApp.isTextual() ? JDBCType.LONGVARCHAR : JDBCType.LONGVARBINARY;
            }
            assert (null != jdbcType);
            return jdbcType;
        }
    }
    ,
    READER(Reader.class, JDBCType.LONGVARCHAR),
    SQLXML(SQLServerSQLXML.class, JDBCType.SQLXML),
    OBJECT(Object.class, JDBCType.UNKNOWN);

    private final Class<?> javaClass;
    private final JDBCType jdbcTypeFromJavaType;
    private static double jvmVersion;
    private static final JavaType[] VALUES;

    private JavaType(Class<?> javaClass, JDBCType jdbcTypeFromJavaType) {
        this.javaClass = javaClass;
        this.jdbcTypeFromJavaType = jdbcTypeFromJavaType;
    }

    static Class<?> getJavaClass(String className) {
        if (0.0 == jvmVersion) {
            try {
                String jvmSpecVersion = System.getProperty("java.specification.version");
                if (jvmSpecVersion != null) {
                    jvmVersion = Double.parseDouble(jvmSpecVersion);
                }
            }
            catch (NumberFormatException e) {
                jvmVersion = 0.1;
            }
        }
        if (jvmVersion < 1.8) {
            return null;
        }
        switch (className) {
            case "LocalDate": {
                return LocalDate.class;
            }
            case "LocalTime": {
                return LocalTime.class;
            }
            case "LocalDateTime": {
                return LocalDateTime.class;
            }
            case "OffsetTime": {
                return OffsetTime.class;
            }
            case "OffsetDateTime": {
                return OffsetDateTime.class;
            }
        }
        return null;
    }

    static JavaType of(Object obj) {
        if (obj instanceof SQLServerDataTable || obj instanceof ResultSet || obj instanceof ISQLServerDataRecord) {
            return TVP;
        }
        if (null != obj) {
            for (JavaType javaType : VALUES) {
                if (null == javaType.javaClass || !javaType.javaClass.isInstance(obj)) continue;
                return javaType;
            }
        }
        return OBJECT;
    }

    JDBCType getJDBCType(SSType ssType, JDBCType jdbcTypeFromApp) {
        return this.jdbcTypeFromJavaType;
    }

    static {
        jvmVersion = 0.0;
        VALUES = JavaType.values();
    }

    static enum SetterConversionAE {
        BIT(BOOLEAN, EnumSet.of(JDBCType.BIT, JDBCType.TINYINT, JDBCType.SMALLINT, JDBCType.INTEGER, JDBCType.BIGINT)),
        SHORT(SHORT, EnumSet.of(JDBCType.TINYINT, JDBCType.SMALLINT, JDBCType.INTEGER, JDBCType.BIGINT)),
        INTEGER(INTEGER, EnumSet.of(JDBCType.INTEGER, JDBCType.BIGINT)),
        LONG(LONG, EnumSet.of(JDBCType.BIGINT)),
        BIGDECIMAL(BIGDECIMAL, EnumSet.of(JDBCType.MONEY, JDBCType.SMALLMONEY, JDBCType.DECIMAL, JDBCType.NUMERIC)),
        BYTE(BYTE, EnumSet.of(JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY, JDBCType.TINYINT)),
        BYTEARRAY(BYTEARRAY, EnumSet.of(JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY)),
        DATE(DATE, EnumSet.of(JDBCType.DATE)),
        DATETIMEOFFSET(DATETIMEOFFSET, EnumSet.of(JDBCType.DATETIMEOFFSET)),
        DOUBLE(DOUBLE, EnumSet.of(JDBCType.DOUBLE)),
        FLOAT(FLOAT, EnumSet.of(JDBCType.REAL, JDBCType.DOUBLE)),
        STRING(STRING, EnumSet.of(JDBCType.CHAR, new JDBCType[]{JDBCType.VARCHAR, JDBCType.LONGVARCHAR, JDBCType.NCHAR, JDBCType.NVARCHAR, JDBCType.LONGNVARCHAR, JDBCType.GUID})),
        TIME(TIME, EnumSet.of(JDBCType.TIME)),
        TIMESTAMP(TIMESTAMP, EnumSet.of(JDBCType.TIME, JDBCType.TIMESTAMP, JDBCType.DATETIME, JDBCType.SMALLDATETIME));

        private final EnumSet<JDBCType> to;
        private final JavaType from;
        private static final SetterConversionAE[] VALUES;
        private static final EnumMap<JavaType, EnumSet<JDBCType>> setterConversionAEMap;

        private SetterConversionAE(JavaType from, EnumSet<JDBCType> to) {
            this.from = from;
            this.to = to;
        }

        static boolean converts(JavaType fromJavaType, JDBCType toJDBCType, boolean sendStringParametersAsUnicode) {
            if (null == fromJavaType || OBJECT == fromJavaType) {
                return true;
            }
            if (!(sendStringParametersAsUnicode || fromJavaType != BYTEARRAY || toJDBCType != JDBCType.VARCHAR && toJDBCType != JDBCType.CHAR && toJDBCType != JDBCType.LONGVARCHAR)) {
                return true;
            }
            if (!setterConversionAEMap.containsKey((Object)fromJavaType)) {
                return false;
            }
            return setterConversionAEMap.get((Object)fromJavaType).contains((Object)toJDBCType);
        }

        static {
            VALUES = SetterConversionAE.values();
            setterConversionAEMap = new EnumMap(JavaType.class);
            for (JavaType javaType : VALUES) {
                setterConversionAEMap.put(javaType, EnumSet.noneOf(JDBCType.class));
            }
            for (Enum enum_ : VALUES) {
                setterConversionAEMap.get((Object)((SetterConversionAE)enum_).from).addAll(((SetterConversionAE)enum_).to);
            }
        }
    }
}

