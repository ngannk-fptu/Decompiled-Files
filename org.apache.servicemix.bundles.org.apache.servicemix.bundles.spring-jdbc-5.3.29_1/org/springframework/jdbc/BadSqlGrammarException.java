/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessResourceUsageException
 */
package org.springframework.jdbc;

import java.sql.SQLException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

public class BadSqlGrammarException
extends InvalidDataAccessResourceUsageException {
    private final String sql;

    public BadSqlGrammarException(String task, String sql, SQLException ex) {
        super(task + "; bad SQL grammar [" + sql + "]", (Throwable)ex);
        this.sql = sql;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    public String getSql() {
        return this.sql;
    }
}

