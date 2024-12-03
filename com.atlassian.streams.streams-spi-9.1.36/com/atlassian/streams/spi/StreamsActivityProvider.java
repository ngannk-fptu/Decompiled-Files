/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.StreamsFeed
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.spi.CancellableTask;

public interface StreamsActivityProvider {
    public static final String REPLY_TO_LINK_REL = "http://streams.atlassian.com/syndication/reply-to";
    public static final String ICON_LINK_REL = "http://streams.atlassian.com/syndication/icon";
    public static final String CSS_LINK_REL = "http://streams.atlassian.com/syndication/css";
    @Deprecated
    public static final String TIMEDOUT_INCREASE_LINK_REL = "http://streams.atlassian.com/syndication/timedout-increase";
    @Deprecated
    public static final String TIMEDOUT_RETRY_LINK_REL = "http://streams.atlassian.com/syndication/timedout-retry";
    public static final String WATCH_LINK_REL = "http://streams.atlassian.com/syndication/watch";

    public CancellableTask<StreamsFeed> getActivityFeed(ActivityRequest var1) throws StreamsException;
}

