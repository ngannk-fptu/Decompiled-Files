/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.gadgets.directory.spi;

import com.google.common.base.Preconditions;
import java.net.URI;

public class SubscribedGadgetFeed {
    private final String id;
    private final URI feedUri;

    public SubscribedGadgetFeed(String id, URI feedUri) {
        this.id = (String)Preconditions.checkNotNull((Object)id, (Object)"id");
        this.feedUri = (URI)Preconditions.checkNotNull((Object)feedUri, (Object)"feedUri");
    }

    public String getId() {
        return this.id;
    }

    public URI getUri() {
        return this.feedUri;
    }

    public String toString() {
        return this.feedUri.toASCIIString();
    }
}

