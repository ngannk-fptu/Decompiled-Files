/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientHandler;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.HttpRequestExecutionHandler;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BufferingHttpClientHandler
implements NHttpClientHandler {
    private final AsyncNHttpClientHandler asyncHandler;

    public BufferingHttpClientHandler(HttpProcessor httpProcessor, HttpRequestExecutionHandler execHandler, ConnectionReuseStrategy connStrategy, ByteBufferAllocator allocator, HttpParams params) {
        this.asyncHandler = new AsyncNHttpClientHandler(httpProcessor, new ExecutionHandlerAdaptor(execHandler), connStrategy, allocator, params);
    }

    public BufferingHttpClientHandler(HttpProcessor httpProcessor, HttpRequestExecutionHandler execHandler, ConnectionReuseStrategy connStrategy, HttpParams params) {
        this(httpProcessor, execHandler, connStrategy, HeapByteBufferAllocator.INSTANCE, params);
    }

    public void setEventListener(EventListener eventListener) {
        this.asyncHandler.setEventListener(eventListener);
    }

    @Override
    public void connected(NHttpClientConnection conn, Object attachment) {
        this.asyncHandler.connected(conn, attachment);
    }

    @Override
    public void closed(NHttpClientConnection conn) {
        this.asyncHandler.closed(conn);
    }

    @Override
    public void requestReady(NHttpClientConnection conn) {
        this.asyncHandler.requestReady(conn);
    }

    @Override
    public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) {
        this.asyncHandler.inputReady(conn, decoder);
    }

    @Override
    public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) {
        this.asyncHandler.outputReady(conn, encoder);
    }

    @Override
    public void responseReceived(NHttpClientConnection conn) {
        this.asyncHandler.responseReceived(conn);
    }

    @Override
    public void exception(NHttpClientConnection conn, HttpException httpex) {
        this.asyncHandler.exception(conn, httpex);
    }

    @Override
    public void exception(NHttpClientConnection conn, IOException ioex) {
        this.asyncHandler.exception(conn, ioex);
    }

    @Override
    public void timeout(NHttpClientConnection conn) {
        this.asyncHandler.timeout(conn);
    }

    static class ExecutionHandlerAdaptor
    implements NHttpRequestExecutionHandler {
        private final HttpRequestExecutionHandler execHandler;

        public ExecutionHandlerAdaptor(HttpRequestExecutionHandler execHandler) {
            this.execHandler = execHandler;
        }

        @Override
        public void initalizeContext(HttpContext context, Object attachment) {
            this.execHandler.initalizeContext(context, attachment);
        }

        @Override
        public void finalizeContext(HttpContext context) {
            this.execHandler.finalizeContext(context);
        }

        @Override
        public HttpRequest submitRequest(HttpContext context) {
            return this.execHandler.submitRequest(context);
        }

        @Override
        public ConsumingNHttpEntity responseEntity(HttpResponse response, HttpContext context) throws IOException {
            return new BufferingNHttpEntity(response.getEntity(), HeapByteBufferAllocator.INSTANCE);
        }

        @Override
        public void handleResponse(HttpResponse response, HttpContext context) throws IOException {
            this.execHandler.handleResponse(response, context);
        }
    }
}

