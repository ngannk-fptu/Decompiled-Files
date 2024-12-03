/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import java.util.Map;
import org.hibernate.JDBCException;
import org.hibernate.LockOptions;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SybaseASE157LimitHandler;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.sql.ForUpdateFragment;
import org.hibernate.type.StandardBasicTypes;

public class SybaseASE157Dialect
extends SybaseASE15Dialect {
    private static final SybaseASE157LimitHandler LIMIT_HANDLER = new SybaseASE157LimitHandler();

    public SybaseASE157Dialect() {
        this.registerFunction("create_locator", new SQLFunctionTemplate(StandardBasicTypes.BINARY, "create_locator(?1, ?2)"));
        this.registerFunction("locator_literal", new SQLFunctionTemplate(StandardBasicTypes.BINARY, "locator_literal(?1, ?2)"));
        this.registerFunction("locator_valid", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "locator_valid(?1)"));
        this.registerFunction("return_lob", new SQLFunctionTemplate(StandardBasicTypes.BINARY, "return_lob(?1, ?2)"));
        this.registerFunction("setdata", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "setdata(?1, ?2, ?3)"));
        this.registerFunction("charindex", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "charindex(?1, ?2, ?3)"));
    }

    @Override
    public String getTableTypeString() {
        return " lock datarows";
    }

    @Override
    public boolean supportsExpectedLobUsagePattern() {
        return true;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean forUpdateOfColumns() {
        return true;
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String getForUpdateString(String aliases) {
        return this.getForUpdateString() + " of " + aliases;
    }

    @Override
    public String appendLockHint(LockOptions mode, String tableName) {
        return tableName;
    }

    @Override
    public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
        return sql + new ForUpdateFragment(this, aliasedLockOptions, keyColumnNames).toFragmentString();
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                if ("JZ0TO".equals(sqlState) || "JZ006".equals(sqlState)) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                if (515 == errorCode && "ZZZZZ".equals(sqlState)) {
                    String constraintName = SybaseASE157Dialect.this.getViolatedConstraintNameExtracter().extractConstraintName(sqlException);
                    return new ConstraintViolationException(message, sqlException, sql, constraintName);
                }
                return null;
            }
        };
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }
}

