/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Preconditions;

public class StreamsFeed {
    private final String title;
    private final Option<String> subtitle;
    private final Iterable<StreamsEntry> entries;

    public StreamsFeed(String title, Iterable<StreamsEntry> entries, Option<String> subtitle) {
        this.title = (String)Preconditions.checkNotNull((Object)title, (Object)"title");
        this.entries = (Iterable)Preconditions.checkNotNull(entries, (Object)"entries");
        this.subtitle = (Option)Preconditions.checkNotNull(subtitle, (Object)"subtitle");
    }

    public String getTitle() {
        return this.title;
    }

    public Iterable<StreamsEntry> getEntries() {
        return this.entries;
    }

    public Option<String> getSubtitle() {
        return this.subtitle;
    }
}

