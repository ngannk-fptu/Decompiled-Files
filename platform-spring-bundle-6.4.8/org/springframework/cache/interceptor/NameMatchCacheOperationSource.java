/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

public class NameMatchCacheOperationSource
implements CacheOperationSource,
Serializable {
    protected static final Log logger = LogFactory.getLog(NameMatchCacheOperationSource.class);
    private final Map<String, Collection<CacheOperation>> nameMap = new LinkedHashMap<String, Collection<CacheOperation>>();

    public void setNameMap(Map<String, Collection<CacheOperation>> nameMap) {
        nameMap.forEach(this::addCacheMethod);
    }

    public void addCacheMethod(String methodName, Collection<CacheOperation> ops) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Adding method [" + methodName + "] with cache operations [" + ops + "]"));
        }
        this.nameMap.put(methodName, ops);
    }

    @Override
    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        String methodName = method.getName();
        Collection<CacheOperation> ops = this.nameMap.get(methodName);
        if (ops == null) {
            String bestNameMatch = null;
            for (String mappedName : this.nameMap.keySet()) {
                if (!this.isMatch(methodName, mappedName) || bestNameMatch != null && bestNameMatch.length() > mappedName.length()) continue;
                ops = this.nameMap.get(mappedName);
                bestNameMatch = mappedName;
            }
        }
        return ops;
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameMatchCacheOperationSource)) {
            return false;
        }
        NameMatchCacheOperationSource otherCos = (NameMatchCacheOperationSource)other;
        return ObjectUtils.nullSafeEquals(this.nameMap, otherCos.nameMap);
    }

    public int hashCode() {
        return NameMatchCacheOperationSource.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.nameMap;
    }
}

