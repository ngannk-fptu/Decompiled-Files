/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.DefaultClientAsyncConnection;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionImpl;
import org.apache.http.message.BasicLineParser;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.conn.ClientAsyncConnection;
import org.apache.http.nio.conn.ClientAsyncConnectionFactory;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;

@Deprecated
public class DefaultClientAsyncConnectionFactory
implements ClientAsyncConnectionFactory,
NHttpConnectionFactory<ManagedNHttpClientConnection> {
    private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
    private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
    private final Log log = LogFactory.getLog(ManagedNHttpClientConnectionImpl.class);
    public static final DefaultClientAsyncConnectionFactory INSTANCE = new DefaultClientAsyncConnectionFactory(null, null);
    private static AtomicLong COUNTER = new AtomicLong();
    private final HttpResponseFactory responseFactory = this.createHttpResponseFactory();
    private final NHttpMessageParserFactory<HttpResponse> responseParserFactory;
    private final ByteBufferAllocator allocator;

    public DefaultClientAsyncConnectionFactory(NHttpMessageParserFactory<HttpResponse> responseParserFactory, ByteBufferAllocator allocator) {
        this.responseParserFactory = responseParserFactory != null ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE;
        this.allocator = allocator != null ? allocator : HeapByteBufferAllocator.INSTANCE;
    }

    public DefaultClientAsyncConnectionFactory() {
        this.responseParserFactory = new DefaultHttpResponseParserFactory(BasicLineParser.INSTANCE, this.responseFactory);
        this.allocator = this.createByteBufferAllocator();
    }

    @Override
    @Deprecated
    public ClientAsyncConnection create(String id, IOSession ioSession, HttpParams params) {
        return new DefaultClientAsyncConnection(id, ioSession, this.responseFactory, this.allocator, params);
    }

    @Deprecated
    protected ByteBufferAllocator createByteBufferAllocator() {
        return HeapByteBufferAllocator.INSTANCE;
    }

    @Deprecated
    protected HttpResponseFactory createHttpResponseFactory() {
        return DefaultHttpResponseFactory.INSTANCE;
    }

    @Override
    public ManagedNHttpClientConnection create(IOSession ioSession, ConnectionConfig config) {
        CodingErrorAction unmappableInputAction;
        String id = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());
        CharsetDecoder charDecoder = null;
        CharsetEncoder charEncoder = null;
        Charset charset = config.getCharset();
        CodingErrorAction malformedInputAction = config.getMalformedInputAction() != null ? config.getMalformedInputAction() : CodingErrorAction.REPORT;
        CodingErrorAction codingErrorAction = unmappableInputAction = config.getUnmappableInputAction() != null ? config.getUnmappableInputAction() : CodingErrorAction.REPORT;
        if (charset != null) {
            charDecoder = charset.newDecoder();
            charDecoder.onMalformedInput(malformedInputAction);
            charDecoder.onUnmappableCharacter(unmappableInputAction);
            charEncoder = charset.newEncoder();
            charEncoder.onMalformedInput(malformedInputAction);
            charEncoder.onUnmappableCharacter(unmappableInputAction);
        }
        ManagedNHttpClientConnectionImpl conn = new ManagedNHttpClientConnectionImpl(id, this.log, this.headerLog, this.wireLog, ioSession, config.getBufferSize(), config.getFragmentSizeHint(), this.allocator, charDecoder, charEncoder, config.getMessageConstraints(), null, null, null, this.responseParserFactory);
        ioSession.setAttribute("http.connection", conn);
        return conn;
    }
}

