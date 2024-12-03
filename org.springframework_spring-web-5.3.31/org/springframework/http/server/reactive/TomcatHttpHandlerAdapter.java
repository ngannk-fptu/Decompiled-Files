/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.catalina.connector.CoyoteInputStream
 *  org.apache.catalina.connector.CoyoteOutputStream
 *  org.apache.catalina.connector.Request
 *  org.apache.catalina.connector.RequestFacade
 *  org.apache.catalina.connector.Response
 *  org.apache.catalina.connector.ResponseFacade
 *  org.apache.coyote.Request
 *  org.apache.coyote.Response
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.coyote.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.AbstractListenerReadPublisher;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.http.server.reactive.TomcatHeadersAdapter;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

public class TomcatHttpHandlerAdapter
extends ServletHttpHandlerAdapter {
    public TomcatHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext asyncContext) throws IOException, URISyntaxException {
        Assert.state((this.getServletPath() != null ? 1 : 0) != 0, (String)"Servlet path is not initialized");
        return new TomcatServerHttpRequest(request, asyncContext, this.getServletPath(), this.getDataBufferFactory(), this.getBufferSize());
    }

    @Override
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext asyncContext, ServletServerHttpRequest request) throws IOException {
        return new TomcatServerHttpResponse(response, asyncContext, this.getDataBufferFactory(), this.getBufferSize(), request);
    }

    private static final class TomcatServerHttpResponse
    extends ServletServerHttpResponse {
        private static final Field COYOTE_RESPONSE_FIELD;

        TomcatServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(TomcatServerHttpResponse.createTomcatHttpHeaders(response), response, context, factory, bufferSize, request);
        }

        private static HttpHeaders createTomcatHttpHeaders(HttpServletResponse response) {
            ResponseFacade responseFacade = TomcatServerHttpResponse.getResponseFacade(response);
            org.apache.catalina.connector.Response connectorResponse = (org.apache.catalina.connector.Response)ReflectionUtils.getField((Field)COYOTE_RESPONSE_FIELD, (Object)responseFacade);
            Assert.state((connectorResponse != null ? 1 : 0) != 0, (String)"No Tomcat connector response");
            Response tomcatResponse = connectorResponse.getCoyoteResponse();
            TomcatHeadersAdapter headers = new TomcatHeadersAdapter(tomcatResponse.getMimeHeaders());
            return new HttpHeaders(headers);
        }

        private static ResponseFacade getResponseFacade(HttpServletResponse response) {
            if (response instanceof ResponseFacade) {
                return (ResponseFacade)response;
            }
            if (response instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;
                HttpServletResponse wrappedResponse = (HttpServletResponse)wrapper.getResponse();
                return TomcatServerHttpResponse.getResponseFacade(wrappedResponse);
            }
            throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.apache.catalina.connector.ResponseFacade");
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
            this.getHeaders().remove("Content-Type");
            Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
            if (response.getCharacterEncoding() == null && charset != null) {
                response.setCharacterEncoding(charset.name());
            }
            if ((contentLength = this.getHeaders().getContentLength()) != -1L) {
                response.setContentLengthLong(contentLength);
            }
            this.getHeaders().remove("Content-Length");
        }

        @Override
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse)this.getNativeResponse();
            ((CoyoteOutputStream)response.getOutputStream()).write(input);
            return len;
        }

        static {
            Field field = ReflectionUtils.findField(ResponseFacade.class, (String)"response");
            Assert.state((field != null ? 1 : 0) != 0, (String)"Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible((Field)field);
            COYOTE_RESPONSE_FIELD = field;
        }
    }

    private static final class TomcatServerHttpRequest
    extends ServletServerHttpRequest {
        private static final Field COYOTE_REQUEST_FIELD;
        private final int bufferSize;
        private final DataBufferFactory factory;

        TomcatServerHttpRequest(HttpServletRequest request, AsyncContext context, String servletPath, DataBufferFactory factory, int bufferSize) throws IOException, URISyntaxException {
            super(TomcatServerHttpRequest.createTomcatHttpHeaders(request), request, context, servletPath, factory, bufferSize);
            this.factory = factory;
            this.bufferSize = bufferSize;
        }

        private static MultiValueMap<String, String> createTomcatHttpHeaders(HttpServletRequest request) {
            RequestFacade requestFacade = TomcatServerHttpRequest.getRequestFacade(request);
            Request connectorRequest = (Request)ReflectionUtils.getField((Field)COYOTE_REQUEST_FIELD, (Object)requestFacade);
            Assert.state((connectorRequest != null ? 1 : 0) != 0, (String)"No Tomcat connector request");
            org.apache.coyote.Request tomcatRequest = connectorRequest.getCoyoteRequest();
            return new TomcatHeadersAdapter(tomcatRequest.getMimeHeaders());
        }

        private static RequestFacade getRequestFacade(HttpServletRequest request) {
            if (request instanceof RequestFacade) {
                return (RequestFacade)request;
            }
            if (request instanceof HttpServletRequestWrapper) {
                HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper)request;
                HttpServletRequest wrappedRequest = (HttpServletRequest)wrapper.getRequest();
                return TomcatServerHttpRequest.getRequestFacade(wrappedRequest);
            }
            throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.apache.catalina.connector.RequestFacade");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected DataBuffer readFromInputStream() throws IOException {
            ServletInputStream inputStream = ((ServletRequest)this.getNativeRequest()).getInputStream();
            if (!(inputStream instanceof CoyoteInputStream)) {
                return super.readFromInputStream();
            }
            boolean release = true;
            int capacity = this.bufferSize;
            DataBuffer dataBuffer = this.factory.allocateBuffer(capacity);
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, capacity);
                int read = ((CoyoteInputStream)inputStream).read(byteBuffer);
                this.logBytesRead(read);
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
                DataBuffer dataBuffer4 = AbstractListenerReadPublisher.EMPTY_BUFFER;
                return dataBuffer4;
            }
            finally {
                if (release) {
                    DataBufferUtils.release((DataBuffer)dataBuffer);
                }
            }
        }

        static {
            Field field = ReflectionUtils.findField(RequestFacade.class, (String)"request");
            Assert.state((field != null ? 1 : 0) != 0, (String)"Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible((Field)field);
            COYOTE_REQUEST_FIELD = field;
        }
    }
}

