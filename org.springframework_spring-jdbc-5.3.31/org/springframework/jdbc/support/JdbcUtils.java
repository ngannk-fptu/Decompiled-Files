/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.NumberUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.support;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

public abstract class JdbcUtils {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;
    private static final Log logger = LogFactory.getLog(JdbcUtils.class);
    private static final Map<Integer, String> typeNames = new HashMap<Integer, String>();

    public static void closeConnection(@Nullable Connection con) {
        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException ex) {
                logger.debug((Object)"Could not close JDBC Connection", (Throwable)ex);
            }
            catch (Throwable ex) {
                logger.debug((Object)"Unexpected exception on closing JDBC Connection", ex);
            }
        }
    }

    public static void closeStatement(@Nullable Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException ex) {
                logger.trace((Object)"Could not close JDBC Statement", (Throwable)ex);
            }
            catch (Throwable ex) {
                logger.trace((Object)"Unexpected exception on closing JDBC Statement", ex);
            }
        }
    }

    public static void closeResultSet(@Nullable ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException ex) {
                logger.trace((Object)"Could not close JDBC ResultSet", (Throwable)ex);
            }
            catch (Throwable ex) {
                logger.trace((Object)"Unexpected exception on closing JDBC ResultSet", ex);
            }
        }
    }

    @Nullable
    public static Object getResultSetValue(ResultSet rs, int index, @Nullable Class<?> requiredType) throws SQLException {
        Comparable<Boolean> value;
        if (requiredType == null) {
            return JdbcUtils.getResultSetValue(rs, index);
        }
        if (String.class == requiredType) {
            return rs.getString(index);
        }
        if (Boolean.TYPE == requiredType || Boolean.class == requiredType) {
            value = rs.getBoolean(index);
        } else if (Byte.TYPE == requiredType || Byte.class == requiredType) {
            value = rs.getByte(index);
        } else if (Short.TYPE == requiredType || Short.class == requiredType) {
            value = rs.getShort(index);
        } else if (Integer.TYPE == requiredType || Integer.class == requiredType) {
            value = rs.getInt(index);
        } else if (Long.TYPE == requiredType || Long.class == requiredType) {
            value = rs.getLong(index);
        } else if (Float.TYPE == requiredType || Float.class == requiredType) {
            value = Float.valueOf(rs.getFloat(index));
        } else if (Double.TYPE == requiredType || Double.class == requiredType || Number.class == requiredType) {
            value = rs.getDouble(index);
        } else {
            if (BigDecimal.class == requiredType) {
                return rs.getBigDecimal(index);
            }
            if (Date.class == requiredType) {
                return rs.getDate(index);
            }
            if (Time.class == requiredType) {
                return rs.getTime(index);
            }
            if (Timestamp.class == requiredType || java.util.Date.class == requiredType) {
                return rs.getTimestamp(index);
            }
            if (byte[].class == requiredType) {
                return rs.getBytes(index);
            }
            if (Blob.class == requiredType) {
                return rs.getBlob(index);
            }
            if (Clob.class == requiredType) {
                return rs.getClob(index);
            }
            if (requiredType.isEnum()) {
                Object obj = rs.getObject(index);
                if (obj instanceof String) {
                    return obj;
                }
                if (obj instanceof Number) {
                    return NumberUtils.convertNumberToTargetClass((Number)((Number)obj), Integer.class);
                }
                return rs.getString(index);
            }
            try {
                return rs.getObject(index, requiredType);
            }
            catch (AbstractMethodError err) {
                logger.debug((Object)"JDBC driver does not implement JDBC 4.1 'getObject(int, Class)' method", (Throwable)err);
            }
            catch (SQLFeatureNotSupportedException ex) {
                logger.debug((Object)"JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method", (Throwable)ex);
            }
            catch (SQLException ex) {
                logger.debug((Object)"JDBC driver has limited support for JDBC 4.1 'getObject(int, Class)' method", (Throwable)ex);
            }
            String typeName = requiredType.getSimpleName();
            if ("LocalDate".equals(typeName)) {
                return rs.getDate(index);
            }
            if ("LocalTime".equals(typeName)) {
                return rs.getTime(index);
            }
            if ("LocalDateTime".equals(typeName)) {
                return rs.getTimestamp(index);
            }
            return JdbcUtils.getResultSetValue(rs, index);
        }
        return rs.wasNull() ? null : value;
    }

    @Nullable
    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob)obj;
            obj = blob.getBytes(1L, (int)blob.length());
        } else if (obj instanceof Clob) {
            Clob clob = (Clob)obj;
            obj = clob.getSubString(1L, (int)clob.length());
        } else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            obj = "java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName) ? rs.getTimestamp(index) : rs.getDate(index);
        } else if (obj instanceof Date && "java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
            obj = rs.getTimestamp(index);
        }
        return obj;
    }

    public static <T> T extractDatabaseMetaData(DataSource dataSource, DatabaseMetaDataCallback<T> action) throws MetaDataAccessException {
        Connection con = null;
        try {
            DatabaseMetaData metaData;
            con = DataSourceUtils.getConnection(dataSource);
            try {
                metaData = con.getMetaData();
            }
            catch (SQLException ex) {
                if (DataSourceUtils.isConnectionTransactional(con, dataSource)) {
                    DataSourceUtils.releaseConnection(con, dataSource);
                    con = null;
                    logger.debug((Object)"Failed to obtain DatabaseMetaData from transactional Connection - retrying against fresh Connection", (Throwable)ex);
                    con = dataSource.getConnection();
                    metaData = con.getMetaData();
                }
                throw ex;
            }
            if (metaData == null) {
                throw new MetaDataAccessException("DatabaseMetaData returned by Connection [" + con + "] was null");
            }
            T t = action.processMetaData(metaData);
            return t;
        }
        catch (CannotGetJdbcConnectionException ex) {
            throw new MetaDataAccessException("Could not get Connection for extracting meta-data", (Throwable)((Object)ex));
        }
        catch (SQLException ex) {
            throw new MetaDataAccessException("Error while extracting DatabaseMetaData", ex);
        }
        catch (AbstractMethodError err) {
            throw new MetaDataAccessException("JDBC DatabaseMetaData method not implemented by JDBC driver - upgrade your driver", err);
        }
        finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Deprecated
    public static <T> T extractDatabaseMetaData(DataSource dataSource, String metaDataMethodName) throws MetaDataAccessException {
        return (T)JdbcUtils.extractDatabaseMetaData(dataSource, (DatabaseMetaData dbmd) -> {
            try {
                return DatabaseMetaData.class.getMethod(metaDataMethodName, new Class[0]).invoke((Object)dbmd, new Object[0]);
            }
            catch (NoSuchMethodException ex) {
                throw new MetaDataAccessException("No method named '" + metaDataMethodName + "' found on DatabaseMetaData instance [" + dbmd + "]", ex);
            }
            catch (IllegalAccessException ex) {
                throw new MetaDataAccessException("Could not access DatabaseMetaData method '" + metaDataMethodName + "'", ex);
            }
            catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof SQLException) {
                    throw (SQLException)ex.getTargetException();
                }
                throw new MetaDataAccessException("Invocation of DatabaseMetaData method '" + metaDataMethodName + "' failed", ex);
            }
        });
    }

    public static boolean supportsBatchUpdates(Connection con) {
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            if (dbmd != null) {
                if (dbmd.supportsBatchUpdates()) {
                    logger.debug((Object)"JDBC driver supports batch updates");
                    return true;
                }
                logger.debug((Object)"JDBC driver does not support batch updates");
            }
        }
        catch (SQLException ex) {
            logger.debug((Object)"JDBC driver 'supportsBatchUpdates' method threw exception", (Throwable)ex);
        }
        return false;
    }

    @Nullable
    public static String commonDatabaseName(@Nullable String source) {
        String name = source;
        if (source != null && source.startsWith("DB2")) {
            name = "DB2";
        } else if ("MariaDB".equals(source)) {
            name = "MySQL";
        } else if ("Sybase SQL Server".equals(source) || "Adaptive Server Enterprise".equals(source) || "ASE".equals(source) || "sql server".equalsIgnoreCase(source)) {
            name = "Sybase";
        }
        return name;
    }

    public static boolean isNumeric(int sqlType) {
        return -7 == sqlType || -5 == sqlType || 3 == sqlType || 8 == sqlType || 6 == sqlType || 4 == sqlType || 2 == sqlType || 7 == sqlType || 5 == sqlType || -6 == sqlType;
    }

    @Nullable
    public static String resolveTypeName(int sqlType) {
        return typeNames.get(sqlType);
    }

    public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);
        if (!StringUtils.hasLength((String)name)) {
            name = resultSetMetaData.getColumnName(columnIndex);
        }
        return name;
    }

    public static String convertUnderscoreNameToPropertyName(@Nullable String name) {
        StringBuilder result = new StringBuilder();
        boolean nextIsUpper = false;
        if (name != null && name.length() > 0) {
            if (name.length() > 1 && name.charAt(1) == '_') {
                result.append(Character.toUpperCase(name.charAt(0)));
            } else {
                result.append(Character.toLowerCase(name.charAt(0)));
            }
            for (int i = 1; i < name.length(); ++i) {
                char c = name.charAt(i);
                if (c == '_') {
                    nextIsUpper = true;
                    continue;
                }
                if (nextIsUpper) {
                    result.append(Character.toUpperCase(c));
                    nextIsUpper = false;
                    continue;
                }
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    static {
        try {
            for (Field field : Types.class.getFields()) {
                typeNames.put((Integer)field.get(null), field.getName());
            }
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to resolve JDBC Types constants", ex);
        }
    }
}

