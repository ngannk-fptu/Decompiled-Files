/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.dataformat.cbor.CBORFactory
 *  com.fasterxml.jackson.dataformat.smile.SmileFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.beans.factory.xml.XmlReaderContext
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.format.support.FormattingConversionServiceFactoryBean
 *  org.springframework.http.converter.ByteArrayHttpMessageConverter
 *  org.springframework.http.converter.ResourceHttpMessageConverter
 *  org.springframework.http.converter.ResourceRegionHttpMessageConverter
 *  org.springframework.http.converter.StringHttpMessageConverter
 *  org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter
 *  org.springframework.http.converter.feed.AtomFeedHttpMessageConverter
 *  org.springframework.http.converter.feed.RssChannelHttpMessageConverter
 *  org.springframework.http.converter.json.GsonHttpMessageConverter
 *  org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter
 *  org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
 *  org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter
 *  org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
 *  org.springframework.http.converter.xml.SourceHttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.xml.DomUtils
 *  org.springframework.web.accept.ContentNegotiationManagerFactoryBean
 *  org.springframework.web.bind.support.ConfigurableWebBindingInitializer
 *  org.springframework.web.bind.support.WebArgumentResolver
 *  org.springframework.web.method.support.CompositeUriComponentsContributor
 */
package org.springframework.web.servlet.config;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.w3c.dom.Element;

class AnnotationDrivenBeanDefinitionParser
implements BeanDefinitionParser {
    public static final String HANDLER_MAPPING_BEAN_NAME = RequestMappingHandlerMapping.class.getName();
    public static final String HANDLER_ADAPTER_BEAN_NAME = RequestMappingHandlerAdapter.class.getName();
    public static final String CONTENT_NEGOTIATION_MANAGER_BEAN_NAME = "mvcContentNegotiationManager";
    private static final boolean javaxValidationPresent;
    private static final boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean jackson2CborPresent;
    private static final boolean gsonPresent;

    AnnotationDrivenBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource((Object)element);
        XmlReaderContext readerContext = context.getReaderContext();
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        context.pushContainingComponent(compDefinition);
        RuntimeBeanReference contentNegotiationManager = this.getContentNegotiationManager(element, source, context);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition(RequestMappingHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add("order", (Object)0);
        handlerMappingDef.getPropertyValues().add("contentNegotiationManager", (Object)contentNegotiationManager);
        if (element.hasAttribute("enable-matrix-variables")) {
            boolean enableMatrixVariables = Boolean.parseBoolean(element.getAttribute("enable-matrix-variables"));
            handlerMappingDef.getPropertyValues().add("removeSemicolonContent", (Object)(!enableMatrixVariables ? 1 : 0));
        }
        this.configurePathMatchingProperties(handlerMappingDef, element, context);
        readerContext.getRegistry().registerBeanDefinition(HANDLER_MAPPING_BEAN_NAME, (BeanDefinition)handlerMappingDef);
        RuntimeBeanReference corsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        handlerMappingDef.getPropertyValues().add("corsConfigurations", (Object)corsRef);
        RuntimeBeanReference conversionService = this.getConversionService(element, source, context);
        RuntimeBeanReference validator = this.getValidator(element, source, context);
        RuntimeBeanReference messageCodesResolver = this.getMessageCodesResolver(element);
        RootBeanDefinition bindingDef = new RootBeanDefinition(ConfigurableWebBindingInitializer.class);
        bindingDef.setSource(source);
        bindingDef.setRole(2);
        bindingDef.getPropertyValues().add("conversionService", (Object)conversionService);
        bindingDef.getPropertyValues().add("validator", (Object)validator);
        bindingDef.getPropertyValues().add("messageCodesResolver", (Object)messageCodesResolver);
        ManagedList<?> messageConverters = this.getMessageConverters(element, source, context);
        ManagedList<?> argumentResolvers = this.getArgumentResolvers(element, context);
        ManagedList<?> returnValueHandlers = this.getReturnValueHandlers(element, context);
        String asyncTimeout = this.getAsyncTimeout(element);
        RuntimeBeanReference asyncExecutor = this.getAsyncExecutor(element);
        ManagedList<?> callableInterceptors = this.getInterceptors(element, source, context, "callable-interceptors");
        ManagedList<?> deferredResultInterceptors = this.getInterceptors(element, source, context, "deferred-result-interceptors");
        RootBeanDefinition handlerAdapterDef = new RootBeanDefinition(RequestMappingHandlerAdapter.class);
        handlerAdapterDef.setSource(source);
        handlerAdapterDef.setRole(2);
        handlerAdapterDef.getPropertyValues().add("contentNegotiationManager", (Object)contentNegotiationManager);
        handlerAdapterDef.getPropertyValues().add("webBindingInitializer", (Object)bindingDef);
        handlerAdapterDef.getPropertyValues().add("messageConverters", messageConverters);
        this.addRequestBodyAdvice(handlerAdapterDef);
        this.addResponseBodyAdvice(handlerAdapterDef);
        if (element.hasAttribute("ignore-default-model-on-redirect")) {
            Boolean ignoreDefaultModel = Boolean.valueOf(element.getAttribute("ignore-default-model-on-redirect"));
            handlerAdapterDef.getPropertyValues().add("ignoreDefaultModelOnRedirect", (Object)ignoreDefaultModel);
        }
        if (argumentResolvers != null) {
            handlerAdapterDef.getPropertyValues().add("customArgumentResolvers", argumentResolvers);
        }
        if (returnValueHandlers != null) {
            handlerAdapterDef.getPropertyValues().add("customReturnValueHandlers", returnValueHandlers);
        }
        if (asyncTimeout != null) {
            handlerAdapterDef.getPropertyValues().add("asyncRequestTimeout", (Object)asyncTimeout);
        }
        if (asyncExecutor != null) {
            handlerAdapterDef.getPropertyValues().add("taskExecutor", (Object)asyncExecutor);
        }
        handlerAdapterDef.getPropertyValues().add("callableInterceptors", callableInterceptors);
        handlerAdapterDef.getPropertyValues().add("deferredResultInterceptors", deferredResultInterceptors);
        readerContext.getRegistry().registerBeanDefinition(HANDLER_ADAPTER_BEAN_NAME, (BeanDefinition)handlerAdapterDef);
        RootBeanDefinition uriContributorDef = new RootBeanDefinition(CompositeUriComponentsContributorFactoryBean.class);
        uriContributorDef.setSource(source);
        uriContributorDef.getPropertyValues().addPropertyValue("handlerAdapter", (Object)handlerAdapterDef);
        uriContributorDef.getPropertyValues().addPropertyValue("conversionService", (Object)conversionService);
        String uriContributorName = "mvcUriComponentsContributor";
        readerContext.getRegistry().registerBeanDefinition(uriContributorName, (BeanDefinition)uriContributorDef);
        RootBeanDefinition csInterceptorDef = new RootBeanDefinition(ConversionServiceExposingInterceptor.class);
        csInterceptorDef.setSource(source);
        csInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)conversionService);
        RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition(MappedInterceptor.class);
        mappedInterceptorDef.setSource(source);
        mappedInterceptorDef.setRole(2);
        mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, null);
        mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, (Object)csInterceptorDef);
        String mappedInterceptorName = readerContext.registerWithGeneratedName((BeanDefinition)mappedInterceptorDef);
        RootBeanDefinition methodExceptionResolver = new RootBeanDefinition(ExceptionHandlerExceptionResolver.class);
        methodExceptionResolver.setSource(source);
        methodExceptionResolver.setRole(2);
        methodExceptionResolver.getPropertyValues().add("contentNegotiationManager", (Object)contentNegotiationManager);
        methodExceptionResolver.getPropertyValues().add("messageConverters", messageConverters);
        methodExceptionResolver.getPropertyValues().add("order", (Object)0);
        this.addResponseBodyAdvice(methodExceptionResolver);
        if (argumentResolvers != null) {
            methodExceptionResolver.getPropertyValues().add("customArgumentResolvers", argumentResolvers);
        }
        if (returnValueHandlers != null) {
            methodExceptionResolver.getPropertyValues().add("customReturnValueHandlers", returnValueHandlers);
        }
        String methodExResolverName = readerContext.registerWithGeneratedName((BeanDefinition)methodExceptionResolver);
        RootBeanDefinition statusExceptionResolver = new RootBeanDefinition(ResponseStatusExceptionResolver.class);
        statusExceptionResolver.setSource(source);
        statusExceptionResolver.setRole(2);
        statusExceptionResolver.getPropertyValues().add("order", (Object)1);
        String statusExResolverName = readerContext.registerWithGeneratedName((BeanDefinition)statusExceptionResolver);
        RootBeanDefinition defaultExceptionResolver = new RootBeanDefinition(DefaultHandlerExceptionResolver.class);
        defaultExceptionResolver.setSource(source);
        defaultExceptionResolver.setRole(2);
        defaultExceptionResolver.getPropertyValues().add("order", (Object)2);
        String defaultExResolverName = readerContext.registerWithGeneratedName((BeanDefinition)defaultExceptionResolver);
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)handlerMappingDef, HANDLER_MAPPING_BEAN_NAME));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)handlerAdapterDef, HANDLER_ADAPTER_BEAN_NAME));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)uriContributorDef, uriContributorName));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)mappedInterceptorDef, mappedInterceptorName));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)methodExceptionResolver, methodExResolverName));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)statusExceptionResolver, statusExResolverName));
        context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)defaultExceptionResolver, defaultExResolverName));
        MvcNamespaceUtils.registerDefaultComponents(context, source);
        context.popAndRegisterContainingComponent();
        return null;
    }

    protected void addRequestBodyAdvice(RootBeanDefinition beanDef) {
        if (jackson2Present) {
            beanDef.getPropertyValues().add("requestBodyAdvice", (Object)new RootBeanDefinition(JsonViewRequestBodyAdvice.class));
        }
    }

    protected void addResponseBodyAdvice(RootBeanDefinition beanDef) {
        if (jackson2Present) {
            beanDef.getPropertyValues().add("responseBodyAdvice", (Object)new RootBeanDefinition(JsonViewResponseBodyAdvice.class));
        }
    }

    private RuntimeBeanReference getConversionService(Element element, @Nullable Object source, ParserContext context) {
        RuntimeBeanReference conversionServiceRef;
        if (element.hasAttribute("conversion-service")) {
            conversionServiceRef = new RuntimeBeanReference(element.getAttribute("conversion-service"));
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition(FormattingConversionServiceFactoryBean.class);
            conversionDef.setSource(source);
            conversionDef.setRole(2);
            String conversionName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)conversionDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)conversionDef, conversionName));
            conversionServiceRef = new RuntimeBeanReference(conversionName);
        }
        return conversionServiceRef;
    }

    @Nullable
    private RuntimeBeanReference getValidator(Element element, @Nullable Object source, ParserContext context) {
        if (element.hasAttribute("validator")) {
            return new RuntimeBeanReference(element.getAttribute("validator"));
        }
        if (javaxValidationPresent) {
            RootBeanDefinition validatorDef = new RootBeanDefinition("org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean");
            validatorDef.setSource(source);
            validatorDef.setRole(2);
            String validatorName = context.getReaderContext().registerWithGeneratedName((BeanDefinition)validatorDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)validatorDef, validatorName));
            return new RuntimeBeanReference(validatorName);
        }
        return null;
    }

    private RuntimeBeanReference getContentNegotiationManager(Element element, @Nullable Object source, ParserContext context) {
        RuntimeBeanReference beanRef;
        if (element.hasAttribute("content-negotiation-manager")) {
            String name = element.getAttribute("content-negotiation-manager");
            beanRef = new RuntimeBeanReference(name);
        } else {
            RootBeanDefinition factoryBeanDef = new RootBeanDefinition(ContentNegotiationManagerFactoryBean.class);
            factoryBeanDef.setSource(source);
            factoryBeanDef.setRole(2);
            factoryBeanDef.getPropertyValues().add("mediaTypes", (Object)this.getDefaultMediaTypes());
            String name = CONTENT_NEGOTIATION_MANAGER_BEAN_NAME;
            context.getReaderContext().getRegistry().registerBeanDefinition(name, (BeanDefinition)factoryBeanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)factoryBeanDef, name));
            beanRef = new RuntimeBeanReference(name);
        }
        return beanRef;
    }

    private void configurePathMatchingProperties(RootBeanDefinition handlerMappingDef, Element element, ParserContext context) {
        Element pathMatchingElement = DomUtils.getChildElementByTagName((Element)element, (String)"path-matching");
        if (pathMatchingElement != null) {
            Object source = context.extractSource((Object)element);
            if (pathMatchingElement.hasAttribute("suffix-pattern")) {
                Boolean useSuffixPatternMatch = Boolean.valueOf(pathMatchingElement.getAttribute("suffix-pattern"));
                handlerMappingDef.getPropertyValues().add("useSuffixPatternMatch", (Object)useSuffixPatternMatch);
            }
            if (pathMatchingElement.hasAttribute("trailing-slash")) {
                Boolean useTrailingSlashMatch = Boolean.valueOf(pathMatchingElement.getAttribute("trailing-slash"));
                handlerMappingDef.getPropertyValues().add("useTrailingSlashMatch", (Object)useTrailingSlashMatch);
            }
            if (pathMatchingElement.hasAttribute("registered-suffixes-only")) {
                Boolean useRegisteredSuffixPatternMatch = Boolean.valueOf(pathMatchingElement.getAttribute("registered-suffixes-only"));
                handlerMappingDef.getPropertyValues().add("useRegisteredSuffixPatternMatch", (Object)useRegisteredSuffixPatternMatch);
            }
            RuntimeBeanReference pathHelperRef = null;
            if (pathMatchingElement.hasAttribute("path-helper")) {
                pathHelperRef = new RuntimeBeanReference(pathMatchingElement.getAttribute("path-helper"));
            }
            pathHelperRef = MvcNamespaceUtils.registerUrlPathHelper(pathHelperRef, context, source);
            handlerMappingDef.getPropertyValues().add("urlPathHelper", (Object)pathHelperRef);
            RuntimeBeanReference pathMatcherRef = null;
            if (pathMatchingElement.hasAttribute("path-matcher")) {
                pathMatcherRef = new RuntimeBeanReference(pathMatchingElement.getAttribute("path-matcher"));
            }
            pathMatcherRef = MvcNamespaceUtils.registerPathMatcher(pathMatcherRef, context, source);
            handlerMappingDef.getPropertyValues().add("pathMatcher", (Object)pathMatcherRef);
        }
    }

    private Properties getDefaultMediaTypes() {
        Properties defaultMediaTypes = new Properties();
        if (romePresent) {
            defaultMediaTypes.put("atom", "application/atom+xml");
            defaultMediaTypes.put("rss", "application/rss+xml");
        }
        if (jaxb2Present || jackson2XmlPresent) {
            defaultMediaTypes.put("xml", "application/xml");
        }
        if (jackson2Present || gsonPresent) {
            defaultMediaTypes.put("json", "application/json");
        }
        if (jackson2SmilePresent) {
            defaultMediaTypes.put("smile", "application/x-jackson-smile");
        }
        if (jackson2CborPresent) {
            defaultMediaTypes.put("cbor", "application/cbor");
        }
        return defaultMediaTypes;
    }

    @Nullable
    private RuntimeBeanReference getMessageCodesResolver(Element element) {
        if (element.hasAttribute("message-codes-resolver")) {
            return new RuntimeBeanReference(element.getAttribute("message-codes-resolver"));
        }
        return null;
    }

    @Nullable
    private String getAsyncTimeout(Element element) {
        Element asyncElement = DomUtils.getChildElementByTagName((Element)element, (String)"async-support");
        return asyncElement != null ? asyncElement.getAttribute("default-timeout") : null;
    }

    @Nullable
    private RuntimeBeanReference getAsyncExecutor(Element element) {
        Element asyncElement = DomUtils.getChildElementByTagName((Element)element, (String)"async-support");
        if (asyncElement != null && asyncElement.hasAttribute("task-executor")) {
            return new RuntimeBeanReference(asyncElement.getAttribute("task-executor"));
        }
        return null;
    }

    private ManagedList<?> getInterceptors(Element element, @Nullable Object source, ParserContext context, String interceptorElementName) {
        Element interceptorsElement;
        ManagedList interceptors = new ManagedList();
        Element asyncElement = DomUtils.getChildElementByTagName((Element)element, (String)"async-support");
        if (asyncElement != null && (interceptorsElement = DomUtils.getChildElementByTagName((Element)asyncElement, (String)interceptorElementName)) != null) {
            interceptors.setSource(source);
            for (Element converter : DomUtils.getChildElementsByTagName((Element)interceptorsElement, (String)"bean")) {
                BeanDefinitionHolder beanDef = context.getDelegate().parseBeanDefinitionElement(converter);
                if (beanDef == null) continue;
                beanDef = context.getDelegate().decorateBeanDefinitionIfRequired(converter, beanDef);
                interceptors.add((Object)beanDef);
            }
        }
        return interceptors;
    }

    @Nullable
    private ManagedList<?> getArgumentResolvers(Element element, ParserContext context) {
        Element resolversElement = DomUtils.getChildElementByTagName((Element)element, (String)"argument-resolvers");
        if (resolversElement != null) {
            ManagedList<Object> resolvers = this.extractBeanSubElements(resolversElement, context);
            return this.wrapLegacyResolvers((List<Object>)resolvers, context);
        }
        return null;
    }

    private ManagedList<Object> wrapLegacyResolvers(List<Object> list, ParserContext context) {
        ManagedList result = new ManagedList();
        for (Object object : list) {
            if (object instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder beanDef = (BeanDefinitionHolder)object;
                String className = beanDef.getBeanDefinition().getBeanClassName();
                Assert.notNull((Object)className, (String)"No resolver class");
                Class clazz = ClassUtils.resolveClassName((String)className, (ClassLoader)context.getReaderContext().getBeanClassLoader());
                if (WebArgumentResolver.class.isAssignableFrom(clazz)) {
                    RootBeanDefinition adapter = new RootBeanDefinition(ServletWebArgumentResolverAdapter.class);
                    adapter.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)beanDef);
                    result.add((Object)new BeanDefinitionHolder((BeanDefinition)adapter, beanDef.getBeanName() + "Adapter"));
                    continue;
                }
            }
            result.add(object);
        }
        return result;
    }

    @Nullable
    private ManagedList<?> getReturnValueHandlers(Element element, ParserContext context) {
        Element handlers = DomUtils.getChildElementByTagName((Element)element, (String)"return-value-handlers");
        return handlers != null ? this.extractBeanSubElements(handlers, context) : null;
    }

    private ManagedList<?> getMessageConverters(Element element, @Nullable Object source, ParserContext context) {
        Element convertersElement = DomUtils.getChildElementByTagName((Element)element, (String)"message-converters");
        ManagedList messageConverters = new ManagedList();
        if (convertersElement != null) {
            messageConverters.setSource(source);
            for (Element beanElement : DomUtils.getChildElementsByTagName((Element)convertersElement, (String[])new String[]{"bean", "ref"})) {
                Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                messageConverters.add(object);
            }
        }
        if (convertersElement == null || Boolean.parseBoolean(convertersElement.getAttribute("register-defaults"))) {
            GenericBeanDefinition jacksonFactoryDef;
            RootBeanDefinition jacksonConverterDef;
            Class<MappingJackson2XmlHttpMessageConverter> type;
            messageConverters.setSource(source);
            messageConverters.add((Object)this.createConverterDefinition(ByteArrayHttpMessageConverter.class, source));
            RootBeanDefinition stringConverterDef = this.createConverterDefinition(StringHttpMessageConverter.class, source);
            stringConverterDef.getPropertyValues().add("writeAcceptCharset", (Object)false);
            messageConverters.add((Object)stringConverterDef);
            messageConverters.add((Object)this.createConverterDefinition(ResourceHttpMessageConverter.class, source));
            messageConverters.add((Object)this.createConverterDefinition(ResourceRegionHttpMessageConverter.class, source));
            messageConverters.add((Object)this.createConverterDefinition(SourceHttpMessageConverter.class, source));
            messageConverters.add((Object)this.createConverterDefinition(AllEncompassingFormHttpMessageConverter.class, source));
            if (romePresent) {
                messageConverters.add((Object)this.createConverterDefinition(AtomFeedHttpMessageConverter.class, source));
                messageConverters.add((Object)this.createConverterDefinition(RssChannelHttpMessageConverter.class, source));
            }
            if (jackson2XmlPresent) {
                type = MappingJackson2XmlHttpMessageConverter.class;
                jacksonConverterDef = this.createConverterDefinition(type, source);
                jacksonFactoryDef = this.createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef.getPropertyValues().add("createXmlMapper", (Object)true);
                jacksonConverterDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)jacksonFactoryDef);
                messageConverters.add((Object)jacksonConverterDef);
            } else if (jaxb2Present) {
                messageConverters.add((Object)this.createConverterDefinition(Jaxb2RootElementHttpMessageConverter.class, source));
            }
            if (jackson2Present) {
                type = MappingJackson2HttpMessageConverter.class;
                jacksonConverterDef = this.createConverterDefinition(type, source);
                jacksonFactoryDef = this.createObjectMapperFactoryDefinition(source);
                jacksonConverterDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)jacksonFactoryDef);
                messageConverters.add((Object)jacksonConverterDef);
            } else if (gsonPresent) {
                messageConverters.add((Object)this.createConverterDefinition(GsonHttpMessageConverter.class, source));
            }
            if (jackson2SmilePresent) {
                type = MappingJackson2SmileHttpMessageConverter.class;
                jacksonConverterDef = this.createConverterDefinition(type, source);
                jacksonFactoryDef = this.createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef.getPropertyValues().add("factory", (Object)new SmileFactory());
                jacksonConverterDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)jacksonFactoryDef);
                messageConverters.add((Object)jacksonConverterDef);
            }
            if (jackson2CborPresent) {
                type = MappingJackson2CborHttpMessageConverter.class;
                jacksonConverterDef = this.createConverterDefinition(type, source);
                jacksonFactoryDef = this.createObjectMapperFactoryDefinition(source);
                jacksonFactoryDef.getPropertyValues().add("factory", (Object)new CBORFactory());
                jacksonConverterDef.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)jacksonFactoryDef);
                messageConverters.add((Object)jacksonConverterDef);
            }
        }
        return messageConverters;
    }

    private GenericBeanDefinition createObjectMapperFactoryDefinition(@Nullable Object source) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Jackson2ObjectMapperFactoryBean.class);
        beanDefinition.setSource(source);
        beanDefinition.setRole(2);
        return beanDefinition;
    }

    private RootBeanDefinition createConverterDefinition(Class<?> converterClass, @Nullable Object source) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(converterClass);
        beanDefinition.setSource(source);
        beanDefinition.setRole(2);
        return beanDefinition;
    }

    private ManagedList<Object> extractBeanSubElements(Element parentElement, ParserContext context) {
        ManagedList list = new ManagedList();
        list.setSource(context.extractSource((Object)parentElement));
        for (Element beanElement : DomUtils.getChildElementsByTagName((Element)parentElement, (String[])new String[]{"bean", "ref"})) {
            Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
            list.add(object);
        }
        return list;
    }

    static {
        ClassLoader classLoader = AnnotationDrivenBeanDefinitionParser.class.getClassLoader();
        javaxValidationPresent = ClassUtils.isPresent((String)"javax.validation.Validator", (ClassLoader)classLoader);
        romePresent = ClassUtils.isPresent((String)"com.rometools.rome.feed.WireFeed", (ClassLoader)classLoader);
        jaxb2Present = ClassUtils.isPresent((String)"javax.xml.bind.Binder", (ClassLoader)classLoader);
        jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)classLoader) && ClassUtils.isPresent((String)"com.fasterxml.jackson.core.JsonGenerator", (ClassLoader)classLoader);
        jackson2XmlPresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.xml.XmlMapper", (ClassLoader)classLoader);
        jackson2SmilePresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.smile.SmileFactory", (ClassLoader)classLoader);
        jackson2CborPresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.cbor.CBORFactory", (ClassLoader)classLoader);
        gsonPresent = ClassUtils.isPresent((String)"com.google.gson.Gson", (ClassLoader)classLoader);
    }

    static class CompositeUriComponentsContributorFactoryBean
    implements FactoryBean<CompositeUriComponentsContributor>,
    InitializingBean {
        @Nullable
        private RequestMappingHandlerAdapter handlerAdapter;
        @Nullable
        private ConversionService conversionService;
        @Nullable
        private CompositeUriComponentsContributor uriComponentsContributor;

        CompositeUriComponentsContributorFactoryBean() {
        }

        public void setHandlerAdapter(RequestMappingHandlerAdapter handlerAdapter) {
            this.handlerAdapter = handlerAdapter;
        }

        public void setConversionService(ConversionService conversionService) {
            this.conversionService = conversionService;
        }

        public void afterPropertiesSet() {
            Assert.state((this.handlerAdapter != null ? 1 : 0) != 0, (String)"No RequestMappingHandlerAdapter set");
            this.uriComponentsContributor = new CompositeUriComponentsContributor(this.handlerAdapter.getArgumentResolvers(), this.conversionService);
        }

        @Nullable
        public CompositeUriComponentsContributor getObject() {
            return this.uriComponentsContributor;
        }

        public Class<?> getObjectType() {
            return CompositeUriComponentsContributor.class;
        }

        public boolean isSingleton() {
            return true;
        }
    }
}

