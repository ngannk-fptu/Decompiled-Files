/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.PathMatcher
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultResourceResolverChain;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

public class ResourceUrlProvider
implements ApplicationListener<ContextRefreshedEvent>,
ApplicationContextAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ApplicationContext applicationContext;
    private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;
    private PathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, ResourceHttpRequestHandler> handlerMap = new LinkedHashMap<String, ResourceHttpRequestHandler>();
    private boolean autodetect = true;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public void setHandlerMap(@Nullable Map<String, ResourceHttpRequestHandler> handlerMap) {
        if (handlerMap != null) {
            this.handlerMap.clear();
            this.handlerMap.putAll(handlerMap);
            this.autodetect = false;
        }
    }

    public Map<String, ResourceHttpRequestHandler> getHandlerMap() {
        return this.handlerMap;
    }

    public boolean isAutodetect() {
        return this.autodetect;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext && this.isAutodetect()) {
            this.handlerMap.clear();
            this.detectResourceHandlers(this.applicationContext);
            if (!this.handlerMap.isEmpty()) {
                this.autodetect = false;
            }
        }
    }

    protected void detectResourceHandlers(ApplicationContext appContext) {
        Map beans = appContext.getBeansOfType(SimpleUrlHandlerMapping.class);
        ArrayList mappings = new ArrayList(beans.values());
        AnnotationAwareOrderComparator.sort(mappings);
        for (SimpleUrlHandlerMapping mapping : mappings) {
            for (String pattern : mapping.getHandlerMap().keySet()) {
                Object handler = mapping.getHandlerMap().get(pattern);
                if (!(handler instanceof ResourceHttpRequestHandler)) continue;
                ResourceHttpRequestHandler resourceHandler = (ResourceHttpRequestHandler)((Object)handler);
                this.handlerMap.put(pattern, resourceHandler);
            }
        }
        if (this.handlerMap.isEmpty()) {
            this.logger.trace((Object)"No resource handling mappings found");
        }
    }

    @Nullable
    public final String getForRequestUrl(HttpServletRequest request, String requestUrl) {
        int suffixIndex;
        int prefixIndex = this.getLookupPathIndex(request);
        if (prefixIndex >= (suffixIndex = this.getEndPathIndex(requestUrl))) {
            return null;
        }
        String prefix = requestUrl.substring(0, prefixIndex);
        String suffix = requestUrl.substring(suffixIndex);
        String lookupPath = requestUrl.substring(prefixIndex, suffixIndex);
        String resolvedLookupPath = this.getForLookupPath(lookupPath);
        return resolvedLookupPath != null ? prefix + resolvedLookupPath + suffix : null;
    }

    private int getLookupPathIndex(HttpServletRequest request) {
        UrlPathHelper pathHelper = this.getUrlPathHelper();
        if (request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE) == null) {
            pathHelper.resolveAndCacheLookupPath(request);
        }
        String requestUri = pathHelper.getRequestUri(request);
        String lookupPath = UrlPathHelper.getResolvedLookupPath((ServletRequest)request);
        return requestUri.indexOf(lookupPath);
    }

    private int getEndPathIndex(String lookupPath) {
        int hashIndex;
        int suffixIndex = lookupPath.length();
        int queryIndex = lookupPath.indexOf(63);
        if (queryIndex > 0) {
            suffixIndex = queryIndex;
        }
        if ((hashIndex = lookupPath.indexOf(35)) > 0) {
            suffixIndex = Math.min(suffixIndex, hashIndex);
        }
        return suffixIndex;
    }

    @Nullable
    public final String getForLookupPath(String lookupPath) {
        String previous;
        do {
            previous = lookupPath;
        } while (!(lookupPath = StringUtils.replace((String)lookupPath, (String)"//", (String)"/")).equals(previous));
        ArrayList<String> matchingPatterns = new ArrayList<String>();
        for (String pattern : this.handlerMap.keySet()) {
            if (!this.getPathMatcher().match(pattern, lookupPath)) continue;
            matchingPatterns.add(pattern);
        }
        if (!matchingPatterns.isEmpty()) {
            Comparator patternComparator = this.getPathMatcher().getPatternComparator(lookupPath);
            matchingPatterns.sort(patternComparator);
            for (String pattern : matchingPatterns) {
                String pathWithinMapping = this.getPathMatcher().extractPathWithinPattern(pattern, lookupPath);
                String pathMapping = lookupPath.substring(0, lookupPath.indexOf(pathWithinMapping));
                ResourceHttpRequestHandler handler = this.handlerMap.get(pattern);
                DefaultResourceResolverChain chain = new DefaultResourceResolverChain(handler.getResourceResolvers());
                String resolved = chain.resolveUrlPath(pathWithinMapping, handler.getLocations());
                if (resolved == null) continue;
                return pathMapping + resolved;
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("No match for \"" + lookupPath + "\""));
        }
        return null;
    }
}

