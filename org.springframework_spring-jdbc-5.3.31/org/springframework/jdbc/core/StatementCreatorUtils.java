/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.SpringProperties
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.jdbc.core.DisposableSqlTypeValue;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.support.SqlValue;
import org.springframework.lang.Nullable;

public abstract class StatementCreatorUtils {
    public static final String IGNORE_GETPARAMETERTYPE_PROPERTY_NAME = "spring.jdbc.getParameterType.ignore";
    static boolean shouldIgnoreGetParameterType = SpringProperties.getFlag((String)"spring.jdbc.getParameterType.ignore");
    private static final Log logger = LogFactory.getLog(StatementCreatorUtils.class);
    private static final Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap(32);

    public static int javaTypeToSqlParameterType(@Nullable Class<?> javaType) {
        if (javaType == null) {
            return Integer.MIN_VALUE;
        }
        Integer sqlType = javaTypeToSqlTypeMap.get(javaType);
        if (sqlType != null) {
            return sqlType;
        }
        if (Number.class.isAssignableFrom(javaType)) {
            return 2;
        }
        if (StatementCreatorUtils.isStringValue(javaType)) {
            return 12;
        }
        if (StatementCreatorUtils.isDateValue(javaType) || Calendar.class.isAssignableFrom(javaType)) {
            return 93;
        }
        return Integer.MIN_VALUE;
    }

    public static void setParameterValue(PreparedStatement ps, int paramIndex, SqlParameter param, @Nullable Object inValue) throws SQLException {
        StatementCreatorUtils.setParameterValueInternal(ps, paramIndex, param.getSqlType(), param.getTypeName(), param.getScale(), inValue);
    }

    public static void setParameterValue(PreparedStatement ps, int paramIndex, int sqlType, @Nullable Object inValue) throws SQLException {
        StatementCreatorUtils.setParameterValueInternal(ps, paramIndex, sqlType, null, null, inValue);
    }

    public static void setParameterValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName, @Nullable Object inValue) throws SQLException {
        StatementCreatorUtils.setParameterValueInternal(ps, paramIndex, sqlType, typeName, null, inValue);
    }

    private static void setParameterValueInternal(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName, @Nullable Integer scale, @Nullable Object inValue) throws SQLException {
        String typeNameToUse = typeName;
        int sqlTypeToUse = sqlType;
        Object inValueToUse = inValue;
        if (inValue instanceof SqlParameterValue) {
            SqlParameterValue parameterValue = (SqlParameterValue)inValue;
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Overriding type info with runtime info from SqlParameterValue: column index " + paramIndex + ", SQL type " + parameterValue.getSqlType() + ", type name " + parameterValue.getTypeName()));
            }
            if (parameterValue.getSqlType() != Integer.MIN_VALUE) {
                sqlTypeToUse = parameterValue.getSqlType();
            }
            if (parameterValue.getTypeName() != null) {
                typeNameToUse = parameterValue.getTypeName();
            }
            inValueToUse = parameterValue.getValue();
        }
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Setting SQL statement parameter value: column index " + paramIndex + ", parameter value [" + inValueToUse + "], value class [" + (inValueToUse != null ? inValueToUse.getClass().getName() : "null") + "], SQL type " + (sqlTypeToUse == Integer.MIN_VALUE ? "unknown" : Integer.toString(sqlTypeToUse))));
        }
        if (inValueToUse == null) {
            StatementCreatorUtils.setNull(ps, paramIndex, sqlTypeToUse, typeNameToUse);
        } else {
            StatementCreatorUtils.setValue(ps, paramIndex, sqlTypeToUse, typeNameToUse, scale, inValueToUse);
        }
    }

    private static void setNull(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName) throws SQLException {
        if (sqlType == Integer.MIN_VALUE || sqlType == 1111 && typeName == null) {
            Integer sqlTypeToUse;
            boolean useSetObject;
            block13: {
                useSetObject = false;
                sqlTypeToUse = null;
                if (!shouldIgnoreGetParameterType) {
                    try {
                        sqlTypeToUse = ps.getParameterMetaData().getParameterType(paramIndex);
                    }
                    catch (SQLException ex) {
                        if (!logger.isDebugEnabled()) break block13;
                        logger.debug((Object)("JDBC getParameterType call failed - using fallback method instead: " + ex));
                    }
                }
            }
            if (sqlTypeToUse == null) {
                sqlTypeToUse = 0;
                DatabaseMetaData dbmd = ps.getConnection().getMetaData();
                String jdbcDriverName = dbmd.getDriverName();
                String databaseProductName = dbmd.getDatabaseProductName();
                if (databaseProductName.startsWith("Informix") || jdbcDriverName.startsWith("Microsoft") && jdbcDriverName.contains("SQL Server")) {
                    useSetObject = true;
                } else if (databaseProductName.startsWith("DB2") || jdbcDriverName.startsWith("jConnect") || jdbcDriverName.startsWith("SQLServer") || jdbcDriverName.startsWith("Apache Derby")) {
                    sqlTypeToUse = 12;
                }
            }
            if (useSetObject) {
                ps.setObject(paramIndex, null);
            } else {
                ps.setNull(paramIndex, sqlTypeToUse);
            }
        } else if (typeName != null) {
            ps.setNull(paramIndex, sqlType, typeName);
        } else {
            ps.setNull(paramIndex, sqlType);
        }
    }

    private static void setValue(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName, @Nullable Integer scale, Object inValue) throws SQLException {
        if (inValue instanceof SqlTypeValue) {
            ((SqlTypeValue)inValue).setTypeValue(ps, paramIndex, sqlType, typeName);
        } else if (inValue instanceof SqlValue) {
            ((SqlValue)inValue).setValue(ps, paramIndex);
        } else if (sqlType == 12 || sqlType == -1) {
            ps.setString(paramIndex, inValue.toString());
        } else if (sqlType == -9 || sqlType == -16) {
            ps.setNString(paramIndex, inValue.toString());
        } else if ((sqlType == 2005 || sqlType == 2011) && StatementCreatorUtils.isStringValue(inValue.getClass())) {
            String strVal = inValue.toString();
            if (strVal.length() > 4000) {
                if (sqlType == 2011) {
                    ps.setNClob(paramIndex, new StringReader(strVal), strVal.length());
                } else {
                    ps.setClob(paramIndex, new StringReader(strVal), strVal.length());
                }
            } else if (sqlType == 2011) {
                ps.setNString(paramIndex, strVal);
            } else {
                ps.setString(paramIndex, strVal);
            }
        } else if (sqlType == 3 || sqlType == 2) {
            if (inValue instanceof BigDecimal) {
                ps.setBigDecimal(paramIndex, (BigDecimal)inValue);
            } else if (scale != null) {
                ps.setObject(paramIndex, inValue, sqlType, (int)scale);
            } else {
                ps.setObject(paramIndex, inValue, sqlType);
            }
        } else if (sqlType == 16) {
            if (inValue instanceof Boolean) {
                ps.setBoolean(paramIndex, (Boolean)inValue);
            } else {
                ps.setObject(paramIndex, inValue, 16);
            }
        } else if (sqlType == 91) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Date) {
                    ps.setDate(paramIndex, (Date)inValue);
                } else {
                    ps.setDate(paramIndex, new Date(((java.util.Date)inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar)inValue;
                ps.setDate(paramIndex, new Date(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, 91);
            }
        } else if (sqlType == 92) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Time) {
                    ps.setTime(paramIndex, (Time)inValue);
                } else {
                    ps.setTime(paramIndex, new Time(((java.util.Date)inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar)inValue;
                ps.setTime(paramIndex, new Time(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, 92);
            }
        } else if (sqlType == 93) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof Timestamp) {
                    ps.setTimestamp(paramIndex, (Timestamp)inValue);
                } else {
                    ps.setTimestamp(paramIndex, new Timestamp(((java.util.Date)inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar)inValue;
                ps.setTimestamp(paramIndex, new Timestamp(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, 93);
            }
        } else if (sqlType == Integer.MIN_VALUE || sqlType == 1111 && "Oracle".equals(ps.getConnection().getMetaData().getDatabaseProductName())) {
            if (StatementCreatorUtils.isStringValue(inValue.getClass())) {
                ps.setString(paramIndex, inValue.toString());
            } else if (StatementCreatorUtils.isDateValue(inValue.getClass())) {
                ps.setTimestamp(paramIndex, new Timestamp(((java.util.Date)inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar)inValue;
                ps.setTimestamp(paramIndex, new Timestamp(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue);
            }
        } else {
            ps.setObject(paramIndex, inValue, sqlType);
        }
    }

    private static boolean isStringValue(Class<?> inValueType) {
        return CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class.isAssignableFrom(inValueType);
    }

    private static boolean isDateValue(Class<?> inValueType) {
        return java.util.Date.class.isAssignableFrom(inValueType) && !Date.class.isAssignableFrom(inValueType) && !Time.class.isAssignableFrom(inValueType) && !Timestamp.class.isAssignableFrom(inValueType);
    }

    public static void cleanupParameters(Object ... paramValues) {
        if (paramValues != null) {
            StatementCreatorUtils.cleanupParameters(Arrays.asList(paramValues));
        }
    }

    public static void cleanupParameters(@Nullable Collection<?> paramValues) {
        if (paramValues != null) {
            for (Object inValue : paramValues) {
                if (inValue instanceof SqlParameterValue) {
                    inValue = ((SqlParameterValue)inValue).getValue();
                }
                if (inValue instanceof SqlValue) {
                    ((SqlValue)inValue).cleanup();
                    continue;
                }
                if (!(inValue instanceof DisposableSqlTypeValue)) continue;
                ((DisposableSqlTypeValue)inValue).cleanup();
            }
        }
    }

    static {
        javaTypeToSqlTypeMap.put(Boolean.TYPE, 16);
        javaTypeToSqlTypeMap.put(Boolean.class, 16);
        javaTypeToSqlTypeMap.put(Byte.TYPE, -6);
        javaTypeToSqlTypeMap.put(Byte.class, -6);
        javaTypeToSqlTypeMap.put(Short.TYPE, 5);
        javaTypeToSqlTypeMap.put(Short.class, 5);
        javaTypeToSqlTypeMap.put(Integer.TYPE, 4);
        javaTypeToSqlTypeMap.put(Integer.class, 4);
        javaTypeToSqlTypeMap.put(Long.TYPE, -5);
        javaTypeToSqlTypeMap.put(Long.class, -5);
        javaTypeToSqlTypeMap.put(BigInteger.class, -5);
        javaTypeToSqlTypeMap.put(Float.TYPE, 6);
        javaTypeToSqlTypeMap.put(Float.class, 6);
        javaTypeToSqlTypeMap.put(Double.TYPE, 8);
        javaTypeToSqlTypeMap.put(Double.class, 8);
        javaTypeToSqlTypeMap.put(BigDecimal.class, 3);
        javaTypeToSqlTypeMap.put(LocalDate.class, 91);
        javaTypeToSqlTypeMap.put(LocalTime.class, 92);
        javaTypeToSqlTypeMap.put(LocalDateTime.class, 93);
        javaTypeToSqlTypeMap.put(Date.class, 91);
        javaTypeToSqlTypeMap.put(Time.class, 92);
        javaTypeToSqlTypeMap.put(Timestamp.class, 93);
        javaTypeToSqlTypeMap.put(Blob.class, 2004);
        javaTypeToSqlTypeMap.put(Clob.class, 2005);
    }
}

