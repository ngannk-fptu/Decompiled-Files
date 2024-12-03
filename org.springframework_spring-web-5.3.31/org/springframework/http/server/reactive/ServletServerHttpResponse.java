/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletResponse
 *  org.reactivestreams.Processor
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
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
import javax.servlet.http.HttpServletResponse;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.AbstractListenerServerHttpResponse;
import org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor;
import org.springframework.http.server.reactive.AbstractListenerWriteProcessor;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
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
    private final ServletServerHttpRequest request;
    private final ResponseAsyncListener asyncListener;

    public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        this(new HttpHeaders(), response, asyncContext, bufferFactory, bufferSize, request);
    }

    public ServletServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        super(bufferFactory, headers);
        Assert.notNull((Object)response, (String)"HttpServletResponse must not be null");
        Assert.notNull((Object)bufferFactory, (String)"DataBufferFactory must not be null");
        Assert.isTrue((bufferSize > 0 ? 1 : 0) != 0, (String)"Buffer size must be greater than 0");
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.bufferSize = bufferSize;
        this.request = request;
        this.asyncListener = new ResponseAsyncListener();
        response.getOutputStream().setWriteListener((WriteListener)new ResponseBodyWriteListener());
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.response;
    }

    @Override
    public HttpStatus getStatusCode() {
        HttpStatus status = super.getStatusCode();
        return status != null ? status : HttpStatus.resolve(this.response.getStatus());
    }

    @Override
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return status != null ? status.intValue() : this.response.getStatus();
    }

    @Override
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.response.setStatus(status.intValue());
        }
    }

    @Override
    protected void applyHeaders() {
        long contentLength;
        Charset charset;
        this.getHeaders().forEach((headerName, headerValues) -> {
            for (String headerValue : headerValues) {
                this.response.addHeader(headerName, headerValue);
            }
        });
        MediaType contentType = null;
        try {
            contentType = this.getHeaders().getContentType();
        }
        catch (Exception ex) {
            String rawContentType = this.getHeaders().getFirst("Content-Type");
            this.response.setContentType(rawContentType);
        }
        if (this.response.getContentType() == null && contentType != null) {
            this.response.setContentType(contentType.toString());
        }
        Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
        if (this.response.getCharacterEncoding() == null && charset != null) {
            this.response.setCharacterEncoding(charset.name());
        }
        if ((contentLength = this.getHeaders().getContentLength()) != -1L) {
            this.response.setContentLengthLong(contentLength);
        }
    }

    @Override
    protected void applyCookies() {
        for (List cookies : this.getCookies().values()) {
            for (ResponseCookie cookie : cookies) {
                this.response.addHeader("Set-Cookie", cookie.toString());
            }
        }
    }

    AsyncListener getAsyncListener() {
        return this.asyncListener;
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
        public ResponseBodyProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
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
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace((Object)(this.getLogPrefix() + "flushing"));
                }
                ServletServerHttpResponse.this.flush();
            }
            boolean ready = ServletServerHttpResponse.this.isWritePossible();
            int remaining = dataBuffer.readableByteCount();
            if (ready && remaining > 0) {
                int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace((Object)(this.getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes"));
                }
                if (written == remaining) {
                    DataBufferUtils.release((DataBuffer)dataBuffer);
                    return true;
                }
            } else if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace((Object)(this.getLogPrefix() + "ready: " + ready + ", remaining: " + remaining));
            }
            return false;
        }

        @Override
        protected void writingComplete() {
            ServletServerHttpResponse.this.bodyProcessor = null;
        }

        @Override
        protected void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
        }
    }

    private class ResponseBodyFlushProcessor
    extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        public ResponseBodyFlushProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
        }

        @Override
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            ResponseBodyProcessor processor = new ResponseBodyProcessor();
            ServletServerHttpResponse.this.bodyProcessor = processor;
            return processor;
        }

        @Override
        protected void flush() throws IOException {
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "flushing"));
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

        public void onWritePossible() {
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
            ServletServerHttpResponse.this.asyncListener.handleError(ex);
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

        public void handleError(Throwable ex) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onError(ex);
                }
                flushProcessor.onError(ex);
            }
        }

        public void onComplete(AsyncEvent event) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onComplete();
                }
                flushProcessor.onComplete();
            }
        }
    }
}

