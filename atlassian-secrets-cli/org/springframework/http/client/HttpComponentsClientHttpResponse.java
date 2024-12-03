/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

final class HttpComponentsClientHttpResponse
extends AbstractClientHttpResponse {
    private final HttpResponse httpResponse;
    @Nullable
    private HttpHeaders headers;

    HttpComponentsClientHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return this.httpResponse.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            for (Header header : this.httpResponse.getAllHeaders()) {
                this.headers.add(header.getName(), header.getValue());
            }
        }
        return this.headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        HttpEntity entity = this.httpResponse.getEntity();
        return entity != null ? entity.getContent() : StreamUtils.emptyInput();
    }

    @Override
    public void close() {
        try {
            try {
                EntityUtils.consume(this.httpResponse.getEntity());
            }
            finally {
                if (this.httpResponse instanceof Closeable) {
                    ((Closeable)((Object)this.httpResponse)).close();
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

