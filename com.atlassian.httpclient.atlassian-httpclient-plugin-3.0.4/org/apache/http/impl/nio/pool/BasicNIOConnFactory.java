/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.pool;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultNHttpClientConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpClientConnectionFactory;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BasicNIOConnFactory
implements NIOConnFactory<HttpHost, NHttpClientConnection> {
    private final NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory;
    private final NHttpConnectionFactory<? extends NHttpClientConnection> sslFactory;

    public BasicNIOConnFactory(NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory, NHttpConnectionFactory<? extends NHttpClientConnection> sslFactory) {
        Args.notNull(plainFactory, "Plain HTTP client connection factory");
        this.plainFactory = plainFactory;
        this.sslFactory = sslFactory;
    }

    public BasicNIOConnFactory(NHttpConnectionFactory<? extends NHttpClientConnection> plainFactory) {
        this(plainFactory, null);
    }

    @Deprecated
    public BasicNIOConnFactory(SSLContext sslContext, SSLSetupHandler sslHandler, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        this(new DefaultNHttpClientConnectionFactory(responseFactory, allocator, params), new SSLNHttpClientConnectionFactory(sslContext, sslHandler, responseFactory, allocator, params));
    }

    @Deprecated
    public BasicNIOConnFactory(SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        this(sslContext, sslHandler, DefaultHttpResponseFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    public BasicNIOConnFactory(HttpParams params) {
        this(null, null, params);
    }

    public BasicNIOConnFactory(SSLContext sslContext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig config) {
        this(new DefaultNHttpClientConnectionFactory(responseParserFactory, requestWriterFactory, allocator, config), new SSLNHttpClientConnectionFactory(sslContext, sslHandler, responseParserFactory, requestWriterFactory, allocator, config));
    }

    public BasicNIOConnFactory(SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(sslContext, sslHandler, null, null, null, config);
    }

    public BasicNIOConnFactory(ConnectionConfig config) {
        this(new DefaultNHttpClientConnectionFactory(config), null);
    }

    @Override
    public NHttpClientConnection create(HttpHost route, IOSession session) throws IOException {
        NHttpClientConnection conn;
        if (route.getSchemeName().equalsIgnoreCase("https")) {
            if (this.sslFactory == null) {
                throw new IOException("SSL not supported");
            }
            conn = this.sslFactory.createConnection(session);
        } else {
            conn = this.plainFactory.createConnection(session);
        }
        session.setAttribute("http.connection", conn);
        return conn;
    }
}

