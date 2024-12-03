/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import org.jdom2.Document;

public interface WireFeedGenerator {
    public String getType();

    public Document generate(WireFeed var1) throws IllegalArgumentException, FeedException;
}

