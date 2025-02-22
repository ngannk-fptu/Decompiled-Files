/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  org.springframework.web.util.UriComponentsBuilder
 *  org.springframework.web.util.UriUtils
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.support;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

public class ServletUriComponentsBuilder
extends UriComponentsBuilder {
    @Nullable
    private String originalPath;

    protected ServletUriComponentsBuilder() {
    }

    protected ServletUriComponentsBuilder(ServletUriComponentsBuilder other) {
        super((UriComponentsBuilder)other);
        this.originalPath = other.originalPath;
    }

    public static ServletUriComponentsBuilder fromContextPath(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.initFromRequest(request);
        builder.replacePath(request.getContextPath());
        return builder;
    }

    public static ServletUriComponentsBuilder fromServletMapping(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromContextPath(request);
        if (StringUtils.hasText((String)UrlPathHelper.defaultInstance.getPathWithinServletMapping(request))) {
            builder.path(request.getServletPath());
        }
        return builder;
    }

    public static ServletUriComponentsBuilder fromRequestUri(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.initFromRequest(request);
        builder.initPath(request.getRequestURI());
        return builder;
    }

    public static ServletUriComponentsBuilder fromRequest(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.initFromRequest(request);
        builder.initPath(request.getRequestURI());
        builder.query(request.getQueryString());
        return builder;
    }

    private static ServletUriComponentsBuilder initFromRequest(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        ServletUriComponentsBuilder builder = new ServletUriComponentsBuilder();
        builder.scheme(scheme);
        builder.host(host);
        if ("http".equals(scheme) && port != 80 || "https".equals(scheme) && port != 443) {
            builder.port(port);
        }
        return builder;
    }

    public static ServletUriComponentsBuilder fromCurrentContextPath() {
        return ServletUriComponentsBuilder.fromContextPath(ServletUriComponentsBuilder.getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentServletMapping() {
        return ServletUriComponentsBuilder.fromServletMapping(ServletUriComponentsBuilder.getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentRequestUri() {
        return ServletUriComponentsBuilder.fromRequestUri(ServletUriComponentsBuilder.getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentRequest() {
        return ServletUriComponentsBuilder.fromRequest(ServletUriComponentsBuilder.getCurrentRequest());
    }

    protected static HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state((boolean)(attrs instanceof ServletRequestAttributes), (String)"No current ServletRequestAttributes");
        return ((ServletRequestAttributes)attrs).getRequest();
    }

    private void initPath(String path) {
        this.originalPath = path;
        this.replacePath(path);
    }

    @Nullable
    public String removePathExtension() {
        String extension = null;
        if (this.originalPath != null) {
            extension = UriUtils.extractFileExtension((String)this.originalPath);
            if (StringUtils.hasLength((String)extension)) {
                int end = this.originalPath.length() - (extension.length() + 1);
                this.replacePath(this.originalPath.substring(0, end));
            }
            this.originalPath = null;
        }
        return extension;
    }

    public ServletUriComponentsBuilder cloneBuilder() {
        return new ServletUriComponentsBuilder(this);
    }
}

