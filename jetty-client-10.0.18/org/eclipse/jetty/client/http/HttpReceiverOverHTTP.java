/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.BadMessageException
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpParser
 *  org.eclipse.jetty.http.HttpParser$ResponseHandler
 *  org.eclipse.jetty.http.HttpStatus
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.io.RetainableByteBuffer
 *  org.eclipse.jetty.io.RetainableByteBufferPool
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpReceiver;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.http.HttpChannelOverHTTP;
import org.eclipse.jetty.client.http.HttpClientTransportOverHTTP;
import org.eclipse.jetty.client.http.HttpConnectionOverHTTP;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpReceiverOverHTTP
extends HttpReceiver
implements HttpParser.ResponseHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpReceiverOverHTTP.class);
    private final LongAdder inMessages = new LongAdder();
    private final HttpParser parser;
    private final RetainableByteBufferPool retainableByteBufferPool;
    private RetainableByteBuffer networkBuffer;
    private boolean shutdown;
    private boolean complete;
    private boolean unsolicited;
    private String method;
    private int status;

    public HttpReceiverOverHTTP(HttpChannelOverHTTP channel) {
        super(channel);
        HttpClient httpClient = channel.getHttpDestination().getHttpClient();
        this.parser = new HttpParser((HttpParser.ResponseHandler)this, httpClient.getMaxResponseHeadersSize(), httpClient.getHttpCompliance());
        HttpClientTransport transport = httpClient.getTransport();
        if (transport instanceof HttpClientTransportOverHTTP) {
            HttpClientTransportOverHTTP httpTransport = (HttpClientTransportOverHTTP)transport;
            this.parser.setHeaderCacheSize(httpTransport.getHeaderCacheSize());
            this.parser.setHeaderCacheCaseSensitive(httpTransport.isHeaderCacheCaseSensitive());
        }
        this.retainableByteBufferPool = httpClient.getByteBufferPool().asRetainableByteBufferPool();
    }

    @Override
    public HttpChannelOverHTTP getHttpChannel() {
        return (HttpChannelOverHTTP)super.getHttpChannel();
    }

    private HttpConnectionOverHTTP getHttpConnection() {
        return this.getHttpChannel().getHttpConnection();
    }

    protected ByteBuffer getResponseBuffer() {
        return this.networkBuffer == null ? null : this.networkBuffer.getBuffer();
    }

    @Override
    public void receive() {
        if (this.networkBuffer == null) {
            this.acquireNetworkBuffer();
        }
        this.process();
    }

    private void acquireNetworkBuffer() {
        this.networkBuffer = this.newNetworkBuffer();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Acquired {}", (Object)this.networkBuffer);
        }
    }

    private void reacquireNetworkBuffer() {
        RetainableByteBuffer currentBuffer = this.networkBuffer;
        if (currentBuffer == null) {
            throw new IllegalStateException();
        }
        if (currentBuffer.hasRemaining()) {
            throw new IllegalStateException();
        }
        currentBuffer.release();
        this.networkBuffer = this.newNetworkBuffer();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reacquired {} <- {}", (Object)currentBuffer, (Object)this.networkBuffer);
        }
    }

    private RetainableByteBuffer newNetworkBuffer() {
        HttpClient client = this.getHttpDestination().getHttpClient();
        boolean direct = client.isUseInputDirectByteBuffers();
        return this.retainableByteBufferPool.acquire(client.getResponseBufferSize(), direct);
    }

    private void releaseNetworkBuffer() {
        if (this.networkBuffer == null) {
            return;
        }
        this.networkBuffer.release();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Released {}", (Object)this.networkBuffer);
        }
        this.networkBuffer = null;
    }

    protected ByteBuffer onUpgradeFrom() {
        RetainableByteBuffer networkBuffer = this.networkBuffer;
        if (networkBuffer == null) {
            return null;
        }
        ByteBuffer upgradeBuffer = null;
        if (networkBuffer.hasRemaining()) {
            HttpClient client = this.getHttpDestination().getHttpClient();
            upgradeBuffer = BufferUtil.allocate((int)networkBuffer.remaining(), (boolean)client.isUseInputDirectByteBuffers());
            BufferUtil.clearToFill((ByteBuffer)upgradeBuffer);
            BufferUtil.put((ByteBuffer)networkBuffer.getBuffer(), (ByteBuffer)upgradeBuffer);
            BufferUtil.flipToFlush((ByteBuffer)upgradeBuffer, (int)0);
        }
        this.releaseNetworkBuffer();
        return upgradeBuffer;
    }

    private void process() {
        HttpConnectionOverHTTP connection = this.getHttpConnection();
        EndPoint endPoint = connection.getEndPoint();
        try {
            int read;
            while (true) {
                if (this.parse()) {
                    return;
                }
                if (connection.isClosed()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Closed {}", (Object)connection);
                    }
                    this.releaseNetworkBuffer();
                    return;
                }
                if (this.networkBuffer.isRetained()) {
                    this.reacquireNetworkBuffer();
                }
                read = endPoint.fill(this.networkBuffer.getBuffer());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Read {} bytes in {} from {}", new Object[]{read, this.networkBuffer, endPoint});
                }
                if (read <= 0) break;
                connection.addBytesIn(read);
            }
            if (read == 0) {
                assert (this.networkBuffer.isEmpty());
                this.releaseNetworkBuffer();
                this.fillInterested();
                return;
            }
            this.releaseNetworkBuffer();
            this.shutdown();
            return;
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error processing {}", (Object)endPoint, (Object)x);
            }
            this.releaseNetworkBuffer();
            this.failAndClose(x);
            return;
        }
    }

    private boolean parse() {
        do {
            boolean handle = this.parser.parseNext(this.networkBuffer.getBuffer());
            boolean failed = this.isFailed();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parse result={}, failed={}", (Object)handle, (Object)failed);
            }
            if (failed) {
                this.parser.close();
            }
            if (handle) {
                return !failed;
            }
            boolean complete = this.complete;
            this.complete = false;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parse complete={}, {} {}", new Object[]{complete, this.networkBuffer, this.parser});
            }
            if (!complete) continue;
            int status = this.status;
            this.status = 0;
            if (status == 101) {
                return true;
            }
            String method = this.method;
            this.method = null;
            if (this.getHttpChannel().isTunnel(method, status)) {
                return true;
            }
            if (this.networkBuffer.isEmpty()) {
                return false;
            }
            if (!HttpStatus.isInformational((int)status)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Discarding unexpected content after response {}: {}", (Object)status, (Object)this.networkBuffer);
                }
                this.networkBuffer.clear();
            }
            return false;
        } while (!this.networkBuffer.isEmpty());
        return false;
    }

    protected void fillInterested() {
        this.getHttpConnection().fillInterested();
    }

    private void shutdown() {
        this.shutdown = true;
        this.parser.atEOF();
        this.parser.parseNext(BufferUtil.EMPTY_BUFFER);
    }

    protected boolean isShutdown() {
        return this.shutdown;
    }

    public void startResponse(HttpVersion version, int status, String reason) {
        HttpExchange exchange = this.getHttpExchange();
        boolean bl = this.unsolicited = exchange == null;
        if (exchange == null) {
            return;
        }
        this.method = exchange.getRequest().getMethod();
        this.status = status;
        this.parser.setHeadResponse(HttpMethod.HEAD.is(this.method) || this.getHttpChannel().isTunnel(this.method, status));
        exchange.getResponse().version(version).status(status).reason(reason);
        this.responseBegin(exchange);
    }

    public void parsedHeader(HttpField field) {
        HttpExchange exchange = this.getHttpExchange();
        this.unsolicited |= exchange == null;
        if (this.unsolicited) {
            return;
        }
        this.responseHeader(exchange, field);
    }

    public boolean headerComplete() {
        HttpExchange exchange = this.getHttpExchange();
        this.unsolicited |= exchange == null;
        if (this.unsolicited) {
            return false;
        }
        exchange.getRequest().getConversation().setAttribute(EndPoint.class.getName(), this.getHttpConnection().getEndPoint());
        this.getHttpConnection().onResponseHeaders(exchange);
        return !this.responseHeaders(exchange);
    }

    public boolean content(ByteBuffer buffer) {
        HttpExchange exchange = this.getHttpExchange();
        this.unsolicited |= exchange == null;
        if (this.unsolicited) {
            return false;
        }
        RetainableByteBuffer networkBuffer = this.networkBuffer;
        networkBuffer.retain();
        return !this.responseContent(exchange, buffer, Callback.from(() -> ((RetainableByteBuffer)networkBuffer).release(), failure -> {
            networkBuffer.release();
            this.failAndClose((Throwable)failure);
        }));
    }

    public boolean contentComplete() {
        return false;
    }

    public void parsedTrailer(HttpField trailer) {
        HttpExchange exchange = this.getHttpExchange();
        this.unsolicited |= exchange == null;
        if (this.unsolicited) {
            return;
        }
        exchange.getResponse().trailer(trailer);
    }

    public boolean messageComplete() {
        boolean stopParsing;
        HttpExchange exchange = this.getHttpExchange();
        if (exchange == null || this.unsolicited) {
            this.getHttpConnection().close();
            return false;
        }
        int status = exchange.getResponse().getStatus();
        if (!HttpStatus.isInterim((int)status)) {
            this.inMessages.increment();
            this.complete = true;
        }
        boolean bl = stopParsing = !this.responseSuccess(exchange);
        if (status == 101) {
            stopParsing = true;
        }
        return stopParsing;
    }

    public void earlyEOF() {
        HttpExchange exchange = this.getHttpExchange();
        HttpConnectionOverHTTP connection = this.getHttpConnection();
        if (exchange == null || this.unsolicited) {
            connection.close();
        } else {
            this.failAndClose(new EOFException(String.valueOf(connection)));
        }
    }

    public void badMessage(BadMessageException failure) {
        HttpExchange exchange = this.getHttpExchange();
        if (exchange == null || this.unsolicited) {
            this.getHttpConnection().close();
        } else {
            HttpResponse response = exchange.getResponse();
            response.status(failure.getCode()).reason(failure.getReason());
            this.failAndClose(new HttpResponseException("HTTP protocol violation: bad response on " + this.getHttpConnection(), response, (Throwable)failure));
        }
    }

    @Override
    protected void reset() {
        super.reset();
        this.parser.reset();
    }

    private void failAndClose(Throwable failure) {
        if (this.responseFailure(failure)) {
            this.getHttpConnection().close(failure);
        }
    }

    long getMessagesIn() {
        return this.inMessages.longValue();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", super.toString(), this.parser);
    }
}

