/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.hibernate.HibernateException
 *  org.hibernate.JDBCException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.PersistenceExceptionTranslator
 *  org.springframework.jdbc.support.SQLExceptionTranslator
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.hibernate5;

import javax.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public class HibernateExceptionTranslator
implements PersistenceExceptionTranslator {
    @Nullable
    private SQLExceptionTranslator jdbcExceptionTranslator;

    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        if (ex instanceof HibernateException) {
            return this.convertHibernateAccessException((HibernateException)((Object)ex));
        }
        if (ex instanceof PersistenceException) {
            if (ex.getCause() instanceof HibernateException) {
                return this.convertHibernateAccessException((HibernateException)ex.getCause());
            }
            return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
        }
        return null;
    }

    protected DataAccessException convertHibernateAccessException(HibernateException ex) {
        if (this.jdbcExceptionTranslator != null && ex instanceof JDBCException) {
            JDBCException jdbcEx = (JDBCException)ex;
            DataAccessException dae = this.jdbcExceptionTranslator.translate("Hibernate operation: " + jdbcEx.getMessage(), jdbcEx.getSQL(), jdbcEx.getSQLException());
            if (dae != null) {
                return dae;
            }
        }
        return SessionFactoryUtils.convertHibernateAccessException(ex);
    }
}

