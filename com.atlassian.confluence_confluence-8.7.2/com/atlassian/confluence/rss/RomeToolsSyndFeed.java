/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.synd.SyndEntry
 *  com.rometools.rome.feed.synd.SyndEntryImpl
 *  com.rometools.rome.feed.synd.SyndFeed
 *  com.rometools.rome.feed.synd.SyndFeedImpl
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.RomeSyndFeed;
import com.atlassian.confluence.rss.RomeToolsSyndEntry;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class RomeToolsSyndFeed
implements RomeSyndFeed,
Supplier<SyndFeed> {
    private final SyndFeed delegate = new SyndFeedImpl();
    private final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    RomeToolsSyndFeed() {
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
    public RomeToolsSyndEntry addEntry() {
        SyndEntryImpl entry = new SyndEntryImpl();
        this.entries.add((SyndEntry)entry);
        return new RomeToolsSyndEntry((SyndEntry)entry);
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

