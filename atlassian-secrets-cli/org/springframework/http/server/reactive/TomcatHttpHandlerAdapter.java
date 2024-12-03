/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.catalina.connector.CoyoteInputStream
 *  org.apache.catalina.connector.CoyoteOutputStream
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.util.Assert;

public class TomcatHttpHandlerAdapter
extends ServletHttpHandlerAdapter {
    public TomcatHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override
    protected ServerHttpRequest createRequest(HttpServletRequest request, AsyncContext asyncContext) throws IOException, URISyntaxException {
        Assert.notNull((Object)this.getServletPath(), "servletPath is not initialized.");
        return new TomcatServerHttpRequest(request, asyncContext, this.getServletPath(), this.getDataBufferFactory(), this.getBufferSize());
    }

    @Override
    protected ServerHttpResponse createResponse(HttpServletResponse response, AsyncContext cxt) throws IOException {
        return new TomcatServerHttpResponse(response, cxt, this.getDataBufferFactory(), this.getBufferSize());
    }

    private static final class TomcatServerHttpResponse
    extends ServletServerHttpResponse {
        public TomcatServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize) throws IOException {
            super(response, context, factory, bufferSize);
        }

        @Override
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse)this.getNativeResponse();
            ((CoyoteOutputStream)response.getOutputStream()).write(input);
            return len;
        }
    }

    private final class TomcatServerHttpRequest
    extends ServletServerHttpRequest {
        public TomcatServerHttpRequest(HttpServletRequest request, AsyncContext context, String servletPath, DataBufferFactory factory, int bufferSize) throws IOException, URISyntaxException {
            super(request, context, servletPath, factory, bufferSize);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected DataBuffer readFromInputStream() throws IOException {
            boolean release = true;
            int capacity = TomcatHttpHandlerAdapter.this.getBufferSize();
            DataBuffer dataBuffer = TomcatHttpHandlerAdapter.this.getDataBufferFactory().allocateBuffer(capacity);
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, capacity);
                ServletRequest request = (ServletRequest)this.getNativeRequest();
                int read = ((CoyoteInputStream)request.getInputStream()).read(byteBuffer);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("read:" + read);
                }
                if (read > 0) {
                    dataBuffer.writePosition(read);
                    release = false;
                    DataBuffer dataBuffer2 = dataBuffer;
                    return dataBuffer2;
                }
                if (read == -1) {
                    DataBuffer dataBuffer3 = EOF_BUFFER;
                    return dataBuffer3;
                }
                DataBuffer dataBuffer4 = null;
                return dataBuffer4;
            }
            finally {
                if (release) {
                    DataBufferUtils.release(dataBuffer);
                }
            }
        }
    }
}

