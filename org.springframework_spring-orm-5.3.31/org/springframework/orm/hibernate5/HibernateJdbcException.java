/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.JDBCException
 *  org.springframework.dao.UncategorizedDataAccessException
 */
package org.springframework.orm.hibernate5;

import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.springframework.dao.UncategorizedDataAccessException;

public class HibernateJdbcException
extends UncategorizedDataAccessException {
    public HibernateJdbcException(JDBCException ex) {
        super("JDBC exception on Hibernate data access: SQLException for SQL [" + ex.getSQL() + "]; SQL state [" + ex.getSQLState() + "]; error code [" + ex.getErrorCode() + "]; " + ex.getMessage(), (Throwable)ex);
    }

    public SQLException getSQLException() {
        return ((JDBCException)this.getCause()).getSQLException();
    }

    public String getSql() {
        return ((JDBCException)this.getCause()).getSQL();
    }
}

