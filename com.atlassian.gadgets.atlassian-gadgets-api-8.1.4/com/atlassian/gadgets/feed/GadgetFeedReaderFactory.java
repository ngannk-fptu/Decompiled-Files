/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.feed;

import com.atlassian.gadgets.feed.GadgetFeedReader;
import java.net.URI;

public interface GadgetFeedReaderFactory {
    public GadgetFeedReader getFeedReader(URI var1);
}

