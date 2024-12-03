/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.ConnSupport;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.nio.reactor.ssl.SSLMode;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class SSLNHttpClientConnectionFactory
implements NHttpConnectionFactory<DefaultNHttpClientConnection> {
    public static final SSLNHttpClientConnectionFactory INSTANCE = new SSLNHttpClientConnectionFactory();
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageParserFactory<HttpResponse> responseParserFactory;
    private final NHttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final ByteBufferAllocator allocator;
    private final SSLContext sslContext;
    private final SSLSetupHandler sslHandler;
    private final ConnectionConfig cconfig;

    @Deprecated
    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        Args.notNull(responseFactory, "HTTP response factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        this.sslContext = sslContext != null ? sslContext : SSLContexts.createSystemDefault();
        this.sslHandler = sslHandler;
        this.allocator = allocator;
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.responseParserFactory = new DefaultHttpResponseParserFactory(null, responseFactory);
        this.requestWriterFactory = null;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    @Deprecated
    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        this(sslContext, sslHandler, DefaultHttpResponseFactory.INSTANCE, HeapByteBufferAllocator.INSTANCE, params);
    }

    @Deprecated
    public SSLNHttpClientConnectionFactory(HttpParams params) {
        this(null, null, params);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        this.sslContext = sslContext != null ? sslContext : SSLContexts.createSystemDefault();
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.responseParserFactory = responseParserFactory;
        this.requestWriterFactory = requestWriterFactory;
        this.allocator = allocator;
        this.cconfig = cconfig != null ? cconfig : ConnectionConfig.DEFAULT;
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ByteBufferAllocator allocator, ConnectionConfig cconfig) {
        this(sslContext, sslHandler, null, null, responseParserFactory, requestWriterFactory, allocator, cconfig);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, NHttpMessageParserFactory<HttpResponse> responseParserFactory, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, ConnectionConfig cconfig) {
        this(sslContext, sslHandler, null, null, responseParserFactory, requestWriterFactory, null, cconfig);
    }

    public SSLNHttpClientConnectionFactory(SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(sslContext, sslHandler, null, null, null, null, null, config);
    }

    public SSLNHttpClientConnectionFactory(ConnectionConfig config) {
        this(null, null, null, null, null, null, null, config);
    }

    public SSLNHttpClientConnectionFactory() {
        this(null, null, null, null, null, null);
    }

    @Deprecated
    protected DefaultNHttpClientConnection createConnection(IOSession session, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        return new DefaultNHttpClientConnection(session, responseFactory, allocator, params);
    }

    protected SSLIOSession createSSLIOSession(IOSession ioSession, SSLContext sslContext, SSLSetupHandler sslHandler) {
        Object attachment = ioSession.getAttribute("http.session.attachment");
        return new SSLIOSession(ioSession, SSLMode.CLIENT, attachment instanceof HttpHost ? (HttpHost)attachment : null, sslContext, sslHandler);
    }

    @Override
    public DefaultNHttpClientConnection createConnection(IOSession ioSession) {
        SSLIOSession sslioSession = this.createSSLIOSession(ioSession, this.sslContext, this.sslHandler);
        ioSession.setAttribute("http.session.ssl", sslioSession);
        return new DefaultNHttpClientConnection(sslioSession, this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), this.allocator, ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
    }
}

