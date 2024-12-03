/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.HSQLIdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorHSQLDBDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.logging.Logger;

public class HSQLDialect
extends Dialect {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)HSQLDialect.class.getName());
    private int hsqldbVersion = 180;
    private final LimitHandler limitHandler;
    private static final ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            String constraintName = null;
            int errorCode = JdbcExceptionHelper.extractErrorCode(sqle);
            if (errorCode == -8) {
                constraintName = this.extractUsingTemplate("Integrity constraint violation ", " table:", sqle.getMessage());
            } else if (errorCode == -9) {
                constraintName = this.extractUsingTemplate("Violation of unique index: ", " in statement [", sqle.getMessage());
            } else if (errorCode == -104) {
                constraintName = this.extractUsingTemplate("Unique constraint violation: ", " in statement [", sqle.getMessage());
            } else if (errorCode == -177) {
                constraintName = this.extractUsingTemplate("Integrity constraint violation - no parent ", " table:", sqle.getMessage());
            }
            return constraintName;
        }
    };
    private static final ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            String constraintName = null;
            int errorCode = JdbcExceptionHelper.extractErrorCode(sqle);
            if (errorCode == -8) {
                constraintName = this.extractUsingTemplate("; ", " table: ", sqle.getMessage());
            } else if (errorCode == -9) {
                constraintName = this.extractUsingTemplate("; ", " table: ", sqle.getMessage());
            } else if (errorCode == -104) {
                constraintName = this.extractUsingTemplate("; ", " table: ", sqle.getMessage());
            } else if (errorCode == -177) {
                constraintName = this.extractUsingTemplate("; ", " table: ", sqle.getMessage());
            }
            return constraintName;
        }
    };

    public HSQLDialect() {
        try {
            Class props = ReflectHelper.classForName("org.hsqldb.persist.HsqlDatabaseProperties");
            String versionString = (String)props.getDeclaredField("THIS_VERSION").get(null);
            this.hsqldbVersion = Integer.parseInt(versionString.substring(0, 1)) * 100;
            this.hsqldbVersion += Integer.parseInt(versionString.substring(2, 3)) * 10;
            this.hsqldbVersion += Integer.parseInt(versionString.substring(4, 5));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(-2, "binary($l)");
        this.registerColumnType(-7, "bit");
        this.registerColumnType(16, "boolean");
        this.registerColumnType(1, "char($l)");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "decimal($p,$s)");
        this.registerColumnType(8, "double");
        this.registerColumnType(6, "float");
        this.registerColumnType(4, "integer");
        this.registerColumnType(-4, "longvarbinary");
        this.registerColumnType(-1, "longvarchar");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(-3, "varbinary($l)");
        this.registerColumnType(2011, "clob");
        if (this.hsqldbVersion < 200) {
            this.registerColumnType(2, "numeric");
        } else {
            this.registerColumnType(2, "numeric($p,$s)");
        }
        if (this.hsqldbVersion < 200) {
            this.registerColumnType(2004, "longvarbinary");
            this.registerColumnType(2005, "longvarchar");
        } else {
            this.registerColumnType(2004, "blob($l)");
            this.registerColumnType(2005, "clob($l)");
        }
        this.registerFunction("avg", new AvgWithArgumentCastFunction("double"));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("lcase", new StandardSQLFunction("lcase"));
        this.registerFunction("ucase", new StandardSQLFunction("ucase"));
        this.registerFunction("soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("reverse", new StandardSQLFunction("reverse"));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar(256))"));
        this.registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
        this.registerFunction("rawtohex", new StandardSQLFunction("rawtohex"));
        this.registerFunction("hextoraw", new StandardSQLFunction("hextoraw"));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING));
        this.registerFunction("database", new NoArgSQLFunction("database", StandardBasicTypes.STRING));
        if (this.hsqldbVersion < 200) {
            this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false));
        } else {
            this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false));
        }
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "cast(second(?1) as int)"));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new StandardSQLFunction("rand", StandardBasicTypes.FLOAT));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("roundmagic", new StandardSQLFunction("roundmagic"));
        this.registerFunction("truncate", new StandardSQLFunction("truncate"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        if (this.hsqldbVersion > 219) {
            this.registerFunction("rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.INTEGER));
        }
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.limitHandler = new HSQLLimitHandler();
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public String getForUpdateString() {
        if (this.hsqldbVersion >= 200) {
            return " for update";
        }
        return "";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return this.limitHandler;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        if (this.hsqldbVersion < 200) {
            return new StringBuilder(sql.length() + 10).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, hasOffset ? " limit ? ?" : " top ?").toString();
        }
        return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return this.hsqldbVersion < 200;
    }

    @Override
    public boolean supportsIfExistsAfterTableName() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsColumnCheck() {
        return this.hsqldbVersion >= 200;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    protected String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName + " start with 1";
    }

    @Override
    protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
        if (this.supportsPooledSequences()) {
            return "create sequence " + sequenceName + " start with " + initialValue + " increment by " + incrementSize;
        }
        throw new MappingException(this.getClass().getName() + " does not support pooled sequences");
    }

    @Override
    protected String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName + " if exists";
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "next value for " + sequenceName;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "call next value for " + sequenceName;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from information_schema.sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorHSQLDBDatabaseImpl.INSTANCE;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return this.hsqldbVersion < 200 ? EXTRACTER_18 : EXTRACTER_20;
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        String literal;
        switch (sqlType) {
            case -1: 
            case 1: 
            case 12: {
                literal = "cast(null as varchar(100))";
                break;
            }
            case -4: 
            case -3: 
            case -2: {
                literal = "cast(null as varbinary(100))";
                break;
            }
            case 2005: {
                literal = "cast(null as clob)";
                break;
            }
            case 2004: {
                literal = "cast(null as blob)";
                break;
            }
            case 91: {
                literal = "cast(null as date)";
                break;
            }
            case 93: {
                literal = "cast(null as timestamp)";
                break;
            }
            case 16: {
                literal = "cast(null as boolean)";
                break;
            }
            case -7: {
                literal = "cast(null as bit)";
                break;
            }
            case 92: {
                literal = "cast(null as time)";
                break;
            }
            default: {
                literal = "cast(null as int)";
            }
        }
        return literal;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        if (this.hsqldbVersion < 200) {
            return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

                @Override
                public String generateIdTableName(String baseName) {
                    return "HT_" + baseName;
                }

                @Override
                public String getCreateIdTableCommand() {
                    return "create global temporary table";
                }
            }, AfterUseAction.CLEAN);
        }
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return "MODULE.HT_" + baseName;
            }

            @Override
            public String getCreateIdTableCommand() {
                return "declare local temporary table";
            }
        }, AfterUseAction.DROP, TempTableDdlTransactionHandling.NONE);
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "call current_timestamp";
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "current_timestamp";
    }

    @Override
    public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
        if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
            return new PessimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_WRITE) {
            return new PessimisticWriteSelectLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_READ) {
            return new PessimisticReadSelectLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC) {
            return new OptimisticLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT) {
            return new OptimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (this.hsqldbVersion < 200) {
            return new ReadUncommittedLockingStrategy(lockable, lockMode);
        }
        return new SelectLockingStrategy(lockable, lockMode);
    }

    @Override
    public boolean supportsCommentOn() {
        return this.hsqldbVersion >= 200;
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean requiresCastingOfParametersInSelectClause() {
        return true;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return this.hsqldbVersion >= 200;
    }

    @Override
    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
        return this.hsqldbVersion >= 200;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return String.valueOf(bool);
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return this.hsqldbVersion >= 229;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new HSQLIdentityColumnSupport(this.hsqldbVersion);
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }

    @Override
    public boolean supportsNamedParameters(DatabaseMetaData databaseMetaData) throws SQLException {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getCascadeConstraintsString() {
        return " CASCADE ";
    }

    private static class ReadUncommittedLockingStrategy
    extends SelectLockingStrategy {
        public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
            super(lockable, lockMode);
        }

        @Override
        public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) throws StaleObjectStateException, JDBCException {
            if (this.getLockMode().greaterThan(LockMode.READ)) {
                LOG.hsqldbSupportsOnlyReadCommittedIsolation();
            }
            super.lock(id, version, object, timeout, session);
        }
    }

    private final class HSQLLimitHandler
    extends AbstractLimitHandler {
        private HSQLLimitHandler() {
        }

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            if (HSQLDialect.this.hsqldbVersion < 200) {
                return new StringBuilder(sql.length() + 10).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, hasOffset ? " limit ? ?" : " top ?").toString();
            }
            return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean bindLimitParametersFirst() {
            return HSQLDialect.this.hsqldbVersion < 200;
        }
    }
}

