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

public interface WireFeedGenerator {
    public String getType();

    public Document generate(WireFeed var1) throws IllegalArgumentException, FeedException;
}

