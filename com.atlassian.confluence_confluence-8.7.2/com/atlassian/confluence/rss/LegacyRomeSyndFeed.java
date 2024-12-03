/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.syndication.feed.synd.SyndEntry
 *  com.sun.syndication.feed.synd.SyndEntryImpl
 *  com.sun.syndication.feed.synd.SyndFeed
 *  com.sun.syndication.feed.synd.SyndFeedImpl
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.LegacyRomeSyndEntry;
import com.atlassian.confluence.rss.RomeSyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class LegacyRomeSyndFeed
implements RomeSyndFeed,
Supplier<SyndFeed> {
    private final SyndFeed delegate = new SyndFeedImpl();
    private final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    LegacyRomeSyndFeed() {
    }

    @Override
    public void setTitle(String title) {
        this.delegate.setTitle(title);
    }

    @Override
    public void setLink(String link) {
        this.delegate.setLink(link);
    }

    @Override
    public void setUri(String uri) {
        this.delegate.setUri(uri);
    }

    @Override
    public void setDescription(String description) {
        this.delegate.setDescription(description);
    }

    @Override
    public LegacyRomeSyndEntry addEntry() {
        SyndEntryImpl entry = new SyndEntryImpl();
        this.entries.add((SyndEntry)entry);
        return new LegacyRomeSyndEntry((SyndEntry)entry);
    }

    @Override
    public int getEntryCount() {
        return this.entries.size();
    }

    @Override
    public SyndFeed get() {
        this.delegate.setEntries(this.entries);
        return this.delegate;
    }
}

