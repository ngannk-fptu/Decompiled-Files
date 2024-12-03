/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.parsing.ReaderContext
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.cache.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.config.CacheNamespaceHandler;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.cache.interceptor.NameMatchCacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

class CacheAdviceParser
extends AbstractSingleBeanDefinitionParser {
    private static final String CACHEABLE_ELEMENT = "cacheable";
    private static final String CACHE_EVICT_ELEMENT = "cache-evict";
    private static final String CACHE_PUT_ELEMENT = "cache-put";
    private static final String METHOD_ATTRIBUTE = "method";
    private static final String DEFS_ELEMENT = "caching";

    CacheAdviceParser() {
    }

    protected Class<?> getBeanClass(Element element) {
        return CacheInterceptor.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference("cacheManager", CacheNamespaceHandler.extractCacheManager(element));
        CacheNamespaceHandler.parseKeyGenerator(element, (BeanDefinition)builder.getBeanDefinition());
        List cacheDefs = DomUtils.getChildElementsByTagName((Element)element, (String)DEFS_ELEMENT);
        if (!cacheDefs.isEmpty()) {
            List<RootBeanDefinition> attributeSourceDefinitions = this.parseDefinitionsSources(cacheDefs, parserContext);
            builder.addPropertyValue("cacheOperationSources", attributeSourceDefinitions);
        } else {
            builder.addPropertyValue("cacheOperationSources", (Object)new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource"));
        }
    }

    private List<RootBeanDefinition> parseDefinitionsSources(List<Element> definitions, ParserContext parserContext) {
        ManagedList defs = new ManagedList(definitions.size());
        for (Element element : definitions) {
            defs.add((Object)this.parseDefinitionSource(element, parserContext));
        }
        return defs;
    }

    private RootBeanDefinition parseDefinitionSource(Element definition, ParserContext parserContext) {
        Props prop = new Props(definition);
        ManagedMap cacheOpMap = new ManagedMap();
        cacheOpMap.setSource(parserContext.extractSource((Object)definition));
        List cacheableCacheMethods = DomUtils.getChildElementsByTagName((Element)definition, (String)CACHEABLE_ELEMENT);
        for (Object opElement : cacheableCacheMethods) {
            String name = prop.merge((Element)opElement, (ReaderContext)parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(opElement));
            CacheableOperation.Builder builder = prop.merge((Element)opElement, (ReaderContext)parserContext.getReaderContext(), new CacheableOperation.Builder());
            builder.setUnless(CacheAdviceParser.getAttributeValue((Element)opElement, "unless", ""));
            builder.setSync(Boolean.parseBoolean(CacheAdviceParser.getAttributeValue((Element)opElement, "sync", "false")));
            Collection col = (Collection)cacheOpMap.computeIfAbsent((Object)nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        List evictCacheMethods = DomUtils.getChildElementsByTagName((Element)definition, (String)CACHE_EVICT_ELEMENT);
        for (Object opElement : evictCacheMethods) {
            String after;
            String name = prop.merge((Element)opElement, (ReaderContext)parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(opElement));
            CacheEvictOperation.Builder builder = prop.merge((Element)opElement, (ReaderContext)parserContext.getReaderContext(), new CacheEvictOperation.Builder());
            String wide = opElement.getAttribute("all-entries");
            if (StringUtils.hasText((String)wide)) {
                builder.setCacheWide(Boolean.parseBoolean(wide.trim()));
            }
            if (StringUtils.hasText((String)(after = opElement.getAttribute("before-invocation")))) {
                builder.setBeforeInvocation(Boolean.parseBoolean(after.trim()));
            }
            Collection col = (Collection)cacheOpMap.computeIfAbsent((Object)nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        List putCacheMethods = DomUtils.getChildElementsByTagName((Element)definition, (String)CACHE_PUT_ELEMENT);
        for (Element opElement : putCacheMethods) {
            String name = prop.merge(opElement, (ReaderContext)parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource((Object)opElement));
            CachePutOperation.Builder builder = prop.merge(opElement, (ReaderContext)parserContext.getReaderContext(), new CachePutOperation.Builder());
            builder.setUnless(CacheAdviceParser.getAttributeValue(opElement, "unless", ""));
            Collection col = (Collection)cacheOpMap.computeIfAbsent((Object)nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition(NameMatchCacheOperationSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource((Object)definition));
        attributeSourceDefinition.getPropertyValues().add("nameMap", (Object)cacheOpMap);
        return attributeSourceDefinition;
    }

    private static String getAttributeValue(Element element, String attributeName, String defaultValue) {
        String attribute = element.getAttribute(attributeName);
        if (StringUtils.hasText((String)attribute)) {
            return attribute.trim();
        }
        return defaultValue;
    }

    private static class Props {
        private String key;
        private String keyGenerator;
        private String cacheManager;
        private String condition;
        private String method;
        @Nullable
        private String[] caches;

        Props(Element root) {
            String defaultCache = root.getAttribute("cache");
            this.key = root.getAttribute("key");
            this.keyGenerator = root.getAttribute("key-generator");
            this.cacheManager = root.getAttribute("cache-manager");
            this.condition = root.getAttribute("condition");
            this.method = root.getAttribute(CacheAdviceParser.METHOD_ATTRIBUTE);
            if (StringUtils.hasText((String)defaultCache)) {
                this.caches = StringUtils.commaDelimitedListToStringArray((String)defaultCache.trim());
            }
        }

        <T extends CacheOperation.Builder> T merge(Element element, ReaderContext readerCtx, T builder) {
            String cache = element.getAttribute("cache");
            String[] localCaches = this.caches;
            if (StringUtils.hasText((String)cache)) {
                localCaches = StringUtils.commaDelimitedListToStringArray((String)cache.trim());
            }
            if (localCaches != null) {
                builder.setCacheNames(localCaches);
            } else {
                readerCtx.error("No cache specified for " + element.getNodeName(), (Object)element);
            }
            builder.setKey(CacheAdviceParser.getAttributeValue(element, "key", this.key));
            builder.setKeyGenerator(CacheAdviceParser.getAttributeValue(element, "key-generator", this.keyGenerator));
            builder.setCacheManager(CacheAdviceParser.getAttributeValue(element, "cache-manager", this.cacheManager));
            builder.setCondition(CacheAdviceParser.getAttributeValue(element, "condition", this.condition));
            if (StringUtils.hasText((String)builder.getKey()) && StringUtils.hasText((String)builder.getKeyGenerator())) {
                throw new IllegalStateException("Invalid cache advice configuration on '" + element.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
            }
            return builder;
        }

        @Nullable
        String merge(Element element, ReaderContext readerCtx) {
            String method = element.getAttribute(CacheAdviceParser.METHOD_ATTRIBUTE);
            if (StringUtils.hasText((String)method)) {
                return method.trim();
            }
            if (StringUtils.hasText((String)this.method)) {
                return this.method;
            }
            readerCtx.error("No method specified for " + element.getNodeName(), (Object)element);
            return null;
        }
    }
}

