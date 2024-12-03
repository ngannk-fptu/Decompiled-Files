/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.lock.UpdateLockingStrategy;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.MckoiCaseFragment;
import org.hibernate.type.StandardBasicTypes;

public class MckoiDialect
extends Dialect {
    public MckoiDialect() {
        this.registerColumnType(-7, "bit");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "varbinary");
        this.registerColumnType(2, "numeric");
        this.registerColumnType(2004, "blob");
        this.registerColumnType(2005, "clob");
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("round", new StandardSQLFunction("round", StandardBasicTypes.INTEGER));
        this.registerFunction("mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER));
        this.registerFunction("least", new StandardSQLFunction("least"));
        this.registerFunction("greatest", new StandardSQLFunction("greatest"));
        this.registerFunction("user", new StandardSQLFunction("user", StandardBasicTypes.STRING));
        this.registerFunction("concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName);
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "nextval('" + sequenceName + "')";
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
    public String getForUpdateString() {
        return "";
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new MckoiCaseFragment();
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

