/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.feed;

import java.net.URI;

public class GadgetFeedHostConnectionException
extends RuntimeException {
    private final URI feedUri;

    public GadgetFeedHostConnectionException(String message, URI feedUri, Throwable cause) {
        super(message, cause);
        this.feedUri = feedUri;
    }

    public URI getFeedUri() {
        return this.feedUri;
    }
}

