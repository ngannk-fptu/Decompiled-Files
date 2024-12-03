/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL94Dialect;

public class PostgreSQL95Dialect
extends PostgreSQL94Dialect {
    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString();
        }
        return super.getWriteLockString(timeout);
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString(aliases);
        }
        return super.getWriteLockString(aliases, timeout);
    }

    @Override
    public String getReadLockString(int timeout) {
        if (timeout == -2) {
            return " for share skip locked";
        }
        return super.getReadLockString(timeout);
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        if (timeout == -2) {
            return String.format(" for share of %s skip locked", aliases);
        }
        return super.getReadLockString(aliases, timeout);
    }

    @Override
    public String getForUpdateSkipLockedString() {
        return " for update skip locked";
    }

    @Override
    public String getForUpdateSkipLockedString(String aliases) {
        return this.getForUpdateString() + " of " + aliases + " skip locked";
    }

    @Override
    public boolean supportsSkipLocked() {
        return true;
    }
}

