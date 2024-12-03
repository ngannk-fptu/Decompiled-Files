/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.support;

import com.querydsl.core.QueryException;
import com.querydsl.sql.support.SQLExceptionWrapper;
import java.sql.SQLException;

class JavaSE7SQLExceptionWrapper
extends SQLExceptionWrapper {
    JavaSE7SQLExceptionWrapper() {
    }

    @Override
    public RuntimeException wrap(SQLException exception) {
        QueryException rv = new QueryException(exception);
        for (SQLException linkedException = exception.getNextException(); linkedException != null; linkedException = linkedException.getNextException()) {
            rv.addSuppressed(linkedException);
        }
        return rv;
    }

    @Override
    public RuntimeException wrap(String message, SQLException exception) {
        QueryException rv = new QueryException(message, exception);
        for (SQLException linkedException = exception.getNextException(); linkedException != null; linkedException = linkedException.getNextException()) {
            rv.addSuppressed(linkedException);
        }
        return rv;
    }
}

