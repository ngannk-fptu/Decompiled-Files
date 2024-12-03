/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.http.CacheControl
 *  org.springframework.http.server.PathContainer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.PathMatcher
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.UrlPathHelper
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class WebContentInterceptor
extends WebContentGenerator
implements HandlerInterceptor {
    private static PathMatcher defaultPathMatcher = new AntPathMatcher();
    private final PathPatternParser patternParser;
    private PathMatcher pathMatcher = defaultPathMatcher;
    private Map<PathPattern, Integer> cacheMappings = new HashMap<PathPattern, Integer>();
    private Map<PathPattern, CacheControl> cacheControlMappings = new HashMap<PathPattern, CacheControl>();

    public WebContentInterceptor() {
        this(PathPatternParser.defaultInstance);
    }

    public WebContentInterceptor(PathPatternParser parser) {
        super(false);
        this.patternParser = parser;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
    }

    @Deprecated
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull((Object)pathMatcher, (String)"PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    public void setCacheMappings(Properties cacheMappings) {
        this.cacheMappings.clear();
        Enumeration<?> propNames = cacheMappings.propertyNames();
        while (propNames.hasMoreElements()) {
            String path = (String)propNames.nextElement();
            int cacheSeconds = Integer.parseInt(cacheMappings.getProperty(path));
            this.cacheMappings.put(this.patternParser.parse(path), cacheSeconds);
        }
    }

    public void addCacheMapping(CacheControl cacheControl, String ... paths) {
        for (String path : paths) {
            this.cacheControlMappings.put(this.patternParser.parse(path), cacheControl);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        this.checkRequest(request);
        Object path = ServletRequestPathUtils.getCachedPath((ServletRequest)request);
        if (this.pathMatcher != defaultPathMatcher) {
            path = path.toString();
        }
        if (!ObjectUtils.isEmpty(this.cacheControlMappings)) {
            CacheControl control;
            CacheControl cacheControl = control = path instanceof PathContainer ? this.lookupCacheControl((PathContainer)path) : this.lookupCacheControl((String)path);
            if (control != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Applying " + control));
                }
                this.applyCacheControl(response, control);
                return true;
            }
        }
        if (!ObjectUtils.isEmpty(this.cacheMappings)) {
            Integer cacheSeconds;
            Integer n = cacheSeconds = path instanceof PathContainer ? this.lookupCacheSeconds((PathContainer)path) : this.lookupCacheSeconds((String)path);
            if (cacheSeconds != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Applying cacheSeconds " + cacheSeconds));
                }
                this.applyCacheSeconds(response, cacheSeconds);
                return true;
            }
        }
        this.prepareResponse(response);
        return true;
    }

    @Nullable
    protected CacheControl lookupCacheControl(PathContainer path) {
        for (Map.Entry<PathPattern, CacheControl> entry : this.cacheControlMappings.entrySet()) {
            if (!entry.getKey().matches(path)) continue;
            return entry.getValue();
        }
        return null;
    }

    @Nullable
    protected CacheControl lookupCacheControl(String lookupPath) {
        for (Map.Entry<PathPattern, CacheControl> entry : this.cacheControlMappings.entrySet()) {
            if (!this.pathMatcher.match(entry.getKey().getPatternString(), lookupPath)) continue;
            return entry.getValue();
        }
        return null;
    }

    @Nullable
    protected Integer lookupCacheSeconds(PathContainer path) {
        for (Map.Entry<PathPattern, Integer> entry : this.cacheMappings.entrySet()) {
            if (!entry.getKey().matches(path)) continue;
            return entry.getValue();
        }
        return null;
    }

    @Nullable
    protected Integer lookupCacheSeconds(String lookupPath) {
        for (Map.Entry<PathPattern, Integer> entry : this.cacheMappings.entrySet()) {
            if (!this.pathMatcher.match(entry.getKey().getPatternString(), lookupPath)) continue;
            return entry.getValue();
        }
        return null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}

