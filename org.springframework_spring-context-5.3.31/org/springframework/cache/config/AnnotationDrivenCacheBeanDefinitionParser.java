/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.config.AopNamespaceUtils
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.cache.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
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
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary((ParserContext)parserContext, (Element)element);
        SpringCachingConfigurer.registerCacheAdvisor(element, parserContext);
        if (jsr107Present && jcacheImplPresent) {
            JCacheCachingConfigurer.registerCacheAdvisor(element, parserContext);
        }
    }

    private static void parseCacheResolution(Element element, BeanDefinition def, boolean setBoth) {
        String name = element.getAttribute("cache-resolver");
        boolean hasText = StringUtils.hasText((String)name);
        if (hasText) {
            def.getPropertyValues().add("cacheResolver", (Object)new RuntimeBeanReference(name.trim()));
        }
        if (!hasText || setBoth) {
            def.getPropertyValues().add("cacheManager", (Object)new RuntimeBeanReference(CacheNamespaceHandler.extractCacheManager(element)));
        }
    }

    private static void parseErrorHandler(Element element, BeanDefinition def) {
        String name = element.getAttribute("error-handler");
        if (StringUtils.hasText((String)name)) {
            def.getPropertyValues().add("errorHandler", (Object)new RuntimeBeanReference(name.trim()));
        }
    }

    static {
        ClassLoader classLoader = AnnotationDrivenCacheBeanDefinitionParser.class.getClassLoader();
        jsr107Present = ClassUtils.isPresent((String)"javax.cache.Cache", (ClassLoader)classLoader);
        jcacheImplPresent = ClassUtils.isPresent((String)"org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource", (ClassLoader)classLoader);
    }

    private static class JCacheCachingConfigurer {
        private JCacheCachingConfigurer() {
        }

        private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalJCacheAdvisor")) {
                Object source = parserContext.extractSource((Object)element);
                RootBeanDefinition sourceDef = JCacheCachingConfigurer.createJCacheOperationSourceBeanDefinition(element, source);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)sourceDef);
                RootBeanDefinition interceptorDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.JCacheInterceptor");
                interceptorDef.setSource(source);
                interceptorDef.setRole(2);
                interceptorDef.getPropertyValues().add("cacheOperationSource", (Object)new RuntimeBeanReference(sourceName));
                AnnotationDrivenCacheBeanDefinitionParser.parseErrorHandler(element, (BeanDefinition)interceptorDef);
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)interceptorDef);
                RootBeanDefinition advisorDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor");
                advisorDef.setSource(source);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("cacheOperationSource", (Object)new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", (Object)interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", (Object)element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalJCacheAdvisor", (BeanDefinition)advisorDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)sourceDef, sourceName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)interceptorDef, interceptorName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)advisorDef, "org.springframework.cache.config.internalJCacheAdvisor"));
                parserContext.registerComponent((ComponentDefinition)compositeDef);
            }
        }

        private static void registerCacheAspect(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalJCacheAspect")) {
                Object source = parserContext.extractSource((Object)element);
                RootBeanDefinition cacheOperationSourceDef = JCacheCachingConfigurer.createJCacheOperationSourceBeanDefinition(element, source);
                String cacheOperationSourceName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)cacheOperationSourceDef);
                RootBeanDefinition jcacheAspectDef = new RootBeanDefinition();
                jcacheAspectDef.setBeanClassName(AnnotationDrivenCacheBeanDefinitionParser.JCACHE_ASPECT_CLASS_NAME);
                jcacheAspectDef.setFactoryMethodName("aspectOf");
                jcacheAspectDef.getPropertyValues().add("cacheOperationSource", (Object)new RuntimeBeanReference(cacheOperationSourceName));
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalJCacheAspect", (BeanDefinition)jcacheAspectDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)cacheOperationSourceDef, cacheOperationSourceName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)jcacheAspectDef, "org.springframework.cache.config.internalJCacheAspect"));
                parserContext.registerComponent((ComponentDefinition)compositeDef);
            }
        }

        private static RootBeanDefinition createJCacheOperationSourceBeanDefinition(Element element, @Nullable Object eleSource) {
            RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource");
            sourceDef.setSource(eleSource);
            sourceDef.setRole(2);
            AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, (BeanDefinition)sourceDef, true);
            CacheNamespaceHandler.parseKeyGenerator(element, (BeanDefinition)sourceDef);
            return sourceDef;
        }
    }

    private static class SpringCachingConfigurer {
        private SpringCachingConfigurer() {
        }

        private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAdvisor")) {
                Object eleSource = parserContext.extractSource((Object)element);
                RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource");
                sourceDef.setSource(eleSource);
                sourceDef.setRole(2);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)sourceDef);
                RootBeanDefinition interceptorDef = new RootBeanDefinition(CacheInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(2);
                AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, (BeanDefinition)interceptorDef, false);
                AnnotationDrivenCacheBeanDefinitionParser.parseErrorHandler(element, (BeanDefinition)interceptorDef);
                CacheNamespaceHandler.parseKeyGenerator(element, (BeanDefinition)interceptorDef);
                interceptorDef.getPropertyValues().add("cacheOperationSources", (Object)new RuntimeBeanReference(sourceName));
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)interceptorDef);
                RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryCacheOperationSourceAdvisor.class);
                advisorDef.setSource(eleSource);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("cacheOperationSource", (Object)new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", (Object)interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", (Object)element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalCacheAdvisor", (BeanDefinition)advisorDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)sourceDef, sourceName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)interceptorDef, interceptorName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)advisorDef, "org.springframework.cache.config.internalCacheAdvisor"));
                parserContext.registerComponent((ComponentDefinition)compositeDef);
            }
        }

        private static void registerCacheAspect(Element element, ParserContext parserContext) {
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAspect")) {
                RootBeanDefinition def = new RootBeanDefinition();
                def.setBeanClassName(AnnotationDrivenCacheBeanDefinitionParser.CACHE_ASPECT_CLASS_NAME);
                def.setFactoryMethodName("aspectOf");
                AnnotationDrivenCacheBeanDefinitionParser.parseCacheResolution(element, (BeanDefinition)def, false);
                CacheNamespaceHandler.parseKeyGenerator(element, (BeanDefinition)def);
                parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)def, "org.springframework.cache.config.internalCacheAspect"));
            }
        }
    }
}

