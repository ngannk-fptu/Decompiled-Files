/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.Response$Status
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public abstract class BaseFormProvider<T extends MultivaluedMap<String, String>>
extends AbstractMessageReaderWriterProvider<T> {
    public T readFrom(T map, MediaType mediaType, InputStream entityStream) throws IOException {
        String encoded = BaseFormProvider.readFromAsString(entityStream, mediaType);
        String charsetName = ReaderWriter.getCharset(mediaType).name();
        StringTokenizer tokenizer = new StringTokenizer(encoded, "&");
        try {
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int idx = token.indexOf(61);
                if (idx < 0) {
                    map.add((Object)URLDecoder.decode(token, charsetName), null);
                    continue;
                }
                if (idx <= 0) continue;
                map.add((Object)URLDecoder.decode(token.substring(0, idx), charsetName), (Object)URLDecoder.decode(token.substring(idx + 1), charsetName));
            }
            return map;
        }
        catch (IllegalArgumentException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
    }

    public void writeTo(T t, MediaType mediaType, OutputStream entityStream) throws IOException {
        String charsetName = ReaderWriter.getCharset(mediaType).name();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry e : t.entrySet()) {
            for (String value : (List)e.getValue()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode((String)e.getKey(), charsetName));
                if (value == null) continue;
                sb.append('=');
                sb.append(URLEncoder.encode(value, charsetName));
            }
        }
        BaseFormProvider.writeToAsString(sb.toString(), entityStream, mediaType);
    }
}

