/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryException;
import com.querydsl.sql.SQLExceptionTranslator;
import com.querydsl.sql.support.SQLExceptionWrapper;
import java.sql.SQLException;
import java.util.List;

public final class DefaultSQLExceptionTranslator
implements SQLExceptionTranslator {
    public static final SQLExceptionTranslator DEFAULT = new DefaultSQLExceptionTranslator();
    private static final SQLExceptionWrapper WRAPPER = SQLExceptionWrapper.INSTANCE;

    @Override
    public RuntimeException translate(SQLException e) {
        if (DefaultSQLExceptionTranslator.containsAdditionalExceptions(e)) {
            return WRAPPER.wrap(e);
        }
        return new QueryException(e);
    }

    @Override
    public RuntimeException translate(String sql, List<Object> bindings, SQLException e) {
        String message = "Caught " + e.getClass().getSimpleName() + " for " + sql;
        if (DefaultSQLExceptionTranslator.containsAdditionalExceptions(e)) {
            return WRAPPER.wrap(message, e);
        }
        return new QueryException(message, e);
    }

    private static boolean containsAdditionalExceptions(SQLException e) {
        return e.getNextException() != null;
    }

    private DefaultSQLExceptionTranslator() {
    }
}

