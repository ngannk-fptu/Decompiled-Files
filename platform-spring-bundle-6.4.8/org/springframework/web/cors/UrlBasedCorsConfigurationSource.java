/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.cors;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class UrlBasedCorsConfigurationSource
implements CorsConfigurationSource {
    private static PathMatcher defaultPathMatcher = new AntPathMatcher();
    private final PathPatternParser patternParser;
    private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;
    private PathMatcher pathMatcher = defaultPathMatcher;
    @Nullable
    private String lookupPathAttributeName;
    private boolean allowInitLookupPath = true;
    private final Map<PathPattern, CorsConfiguration> corsConfigurations = new LinkedHashMap<PathPattern, CorsConfiguration>();

    public UrlBasedCorsConfigurationSource() {
        this(PathPatternParser.defaultInstance);
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser parser) {
        Assert.notNull((Object)parser, "PathPatternParser must not be null");
        this.patternParser = parser;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.initUrlPathHelper();
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
        this.initUrlPathHelper();
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    @Deprecated
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.initUrlPathHelper();
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
    }

    private void initUrlPathHelper() {
        if (this.urlPathHelper == UrlPathHelper.defaultInstance) {
            this.urlPathHelper = new UrlPathHelper();
        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull((Object)urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    public void setAllowInitLookupPath(boolean allowInitLookupPath) {
        this.allowInitLookupPath = allowInitLookupPath;
    }

    @Deprecated
    public void setLookupPathAttributeName(String name) {
        this.lookupPathAttributeName = name;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            corsConfigurations.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String pattern, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(pattern), config);
    }

    public Map<String, CorsConfiguration> getCorsConfigurations() {
        HashMap result = CollectionUtils.newHashMap(this.corsConfigurations.size());
        this.corsConfigurations.forEach((pattern, config) -> result.put(pattern.getPatternString(), config));
        return Collections.unmodifiableMap(result);
    }

    @Override
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        Object path = this.resolvePath(request);
        boolean isPathContainer = path instanceof PathContainer;
        for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (!this.match(path, isPathContainer, entry.getKey())) continue;
            return entry.getValue();
        }
        return null;
    }

    private Object resolvePath(HttpServletRequest request) {
        if (this.allowInitLookupPath && !ServletRequestPathUtils.hasCachedPath((ServletRequest)request)) {
            return this.lookupPathAttributeName != null ? this.urlPathHelper.getLookupPathForRequest(request, this.lookupPathAttributeName) : this.urlPathHelper.getLookupPathForRequest(request);
        }
        Object lookupPath = ServletRequestPathUtils.getCachedPath((ServletRequest)request);
        if (this.pathMatcher != defaultPathMatcher) {
            lookupPath = lookupPath.toString();
        }
        return lookupPath;
    }

    private boolean match(Object path, boolean isPathContainer, PathPattern pattern) {
        return isPathContainer ? pattern.matches((PathContainer)path) : this.pathMatcher.match(pattern.getPatternString(), (String)path);
    }
}

