/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.server.HttpOutput
 *  org.eclipse.jetty.server.Request
 *  org.eclipse.jetty.server.Response
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.JettyHeadersAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

public class JettyHttpHandlerAdapter
extends ServletHttpHandlerAdapter {
    private static final boolean jetty10Present = ClassUtils.isPresent((String)"org.eclipse.jetty.http.CookieCutter", (ClassLoader)JettyHttpHandlerAdapter.class.getClassLoader());

    public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        if (jetty10Present) {
            return super.createRequest(request, context);
        }
        Assert.state((this.getServletPath() != null ? 1 : 0) != 0, (String)"Servlet path is not initialized");
        return new JettyServerHttpRequest(request, context, this.getServletPath(), this.getDataBufferFactory(), this.getBufferSize());
    }

    @Override
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        if (jetty10Present) {
            return new BaseJettyServerHttpResponse(response, context, this.getDataBufferFactory(), this.getBufferSize(), request);
        }
        return new JettyServerHttpResponse(response, context, this.getDataBufferFactory(), this.getBufferSize(), request);
    }

    private static final class JettyServerHttpResponse
    extends BaseJettyServerHttpResponse {
        JettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(JettyServerHttpResponse.createHeaders(response), response, asyncContext, bufferFactory, bufferSize, request);
        }

        private static HttpHeaders createHeaders(HttpServletResponse servletResponse) {
            Response response = JettyServerHttpResponse.getResponse(servletResponse);
            HttpFields fields = response.getHttpFields();
            return new HttpHeaders(new JettyHeadersAdapter(fields));
        }

        private static Response getResponse(HttpServletResponse response) {
            if (response instanceof Response) {
                return (Response)response;
            }
            if (response instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;
                HttpServletResponse wrappedResponse = (HttpServletResponse)wrapper.getResponse();
                return JettyServerHttpResponse.getResponse(wrappedResponse);
            }
            throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.eclipse.jetty.server.Response");
        }

        @Override
        protected void applyHeaders() {
            long contentLength;
            Charset charset;
            HttpServletResponse response = (HttpServletResponse)this.getNativeResponse();
            MediaType contentType = null;
            try {
                contentType = this.getHeaders().getContentType();
            }
            catch (Exception ex) {
                String rawContentType = this.getHeaders().getFirst("Content-Type");
                response.setContentType(rawContentType);
            }
            if (response.getContentType() == null && contentType != null) {
                response.setContentType(contentType.toString());
            }
            Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
            if (response.getCharacterEncoding() == null && charset != null) {
                response.setCharacterEncoding(charset.name());
            }
            if ((contentLength = this.getHeaders().getContentLength()) != -1L) {
                response.setContentLengthLong(contentLength);
            }
        }
    }

    private static class BaseJettyServerHttpResponse
    extends ServletServerHttpResponse {
        BaseJettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(response, asyncContext, bufferFactory, bufferSize, request);
        }

        BaseJettyServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(headers, response, asyncContext, bufferFactory, bufferSize, request);
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

    private static final class JettyServerHttpRequest
    extends ServletServerHttpRequest {
        JettyServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
            super(JettyServerHttpRequest.createHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
        }

        private static MultiValueMap<String, String> createHeaders(HttpServletRequest servletRequest) {
            Request request = JettyServerHttpRequest.getRequest(servletRequest);
            HttpFields fields = request.getMetaData().getFields();
            return new JettyHeadersAdapter(fields);
        }

        private static Request getRequest(HttpServletRequest request) {
            if (request instanceof Request) {
                return (Request)request;
            }
            if (request instanceof HttpServletRequestWrapper) {
                HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper)request;
                HttpServletRequest wrappedRequest = (HttpServletRequest)wrapper.getRequest();
                return JettyServerHttpRequest.getRequest(wrappedRequest);
            }
            throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.eclipse.jetty.server.Request");
        }
    }
}

