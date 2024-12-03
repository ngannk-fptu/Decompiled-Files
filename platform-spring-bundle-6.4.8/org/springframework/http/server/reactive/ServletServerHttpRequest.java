/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.AbstractListenerReadPublisher;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.DefaultSslInfo;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

class ServletServerHttpRequest
extends AbstractServerHttpRequest {
    static final DataBuffer EOF_BUFFER = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
    private final HttpServletRequest request;
    private final RequestBodyPublisher bodyPublisher;
    private final Object cookieLock = new Object();
    private final DataBufferFactory bufferFactory;
    private final byte[] buffer;
    private final AsyncListener asyncListener;

    public ServletServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        this(ServletServerHttpRequest.createDefaultHttpHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
    }

    public ServletServerHttpRequest(MultiValueMap<String, String> headers, HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        super(ServletServerHttpRequest.initUri(request), request.getContextPath() + servletPath, ServletServerHttpRequest.initHeaders(headers, request));
        Assert.notNull((Object)bufferFactory, "'bufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be greater than 0");
        this.request = request;
        this.bufferFactory = bufferFactory;
        this.buffer = new byte[bufferSize];
        this.asyncListener = new RequestAsyncListener();
        ServletInputStream inputStream = request.getInputStream();
        this.bodyPublisher = new RequestBodyPublisher(inputStream);
        this.bodyPublisher.registerReadListener();
    }

    private static MultiValueMap<String, String> createDefaultHttpHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headers = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            Enumeration values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                headers.add(name, (String)values.nextElement());
            }
        }
        return headers;
    }

    private static URI initUri(HttpServletRequest request) throws URISyntaxException {
        Assert.notNull((Object)request, "'request' must not be null");
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            url.append('?').append(query);
        }
        return new URI(url.toString());
    }

    private static MultiValueMap<String, String> initHeaders(MultiValueMap<String, String> headerValues, HttpServletRequest request) {
        int contentLength;
        String encoding;
        String requestContentType;
        HttpHeaders headers = null;
        MimeType contentType = null;
        if (!StringUtils.hasLength(headerValues.getFirst((String)"Content-Type")) && StringUtils.hasLength(requestContentType = request.getContentType())) {
            contentType = MediaType.parseMediaType(requestContentType);
            headers = new HttpHeaders(headerValues);
            headers.setContentType((MediaType)contentType);
        }
        if (contentType != null && contentType.getCharset() == null && StringUtils.hasLength(encoding = request.getCharacterEncoding())) {
            LinkedCaseInsensitiveMap<String> params = new LinkedCaseInsensitiveMap<String>();
            params.putAll(contentType.getParameters());
            params.put("charset", Charset.forName(encoding).toString());
            headers.setContentType(new MediaType((MediaType)contentType, params));
        }
        if (headerValues.getFirst((String)"Content-Type") == null && (contentLength = request.getContentLength()) != -1) {
            headers = headers != null ? headers : new HttpHeaders(headerValues);
            headers.setContentLength(contentLength);
        }
        return headers != null ? headers : headerValues;
    }

    @Override
    public String getMethodValue() {
        return this.request.getMethod();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        LinkedMultiValueMap<String, HttpCookie> httpCookies = new LinkedMultiValueMap<String, HttpCookie>();
        Cookie[] cookieArray = this.cookieLock;
        synchronized (this.cookieLock) {
            Cookie[] cookies = this.request.getCookies();
            // ** MonitorExit[var3_2] (shouldn't be in output)
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
                    httpCookies.add(name, httpCookie);
                }
            }
            return httpCookies;
        }
    }

    @Override
    @NonNull
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(this.request.getLocalAddr(), this.request.getLocalPort());
    }

    @Override
    @NonNull
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.request.getRemoteHost(), this.request.getRemotePort());
    }

    @Override
    @Nullable
    protected SslInfo initSslInfo() {
        X509Certificate[] certificates = this.getX509Certificates();
        return certificates != null ? new DefaultSslInfo(this.getSslSessionId(), certificates) : null;
    }

    @Nullable
    private String getSslSessionId() {
        return (String)this.request.getAttribute("javax.servlet.request.ssl_session_id");
    }

    @Nullable
    private X509Certificate[] getX509Certificates() {
        return (X509Certificate[])this.request.getAttribute("javax.servlet.request.X509Certificate");
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.from((Publisher)this.bodyPublisher);
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.request;
    }

    AsyncListener getAsyncListener() {
        return this.asyncListener;
    }

    DataBuffer readFromInputStream() throws IOException {
        int read = this.request.getInputStream().read(this.buffer);
        this.logBytesRead(read);
        if (read > 0) {
            DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
            dataBuffer.write(this.buffer, 0, read);
            return dataBuffer;
        }
        if (read == -1) {
            return EOF_BUFFER;
        }
        return AbstractListenerReadPublisher.EMPTY_BUFFER;
    }

    protected final void logBytesRead(int read) {
        Log rsReadLogger = AbstractListenerReadPublisher.rsReadLogger;
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace((Object)(this.getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : "")));
        }
    }

    private class RequestBodyPublisher
    extends AbstractListenerReadPublisher<DataBuffer> {
        private final ServletInputStream inputStream;

        public RequestBodyPublisher(ServletInputStream inputStream) {
            super(ServletServerHttpRequest.this.getLogPrefix());
            this.inputStream = inputStream;
        }

        public void registerReadListener() throws IOException {
            this.inputStream.setReadListener((ReadListener)new RequestBodyPublisherReadListener());
        }

        @Override
        protected void checkOnDataAvailable() {
            if (this.inputStream.isReady() && !this.inputStream.isFinished()) {
                this.onDataAvailable();
            }
        }

        @Override
        @Nullable
        protected DataBuffer read() throws IOException {
            if (this.inputStream.isReady()) {
                DataBuffer dataBuffer = ServletServerHttpRequest.this.readFromInputStream();
                if (dataBuffer == EOF_BUFFER) {
                    this.onAllDataRead();
                    dataBuffer = null;
                }
                return dataBuffer;
            }
            return null;
        }

        @Override
        protected void readingPaused() {
        }

        @Override
        protected void discardData() {
        }

        private class RequestBodyPublisherReadListener
        implements ReadListener {
            private RequestBodyPublisherReadListener() {
            }

            public void onDataAvailable() throws IOException {
                RequestBodyPublisher.this.onDataAvailable();
            }

            public void onAllDataRead() throws IOException {
                RequestBodyPublisher.this.onAllDataRead();
            }

            public void onError(Throwable throwable) {
                RequestBodyPublisher.this.onError(throwable);
            }
        }
    }

    private final class RequestAsyncListener
    implements AsyncListener {
        private RequestAsyncListener() {
        }

        public void onStartAsync(AsyncEvent event) {
        }

        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            ex = ex != null ? ex : new IllegalStateException("Async operation timeout.");
            ServletServerHttpRequest.this.bodyPublisher.onError(ex);
        }

        public void onError(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onError(event.getThrowable());
        }

        public void onComplete(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onAllDataRead();
        }
    }
}

