/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.nio.ByteBuffer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;

class InternalState {
    private final long id;
    private final HttpAsyncRequestProducer requestProducer;
    private final HttpAsyncResponseConsumer<?> responseConsumer;
    private final HttpClientContext localContext;
    private HttpRequestWrapper mainRequest;
    private HttpResponse finalResponse;
    private ByteBuffer tmpbuf;
    private boolean requestContentProduced;
    private int execCount;
    private int redirectCount;
    private HttpUriRequest redirect;

    public InternalState(long id, HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<?> responseConsumer, HttpClientContext localContext) {
        this.id = id;
        this.requestProducer = requestProducer;
        this.responseConsumer = responseConsumer;
        this.localContext = localContext;
    }

    public long getId() {
        return this.id;
    }

    public HttpAsyncRequestProducer getRequestProducer() {
        return this.requestProducer;
    }

    public HttpAsyncResponseConsumer<?> getResponseConsumer() {
        return this.responseConsumer;
    }

    public HttpClientContext getLocalContext() {
        return this.localContext;
    }

    public HttpRequestWrapper getMainRequest() {
        return this.mainRequest;
    }

    public void setMainRequest(HttpRequestWrapper mainRequest) {
        this.mainRequest = mainRequest;
    }

    public HttpResponse getFinalResponse() {
        return this.finalResponse;
    }

    public void setFinalResponse(HttpResponse finalResponse) {
        this.finalResponse = finalResponse;
    }

    public ByteBuffer getTmpbuf() {
        if (this.tmpbuf == null) {
            this.tmpbuf = ByteBuffer.allocate(4096);
        }
        return this.tmpbuf;
    }

    public boolean isRequestContentProduced() {
        return this.requestContentProduced;
    }

    public void setRequestContentProduced() {
        this.requestContentProduced = true;
    }

    public int getExecCount() {
        return this.execCount;
    }

    public void incrementExecCount() {
        ++this.execCount;
    }

    public int getRedirectCount() {
        return this.redirectCount;
    }

    public void incrementRedirectCount() {
        ++this.redirectCount;
    }

    public HttpUriRequest getRedirect() {
        return this.redirect;
    }

    public void setRedirect(HttpUriRequest redirect) {
        this.redirect = redirect;
    }

    public String toString() {
        return Long.toString(this.id);
    }
}

