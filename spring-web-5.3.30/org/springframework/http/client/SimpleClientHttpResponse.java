/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

final class SimpleClientHttpResponse
extends AbstractClientHttpResponse {
    private final HttpURLConnection connection;
    @Nullable
    private HttpHeaders headers;
    @Nullable
    private InputStream responseStream;

    SimpleClientHttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }

    @Override
    public String getStatusText() throws IOException {
        String result = this.connection.getResponseMessage();
        return result != null ? result : "";
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            String name = this.connection.getHeaderFieldKey(0);
            if (StringUtils.hasLength((String)name)) {
                this.headers.add(name, this.connection.getHeaderField(0));
            }
            int i = 1;
            while (StringUtils.hasLength((String)(name = this.connection.getHeaderFieldKey(i)))) {
                this.headers.add(name, this.connection.getHeaderField(i));
                ++i;
            }
        }
        return this.headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        InputStream errorStream = this.connection.getErrorStream();
        this.responseStream = errorStream != null ? errorStream : this.connection.getInputStream();
        return this.responseStream;
    }

    @Override
    public void close() {
        try {
            if (this.responseStream == null) {
                this.getBody();
            }
            StreamUtils.drain((InputStream)this.responseStream);
            this.responseStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

