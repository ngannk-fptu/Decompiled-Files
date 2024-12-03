/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.internal.feed.FeedModel;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public interface FeedParser {
    public FeedModel readFeed(Reader var1) throws IOException, ParseException;
}

