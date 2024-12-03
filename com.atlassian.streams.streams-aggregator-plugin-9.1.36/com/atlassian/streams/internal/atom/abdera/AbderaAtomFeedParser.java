/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.atom.abdera.StreamsAbdera;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.FeedParser;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.Reader;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.joda.time.DateTime;

public class AbderaAtomFeedParser
implements FeedParser {
    private final Abdera abdera = StreamsAbdera.getAbdera();
    private Function<Entry, FeedEntry> abderaEntryToFeedEntry = new Function<Entry, FeedEntry>(){

        public FeedEntry apply(Entry from) {
            return new StreamsAbdera.AtomParsedFeedEntry(from);
        }
    };
    private Function<Element, FeedHeader> abderaExtensionToFeedHeader = new Function<Element, FeedHeader>(){

        public FeedHeader apply(Element from) {
            return new StreamsAbdera.AtomParsedFeedHeader(from);
        }
    };

    @Override
    public FeedModel readFeed(Reader reader) throws IOException, java.text.ParseException {
        Feed parsedFeed;
        Parser parser = this.abdera.getParser();
        try {
            parsedFeed = (Feed)parser.parse(reader).getRoot();
        }
        catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), 0);
        }
        Uri feedUri = parsedFeed.getSelfLink() != null ? Uri.parse((String)parsedFeed.getSelfLink().getHref().toASCIIString()) : Uri.parse((String)parsedFeed.getId().toASCIIString());
        FeedModel.Builder builder = FeedModel.builder(feedUri).title((Option<String>)Option.option((Object)parsedFeed.getTitle())).subtitle((Option<String>)Option.option((Object)parsedFeed.getSubtitle())).addEntries(Iterables.transform(parsedFeed.getEntries(), this.abderaEntryToFeedEntry)).addHeaders(Iterables.transform(parsedFeed.getExtensions(), this.abderaExtensionToFeedHeader)).encodedContent(parsedFeed);
        if (parsedFeed.getUpdated() != null) {
            builder.updated((Option<DateTime>)Option.some((Object)new DateTime((Object)parsedFeed.getUpdated())));
        }
        return builder.build();
    }
}

