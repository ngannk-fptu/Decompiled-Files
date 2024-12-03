/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.AbstractListenerServerHttpResponse;
import org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor;
import org.springframework.http.server.reactive.AbstractListenerWriteProcessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class ServletServerHttpResponse
extends AbstractListenerServerHttpResponse {
    private final HttpServletResponse response;
    private final ServletOutputStream outputStream;
    private final int bufferSize;
    @Nullable
    private volatile ResponseBodyFlushProcessor bodyFlushProcessor;
    @Nullable
    private volatile ResponseBodyProcessor bodyProcessor;
    private volatile boolean flushOnNext;

    public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize) throws IOException {
        super(bufferFactory);
        Assert.notNull((Object)response, "HttpServletResponse must not be null");
        Assert.notNull((Object)bufferFactory, "DataBufferFactory must not be null");
        Assert.isTrue(bufferSize > 0, "Buffer size must be greater than 0");
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.bufferSize = bufferSize;
        asyncContext.addListener((AsyncListener)new ResponseAsyncListener());
        response.getOutputStream().setWriteListener((WriteListener)new ResponseBodyWriteListener());
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.response;
    }

    @Override
    protected void applyStatusCode() {
        Integer statusCode = this.getStatusCodeValue();
        if (statusCode != null) {
            this.response.setStatus(statusCode.intValue());
        }
    }

    @Override
    protected void applyHeaders() {
        Charset charset;
        this.getHeaders().forEach((headerName, headerValues) -> {
            for (String headerValue : headerValues) {
                this.response.addHeader(headerName, headerValue);
            }
        });
        MediaType contentType = this.getHeaders().getContentType();
        if (this.response.getContentType() == null && contentType != null) {
            this.response.setContentType(contentType.toString());
        }
        Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
        if (this.response.getCharacterEncoding() == null && charset != null) {
            this.response.setCharacterEncoding(charset.name());
        }
    }

    @Override
    protected void applyCookies() {
        for (String name : this.getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List)this.getCookies().get(name)) {
                Cookie cookie = new Cookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge((int)httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                this.response.addCookie(cookie);
            }
        }
    }

    @Override
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        ResponseBodyFlushProcessor processor;
        this.bodyFlushProcessor = processor = new ResponseBodyFlushProcessor();
        return processor;
    }

    protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
        int bytesRead;
        ServletOutputStream outputStream = this.outputStream;
        InputStream input = dataBuffer.asInputStream();
        int bytesWritten = 0;
        byte[] buffer = new byte[this.bufferSize];
        while (outputStream.isReady() && (bytesRead = input.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            bytesWritten += bytesRead;
        }
        return bytesWritten;
    }

    private void flush() throws IOException {
        ServletOutputStream outputStream = this.outputStream;
        if (outputStream.isReady()) {
            try {
                outputStream.flush();
                this.flushOnNext = false;
            }
            catch (IOException ex) {
                this.flushOnNext = true;
                throw ex;
            }
        } else {
            this.flushOnNext = true;
        }
    }

    private boolean isWritePossible() {
        return this.outputStream.isReady();
    }

    private class ResponseBodyProcessor
    extends AbstractListenerWriteProcessor<DataBuffer> {
        private ResponseBodyProcessor() {
        }

        @Override
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override
        protected boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        @Override
        protected boolean write(DataBuffer dataBuffer) throws IOException {
            if (ServletServerHttpResponse.this.flushOnNext) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("flush");
                }
                ServletServerHttpResponse.this.flush();
            }
            boolean ready = ServletServerHttpResponse.this.isWritePossible();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("write: " + dataBuffer + " ready: " + ready);
            }
            int remaining = dataBuffer.readableByteCount();
            if (ready && remaining > 0) {
                int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("written: " + written + " total: " + remaining);
                }
                if (written == remaining) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("releaseData: " + dataBuffer);
                    }
                    DataBufferUtils.release(dataBuffer);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void writingComplete() {
            ServletServerHttpResponse.this.bodyProcessor = null;
        }

        @Override
        protected void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }

    private class ResponseBodyFlushProcessor
    extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        private ResponseBodyFlushProcessor() {
        }

        @Override
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            ResponseBodyProcessor processor = new ResponseBodyProcessor();
            ServletServerHttpResponse.this.bodyProcessor = processor;
            return processor;
        }

        @Override
        protected void flush() throws IOException {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("flush");
            }
            ServletServerHttpResponse.this.flush();
        }

        @Override
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override
        protected boolean isFlushPending() {
            return ServletServerHttpResponse.this.flushOnNext;
        }
    }

    private class ResponseBodyWriteListener
    implements WriteListener {
        private ResponseBodyWriteListener() {
        }

        public void onWritePossible() throws IOException {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor != null) {
                processor.onWritePossible();
            } else {
                ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
                if (flushProcessor != null) {
                    flushProcessor.onFlushPossible();
                }
            }
        }

        public void onError(Throwable ex) {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor != null) {
                processor.cancel();
                processor.onError(ex);
            } else {
                ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
                if (flushProcessor != null) {
                    flushProcessor.cancel();
                    flushProcessor.onError(ex);
                }
            }
        }
    }

    private final class ResponseAsyncListener
    implements AsyncListener {
        private ResponseAsyncListener() {
        }

        public void onStartAsync(AsyncEvent event) {
        }

        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            ex = ex != null ? ex : new IllegalStateException("Async operation timeout.");
            this.handleError(ex);
        }

        public void onError(AsyncEvent event) {
            this.handleError(event.getThrowable());
        }

        void handleError(Throwable ex) {
            ResponseBodyProcessor processor;
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                flushProcessor.onError(ex);
            }
            if ((processor = ServletServerHttpResponse.this.bodyProcessor) != null) {
                processor.cancel();
                processor.onError(ex);
            }
        }

        public void onComplete(AsyncEvent event) {
            ResponseBodyProcessor processor;
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                flushProcessor.onComplete();
            }
            if ((processor = ServletServerHttpResponse.this.bodyProcessor) != null) {
                processor.cancel();
                processor.onComplete();
            }
        }
    }
}

