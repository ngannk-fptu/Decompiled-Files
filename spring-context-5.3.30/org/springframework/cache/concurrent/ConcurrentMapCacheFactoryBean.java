/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.cache.concurrent;

import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ConcurrentMapCacheFactoryBean
implements FactoryBean<ConcurrentMapCache>,
BeanNameAware,
InitializingBean {
    private String name = "";
    @Nullable
    private ConcurrentMap<Object, Object> store;
    private boolean allowNullValues = true;
    @Nullable
    private ConcurrentMapCache cache;

    public void setName(String name) {
        this.name = name;
    }

    public void setStore(ConcurrentMap<Object, Object> store) {
        this.store = store;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setBeanName(String beanName) {
        if (!StringUtils.hasLength((String)this.name)) {
            this.setName(beanName);
        }
    }

    public void afterPropertiesSet() {
        this.cache = this.store != null ? new ConcurrentMapCache(this.name, this.store, this.allowNullValues) : new ConcurrentMapCache(this.name, this.allowNullValues);
    }

    @Nullable
    public ConcurrentMapCache getObject() {
        return this.cache;
    }

    public Class<?> getObjectType() {
        return ConcurrentMapCache.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

