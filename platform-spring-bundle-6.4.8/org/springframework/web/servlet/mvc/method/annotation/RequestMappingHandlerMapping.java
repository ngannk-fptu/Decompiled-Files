/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

public class RequestMappingHandlerMapping
extends RequestMappingInfoHandlerMapping
implements MatchableHandlerMapping,
EmbeddedValueResolverAware {
    private boolean useSuffixPatternMatch = false;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = true;
    private Map<String, Predicate<Class<?>>> pathPrefixes = Collections.emptyMap();
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    @Nullable
    private StringValueResolver embeddedValueResolver;
    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Deprecated
    public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
        this.useSuffixPatternMatch = useSuffixPatternMatch;
    }

    @Deprecated
    public void setUseRegisteredSuffixPatternMatch(boolean useRegisteredSuffixPatternMatch) {
        this.useRegisteredSuffixPatternMatch = useRegisteredSuffixPatternMatch;
        this.useSuffixPatternMatch = useRegisteredSuffixPatternMatch || this.useSuffixPatternMatch;
    }

    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        if (this.getPatternParser() != null) {
            this.getPatternParser().setMatchOptionalTrailingSeparator(useTrailingSlashMatch);
        }
    }

    public void setPathPrefixes(Map<String, Predicate<Class<?>>> prefixes) {
        this.pathPrefixes = !prefixes.isEmpty() ? Collections.unmodifiableMap(new LinkedHashMap(prefixes)) : Collections.emptyMap();
    }

    public Map<String, Predicate<Class<?>>> getPathPrefixes() {
        return this.pathPrefixes;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        Assert.notNull((Object)contentNegotiationManager, "ContentNegotiationManager must not be null");
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch());
        this.config.setContentNegotiationManager(this.getContentNegotiationManager());
        if (this.getPatternParser() != null) {
            this.config.setPatternParser(this.getPatternParser());
            Assert.isTrue(!this.useSuffixPatternMatch && !this.useRegisteredSuffixPatternMatch, "Suffix pattern matching not supported with PathPatternParser.");
        } else {
            this.config.setSuffixPatternMatch(this.useSuffixPatternMatch());
            this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch());
            this.config.setPathMatcher(this.getPathMatcher());
        }
        super.afterPropertiesSet();
    }

    @Deprecated
    public boolean useSuffixPatternMatch() {
        return this.useSuffixPatternMatch;
    }

    @Deprecated
    public boolean useRegisteredSuffixPatternMatch() {
        return this.useRegisteredSuffixPatternMatch;
    }

    public boolean useTrailingSlashMatch() {
        return this.useTrailingSlashMatch;
    }

    @Nullable
    @Deprecated
    public List<String> getFileExtensions() {
        return this.config.getFileExtensions();
    }

    public RequestMappingInfo.BuilderConfiguration getBuilderConfiguration() {
        return this.config;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class);
    }

    @Override
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.createRequestMappingInfo(method);
        if (info != null) {
            String prefix;
            RequestMappingInfo typeInfo = this.createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            if ((prefix = this.getPathPrefix(handlerType)) != null) {
                info = RequestMappingInfo.paths(prefix).options(this.config).build().combine(info);
            }
        }
        return info;
    }

    @Nullable
    String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : this.pathPrefixes.entrySet()) {
            if (!entry.getValue().test(handlerType)) continue;
            String prefix = entry.getKey();
            if (this.embeddedValueResolver != null) {
                prefix = this.embeddedValueResolver.resolveStringValue(prefix);
            }
            return prefix;
        }
        return null;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class)element) : this.getCustomMethodCondition((Method)element);
        return requestMapping != null ? this.createRequestMappingInfo(requestMapping, condition) : null;
    }

    @Nullable
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }

    protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(this.resolveEmbeddedValuesInPatterns(requestMapping.path())).methods(requestMapping.method()).params(requestMapping.params()).headers(requestMapping.headers()).consumes(requestMapping.consumes()).produces(requestMapping.produces()).mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }

    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        if (this.embeddedValueResolver == null) {
            return patterns;
        }
        String[] resolvedPatterns = new String[patterns.length];
        for (int i2 = 0; i2 < patterns.length; ++i2) {
            resolvedPatterns[i2] = this.embeddedValueResolver.resolveStringValue(patterns[i2]);
        }
        return resolvedPatterns;
    }

    @Override
    public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
        super.registerMapping(mapping, handler, method);
        this.updateConsumesCondition(mapping, method);
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        super.registerHandlerMethod(handler, method, mapping);
        this.updateConsumesCondition(mapping, method);
    }

    private void updateConsumesCondition(RequestMappingInfo info, Method method) {
        ConsumesRequestCondition condition = info.getConsumesCondition();
        if (!condition.isEmpty()) {
            for (Parameter parameter : method.getParameters()) {
                MergedAnnotation<RequestBody> annot = MergedAnnotations.from(parameter).get(RequestBody.class);
                if (!annot.isPresent()) continue;
                condition.setBodyRequired(annot.getBoolean("required"));
                break;
            }
        }
    }

    @Override
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        Assert.isNull((Object)this.getPatternParser(), "This HandlerMapping requires a PathPattern");
        RequestMappingInfo info = RequestMappingInfo.paths(pattern).options(this.config).build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        return match != null && match.getPatternsCondition() != null ? new RequestMatchResult(match.getPatternsCondition().getPatterns().iterator().next(), UrlPathHelper.getResolvedLookupPath((ServletRequest)request), this.getPathMatcher()) : null;
    }

    @Override
    protected CorsConfiguration initCorsConfiguration(Object handler, Method method, RequestMappingInfo mappingInfo) {
        HandlerMethod handlerMethod = this.createHandlerMethod(handler, method);
        Class<?> beanType = handlerMethod.getBeanType();
        CrossOrigin typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(beanType, CrossOrigin.class);
        CrossOrigin methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, CrossOrigin.class);
        if (typeAnnotation == null && methodAnnotation == null) {
            return null;
        }
        CorsConfiguration config = new CorsConfiguration();
        this.updateCorsConfig(config, typeAnnotation);
        this.updateCorsConfig(config, methodAnnotation);
        if (CollectionUtils.isEmpty(config.getAllowedMethods())) {
            for (RequestMethod allowedMethod : mappingInfo.getMethodsCondition().getMethods()) {
                config.addAllowedMethod(allowedMethod.name());
            }
        }
        return config.applyPermitDefaultValues();
    }

    private void updateCorsConfig(CorsConfiguration config, @Nullable CrossOrigin annotation) {
        if (annotation == null) {
            return;
        }
        for (String string : annotation.origins()) {
            config.addAllowedOrigin(this.resolveCorsAnnotationValue(string));
        }
        for (String string : annotation.originPatterns()) {
            config.addAllowedOriginPattern(this.resolveCorsAnnotationValue(string));
        }
        for (RequestMethod requestMethod : annotation.methods()) {
            config.addAllowedMethod(requestMethod.name());
        }
        for (String string : annotation.allowedHeaders()) {
            config.addAllowedHeader(this.resolveCorsAnnotationValue(string));
        }
        for (String string : annotation.exposedHeaders()) {
            config.addExposedHeader(this.resolveCorsAnnotationValue(string));
        }
        String string = this.resolveCorsAnnotationValue(annotation.allowCredentials());
        if ("true".equalsIgnoreCase(string)) {
            config.setAllowCredentials(true);
        } else if ("false".equalsIgnoreCase(string)) {
            config.setAllowCredentials(false);
        } else if (!string.isEmpty()) {
            throw new IllegalStateException("@CrossOrigin's allowCredentials value must be \"true\", \"false\", or an empty string (\"\"): current value is [" + string + "]");
        }
        if (annotation.maxAge() >= 0L) {
            config.setMaxAge(annotation.maxAge());
        }
    }

    private String resolveCorsAnnotationValue(String value) {
        if (this.embeddedValueResolver != null) {
            String resolved = this.embeddedValueResolver.resolveStringValue(value);
            return resolved != null ? resolved : "";
        }
        return value;
    }
}

