/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.http.server.RequestPath
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.cors.CorsConfigurationSource
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.UrlPathHelper
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.PathPatternMatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

public class HandlerMappingIntrospector
implements CorsConfigurationSource,
ApplicationContextAware,
InitializingBean {
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private List<HandlerMapping> handlerMappings;
    private Map<HandlerMapping, PathPatternMatchableHandlerMapping> pathPatternMappings = Collections.emptyMap();

    public HandlerMappingIntrospector() {
    }

    @Deprecated
    public HandlerMappingIntrospector(ApplicationContext context) {
        this.handlerMappings = HandlerMappingIntrospector.initHandlerMappings(context);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() {
        if (this.handlerMappings == null) {
            Assert.notNull((Object)this.applicationContext, (String)"No ApplicationContext");
            this.handlerMappings = HandlerMappingIntrospector.initHandlerMappings(this.applicationContext);
            this.pathPatternMappings = this.handlerMappings.stream().filter(m -> m instanceof MatchableHandlerMapping && ((MatchableHandlerMapping)m).getPatternParser() != null).map(mapping -> (MatchableHandlerMapping)mapping).collect(Collectors.toMap(mapping -> mapping, PathPatternMatchableHandlerMapping::new));
        }
    }

    private static List<HandlerMapping> initHandlerMappings(ApplicationContext context) {
        Map beans = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)context, HandlerMapping.class, (boolean)true, (boolean)false);
        if (!beans.isEmpty()) {
            ArrayList mappings = new ArrayList(beans.values());
            AnnotationAwareOrderComparator.sort(mappings);
            return Collections.unmodifiableList(mappings);
        }
        return Collections.unmodifiableList(HandlerMappingIntrospector.initFallback(context));
    }

    private static List<HandlerMapping> initFallback(ApplicationContext applicationContext) {
        Properties properties;
        try {
            ClassPathResource resource = new ClassPathResource("DispatcherServlet.properties", DispatcherServlet.class);
            properties = PropertiesLoaderUtils.loadProperties((Resource)resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load DispatcherServlet.properties: " + ex.getMessage());
        }
        String value = properties.getProperty(HandlerMapping.class.getName());
        String[] names = StringUtils.commaDelimitedListToStringArray((String)value);
        ArrayList<HandlerMapping> result = new ArrayList<HandlerMapping>(names.length);
        for (String name : names) {
            try {
                Class clazz = ClassUtils.forName((String)name, (ClassLoader)DispatcherServlet.class.getClassLoader());
                Object mapping = applicationContext.getAutowireCapableBeanFactory().createBean(clazz);
                result.add((HandlerMapping)mapping);
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Could not find default HandlerMapping [" + name + "]");
            }
        }
        return result;
    }

    public List<HandlerMapping> getHandlerMappings() {
        return this.handlerMappings != null ? this.handlerMappings : Collections.emptyList();
    }

    @Nullable
    public MatchableHandlerMapping getMatchableHandlerMapping(HttpServletRequest request) throws Exception {
        AttributesPreservingRequest wrappedRequest = new AttributesPreservingRequest(request);
        return this.doWithHandlerMapping((HttpServletRequest)wrappedRequest, false, (arg_0, arg_1) -> this.lambda$getMatchableHandlerMapping$3((HttpServletRequest)wrappedRequest, arg_0, arg_1));
    }

    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        AttributesPreservingRequest wrappedRequest = new AttributesPreservingRequest(request);
        return this.doWithHandlerMappingIgnoringException((HttpServletRequest)wrappedRequest, (handlerMapping, executionChain) -> {
            for (HandlerInterceptor interceptor : executionChain.getInterceptorList()) {
                if (!(interceptor instanceof CorsConfigurationSource)) continue;
                return ((CorsConfigurationSource)interceptor).getCorsConfiguration((HttpServletRequest)wrappedRequest);
            }
            if (executionChain.getHandler() instanceof CorsConfigurationSource) {
                return ((CorsConfigurationSource)executionChain.getHandler()).getCorsConfiguration((HttpServletRequest)wrappedRequest);
            }
            return null;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private <T> T doWithHandlerMapping(HttpServletRequest request, boolean ignoreException, BiFunction<HandlerMapping, HandlerExecutionChain, T> extractor) throws Exception {
        Assert.state((this.handlerMappings != null ? 1 : 0) != 0, (String)"HandlerMapping's not initialized");
        boolean parsePath = !this.pathPatternMappings.isEmpty();
        RequestPath previousPath = null;
        if (parsePath) {
            previousPath = (RequestPath)request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
            ServletRequestPathUtils.parseAndCache((HttpServletRequest)request);
        }
        try {
            for (HandlerMapping handlerMapping : this.handlerMappings) {
                HandlerExecutionChain chain;
                block8: {
                    chain = null;
                    try {
                        chain = handlerMapping.getHandler(request);
                    }
                    catch (Exception ex) {
                        if (ignoreException) break block8;
                        throw ex;
                    }
                }
                if (chain == null) continue;
                T t = extractor.apply(handlerMapping, chain);
                return t;
            }
        }
        finally {
            if (parsePath) {
                ServletRequestPathUtils.setParsedRequestPath((RequestPath)previousPath, (ServletRequest)request);
            }
        }
        return null;
    }

    @Nullable
    private <T> T doWithHandlerMappingIgnoringException(HttpServletRequest request, BiFunction<HandlerMapping, HandlerExecutionChain, T> matchHandler) {
        try {
            return this.doWithHandlerMapping(request, true, matchHandler);
        }
        catch (Exception ex) {
            throw new IllegalStateException("HandlerMapping exception not suppressed", ex);
        }
    }

    private /* synthetic */ LookupPathMatchableHandlerMapping lambda$getMatchableHandlerMapping$3(HttpServletRequest wrappedRequest, HandlerMapping mapping, HandlerExecutionChain executionChain) {
        if (mapping instanceof MatchableHandlerMapping) {
            PathPatternMatchableHandlerMapping pathPatternMapping = this.pathPatternMappings.get(mapping);
            if (pathPatternMapping != null) {
                RequestPath requestPath = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)wrappedRequest);
                return new LookupPathMatchableHandlerMapping(pathPatternMapping, requestPath);
            }
            String lookupPath = (String)wrappedRequest.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
            return new LookupPathMatchableHandlerMapping((MatchableHandlerMapping)mapping, lookupPath);
        }
        throw new IllegalStateException("HandlerMapping is not a MatchableHandlerMapping");
    }

    private static class LookupPathMatchableHandlerMapping
    implements MatchableHandlerMapping {
        private final MatchableHandlerMapping delegate;
        private final Object lookupPath;
        private final String pathAttributeName;

        LookupPathMatchableHandlerMapping(MatchableHandlerMapping delegate, Object lookupPath) {
            this.delegate = delegate;
            this.lookupPath = lookupPath;
            this.pathAttributeName = lookupPath instanceof RequestPath ? ServletRequestPathUtils.PATH_ATTRIBUTE : UrlPathHelper.PATH_ATTRIBUTE;
        }

        @Override
        public PathPatternParser getPatternParser() {
            return this.delegate.getPatternParser();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public RequestMatchResult match(HttpServletRequest request, String pattern) {
            pattern = this.initFullPathPattern(pattern);
            Object previousPath = request.getAttribute(this.pathAttributeName);
            request.setAttribute(this.pathAttributeName, this.lookupPath);
            try {
                RequestMatchResult requestMatchResult = this.delegate.match(request, pattern);
                return requestMatchResult;
            }
            finally {
                request.setAttribute(this.pathAttributeName, previousPath);
            }
        }

        private String initFullPathPattern(String pattern) {
            PathPatternParser parser = this.getPatternParser() != null ? this.getPatternParser() : PathPatternParser.defaultInstance;
            return parser.initFullPathPattern(pattern);
        }

        @Override
        @Nullable
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            return this.delegate.getHandler(request);
        }
    }

    private static class AttributesPreservingRequest
    extends HttpServletRequestWrapper {
        private final Map<String, Object> attributes;

        AttributesPreservingRequest(HttpServletRequest request) {
            super(request);
            this.attributes = this.initAttributes(request);
        }

        private Map<String, Object> initAttributes(HttpServletRequest request) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            Enumeration names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                map.put(name, request.getAttribute(name));
            }
            return map;
        }

        public void setAttribute(String name, Object value) {
            this.attributes.put(name, value);
        }

        public Object getAttribute(String name) {
            return this.attributes.get(name);
        }

        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(this.attributes.keySet());
        }

        public void removeAttribute(String name) {
            this.attributes.remove(name);
        }
    }
}

