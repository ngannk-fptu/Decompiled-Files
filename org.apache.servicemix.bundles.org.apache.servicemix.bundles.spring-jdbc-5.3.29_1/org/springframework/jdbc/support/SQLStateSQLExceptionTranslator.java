/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.ConcurrencyFailureException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.QueryTimeoutException
 *  org.springframework.dao.TransientDataAccessResourceException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator;
import org.springframework.lang.Nullable;

public class SQLStateSQLExceptionTranslator
extends AbstractFallbackSQLExceptionTranslator {
    private static final Set<String> BAD_SQL_GRAMMAR_CODES = new HashSet<String>(8);
    private static final Set<String> DATA_INTEGRITY_VIOLATION_CODES = new HashSet<String>(8);
    private static final Set<String> DATA_ACCESS_RESOURCE_FAILURE_CODES = new HashSet<String>(8);
    private static final Set<String> TRANSIENT_DATA_ACCESS_RESOURCE_CODES = new HashSet<String>(8);
    private static final Set<String> CONCURRENCY_FAILURE_CODES = new HashSet<String>(4);

    @Override
    @Nullable
    protected DataAccessException doTranslate(String task, @Nullable String sql, SQLException ex) {
        String sqlState = this.getSqlState(ex);
        if (sqlState != null && sqlState.length() >= 2) {
            String classCode = sqlState.substring(0, 2);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Extracted SQL state class '" + classCode + "' from value '" + sqlState + "'"));
            }
            if (BAD_SQL_GRAMMAR_CODES.contains(classCode)) {
                return new BadSqlGrammarException(task, sql != null ? sql : "", ex);
            }
            if (DATA_INTEGRITY_VIOLATION_CODES.contains(classCode)) {
                return new DataIntegrityViolationException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (DATA_ACCESS_RESOURCE_FAILURE_CODES.contains(classCode)) {
                return new DataAccessResourceFailureException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (TRANSIENT_DATA_ACCESS_RESOURCE_CODES.contains(classCode)) {
                return new TransientDataAccessResourceException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
            if (CONCURRENCY_FAILURE_CODES.contains(classCode)) {
                return new ConcurrencyFailureException(this.buildMessage(task, sql, ex), (Throwable)ex);
            }
        }
        if (ex.getClass().getName().contains("Timeout")) {
            return new QueryTimeoutException(this.buildMessage(task, sql, ex), (Throwable)ex);
        }
        return null;
    }

    @Nullable
    private String getSqlState(SQLException ex) {
        SQLException nestedEx;
        String sqlState = ex.getSQLState();
        if (sqlState == null && (nestedEx = ex.getNextException()) != null) {
            sqlState = nestedEx.getSQLState();
        }
        return sqlState;
    }

    static {
        BAD_SQL_GRAMMAR_CODES.add("07");
        BAD_SQL_GRAMMAR_CODES.add("21");
        BAD_SQL_GRAMMAR_CODES.add("2A");
        BAD_SQL_GRAMMAR_CODES.add("37");
        BAD_SQL_GRAMMAR_CODES.add("42");
        BAD_SQL_GRAMMAR_CODES.add("65");
        DATA_INTEGRITY_VIOLATION_CODES.add("01");
        DATA_INTEGRITY_VIOLATION_CODES.add("02");
        DATA_INTEGRITY_VIOLATION_CODES.add("22");
        DATA_INTEGRITY_VIOLATION_CODES.add("23");
        DATA_INTEGRITY_VIOLATION_CODES.add("27");
        DATA_INTEGRITY_VIOLATION_CODES.add("44");
        DATA_ACCESS_RESOURCE_FAILURE_CODES.add("08");
        DATA_ACCESS_RESOURCE_FAILURE_CODES.add("53");
        DATA_ACCESS_RESOURCE_FAILURE_CODES.add("54");
        DATA_ACCESS_RESOURCE_FAILURE_CODES.add("57");
        DATA_ACCESS_RESOURCE_FAILURE_CODES.add("58");
        TRANSIENT_DATA_ACCESS_RESOURCE_CODES.add("JW");
        TRANSIENT_DATA_ACCESS_RESOURCE_CODES.add("JZ");
        TRANSIENT_DATA_ACCESS_RESOURCE_CODES.add("S1");
        CONCURRENCY_FAILURE_CODES.add("40");
        CONCURRENCY_FAILURE_CODES.add("61");
    }
}

