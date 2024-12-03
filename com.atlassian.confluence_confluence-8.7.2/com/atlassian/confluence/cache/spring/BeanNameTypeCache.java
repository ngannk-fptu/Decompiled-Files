/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache.spring;

import com.atlassian.confluence.cache.spring.BeanNameCacheResult;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeanNameTypeCache {
    private final ConcurrentMap<BeanNamesForTypeParams, CacheResult> beanNamesForTypesCache = new ConcurrentHashMap<BeanNamesForTypeParams, CacheResult>();

    public BeanNameCacheResult put(Class type, boolean includeNonSingletons, boolean allowEagerInit, String[] value) {
        return this.beanNamesForTypesCache.put(new BeanNamesForTypeParams(type, includeNonSingletons, allowEagerInit), new CacheResult(value));
    }

    public BeanNameCacheResult get(Class type, boolean includeNonSingletons, boolean allowEagerInit) {
        return (BeanNameCacheResult)this.beanNamesForTypesCache.get(new BeanNamesForTypeParams(type, includeNonSingletons, allowEagerInit));
    }

    public void clearCache() {
        this.beanNamesForTypesCache.clear();
    }

    private static class BeanNamesForTypeParams {
        private final Class type;
        private final boolean includeNonSingletons;
        private final boolean allowEagerInit;

        private BeanNamesForTypeParams(Class type, boolean includeNonSingletons, boolean allowEagerInit) {
            this.type = type;
            this.includeNonSingletons = includeNonSingletons;
            this.allowEagerInit = allowEagerInit;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BeanNamesForTypeParams that = (BeanNamesForTypeParams)o;
            if (this.allowEagerInit != that.allowEagerInit) {
                return false;
            }
            if (this.includeNonSingletons != that.includeNonSingletons) {
                return false;
            }
            return !(this.type != null ? !this.type.equals(that.type) : that.type != null);
        }

        public int hashCode() {
            int result = this.type != null ? this.type.hashCode() : 0;
            result = 31 * result + (this.includeNonSingletons ? 1 : 0);
            result = 31 * result + (this.allowEagerInit ? 1 : 0);
            return result;
        }
    }

    private static class CacheResult
    implements BeanNameCacheResult {
        private final String[] result;

        private CacheResult(String[] result) {
            this.result = result;
        }

        @Override
        public String[] getResult() {
            if (this.result != null) {
                return (String[])this.result.clone();
            }
            return this.result;
        }
    }
}

