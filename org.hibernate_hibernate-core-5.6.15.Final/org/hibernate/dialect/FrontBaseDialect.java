/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.lock.UpdateLockingStrategy;
import org.hibernate.persister.entity.Lockable;

public class FrontBaseDialect
extends Dialect {
    public FrontBaseDialect() {
        this.registerColumnType(-7, "bit");
        this.registerColumnType(-5, "longint");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "bit varying($l)");
        this.registerColumnType(2, "numeric($p,$s)");
        this.registerColumnType(2004, "blob");
        this.registerColumnType(2005, "clob");
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public String getCascadeConstraintsString() {
        return " cascade";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "{?= call current_timestamp}";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
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

