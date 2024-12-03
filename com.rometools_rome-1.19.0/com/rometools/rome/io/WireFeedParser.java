/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import java.util.Locale;
import org.jdom2.Document;

public interface WireFeedParser {
    public String getType();

    public boolean isMyType(Document var1);

    public WireFeed parse(Document var1, boolean var2, Locale var3) throws IllegalArgumentException, FeedException;
}

