/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.event;

import java.net.URI;

public class AddGadgetFeedEvent {
    private final URI feedUri;

    public AddGadgetFeedEvent(URI feedUri) {
        this.feedUri = feedUri;
    }

    public URI getFeedUri() {
        return this.feedUri;
    }
}

