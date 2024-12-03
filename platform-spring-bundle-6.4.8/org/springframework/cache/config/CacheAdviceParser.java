/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    @Override
    protected Class<?> getBeanClass(Element element) {
        return CacheInterceptor.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference("cacheManager", CacheNamespaceHandler.extractCacheManager(element));
        CacheNamespaceHandler.parseKeyGenerator(element, builder.getBeanDefinition());
        List<Element> cacheDefs = DomUtils.getChildElementsByTagName(element, DEFS_ELEMENT);
        if (!cacheDefs.isEmpty()) {
            List<RootBeanDefinition> attributeSourceDefinitions = this.parseDefinitionsSources(cacheDefs, parserContext);
            builder.addPropertyValue("cacheOperationSources", attributeSourceDefinitions);
        } else {
            builder.addPropertyValue("cacheOperationSources", new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource"));
        }
    }

    private List<RootBeanDefinition> parseDefinitionsSources(List<Element> definitions, ParserContext parserContext) {
        ManagedList<RootBeanDefinition> defs = new ManagedList<RootBeanDefinition>(definitions.size());
        for (Element element : definitions) {
            defs.add(this.parseDefinitionSource(element, parserContext));
        }
        return defs;
    }

    private RootBeanDefinition parseDefinitionSource(Element definition, ParserContext parserContext) {
        Props prop = new Props(definition);
        ManagedMap<TypedStringValue, Collection> cacheOpMap = new ManagedMap<TypedStringValue, Collection>();
        cacheOpMap.setSource(parserContext.extractSource(definition));
        List<Element> cacheableCacheMethods = DomUtils.getChildElementsByTagName(definition, CACHEABLE_ELEMENT);
        for (Element element : cacheableCacheMethods) {
            String string = prop.merge(element, parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(string);
            nameHolder.setSource(parserContext.extractSource(element));
            CacheableOperation.Builder builder = prop.merge(element, parserContext.getReaderContext(), new CacheableOperation.Builder());
            builder.setUnless(CacheAdviceParser.getAttributeValue(element, "unless", ""));
            builder.setSync(Boolean.parseBoolean(CacheAdviceParser.getAttributeValue(element, "sync", "false")));
            Collection col = cacheOpMap.computeIfAbsent(nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        List<Element> evictCacheMethods = DomUtils.getChildElementsByTagName(definition, CACHE_EVICT_ELEMENT);
        for (Element element : evictCacheMethods) {
            String after;
            String name = prop.merge(element, parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(element));
            CacheEvictOperation.Builder builder = prop.merge(element, parserContext.getReaderContext(), new CacheEvictOperation.Builder());
            String wide = element.getAttribute("all-entries");
            if (StringUtils.hasText(wide)) {
                builder.setCacheWide(Boolean.parseBoolean(wide.trim()));
            }
            if (StringUtils.hasText(after = element.getAttribute("before-invocation"))) {
                builder.setBeforeInvocation(Boolean.parseBoolean(after.trim()));
            }
            Collection col = cacheOpMap.computeIfAbsent(nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        List<Element> list = DomUtils.getChildElementsByTagName(definition, CACHE_PUT_ELEMENT);
        for (Element opElement : list) {
            String name = prop.merge(opElement, parserContext.getReaderContext());
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(opElement));
            CachePutOperation.Builder builder = prop.merge(opElement, parserContext.getReaderContext(), new CachePutOperation.Builder());
            builder.setUnless(CacheAdviceParser.getAttributeValue(opElement, "unless", ""));
            Collection col = cacheOpMap.computeIfAbsent(nameHolder, k -> new ArrayList(2));
            col.add(builder.build());
        }
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(NameMatchCacheOperationSource.class);
        rootBeanDefinition.setSource(parserContext.extractSource(definition));
        rootBeanDefinition.getPropertyValues().add("nameMap", cacheOpMap);
        return rootBeanDefinition;
    }

    private static String getAttributeValue(Element element, String attributeName, String defaultValue) {
        String attribute = element.getAttribute(attributeName);
        if (StringUtils.hasText(attribute)) {
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
            if (StringUtils.hasText(defaultCache)) {
                this.caches = StringUtils.commaDelimitedListToStringArray(defaultCache.trim());
            }
        }

        <T extends CacheOperation.Builder> T merge(Element element, ReaderContext readerCtx, T builder) {
            String cache = element.getAttribute("cache");
            String[] localCaches = this.caches;
            if (StringUtils.hasText(cache)) {
                localCaches = StringUtils.commaDelimitedListToStringArray(cache.trim());
            }
            if (localCaches != null) {
                builder.setCacheNames(localCaches);
            } else {
                readerCtx.error("No cache specified for " + element.getNodeName(), element);
            }
            builder.setKey(CacheAdviceParser.getAttributeValue(element, "key", this.key));
            builder.setKeyGenerator(CacheAdviceParser.getAttributeValue(element, "key-generator", this.keyGenerator));
            builder.setCacheManager(CacheAdviceParser.getAttributeValue(element, "cache-manager", this.cacheManager));
            builder.setCondition(CacheAdviceParser.getAttributeValue(element, "condition", this.condition));
            if (StringUtils.hasText(builder.getKey()) && StringUtils.hasText(builder.getKeyGenerator())) {
                throw new IllegalStateException("Invalid cache advice configuration on '" + element.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
            }
            return builder;
        }

        @Nullable
        String merge(Element element, ReaderContext readerCtx) {
            String method = element.getAttribute(CacheAdviceParser.METHOD_ATTRIBUTE);
            if (StringUtils.hasText(method)) {
                return method.trim();
            }
            if (StringUtils.hasText(this.method)) {
                return this.method;
            }
            readerCtx.error("No method specified for " + element.getNodeName(), element);
            return null;
        }
    }
}

