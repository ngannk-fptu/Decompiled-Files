/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.security.access.method;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@Deprecated
public final class DelegatingMethodSecurityMetadataSource
extends AbstractMethodSecurityMetadataSource {
    private static final List<ConfigAttribute> NULL_CONFIG_ATTRIBUTE = Collections.emptyList();
    private final List<MethodSecurityMetadataSource> methodSecurityMetadataSources;
    private final Map<DefaultCacheKey, Collection<ConfigAttribute>> attributeCache = new HashMap<DefaultCacheKey, Collection<ConfigAttribute>>();

    public DelegatingMethodSecurityMetadataSource(List<MethodSecurityMetadataSource> methodSecurityMetadataSources) {
        Assert.notNull(methodSecurityMetadataSources, (String)"MethodSecurityMetadataSources cannot be null");
        this.methodSecurityMetadataSources = methodSecurityMetadataSources;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        DefaultCacheKey cacheKey = new DefaultCacheKey(method, targetClass);
        Map<DefaultCacheKey, Collection<ConfigAttribute>> map = this.attributeCache;
        synchronized (map) {
            MethodSecurityMetadataSource s;
            Collection<ConfigAttribute> cached = this.attributeCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
            Collection<ConfigAttribute> attributes = null;
            Iterator<MethodSecurityMetadataSource> iterator = this.methodSecurityMetadataSources.iterator();
            while (iterator.hasNext() && ((attributes = (s = iterator.next()).getAttributes(method, targetClass)) == null || attributes.isEmpty())) {
            }
            if (attributes == null || attributes.isEmpty()) {
                this.attributeCache.put(cacheKey, NULL_CONFIG_ATTRIBUTE);
                return NULL_CONFIG_ATTRIBUTE;
            }
            this.logger.debug((Object)LogMessage.format((String)"Caching method [%s] with attributes %s", (Object)cacheKey, attributes));
            this.attributeCache.put(cacheKey, attributes);
            return attributes;
        }
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        HashSet<ConfigAttribute> set = new HashSet<ConfigAttribute>();
        for (MethodSecurityMetadataSource s : this.methodSecurityMetadataSources) {
            Collection<ConfigAttribute> attrs = s.getAllConfigAttributes();
            if (attrs == null) continue;
            set.addAll(attrs);
        }
        return set;
    }

    public List<MethodSecurityMetadataSource> getMethodSecurityMetadataSources() {
        return this.methodSecurityMetadataSources;
    }

    private static class DefaultCacheKey {
        private final Method method;
        private final Class<?> targetClass;

        DefaultCacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        public boolean equals(Object other) {
            DefaultCacheKey otherKey = (DefaultCacheKey)other;
            return this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass);
        }

        public int hashCode() {
            return this.method.hashCode() * 21 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
        }

        public String toString() {
            String targetClassName = this.targetClass != null ? this.targetClass.getName() : "-";
            return "CacheKey[" + targetClassName + "; " + this.method + "]";
        }
    }
}

