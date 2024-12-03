/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Response
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@Deprecated
class OkHttp3AsyncClientHttpRequest
extends AbstractBufferingAsyncClientHttpRequest {
    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public OkHttp3AsyncClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getMethodValue() {
        return this.method.name();
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] content) throws IOException {
        Request request = OkHttp3ClientHttpRequestFactory.buildRequest(headers, content, this.uri, this.method);
        return new OkHttpListenableFuture(this.client.newCall(request));
    }

    private static class OkHttpListenableFuture
    extends SettableListenableFuture<ClientHttpResponse> {
        private final Call call;

        public OkHttpListenableFuture(Call call) {
            this.call = call;
            this.call.enqueue(new Callback(){

                public void onResponse(Call call, Response response) {
                    this.set(new OkHttp3ClientHttpResponse(response));
                }

                public void onFailure(Call call, IOException ex) {
                    this.setException(ex);
                }
            });
        }

        @Override
        protected void interruptTask() {
            this.call.cancel();
        }
    }
}

