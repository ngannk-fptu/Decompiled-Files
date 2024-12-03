/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.search.IndexerControl;

public class ThreadLocalIndexerControl
implements IndexerControl {
    private static final ThreadLocal<Object> indexingDisabledThreadLocal = new ThreadLocal();
    private static final ThreadLocalIndexerControl instance = new ThreadLocalIndexerControl();

    public static ThreadLocalIndexerControl getInstance() {
        return instance;
    }

    @Override
    public void suspend() {
        indexingDisabledThreadLocal.set(new Object());
    }

    @Override
    public void resume() {
        indexingDisabledThreadLocal.remove();
    }

    @Override
    public boolean indexingEnabled() {
        return indexingDisabledThreadLocal.get() == null;
    }

    @Override
    public boolean indexingDisabled() {
        return !this.indexingEnabled();
    }
}

