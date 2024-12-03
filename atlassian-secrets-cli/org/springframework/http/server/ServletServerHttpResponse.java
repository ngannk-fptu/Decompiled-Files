/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class ServletServerHttpResponse
implements ServerHttpResponse {
    private final HttpServletResponse servletResponse;
    private final HttpHeaders headers;
    private boolean headersWritten = false;
    private boolean bodyUsed = false;

    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull((Object)servletResponse, "HttpServletResponse must not be null");
        this.servletResponse = servletResponse;
        this.headers = new ServletResponseHttpHeaders();
    }

    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }

    @Override
    public void setStatusCode(HttpStatus status) {
        Assert.notNull((Object)status, "HttpStatus must not be null");
        this.servletResponse.setStatus(status.value());
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override
    public OutputStream getBody() throws IOException {
        this.bodyUsed = true;
        this.writeHeaders();
        return this.servletResponse.getOutputStream();
    }

    @Override
    public void flush() throws IOException {
        this.writeHeaders();
        if (this.bodyUsed) {
            this.servletResponse.flushBuffer();
        }
    }

    @Override
    public void close() {
        this.writeHeaders();
    }

    private void writeHeaders() {
        if (!this.headersWritten) {
            this.getHeaders().forEach((headerName, headerValues) -> {
                for (String headerValue : headerValues) {
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            });
            if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
                this.servletResponse.setContentType(this.headers.getContentType().toString());
            }
            if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
                this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
            }
            this.headersWritten = true;
        }
    }

    private class ServletResponseHttpHeaders
    extends HttpHeaders {
        private static final long serialVersionUID = 3410708522401046302L;

        private ServletResponseHttpHeaders() {
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(key) || this.get(key) != null;
        }

        @Override
        @Nullable
        public String getFirst(String headerName) {
            String value = ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
            if (value != null) {
                return value;
            }
            return super.getFirst(headerName);
        }

        @Override
        public List<String> get(Object key) {
            Assert.isInstanceOf(String.class, key, "Key must be a String-based header name");
            Collection values1 = ServletServerHttpResponse.this.servletResponse.getHeaders((String)key);
            boolean isEmpty1 = CollectionUtils.isEmpty(values1);
            Object values2 = super.get(key);
            boolean isEmpty2 = CollectionUtils.isEmpty(values2);
            if (isEmpty1 && isEmpty2) {
                return null;
            }
            ArrayList<String> values = new ArrayList<String>();
            if (!isEmpty1) {
                values.addAll(values1);
            }
            if (!isEmpty2) {
                values.addAll((Collection<String>)values2);
            }
            return values;
        }
    }
}

