/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.feed;

import java.net.URI;

public class NonAtomGadgetSpecFeedException
extends RuntimeException {
    private final URI feedUri;

    public NonAtomGadgetSpecFeedException(URI feedUri) {
        this.feedUri = feedUri;
    }

    public URI getFeedUri() {
        return this.feedUri;
    }
}

