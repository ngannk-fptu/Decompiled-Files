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
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionImpl;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;

public class ManagedNHttpClientConnectionFactory
implements NHttpConnectionFactory<ManagedNHttpClientConnection> {
    private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
    private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
    private final Log log = LogFactory.getLog(ManagedNHttpClientConnectionImpl.class);
    private static final AtomicLong COUNTER = new AtomicLong();
    public static final ManagedNHttpClientConnectionFactory INSTANCE = new ManagedNHttpClientConnectionFactory();
    private final ByteBufferAllocator allocator;
    private final NHttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final NHttpMessageParserFactory<HttpResponse> responseParserFactory;

    public ManagedNHttpClientConnectionFactory(NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, NHttpMessageParserFactory<HttpResponse> responseParserFactory, ByteBufferAllocator allocator) {
        this.requestWriterFactory = requestWriterFactory != null ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE;
        this.responseParserFactory = responseParserFactory != null ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE;
        this.allocator = allocator != null ? allocator : HeapByteBufferAllocator.INSTANCE;
    }

    public ManagedNHttpClientConnectionFactory() {
        this(null, null, null);
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
        ManagedNHttpClientConnectionImpl conn = new ManagedNHttpClientConnectionImpl(id, this.log, this.headerLog, this.wireLog, ioSession, config.getBufferSize(), config.getFragmentSizeHint(), this.allocator, charDecoder, charEncoder, config.getMessageConstraints(), null, null, this.requestWriterFactory, this.responseParserFactory);
        ioSession.setAttribute("http.connection", conn);
        return conn;
    }
}

