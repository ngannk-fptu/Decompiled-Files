/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.directory.spi;

import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import java.net.URI;

public interface SubscribedGadgetFeedStore {
    public SubscribedGadgetFeed addFeed(URI var1);

    public boolean containsFeed(String var1);

    public SubscribedGadgetFeed getFeed(String var1);

    public Iterable<SubscribedGadgetFeed> getAllFeeds();

    public void removeFeed(String var1);
}

