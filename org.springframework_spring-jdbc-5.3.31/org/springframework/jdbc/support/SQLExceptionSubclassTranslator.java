/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.ConcurrencyFailureException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.dao.PermissionDeniedDataAccessException
 *  org.springframework.dao.QueryTimeoutException
 *  org.springframework.dao.RecoverableDataAccessException
 *  org.springframework.dao.TransientDataAccessResourceException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLNonTransientException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.lang.Nullable;

public class SQLExceptionSubclassTranslator
extends AbstractFallbackSQLExceptionTranslator {
    public SQLExceptionSubclassTranslator() {
        this.setFallbackTranslator(new SQLStateSQLExceptionTranslator());
    }

    @Override
    @Nullable
    protected DataAccessException doTranslate(String task, @Nullable String sql, SQLException ex) {
        if (ex instanceof SQLTransientException) {
            if (ex instanceof SQLTransientConnectionException) {
                return new TransientDataAccessResourceException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLTransactionRollbackException) {
                return new ConcurrencyFailureException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLTimeoutException) {
                return new QueryTimeoutException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
        } else if (ex instanceof SQLNonTransientException) {
            if (ex instanceof SQLNonTransientConnectionException) {
                return new DataAccessResourceFailureException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLDataException) {
                return new DataIntegrityViolationException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLIntegrityConstraintViolationException) {
                return new DataIntegrityViolationException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLInvalidAuthorizationSpecException) {
                return new PermissionDeniedDataAccessException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (ex instanceof SQLSyntaxErrorException) {
                return new BadSqlGrammarException(task, sql != null ? sql : "", ex);
            }
            if (ex instanceof SQLFeatureNotSupportedException) {
                return new InvalidDataAccessApiUsageException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
        } else if (ex instanceof SQLRecoverableException) {
            return new RecoverableDataAccessException(this.buildMessage(task, sql, ex), (Throwable)ex);
        }
        return null;
    }
}

