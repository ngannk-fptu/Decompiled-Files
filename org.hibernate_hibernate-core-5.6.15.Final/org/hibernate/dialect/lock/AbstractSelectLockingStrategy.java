/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import org.hibernate.LockMode;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.persister.entity.Lockable;

public abstract class AbstractSelectLockingStrategy
implements LockingStrategy {
    private final Lockable lockable;
    private final LockMode lockMode;
    private final String waitForeverSql;
    private String noWaitSql;
    private String skipLockedSql;

    protected AbstractSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
        this.lockable = lockable;
        this.lockMode = lockMode;
        this.waitForeverSql = this.generateLockString(-1);
    }

    protected Lockable getLockable() {
        return this.lockable;
    }

    protected LockMode getLockMode() {
        return this.lockMode;
    }

    protected abstract String generateLockString(int var1);

    protected String determineSql(int timeout) {
        if (timeout == -1) {
            return this.waitForeverSql;
        }
        if (timeout == 0) {
            return this.getNoWaitSql();
        }
        if (timeout == -2) {
            return this.getSkipLockedSql();
        }
        return this.generateLockString(timeout);
    }

    protected String getNoWaitSql() {
        if (this.noWaitSql == null) {
            this.noWaitSql = this.generateLockString(0);
        }
        return this.noWaitSql;
    }

    protected String getSkipLockedSql() {
        if (this.skipLockedSql == null) {
            this.skipLockedSql = this.generateLockString(-2);
        }
        return this.skipLockedSql;
    }
}

