/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.synd.SyndFeed
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.search.v2.ISearch;
import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SyndFeedService {
    public SyndFeed createSyndFeed(ISearch var1, FeedProperties var2);
}

