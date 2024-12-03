/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.lock.UpdateLockingStrategy;
import org.hibernate.dialect.pagination.FirstLimitHandler;
import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.OracleJoinFragment;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorTimesTenDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class TimesTenDialect
extends Dialect {
    public TimesTenDialect() {
        this.registerColumnType(-7, "TINYINT");
        this.registerColumnType(-5, "BIGINT");
        this.registerColumnType(5, "SMALLINT");
        this.registerColumnType(-6, "TINYINT");
        this.registerColumnType(4, "INTEGER");
        this.registerColumnType(1, "CHAR(1)");
        this.registerColumnType(12, "VARCHAR($l)");
        this.registerColumnType(6, "FLOAT");
        this.registerColumnType(8, "DOUBLE");
        this.registerColumnType(91, "DATE");
        this.registerColumnType(92, "TIME");
        this.registerColumnType(93, "TIMESTAMP");
        this.registerColumnType(-3, "VARBINARY($l)");
        this.registerColumnType(2, "DECIMAL($p, $s)");
        this.registerColumnType(2004, "VARBINARY(4000000)");
        this.registerColumnType(2005, "VARCHAR(4000000)");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "true");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING));
        this.registerFunction("mod", new StandardSQLFunction("mod"));
        this.registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
        this.registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("nvl", new StandardSQLFunction("nvl"));
    }

    @Override
    public boolean dropConstraints() {
        return true;
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select first 1 " + sequenceName + ".nextval from sys.tables";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from sys.sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorTimesTenDatabaseImpl.INSTANCE;
    }

    @Override
    public JoinFragment createOuterJoinFragment() {
        return new OracleJoinFragment();
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }

    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public boolean supportsTableCheck() {
        return false;
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return LegacyFirstLimitHandler.INSTANCE;
        }
        return FirstLimitHandler.INSTANCE;
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public String getLimitString(String querySelect, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(6, " first " + limit).toString();
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select first 1 sysdate from sys.tables";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                String name = super.generateIdTableName(baseName);
                return name.length() > 30 ? name.substring(1, 30) : name;
            }

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "on commit delete rows";
            }
        }, AfterUseAction.CLEAN);
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

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }
}

