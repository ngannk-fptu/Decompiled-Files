/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.internal;

import java.sql.DataTruncation;
import java.sql.SQLClientInfoException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTransientConnectionException;
import org.hibernate.JDBCException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.exception.spi.AbstractSQLExceptionConversionDelegate;
import org.hibernate.exception.spi.ConversionContext;

public class SQLExceptionTypeDelegate
extends AbstractSQLExceptionConversionDelegate {
    public SQLExceptionTypeDelegate(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public JDBCException convert(SQLException sqlException, String message, String sql) {
        if (SQLClientInfoException.class.isInstance(sqlException) || SQLInvalidAuthorizationSpecException.class.isInstance(sqlException) || SQLNonTransientConnectionException.class.isInstance(sqlException) || SQLTransientConnectionException.class.isInstance(sqlException)) {
            return new JDBCConnectionException(message, sqlException, sql);
        }
        if (DataTruncation.class.isInstance(sqlException) || SQLDataException.class.isInstance(sqlException)) {
            throw new DataException(message, sqlException, sql);
        }
        if (SQLIntegrityConstraintViolationException.class.isInstance(sqlException)) {
            return new ConstraintViolationException(message, sqlException, sql, this.getConversionContext().getViolatedConstraintNameExtracter().extractConstraintName(sqlException));
        }
        if (SQLSyntaxErrorException.class.isInstance(sqlException)) {
            return new SQLGrammarException(message, sqlException, sql);
        }
        if (SQLTimeoutException.class.isInstance(sqlException)) {
            return new QueryTimeoutException(message, sqlException, sql);
        }
        if (SQLTransactionRollbackException.class.isInstance(sqlException)) {
            return new LockAcquisitionException(message, sqlException, sql);
        }
        return null;
    }
}

