/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 */
package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.FeedException;
import org.jdom.Document;

public interface WireFeedParser {
    public String getType();

    public boolean isMyType(Document var1);

    public WireFeed parse(Document var1, boolean var2) throws IllegalArgumentException, FeedException;
}

