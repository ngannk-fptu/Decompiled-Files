/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.config.CacheNamespaceHandler;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class AnnotationDrivenCacheBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String CACHE_ASPECT_CLASS_NAME = "org.springframework.cache.aspectj.AnnotationCacheAspect";
    private static final String JCACHE_ASPECT_CLASS_NAME = "org.springframework.cache.aspectj.JCacheCacheAspect";
    private static final boolean jsr107Present;
    private static final boolean jcacheImplPresent;

    AnnotationDrivenCacheBeanDefinitionParser() {
    }

    @Override
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            this.registerCacheAspect(element, parserContext);
        } else {
            this.registerCacheAdvisor(element, parserContext);
        }
        return null;
    }

    private void registerCacheAspect(Element element, ParserContext parserContext) {
        SpringCachingConfigurer.registerCacheAspect(element, parserContext);
        if (jsr107Present && jcacheImplPresent) {
            JCacheCachingConfigurer.registerCacheAspect(element, parserContext);
        }
    }

    private void registerCacheAdvisor(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
        SpringCachingConfigurer.registerCacheAdvisor(element, parserContext);
        if (jsr107Present && jcacheImplPresent) {
            JCacheCachingConfigurer.registerCacheAdvisor(element, parserContext);
        }
    }

    private static void parseCacheResolution(Element element, BeanDefinition def, boolean setBoth) {
        String name = element.getAttribute("cache-resolver");
        boolean hasText = StringUtils.hasText(name);
        if (hasText) {
            def.getPropertyValues().add("cacheResolver", new RuntimeBeanReference(name.trim()));
        }
        if (!hasText || setBoth) {
            def.getPropertyValues().add("cacheManager", new RuntimeBeanReference(CacheNamespaceHandler.extractCacheManager(element)));
        }
    }

    private static void parseErrorHandler(Element element, BeanDefinition def) {
        String name = element.getAttribute("error-handler");
        if (StringUtils.hasText(name)) {
            def.getPropertyValues().add("errorHandler", new RuntimeBeanReference(name.trim()));
        }
    }

    static {
        ClassLoader classLoader = AnnotationDrivenCacheBeanDefinitionParser.class.getClassLoader();
        jsr107Present = ClassUtils.isPresent("javax.cache.Cache", classLoader);
        jcacheImplPresent = ClassUtils.isPresent("org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource", classLoader);
    }

    private static class JCacheCachingConfigurer {
        private JCacheCachingConfigurer() {
        }

        private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalJCacheAdvisor")) {
                Object source = parserContext.extractSource(element);
                RootBeanDefinition sourceDef = JCacheCachingConfigurer.createJCacheOperationSourceBeanDefinition(element, source);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);
                RootBeanDefinition interceptorDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.JCacheInterceptor");
                interceptorDef.setSource(source);
                interceptorDef.setRole(2);
                interceptorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
                AnnotationDrivenCacheBeanDefinitionParser.parseErrorHandler(element, interceptorDef);
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);
                RootBeanDefinition advisorDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor");
                advisorDef.setSource(source);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalJCacheAdvisor", advisorDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
                compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, "org.springframework.cache.config.internalJCacheAdvisor"));
                parserContext.registerComponent(compositeDef);
            }
        }

        private static void registerCacheAspect(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalJCacheAspect")) {
                Object source = parserContext.extractSource(element);
                RootBeanDefinition cacheOperationSourceDef = JCacheCachingConfigurer.createJCacheOperationSourceBeanDefinition(element, source);
                String cacheOperationSourceName = parserContext.getReaderContext().registerWithGeneratedName(cacheOperationSourceDef);
                RootBeanDefinition jcacheAspectDef = new RootBeanDefinition();
                jcacheAspectDef.setBeanClassName(AnnotationDrivenCacheBeanDefinitionParser.JCACHE_ASPECT_CLASS_NAME);
                jcacheAspectDef.setFactoryMethodName("aspectOf");
                jcacheAspectDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(cacheOperationSourceName));
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalJCacheAspect", jcacheAspectDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
                compositeDef.addNestedComponent(new BeanComponentDefinition(cacheOperationSourceDef, cacheOperationSourceName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(jcacheAspectDef, "org.springframework.cache.config.internalJCacheAspect"));
                parserContext.registerComponent(compositeDef);
            }
        }

        private static RootBeanDefinition createJCacheOperationSourceBeanDefinition(Element element, @Nullable Object eleSource) {
            RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource");
            sourceDef.setSource(eleSource);
            sourceDef.setRole(2);
            AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, sourceDef, true);
            CacheNamespaceHandler.parseKeyGenerator(element, sourceDef);
            return sourceDef;
        }
    }

    private static class SpringCachingConfigurer {
        private SpringCachingConfigurer() {
        }

        private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAdvisor")) {
                Object eleSource = parserContext.extractSource(element);
                RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource");
                sourceDef.setSource(eleSource);
                sourceDef.setRole(2);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);
                RootBeanDefinition interceptorDef = new RootBeanDefinition(CacheInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(2);
                AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, interceptorDef, false);
                AnnotationDrivenCacheBeanDefinitionParser.parseErrorHandler(element, interceptorDef);
                CacheNamespaceHandler.parseKeyGenerator(element, interceptorDef);
                interceptorDef.getPropertyValues().add("cacheOperationSources", new RuntimeBeanReference(sourceName));
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);
                RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryCacheOperationSourceAdvisor.class);
                advisorDef.setSource(eleSource);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalCacheAdvisor", advisorDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
                compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, "org.springframework.cache.config.internalCacheAdvisor"));
                parserContext.registerComponent(compositeDef);
            }
        }

        private static void registerCacheAspect(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAspect")) {
                RootBeanDefinition def = new RootBeanDefinition();
                def.setBeanClassName(AnnotationDrivenCacheBeanDefinitionParser.CACHE_ASPECT_CLASS_NAME);
                def.setFactoryMethodName("aspectOf");
                AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, def, false);
                CacheNamespaceHandler.parseKeyGenerator(element, def);
                parserContext.registerBeanComponent(new BeanComponentDefinition(def, "org.springframework.cache.config.internalCacheAspect"));
            }
        }
    }
}

