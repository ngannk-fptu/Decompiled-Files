/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.FeedRendererContext;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

public interface FeedRenderer {
    public void writeFeed(URI var1, FeedModel var2, Writer var3, FeedRendererContext var4) throws IOException;

    public String getContentType();
}

