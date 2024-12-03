/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import java.io.IOException;

public abstract class LockFactory {
    protected String lockPrefix = null;

    public void setLockPrefix(String lockPrefix) {
        this.lockPrefix = lockPrefix;
    }

    public String getLockPrefix() {
        return this.lockPrefix;
    }

    public abstract Lock makeLock(String var1);

    public abstract void clearLock(String var1) throws IOException;
}

