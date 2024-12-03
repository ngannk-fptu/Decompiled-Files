/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.util.BufferUtil
 */
package org.eclipse.jetty.client.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.BufferUtil;

public abstract class BufferingResponseListener
extends Response.Listener.Adapter {
    private final int maxLength;
    private ByteBuffer buffer;
    private String mediaType;
    private String encoding;

    public BufferingResponseListener() {
        this(0x200000);
    }

    public BufferingResponseListener(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Invalid max length " + maxLength);
        }
        this.maxLength = maxLength;
    }

    @Override
    public void onHeaders(Response response) {
        super.onHeaders(response);
        Request request = response.getRequest();
        HttpFields headers = response.getHeaders();
        long length = headers.getLongField(HttpHeader.CONTENT_LENGTH);
        if (HttpMethod.HEAD.is(request.getMethod())) {
            length = 0L;
        }
        if (length > (long)this.maxLength) {
            response.abort(new IllegalArgumentException("Buffering capacity " + this.maxLength + " exceeded"));
            return;
        }
        String contentType = headers.get(HttpHeader.CONTENT_TYPE);
        if (contentType != null) {
            int semicolon;
            String media = contentType;
            String charset = "charset=";
            int index = contentType.toLowerCase(Locale.ENGLISH).indexOf(charset);
            if (index > 0) {
                media = contentType.substring(0, index);
                String encoding = contentType.substring(index + charset.length());
                int semicolon2 = encoding.indexOf(59);
                if (semicolon2 > 0) {
                    encoding = encoding.substring(0, semicolon2).trim();
                }
                int lastIndex = encoding.length() - 1;
                if (encoding.charAt(0) == '\"' && encoding.charAt(lastIndex) == '\"') {
                    encoding = encoding.substring(1, lastIndex).trim();
                }
                this.encoding = encoding;
            }
            if ((semicolon = media.indexOf(59)) > 0) {
                media = media.substring(0, semicolon).trim();
            }
            this.mediaType = media;
        }
    }

    @Override
    public void onContent(Response response, ByteBuffer content) {
        int length = content.remaining();
        if (length > BufferUtil.space((ByteBuffer)this.buffer)) {
            int remaining;
            int n = remaining = this.buffer == null ? 0 : this.buffer.remaining();
            if (remaining + length > this.maxLength) {
                response.abort(new IllegalArgumentException("Buffering capacity " + this.maxLength + " exceeded"));
            }
            int requiredCapacity = this.buffer == null ? length : this.buffer.capacity() + length;
            int newCapacity = Math.min(Integer.highestOneBit(requiredCapacity) << 1, this.maxLength);
            this.buffer = BufferUtil.ensureCapacity((ByteBuffer)this.buffer, (int)newCapacity);
        }
        BufferUtil.append((ByteBuffer)this.buffer, (ByteBuffer)content);
    }

    @Override
    public abstract void onComplete(Result var1);

    public String getMediaType() {
        return this.mediaType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public byte[] getContent() {
        if (this.buffer == null) {
            return new byte[0];
        }
        return BufferUtil.toArray((ByteBuffer)this.buffer);
    }

    public String getContentAsString() {
        String encoding = this.encoding;
        if (encoding == null) {
            return this.getContentAsString(StandardCharsets.UTF_8);
        }
        return this.getContentAsString(encoding);
    }

    public String getContentAsString(String encoding) {
        if (this.buffer == null) {
            return null;
        }
        return BufferUtil.toString((ByteBuffer)this.buffer, (Charset)Charset.forName(encoding));
    }

    public String getContentAsString(Charset encoding) {
        if (this.buffer == null) {
            return null;
        }
        return BufferUtil.toString((ByteBuffer)this.buffer, (Charset)encoding);
    }

    public InputStream getContentAsInputStream() {
        if (this.buffer == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(this.buffer.array(), this.buffer.arrayOffset(), this.buffer.remaining());
    }
}

