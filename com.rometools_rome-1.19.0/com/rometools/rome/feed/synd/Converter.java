/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.synd.SyndFeed;

public interface Converter {
    public String getType();

    public void copyInto(WireFeed var1, SyndFeed var2);

    public WireFeed createRealFeed(SyndFeed var1);
}

