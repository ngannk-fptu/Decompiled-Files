/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.syndication.feed.synd.SyndFeed
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.search.v2.ISearch;
import com.sun.syndication.feed.synd.SyndFeed;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Deprecated
public interface FeedBuilder {
    @Deprecated
    public SyndFeed createFeed(ISearch var1, FeedProperties var2);
}

