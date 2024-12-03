/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.MappingMatch
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.MappingMatch;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

public abstract class ServletRequestPathUtils {
    public static final String PATH_ATTRIBUTE = ServletRequestPathUtils.class.getName() + ".PATH";

    public static RequestPath parseAndCache(HttpServletRequest request) {
        RequestPath requestPath = ServletRequestPath.parse(request);
        request.setAttribute(PATH_ATTRIBUTE, (Object)requestPath);
        return requestPath;
    }

    public static RequestPath getParsedRequestPath(ServletRequest request) {
        RequestPath path = (RequestPath)request.getAttribute(PATH_ATTRIBUTE);
        Assert.notNull((Object)path, () -> "Expected parsed RequestPath in request attribute \"" + PATH_ATTRIBUTE + "\".");
        return path;
    }

    public static void setParsedRequestPath(@Nullable RequestPath requestPath, ServletRequest request) {
        if (requestPath != null) {
            request.setAttribute(PATH_ATTRIBUTE, (Object)requestPath);
        } else {
            request.removeAttribute(PATH_ATTRIBUTE);
        }
    }

    public static boolean hasParsedRequestPath(ServletRequest request) {
        return request.getAttribute(PATH_ATTRIBUTE) != null;
    }

    public static void clearParsedRequestPath(ServletRequest request) {
        request.removeAttribute(PATH_ATTRIBUTE);
    }

    public static Object getCachedPath(ServletRequest request) {
        String lookupPath = (String)request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
        if (lookupPath != null) {
            return lookupPath;
        }
        RequestPath requestPath = (RequestPath)request.getAttribute(PATH_ATTRIBUTE);
        if (requestPath != null) {
            return requestPath.pathWithinApplication();
        }
        throw new IllegalArgumentException("Neither a pre-parsed RequestPath nor a pre-resolved String lookupPath is available.");
    }

    public static String getCachedPathValue(ServletRequest request) {
        Object path = ServletRequestPathUtils.getCachedPath(request);
        if (path instanceof PathContainer) {
            String value = ((PathContainer)path).value();
            path = UrlPathHelper.defaultInstance.removeSemicolonContent(value);
        }
        return (String)path;
    }

    public static boolean hasCachedPath(ServletRequest request) {
        return request.getAttribute(PATH_ATTRIBUTE) != null || request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE) != null;
    }

    private static class Servlet4Delegate {
        private Servlet4Delegate() {
        }

        @Nullable
        public static String getServletPathPrefix(HttpServletRequest request) {
            MappingMatch match;
            HttpServletMapping mapping = (HttpServletMapping)request.getAttribute("javax.servlet.include.mapping");
            if (mapping == null) {
                mapping = request.getHttpServletMapping();
            }
            if (!ObjectUtils.nullSafeEquals((Object)(match = mapping.getMappingMatch()), (Object)MappingMatch.PATH)) {
                return null;
            }
            String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
            servletPath = servletPath != null ? servletPath : request.getServletPath();
            return UriUtils.encodePath(servletPath, StandardCharsets.UTF_8);
        }
    }

    private static final class ServletRequestPath
    implements RequestPath {
        private final RequestPath requestPath;
        private final PathContainer contextPath;

        private ServletRequestPath(String rawPath, @Nullable String contextPath, String servletPathPrefix) {
            Assert.notNull((Object)servletPathPrefix, (String)"`servletPathPrefix` is required");
            this.requestPath = RequestPath.parse(rawPath, contextPath + servletPathPrefix);
            this.contextPath = PathContainer.parsePath(StringUtils.hasText((String)contextPath) ? contextPath : "");
        }

        @Override
        public String value() {
            return this.requestPath.value();
        }

        @Override
        public List<PathContainer.Element> elements() {
            return this.requestPath.elements();
        }

        @Override
        public PathContainer contextPath() {
            return this.contextPath;
        }

        @Override
        public PathContainer pathWithinApplication() {
            return this.requestPath.pathWithinApplication();
        }

        @Override
        public RequestPath modifyContextPath(String contextPath) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            return this.requestPath.equals(((ServletRequestPath)other).requestPath);
        }

        public int hashCode() {
            return this.requestPath.hashCode();
        }

        public String toString() {
            return this.requestPath.toString();
        }

        public static RequestPath parse(HttpServletRequest request) {
            String servletPathPrefix;
            String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri == null) {
                requestUri = request.getRequestURI();
            }
            if (UrlPathHelper.servlet4Present && StringUtils.hasText((String)(servletPathPrefix = Servlet4Delegate.getServletPathPrefix(request)))) {
                return new ServletRequestPath(requestUri, request.getContextPath(), servletPathPrefix);
            }
            return RequestPath.parse(requestUri, request.getContextPath());
        }
    }
}

