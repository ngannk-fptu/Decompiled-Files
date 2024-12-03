/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.Part
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpRange
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.GenericHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.RequestPath
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.UriBuilder
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriBuilder;

class DefaultServerRequest
implements ServerRequest {
    private final ServletServerHttpRequest serverHttpRequest;
    private final RequestPath requestPath;
    private final ServerRequest.Headers headers;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final MultiValueMap<String, String> params;
    private final Map<String, Object> attributes;
    @Nullable
    private MultiValueMap<String, Part> parts;

    public DefaultServerRequest(HttpServletRequest servletRequest, List<HttpMessageConverter<?>> messageConverters) {
        this.serverHttpRequest = new ServletServerHttpRequest(servletRequest);
        this.messageConverters = Collections.unmodifiableList(new ArrayList(messageConverters));
        this.headers = new DefaultRequestHeaders(this.serverHttpRequest.getHeaders());
        this.params = CollectionUtils.toMultiValueMap((Map)new ServletParametersMap(servletRequest));
        this.attributes = new ServletAttributesMap(servletRequest);
        this.requestPath = ServletRequestPathUtils.hasParsedRequestPath((ServletRequest)servletRequest) ? ServletRequestPathUtils.getParsedRequestPath((ServletRequest)servletRequest) : ServletRequestPathUtils.parseAndCache((HttpServletRequest)servletRequest);
    }

    @Override
    public String methodName() {
        return this.servletRequest().getMethod();
    }

    @Override
    public URI uri() {
        return this.serverHttpRequest.getURI();
    }

    @Override
    public UriBuilder uriBuilder() {
        return ServletUriComponentsBuilder.fromRequest(this.servletRequest());
    }

    @Override
    public RequestPath requestPath() {
        return this.requestPath;
    }

    @Override
    public ServerRequest.Headers headers() {
        return this.headers;
    }

    @Override
    public MultiValueMap<String, Cookie> cookies() {
        Cookie[] cookies = this.servletRequest().getCookies();
        if (cookies == null) {
            cookies = new Cookie[]{};
        }
        LinkedMultiValueMap result = new LinkedMultiValueMap(cookies.length);
        for (Cookie cookie : cookies) {
            result.add((Object)cookie.getName(), (Object)cookie);
        }
        return result;
    }

    @Override
    public HttpServletRequest servletRequest() {
        return this.serverHttpRequest.getServletRequest();
    }

    @Override
    public Optional<InetSocketAddress> remoteAddress() {
        return Optional.of(this.serverHttpRequest.getRemoteAddress());
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

    static Class<?> bodyClass(Type type) {
        ParameterizedType parameterizedType;
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType && (parameterizedType = (ParameterizedType)type).getRawType() instanceof Class) {
            return (Class)parameterizedType.getRawType();
        }
        return Object.class;
    }

    private <T> T bodyInternal(Type bodyType, Class<?> bodyClass) throws ServletException, IOException {
        MediaType contentType = this.headers.contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
        for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
            GenericHttpMessageConverter genericMessageConverter;
            if (messageConverter instanceof GenericHttpMessageConverter && (genericMessageConverter = (GenericHttpMessageConverter)messageConverter).canRead(bodyType, bodyClass, contentType)) {
                return (T)genericMessageConverter.read(bodyType, bodyClass, (HttpInputMessage)this.serverHttpRequest);
            }
            if (!messageConverter.canRead(bodyClass, contentType)) continue;
            HttpMessageConverter<?> theConverter = messageConverter;
            Class<?> clazz = bodyClass;
            return (T)theConverter.read(clazz, (HttpInputMessage)this.serverHttpRequest);
        }
        throw new HttpMediaTypeNotSupportedException(contentType, this.getSupportedMediaTypes(bodyClass));
    }

    private List<MediaType> getSupportedMediaTypes(Class<?> bodyClass) {
        return this.messageConverters.stream().flatMap(converter -> converter.getSupportedMediaTypes(bodyClass).stream()).sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    public Optional<Object> attribute(String name) {
        return Optional.ofNullable(this.servletRequest().getAttribute(name));
    }

    @Override
    public Map<String, Object> attributes() {
        return this.attributes;
    }

    @Override
    public Optional<String> param(String name) {
        return Optional.ofNullable(this.servletRequest().getParameter(name));
    }

    @Override
    public MultiValueMap<String, String> params() {
        return this.params;
    }

    @Override
    public MultiValueMap<String, Part> multipartData() throws IOException, ServletException {
        MultiValueMap result = this.parts;
        if (result == null) {
            this.parts = result = (MultiValueMap)this.servletRequest().getParts().stream().collect(Collectors.groupingBy(Part::getName, LinkedMultiValueMap::new, Collectors.toList()));
        }
        return result;
    }

    @Override
    public Map<String, String> pathVariables() {
        Map pathVariables = (Map)this.servletRequest().getAttribute(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            return pathVariables;
        }
        return Collections.emptyMap();
    }

    @Override
    public HttpSession session() {
        return this.servletRequest().getSession(true);
    }

    @Override
    public Optional<Principal> principal() {
        return Optional.ofNullable(this.serverHttpRequest.getPrincipal());
    }

    public String toString() {
        return String.format("HTTP %s %s", this.method(), this.path());
    }

    static Optional<ServerResponse> checkNotModified(HttpServletRequest servletRequest, @Nullable Instant lastModified, @Nullable String etag) {
        CheckNotModifiedResponse response;
        ServletWebRequest webRequest;
        long lastModifiedTimestamp = -1L;
        if (lastModified != null && lastModified.isAfter(Instant.EPOCH)) {
            lastModifiedTimestamp = lastModified.toEpochMilli();
        }
        if ((webRequest = new ServletWebRequest(servletRequest, (HttpServletResponse)(response = new CheckNotModifiedResponse()))).checkNotModified(etag, lastModifiedTimestamp)) {
            return Optional.of(((ServerResponse.BodyBuilder)ServerResponse.status(response.status).headers(headers -> headers.addAll((MultiValueMap)response.headers))).build());
        }
        return Optional.empty();
    }

    private static final class CheckNotModifiedResponse
    implements HttpServletResponse {
        private final HttpHeaders headers = new HttpHeaders();
        private int status = 200;

        private CheckNotModifiedResponse() {
        }

        public boolean containsHeader(String name) {
            return this.headers.containsKey((Object)name);
        }

        public void setDateHeader(String name, long date) {
            this.headers.setDate(name, date);
        }

        public void setHeader(String name, String value) {
            this.headers.set(name, value);
        }

        public void addHeader(String name, String value) {
            this.headers.add(name, value);
        }

        public void setStatus(int sc) {
            this.status = sc;
        }

        @Deprecated
        public void setStatus(int sc, String sm) {
            this.status = sc;
        }

        public int getStatus() {
            return this.status;
        }

        @Nullable
        public String getHeader(String name) {
            return this.headers.getFirst(name);
        }

        public Collection<String> getHeaders(String name) {
            List<String> result = this.headers.get((Object)name);
            return result != null ? result : Collections.emptyList();
        }

        public Collection<String> getHeaderNames() {
            return this.headers.keySet();
        }

        public void addCookie(Cookie cookie) {
            throw new UnsupportedOperationException();
        }

        public String encodeURL(String url) {
            throw new UnsupportedOperationException();
        }

        public String encodeRedirectURL(String url) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public String encodeUrl(String url) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public String encodeRedirectUrl(String url) {
            throw new UnsupportedOperationException();
        }

        public void sendError(int sc, String msg) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void sendError(int sc) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void sendRedirect(String location) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void addDateHeader(String name, long date) {
            throw new UnsupportedOperationException();
        }

        public void setIntHeader(String name, int value) {
            throw new UnsupportedOperationException();
        }

        public void addIntHeader(String name, int value) {
            throw new UnsupportedOperationException();
        }

        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        public PrintWriter getWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        public void setCharacterEncoding(String charset) {
            throw new UnsupportedOperationException();
        }

        public void setContentLength(int len) {
            throw new UnsupportedOperationException();
        }

        public void setContentLengthLong(long len) {
            throw new UnsupportedOperationException();
        }

        public void setContentType(String type) {
            throw new UnsupportedOperationException();
        }

        public void setBufferSize(int size) {
            throw new UnsupportedOperationException();
        }

        public int getBufferSize() {
            throw new UnsupportedOperationException();
        }

        public void flushBuffer() throws IOException {
            throw new UnsupportedOperationException();
        }

        public void resetBuffer() {
            throw new UnsupportedOperationException();
        }

        public boolean isCommitted() {
            throw new UnsupportedOperationException();
        }

        public void reset() {
            throw new UnsupportedOperationException();
        }

        public void setLocale(Locale loc) {
            throw new UnsupportedOperationException();
        }

        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ServletAttributesMap
    extends AbstractMap<String, Object> {
        private final HttpServletRequest servletRequest;

        private ServletAttributesMap(HttpServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        @Override
        public boolean containsKey(Object key) {
            String name = (String)key;
            return this.servletRequest.getAttribute(name) != null;
        }

        @Override
        public void clear() {
            ArrayList<String> attributeNames = Collections.list(this.servletRequest.getAttributeNames());
            attributeNames.forEach(arg_0 -> ((HttpServletRequest)this.servletRequest).removeAttribute(arg_0));
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            return Collections.list(this.servletRequest.getAttributeNames()).stream().map(name -> {
                Object value = this.servletRequest.getAttribute(name);
                return new AbstractMap.SimpleImmutableEntry<String, Object>((String)name, value);
            }).collect(Collectors.toSet());
        }

        @Override
        public Object get(Object key) {
            String name = (String)key;
            return this.servletRequest.getAttribute(name);
        }

        @Override
        public Object put(String key, Object value) {
            Object oldValue = this.servletRequest.getAttribute(key);
            this.servletRequest.setAttribute(key, value);
            return oldValue;
        }

        @Override
        public Object remove(Object key) {
            String name = (String)key;
            Object value = this.servletRequest.getAttribute(name);
            this.servletRequest.removeAttribute(name);
            return value;
        }
    }

    private static final class ServletParametersMap
    extends AbstractMap<String, List<String>> {
        private final HttpServletRequest servletRequest;

        private ServletParametersMap(HttpServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        @Override
        public Set<Map.Entry<String, List<String>>> entrySet() {
            return this.servletRequest.getParameterMap().entrySet().stream().map(entry -> {
                List<Object> value = Arrays.asList((Object[])entry.getValue());
                return new AbstractMap.SimpleImmutableEntry(entry.getKey(), value);
            }).collect(Collectors.toSet());
        }

        @Override
        public int size() {
            return this.servletRequest.getParameterMap().size();
        }

        @Override
        public List<String> get(Object key) {
            String name = (String)key;
            Object[] parameterValues = this.servletRequest.getParameterValues(name);
            if (!ObjectUtils.isEmpty((Object[])parameterValues)) {
                return Arrays.asList(parameterValues);
            }
            return Collections.emptyList();
        }

        @Override
        public List<String> put(String key, List<String> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    static class DefaultRequestHeaders
    implements ServerRequest.Headers {
        private final HttpHeaders httpHeaders;

        public DefaultRequestHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = HttpHeaders.readOnlyHttpHeaders((HttpHeaders)httpHeaders);
        }

        @Override
        public List<MediaType> accept() {
            return this.httpHeaders.getAccept();
        }

        @Override
        public List<Charset> acceptCharset() {
            return this.httpHeaders.getAcceptCharset();
        }

        @Override
        public List<Locale.LanguageRange> acceptLanguage() {
            return this.httpHeaders.getAcceptLanguage();
        }

        @Override
        public OptionalLong contentLength() {
            long value = this.httpHeaders.getContentLength();
            return value != -1L ? OptionalLong.of(value) : OptionalLong.empty();
        }

        @Override
        public Optional<MediaType> contentType() {
            return Optional.ofNullable(this.httpHeaders.getContentType());
        }

        @Override
        public InetSocketAddress host() {
            return this.httpHeaders.getHost();
        }

        @Override
        public List<HttpRange> range() {
            return this.httpHeaders.getRange();
        }

        @Override
        public List<String> header(String headerName) {
            List<String> headerValues = this.httpHeaders.get((Object)headerName);
            return headerValues != null ? headerValues : Collections.emptyList();
        }

        @Override
        public HttpHeaders asHttpHeaders() {
            return this.httpHeaders;
        }

        public String toString() {
            return this.httpHeaders.toString();
        }
    }
}

