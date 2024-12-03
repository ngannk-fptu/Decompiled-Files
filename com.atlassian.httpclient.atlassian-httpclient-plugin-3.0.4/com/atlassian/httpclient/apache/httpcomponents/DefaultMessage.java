/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.Headers;
import com.atlassian.httpclient.api.Message;
import io.atlassian.fugue.Option;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.util.CharArrayBuffer;

abstract class DefaultMessage
implements Message {
    private final InputStream entityStream;
    private final Headers headers;
    private final long maxEntitySize;
    private boolean hasRead;

    public DefaultMessage(Headers headers, InputStream entityStream, Option<Long> maxEntitySize) {
        this.maxEntitySize = (Long)maxEntitySize.getOrElse((Object)Integer.MAX_VALUE);
        this.headers = headers;
        this.entityStream = entityStream;
    }

    @Override
    public String getContentType() {
        return this.headers.getContentType();
    }

    @Override
    public String getContentCharset() {
        return this.headers.getContentCharset();
    }

    public String getAccept() {
        return this.headers.getHeader("Accept");
    }

    @Override
    public InputStream getEntityStream() throws IllegalStateException {
        this.checkRead();
        return this.entityStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getEntity() throws IllegalStateException, IllegalArgumentException {
        String entity = null;
        if (this.hasEntity()) {
            String string;
            this.checkValidSize();
            String charsetAsString = this.getContentCharset();
            Charset charset = charsetAsString != null ? Charset.forName(charsetAsString) : Charset.forName("UTF-8");
            InputStream instream = this.getEntityStream();
            if (instream == null) {
                return null;
            }
            try {
                int l;
                int bufferLength = 4096;
                String lengthHeader = this.getHeader("Content-Length");
                if (lengthHeader != null) {
                    bufferLength = Integer.parseInt(lengthHeader);
                }
                InputStreamReader reader = new InputStreamReader(instream, charset);
                CharArrayBuffer buffer = new CharArrayBuffer(bufferLength);
                char[] tmp = new char[1024];
                while ((l = reader.read(tmp)) != -1) {
                    if ((long)(buffer.length() + l) > this.maxEntitySize) {
                        throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                    }
                    buffer.append(tmp, 0, l);
                }
                string = buffer.toString();
            }
            catch (Throwable throwable) {
                try {
                    instream.close();
                    throw throwable;
                }
                catch (IOException e) {
                    throw new IllegalStateException("Unable to convert response body to String", e);
                }
            }
            instream.close();
            return string;
        }
        return entity;
    }

    @Override
    public boolean hasEntity() {
        return this.entityStream != null;
    }

    @Override
    public boolean hasReadEntity() {
        return this.hasRead;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers.getHeaders();
    }

    @Override
    public String getHeader(String name) {
        return this.headers.getHeader(name);
    }

    public Message validate() {
        if (this.hasEntity() && this.headers.getContentType() == null) {
            throw new IllegalStateException("Property contentType must be set when entity is present");
        }
        return this;
    }

    private void checkRead() throws IllegalStateException {
        if (this.entityStream != null) {
            if (this.hasRead) {
                throw new IllegalStateException("Entity may only be accessed once");
            }
            this.hasRead = true;
        }
    }

    private void checkValidSize() throws IllegalArgumentException {
        Integer contentLength;
        String lengthHeader = this.getHeader("Content-Length");
        if (lengthHeader != null && (long)(contentLength = Integer.valueOf(Integer.parseInt(lengthHeader))).intValue() > this.maxEntitySize) {
            throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
        }
    }
}

