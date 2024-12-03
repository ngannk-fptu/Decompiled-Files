/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.pages.collab.impl;

import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyLockEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyUnlockEvent;
import com.atlassian.confluence.pages.collab.SynchronyLockManager;
import com.atlassian.confluence.pages.collab.impl.DefaultSynchronyContentLock;
import com.atlassian.confluence.util.synchrony.SynchronyConfigurationReader;
import com.atlassian.event.api.EventPublisher;
import java.util.Collection;

public class DefaultSynchronyLockManager
implements SynchronyLockManager<DefaultSynchronyContentLock> {
    private final EventPublisher eventPublisher;
    private final SynchronyConfigurationReader synchronyConfig;

    public DefaultSynchronyLockManager(EventPublisher eventPublisher, SynchronyConfigurationReader synchronyConfig) {
        this.eventPublisher = eventPublisher;
        this.synchronyConfig = synchronyConfig;
    }

    @Override
    public DefaultSynchronyContentLock lockContent(Collection<Long> contentIds, Long timeout) {
        if (this.synchronyConfig.isSharedDraftsEnabled()) {
            this.eventPublisher.publish((Object)SynchronyLockEvent.lockEntities(contentIds, timeout));
        }
        return new DefaultSynchronyContentLock(this, contentIds);
    }

    @Override
    public DefaultSynchronyContentLock lockAllContent(long timeout) {
        this.eventPublisher.publish((Object)SynchronyLockEvent.lockEverything(timeout));
        return new DefaultSynchronyContentLock(this);
    }

    void unlockContent(Collection<Long> contentIds) {
        if (this.synchronyConfig.isSharedDraftsEnabled()) {
            this.eventPublisher.publish((Object)SynchronyUnlockEvent.unlockEntities(contentIds));
        }
    }

    void unlockContent() {
        this.eventPublisher.publish((Object)SynchronyUnlockEvent.unlockEverything());
    }
}

