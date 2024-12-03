/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheManager
 *  javax.cache.Caching
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.jcache;

import java.net.URI;
import java.util.Properties;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

public class JCacheManagerFactoryBean
implements FactoryBean<CacheManager>,
BeanClassLoaderAware,
InitializingBean,
DisposableBean {
    @Nullable
    private URI cacheManagerUri;
    @Nullable
    private Properties cacheManagerProperties;
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private CacheManager cacheManager;

    public void setCacheManagerUri(@Nullable URI cacheManagerUri) {
        this.cacheManagerUri = cacheManagerUri;
    }

    public void setCacheManagerProperties(@Nullable Properties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void afterPropertiesSet() {
        this.cacheManager = Caching.getCachingProvider().getCacheManager(this.cacheManagerUri, this.beanClassLoader, this.cacheManagerProperties);
    }

    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    public Class<?> getObjectType() {
        return this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.cacheManager != null) {
            this.cacheManager.close();
        }
    }
}

