/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql;

import com.mchange.lang.ThrowableUtils;
import com.mchange.v2.lang.VersionUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SqlUtils {
    static final MLogger logger = MLog.getLogger(SqlUtils.class);
    static final DateFormat tsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
    public static final String DRIVER_MANAGER_USER_PROPERTY = "user";
    public static final String DRIVER_MANAGER_PASSWORD_PROPERTY = "password";

    public static String escapeBadSqlPatternChars(String string) {
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = stringBuffer.length();
        for (int i = 0; i < n; ++i) {
            if (stringBuffer.charAt(i) != '\'') continue;
            stringBuffer.insert(i, '\'');
            ++n;
            i += 2;
        }
        return stringBuffer.toString();
    }

    public static synchronized String escapeAsTimestamp(Date date) {
        return "{ts '" + tsdf.format(date) + "'}";
    }

    public static SQLException toSQLException(Throwable throwable) {
        return SqlUtils.toSQLException(null, throwable);
    }

    public static SQLException toSQLException(String string, Throwable throwable) {
        return SqlUtils.toSQLException(string, null, throwable);
    }

    public static SQLException toSQLException(String string, String string2, Throwable throwable) {
        if (throwable instanceof SQLException) {
            if (logger.isLoggable(MLevel.FINER)) {
                SQLException sQLException = (SQLException)throwable;
                StringBuffer stringBuffer = new StringBuffer(255);
                stringBuffer.append("Attempted to convert SQLException to SQLException. Leaving it alone.");
                stringBuffer.append(" [SQLState: ");
                stringBuffer.append(sQLException.getSQLState());
                stringBuffer.append("; errorCode: ");
                stringBuffer.append(sQLException.getErrorCode());
                stringBuffer.append(']');
                if (string != null) {
                    stringBuffer.append(" Ignoring suggested message: '" + string + "'.");
                }
                logger.log(MLevel.FINER, stringBuffer.toString(), throwable);
                SQLException sQLException2 = sQLException;
                while ((sQLException2 = sQLException2.getNextException()) != null) {
                    logger.log(MLevel.FINER, "Nested SQLException or SQLWarning: ", sQLException2);
                }
            }
            return (SQLException)throwable;
        }
        if (logger.isLoggable(MLevel.FINE)) {
            logger.log(MLevel.FINE, "Converting Throwable to SQLException...", throwable);
        }
        if (string == null) {
            string = "An SQLException was provoked by the following failure: " + throwable.toString();
        }
        if (VersionUtils.isAtLeastJavaVersion1_4()) {
            SQLException sQLException = new SQLException(string);
            sQLException.initCause(throwable);
            return sQLException;
        }
        return new SQLException(string + System.getProperty("line.separator") + "[Cause: " + ThrowableUtils.extractStackTrace(throwable) + ']', string2);
    }

    public static SQLClientInfoException toSQLClientInfoException(Throwable throwable) {
        if (throwable instanceof SQLClientInfoException) {
            return (SQLClientInfoException)throwable;
        }
        if (throwable.getCause() instanceof SQLClientInfoException) {
            return (SQLClientInfoException)throwable.getCause();
        }
        if (throwable instanceof SQLException) {
            SQLException sQLException = (SQLException)throwable;
            return new SQLClientInfoException(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode(), null, throwable);
        }
        return new SQLClientInfoException(throwable.getMessage(), null, throwable);
    }

    private SqlUtils() {
    }
}

