/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.concurrent.FutureCallback
 *  org.apache.http.nio.client.HttpAsyncClient
 *  org.apache.http.nio.entity.NByteArrayEntity
 *  org.apache.http.protocol.HttpContext
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequest;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.FutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureCallbackRegistry;
import org.springframework.util.concurrent.SuccessCallback;

@Deprecated
final class HttpComponentsAsyncClientHttpRequest
extends AbstractBufferingAsyncClientHttpRequest {
    private final HttpAsyncClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    HttpComponentsAsyncClientHttpRequest(HttpAsyncClient client, HttpUriRequest request, HttpContext context) {
        this.httpClient = client;
        this.httpRequest = request;
        this.httpContext = context;
    }

    @Override
    public String getMethodValue() {
        return this.httpRequest.getMethod();
    }

    @Override
    public URI getURI() {
        return this.httpRequest.getURI();
    }

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
            NByteArrayEntity requestEntity = new NByteArrayEntity(bufferedOutput);
            entityEnclosingRequest.setEntity((HttpEntity)requestEntity);
        }
        HttpResponseFutureCallback callback = new HttpResponseFutureCallback(this.httpRequest);
        Future futureResponse = this.httpClient.execute(this.httpRequest, this.httpContext, (FutureCallback)callback);
        return new ClientHttpResponseFuture(futureResponse, callback);
    }

    private static class ClientHttpResponseFuture
    extends FutureAdapter<ClientHttpResponse, HttpResponse>
    implements ListenableFuture<ClientHttpResponse> {
        private final HttpResponseFutureCallback callback;

        public ClientHttpResponseFuture(Future<HttpResponse> response, HttpResponseFutureCallback callback) {
            super(response);
            this.callback = callback;
        }

        @Override
        protected ClientHttpResponse adapt(HttpResponse response) {
            return new HttpComponentsAsyncClientHttpResponse(response);
        }

        @Override
        public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callback.addCallback(callback);
        }

        @Override
        public void addCallback(SuccessCallback<? super ClientHttpResponse> successCallback, FailureCallback failureCallback) {
            this.callback.addSuccessCallback(successCallback);
            this.callback.addFailureCallback(failureCallback);
        }
    }

    private static class HttpResponseFutureCallback
    implements FutureCallback<HttpResponse> {
        private final HttpUriRequest request;
        private final ListenableFutureCallbackRegistry<ClientHttpResponse> callbacks = new ListenableFutureCallbackRegistry();

        public HttpResponseFutureCallback(HttpUriRequest request) {
            this.request = request;
        }

        public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callbacks.addCallback(callback);
        }

        public void addSuccessCallback(SuccessCallback<? super ClientHttpResponse> callback) {
            this.callbacks.addSuccessCallback(callback);
        }

        public void addFailureCallback(FailureCallback callback) {
            this.callbacks.addFailureCallback(callback);
        }

        public void completed(HttpResponse result) {
            this.callbacks.success(new HttpComponentsAsyncClientHttpResponse(result));
        }

        public void failed(Exception ex) {
            this.callbacks.failure(ex);
        }

        public void cancelled() {
            this.request.abort();
        }
    }
}

