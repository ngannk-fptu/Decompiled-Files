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
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="subscribed-gadget-feed")
public class SubscribedGadgetFeedRepresentation {
    private static final Logger logger = LoggerFactory.getLogger(SubscribedGadgetFeedRepresentation.class);
    @XmlElement
    private final Link feed;
    @XmlElement
    private final Link self;
    @XmlElementWrapper
    @XmlElement(name="gadget")
    final List<GadgetSpecRepresentation> gadgets;

    public SubscribedGadgetFeedRepresentation(SubscribedGadgetFeed feed, DirectoryUrlBuilder urlBuilder, GadgetFeedReaderFactory readerFactory) {
        Preconditions.checkNotNull((Object)feed, (Object)"feed");
        this.feed = Link.link((URI)feed.getUri(), (String)"alternate");
        this.self = Link.self((URI)URI.create(urlBuilder.buildSubscribedGadgetFeedUrl(feed.getId())));
        ImmutableList.Builder builder = ImmutableList.builder();
        try {
            GadgetFeedReader feedReader = readerFactory.getFeedReader(feed.getUri());
            for (URI uri : feedReader.entries()) {
                builder.add((Object)new GadgetSpecRepresentation(uri));
            }
        }
        catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.warn("Unable to parse feed from, Skipping: {}", (Object)feed.getUri().toString(), (Object)e);
            }
            logger.warn("Unable to parse feed from, Skipping: {}", (Object)feed.getUri().toString());
        }
        this.gadgets = builder.build();
    }

    private SubscribedGadgetFeedRepresentation() {
        this.feed = null;
        this.self = null;
        this.gadgets = null;
    }

    public Link getFeed() {
        return this.feed;
    }

    public Link getSelf() {
        return this.self;
    }

    public List<GadgetSpecRepresentation> getGadgets() {
        return this.gadgets;
    }

    public static final class GadgetSpecRepresentation {
        @XmlAttribute
        private final URI uri;

        public GadgetSpecRepresentation(URI uri) {
            this.uri = uri;
        }

        private GadgetSpecRepresentation() {
            this.uri = null;
        }

        public URI getUri() {
            return this.uri;
        }
    }
}

