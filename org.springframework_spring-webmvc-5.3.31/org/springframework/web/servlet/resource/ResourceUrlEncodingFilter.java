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
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.web.filter.GenericFilterBean
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.util.UrlPathHelper;

public class ResourceUrlEncodingFilter
extends GenericFilterBean {
    private static final Log logger = LogFactory.getLog(ResourceUrlEncodingFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("ResourceUrlEncodingFilter only supports HTTP requests");
        }
        ResourceUrlEncodingRequestWrapper wrappedRequest = new ResourceUrlEncodingRequestWrapper((HttpServletRequest)request);
        ResourceUrlEncodingResponseWrapper wrappedResponse = new ResourceUrlEncodingResponseWrapper(wrappedRequest, (HttpServletResponse)response);
        filterChain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)wrappedResponse);
    }

    static class LookupPathIndexException
    extends IllegalArgumentException {
        LookupPathIndexException(String lookupPath, String requestUri) {
            super("Failed to find lookupPath '" + lookupPath + "' within requestUri '" + requestUri + "'. This could be because the path has invalid encoded characters or isn't normalized.");
        }
    }

    private static class ResourceUrlEncodingResponseWrapper
    extends HttpServletResponseWrapper {
        private final ResourceUrlEncodingRequestWrapper request;

        ResourceUrlEncodingResponseWrapper(ResourceUrlEncodingRequestWrapper request, HttpServletResponse wrapped) {
            super(wrapped);
            this.request = request;
        }

        public String encodeURL(String url) {
            String urlPath = this.request.resolveUrlPath(url);
            if (urlPath != null) {
                return super.encodeURL(urlPath);
            }
            return super.encodeURL(url);
        }
    }

    private static class ResourceUrlEncodingRequestWrapper
    extends HttpServletRequestWrapper {
        @Nullable
        private ResourceUrlProvider resourceUrlProvider;
        @Nullable
        private Integer indexLookupPath;
        private String prefixLookupPath = "";

        ResourceUrlEncodingRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void setAttribute(String name, Object value) {
            super.setAttribute(name, value);
            if (ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR.equals(name) && value instanceof ResourceUrlProvider) {
                this.initLookupPath((ResourceUrlProvider)value);
            }
        }

        private void initLookupPath(ResourceUrlProvider urlProvider) {
            this.resourceUrlProvider = urlProvider;
            if (this.indexLookupPath == null) {
                String contextPath;
                UrlPathHelper pathHelper = this.resourceUrlProvider.getUrlPathHelper();
                String requestUri = pathHelper.getRequestUri((HttpServletRequest)this);
                String lookupPath = pathHelper.getLookupPathForRequest((HttpServletRequest)this);
                this.indexLookupPath = requestUri.lastIndexOf(lookupPath);
                if (this.indexLookupPath == -1) {
                    throw new LookupPathIndexException(lookupPath, requestUri);
                }
                this.prefixLookupPath = requestUri.substring(0, this.indexLookupPath);
                if (StringUtils.matchesCharacter((String)lookupPath, (char)'/') && !StringUtils.matchesCharacter((String)requestUri, (char)'/') && requestUri.equals(contextPath = pathHelper.getContextPath((HttpServletRequest)this))) {
                    this.indexLookupPath = requestUri.length();
                    this.prefixLookupPath = requestUri;
                }
            }
        }

        @Nullable
        public String resolveUrlPath(String url) {
            if (this.resourceUrlProvider == null) {
                logger.trace((Object)("ResourceUrlProvider not available via request attribute " + ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR));
                return null;
            }
            if (this.indexLookupPath != null && url.startsWith(this.prefixLookupPath)) {
                int suffixIndex = this.getEndPathIndex(url);
                String suffix = url.substring(suffixIndex);
                String lookupPath = url.substring(this.indexLookupPath, suffixIndex);
                if ((lookupPath = this.resourceUrlProvider.getForLookupPath(lookupPath)) != null) {
                    return this.prefixLookupPath + lookupPath + suffix;
                }
            }
            return null;
        }

        private int getEndPathIndex(String path) {
            int end = path.indexOf(63);
            int fragmentIndex = path.indexOf(35);
            if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
                end = fragmentIndex;
            }
            if (end == -1) {
                end = path.length();
            }
            return end;
        }
    }
}

