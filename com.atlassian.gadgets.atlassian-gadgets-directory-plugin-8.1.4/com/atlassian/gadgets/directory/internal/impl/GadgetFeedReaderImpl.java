/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.feed.GadgetFeedHostConnectionException
 *  com.atlassian.gadgets.feed.GadgetFeedParsingException
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.feed.atom.Entry
 *  com.rometools.rome.feed.atom.Feed
 *  com.rometools.rome.feed.atom.Link
 *  com.rometools.rome.feed.synd.SyndPerson
 *  com.rometools.rome.io.FeedException
 *  com.rometools.rome.io.WireFeedInput
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.httpcache4j.HTTPException
 *  org.codehaus.httpcache4j.HTTPRequest
 *  org.codehaus.httpcache4j.HTTPResponse
 *  org.codehaus.httpcache4j.payload.Payload
 *  org.jdom2.Document
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.directory.internal.GadgetHttpCache;
import com.atlassian.gadgets.feed.GadgetFeedHostConnectionException;
import com.atlassian.gadgets.feed.GadgetFeedParsingException;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException;
import com.google.common.collect.ImmutableSet;
import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.httpcache4j.HTTPException;
import org.codehaus.httpcache4j.HTTPRequest;
import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.payload.Payload;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GadgetFeedReaderImpl
implements GadgetFeedReader {
    private static final String ATOM = "atom_1.0";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Feed feed;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    GadgetFeedReaderImpl(URI feedURI, GadgetHttpCache http) {
        HTTPRequest request = new HTTPRequest(feedURI);
        try {
            HTTPResponse response = http.execute(request);
            InputStream is = ((Payload)response.getPayload().get()).getInputStream();
            try {
                this.feed = this.parseFeed(new InputStreamReader(is), feedURI);
            }
            finally {
                IOUtils.closeQuietly((InputStream)is);
            }
        }
        catch (HTTPException e) {
            throw new GadgetFeedHostConnectionException("Unable to connect to host", feedURI, (Throwable)e);
        }
    }

    private Feed parseFeed(Reader reader, URI feedURI) {
        FixedClassLoaderWireFeedInput feedBuilder = new FixedClassLoaderWireFeedInput();
        try {
            WireFeed feed = feedBuilder.build(reader);
            if (!ATOM.equals(feed.getFeedType())) {
                throw new NonAtomGadgetSpecFeedException(feedURI);
            }
            return (Feed)feed;
        }
        catch (IllegalArgumentException e) {
            throw new NonAtomGadgetSpecFeedException(feedURI);
        }
        catch (FeedException e) {
            throw new GadgetFeedParsingException("Unable to parse the feed, it is not validly formed", feedURI, (Throwable)e);
        }
    }

    public String getApplicationName() {
        List<SyndPerson> authors = this.getAuthors();
        if (authors.size() == 0) {
            return "";
        }
        return authors.get(0).getName();
    }

    public String getTitle() {
        return this.feed.getTitle();
    }

    public URI getIcon() {
        String icon = this.feed.getIcon();
        if (StringUtils.isBlank((CharSequence)icon)) {
            return null;
        }
        return URI.create(icon);
    }

    public URI getBaseUri() {
        for (Link link : this.getOtherLinks(this.feed)) {
            if (!"base".equals(link.getRel())) continue;
            try {
                return new URI(link.getHref());
            }
            catch (URISyntaxException e) {
                return null;
            }
        }
        return null;
    }

    private List<Link> getOtherLinks(Feed feed) {
        return feed.getOtherLinks();
    }

    public boolean contains(URI gadgetSpecUri) {
        return this.getGadgetUris().contains(gadgetSpecUri);
    }

    public Iterable<URI> entries() {
        return this.getGadgetUris();
    }

    private Set<URI> getGadgetUris() {
        ImmutableSet.Builder uris = ImmutableSet.builder();
        for (Entry entry : this.getEntries()) {
            String href = ((Link)entry.getAlternateLinks().get(0)).getHref();
            try {
                uris.add((Object)new URI(href));
            }
            catch (URISyntaxException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.info("URI '" + href + "' of gadget feed is invalid", (Throwable)e);
                    continue;
                }
                this.logger.info("URI '" + href + "' of gadget directory feed is invalid: " + e.getMessage());
            }
        }
        return uris.build();
    }

    private List<Entry> getEntries() {
        return this.feed.getEntries();
    }

    private List<SyndPerson> getAuthors() {
        return this.feed.getAuthors();
    }

    private static class FixedClassLoaderWireFeedInput
    extends WireFeedInput {
        private FixedClassLoaderWireFeedInput() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public WireFeed build(Document document) throws IllegalArgumentException, FeedException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(WireFeedInput.class.getClassLoader());
            try {
                WireFeed wireFeed = super.build(document);
                return wireFeed;
            }
            finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }
}

