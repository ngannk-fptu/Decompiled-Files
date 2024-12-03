/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.nio.SessionHttpContext;
import org.apache.http.impl.nio.codecs.ChunkDecoder;
import org.apache.http.impl.nio.codecs.ChunkEncoder;
import org.apache.http.impl.nio.codecs.IdentityDecoder;
import org.apache.http.impl.nio.codecs.IdentityEncoder;
import org.apache.http.impl.nio.codecs.LengthDelimitedDecoder;
import org.apache.http.impl.nio.codecs.LengthDelimitedEncoder;
import org.apache.http.impl.nio.reactor.SessionInputBufferImpl;
import org.apache.http.impl.nio.reactor.SessionOutputBufferImpl;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.reactor.SessionOutputBuffer;
import org.apache.http.nio.reactor.SocketAccessor;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.NetUtils;

public class NHttpConnectionBase
implements NHttpConnection,
HttpInetConnection,
SessionBufferStatus,
SocketAccessor {
    protected final ContentLengthStrategy incomingContentStrategy;
    protected final ContentLengthStrategy outgoingContentStrategy;
    protected final SessionInputBufferImpl inbuf;
    protected final SessionOutputBufferImpl outbuf;
    private final int fragmentSizeHint;
    private final MessageConstraints constraints;
    protected final HttpTransportMetricsImpl inTransportMetrics;
    protected final HttpTransportMetricsImpl outTransportMetrics;
    protected final HttpConnectionMetricsImpl connMetrics;
    protected HttpContext context;
    protected IOSession session;
    protected SocketAddress remote;
    protected volatile ContentDecoder contentDecoder;
    protected volatile boolean hasBufferedInput;
    protected volatile ContentEncoder contentEncoder;
    protected volatile boolean hasBufferedOutput;
    protected volatile HttpRequest request;
    protected volatile HttpResponse response;
    protected volatile int status;

    @Deprecated
    public NHttpConnectionBase(IOSession session, ByteBufferAllocator allocator, HttpParams params) {
        int lineBufferSize;
        Args.notNull(session, "I/O session");
        Args.notNull(params, "HTTP params");
        int bufferSize = params.getIntParameter("http.socket.buffer-size", -1);
        if (bufferSize <= 0) {
            bufferSize = 4096;
        }
        if ((lineBufferSize = bufferSize) > 512) {
            lineBufferSize = 512;
        }
        CharsetDecoder decoder = null;
        CharsetEncoder encoder = null;
        Charset charset = CharsetUtils.lookup((String)params.getParameter("http.protocol.element-charset"));
        if (charset != null) {
            charset = Consts.ASCII;
            decoder = charset.newDecoder();
            encoder = charset.newEncoder();
            CodingErrorAction malformedCharAction = (CodingErrorAction)params.getParameter("http.malformed.input.action");
            CodingErrorAction unmappableCharAction = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
            decoder.onMalformedInput(malformedCharAction).onUnmappableCharacter(unmappableCharAction);
            encoder.onMalformedInput(malformedCharAction).onUnmappableCharacter(unmappableCharAction);
        }
        this.inbuf = new SessionInputBufferImpl(bufferSize, lineBufferSize, decoder, allocator);
        this.outbuf = new SessionOutputBufferImpl(bufferSize, lineBufferSize, encoder, allocator);
        this.fragmentSizeHint = bufferSize;
        this.constraints = MessageConstraints.DEFAULT;
        this.incomingContentStrategy = this.createIncomingContentStrategy();
        this.outgoingContentStrategy = this.createOutgoingContentStrategy();
        this.inTransportMetrics = this.createTransportMetrics();
        this.outTransportMetrics = this.createTransportMetrics();
        this.connMetrics = this.createConnectionMetrics(this.inTransportMetrics, this.outTransportMetrics);
        this.setSession(session);
        this.status = 0;
    }

    protected NHttpConnectionBase(IOSession session, int bufferSize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
        Args.notNull(session, "I/O session");
        Args.positive(bufferSize, "Buffer size");
        int lineBufferSize = bufferSize;
        if (lineBufferSize > 512) {
            lineBufferSize = 512;
        }
        this.inbuf = new SessionInputBufferImpl(bufferSize, lineBufferSize, charDecoder, allocator);
        this.outbuf = new SessionOutputBufferImpl(bufferSize, lineBufferSize, charEncoder, allocator);
        this.fragmentSizeHint = fragmentSizeHint >= 0 ? fragmentSizeHint : bufferSize;
        this.inTransportMetrics = new HttpTransportMetricsImpl();
        this.outTransportMetrics = new HttpTransportMetricsImpl();
        this.connMetrics = new HttpConnectionMetricsImpl(this.inTransportMetrics, this.outTransportMetrics);
        this.constraints = constraints != null ? constraints : MessageConstraints.DEFAULT;
        this.incomingContentStrategy = incomingContentStrategy != null ? incomingContentStrategy : LaxContentLengthStrategy.INSTANCE;
        this.outgoingContentStrategy = outgoingContentStrategy != null ? outgoingContentStrategy : StrictContentLengthStrategy.INSTANCE;
        this.setSession(session);
        this.status = 0;
    }

    protected NHttpConnectionBase(IOSession session, int bufferSize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder charDecoder, CharsetEncoder charEncoder, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
        this(session, bufferSize, fragmentSizeHint, allocator, charDecoder, charEncoder, null, incomingContentStrategy, outgoingContentStrategy);
    }

    private void setSession(IOSession session) {
        this.session = session;
        this.context = new SessionHttpContext(this.session);
        this.session.setBufferStatus(this);
        this.remote = this.session.getRemoteAddress();
    }

    protected void bind(IOSession session) {
        Args.notNull(session, "I/O session");
        this.setSession(session);
    }

    @Deprecated
    protected ContentLengthStrategy createIncomingContentStrategy() {
        return new LaxContentLengthStrategy();
    }

    @Deprecated
    protected ContentLengthStrategy createOutgoingContentStrategy() {
        return new StrictContentLengthStrategy();
    }

    @Deprecated
    protected HttpTransportMetricsImpl createTransportMetrics() {
        return new HttpTransportMetricsImpl();
    }

    @Deprecated
    protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public HttpContext getContext() {
        return this.context;
    }

    @Override
    public HttpRequest getHttpRequest() {
        return this.request;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return this.response;
    }

    @Override
    public void requestInput() {
        this.session.setEvent(1);
    }

    @Override
    public void requestOutput() {
        this.session.setEvent(4);
    }

    @Override
    public void suspendInput() {
        this.session.clearEvent(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void suspendOutput() {
        IOSession iOSession = this.session;
        synchronized (iOSession) {
            if (!this.outbuf.hasData()) {
                this.session.clearEvent(4);
            }
        }
    }

    protected HttpEntity prepareDecoder(HttpMessage message) throws HttpException {
        Header contentEncodingHeader;
        BasicHttpEntity entity = new BasicHttpEntity();
        long len = this.incomingContentStrategy.determineLength(message);
        this.contentDecoder = this.createContentDecoder(len, this.session.channel(), this.inbuf, this.inTransportMetrics);
        if (len == -2L) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
        } else if (len == -1L) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
        } else {
            entity.setChunked(false);
            entity.setContentLength(len);
        }
        Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        if ((contentEncodingHeader = message.getFirstHeader("Content-Encoding")) != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }

    protected ContentDecoder createContentDecoder(long len, ReadableByteChannel channel, SessionInputBuffer buffer, HttpTransportMetricsImpl metrics) {
        if (len == -2L) {
            return new ChunkDecoder(channel, buffer, this.constraints, metrics);
        }
        if (len == -1L) {
            return new IdentityDecoder(channel, buffer, metrics);
        }
        return new LengthDelimitedDecoder(channel, buffer, metrics, len);
    }

    protected void prepareEncoder(HttpMessage message) throws HttpException {
        long len = this.outgoingContentStrategy.determineLength(message);
        this.contentEncoder = this.createContentEncoder(len, this.session.channel(), this.outbuf, this.outTransportMetrics);
    }

    protected ContentEncoder createContentEncoder(long len, WritableByteChannel channel, SessionOutputBuffer buffer, HttpTransportMetricsImpl metrics) {
        if (len == -2L) {
            return new ChunkEncoder(channel, buffer, metrics, this.fragmentSizeHint);
        }
        if (len == -1L) {
            return new IdentityEncoder(channel, buffer, metrics, this.fragmentSizeHint);
        }
        return new LengthDelimitedEncoder(channel, buffer, metrics, len, this.fragmentSizeHint);
    }

    @Override
    public boolean hasBufferedInput() {
        return this.hasBufferedInput;
    }

    @Override
    public boolean hasBufferedOutput() {
        return this.hasBufferedOutput;
    }

    protected void assertNotClosed() throws ConnectionClosedException {
        if (this.status != 0) {
            throw new ConnectionClosedException();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.status != 0) {
            return;
        }
        this.status = 1;
        this.inbuf.clear();
        this.hasBufferedInput = false;
        if (this.outbuf.hasData()) {
            this.session.setEvent(4);
        } else {
            this.session.close();
            this.status = 2;
        }
    }

    @Override
    public boolean isOpen() {
        return this.status == 0 && !this.session.isClosed();
    }

    @Override
    public boolean isStale() {
        return this.session.isClosed();
    }

    @Override
    public InetAddress getLocalAddress() {
        SocketAddress address = this.session.getLocalAddress();
        return address instanceof InetSocketAddress ? ((InetSocketAddress)address).getAddress() : null;
    }

    @Override
    public int getLocalPort() {
        SocketAddress address = this.session.getLocalAddress();
        return address instanceof InetSocketAddress ? ((InetSocketAddress)address).getPort() : -1;
    }

    @Override
    public InetAddress getRemoteAddress() {
        SocketAddress address = this.session.getRemoteAddress();
        return address instanceof InetSocketAddress ? ((InetSocketAddress)address).getAddress() : null;
    }

    @Override
    public int getRemotePort() {
        SocketAddress address = this.session.getRemoteAddress();
        return address instanceof InetSocketAddress ? ((InetSocketAddress)address).getPort() : -1;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.session.setSocketTimeout(timeout);
    }

    @Override
    public int getSocketTimeout() {
        return this.session.getSocketTimeout();
    }

    @Override
    public void shutdown() throws IOException {
        this.status = 2;
        this.session.shutdown();
    }

    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.connMetrics;
    }

    public String toString() {
        SocketAddress remoteAddress = this.session.getRemoteAddress();
        SocketAddress localAddress = this.session.getLocalAddress();
        if (remoteAddress != null && localAddress != null) {
            StringBuilder buffer = new StringBuilder();
            NetUtils.formatAddress(buffer, localAddress);
            buffer.append("<->");
            NetUtils.formatAddress(buffer, remoteAddress);
            return buffer.toString();
        }
        return "[Not bound]";
    }

    @Override
    public Socket getSocket() {
        return this.session instanceof SocketAccessor ? ((SocketAccessor)((Object)this.session)).getSocket() : null;
    }
}

