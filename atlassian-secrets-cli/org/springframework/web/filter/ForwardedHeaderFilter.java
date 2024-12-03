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
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
    private static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap<Boolean>(5, Locale.ENGLISH));
    private boolean removeOnly;
    private boolean relativeRedirects;

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public void setRelativeRedirects(boolean relativeRedirects) {
        this.relativeRedirects = relativeRedirects;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
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
            ForwardedHeaderRemovingRequest theRequest = new ForwardedHeaderRemovingRequest(request);
            filterChain.doFilter((ServletRequest)theRequest, (ServletResponse)response);
        } else {
            ForwardedHeaderExtractingRequest theRequest = new ForwardedHeaderExtractingRequest(request);
            ForwardedHeaderExtractingResponse theResponse = this.relativeRedirects ? RelativeRedirectResponseWrapper.wrapIfNecessary(response, HttpStatus.SEE_OTHER) : new ForwardedHeaderExtractingResponse(response, (HttpServletRequest)theRequest);
            filterChain.doFilter((ServletRequest)theRequest, (ServletResponse)theResponse);
        }
    }

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
    }

    private static class ForwardedHeaderExtractingResponse
    extends HttpServletResponseWrapper {
        private static final String FOLDER_SEPARATOR = "/";
        private final HttpServletRequest request;

        public ForwardedHeaderExtractingResponse(HttpServletResponse response, HttpServletRequest request) {
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
                path = path.startsWith(FOLDER_SEPARATOR) ? path : StringUtils.applyRelativePath(this.request.getRequestURI(), path);
            }
            String result = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(this.request)).replacePath(path).replaceQuery(uriComponents.getQuery()).fragment(uriComponents.getFragment()).build().normalize().toUriString();
            super.sendRedirect(result);
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
        private final String contextPath;
        private final String requestUri;
        private final String requestUrl;

        public ForwardedHeaderExtractingRequest(HttpServletRequest request) {
            super(request);
            ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(request);
            UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
            int port = uriComponents.getPort();
            this.scheme = uriComponents.getScheme();
            this.secure = "https".equals(this.scheme);
            this.host = uriComponents.getHost();
            this.port = port == -1 ? (this.secure ? 443 : 80) : port;
            String prefix = ForwardedHeaderExtractingRequest.getForwardedPrefix(request);
            this.contextPath = prefix != null ? prefix : request.getContextPath();
            this.requestUri = this.contextPath + UrlPathHelper.rawPathInstance.getPathWithinApplication(request);
            this.requestUrl = this.scheme + "://" + this.host + (port == -1 ? "" : ":" + port) + this.requestUri;
        }

        @Nullable
        private static String getForwardedPrefix(HttpServletRequest request) {
            String prefix = null;
            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (!"X-Forwarded-Prefix".equalsIgnoreCase(name)) continue;
                prefix = request.getHeader(name);
            }
            if (prefix != null) {
                while (prefix.endsWith("/")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
            }
            return prefix;
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
            return this.contextPath;
        }

        public String getRequestURI() {
            return this.requestUri;
        }

        public StringBuffer getRequestURL() {
            return new StringBuffer(this.requestUrl);
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
            LinkedCaseInsensitiveMap<List<String>> headers = new LinkedCaseInsensitiveMap<List<String>>(Locale.ENGLISH);
            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (FORWARDED_HEADER_NAMES.contains(name)) continue;
                headers.put(name, (List<String>)Collections.list(request.getHeaders(name)));
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

