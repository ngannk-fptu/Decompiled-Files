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

public class PointbaseDialect
extends Dialect {
    public PointbaseDialect() {
        this.registerColumnType(-7, "smallint");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "smallint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "blob($l)");
        this.registerColumnType(2, "numeric($p,$s)");
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getCascadeConstraintsString() {
        return " cascade";
    }

    @Override
    public String getForUpdateString() {
        return "";
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

