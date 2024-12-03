/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.feed;

import java.net.URI;

public interface GadgetFeedReader {
    public String getApplicationName();

    public String getTitle();

    public URI getIcon();

    public URI getBaseUri();

    public boolean contains(URI var1);

    public Iterable<URI> entries();
}

