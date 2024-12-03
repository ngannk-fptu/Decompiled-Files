/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.content.page.synchrony;

import com.atlassian.annotations.Internal;
import java.util.Collection;
import java.util.Collections;

@Internal
public class SynchronyLockEvent {
    private final Collection<Long> contentIds;
    private final Long timeout;
    private final boolean lockEverything;

    public static SynchronyLockEvent lockEntities(Collection<Long> contentIds, Long timeout) {
        return new SynchronyLockEvent(contentIds, false, timeout);
    }

    public static SynchronyLockEvent lockEverything(Long timeout) {
        return new SynchronyLockEvent(Collections.emptyList(), true, timeout);
    }

    private SynchronyLockEvent(Collection<Long> contentIds, boolean lockEverything, Long timeout) {
        this.contentIds = contentIds;
        this.timeout = timeout;
        this.lockEverything = lockEverything;
    }

    public Collection<Long> getContentIds() {
        return this.contentIds;
    }

    public Long getTimeout() {
        return this.timeout;
    }

    public boolean isGlobal() {
        return this.lockEverything;
    }
}

