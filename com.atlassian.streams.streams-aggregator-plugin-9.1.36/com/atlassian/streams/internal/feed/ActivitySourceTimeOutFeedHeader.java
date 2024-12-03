/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.internal.feed.FeedHeader;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public class ActivitySourceTimeOutFeedHeader
implements FeedHeader {
    private final String sourceName;

    public ActivitySourceTimeOutFeedHeader(@Nonnull String sourceName) {
        this.sourceName = (String)Preconditions.checkNotNull((Object)sourceName);
    }

    @Nonnull
    public String getSourceName() {
        return this.sourceName;
    }
}

