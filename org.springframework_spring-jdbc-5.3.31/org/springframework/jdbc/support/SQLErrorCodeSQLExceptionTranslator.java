/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.CannotAcquireLockException
 *  org.springframework.dao.CannotSerializeTransactionException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.DeadlockLoserDataAccessException
 *  org.springframework.dao.DuplicateKeyException
 *  org.springframework.dao.PermissionDeniedDataAccessException
 *  org.springframework.dao.TransientDataAccessResourceException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.function.SingletonSupplier
 *  org.springframework.util.function.SupplierUtils
 */
package org.springframework.jdbc.support;

import java.lang.reflect.Constructor;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator;
import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLErrorCodesFactory;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

public class SQLErrorCodeSQLExceptionTranslator
extends AbstractFallbackSQLExceptionTranslator {
    private static final int MESSAGE_ONLY_CONSTRUCTOR = 1;
    private static final int MESSAGE_THROWABLE_CONSTRUCTOR = 2;
    private static final int MESSAGE_SQLEX_CONSTRUCTOR = 3;
    private static final int MESSAGE_SQL_THROWABLE_CONSTRUCTOR = 4;
    private static final int MESSAGE_SQL_SQLEX_CONSTRUCTOR = 5;
    @Nullable
    private SingletonSupplier<SQLErrorCodes> sqlErrorCodes;

    public SQLErrorCodeSQLExceptionTranslator() {
        this.setFallbackTranslator(new SQLExceptionSubclassTranslator());
    }

    public SQLErrorCodeSQLExceptionTranslator(DataSource dataSource) {
        this();
        this.setDataSource(dataSource);
    }

    public SQLErrorCodeSQLExceptionTranslator(String dbName) {
        this();
        this.setDatabaseProductName(dbName);
    }

    public SQLErrorCodeSQLExceptionTranslator(SQLErrorCodes sec) {
        this();
        this.sqlErrorCodes = SingletonSupplier.of((Object)sec);
    }

    public void setDataSource(DataSource dataSource) {
        this.sqlErrorCodes = SingletonSupplier.of(() -> SQLErrorCodesFactory.getInstance().resolveErrorCodes(dataSource));
        this.sqlErrorCodes.get();
    }

    public void setDatabaseProductName(String dbName) {
        this.sqlErrorCodes = SingletonSupplier.of((Object)SQLErrorCodesFactory.getInstance().getErrorCodes(dbName));
    }

    public void setSqlErrorCodes(@Nullable SQLErrorCodes sec) {
        this.sqlErrorCodes = SingletonSupplier.ofNullable((Object)sec);
    }

    @Nullable
    public SQLErrorCodes getSqlErrorCodes() {
        return (SQLErrorCodes)SupplierUtils.resolve(this.sqlErrorCodes);
    }

    @Override
    @Nullable
    protected DataAccessException doTranslate(String task, @Nullable String sql, SQLException ex) {
        SQLExceptionTranslator customTranslator;
        DataAccessException dae;
        SQLException nestedSqlEx;
        SQLException sqlEx = ex;
        if (sqlEx instanceof BatchUpdateException && sqlEx.getNextException() != null && ((nestedSqlEx = sqlEx.getNextException()).getErrorCode() > 0 || nestedSqlEx.getSQLState() != null)) {
            sqlEx = nestedSqlEx;
        }
        if ((dae = this.customTranslate(task, sql, sqlEx)) != null) {
            return dae;
        }
        SQLErrorCodes sqlErrorCodes = this.getSqlErrorCodes();
        if (sqlErrorCodes != null && (customTranslator = sqlErrorCodes.getCustomSqlExceptionTranslator()) != null && (dae = customTranslator.translate(task, sql, sqlEx)) != null) {
            return dae;
        }
        if (sqlErrorCodes != null) {
            String errorCode;
            if (sqlErrorCodes.isUseSqlStateForTranslation()) {
                errorCode = sqlEx.getSQLState();
            } else {
                SQLException current = sqlEx;
                while (current.getErrorCode() == 0 && current.getCause() instanceof SQLException) {
                    current = (SQLException)current.getCause();
                }
                errorCode = Integer.toString(current.getErrorCode());
            }
            if (errorCode != null) {
                CustomSQLErrorCodesTranslation[] customTranslations = sqlErrorCodes.getCustomTranslations();
                if (customTranslations != null) {
                    for (CustomSQLErrorCodesTranslation customTranslation : customTranslations) {
                        if (Arrays.binarySearch(customTranslation.getErrorCodes(), errorCode) < 0 || customTranslation.getExceptionClass() == null || (dae = this.createCustomException(task, sql, sqlEx, customTranslation.getExceptionClass())) == null) continue;
                        this.logTranslation(task, sql, sqlEx, true);
                        return dae;
                    }
                }
                if (Arrays.binarySearch(sqlErrorCodes.getBadSqlGrammarCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new BadSqlGrammarException(task, sql != null ? sql : "", sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getInvalidResultSetAccessCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new InvalidResultSetAccessException(task, sql != null ? sql : "", sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getDuplicateKeyCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new DuplicateKeyException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getDataIntegrityViolationCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new DataIntegrityViolationException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getPermissionDeniedCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new PermissionDeniedDataAccessException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getDataAccessResourceFailureCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new DataAccessResourceFailureException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getTransientDataAccessResourceCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new TransientDataAccessResourceException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getCannotAcquireLockCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new CannotAcquireLockException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getDeadlockLoserCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new DeadlockLoserDataAccessException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
                if (Arrays.binarySearch(sqlErrorCodes.getCannotSerializeTransactionCodes(), errorCode) >= 0) {
                    this.logTranslation(task, sql, sqlEx, false);
                    return new CannotSerializeTransactionException(this.buildMessage(task, sql, sqlEx), (Throwable)sqlEx);
                }
            }
        }
        if (this.logger.isDebugEnabled()) {
            String codes = sqlErrorCodes != null && sqlErrorCodes.isUseSqlStateForTranslation() ? "SQL state '" + sqlEx.getSQLState() + "', error code '" + sqlEx.getErrorCode() : "Error code '" + sqlEx.getErrorCode() + "'";
            this.logger.debug((Object)("Unable to translate SQLException with " + codes + ", will now try the fallback translator"));
        }
        return null;
    }

    @Nullable
    protected DataAccessException customTranslate(String task, @Nullable String sql, SQLException sqlEx) {
        return null;
    }

    @Nullable
    protected DataAccessException createCustomException(String task, @Nullable String sql, SQLException sqlEx, Class<?> exceptionClass) {
        try {
            Constructor<?>[] constructors;
            int constructorType = 0;
            for (Constructor<?> constructor : constructors = exceptionClass.getConstructors()) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && String.class == parameterTypes[0] && constructorType < 1) {
                    constructorType = 1;
                }
                if (parameterTypes.length == 2 && String.class == parameterTypes[0] && Throwable.class == parameterTypes[1] && constructorType < 2) {
                    constructorType = 2;
                }
                if (parameterTypes.length == 2 && String.class == parameterTypes[0] && SQLException.class == parameterTypes[1] && constructorType < 3) {
                    constructorType = 3;
                }
                if (parameterTypes.length == 3 && String.class == parameterTypes[0] && String.class == parameterTypes[1] && Throwable.class == parameterTypes[2] && constructorType < 4) {
                    constructorType = 4;
                }
                if (parameterTypes.length != 3 || String.class != parameterTypes[0] || String.class != parameterTypes[1] || SQLException.class != parameterTypes[2] || constructorType >= 5) continue;
                constructorType = 5;
            }
            switch (constructorType) {
                case 5: {
                    Class[] messageAndSqlAndSqlExArgsClass = new Class[]{String.class, String.class, SQLException.class};
                    Object[] messageAndSqlAndSqlExArgs = new Object[]{task, sql, sqlEx};
                    Constructor<?> exceptionConstructor = exceptionClass.getConstructor(messageAndSqlAndSqlExArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlAndSqlExArgs);
                }
                case 4: {
                    Class[] messageAndSqlAndThrowableArgsClass = new Class[]{String.class, String.class, Throwable.class};
                    Object[] messageAndSqlAndThrowableArgs = new Object[]{task, sql, sqlEx};
                    Constructor<?> exceptionConstructor = exceptionClass.getConstructor(messageAndSqlAndThrowableArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlAndThrowableArgs);
                }
                case 3: {
                    Class[] messageAndSqlExArgsClass = new Class[]{String.class, SQLException.class};
                    Object[] messageAndSqlExArgs = new Object[]{task + ": " + sqlEx.getMessage(), sqlEx};
                    Constructor<?> exceptionConstructor = exceptionClass.getConstructor(messageAndSqlExArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlExArgs);
                }
                case 2: {
                    Class[] messageAndThrowableArgsClass = new Class[]{String.class, Throwable.class};
                    Object[] messageAndThrowableArgs = new Object[]{task + ": " + sqlEx.getMessage(), sqlEx};
                    Constructor<?> exceptionConstructor = exceptionClass.getConstructor(messageAndThrowableArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndThrowableArgs);
                }
                case 1: {
                    Class[] messageOnlyArgsClass = new Class[]{String.class};
                    Object[] messageOnlyArgs = new Object[]{task + ": " + sqlEx.getMessage()};
                    Constructor<?> exceptionConstructor = exceptionClass.getConstructor(messageOnlyArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageOnlyArgs);
                }
            }
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Unable to find appropriate constructor of custom exception class [" + exceptionClass.getName() + "]"));
            }
            return null;
        }
        catch (Throwable ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Unable to instantiate custom exception class [" + exceptionClass.getName() + "]"), ex);
            }
            return null;
        }
    }

    private void logTranslation(String task, @Nullable String sql, SQLException sqlEx, boolean custom) {
        if (this.logger.isDebugEnabled()) {
            String intro = custom ? "Custom translation of" : "Translating";
            this.logger.debug((Object)(intro + " SQLException with SQL state '" + sqlEx.getSQLState() + "', error code '" + sqlEx.getErrorCode() + "', message [" + sqlEx.getMessage() + "]" + (sql != null ? "; SQL was [" + sql + "]" : "") + " for task [" + task + "]"));
        }
    }
}

