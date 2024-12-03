/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

class OkHttp3ClientHttpResponse
extends AbstractClientHttpResponse {
    private final Response response;
    @Nullable
    private volatile HttpHeaders headers;

    public OkHttp3ClientHttpResponse(Response response) {
        Assert.notNull((Object)response, "Response must not be null");
        this.response = response;
    }

    @Override
    public int getRawStatusCode() {
        return this.response.code();
    }

    @Override
    public String getStatusText() {
        return this.response.message();
    }

    @Override
    public InputStream getBody() throws IOException {
        ResponseBody body2 = this.response.body();
        return body2 != null ? body2.byteStream() : StreamUtils.emptyInput();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = this.headers;
        if (headers == null) {
            headers = new HttpHeaders();
            for (String headerName : this.response.headers().names()) {
                for (String headerValue : this.response.headers(headerName)) {
                    headers.add(headerName, headerValue);
                }
            }
            this.headers = headers;
        }
        return headers;
    }

    @Override
    public void close() {
        ResponseBody body2 = this.response.body();
        if (body2 != null) {
            body2.close();
        }
    }
}

