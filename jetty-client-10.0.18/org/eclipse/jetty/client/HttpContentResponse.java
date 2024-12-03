/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpVersion
 */
package org.eclipse.jetty.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;

public class HttpContentResponse
implements ContentResponse {
    private final Response response;
    private final byte[] content;
    private final String mediaType;
    private final String encoding;

    public HttpContentResponse(Response response, byte[] content, String mediaType, String encoding) {
        this.response = response;
        this.content = content;
        this.mediaType = mediaType;
        this.encoding = encoding;
    }

    @Override
    public Request getRequest() {
        return this.response.getRequest();
    }

    @Override
    public <T extends Response.ResponseListener> List<T> getListeners(Class<T> listenerClass) {
        return this.response.getListeners(listenerClass);
    }

    @Override
    public HttpVersion getVersion() {
        return this.response.getVersion();
    }

    @Override
    public int getStatus() {
        return this.response.getStatus();
    }

    @Override
    public String getReason() {
        return this.response.getReason();
    }

    @Override
    public HttpFields getHeaders() {
        return this.response.getHeaders();
    }

    @Override
    public boolean abort(Throwable cause) {
        return this.response.abort(cause);
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public String getContentAsString() {
        String encoding = this.encoding;
        if (encoding == null) {
            return new String(this.getContent(), StandardCharsets.UTF_8);
        }
        try {
            return new String(this.getContent(), encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(encoding);
        }
    }

    public String toString() {
        return String.format("%s[%s %d %s - %d bytes]", HttpContentResponse.class.getSimpleName(), this.getVersion(), this.getStatus(), this.getReason(), this.getContent().length);
    }
}

