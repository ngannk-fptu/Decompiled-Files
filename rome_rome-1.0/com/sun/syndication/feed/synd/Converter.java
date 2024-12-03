/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.synd.SyndFeed;

public interface Converter {
    public String getType();

    public void copyInto(WireFeed var1, SyndFeed var2);

    public WireFeed createRealFeed(SyndFeed var1);
}

