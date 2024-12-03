/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.sql.SQLException;
import org.hibernate.HibernateException;

public class JDBCException
extends HibernateException {
    private final SQLException sqlException;
    private final String sql;

    public JDBCException(String message, SQLException cause) {
        this(message, cause, null);
    }

    public JDBCException(String message, SQLException cause, String sql) {
        super(message, cause);
        this.sqlException = cause;
        this.sql = sql;
    }

    public String getSQLState() {
        return this.sqlException.getSQLState();
    }

    public int getErrorCode() {
        return this.sqlException.getErrorCode();
    }

    public SQLException getSQLException() {
        return this.sqlException;
    }

    public String getSQL() {
        return this.sql;
    }
}

