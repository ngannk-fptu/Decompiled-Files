/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.UncategorizedDataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc;

import java.sql.SQLException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

public class UncategorizedSQLException
extends UncategorizedDataAccessException {
    @Nullable
    private final String sql;

    public UncategorizedSQLException(String task, @Nullable String sql, SQLException ex) {
        super(task + "; uncategorized SQLException" + (sql != null ? " for SQL [" + sql + "]" : "") + "; SQL state [" + ex.getSQLState() + "]; error code [" + ex.getErrorCode() + "]; " + ex.getMessage(), (Throwable)ex);
        this.sql = sql;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    @Nullable
    public String getSql() {
        return this.sql;
    }
}

