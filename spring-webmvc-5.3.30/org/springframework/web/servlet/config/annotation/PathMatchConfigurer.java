/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.PathMatcher
 *  org.springframework.web.util.UrlPathHelper
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.config.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

public class PathMatchConfigurer {
    @Nullable
    private PathPatternParser patternParser;
    @Nullable
    private Boolean trailingSlashMatch;
    @Nullable
    private Map<String, Predicate<Class<?>>> pathPrefixes;
    @Nullable
    private Boolean suffixPatternMatch;
    @Nullable
    private Boolean registeredSuffixPatternMatch;
    @Nullable
    private UrlPathHelper urlPathHelper;
    @Nullable
    private PathMatcher pathMatcher;
    @Nullable
    private PathPatternParser defaultPatternParser;
    @Nullable
    private UrlPathHelper defaultUrlPathHelper;
    @Nullable
    private PathMatcher defaultPathMatcher;

    public PathMatchConfigurer setPatternParser(PathPatternParser patternParser) {
        this.patternParser = patternParser;
        return this;
    }

    public PathMatchConfigurer setUseTrailingSlashMatch(Boolean trailingSlashMatch) {
        this.trailingSlashMatch = trailingSlashMatch;
        return this;
    }

    public PathMatchConfigurer addPathPrefix(String prefix, Predicate<Class<?>> predicate) {
        if (this.pathPrefixes == null) {
            this.pathPrefixes = new LinkedHashMap();
        }
        this.pathPrefixes.put(prefix, predicate);
        return this;
    }

    @Deprecated
    public PathMatchConfigurer setUseSuffixPatternMatch(Boolean suffixPatternMatch) {
        this.suffixPatternMatch = suffixPatternMatch;
        return this;
    }

    @Deprecated
    public PathMatchConfigurer setUseRegisteredSuffixPatternMatch(Boolean registeredSuffixPatternMatch) {
        this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
        return this;
    }

    public PathMatchConfigurer setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
        return this;
    }

    public PathMatchConfigurer setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }

    @Nullable
    public PathPatternParser getPatternParser() {
        return this.patternParser;
    }

    @Nullable
    @Deprecated
    public Boolean isUseTrailingSlashMatch() {
        return this.trailingSlashMatch;
    }

    @Nullable
    protected Map<String, Predicate<Class<?>>> getPathPrefixes() {
        return this.pathPrefixes;
    }

    @Nullable
    @Deprecated
    public Boolean isUseRegisteredSuffixPatternMatch() {
        return this.registeredSuffixPatternMatch;
    }

    @Nullable
    @Deprecated
    public Boolean isUseSuffixPatternMatch() {
        return this.suffixPatternMatch;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Nullable
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    protected UrlPathHelper getUrlPathHelperOrDefault() {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper;
        }
        if (this.defaultUrlPathHelper == null) {
            this.defaultUrlPathHelper = new UrlPathHelper();
        }
        return this.defaultUrlPathHelper;
    }

    protected PathMatcher getPathMatcherOrDefault() {
        if (this.pathMatcher != null) {
            return this.pathMatcher;
        }
        if (this.defaultPathMatcher == null) {
            this.defaultPathMatcher = new AntPathMatcher();
        }
        return this.defaultPathMatcher;
    }

    public PathPatternParser getPatternParserOrDefault() {
        if (this.patternParser != null) {
            return this.patternParser;
        }
        if (this.defaultPatternParser == null) {
            this.defaultPatternParser = new PathPatternParser();
        }
        return this.defaultPatternParser;
    }
}

