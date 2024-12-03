/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.Part
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.GenericHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.util.UriBuilder
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.servlet.function;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.servlet.function.DefaultServerRequest;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

class DefaultServerRequestBuilder
implements ServerRequest.Builder {
    private final HttpServletRequest servletRequest;
    private final List<HttpMessageConverter<?>> messageConverters;
    private String methodName;
    private URI uri;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap();
    @Nullable
    private InetSocketAddress remoteAddress;
    private byte[] body = new byte[0];

    public DefaultServerRequestBuilder(ServerRequest other) {
        Assert.notNull((Object)other, (String)"ServerRequest must not be null");
        this.servletRequest = other.servletRequest();
        this.messageConverters = new ArrayList(other.messageConverters());
        this.methodName = other.methodName();
        this.uri = other.uri();
        this.headers(headers -> headers.addAll((MultiValueMap)other.headers().asHttpHeaders()));
        this.cookies(cookies -> cookies.addAll(other.cookies()));
        this.attributes(attributes -> attributes.putAll(other.attributes()));
        this.params(params -> params.addAll(other.params()));
        this.remoteAddress = other.remoteAddress().orElse(null);
    }

    @Override
    public ServerRequest.Builder method(HttpMethod method) {
        Assert.notNull((Object)method, (String)"HttpMethod must not be null");
        this.methodName = method.name();
        return this;
    }

    @Override
    public ServerRequest.Builder uri(URI uri) {
        Assert.notNull((Object)uri, (String)"URI must not be null");
        this.uri = uri;
        return this;
    }

    @Override
    public ServerRequest.Builder header(String headerName, String ... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override
    public ServerRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override
    public ServerRequest.Builder cookie(String name, String ... values) {
        for (String value : values) {
            this.cookies.add((Object)name, (Object)new Cookie(name, value));
        }
        return this;
    }

    @Override
    public ServerRequest.Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override
    public ServerRequest.Builder body(byte[] body2) {
        this.body = body2;
        return this;
    }

    @Override
    public ServerRequest.Builder body(String body2) {
        return this.body(body2.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ServerRequest.Builder attribute(String name, Object value) {
        Assert.notNull((Object)name, (String)"'name' must not be null");
        this.attributes.put(name, value);
        return this;
    }

    @Override
    public ServerRequest.Builder attributes(Consumer<Map<String, Object>> attributesConsumer) {
        attributesConsumer.accept(this.attributes);
        return this;
    }

    @Override
    public ServerRequest.Builder param(String name, String ... values) {
        for (String value : values) {
            this.params.add((Object)name, (Object)value);
        }
        return this;
    }

    @Override
    public ServerRequest.Builder params(Consumer<MultiValueMap<String, String>> paramsConsumer) {
        paramsConsumer.accept(this.params);
        return this;
    }

    @Override
    public ServerRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    @Override
    public ServerRequest build() {
        return new BuiltServerRequest(this.servletRequest, this.methodName, this.uri, this.headers, this.cookies, this.attributes, this.params, this.remoteAddress, this.body, this.messageConverters);
    }

    private static class BodyInputStream
    extends ServletInputStream {
        private final InputStream delegate;

        public BodyInputStream(byte[] body2) {
            this.delegate = new ByteArrayInputStream(body2);
        }

        public boolean isFinished() {
            return false;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        public int read() throws IOException {
            return this.delegate.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        public int available() throws IOException {
            return this.delegate.available();
        }

        public void close() throws IOException {
            this.delegate.close();
        }

        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }

    private static class BuiltServerRequest
    implements ServerRequest {
        private final String methodName;
        private final URI uri;
        private final HttpHeaders headers;
        private final HttpServletRequest servletRequest;
        private final MultiValueMap<String, Cookie> cookies;
        private final Map<String, Object> attributes;
        private final byte[] body;
        private final List<HttpMessageConverter<?>> messageConverters;
        private final MultiValueMap<String, String> params;
        @Nullable
        private final InetSocketAddress remoteAddress;

        public BuiltServerRequest(HttpServletRequest servletRequest, String methodName, URI uri, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, Map<String, Object> attributes, MultiValueMap<String, String> params, @Nullable InetSocketAddress remoteAddress, byte[] body2, List<HttpMessageConverter<?>> messageConverters) {
            this.servletRequest = servletRequest;
            this.methodName = methodName;
            this.uri = uri;
            this.headers = new HttpHeaders((MultiValueMap)headers);
            this.cookies = new LinkedMultiValueMap(cookies);
            this.attributes = new LinkedHashMap<String, Object>(attributes);
            this.params = new LinkedMultiValueMap(params);
            this.remoteAddress = remoteAddress;
            this.body = body2;
            this.messageConverters = messageConverters;
        }

        @Override
        public String methodName() {
            return this.methodName;
        }

        @Override
        public MultiValueMap<String, Part> multipartData() throws IOException, ServletException {
            return (MultiValueMap)this.servletRequest().getParts().stream().collect(Collectors.groupingBy(Part::getName, LinkedMultiValueMap::new, Collectors.toList()));
        }

        @Override
        public URI uri() {
            return this.uri;
        }

        @Override
        public UriBuilder uriBuilder() {
            return UriComponentsBuilder.fromUri((URI)this.uri);
        }

        @Override
        public ServerRequest.Headers headers() {
            return new DefaultServerRequest.DefaultRequestHeaders(this.headers);
        }

        @Override
        public MultiValueMap<String, Cookie> cookies() {
            return this.cookies;
        }

        @Override
        public Optional<InetSocketAddress> remoteAddress() {
            return Optional.ofNullable(this.remoteAddress);
        }

        @Override
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.messageConverters;
        }

        @Override
        public <T> T body(Class<T> bodyType) throws IOException, ServletException {
            return this.bodyInternal(bodyType, bodyType);
        }

        @Override
        public <T> T body(ParameterizedTypeReference<T> bodyType) throws IOException, ServletException {
            Type type = bodyType.getType();
            return this.bodyInternal(type, DefaultServerRequest.bodyClass(type));
        }

        private <T> T bodyInternal(Type bodyType, Class<?> bodyClass) throws ServletException, IOException {
            BuiltInputMessage inputMessage = new BuiltInputMessage();
            MediaType contentType = this.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
            for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
                GenericHttpMessageConverter genericMessageConverter;
                if (messageConverter instanceof GenericHttpMessageConverter && (genericMessageConverter = (GenericHttpMessageConverter)messageConverter).canRead(bodyType, bodyClass, contentType)) {
                    return (T)genericMessageConverter.read(bodyType, bodyClass, (HttpInputMessage)inputMessage);
                }
                if (!messageConverter.canRead(bodyClass, contentType)) continue;
                HttpMessageConverter<?> theConverter = messageConverter;
                Class<?> clazz = bodyClass;
                return (T)theConverter.read(clazz, (HttpInputMessage)inputMessage);
            }
            throw new HttpMediaTypeNotSupportedException(contentType, Collections.emptyList());
        }

        @Override
        public Map<String, Object> attributes() {
            return this.attributes;
        }

        @Override
        public MultiValueMap<String, String> params() {
            return this.params;
        }

        @Override
        public Map<String, String> pathVariables() {
            Map pathVariables = (Map)this.attributes().get(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables != null) {
                return pathVariables;
            }
            return Collections.emptyMap();
        }

        @Override
        public HttpSession session() {
            return this.servletRequest.getSession();
        }

        @Override
        public Optional<Principal> principal() {
            return Optional.ofNullable(this.servletRequest.getUserPrincipal());
        }

        @Override
        public HttpServletRequest servletRequest() {
            return this.servletRequest;
        }

        private class BuiltInputMessage
        implements HttpInputMessage {
            private BuiltInputMessage() {
            }

            public InputStream getBody() throws IOException {
                return new BodyInputStream(BuiltServerRequest.this.body);
            }

            public HttpHeaders getHeaders() {
                return BuiltServerRequest.this.headers;
            }
        }
    }
}

