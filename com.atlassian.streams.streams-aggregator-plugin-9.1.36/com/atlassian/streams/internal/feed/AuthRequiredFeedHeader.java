/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.internal.feed.AbstractApplicationSpecificFeedHeader;
import java.net.URI;

public class AuthRequiredFeedHeader
extends AbstractApplicationSpecificFeedHeader {
    private final URI authUri;

    public AuthRequiredFeedHeader(String applicationId, String applicationName, URI applicationUri, URI authUri) {
        super(applicationId, applicationName, applicationUri);
        this.authUri = authUri;
    }

    public URI getAuthUri() {
        return this.authUri;
    }
}

