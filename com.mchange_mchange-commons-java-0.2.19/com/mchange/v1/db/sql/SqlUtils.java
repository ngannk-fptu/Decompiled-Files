/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.lang.ThrowableUtils;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SqlUtils {
    static final DateFormat tsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

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

    public static String escapeAsTimestamp(Date date) {
        return "{ts '" + tsdf.format(date) + "'}";
    }

    public static SQLException toSQLException(Throwable throwable) {
        if (throwable instanceof SQLException) {
            return (SQLException)throwable;
        }
        throwable.printStackTrace();
        return new SQLException(ThrowableUtils.extractStackTrace(throwable));
    }

    private SqlUtils() {
    }
}

