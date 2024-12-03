/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 */
package org.springframework.web.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class AbstractHandlerMapping
extends WebApplicationObjectSupport
implements HandlerMapping,
Ordered,
BeanNameAware {
    protected final Log mappingsLogger = LogDelegateFactory.getHiddenLog(HandlerMapping.class.getName() + ".Mappings");
    @Nullable
    private Object defaultHandler;
    @Nullable
    private PathPatternParser patternParser;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private final List<Object> interceptors = new ArrayList<Object>();
    private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList<HandlerInterceptor>();
    @Nullable
    private CorsConfigurationSource corsConfigurationSource;
    private CorsProcessor corsProcessor = new DefaultCorsProcessor();
    private int order = Integer.MAX_VALUE;
    @Nullable
    private String beanName;

    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }

    public void setPatternParser(PathPatternParser patternParser) {
        this.patternParser = patternParser;
    }

    @Nullable
    public PathPatternParser getPatternParser() {
        return this.patternParser;
    }

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)this.corsConfigurationSource).setAlwaysUseFullPath(alwaysUseFullPath);
        }
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)this.corsConfigurationSource).setUrlDecode(urlDecode);
        }
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)this.corsConfigurationSource).setRemoveSemicolonContent(removeSemicolonContent);
        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull((Object)urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)this.corsConfigurationSource).setUrlPathHelper(urlPathHelper);
        }
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull((Object)pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)this.corsConfigurationSource).setPathMatcher(pathMatcher);
        }
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public void setInterceptors(Object ... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
        UrlBasedCorsConfigurationSource source;
        if (CollectionUtils.isEmpty(corsConfigurations)) {
            this.corsConfigurationSource = null;
            return;
        }
        if (this.getPatternParser() != null) {
            source = new UrlBasedCorsConfigurationSource(this.getPatternParser());
            source.setCorsConfigurations(corsConfigurations);
        } else {
            source = new UrlBasedCorsConfigurationSource();
            source.setCorsConfigurations(corsConfigurations);
            source.setPathMatcher(this.pathMatcher);
            source.setUrlPathHelper(this.urlPathHelper);
        }
        this.setCorsConfigurationSource(source);
    }

    public void setCorsConfigurationSource(CorsConfigurationSource source) {
        Assert.notNull((Object)source, "CorsConfigurationSource must not be null");
        this.corsConfigurationSource = source;
        if (source instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource)source).setAllowInitLookupPath(false);
        }
    }

    @Nullable
    public CorsConfigurationSource getCorsConfigurationSource() {
        return this.corsConfigurationSource;
    }

    public void setCorsProcessor(CorsProcessor corsProcessor) {
        Assert.notNull((Object)corsProcessor, "CorsProcessor must not be null");
        this.corsProcessor = corsProcessor;
    }

    public CorsProcessor getCorsProcessor() {
        return this.corsProcessor;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    protected String formatMappingName() {
        return this.beanName != null ? "'" + this.beanName + "'" : this.getClass().getName();
    }

    @Override
    protected void initApplicationContext() throws BeansException {
        this.extendInterceptors(this.interceptors);
        this.detectMappedInterceptors(this.adaptedInterceptors);
        this.initInterceptors();
    }

    protected void extendInterceptors(List<Object> interceptors) {
    }

    protected void detectMappedInterceptors(List<HandlerInterceptor> mappedInterceptors) {
        mappedInterceptors.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(this.obtainApplicationContext(), MappedInterceptor.class, true, false).values());
    }

    protected void initInterceptors() {
        if (!this.interceptors.isEmpty()) {
            for (int i2 = 0; i2 < this.interceptors.size(); ++i2) {
                Object interceptor = this.interceptors.get(i2);
                if (interceptor == null) {
                    throw new IllegalArgumentException("Entry number " + i2 + " in interceptors array is null");
                }
                this.adaptedInterceptors.add(this.adaptInterceptor(interceptor));
            }
        }
    }

    protected HandlerInterceptor adaptInterceptor(Object interceptor) {
        if (interceptor instanceof HandlerInterceptor) {
            return (HandlerInterceptor)interceptor;
        }
        if (interceptor instanceof WebRequestInterceptor) {
            return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor)interceptor);
        }
        throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
    }

    @Nullable
    protected final HandlerInterceptor[] getAdaptedInterceptors() {
        return !this.adaptedInterceptors.isEmpty() ? this.adaptedInterceptors.toArray(new HandlerInterceptor[0]) : null;
    }

    @Nullable
    protected final MappedInterceptor[] getMappedInterceptors() {
        ArrayList<MappedInterceptor> mappedInterceptors = new ArrayList<MappedInterceptor>(this.adaptedInterceptors.size());
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (!(interceptor instanceof MappedInterceptor)) continue;
            mappedInterceptors.add((MappedInterceptor)interceptor);
        }
        return !mappedInterceptors.isEmpty() ? mappedInterceptors.toArray(new MappedInterceptor[0]) : null;
    }

    @Override
    public boolean usesPathPatterns() {
        return this.getPatternParser() != null;
    }

    @Override
    @Nullable
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = this.getHandlerInternal(request);
        if (handler == null) {
            handler = this.getDefaultHandler();
        }
        if (handler == null) {
            return null;
        }
        if (handler instanceof String) {
            String handlerName = (String)handler;
            handler = this.obtainApplicationContext().getBean(handlerName);
        }
        if (!ServletRequestPathUtils.hasCachedPath((ServletRequest)request)) {
            this.initLookupPath(request);
        }
        HandlerExecutionChain executionChain = this.getHandlerExecutionChain(handler, request);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Mapped to " + handler));
        } else if (this.logger.isDebugEnabled() && !DispatcherType.ASYNC.equals((Object)request.getDispatcherType())) {
            this.logger.debug((Object)("Mapped to " + executionChain.getHandler()));
        }
        if (this.hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
            CorsConfiguration config = this.getCorsConfiguration(handler, request);
            if (this.getCorsConfigurationSource() != null) {
                CorsConfiguration globalConfig = this.getCorsConfigurationSource().getCorsConfiguration(request);
                CorsConfiguration corsConfiguration = config = globalConfig != null ? globalConfig.combine(config) : config;
            }
            if (config != null) {
                config.validateAllowCredentials();
            }
            executionChain = this.getCorsHandlerExecutionChain(request, executionChain, config);
        }
        return executionChain;
    }

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest var1) throws Exception;

    protected String initLookupPath(HttpServletRequest request) {
        if (this.usesPathPatterns()) {
            request.removeAttribute(UrlPathHelper.PATH_ATTRIBUTE);
            RequestPath requestPath = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)request);
            String lookupPath = requestPath.pathWithinApplication().value();
            return UrlPathHelper.defaultInstance.removeSemicolonContent(lookupPath);
        }
        return this.getUrlPathHelper().resolveAndCacheLookupPath(request);
    }

    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = handler instanceof HandlerExecutionChain ? (HandlerExecutionChain)handler : new HandlerExecutionChain(handler);
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor)interceptor;
                if (!mappedInterceptor.matches(request)) continue;
                chain.addInterceptor(mappedInterceptor.getInterceptor());
                continue;
            }
            chain.addInterceptor(interceptor);
        }
        return chain;
    }

    protected boolean hasCorsConfigurationSource(Object handler) {
        if (handler instanceof HandlerExecutionChain) {
            handler = ((HandlerExecutionChain)handler).getHandler();
        }
        return handler instanceof CorsConfigurationSource || this.corsConfigurationSource != null;
    }

    @Nullable
    protected CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        Object resolvedHandler = handler;
        if (handler instanceof HandlerExecutionChain) {
            resolvedHandler = ((HandlerExecutionChain)handler).getHandler();
        }
        if (resolvedHandler instanceof CorsConfigurationSource) {
            return ((CorsConfigurationSource)resolvedHandler).getCorsConfiguration(request);
        }
        return null;
    }

    protected HandlerExecutionChain getCorsHandlerExecutionChain(HttpServletRequest request, HandlerExecutionChain chain, @Nullable CorsConfiguration config) {
        if (CorsUtils.isPreFlightRequest(request)) {
            HandlerInterceptor[] interceptors = chain.getInterceptors();
            return new HandlerExecutionChain((Object)new PreFlightHandler(config), interceptors);
        }
        chain.addInterceptor(0, new CorsInterceptor(config));
        return chain;
    }

    private class CorsInterceptor
    implements HandlerInterceptor,
    CorsConfigurationSource {
        @Nullable
        private final CorsConfiguration config;

        public CorsInterceptor(CorsConfiguration config) {
            this.config = config;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
            if (asyncManager.hasConcurrentResult()) {
                return true;
            }
            return AbstractHandlerMapping.this.corsProcessor.processRequest(this.config, request, response);
        }

        @Override
        @Nullable
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            return this.config;
        }
    }

    private class PreFlightHandler
    implements HttpRequestHandler,
    CorsConfigurationSource {
        @Nullable
        private final CorsConfiguration config;

        public PreFlightHandler(CorsConfiguration config) {
            this.config = config;
        }

        @Override
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
            AbstractHandlerMapping.this.corsProcessor.processRequest(this.config, request, response);
        }

        @Override
        @Nullable
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            return this.config;
        }
    }
}

