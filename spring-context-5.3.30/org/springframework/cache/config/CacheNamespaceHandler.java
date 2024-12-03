/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 *  org.springframework.util.StringUtils
 */
package org.springframework.cache.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.cache.config.AnnotationDrivenCacheBeanDefinitionParser;
import org.springframework.cache.config.CacheAdviceParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class CacheNamespaceHandler
extends NamespaceHandlerSupport {
    static final String CACHE_MANAGER_ATTRIBUTE = "cache-manager";
    static final String DEFAULT_CACHE_MANAGER_BEAN_NAME = "cacheManager";

    static String extractCacheManager(Element element) {
        return element.hasAttribute(CACHE_MANAGER_ATTRIBUTE) ? element.getAttribute(CACHE_MANAGER_ATTRIBUTE) : DEFAULT_CACHE_MANAGER_BEAN_NAME;
    }

    static BeanDefinition parseKeyGenerator(Element element, BeanDefinition def) {
        String name = element.getAttribute("key-generator");
        if (StringUtils.hasText((String)name)) {
            def.getPropertyValues().add("keyGenerator", (Object)new RuntimeBeanReference(name.trim()));
        }
        return def;
    }

    public void init() {
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenCacheBeanDefinitionParser());
        this.registerBeanDefinitionParser("advice", (BeanDefinitionParser)new CacheAdviceParser());
    }
}

