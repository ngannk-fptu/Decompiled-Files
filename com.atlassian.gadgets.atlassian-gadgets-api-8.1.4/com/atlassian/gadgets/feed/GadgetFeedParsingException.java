/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.feed;

import java.net.URI;

public class GadgetFeedParsingException
extends RuntimeException {
    private final URI feedUri;

    public GadgetFeedParsingException(String message, URI feedUri, Throwable cause) {
        super(message, cause);
        this.feedUri = feedUri;
    }

    public URI getFeedUri() {
        return this.feedUri;
    }
}

