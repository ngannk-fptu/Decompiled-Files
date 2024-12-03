/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.lock.UpdateLockingStrategy;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.logging.Logger;

public class RDMSOS2200Dialect
extends Dialect {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)RDMSOS2200Dialect.class.getName());
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            if (hasOffset) {
                throw new UnsupportedOperationException("query result offset is not supported");
            }
            return sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only ";
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
        public boolean supportsVariableLimit() {
            return false;
        }
    };
    private static final AbstractLimitHandler LEGACY_LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            return sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only ";
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
        public boolean supportsVariableLimit() {
            return false;
        }
    };

    public RDMSOS2200Dialect() {
        LOG.rdmsOs2200Dialect();
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.INTEGER));
        this.registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.INTEGER));
        this.registerFunction("concat", new SQLFunctionTemplate(StandardBasicTypes.STRING, "concat(?1, ?2)"));
        this.registerFunction("instr", new StandardSQLFunction("instr", StandardBasicTypes.STRING));
        this.registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("lcase", new StandardSQLFunction("lcase"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("reverse", new StandardSQLFunction("reverse"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("trim", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))"));
        this.registerFunction("soundex", new StandardSQLFunction("soundex"));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("ucase", new StandardSQLFunction("ucase"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("initcap", new StandardSQLFunction("initcap"));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("days", new StandardSQLFunction("days", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE));
        this.registerFunction("microsecond", new StandardSQLFunction("microsecond", StandardBasicTypes.INTEGER));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME));
        this.registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));
        this.registerFunction("mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER));
        this.registerFunction("nvl", new StandardSQLFunction("nvl"));
        this.registerFunction("power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE));
        this.registerColumnType(-7, "SMALLINT");
        this.registerColumnType(-6, "SMALLINT");
        this.registerColumnType(-5, "NUMERIC(21,0)");
        this.registerColumnType(5, "SMALLINT");
        this.registerColumnType(1, "CHARACTER(1)");
        this.registerColumnType(8, "DOUBLE PRECISION");
        this.registerColumnType(6, "FLOAT");
        this.registerColumnType(7, "REAL");
        this.registerColumnType(4, "INTEGER");
        this.registerColumnType(2, "NUMERIC(21,$l)");
        this.registerColumnType(3, "NUMERIC(21,$l)");
        this.registerColumnType(91, "DATE");
        this.registerColumnType(92, "TIME");
        this.registerColumnType(93, "TIMESTAMP");
        this.registerColumnType(12, "CHARACTER($l)");
        this.registerColumnType(2004, "BLOB($l)");
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public boolean forUpdateOfColumns() {
        return false;
    }

    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getNullColumnString() {
        return " null";
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "";
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "";
    }

    @Override
    public String getCascadeConstraintsString() {
        return " including contents";
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new DecodeCaseFragment();
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return LEGACY_LIMIT_HANDLER;
        }
        return LIMIT_HANDLER;
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
    public String getLimitString(String sql, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return sql + " fetch first " + limit + " rows only ";
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
        if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
            return new PessimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_WRITE) {
            return new PessimisticWriteUpdateLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_READ) {
            return new PessimisticReadUpdateLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC) {
            return new OptimisticLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT) {
            return new OptimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (lockMode.greaterThan(LockMode.READ)) {
            return new UpdateLockingStrategy(lockable, lockMode);
        }
        return new SelectLockingStrategy(lockable, lockMode);
    }
}

