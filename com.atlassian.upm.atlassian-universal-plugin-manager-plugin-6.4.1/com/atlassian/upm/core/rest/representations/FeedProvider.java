/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.feed.atom.Feed
 *  com.rometools.rome.io.FeedException
 *  com.rometools.rome.io.WireFeedInput
 *  com.rometools.rome.io.WireFeedOutput
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyReader
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.core.rest.representations;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(value={"application/atom+xml", "text/xml"})
@Produces(value={"application/atom+xml", "text/xml"})
public class FeedProvider
implements MessageBodyWriter<Feed>,
MessageBodyReader<Feed> {
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

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Feed.class.equals(type);
    }

    public Feed readFrom(Class<Feed> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            WireFeedInput input = new WireFeedInput();
            WireFeed wireFeed = input.build((Reader)new InputStreamReader(entityStream));
            if (!(wireFeed instanceof Feed)) {
                throw new IOException("Not an ATOM feed");
            }
            return (Feed)wireFeed;
        }
        catch (FeedException cause) {
            IOException effect = new IOException("Error reading ATOM feed");
            effect.initCause(cause);
            throw effect;
        }
    }
}

