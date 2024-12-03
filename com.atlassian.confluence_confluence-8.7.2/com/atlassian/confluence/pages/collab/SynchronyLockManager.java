/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.collab;

import java.util.Collection;

public interface SynchronyLockManager<L extends SynchronyContentLock> {
    public L lockContent(Collection<Long> var1, Long var2);

    public L lockAllContent(long var1);

    public static interface SynchronyContentLock
    extends AutoCloseable {
        public void unlock();
    }
}

