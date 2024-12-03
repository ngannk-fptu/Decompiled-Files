/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.handler;

import java.util.Arrays;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;

public final class MappedInterceptor
implements HandlerInterceptor {
    private static final PathMatcher defaultPathMatcher = new AntPathMatcher();
    @Nullable
    private final PatternAdapter[] includePatterns;
    @Nullable
    private final PatternAdapter[] excludePatterns;
    private PathMatcher pathMatcher = defaultPathMatcher;
    private final HandlerInterceptor interceptor;

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, HandlerInterceptor interceptor, @Nullable PathPatternParser parser) {
        this.includePatterns = PatternAdapter.initPatterns(includePatterns, parser);
        this.excludePatterns = PatternAdapter.initPatterns(excludePatterns, parser);
        this.interceptor = interceptor;
    }

    public MappedInterceptor(@Nullable String[] includePatterns, HandlerInterceptor interceptor) {
        this(includePatterns, null, interceptor);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, HandlerInterceptor interceptor) {
        this(includePatterns, excludePatterns, interceptor, null);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, WebRequestInterceptor interceptor) {
        this(includePatterns, null, interceptor);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, WebRequestInterceptor interceptor) {
        this(includePatterns, excludePatterns, new WebRequestHandlerInterceptorAdapter(interceptor));
    }

    @Nullable
    public String[] getPathPatterns() {
        return !ObjectUtils.isEmpty(this.includePatterns) ? (String[])Arrays.stream(this.includePatterns).map(PatternAdapter::getPatternString).toArray(String[]::new) : null;
    }

    public HandlerInterceptor getInterceptor() {
        return this.interceptor;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public boolean matches(HttpServletRequest request) {
        Object path = ServletRequestPathUtils.getCachedPath((ServletRequest)request);
        if (this.pathMatcher != defaultPathMatcher) {
            path = path.toString();
        }
        boolean isPathContainer = path instanceof PathContainer;
        if (!ObjectUtils.isEmpty(this.excludePatterns)) {
            for (PatternAdapter adapter : this.excludePatterns) {
                if (!adapter.match(path, isPathContainer, this.pathMatcher)) continue;
                return false;
            }
        }
        if (ObjectUtils.isEmpty(this.includePatterns)) {
            return true;
        }
        for (PatternAdapter adapter : this.includePatterns) {
            if (!adapter.match(path, isPathContainer, this.pathMatcher)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public boolean matches(String lookupPath, PathMatcher pathMatcher) {
        PathMatcher pathMatcher2 = pathMatcher = this.pathMatcher != defaultPathMatcher ? this.pathMatcher : pathMatcher;
        if (!ObjectUtils.isEmpty(this.excludePatterns)) {
            for (PatternAdapter adapter : this.excludePatterns) {
                if (!pathMatcher.match(adapter.getPatternString(), lookupPath)) continue;
                return false;
            }
        }
        if (ObjectUtils.isEmpty(this.includePatterns)) {
            return true;
        }
        for (PatternAdapter adapter : this.includePatterns) {
            if (!pathMatcher.match(adapter.getPatternString(), lookupPath)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.interceptor.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        this.interceptor.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        this.interceptor.afterCompletion(request, response, handler, ex);
    }

    private static class PatternAdapter {
        private final String patternString;
        @Nullable
        private final PathPattern pathPattern;

        public PatternAdapter(String pattern, @Nullable PathPatternParser parser) {
            this.patternString = pattern;
            this.pathPattern = PatternAdapter.initPathPattern(pattern, parser);
        }

        @Nullable
        private static PathPattern initPathPattern(String pattern, @Nullable PathPatternParser parser) {
            try {
                return (parser != null ? parser : PathPatternParser.defaultInstance).parse(pattern);
            }
            catch (PatternParseException ex) {
                return null;
            }
        }

        public String getPatternString() {
            return this.patternString;
        }

        public boolean match(Object path, boolean isPathContainer, PathMatcher pathMatcher) {
            if (isPathContainer) {
                PathContainer pathContainer = (PathContainer)path;
                if (this.pathPattern != null) {
                    return this.pathPattern.matches(pathContainer);
                }
                String lookupPath = pathContainer.value();
                path = UrlPathHelper.defaultInstance.removeSemicolonContent(lookupPath);
            }
            return pathMatcher.match(this.patternString, (String)path);
        }

        @Nullable
        public static PatternAdapter[] initPatterns(@Nullable String[] patterns, @Nullable PathPatternParser parser) {
            if (ObjectUtils.isEmpty(patterns)) {
                return null;
            }
            return (PatternAdapter[])Arrays.stream(patterns).map(pattern -> new PatternAdapter((String)pattern, parser)).toArray(PatternAdapter[]::new);
        }
    }
}

