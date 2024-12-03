/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.server.HttpOutput
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpOutput;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletServerHttpResponse;

public class JettyHttpHandlerAdapter
extends ServletHttpHandlerAdapter {
    public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override
    protected ServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context) throws IOException {
        return new JettyServerHttpResponse(response, context, this.getDataBufferFactory(), this.getBufferSize());
    }

    private static final class JettyServerHttpResponse
    extends ServletServerHttpResponse {
        public JettyServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize) throws IOException {
            super(response, context, factory, bufferSize);
        }

        @Override
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse)this.getNativeResponse();
            ((HttpOutput)response.getOutputStream()).write(input);
            return len;
        }
    }
}

