/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.collab.impl;

import com.atlassian.confluence.pages.collab.SynchronyLockManager;
import com.atlassian.confluence.pages.collab.impl.DefaultSynchronyLockManager;
import java.util.Collection;
import java.util.Collections;

class DefaultSynchronyContentLock
implements SynchronyLockManager.SynchronyContentLock {
    private final DefaultSynchronyLockManager lockManager;
    private final Collection<Long> contentIds;
    private final boolean isGlobal;

    DefaultSynchronyContentLock(DefaultSynchronyLockManager lockManager) {
        this.lockManager = lockManager;
        this.contentIds = Collections.emptyList();
        this.isGlobal = true;
    }

    DefaultSynchronyContentLock(DefaultSynchronyLockManager lockManager, Collection<Long> contentIds) {
        this.lockManager = lockManager;
        this.contentIds = contentIds;
        this.isGlobal = false;
    }

    @Override
    public void close() throws Exception {
        this.unlock();
    }

    @Override
    public void unlock() {
        if (this.isGlobal) {
            this.lockManager.unlockContent();
        } else {
            this.lockManager.unlockContent(this.contentIds);
        }
    }
}

