/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.internal.feed.FeedModel;
import com.google.common.base.Function;
import org.joda.time.DateTime;

public abstract class FeedEntry {
    private final Option<FeedModel> sourceFeed;

    public abstract StreamsEntry getStreamsEntry();

    public abstract DateTime getEntryDate();

    public Option<FeedModel> getSourceFeed() {
        return this.sourceFeed;
    }

    public abstract FeedEntry toAggregatedEntry(Option<FeedModel> var1);

    protected FeedEntry() {
        this((Option<FeedModel>)Option.none(FeedModel.class));
    }

    protected FeedEntry(Option<FeedModel> sourceFeed) {
        this.sourceFeed = sourceFeed;
    }

    public static FeedEntry fromStreamsEntry(StreamsEntry streamsEntry) {
        return new LocalEntryWrapper(streamsEntry.toStaticEntry(), (Option<FeedModel>)Option.none(FeedModel.class));
    }

    public static final Function<StreamsEntry, FeedEntry> fromStreamsEntry() {
        return new Function<StreamsEntry, FeedEntry>(){

            public FeedEntry apply(StreamsEntry from) {
                return FeedEntry.fromStreamsEntry(from);
            }
        };
    }

    private static class LocalEntryWrapper
    extends FeedEntry {
        private final StreamsEntry streamsEntry;

        LocalEntryWrapper(StreamsEntry streamsEntry, Option<FeedModel> sourceFeed) {
            super(sourceFeed);
            this.streamsEntry = streamsEntry;
        }

        @Override
        public StreamsEntry getStreamsEntry() {
            return this.streamsEntry;
        }

        @Override
        public DateTime getEntryDate() {
            return this.streamsEntry.getPostedDate();
        }

        @Override
        public FeedEntry toAggregatedEntry(Option<FeedModel> sourceFeed) {
            return new LocalEntryWrapper(this.streamsEntry, sourceFeed);
        }
    }
}

