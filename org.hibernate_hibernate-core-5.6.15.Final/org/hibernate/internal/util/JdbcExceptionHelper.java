/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.sql.SQLException;

public final class JdbcExceptionHelper {
    private JdbcExceptionHelper() {
    }

    public static int extractErrorCode(SQLException sqlException) {
        int errorCode = sqlException.getErrorCode();
        for (SQLException nested = sqlException.getNextException(); errorCode == 0 && nested != null; nested = nested.getNextException()) {
            errorCode = nested.getErrorCode();
        }
        return errorCode;
    }

    public static String extractSqlState(SQLException sqlException) {
        String sqlState = sqlException.getSQLState();
        for (SQLException nested = sqlException.getNextException(); sqlState == null && nested != null; nested = nested.getNextException()) {
            sqlState = nested.getSQLState();
        }
        return sqlState;
    }

    public static String extractSqlStateClassCode(SQLException sqlException) {
        return JdbcExceptionHelper.determineSqlStateClassCode(JdbcExceptionHelper.extractSqlState(sqlException));
    }

    public static String determineSqlStateClassCode(String sqlState) {
        if (sqlState == null || sqlState.length() < 2) {
            return sqlState;
        }
        return sqlState.substring(0, 2);
    }
}

