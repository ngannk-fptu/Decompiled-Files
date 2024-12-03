/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.pages.collab.impl.tracking;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.collab.SynchronyEvictionEvent;
import com.atlassian.confluence.pages.collab.SynchronyEvictionRemovalEvent;
import com.atlassian.confluence.pages.collab.SynchronyEvictionSearchEvent;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgress;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgressTracking;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;
import com.atlassian.event.api.EventPublisher;

@Internal
public class AnalyticsEvictionProgressTracking
implements SynchronyEvictionProgressTracking {
    private final EventPublisher eventPublisher;

    public AnalyticsEvictionProgressTracking(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SynchronyEvictionProgress startEviction(SynchronyEvictionType type, Integer thresholdHours, Integer limit) {
        return new SynchronyEvictionProgress().startEvictionTimer().setEvictionType(type).setThresholdHours(thresholdHours).setEvictionLimit(limit);
    }

    @Override
    public void finishEviction(SynchronyEvictionProgress progress, int contentsRemoved, int rowsRemoved) {
        this.eventPublisher.publish((Object)SynchronyEvictionEvent.successful(progress, contentsRemoved, rowsRemoved));
    }

    @Override
    public void failEviction(SynchronyEvictionProgress progress) {
        this.eventPublisher.publish((Object)SynchronyEvictionEvent.failed(progress));
    }

    @Override
    public void startSearch(SynchronyEvictionProgress progress, SynchronyEvictionSearchType type, Integer limit) {
        progress.startSearchTimer().setSearchType(type).setSearchLimit(limit);
    }

    @Override
    public void finishSearch(SynchronyEvictionProgress progress, Integer numberOfContent) {
        this.eventPublisher.publish((Object)SynchronyEvictionSearchEvent.successful(progress, numberOfContent));
    }

    @Override
    public void failSearch(SynchronyEvictionProgress progress) {
        this.eventPublisher.publish((Object)SynchronyEvictionSearchEvent.failed(progress));
    }

    @Override
    public void startRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfContent) {
        progress.startRemovalTimer().setRemovalNumberOfContent(numberOfContent);
    }

    @Override
    public void finishRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfRowsRemoved) {
        this.eventPublisher.publish((Object)SynchronyEvictionRemovalEvent.successful(progress, numberOfRowsRemoved));
    }

    @Override
    public void failRemovalUnderLock(SynchronyEvictionProgress progress) {
        this.eventPublisher.publish((Object)SynchronyEvictionRemovalEvent.failed(progress));
    }
}

