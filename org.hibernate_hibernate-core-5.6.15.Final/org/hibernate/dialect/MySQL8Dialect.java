/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySQL8Dialect
extends MySQL57Dialect {
    public MySQL8Dialect() {
        this.registerKeyword("CUME_DIST");
        this.registerKeyword("DENSE_RANK");
        this.registerKeyword("EMPTY");
        this.registerKeyword("EXCEPT");
        this.registerKeyword("FIRST_VALUE");
        this.registerKeyword("GROUPS");
        this.registerKeyword("JSON_TABLE");
        this.registerKeyword("LAG");
        this.registerKeyword("LAST_VALUE");
        this.registerKeyword("LEAD");
        this.registerKeyword("NTH_VALUE");
        this.registerKeyword("NTILE");
        this.registerKeyword("PERSIST");
        this.registerKeyword("PERCENT_RANK");
        this.registerKeyword("PERSIST_ONLY");
        this.registerKeyword("RANK");
        this.registerKeyword("ROW_NUMBER");
        this.registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace", StandardBasicTypes.STRING));
        this.registerFunction("regexp_instr", new StandardSQLFunction("regexp_instr", StandardBasicTypes.INTEGER));
        this.registerFunction("regexp_substr", new StandardSQLFunction("regexp_substr", StandardBasicTypes.STRING));
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == 0) {
            return this.getForUpdateNowaitString();
        }
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString();
        }
        return super.getWriteLockString(timeout);
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout == 0) {
            return this.getForUpdateNowaitString(aliases);
        }
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString(aliases);
        }
        return super.getWriteLockString(aliases, timeout);
    }

    @Override
    public String getReadLockString(int timeout) {
        String readLockString = " for share";
        if (timeout == 0) {
            return readLockString + " nowait ";
        }
        if (timeout == -2) {
            return readLockString + " skip locked ";
        }
        return readLockString;
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        String readLockString = String.format(" for share of %s ", aliases);
        if (timeout == 0) {
            return readLockString + " nowait ";
        }
        if (timeout == -2) {
            return readLockString + " skip locked ";
        }
        return readLockString;
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
    public String getForUpdateNowaitString() {
        return this.getForUpdateString() + " nowait ";
    }

    @Override
    public String getForUpdateNowaitString(String aliases) {
        return this.getForUpdateString(aliases) + " nowait ";
    }

    @Override
    public String getForUpdateString(String aliases) {
        return this.getForUpdateString() + " of " + aliases;
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

