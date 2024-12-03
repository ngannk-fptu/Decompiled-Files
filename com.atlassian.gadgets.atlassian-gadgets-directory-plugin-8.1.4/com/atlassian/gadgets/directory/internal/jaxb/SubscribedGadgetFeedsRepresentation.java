/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.plugins.rest.common.Link
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.directory.internal.DirectoryUrlBuilder;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.plugins.rest.common.Link;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="subscribed-gadget-feeds")
public class SubscribedGadgetFeedsRepresentation {
    @XmlElement
    private final Link self;
    @XmlElement(name="feeds")
    private final List<BriefSubscribedGadgetFeedRepresentation> feeds;

    public SubscribedGadgetFeedsRepresentation(Iterable<SubscribedGadgetFeed> feeds, DirectoryUrlBuilder urlBuilder, GadgetFeedReaderFactory readerFactory) {
        Preconditions.checkNotNull(feeds);
        Preconditions.checkNotNull((Object)urlBuilder);
        Preconditions.checkNotNull((Object)readerFactory);
        this.self = Link.self((URI)URI.create(urlBuilder.buildSubscribedGadgetFeedsUrl()));
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SubscribedGadgetFeed feed : feeds) {
            builder.add((Object)new BriefSubscribedGadgetFeedRepresentation(feed, urlBuilder, readerFactory));
        }
        this.feeds = builder.build();
    }

    private SubscribedGadgetFeedsRepresentation() {
        this.self = null;
        this.feeds = null;
    }

    public Link getSelf() {
        return this.self;
    }

    public List<BriefSubscribedGadgetFeedRepresentation> getSubscribedGadgetFeeds() {
        return this.feeds;
    }

    public static final class BriefSubscribedGadgetFeedRepresentation {
        @XmlTransient
        private static final Logger LOG = LoggerFactory.getLogger(SubscribedGadgetFeedsRepresentation.class);
        @XmlElement
        private final Link self;
        @XmlElement
        private final Link feed;
        @XmlElement
        private final String id;
        @XmlElement
        private String name;
        @XmlElement
        private URI icon;
        @XmlElement
        private String title;
        @XmlElement
        private URI baseUri;
        @XmlElement
        private boolean invalid;

        public BriefSubscribedGadgetFeedRepresentation(SubscribedGadgetFeed feed, DirectoryUrlBuilder urlBuilder, GadgetFeedReaderFactory readerFactory) {
            this.self = Link.self((URI)URI.create(urlBuilder.buildSubscribedGadgetFeedUrl(feed.getId())));
            this.feed = Link.link((URI)feed.getUri(), (String)"alternate");
            this.id = feed.getId();
            try {
                GadgetFeedReader reader = readerFactory.getFeedReader(feed.getUri());
                this.name = reader.getApplicationName();
                this.icon = reader.getIcon();
                this.title = reader.getTitle();
                this.baseUri = reader.getBaseUri();
                this.invalid = false;
            }
            catch (RuntimeException e) {
                LOG.info("Subscribed Gadget Feed is invalid because of exception", (Throwable)e);
                this.invalid = true;
            }
        }

        private BriefSubscribedGadgetFeedRepresentation() {
            this.self = null;
            this.feed = null;
            this.id = null;
            this.name = null;
            this.icon = null;
            this.title = null;
            this.baseUri = null;
        }

        public String getId() {
            return this.id;
        }

        public Link getSelf() {
            return this.self;
        }

        public Link getFeed() {
            return this.feed;
        }

        public String getName() {
            return this.name;
        }

        public URI getIcon() {
            return this.icon;
        }

        public String getTitle() {
            return this.title;
        }

        public URI getBaseUri() {
            return this.baseUri;
        }
    }
}

