/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessResourceUsageException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc;

import java.sql.SQLException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.lang.Nullable;

public class InvalidResultSetAccessException
extends InvalidDataAccessResourceUsageException {
    @Nullable
    private final String sql;

    public InvalidResultSetAccessException(String task, String sql, SQLException ex) {
        super(task + "; invalid ResultSet access for SQL [" + sql + "]", (Throwable)ex);
        this.sql = sql;
    }

    public InvalidResultSetAccessException(SQLException ex) {
        super(ex.getMessage(), (Throwable)ex);
        this.sql = null;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    @Nullable
    public String getSql() {
        return this.sql;
    }
}

