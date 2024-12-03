/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.feed.atom.Feed
 *  com.rometools.rome.io.FeedException
 *  com.rometools.rome.io.WireFeedOutput
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.gadgets.publisher.internal.rest;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
public class FeedWriter
implements MessageBodyWriter<Feed> {
    private static final String ATOM = "atom_1.0";
    private static final String DEFAULT_ENCODING = "UTF-8";

    public long getSize(Feed t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Feed.class.equals(type);
    }

    public void writeTo(Feed feed, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, feed.getEncoding() != null ? feed.getEncoding() : DEFAULT_ENCODING);
        if (feed.getFeedType() == null) {
            feed.setFeedType(ATOM);
        }
        ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Feed.class.getClassLoader());
        try {
            WireFeedOutput wireFeedOutput = new WireFeedOutput();
            wireFeedOutput.output((WireFeed)feed, (Writer)writer);
            ((Writer)writer).flush();
        }
        catch (FeedException cause) {
            IOException effect = new IOException("Error marshalling atom feed");
            effect.initCause(cause);
            throw effect;
        }
        finally {
            Thread.currentThread().setContextClassLoader(origLoader);
        }
    }
}

