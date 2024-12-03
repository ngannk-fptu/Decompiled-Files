/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryTimeoutException;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQLServer2005LimitHandler;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.type.StandardBasicTypes;

public class SQLServer2005Dialect
extends SQLServerDialect {
    private static final int MAX_LENGTH = 8000;

    public SQLServer2005Dialect() {
        this.registerColumnType(2004, "varbinary(MAX)");
        this.registerColumnType(-3, "varbinary(MAX)");
        this.registerColumnType(-3, 8000L, "varbinary($l)");
        this.registerColumnType(-4, "varbinary(MAX)");
        this.registerColumnType(2005, "varchar(MAX)");
        this.registerColumnType(-1, "varchar(MAX)");
        this.registerColumnType(12, "varchar(MAX)");
        this.registerColumnType(12, 8000L, "varchar($l)");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(-7, "bit");
        this.registerColumnType(2011, "nvarchar(MAX)");
        this.registerFunction("row_number", new NoArgSQLFunction("row_number", StandardBasicTypes.INTEGER, true));
    }

    @Override
    protected LimitHandler getDefaultLimitHandler() {
        return new SQLServer2005LimitHandler();
    }

    @Override
    public String appendLockHint(LockOptions lockOptions, String tableName) {
        LockMode lockMode = lockOptions.getAliasSpecificLockMode(tableName);
        if (lockMode == null) {
            lockMode = lockOptions.getLockMode();
        }
        String writeLockStr = lockOptions.getTimeOut() == -2 ? "updlock" : "updlock, holdlock";
        String readLockStr = lockOptions.getTimeOut() == -2 ? "updlock" : "holdlock";
        String noWaitStr = lockOptions.getTimeOut() == 0 ? ", nowait" : "";
        String skipLockStr = lockOptions.getTimeOut() == -2 ? ", readpast" : "";
        switch (lockMode) {
            case UPGRADE: 
            case PESSIMISTIC_WRITE: 
            case WRITE: {
                return tableName + " with (" + writeLockStr + ", rowlock" + noWaitStr + skipLockStr + ")";
            }
            case PESSIMISTIC_READ: {
                return tableName + " with (" + readLockStr + ", rowlock" + noWaitStr + skipLockStr + ")";
            }
            case UPGRADE_SKIPLOCKED: {
                return tableName + " with (updlock, rowlock, readpast" + noWaitStr + ")";
            }
            case UPGRADE_NOWAIT: {
                return tableName + " with (updlock, holdlock, rowlock, nowait)";
            }
        }
        return tableName;
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                if ("HY008".equals(sqlState)) {
                    throw new QueryTimeoutException(message, sqlException, sql);
                }
                if (1222 == errorCode) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                return null;
            }
        };
    }

    @Override
    public boolean supportsNonQueryWithCTE() {
        return true;
    }

    @Override
    public boolean supportsSkipLocked() {
        return true;
    }

    @Override
    public boolean supportsNoWait() {
        return true;
    }
}

