/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.server.RequestPath
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.UrlPathHelper
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class AbstractUrlHandlerMapping
extends AbstractHandlerMapping
implements MatchableHandlerMapping {
    @Nullable
    private Object rootHandler;
    private boolean useTrailingSlashMatch = false;
    private boolean lazyInitHandlers = false;
    private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>();
    private final Map<PathPattern, Object> pathPatternHandlerMap = new LinkedHashMap<PathPattern, Object>();

    @Override
    public void setPatternParser(PathPatternParser patternParser) {
        Assert.state((boolean)this.handlerMap.isEmpty(), (String)"PathPatternParser must be set before the initialization of the handler map via ApplicationContextAware#setApplicationContext.");
        super.setPatternParser(patternParser);
    }

    public void setRootHandler(@Nullable Object rootHandler) {
        this.rootHandler = rootHandler;
    }

    @Nullable
    public Object getRootHandler() {
        return this.rootHandler;
    }

    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        if (this.getPatternParser() != null) {
            this.getPatternParser().setMatchOptionalTrailingSeparator(useTrailingSlashMatch);
        }
    }

    public boolean useTrailingSlashMatch() {
        return this.useTrailingSlashMatch;
    }

    public void setLazyInitHandlers(boolean lazyInitHandlers) {
        this.lazyInitHandlers = lazyInitHandlers;
    }

    @Override
    @Nullable
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        Object handler;
        String lookupPath = this.initLookupPath(request);
        if (this.usesPathPatterns()) {
            RequestPath path = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)request);
            handler = this.lookupHandler(path, lookupPath, request);
        } else {
            handler = this.lookupHandler(lookupPath, request);
        }
        if (handler == null) {
            Object rawHandler = null;
            if (StringUtils.matchesCharacter((String)lookupPath, (char)'/')) {
                rawHandler = this.getRootHandler();
            }
            if (rawHandler == null) {
                rawHandler = this.getDefaultHandler();
            }
            if (rawHandler != null) {
                if (rawHandler instanceof String) {
                    String handlerName = (String)rawHandler;
                    rawHandler = this.obtainApplicationContext().getBean(handlerName);
                }
                this.validateHandler(rawHandler, request);
                handler = this.buildPathExposingHandler(rawHandler, lookupPath, lookupPath, null);
            }
        }
        return handler;
    }

    @Nullable
    protected Object lookupHandler(RequestPath path, String lookupPath, HttpServletRequest request) throws Exception {
        PathPattern pattern;
        Object handler = this.getDirectMatch(lookupPath, request);
        if (handler != null) {
            return handler;
        }
        List matches = null;
        for (PathPattern pattern2 : this.pathPatternHandlerMap.keySet()) {
            if (!pattern2.matches(path.pathWithinApplication())) continue;
            matches = matches != null ? matches : new ArrayList();
            matches.add(pattern2);
        }
        if (matches == null) {
            return null;
        }
        if (matches.size() > 1) {
            matches.sort(PathPattern.SPECIFICITY_COMPARATOR);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Matching patterns " + matches));
            }
        }
        if ((handler = this.pathPatternHandlerMap.get(pattern = (PathPattern)matches.get(0))) instanceof String) {
            String handlerName = (String)handler;
            handler = this.obtainApplicationContext().getBean(handlerName);
        }
        this.validateHandler(handler, request);
        String pathWithinMapping = pattern.extractPathWithinPattern(path.pathWithinApplication()).value();
        pathWithinMapping = UrlPathHelper.defaultInstance.removeSemicolonContent(pathWithinMapping);
        return this.buildPathExposingHandler(handler, pattern.getPatternString(), pathWithinMapping, null);
    }

    @Nullable
    protected Object lookupHandler(String lookupPath, HttpServletRequest request) throws Exception {
        Object handler = this.getDirectMatch(lookupPath, request);
        if (handler != null) {
            return handler;
        }
        ArrayList<String> matchingPatterns = new ArrayList<String>();
        for (String registeredPattern : this.handlerMap.keySet()) {
            if (this.getPathMatcher().match(registeredPattern, lookupPath)) {
                matchingPatterns.add(registeredPattern);
                continue;
            }
            if (!this.useTrailingSlashMatch() || registeredPattern.endsWith("/") || !this.getPathMatcher().match(registeredPattern + "/", lookupPath)) continue;
            matchingPatterns.add(registeredPattern + "/");
        }
        String bestMatch = null;
        Comparator patternComparator = this.getPathMatcher().getPatternComparator(lookupPath);
        if (!matchingPatterns.isEmpty()) {
            matchingPatterns.sort(patternComparator);
            if (this.logger.isTraceEnabled() && matchingPatterns.size() > 1) {
                this.logger.trace((Object)("Matching patterns " + matchingPatterns));
            }
            bestMatch = (String)matchingPatterns.get(0);
        }
        if (bestMatch != null) {
            handler = this.handlerMap.get(bestMatch);
            if (handler == null) {
                if (bestMatch.endsWith("/")) {
                    handler = this.handlerMap.get(bestMatch.substring(0, bestMatch.length() - 1));
                }
                if (handler == null) {
                    throw new IllegalStateException("Could not find handler for best pattern match [" + bestMatch + "]");
                }
            }
            if (handler instanceof String) {
                String handlerName = (String)handler;
                handler = this.obtainApplicationContext().getBean(handlerName);
            }
            this.validateHandler(handler, request);
            String pathWithinMapping = this.getPathMatcher().extractPathWithinPattern(bestMatch, lookupPath);
            LinkedHashMap<String, String> uriTemplateVariables = new LinkedHashMap<String, String>();
            for (String matchingPattern : matchingPatterns) {
                if (patternComparator.compare(bestMatch, matchingPattern) != 0) continue;
                Map vars = this.getPathMatcher().extractUriTemplateVariables(matchingPattern, lookupPath);
                Map decodedVars = this.getUrlPathHelper().decodePathVariables(request, vars);
                uriTemplateVariables.putAll(decodedVars);
            }
            if (this.logger.isTraceEnabled() && uriTemplateVariables.size() > 0) {
                this.logger.trace((Object)("URI variables " + uriTemplateVariables));
            }
            return this.buildPathExposingHandler(handler, bestMatch, pathWithinMapping, uriTemplateVariables);
        }
        return null;
    }

    @Nullable
    private Object getDirectMatch(String urlPath, HttpServletRequest request) throws Exception {
        Object handler = this.handlerMap.get(urlPath);
        if (handler != null) {
            if (handler instanceof String) {
                String handlerName = (String)handler;
                handler = this.obtainApplicationContext().getBean(handlerName);
            }
            this.validateHandler(handler, request);
            return this.buildPathExposingHandler(handler, urlPath, urlPath, null);
        }
        return null;
    }

    protected void validateHandler(Object handler, HttpServletRequest request) throws Exception {
    }

    protected Object buildPathExposingHandler(Object rawHandler, String bestMatchingPattern, String pathWithinMapping, @Nullable Map<String, String> uriTemplateVariables) {
        HandlerExecutionChain chain = new HandlerExecutionChain(rawHandler);
        chain.addInterceptor(new PathExposingHandlerInterceptor(bestMatchingPattern, pathWithinMapping));
        if (!CollectionUtils.isEmpty(uriTemplateVariables)) {
            chain.addInterceptor(new UriTemplateVariablesHandlerInterceptor(uriTemplateVariables));
        }
        return chain;
    }

    protected void exposePathWithinMapping(String bestMatchingPattern, String pathWithinMapping, HttpServletRequest request) {
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, (Object)bestMatchingPattern);
        request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, (Object)pathWithinMapping);
    }

    protected void exposeUriTemplateVariables(Map<String, String> uriTemplateVariables, HttpServletRequest request) {
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
    }

    @Override
    @Nullable
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        Assert.isNull((Object)this.getPatternParser(), (String)"This HandlerMapping uses PathPatterns.");
        String lookupPath = UrlPathHelper.getResolvedLookupPath((ServletRequest)request);
        if (this.getPathMatcher().match(pattern, lookupPath)) {
            return new RequestMatchResult(pattern, lookupPath, this.getPathMatcher());
        }
        if (this.useTrailingSlashMatch() && !pattern.endsWith("/") && this.getPathMatcher().match(pattern + "/", lookupPath)) {
            return new RequestMatchResult(pattern + "/", lookupPath, this.getPathMatcher());
        }
        return null;
    }

    protected void registerHandler(String[] urlPaths, String beanName) throws BeansException, IllegalStateException {
        Assert.notNull((Object)urlPaths, (String)"URL path array must not be null");
        for (String urlPath : urlPaths) {
            this.registerHandler(urlPath, (Object)beanName);
        }
    }

    protected void registerHandler(String urlPath, Object handler) throws BeansException, IllegalStateException {
        Object mappedHandler;
        Assert.notNull((Object)urlPath, (String)"URL path must not be null");
        Assert.notNull((Object)handler, (String)"Handler object must not be null");
        Object resolvedHandler = handler;
        if (!this.lazyInitHandlers && handler instanceof String) {
            String handlerName = (String)handler;
            ApplicationContext applicationContext = this.obtainApplicationContext();
            if (applicationContext.isSingleton(handlerName)) {
                resolvedHandler = applicationContext.getBean(handlerName);
            }
        }
        if ((mappedHandler = this.handlerMap.get(urlPath)) != null) {
            if (mappedHandler != resolvedHandler) {
                throw new IllegalStateException("Cannot map " + this.getHandlerDescription(handler) + " to URL path [" + urlPath + "]: There is already " + this.getHandlerDescription(mappedHandler) + " mapped.");
            }
        } else if (urlPath.equals("/")) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Root mapping to " + this.getHandlerDescription(handler)));
            }
            this.setRootHandler(resolvedHandler);
        } else if (urlPath.equals("/*")) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Default mapping to " + this.getHandlerDescription(handler)));
            }
            this.setDefaultHandler(resolvedHandler);
        } else {
            this.handlerMap.put(urlPath, resolvedHandler);
            if (this.getPatternParser() != null) {
                this.pathPatternHandlerMap.put(this.getPatternParser().parse(urlPath), resolvedHandler);
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Mapped [" + urlPath + "] onto " + this.getHandlerDescription(handler)));
            }
        }
    }

    private String getHandlerDescription(Object handler) {
        return handler instanceof String ? "'" + handler + "'" : handler.toString();
    }

    public final Map<String, Object> getHandlerMap() {
        return Collections.unmodifiableMap(this.handlerMap);
    }

    public final Map<PathPattern, Object> getPathPatternHandlerMap() {
        return this.pathPatternHandlerMap.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.pathPatternHandlerMap);
    }

    protected boolean supportsTypeLevelMappings() {
        return false;
    }

    private class UriTemplateVariablesHandlerInterceptor
    implements HandlerInterceptor {
        private final Map<String, String> uriTemplateVariables;

        public UriTemplateVariablesHandlerInterceptor(Map<String, String> uriTemplateVariables) {
            this.uriTemplateVariables = uriTemplateVariables;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            AbstractUrlHandlerMapping.this.exposeUriTemplateVariables(this.uriTemplateVariables, request);
            return true;
        }
    }

    private class PathExposingHandlerInterceptor
    implements HandlerInterceptor {
        private final String bestMatchingPattern;
        private final String pathWithinMapping;

        public PathExposingHandlerInterceptor(String bestMatchingPattern, String pathWithinMapping) {
            this.bestMatchingPattern = bestMatchingPattern;
            this.pathWithinMapping = pathWithinMapping;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            AbstractUrlHandlerMapping.this.exposePathWithinMapping(this.bestMatchingPattern, this.pathWithinMapping, request);
            request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, handler);
            request.setAttribute(HandlerMapping.INTROSPECT_TYPE_LEVEL_MAPPING, (Object)AbstractUrlHandlerMapping.this.supportsTypeLevelMappings());
            return true;
        }
    }
}

