/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.confluence.event.events.admin.ReindexBatchFinishedEvent;
import com.atlassian.confluence.event.events.admin.ReindexBatchStartedEvent;
import com.atlassian.confluence.event.events.admin.ReindexFinishedEvent;
import com.atlassian.confluence.event.events.admin.ReindexSkippedEvent;
import com.atlassian.confluence.event.events.admin.ReindexStageFinishedEvent;
import com.atlassian.confluence.event.events.admin.ReindexStageStartedEvent;
import com.atlassian.confluence.event.events.admin.ReindexStartedEvent;
import com.atlassian.confluence.internal.index.ReindexProgress;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.util.Progress;
import com.atlassian.event.api.EventPublisher;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public final class EventPublishingReindexProgress
implements Progress,
ReindexProgress {
    private final EventPublisher eventPublisher;
    private final Progress progress;
    private final UUID reindexId = UUID.randomUUID();

    public EventPublishingReindexProgress(EventPublisher eventPublisher, Progress progress) {
        this.eventPublisher = eventPublisher;
        this.progress = progress;
    }

    @Override
    public void reindexStarted(EnumSet<ReIndexOption> options, List<String> spaceKeys) {
        this.eventPublisher.publish((Object)new ReindexStartedEvent(this, this.progress, this.reindexId, options, spaceKeys));
    }

    @Override
    public void reindexStageStarted(ReIndexOption option) {
        this.eventPublisher.publish((Object)new ReindexStageStartedEvent(this, option.name(), this.reindexId));
    }

    @Override
    public void reindexStageFinished(ReIndexOption option) {
        this.eventPublisher.publish((Object)new ReindexStageFinishedEvent(this, option.name(), this.reindexId));
    }

    @Override
    public void reindexFinished(List<String> spaceKeys) {
        this.eventPublisher.publish((Object)new ReindexFinishedEvent(this, this.reindexId, spaceKeys));
    }

    @Override
    public void reIndexSkipped() {
        this.eventPublisher.publish((Object)new ReindexSkippedEvent(this, this.reindexId));
    }

    @Override
    public void reindexBatchStarted() {
        this.eventPublisher.publish((Object)new ReindexBatchStartedEvent(this, this.reindexId));
    }

    @Override
    public void reindexBatchFinished() {
        this.eventPublisher.publish((Object)new ReindexBatchFinishedEvent(this, this.reindexId));
    }

    @Override
    public int getCount() {
        return this.progress.getCount();
    }

    @Override
    public int getTotal() {
        return this.progress.getTotal();
    }

    @Override
    public int getPercentComplete() {
        return this.progress.getPercentComplete();
    }

    @Override
    public int increment() {
        return this.progress.increment();
    }

    @Override
    public int increment(int delta) {
        return this.progress.increment(delta);
    }
}

