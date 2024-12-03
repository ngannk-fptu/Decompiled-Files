/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.internal.feed.FeedHeader;
import java.net.URI;

public abstract class AbstractApplicationSpecificFeedHeader
implements FeedHeader {
    private final String applicationId;
    private final String applicationName;
    private final URI applicationUri;

    protected AbstractApplicationSpecificFeedHeader(String applicationId, String applicationName, URI applicationUri) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationUri = applicationUri;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public URI getApplicationUri() {
        return this.applicationUri;
    }
}

