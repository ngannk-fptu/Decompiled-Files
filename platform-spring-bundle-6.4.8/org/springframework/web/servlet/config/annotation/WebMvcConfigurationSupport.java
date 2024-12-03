/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  javax.servlet.ServletContext
 */
package org.springframework.web.servlet.config.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.SpringProperties;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

public class WebMvcConfigurationSupport
implements ApplicationContextAware,
ServletContextAware {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    private static final boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean kotlinSerializationJsonPresent;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private List<Object> interceptors;
    @Nullable
    private PathMatchConfigurer pathMatchConfigurer;
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    @Nullable
    private List<HandlerMethodArgumentResolver> argumentResolvers;
    @Nullable
    private List<HandlerMethodReturnValueHandler> returnValueHandlers;
    @Nullable
    private List<HttpMessageConverter<?>> messageConverters;
    @Nullable
    private Map<String, CorsConfiguration> corsConfigurations;
    @Nullable
    private AsyncSupportConfigurer asyncSupportConfigurer;

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Nullable
    public final ServletContext getServletContext() {
        return this.servletContext;
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping(@Qualifier(value="mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager, @Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        RequestMappingHandlerMapping mapping = this.createRequestMappingHandlerMapping();
        mapping.setOrder(0);
        mapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        mapping.setContentNegotiationManager(contentNegotiationManager);
        mapping.setCorsConfigurations(this.getCorsConfigurations());
        PathMatchConfigurer pathConfig = this.getPathMatchConfigurer();
        if (pathConfig.getPatternParser() != null) {
            mapping.setPatternParser(pathConfig.getPatternParser());
        } else {
            Boolean useRegisteredSuffixPatternMatch;
            mapping.setUrlPathHelper(pathConfig.getUrlPathHelperOrDefault());
            mapping.setPathMatcher(pathConfig.getPathMatcherOrDefault());
            Boolean useSuffixPatternMatch = pathConfig.isUseSuffixPatternMatch();
            if (useSuffixPatternMatch != null) {
                mapping.setUseSuffixPatternMatch(useSuffixPatternMatch);
            }
            if ((useRegisteredSuffixPatternMatch = pathConfig.isUseRegisteredSuffixPatternMatch()) != null) {
                mapping.setUseRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch);
            }
        }
        Boolean useTrailingSlashMatch = pathConfig.isUseTrailingSlashMatch();
        if (useTrailingSlashMatch != null) {
            mapping.setUseTrailingSlashMatch(useTrailingSlashMatch);
        }
        if (pathConfig.getPathPrefixes() != null) {
            mapping.setPathPrefixes(pathConfig.getPathPrefixes());
        }
        return mapping;
    }

    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    protected final Object[] getInterceptors(FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
        if (this.interceptors == null) {
            InterceptorRegistry registry = new InterceptorRegistry();
            this.addInterceptors(registry);
            registry.addInterceptor(new ConversionServiceExposingInterceptor(mvcConversionService));
            registry.addInterceptor(new ResourceUrlProviderExposingInterceptor(mvcResourceUrlProvider));
            this.interceptors = registry.getInterceptors();
        }
        return this.interceptors.toArray();
    }

    protected void addInterceptors(InterceptorRegistry registry) {
    }

    protected PathMatchConfigurer getPathMatchConfigurer() {
        if (this.pathMatchConfigurer == null) {
            this.pathMatchConfigurer = new PathMatchConfigurer();
            this.configurePathMatch(this.pathMatchConfigurer);
        }
        return this.pathMatchConfigurer;
    }

    protected void configurePathMatch(PathMatchConfigurer configurer) {
    }

    @Bean
    public PathPatternParser mvcPatternParser() {
        return this.getPathMatchConfigurer().getPatternParserOrDefault();
    }

    @Bean
    public UrlPathHelper mvcUrlPathHelper() {
        return this.getPathMatchConfigurer().getUrlPathHelperOrDefault();
    }

    @Bean
    public PathMatcher mvcPathMatcher() {
        return this.getPathMatchConfigurer().getPathMatcherOrDefault();
    }

    @Bean
    public ContentNegotiationManager mvcContentNegotiationManager() {
        if (this.contentNegotiationManager == null) {
            ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(this.servletContext);
            configurer.mediaTypes(this.getDefaultMediaTypes());
            this.configureContentNegotiation(configurer);
            this.contentNegotiationManager = configurer.buildContentNegotiationManager();
        }
        return this.contentNegotiationManager;
    }

    protected Map<String, MediaType> getDefaultMediaTypes() {
        HashMap<String, MediaType> map = new HashMap<String, MediaType>(4);
        if (romePresent) {
            map.put("atom", MediaType.APPLICATION_ATOM_XML);
            map.put("rss", MediaType.APPLICATION_RSS_XML);
        }
        if (!shouldIgnoreXml && (jaxb2Present || jackson2XmlPresent)) {
            map.put("xml", MediaType.APPLICATION_XML);
        }
        if (jackson2Present || gsonPresent || jsonbPresent) {
            map.put("json", MediaType.APPLICATION_JSON);
        }
        if (jackson2SmilePresent) {
            map.put("smile", MediaType.valueOf("application/x-jackson-smile"));
        }
        if (jackson2CborPresent) {
            map.put("cbor", MediaType.APPLICATION_CBOR);
        }
        return map;
    }

    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    @Bean
    @Nullable
    public HandlerMapping viewControllerHandlerMapping(@Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        ViewControllerRegistry registry = new ViewControllerRegistry(this.applicationContext);
        this.addViewControllers(registry);
        SimpleUrlHandlerMapping handlerMapping = registry.buildHandlerMapping();
        if (handlerMapping == null) {
            return null;
        }
        PathMatchConfigurer pathConfig = this.getPathMatchConfigurer();
        if (pathConfig.getPatternParser() != null) {
            ((AbstractHandlerMapping)handlerMapping).setPatternParser(pathConfig.getPatternParser());
        } else {
            handlerMapping.setUrlPathHelper(pathConfig.getUrlPathHelperOrDefault());
            handlerMapping.setPathMatcher(pathConfig.getPathMatcherOrDefault());
        }
        handlerMapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        handlerMapping.setCorsConfigurations(this.getCorsConfigurations());
        return handlerMapping;
    }

    protected void addViewControllers(ViewControllerRegistry registry) {
    }

    @Bean
    public BeanNameUrlHandlerMapping beanNameHandlerMapping(@Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();
        mapping.setOrder(2);
        PathMatchConfigurer pathConfig = this.getPathMatchConfigurer();
        if (pathConfig.getPatternParser() != null) {
            mapping.setPatternParser(pathConfig.getPatternParser());
        } else {
            mapping.setUrlPathHelper(pathConfig.getUrlPathHelperOrDefault());
            mapping.setPathMatcher(pathConfig.getPathMatcherOrDefault());
        }
        mapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        mapping.setCorsConfigurations(this.getCorsConfigurations());
        return mapping;
    }

    @Bean
    public RouterFunctionMapping routerFunctionMapping(@Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        RouterFunctionMapping mapping = new RouterFunctionMapping();
        mapping.setOrder(3);
        mapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        mapping.setCorsConfigurations(this.getCorsConfigurations());
        mapping.setMessageConverters(this.getMessageConverters());
        PathPatternParser patternParser = this.getPathMatchConfigurer().getPatternParser();
        if (patternParser != null) {
            mapping.setPatternParser(patternParser);
        }
        return mapping;
    }

    @Bean
    @Nullable
    public HandlerMapping resourceHandlerMapping(@Qualifier(value="mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager, @Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        Assert.state(this.applicationContext != null, "No ApplicationContext set");
        Assert.state(this.servletContext != null, "No ServletContext set");
        PathMatchConfigurer pathConfig = this.getPathMatchConfigurer();
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(this.applicationContext, this.servletContext, contentNegotiationManager, pathConfig.getUrlPathHelper());
        this.addResourceHandlers(registry);
        AbstractHandlerMapping handlerMapping = registry.getHandlerMapping();
        if (handlerMapping == null) {
            return null;
        }
        if (pathConfig.getPatternParser() != null) {
            handlerMapping.setPatternParser(pathConfig.getPatternParser());
        } else {
            handlerMapping.setUrlPathHelper(pathConfig.getUrlPathHelperOrDefault());
            handlerMapping.setPathMatcher(pathConfig.getPathMatcherOrDefault());
        }
        handlerMapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        handlerMapping.setCorsConfigurations(this.getCorsConfigurations());
        return handlerMapping;
    }

    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    @Bean
    public ResourceUrlProvider mvcResourceUrlProvider() {
        ResourceUrlProvider urlProvider = new ResourceUrlProvider();
        urlProvider.setUrlPathHelper(this.getPathMatchConfigurer().getUrlPathHelperOrDefault());
        urlProvider.setPathMatcher(this.getPathMatchConfigurer().getPathMatcherOrDefault());
        return urlProvider;
    }

    @Bean
    @Nullable
    public HandlerMapping defaultServletHandlerMapping() {
        Assert.state(this.servletContext != null, "No ServletContext set");
        DefaultServletHandlerConfigurer configurer = new DefaultServletHandlerConfigurer(this.servletContext);
        this.configureDefaultServletHandling(configurer);
        return configurer.buildHandlerMapping();
    }

    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(@Qualifier(value="mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager, @Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="mvcValidator") Validator validator) {
        AsyncSupportConfigurer configurer;
        RequestMappingHandlerAdapter adapter = this.createRequestMappingHandlerAdapter();
        adapter.setContentNegotiationManager(contentNegotiationManager);
        adapter.setMessageConverters(this.getMessageConverters());
        adapter.setWebBindingInitializer(this.getConfigurableWebBindingInitializer(conversionService, validator));
        adapter.setCustomArgumentResolvers(this.getArgumentResolvers());
        adapter.setCustomReturnValueHandlers(this.getReturnValueHandlers());
        if (jackson2Present) {
            adapter.setRequestBodyAdvice(Collections.singletonList(new JsonViewRequestBodyAdvice()));
            adapter.setResponseBodyAdvice(Collections.singletonList(new JsonViewResponseBodyAdvice()));
        }
        if ((configurer = this.getAsyncSupportConfigurer()).getTaskExecutor() != null) {
            adapter.setTaskExecutor(configurer.getTaskExecutor());
        }
        if (configurer.getTimeout() != null) {
            adapter.setAsyncRequestTimeout(configurer.getTimeout());
        }
        adapter.setCallableInterceptors(configurer.getCallableInterceptors());
        adapter.setDeferredResultInterceptors(configurer.getDeferredResultInterceptors());
        return adapter;
    }

    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }

    @Bean
    public HandlerFunctionAdapter handlerFunctionAdapter() {
        HandlerFunctionAdapter adapter = new HandlerFunctionAdapter();
        AsyncSupportConfigurer configurer = this.getAsyncSupportConfigurer();
        if (configurer.getTimeout() != null) {
            adapter.setAsyncRequestTimeout(configurer.getTimeout());
        }
        return adapter;
    }

    protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer(FormattingConversionService mvcConversionService, Validator mvcValidator) {
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(mvcConversionService);
        initializer.setValidator(mvcValidator);
        MessageCodesResolver messageCodesResolver = this.getMessageCodesResolver();
        if (messageCodesResolver != null) {
            initializer.setMessageCodesResolver(messageCodesResolver);
        }
        return initializer;
    }

    @Nullable
    protected MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Bean
    public FormattingConversionService mvcConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        this.addFormatters(conversionService);
        return conversionService;
    }

    protected void addFormatters(FormatterRegistry registry) {
    }

    @Bean
    public Validator mvcValidator() {
        Validator validator = this.getValidator();
        if (validator == null) {
            if (ClassUtils.isPresent("javax.validation.Validator", this.getClass().getClassLoader())) {
                Class<?> clazz;
                try {
                    String className = "org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean";
                    clazz = ClassUtils.forName(className, WebMvcConfigurationSupport.class.getClassLoader());
                }
                catch (ClassNotFoundException | LinkageError ex) {
                    throw new BeanInitializationException("Failed to resolve default validator class", ex);
                }
                validator = (Validator)BeanUtils.instantiateClass(clazz);
            } else {
                validator = new NoOpValidator();
            }
        }
        return validator;
    }

    @Nullable
    protected Validator getValidator() {
        return null;
    }

    protected final List<HandlerMethodArgumentResolver> getArgumentResolvers() {
        if (this.argumentResolvers == null) {
            this.argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
            this.addArgumentResolvers(this.argumentResolvers);
        }
        return this.argumentResolvers;
    }

    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    protected final List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
        if (this.returnValueHandlers == null) {
            this.returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
            this.addReturnValueHandlers(this.returnValueHandlers);
        }
        return this.returnValueHandlers;
    }

    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    protected final List<HttpMessageConverter<?>> getMessageConverters() {
        if (this.messageConverters == null) {
            this.messageConverters = new ArrayList();
            this.configureMessageConverters(this.messageConverters);
            if (this.messageConverters.isEmpty()) {
                this.addDefaultHttpMessageConverters(this.messageConverters);
            }
            this.extendMessageConverters(this.messageConverters);
        }
        return this.messageConverters;
    }

    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        Jackson2ObjectMapperBuilder builder;
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new ResourceRegionHttpMessageConverter());
        if (!shouldIgnoreXml) {
            try {
                messageConverters.add(new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
        }
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (romePresent) {
            messageConverters.add(new AtomFeedHttpMessageConverter());
            messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (!shouldIgnoreXml) {
            if (jackson2XmlPresent) {
                builder = Jackson2ObjectMapperBuilder.xml();
                if (this.applicationContext != null) {
                    builder.applicationContext(this.applicationContext);
                }
                messageConverters.add(new MappingJackson2XmlHttpMessageConverter((ObjectMapper)builder.build()));
            } else if (jaxb2Present) {
                messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
            }
        }
        if (kotlinSerializationJsonPresent) {
            messageConverters.add(new KotlinSerializationJsonHttpMessageConverter());
        }
        if (jackson2Present) {
            builder = Jackson2ObjectMapperBuilder.json();
            if (this.applicationContext != null) {
                builder.applicationContext(this.applicationContext);
            }
            messageConverters.add(new MappingJackson2HttpMessageConverter((ObjectMapper)builder.build()));
        } else if (gsonPresent) {
            messageConverters.add(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            messageConverters.add(new JsonbHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            builder = Jackson2ObjectMapperBuilder.smile();
            if (this.applicationContext != null) {
                builder.applicationContext(this.applicationContext);
            }
            messageConverters.add(new MappingJackson2SmileHttpMessageConverter((ObjectMapper)builder.build()));
        }
        if (jackson2CborPresent) {
            builder = Jackson2ObjectMapperBuilder.cbor();
            if (this.applicationContext != null) {
                builder.applicationContext(this.applicationContext);
            }
            messageConverters.add(new MappingJackson2CborHttpMessageConverter((ObjectMapper)builder.build()));
        }
    }

    protected AsyncSupportConfigurer getAsyncSupportConfigurer() {
        if (this.asyncSupportConfigurer == null) {
            this.asyncSupportConfigurer = new AsyncSupportConfigurer();
            this.configureAsyncSupport(this.asyncSupportConfigurer);
        }
        return this.asyncSupportConfigurer;
    }

    protected void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    }

    @Bean
    public CompositeUriComponentsContributor mvcUriComponentsContributor(@Qualifier(value="mvcConversionService") FormattingConversionService conversionService, @Qualifier(value="requestMappingHandlerAdapter") RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        return new CompositeUriComponentsContributor(requestMappingHandlerAdapter.getArgumentResolvers(), conversionService);
    }

    @Bean
    public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
        return new HttpRequestHandlerAdapter();
    }

    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }

    @Bean
    public HandlerExceptionResolver handlerExceptionResolver(@Qualifier(value="mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager) {
        ArrayList<HandlerExceptionResolver> exceptionResolvers = new ArrayList<HandlerExceptionResolver>();
        this.configureHandlerExceptionResolvers(exceptionResolvers);
        if (exceptionResolvers.isEmpty()) {
            this.addDefaultHandlerExceptionResolvers(exceptionResolvers, contentNegotiationManager);
        }
        this.extendHandlerExceptionResolvers(exceptionResolvers);
        HandlerExceptionResolverComposite composite = new HandlerExceptionResolverComposite();
        composite.setOrder(0);
        composite.setExceptionResolvers(exceptionResolvers);
        return composite;
    }

    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    protected void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    protected final void addDefaultHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers, ContentNegotiationManager mvcContentNegotiationManager) {
        ExceptionHandlerExceptionResolver exceptionHandlerResolver = this.createExceptionHandlerExceptionResolver();
        exceptionHandlerResolver.setContentNegotiationManager(mvcContentNegotiationManager);
        exceptionHandlerResolver.setMessageConverters(this.getMessageConverters());
        exceptionHandlerResolver.setCustomArgumentResolvers(this.getArgumentResolvers());
        exceptionHandlerResolver.setCustomReturnValueHandlers(this.getReturnValueHandlers());
        if (jackson2Present) {
            exceptionHandlerResolver.setResponseBodyAdvice(Collections.singletonList(new JsonViewResponseBodyAdvice()));
        }
        if (this.applicationContext != null) {
            exceptionHandlerResolver.setApplicationContext(this.applicationContext);
        }
        exceptionHandlerResolver.afterPropertiesSet();
        exceptionResolvers.add(exceptionHandlerResolver);
        ResponseStatusExceptionResolver responseStatusResolver = new ResponseStatusExceptionResolver();
        responseStatusResolver.setMessageSource(this.applicationContext);
        exceptionResolvers.add(responseStatusResolver);
        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
    }

    protected ExceptionHandlerExceptionResolver createExceptionHandlerExceptionResolver() {
        return new ExceptionHandlerExceptionResolver();
    }

    @Bean
    public ViewResolver mvcViewResolver(@Qualifier(value="mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager) {
        String[] names;
        ViewResolverRegistry registry = new ViewResolverRegistry(contentNegotiationManager, this.applicationContext);
        this.configureViewResolvers(registry);
        if (registry.getViewResolvers().isEmpty() && this.applicationContext != null && (names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this.applicationContext, ViewResolver.class, true, false)).length == 1) {
            registry.getViewResolvers().add(new InternalResourceViewResolver());
        }
        ViewResolverComposite composite = new ViewResolverComposite();
        composite.setOrder(registry.getOrder());
        composite.setViewResolvers(registry.getViewResolvers());
        if (this.applicationContext != null) {
            composite.setApplicationContext(this.applicationContext);
        }
        if (this.servletContext != null) {
            composite.setServletContext(this.servletContext);
        }
        return composite;
    }

    protected void configureViewResolvers(ViewResolverRegistry registry) {
    }

    protected final Map<String, CorsConfiguration> getCorsConfigurations() {
        if (this.corsConfigurations == null) {
            CorsRegistry registry = new CorsRegistry();
            this.addCorsMappings(registry);
            this.corsConfigurations = registry.getCorsConfigurations();
        }
        return this.corsConfigurations;
    }

    protected void addCorsMappings(CorsRegistry registry) {
    }

    @Bean
    @Lazy
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver();
    }

    @Bean
    public ThemeResolver themeResolver() {
        return new FixedThemeResolver();
    }

    @Bean
    public FlashMapManager flashMapManager() {
        return new SessionFlashMapManager();
    }

    @Bean
    public RequestToViewNameTranslator viewNameTranslator() {
        return new DefaultRequestToViewNameTranslator();
    }

    static {
        ClassLoader classLoader = WebMvcConfigurationSupport.class.getClassLoader();
        romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", classLoader);
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
    }

    private static final class NoOpValidator
    implements Validator {
        private NoOpValidator() {
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return false;
        }

        @Override
        public void validate(@Nullable Object target, Errors errors) {
        }
    }
}

