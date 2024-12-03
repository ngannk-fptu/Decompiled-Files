/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.AsyncNHttpServiceHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpRequestHandlerResolver;
import org.apache.http.nio.protocol.SimpleNHttpRequestHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExpectationVerifier;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BufferingHttpServiceHandler
implements NHttpServiceHandler {
    private final AsyncNHttpServiceHandler asyncHandler;
    private HttpRequestHandlerResolver handlerResolver;

    public BufferingHttpServiceHandler(HttpProcessor httpProcessor, HttpResponseFactory responseFactory, ConnectionReuseStrategy connStrategy, ByteBufferAllocator allocator, HttpParams params) {
        this.asyncHandler = new AsyncNHttpServiceHandler(httpProcessor, responseFactory, connStrategy, allocator, params);
        this.asyncHandler.setHandlerResolver(new RequestHandlerResolverAdaptor());
    }

    public BufferingHttpServiceHandler(HttpProcessor httpProcessor, HttpResponseFactory responseFactory, ConnectionReuseStrategy connStrategy, HttpParams params) {
        this(httpProcessor, responseFactory, connStrategy, HeapByteBufferAllocator.INSTANCE, params);
    }

    public void setEventListener(EventListener eventListener) {
        this.asyncHandler.setEventListener(eventListener);
    }

    public void setExpectationVerifier(HttpExpectationVerifier expectationVerifier) {
        this.asyncHandler.setExpectationVerifier(expectationVerifier);
    }

    public void setHandlerResolver(HttpRequestHandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    @Override
    public void connected(NHttpServerConnection conn) {
        this.asyncHandler.connected(conn);
    }

    @Override
    public void closed(NHttpServerConnection conn) {
        this.asyncHandler.closed(conn);
    }

    @Override
    public void requestReceived(NHttpServerConnection conn) {
        this.asyncHandler.requestReceived(conn);
    }

    @Override
    public void inputReady(NHttpServerConnection conn, ContentDecoder decoder) {
        this.asyncHandler.inputReady(conn, decoder);
    }

    @Override
    public void responseReady(NHttpServerConnection conn) {
        this.asyncHandler.responseReady(conn);
    }

    @Override
    public void outputReady(NHttpServerConnection conn, ContentEncoder encoder) {
        this.asyncHandler.outputReady(conn, encoder);
    }

    @Override
    public void exception(NHttpServerConnection conn, HttpException httpex) {
        this.asyncHandler.exception(conn, httpex);
    }

    @Override
    public void exception(NHttpServerConnection conn, IOException ioex) {
        this.asyncHandler.exception(conn, ioex);
    }

    @Override
    public void timeout(NHttpServerConnection conn) {
        this.asyncHandler.timeout(conn);
    }

    static class RequestHandlerAdaptor
    extends SimpleNHttpRequestHandler {
        private final HttpRequestHandler requestHandler;

        public RequestHandlerAdaptor(HttpRequestHandler requestHandler) {
            this.requestHandler = requestHandler;
        }

        @Override
        public ConsumingNHttpEntity entityRequest(HttpEntityEnclosingRequest request, HttpContext context) throws HttpException, IOException {
            return new BufferingNHttpEntity(request.getEntity(), HeapByteBufferAllocator.INSTANCE);
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            this.requestHandler.handle(request, response, context);
        }
    }

    class RequestHandlerResolverAdaptor
    implements NHttpRequestHandlerResolver {
        RequestHandlerResolverAdaptor() {
        }

        @Override
        public NHttpRequestHandler lookup(String requestURI) {
            HttpRequestHandler handler = BufferingHttpServiceHandler.this.handlerResolver.lookup(requestURI);
            if (handler != null) {
                return new RequestHandlerAdaptor(handler);
            }
            return null;
        }
    }
}

