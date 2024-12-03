/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.UserIndexingManagerInternal;

public class DefaultUserIndexingManagerInternal
implements UserIndexingManagerInternal {
    private final ThreadLocal<Boolean> processEvents = new ThreadLocal();

    @Override
    public boolean shouldProcessEvents() {
        Boolean process = this.processEvents.get();
        return process == null ? true : process;
    }

    @Override
    public void setProcessEventsForCurrentThread(boolean enabled) {
        if (enabled) {
            this.processEvents.remove();
        } else {
            this.processEvents.set(false);
        }
    }
}

