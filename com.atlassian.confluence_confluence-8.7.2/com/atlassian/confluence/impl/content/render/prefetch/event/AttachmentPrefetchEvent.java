/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.content.render.prefetch.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.event.api.AsynchronousPreferred;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@EventName(value="confluence.render.prefetch.attachments")
public class AttachmentPrefetchEvent {
    private final Long renderedContentId;
    private final int preFetchedAttachmentCount;
    private final int totalAttachmentLoadCount;
    private final int discardedAttachmentCount;
    private final int unfetchedAttachmentCount;
    private final int preFetchedImageDetailsCount;
    private final Duration elapsedTime;

    private AttachmentPrefetchEvent(@Nullable Long renderedContentId, int preFetchedAttachmentCount, int totalAttachmentLoadCount, int discardedAttachmentCount, int unfetchedAttachmentCount, int preFetchedImageDetailsCount, Duration elapsedTime) {
        this.renderedContentId = renderedContentId;
        this.preFetchedAttachmentCount = preFetchedAttachmentCount;
        this.totalAttachmentLoadCount = totalAttachmentLoadCount;
        this.discardedAttachmentCount = discardedAttachmentCount;
        this.unfetchedAttachmentCount = unfetchedAttachmentCount;
        this.preFetchedImageDetailsCount = preFetchedImageDetailsCount;
        this.elapsedTime = Objects.requireNonNull(elapsedTime);
    }

    public @Nullable Long getRenderedContentId() {
        return this.renderedContentId;
    }

    public int getPreFetchedAttachmentCount() {
        return this.preFetchedAttachmentCount;
    }

    public int getTotalAttachmentLoadCount() {
        return this.totalAttachmentLoadCount;
    }

    public int getDiscardedAttachmentCount() {
        return this.discardedAttachmentCount;
    }

    public int getUnfetchedAttachmentCount() {
        return this.unfetchedAttachmentCount;
    }

    public int getPreFetchedImageDetailsCount() {
        return this.preFetchedImageDetailsCount;
    }

    public long getElapsedTimeMillis() {
        return this.elapsedTime.toMillis();
    }

    public static Builder builder(@Nullable ContentEntityObject renderedContentEntity) {
        Builder builder = new Builder();
        Optional.ofNullable(renderedContentEntity).map(EntityObject::getId).ifPresent(x$0 -> builder.renderedContentId((long)x$0));
        return builder;
    }

    public static class Builder {
        private Long renderedContentId;
        private int preFetchedAttachmentCount;
        private int totalAttachmentLoadCount;
        private int discardedAttachmentCount;
        private int unfetchedAttachmentCount;
        private int preFetchedImageDetailsCount;
        private Duration elapsedTime;

        private Builder renderedContentId(long renderedContentId) {
            this.renderedContentId = renderedContentId;
            return this;
        }

        public Builder preFetchedAttachmentCount(int preFetchedAttachmentCount) {
            this.preFetchedAttachmentCount = preFetchedAttachmentCount;
            return this;
        }

        public Builder totalAttachmentLoadCount(int totalAttachmentLoadCount) {
            this.totalAttachmentLoadCount = totalAttachmentLoadCount;
            return this;
        }

        public Builder discardedAttachmentCount(int discardedAttachmentCount) {
            this.discardedAttachmentCount = discardedAttachmentCount;
            return this;
        }

        public Builder unfetchedAttachmentCount(int unfetchedAttachmentCount) {
            this.unfetchedAttachmentCount = unfetchedAttachmentCount;
            return this;
        }

        public Builder preFetchedImageDetailsCount(int preFetchedImageDetailsCount) {
            this.preFetchedImageDetailsCount = preFetchedImageDetailsCount;
            return this;
        }

        public Builder elapsedTime(Duration elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public AttachmentPrefetchEvent build() {
            return new AttachmentPrefetchEvent(this.renderedContentId, this.preFetchedAttachmentCount, this.totalAttachmentLoadCount, this.discardedAttachmentCount, this.unfetchedAttachmentCount, this.preFetchedImageDetailsCount, this.elapsedTime);
        }
    }
}

