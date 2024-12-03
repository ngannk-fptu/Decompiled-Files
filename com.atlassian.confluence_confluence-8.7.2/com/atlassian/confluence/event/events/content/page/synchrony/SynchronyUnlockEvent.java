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
public class SynchronyUnlockEvent {
    private final Collection<Long> contentIds;
    private final boolean unlockEverything;

    public static SynchronyUnlockEvent unlockEntities(Collection<Long> contentIds) {
        return new SynchronyUnlockEvent(contentIds, false);
    }

    public static SynchronyUnlockEvent unlockEverything() {
        return new SynchronyUnlockEvent(Collections.emptyList(), true);
    }

    private SynchronyUnlockEvent(Collection<Long> contentIds, boolean unlockEverything) {
        this.contentIds = contentIds;
        this.unlockEverything = unlockEverything;
    }

    public Collection<Long> getContentIds() {
        return this.contentIds;
    }

    public boolean isGlobal() {
        return this.unlockEverything;
    }
}

