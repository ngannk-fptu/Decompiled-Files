/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.entity.ContentType
 *  org.apache.http.util.EntityUtils
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class HttpClientResponse
implements Response {
    private final int statusCode;
    private final String statusText;
    private final byte[] body;
    private final Charset bodyCharset;
    private final Map<String, String> headers;

    public HttpClientResponse(CloseableHttpResponse response) throws IOException {
        this.statusCode = response.getStatusLine().getStatusCode();
        this.statusText = response.getStatusLine().getReasonPhrase();
        this.body = response.getEntity() != null ? EntityUtils.toByteArray((HttpEntity)response.getEntity()) : new byte[]{};
        this.bodyCharset = ContentType.getOrDefault((HttpEntity)response.getEntity()).getCharset();
        TreeMap<String, String> extractedHeaders = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (Header header : response.getAllHeaders()) {
            extractedHeaders.put(header.getName(), header.getValue());
        }
        this.headers = Collections.unmodifiableMap(extractedHeaders);
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 400;
    }

    public String getHeader(String name) {
        return this.headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getResponseBodyAsString() throws ResponseException {
        Charset decodeCharset = this.bodyCharset != null ? this.bodyCharset : Charset.defaultCharset();
        return new String(this.body, decodeCharset);
    }

    public InputStream getResponseBodyAsStream() throws ResponseException {
        return new ByteArrayInputStream(this.body);
    }

    public <T> T getEntity(Class<T> entityClass) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented");
    }
}

