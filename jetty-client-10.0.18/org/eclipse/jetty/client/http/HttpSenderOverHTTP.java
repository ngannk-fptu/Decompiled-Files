/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpGenerator
 *  org.eclipse.jetty.http.HttpGenerator$Result
 *  org.eclipse.jetty.http.HttpURI
 *  org.eclipse.jetty.http.MetaData$Request
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IteratingCallback
 *  org.eclipse.jetty.util.IteratingCallback$Action
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.nio.ByteBuffer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpRequestException;
import org.eclipse.jetty.client.HttpSender;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.http.HttpChannelOverHTTP;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSenderOverHTTP
extends HttpSender {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSenderOverHTTP.class);
    private final IteratingCallback headersCallback = new HeadersCallback();
    private final IteratingCallback contentCallback = new ContentCallback();
    private final HttpGenerator generator = new HttpGenerator();
    private HttpExchange exchange;
    private MetaData.Request metaData;
    private ByteBuffer contentBuffer;
    private boolean lastContent;
    private Callback callback;
    private boolean shutdown;

    public HttpSenderOverHTTP(HttpChannelOverHTTP channel) {
        super(channel);
    }

    @Override
    public HttpChannelOverHTTP getHttpChannel() {
        return (HttpChannelOverHTTP)super.getHttpChannel();
    }

    @Override
    protected void sendHeaders(HttpExchange exchange, ByteBuffer contentBuffer, boolean lastContent, Callback callback) {
        try {
            this.exchange = exchange;
            this.contentBuffer = contentBuffer;
            this.lastContent = lastContent;
            this.callback = callback;
            HttpRequest request = exchange.getRequest();
            Request.Content requestContent = request.getBody();
            long contentLength = requestContent == null ? -1L : requestContent.getLength();
            Object path = request.getPath();
            String query = request.getQuery();
            if (query != null) {
                path = (String)path + "?" + query;
            }
            this.metaData = new MetaData.Request(request.getMethod(), (HttpURI)HttpURI.from((String)path), request.getVersion(), request.getHeaders(), contentLength, request.getTrailers());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending headers with content {} last={} for {}", new Object[]{BufferUtil.toDetailString((ByteBuffer)contentBuffer), lastContent, exchange.getRequest()});
            }
            this.headersCallback.iterate();
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send headers on exchange {}", (Object)exchange, (Object)x);
            }
            callback.failed(x);
        }
    }

    @Override
    protected void sendContent(HttpExchange exchange, ByteBuffer contentBuffer, boolean lastContent, Callback callback) {
        try {
            this.exchange = exchange;
            this.contentBuffer = contentBuffer;
            this.lastContent = lastContent;
            this.callback = callback;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending content {} last={} for {}", new Object[]{BufferUtil.toDetailString((ByteBuffer)contentBuffer), lastContent, exchange.getRequest()});
            }
            this.contentCallback.iterate();
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send content on {}", (Object)exchange, (Object)x);
            }
            callback.failed(x);
        }
    }

    @Override
    protected void reset() {
        this.headersCallback.reset();
        this.contentCallback.reset();
        this.generator.reset();
        super.reset();
    }

    @Override
    protected void dispose() {
        this.generator.abort();
        super.dispose();
        this.shutdownOutput();
    }

    private void shutdownOutput() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request shutdown output {}", (Object)this.getHttpExchange().getRequest());
        }
        this.shutdown = true;
    }

    protected boolean isShutdown() {
        return this.shutdown;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", super.toString(), this.generator);
    }

    private class HeadersCallback
    extends IteratingCallback {
        private ByteBuffer headerBuffer;
        private ByteBuffer chunkBuffer;
        private boolean generated;

        private HeadersCallback() {
            super(false);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected IteratingCallback.Action process() throws Exception {
            HttpGenerator.Result result;
            HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
            ByteBufferPool byteBufferPool = httpClient.getByteBufferPool();
            boolean useDirectByteBuffers = httpClient.isUseOutputDirectByteBuffers();
            block10: while (true) {
                result = HttpSenderOverHTTP.this.generator.generateRequest(HttpSenderOverHTTP.this.metaData, this.headerBuffer, this.chunkBuffer, HttpSenderOverHTTP.this.contentBuffer, HttpSenderOverHTTP.this.lastContent);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Generated headers ({} bytes), chunk ({} bytes), content ({} bytes) - {}/{} for {}", new Object[]{this.headerBuffer == null ? -1 : this.headerBuffer.remaining(), this.chunkBuffer == null ? -1 : this.chunkBuffer.remaining(), HttpSenderOverHTTP.this.contentBuffer == null ? -1 : HttpSenderOverHTTP.this.contentBuffer.remaining(), result, HttpSenderOverHTTP.this.generator, HttpSenderOverHTTP.this.exchange.getRequest()});
                }
                switch (result) {
                    case NEED_HEADER: {
                        this.headerBuffer = byteBufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                        continue block10;
                    }
                    case HEADER_OVERFLOW: {
                        httpClient.getByteBufferPool().release(this.headerBuffer);
                        this.headerBuffer = null;
                        throw new IllegalArgumentException("Request header too large");
                    }
                    case NEED_CHUNK: {
                        this.chunkBuffer = byteBufferPool.acquire(12, useDirectByteBuffers);
                        continue block10;
                    }
                    case NEED_CHUNK_TRAILER: {
                        this.chunkBuffer = byteBufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                        continue block10;
                    }
                    case FLUSH: {
                        EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
                        if (this.headerBuffer == null) {
                            this.headerBuffer = BufferUtil.EMPTY_BUFFER;
                        }
                        if (this.chunkBuffer == null) {
                            this.chunkBuffer = BufferUtil.EMPTY_BUFFER;
                        }
                        if (HttpSenderOverHTTP.this.contentBuffer == null) {
                            HttpSenderOverHTTP.this.contentBuffer = BufferUtil.EMPTY_BUFFER;
                        }
                        long bytes = this.headerBuffer.remaining() + this.chunkBuffer.remaining() + HttpSenderOverHTTP.this.contentBuffer.remaining();
                        HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().addBytesOut(bytes);
                        endPoint.write((Callback)this, new ByteBuffer[]{this.headerBuffer, this.chunkBuffer, HttpSenderOverHTTP.this.contentBuffer});
                        this.generated = true;
                        return IteratingCallback.Action.SCHEDULED;
                    }
                    case SHUTDOWN_OUT: {
                        HttpSenderOverHTTP.this.shutdownOutput();
                        return IteratingCallback.Action.SUCCEEDED;
                    }
                    case CONTINUE: {
                        if (this.generated) return IteratingCallback.Action.SUCCEEDED;
                        continue block10;
                    }
                    case DONE: {
                        if (!this.generated) throw new HttpRequestException("Could not generate headers", HttpSenderOverHTTP.this.exchange.getRequest());
                        return IteratingCallback.Action.SUCCEEDED;
                    }
                }
                break;
            }
            throw new IllegalStateException(result.toString());
        }

        public void succeeded() {
            this.release();
            super.succeeded();
        }

        public void failed(Throwable x) {
            this.release();
            super.failed(x);
        }

        protected void onCompleteSuccess() {
            super.onCompleteSuccess();
            HttpSenderOverHTTP.this.callback.succeeded();
        }

        protected void onCompleteFailure(Throwable cause) {
            super.onCompleteFailure(cause);
            HttpSenderOverHTTP.this.callback.failed(cause);
        }

        private void release() {
            HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
            ByteBufferPool bufferPool = httpClient.getByteBufferPool();
            if (!BufferUtil.isTheEmptyBuffer((ByteBuffer)this.headerBuffer)) {
                bufferPool.release(this.headerBuffer);
            }
            this.headerBuffer = null;
            if (!BufferUtil.isTheEmptyBuffer((ByteBuffer)this.chunkBuffer)) {
                bufferPool.release(this.chunkBuffer);
            }
            this.chunkBuffer = null;
            HttpSenderOverHTTP.this.contentBuffer = null;
        }
    }

    private class ContentCallback
    extends IteratingCallback {
        private ByteBuffer chunkBuffer;

        public ContentCallback() {
            super(false);
        }

        protected IteratingCallback.Action process() throws Exception {
            HttpGenerator.Result result;
            HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
            ByteBufferPool bufferPool = httpClient.getByteBufferPool();
            boolean useDirectByteBuffers = httpClient.isUseOutputDirectByteBuffers();
            block8: while (true) {
                result = HttpSenderOverHTTP.this.generator.generateRequest(null, null, this.chunkBuffer, HttpSenderOverHTTP.this.contentBuffer, HttpSenderOverHTTP.this.lastContent);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Generated content ({} bytes, last={}) - {}/{}", new Object[]{HttpSenderOverHTTP.this.contentBuffer == null ? -1 : HttpSenderOverHTTP.this.contentBuffer.remaining(), HttpSenderOverHTTP.this.lastContent, result, HttpSenderOverHTTP.this.generator});
                }
                switch (result) {
                    case NEED_CHUNK: {
                        this.chunkBuffer = bufferPool.acquire(12, useDirectByteBuffers);
                        continue block8;
                    }
                    case NEED_CHUNK_TRAILER: {
                        this.chunkBuffer = bufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                        continue block8;
                    }
                    case FLUSH: {
                        EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
                        if (this.chunkBuffer != null) {
                            endPoint.write((Callback)this, new ByteBuffer[]{this.chunkBuffer, HttpSenderOverHTTP.this.contentBuffer});
                        } else {
                            endPoint.write((Callback)this, new ByteBuffer[]{HttpSenderOverHTTP.this.contentBuffer});
                        }
                        return IteratingCallback.Action.SCHEDULED;
                    }
                    case SHUTDOWN_OUT: {
                        HttpSenderOverHTTP.this.shutdownOutput();
                        continue block8;
                    }
                    case CONTINUE: {
                        continue block8;
                    }
                    case DONE: {
                        this.release();
                        HttpSenderOverHTTP.this.callback.succeeded();
                        return IteratingCallback.Action.IDLE;
                    }
                }
                break;
            }
            throw new IllegalStateException(result.toString());
        }

        protected void onCompleteFailure(Throwable cause) {
            this.release();
            HttpSenderOverHTTP.this.callback.failed(cause);
        }

        private void release() {
            HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
            ByteBufferPool bufferPool = httpClient.getByteBufferPool();
            bufferPool.release(this.chunkBuffer);
            this.chunkBuffer = null;
            HttpSenderOverHTTP.this.contentBuffer = null;
        }
    }
}

