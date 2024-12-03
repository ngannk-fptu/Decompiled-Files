/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.internal.atom.abdera.ActivityStreamsExtensionFactory;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.atlassian.streams.internal.feed.FeedModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.thread.ThreadExtensionFactory;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.factory.StreamBuilder;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMParser;
import org.apache.abdera.parser.stax.FOMParserFactory;
import org.apache.abdera.parser.stax.FOMWriter;
import org.apache.abdera.parser.stax.FOMWriterFactory;
import org.apache.abdera.parser.stax.FOMXPath;
import org.apache.abdera.parser.stax.StaxStreamWriter;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;
import org.joda.time.DateTime;

public final class StreamsAbdera {
    private static final Abdera abdera = StreamsAbdera.newAbdera(StreamsAbdera.newConfiguration());

    private StreamsAbdera() {
    }

    public static Abdera getAbdera() {
        return abdera;
    }

    private static Configuration newConfiguration() {
        return new StreamsAbderaConfiguration();
    }

    private static Abdera newAbdera(Configuration config) {
        return new Abdera(config);
    }

    private static final class StreamsAbderaConfiguration
    implements Configuration {
        final List<ExtensionFactory> extensionFactories = ImmutableList.of((Object)new ThreadExtensionFactory(), (Object)new ActivityStreamsExtensionFactory());
        final Map<String, NamedWriter> namedWriters = ImmutableMap.of((Object)"prettyxml", (Object)new PrettyWriter());
        final Map<String, NamedParser> namedParsers = Collections.emptyMap();
        final Map<String, Class<? extends StreamWriter>> streamWriters = ImmutableMap.of((Object)"default", StaxStreamWriter.class, (Object)"fom", StreamBuilder.class);

        private StreamsAbderaConfiguration() {
        }

        @Override
        public String getConfigurationOption(String id) {
            return null;
        }

        @Override
        public String getConfigurationOption(String id, String defaultValue) {
            return defaultValue;
        }

        @Override
        public List<ExtensionFactory> getExtensionFactories() {
            return this.extensionFactories;
        }

        @Override
        public Map<String, NamedParser> getNamedParsers() {
            return this.namedParsers;
        }

        @Override
        public Map<String, NamedWriter> getNamedWriters() {
            return this.namedWriters;
        }

        @Override
        public Map<String, Class<? extends StreamWriter>> getStreamWriters() {
            return this.streamWriters;
        }

        @Override
        public Factory newFactoryInstance(Abdera abdera) {
            return new FOMFactory(abdera);
        }

        @Override
        public ParserFactory newParserFactoryInstance(Abdera abdera) {
            return new FOMParserFactory(abdera);
        }

        @Override
        public Parser newParserInstance(Abdera abdera) {
            return new FOMParser(abdera);
        }

        @Override
        public StreamWriter newStreamWriterInstance(Abdera abdera) {
            return new StaxStreamWriter(abdera);
        }

        @Override
        public WriterFactory newWriterFactoryInstance(Abdera abdera) {
            return new FOMWriterFactory(abdera);
        }

        @Override
        public Writer newWriterInstance(Abdera abdera) {
            return new FOMWriter(abdera);
        }

        @Override
        public XPath newXPathInstance(Abdera abdera) {
            return new FOMXPath(abdera);
        }

        @Override
        public Configuration addExtensionFactory(ExtensionFactory factory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Configuration addNamedParser(NamedParser parser) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Configuration addNamedWriter(NamedWriter writer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Configuration addStreamWriter(Class<? extends StreamWriter> sw) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object clone() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class AtomParsedFeedHeader
    implements FeedHeader {
        private Element element;

        public AtomParsedFeedHeader(Element element) {
            this.element = element;
        }

        public Element getElement() {
            return this.element;
        }
    }

    public static final class AtomParsedFeedEntry
    extends FeedEntry {
        private final Entry atomEntry;
        private final DateTime entryDate;

        AtomParsedFeedEntry(Entry atomEntry, DateTime entryDate, Option<FeedModel> sourceFeed) {
            super(sourceFeed);
            this.atomEntry = atomEntry;
            this.entryDate = entryDate;
        }

        AtomParsedFeedEntry(Entry atomEntry) {
            this.atomEntry = atomEntry;
            this.entryDate = new DateTime((Object)atomEntry.getPublished());
        }

        public Entry getAtomEntry() {
            return this.atomEntry;
        }

        @Override
        public StreamsEntry getStreamsEntry() {
            throw new IllegalStateException("can't convert Atom entry back into StreamsEntry");
        }

        @Override
        public DateTime getEntryDate() {
            return this.entryDate;
        }

        @Override
        public FeedEntry toAggregatedEntry(Option<FeedModel> sourceFeed) {
            return new AtomParsedFeedEntry(this.atomEntry, this.entryDate, sourceFeed);
        }
    }
}

