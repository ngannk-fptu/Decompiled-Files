/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.FixedVersionStrategy;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.w3c.dom.Element;

class ResourcesBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String RESOURCE_CHAIN_CACHE = "spring-resource-chain-cache";
    private static final String VERSION_RESOLVER_ELEMENT = "version-resolver";
    private static final String VERSION_STRATEGY_ELEMENT = "version-strategy";
    private static final String FIXED_VERSION_STRATEGY_ELEMENT = "fixed-version-strategy";
    private static final String CONTENT_VERSION_STRATEGY_ELEMENT = "content-version-strategy";
    private static final String RESOURCE_URL_PROVIDER = "mvcResourceUrlProvider";
    private static final boolean webJarsPresent = ClassUtils.isPresent("org.webjars.WebJarAssetLocator", ResourcesBeanDefinitionParser.class.getClassLoader());

    ResourcesBeanDefinitionParser() {
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext context) {
        Object source = context.extractSource(element);
        this.registerUrlProvider(context, source);
        RuntimeBeanReference pathMatcherRef = MvcNamespaceUtils.registerPathMatcher(null, context, source);
        RuntimeBeanReference pathHelperRef = MvcNamespaceUtils.registerUrlPathHelper(null, context, source);
        String resourceHandlerName = this.registerResourceHandler(context, element, pathHelperRef, source);
        if (resourceHandlerName == null) {
            return null;
        }
        ManagedMap<String, String> urlMap = new ManagedMap<String, String>();
        String resourceRequestPath = element.getAttribute("mapping");
        if (!StringUtils.hasText(resourceRequestPath)) {
            context.getReaderContext().error("The 'mapping' attribute is required.", context.extractSource(element));
            return null;
        }
        urlMap.put(resourceRequestPath, resourceHandlerName);
        RootBeanDefinition handlerMappingDef = new RootBeanDefinition(SimpleUrlHandlerMapping.class);
        handlerMappingDef.setSource(source);
        handlerMappingDef.setRole(2);
        handlerMappingDef.getPropertyValues().add("urlMap", urlMap);
        handlerMappingDef.getPropertyValues().add("pathMatcher", pathMatcherRef).add("urlPathHelper", pathHelperRef);
        String orderValue = element.getAttribute("order");
        Object order = StringUtils.hasText(orderValue) ? orderValue : Integer.valueOf(0x7FFFFFFE);
        handlerMappingDef.getPropertyValues().add("order", order);
        RuntimeBeanReference corsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        handlerMappingDef.getPropertyValues().add("corsConfigurations", corsRef);
        String beanName = context.getReaderContext().generateBeanName(handlerMappingDef);
        context.getRegistry().registerBeanDefinition(beanName, handlerMappingDef);
        context.registerComponent(new BeanComponentDefinition(handlerMappingDef, beanName));
        MvcNamespaceUtils.registerDefaultComponents(context, source);
        return null;
    }

    private void registerUrlProvider(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(RESOURCE_URL_PROVIDER)) {
            RootBeanDefinition urlProvider = new RootBeanDefinition(ResourceUrlProvider.class);
            urlProvider.setSource(source);
            urlProvider.setRole(2);
            context.getRegistry().registerBeanDefinition(RESOURCE_URL_PROVIDER, urlProvider);
            context.registerComponent(new BeanComponentDefinition(urlProvider, RESOURCE_URL_PROVIDER));
            RootBeanDefinition interceptor = new RootBeanDefinition(ResourceUrlProviderExposingInterceptor.class);
            interceptor.setSource(source);
            interceptor.getConstructorArgumentValues().addIndexedArgumentValue(0, urlProvider);
            RootBeanDefinition mappedInterceptor = new RootBeanDefinition(MappedInterceptor.class);
            mappedInterceptor.setSource(source);
            mappedInterceptor.setRole(2);
            mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object)null);
            mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(1, interceptor);
            String mappedInterceptorName = context.getReaderContext().registerWithGeneratedName(mappedInterceptor);
            context.registerComponent(new BeanComponentDefinition(mappedInterceptor, mappedInterceptorName));
        }
    }

    @Nullable
    private String registerResourceHandler(ParserContext context, Element element, RuntimeBeanReference pathHelperRef, @Nullable Object source) {
        Object manager;
        Element resourceChainElement;
        Element cacheControlElement;
        String locationAttr = element.getAttribute("location");
        if (!StringUtils.hasText(locationAttr)) {
            context.getReaderContext().error("The 'location' attribute is required.", context.extractSource(element));
            return null;
        }
        RootBeanDefinition resourceHandlerDef = new RootBeanDefinition(ResourceHttpRequestHandler.class);
        resourceHandlerDef.setSource(source);
        resourceHandlerDef.setRole(2);
        MutablePropertyValues values = resourceHandlerDef.getPropertyValues();
        values.add("urlPathHelper", pathHelperRef);
        values.add("locationValues", StringUtils.commaDelimitedListToStringArray(locationAttr));
        String cacheSeconds = element.getAttribute("cache-period");
        if (StringUtils.hasText(cacheSeconds)) {
            values.add("cacheSeconds", cacheSeconds);
        }
        if ((cacheControlElement = DomUtils.getChildElementByTagName(element, "cache-control")) != null) {
            CacheControl cacheControl = this.parseCacheControl(cacheControlElement);
            values.add("cacheControl", cacheControl);
        }
        if ((resourceChainElement = DomUtils.getChildElementByTagName(element, "resource-chain")) != null) {
            this.parseResourceChain(resourceHandlerDef, context, resourceChainElement, source);
        }
        if ((manager = MvcNamespaceUtils.getContentNegotiationManager(context)) != null) {
            values.add("contentNegotiationManager", manager);
        }
        String beanName = context.getReaderContext().generateBeanName(resourceHandlerDef);
        context.getRegistry().registerBeanDefinition(beanName, resourceHandlerDef);
        context.registerComponent(new BeanComponentDefinition(resourceHandlerDef, beanName));
        return beanName;
    }

    private CacheControl parseCacheControl(Element element) {
        CacheControl cacheControl = "true".equals(element.getAttribute("no-cache")) ? CacheControl.noCache() : ("true".equals(element.getAttribute("no-store")) ? CacheControl.noStore() : (element.hasAttribute("max-age") ? CacheControl.maxAge(Long.parseLong(element.getAttribute("max-age")), TimeUnit.SECONDS) : CacheControl.empty()));
        if ("true".equals(element.getAttribute("must-revalidate"))) {
            cacheControl = cacheControl.mustRevalidate();
        }
        if ("true".equals(element.getAttribute("no-transform"))) {
            cacheControl = cacheControl.noTransform();
        }
        if ("true".equals(element.getAttribute("cache-public"))) {
            cacheControl = cacheControl.cachePublic();
        }
        if ("true".equals(element.getAttribute("cache-private"))) {
            cacheControl = cacheControl.cachePrivate();
        }
        if ("true".equals(element.getAttribute("proxy-revalidate"))) {
            cacheControl = cacheControl.proxyRevalidate();
        }
        if (element.hasAttribute("s-maxage")) {
            cacheControl = cacheControl.sMaxAge(Long.parseLong(element.getAttribute("s-maxage")), TimeUnit.SECONDS);
        }
        if (element.hasAttribute("stale-while-revalidate")) {
            cacheControl = cacheControl.staleWhileRevalidate(Long.parseLong(element.getAttribute("stale-while-revalidate")), TimeUnit.SECONDS);
        }
        if (element.hasAttribute("stale-if-error")) {
            cacheControl = cacheControl.staleIfError(Long.parseLong(element.getAttribute("stale-if-error")), TimeUnit.SECONDS);
        }
        return cacheControl;
    }

    private void parseResourceChain(RootBeanDefinition resourceHandlerDef, ParserContext context, Element element, @Nullable Object source) {
        String autoRegistration = element.getAttribute("auto-registration");
        boolean isAutoRegistration = !StringUtils.hasText(autoRegistration) || !"false".equals(autoRegistration);
        ManagedList<Object> resourceResolvers = new ManagedList<Object>();
        resourceResolvers.setSource(source);
        ManagedList<Object> resourceTransformers = new ManagedList<Object>();
        resourceTransformers.setSource(source);
        this.parseResourceCache(resourceResolvers, resourceTransformers, element, source);
        this.parseResourceResolversTransformers(isAutoRegistration, resourceResolvers, resourceTransformers, context, element, source);
        if (!resourceResolvers.isEmpty()) {
            resourceHandlerDef.getPropertyValues().add("resourceResolvers", resourceResolvers);
        }
        if (!resourceTransformers.isEmpty()) {
            resourceHandlerDef.getPropertyValues().add("resourceTransformers", resourceTransformers);
        }
    }

    private void parseResourceCache(ManagedList<Object> resourceResolvers, ManagedList<Object> resourceTransformers, Element element, @Nullable Object source) {
        String resourceCache = element.getAttribute("resource-cache");
        if ("true".equals(resourceCache)) {
            ConstructorArgumentValues cargs = new ConstructorArgumentValues();
            RootBeanDefinition cachingResolverDef = new RootBeanDefinition(CachingResourceResolver.class);
            cachingResolverDef.setSource(source);
            cachingResolverDef.setRole(2);
            cachingResolverDef.setConstructorArgumentValues(cargs);
            RootBeanDefinition cachingTransformerDef = new RootBeanDefinition(CachingResourceTransformer.class);
            cachingTransformerDef.setSource(source);
            cachingTransformerDef.setRole(2);
            cachingTransformerDef.setConstructorArgumentValues(cargs);
            String cacheManagerName = element.getAttribute("cache-manager");
            String cacheName = element.getAttribute("cache-name");
            if (StringUtils.hasText(cacheManagerName) && StringUtils.hasText(cacheName)) {
                RuntimeBeanReference cacheManagerRef = new RuntimeBeanReference(cacheManagerName);
                cargs.addIndexedArgumentValue(0, cacheManagerRef);
                cargs.addIndexedArgumentValue(1, cacheName);
            } else {
                ConstructorArgumentValues cacheCavs = new ConstructorArgumentValues();
                cacheCavs.addIndexedArgumentValue(0, RESOURCE_CHAIN_CACHE);
                RootBeanDefinition cacheDef = new RootBeanDefinition(ConcurrentMapCache.class);
                cacheDef.setSource(source);
                cacheDef.setRole(2);
                cacheDef.setConstructorArgumentValues(cacheCavs);
                cargs.addIndexedArgumentValue(0, cacheDef);
            }
            resourceResolvers.add(cachingResolverDef);
            resourceTransformers.add(cachingTransformerDef);
        }
    }

    private void parseResourceResolversTransformers(boolean isAutoRegistration, ManagedList<Object> resourceResolvers, ManagedList<Object> resourceTransformers, ParserContext context, Element element, @Nullable Object source) {
        Element transformersElement;
        Element resolversElement = DomUtils.getChildElementByTagName(element, "resolvers");
        if (resolversElement != null) {
            for (Element beanElement : DomUtils.getChildElements(resolversElement)) {
                if (VERSION_RESOLVER_ELEMENT.equals(beanElement.getLocalName())) {
                    RootBeanDefinition versionResolverDef = this.parseVersionResolver(context, beanElement, source);
                    versionResolverDef.setSource(source);
                    resourceResolvers.add(versionResolverDef);
                    if (!isAutoRegistration) continue;
                    RootBeanDefinition cssLinkTransformerDef = new RootBeanDefinition(CssLinkResourceTransformer.class);
                    cssLinkTransformerDef.setSource(source);
                    cssLinkTransformerDef.setRole(2);
                    resourceTransformers.add(cssLinkTransformerDef);
                    continue;
                }
                Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                resourceResolvers.add(object);
            }
        }
        if (isAutoRegistration) {
            if (webJarsPresent) {
                RootBeanDefinition webJarsResolverDef = new RootBeanDefinition(WebJarsResourceResolver.class);
                webJarsResolverDef.setSource(source);
                webJarsResolverDef.setRole(2);
                resourceResolvers.add(webJarsResolverDef);
            }
            RootBeanDefinition pathResolverDef = new RootBeanDefinition(PathResourceResolver.class);
            pathResolverDef.setSource(source);
            pathResolverDef.setRole(2);
            resourceResolvers.add(pathResolverDef);
        }
        if ((transformersElement = DomUtils.getChildElementByTagName(element, "transformers")) != null) {
            for (Element beanElement : DomUtils.getChildElementsByTagName(transformersElement, "bean", "ref")) {
                Object object = context.getDelegate().parsePropertySubElement(beanElement, null);
                resourceTransformers.add(object);
            }
        }
    }

    private RootBeanDefinition parseVersionResolver(ParserContext context, Element element, @Nullable Object source) {
        ManagedMap<String, RootBeanDefinition> strategyMap = new ManagedMap<String, RootBeanDefinition>();
        strategyMap.setSource(source);
        RootBeanDefinition versionResolverDef = new RootBeanDefinition(VersionResourceResolver.class);
        versionResolverDef.setSource(source);
        versionResolverDef.setRole(2);
        versionResolverDef.getPropertyValues().addPropertyValue("strategyMap", strategyMap);
        for (Element beanElement : DomUtils.getChildElements(element)) {
            String[] patterns = StringUtils.commaDelimitedListToStringArray(beanElement.getAttribute("patterns"));
            Object strategy = null;
            if (FIXED_VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                ConstructorArgumentValues cargs = new ConstructorArgumentValues();
                cargs.addIndexedArgumentValue(0, beanElement.getAttribute("version"));
                RootBeanDefinition strategyDef = new RootBeanDefinition(FixedVersionStrategy.class);
                strategyDef.setSource(source);
                strategyDef.setRole(2);
                strategyDef.setConstructorArgumentValues(cargs);
                strategy = strategyDef;
            } else if (CONTENT_VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                RootBeanDefinition strategyDef = new RootBeanDefinition(ContentVersionStrategy.class);
                strategyDef.setSource(source);
                strategyDef.setRole(2);
                strategy = strategyDef;
            } else if (VERSION_STRATEGY_ELEMENT.equals(beanElement.getLocalName())) {
                Element childElement = DomUtils.getChildElementsByTagName(beanElement, "bean", "ref").get(0);
                strategy = context.getDelegate().parsePropertySubElement(childElement, null);
            }
            for (String pattern : patterns) {
                strategyMap.put(pattern, (RootBeanDefinition)strategy);
            }
        }
        return versionResolverDef;
    }
}

