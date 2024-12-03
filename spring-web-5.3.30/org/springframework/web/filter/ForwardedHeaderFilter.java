/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RelativeRedirectResponseWrapper;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

public class ForwardedHeaderFilter
extends OncePerRequestFilter {
    private static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
    private boolean removeOnly;
    private boolean relativeRedirects;

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public void setRelativeRedirects(boolean relativeRedirects) {
        this.relativeRedirects = relativeRedirects;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (request.getHeader(headerName) == null) continue;
            return false;
        }
        return true;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.removeOnly) {
            ForwardedHeaderRemovingRequest wrappedRequest = new ForwardedHeaderRemovingRequest(request);
            filterChain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)response);
        } else {
            ForwardedHeaderExtractingRequest wrappedRequest = new ForwardedHeaderExtractingRequest(request);
            ForwardedHeaderExtractingResponse wrappedResponse = this.relativeRedirects ? RelativeRedirectResponseWrapper.wrapIfNecessary(response, HttpStatus.SEE_OTHER) : new ForwardedHeaderExtractingResponse(response, (HttpServletRequest)wrappedRequest);
            filterChain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)wrappedResponse);
        }
    }

    @Override
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        this.doFilterInternal(request, response, filterChain);
    }

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
    }

    private static class ForwardedHeaderExtractingResponse
    extends HttpServletResponseWrapper {
        private static final String FOLDER_SEPARATOR = "/";
        private final HttpServletRequest request;

        ForwardedHeaderExtractingResponse(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        public void sendRedirect(String location) throws IOException {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(location);
            UriComponents uriComponents = builder.build();
            if (uriComponents.getScheme() != null) {
                super.sendRedirect(location);
                return;
            }
            if (location.startsWith("//")) {
                String scheme = this.request.getScheme();
                super.sendRedirect(builder.scheme(scheme).toUriString());
                return;
            }
            String path = uriComponents.getPath();
            if (path != null) {
                path = path.startsWith(FOLDER_SEPARATOR) ? path : StringUtils.applyRelativePath((String)this.request.getRequestURI(), (String)path);
            }
            String result = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(this.request)).replacePath(path).replaceQuery(uriComponents.getQuery()).fragment(uriComponents.getFragment()).build().normalize().toUriString();
            super.sendRedirect(result);
        }
    }

    private static class ForwardedPrefixExtractor {
        private final Supplier<HttpServletRequest> delegate;
        private final String baseUrl;
        private String actualRequestUri;
        @Nullable
        private final String forwardedPrefix;
        @Nullable
        private String requestUri;
        private String requestUrl;

        public ForwardedPrefixExtractor(Supplier<HttpServletRequest> delegateRequest, String baseUrl) {
            this.delegate = delegateRequest;
            this.baseUrl = baseUrl;
            this.actualRequestUri = delegateRequest.get().getRequestURI();
            this.forwardedPrefix = ForwardedPrefixExtractor.initForwardedPrefix(delegateRequest.get());
            this.requestUri = this.initRequestUri();
            this.requestUrl = this.initRequestUrl();
        }

        @Nullable
        private static String initForwardedPrefix(HttpServletRequest request) {
            String result = null;
            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (!"X-Forwarded-Prefix".equalsIgnoreCase(name)) continue;
                result = request.getHeader(name);
            }
            if (result != null) {
                String[] rawPrefixes;
                StringBuilder prefix = new StringBuilder(result.length());
                for (String rawPrefix : rawPrefixes = StringUtils.tokenizeToStringArray((String)result, (String)",")) {
                    int endIndex;
                    for (endIndex = rawPrefix.length(); endIndex > 0 && rawPrefix.charAt(endIndex - 1) == '/'; --endIndex) {
                    }
                    prefix.append(endIndex != rawPrefix.length() ? rawPrefix.substring(0, endIndex) : rawPrefix);
                }
                return prefix.toString();
            }
            return null;
        }

        @Nullable
        private String initRequestUri() {
            if (this.forwardedPrefix != null) {
                return this.forwardedPrefix + UrlPathHelper.rawPathInstance.getPathWithinApplication(this.delegate.get());
            }
            return null;
        }

        private String initRequestUrl() {
            return this.baseUrl + (this.requestUri != null ? this.requestUri : this.delegate.get().getRequestURI());
        }

        public String getContextPath() {
            return this.forwardedPrefix != null ? this.forwardedPrefix : this.delegate.get().getContextPath();
        }

        public String getRequestUri() {
            if (this.requestUri == null) {
                return this.delegate.get().getRequestURI();
            }
            this.recalculatePathsIfNecessary();
            return this.requestUri;
        }

        public StringBuffer getRequestUrl() {
            this.recalculatePathsIfNecessary();
            return new StringBuffer(this.requestUrl);
        }

        private void recalculatePathsIfNecessary() {
            if (!this.actualRequestUri.equals(this.delegate.get().getRequestURI())) {
                this.actualRequestUri = this.delegate.get().getRequestURI();
                this.requestUri = this.initRequestUri();
                this.requestUrl = this.initRequestUrl();
            }
        }
    }

    private static class ForwardedHeaderExtractingRequest
    extends ForwardedHeaderRemovingRequest {
        @Nullable
        private final String scheme;
        private final boolean secure;
        @Nullable
        private final String host;
        private final int port;
        @Nullable
        private final InetSocketAddress remoteAddress;
        private final ForwardedPrefixExtractor forwardedPrefixExtractor;

        ForwardedHeaderExtractingRequest(HttpServletRequest servletRequest) {
            super(servletRequest);
            ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
            UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(request).build();
            int port = uriComponents.getPort();
            this.scheme = uriComponents.getScheme();
            this.secure = "https".equals(this.scheme) || "wss".equals(this.scheme);
            this.host = uriComponents.getHost();
            this.port = port == -1 ? (this.secure ? 443 : 80) : port;
            this.remoteAddress = UriComponentsBuilder.parseForwardedFor(request, request.getRemoteAddress());
            String baseUrl = this.scheme + "://" + this.host + (port == -1 ? "" : ":" + port);
            Supplier<HttpServletRequest> delegateRequest = () -> (HttpServletRequest)this.getRequest();
            this.forwardedPrefixExtractor = new ForwardedPrefixExtractor(delegateRequest, baseUrl);
        }

        @Nullable
        public String getScheme() {
            return this.scheme;
        }

        @Nullable
        public String getServerName() {
            return this.host;
        }

        public int getServerPort() {
            return this.port;
        }

        public boolean isSecure() {
            return this.secure;
        }

        public String getContextPath() {
            return this.forwardedPrefixExtractor.getContextPath();
        }

        public String getRequestURI() {
            return this.forwardedPrefixExtractor.getRequestUri();
        }

        public StringBuffer getRequestURL() {
            return this.forwardedPrefixExtractor.getRequestUrl();
        }

        @Nullable
        public String getRemoteHost() {
            return this.remoteAddress != null ? this.remoteAddress.getHostString() : super.getRemoteHost();
        }

        @Nullable
        public String getRemoteAddr() {
            return this.remoteAddress != null ? this.remoteAddress.getHostString() : super.getRemoteAddr();
        }

        public int getRemotePort() {
            return this.remoteAddress != null ? this.remoteAddress.getPort() : super.getRemotePort();
        }
    }

    private static class ForwardedHeaderRemovingRequest
    extends HttpServletRequestWrapper {
        private final Map<String, List<String>> headers;

        public ForwardedHeaderRemovingRequest(HttpServletRequest request) {
            super(request);
            this.headers = ForwardedHeaderRemovingRequest.initHeaders(request);
        }

        private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
            LinkedCaseInsensitiveMap headers = new LinkedCaseInsensitiveMap(Locale.ENGLISH);
            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (FORWARDED_HEADER_NAMES.contains(name)) continue;
                headers.put(name, Collections.list(request.getHeaders(name)));
            }
            return headers;
        }

        @Nullable
        public String getHeader(String name) {
            List<String> value = this.headers.get(name);
            return CollectionUtils.isEmpty(value) ? null : value.get(0);
        }

        public Enumeration<String> getHeaders(String name) {
            List<String> value = this.headers.get(name);
            return Collections.enumeration(value != null ? value : Collections.emptySet());
        }

        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }
    }
}

