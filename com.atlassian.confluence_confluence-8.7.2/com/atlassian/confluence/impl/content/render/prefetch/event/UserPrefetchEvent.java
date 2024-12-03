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
@EventName(value="confluence.render.prefetch.users")
public class UserPrefetchEvent {
    private final Long renderedContentId;
    private final int userResourceCount;
    private final int userKeyCount;
    private final int confluenceUserCount;
    private final int crowdUserCount;
    private final Duration elapsedTime;

    private UserPrefetchEvent(@Nullable Long renderedContentId, int userResourceCount, int userKeyCount, int confluenceUserCount, int crowdUserCount, Duration elapsedTime) {
        this.renderedContentId = renderedContentId;
        this.userResourceCount = userResourceCount;
        this.userKeyCount = userKeyCount;
        this.confluenceUserCount = confluenceUserCount;
        this.crowdUserCount = crowdUserCount;
        this.elapsedTime = Objects.requireNonNull(elapsedTime);
    }

    public @Nullable Long getRenderedContentId() {
        return this.renderedContentId;
    }

    public int getUserResourceCount() {
        return this.userResourceCount;
    }

    public int getUserKeyCount() {
        return this.userKeyCount;
    }

    public int getConfluenceUserCount() {
        return this.confluenceUserCount;
    }

    public int getCrowdUserCount() {
        return this.crowdUserCount;
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
        private int userResourceCount;
        private int userKeyCount;
        private int confluenceUserCount;
        private int crowdUserCount;
        private Duration elapsedTime;

        private Builder renderedContentId(long renderedContentId) {
            this.renderedContentId = renderedContentId;
            return this;
        }

        public Builder userResourceCount(int userResourceCount) {
            this.userResourceCount = userResourceCount;
            return this;
        }

        public Builder userKeyCount(int userKeyCount) {
            this.userKeyCount = userKeyCount;
            return this;
        }

        public Builder crowdUserCount(int crowdUserCount) {
            this.crowdUserCount = crowdUserCount;
            return this;
        }

        public Builder confluenceUserCount(int confluenceUserCount) {
            this.confluenceUserCount = confluenceUserCount;
            return this;
        }

        public Builder elapsedTime(Duration elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public UserPrefetchEvent build() {
            return new UserPrefetchEvent(this.renderedContentId, this.userResourceCount, this.userKeyCount, this.confluenceUserCount, this.crowdUserCount, this.elapsedTime);
        }
    }
}

