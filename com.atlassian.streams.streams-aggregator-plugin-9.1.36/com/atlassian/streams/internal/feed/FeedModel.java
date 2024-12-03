/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.DateUtil
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.api.DateUtil;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import java.net.URI;
import java.time.ZonedDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joda.time.DateTime;

public class FeedModel {
    private final Uri uri;
    private final Option<String> title;
    private final Option<String> subtitle;
    private final ZonedDateTime updated;
    private final ImmutableMultimap<String, Uri> links;
    private final ImmutableList<FeedHeader> headers;
    private final ImmutableList<FeedEntry> entries;
    private final Option<Object> encodedContent;

    public static Builder builder(Uri uri) {
        return new Builder(uri);
    }

    public static Builder builder(URI uri) {
        return new Builder(Uri.fromJavaUri((URI)uri));
    }

    private FeedModel(Builder builder) {
        this.uri = builder.uri;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.updated = builder.updated;
        this.links = ImmutableMultimap.copyOf((Multimap)builder.links);
        this.headers = ImmutableList.copyOf((Iterable)builder.headers);
        this.entries = ImmutableList.copyOf((Iterable)builder.entries);
        this.encodedContent = builder.encodedContent;
    }

    public Uri getUri() {
        return this.uri;
    }

    public Option<String> getTitle() {
        return this.title;
    }

    public Option<String> getSubtitle() {
        return this.subtitle;
    }

    @Deprecated
    public Option<DateTime> getUpdated() {
        return Option.option((Object)DateUtil.fromZonedDate((ZonedDateTime)this.updated));
    }

    @Nullable
    public ZonedDateTime getUpdatedDate() {
        return this.updated;
    }

    public ImmutableMultimap<String, Uri> getLinks() {
        return this.links;
    }

    public Iterable<FeedHeader> getHeaders() {
        return this.headers;
    }

    public Iterable<FeedEntry> getEntries() {
        return this.entries;
    }

    public Option<Object> getEncodedContent() {
        return this.encodedContent;
    }

    public static class Builder {
        private Uri uri;
        private Option<String> title = Option.none();
        private Option<String> subtitle = Option.none();
        private ZonedDateTime updated = null;
        private Multimap<String, Uri> links = HashMultimap.create();
        private Iterable<FeedHeader> headers = ImmutableList.of();
        private Iterable<FeedEntry> entries = ImmutableList.of();
        private Option<Object> encodedContent = Option.none();

        public Builder(Uri uri) {
            this.uri = uri;
        }

        public Builder(FeedModel from) {
            this.uri = from.getUri();
            this.title = from.getTitle();
            this.subtitle = from.getSubtitle();
            this.updated = from.updated;
            this.links = HashMultimap.create(from.getLinks());
            this.headers = from.getHeaders();
            this.entries = from.getEntries();
            this.encodedContent = from.getEncodedContent();
        }

        public Builder title(Option<String> title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(Option<String> subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        @Deprecated
        public Builder updated(Option<DateTime> updated) {
            this.updated = updated.isDefined() ? DateUtil.toZonedDate((DateTime)((DateTime)updated.get())) : null;
            return this;
        }

        public Builder updated(@Nonnull ZonedDateTime updated) {
            this.updated = updated;
            return this;
        }

        public Builder replaceLink(String rel, Uri href) {
            this.links.removeAll((Object)rel);
            this.links.put((Object)rel, (Object)href);
            return this;
        }

        public Builder addLink(String rel, Uri href) {
            this.links.put((Object)rel, (Object)href);
            return this;
        }

        public Builder addLinkIfNotPresent(String rel, Uri href) {
            if (!this.links.containsKey((Object)rel)) {
                this.links.put((Object)rel, (Object)href);
            }
            return this;
        }

        public Builder addHeaders(Iterable<FeedHeader> headers) {
            this.headers = Iterables.concat(this.headers, headers);
            return this;
        }

        public Builder addEntries(Iterable<FeedEntry> entries) {
            this.entries = Iterables.concat(this.entries, entries);
            return this;
        }

        public Builder encodedContent(Object encodedContent) {
            this.encodedContent = Option.some((Object)encodedContent);
            return this;
        }

        public FeedModel build() {
            return new FeedModel(this);
        }
    }
}

