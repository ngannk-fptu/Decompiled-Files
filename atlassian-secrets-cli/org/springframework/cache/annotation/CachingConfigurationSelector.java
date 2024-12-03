/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import java.util.ArrayList;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class CachingConfigurationSelector
extends AdviceModeImportSelector<EnableCaching> {
    private static final String PROXY_JCACHE_CONFIGURATION_CLASS = "org.springframework.cache.jcache.config.ProxyJCacheConfiguration";
    private static final String CACHE_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.cache.aspectj.AspectJCachingConfiguration";
    private static final String JCACHE_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.cache.aspectj.AspectJJCacheConfiguration";
    private static final boolean jsr107Present = ClassUtils.isPresent("javax.cache.Cache", CachingConfigurationSelector.class.getClassLoader());
    private static final boolean jcacheImplPresent = ClassUtils.isPresent("org.springframework.cache.jcache.config.ProxyJCacheConfiguration", CachingConfigurationSelector.class.getClassLoader());

    @Override
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY: {
                return this.getProxyImports();
            }
            case ASPECTJ: {
                return this.getAspectJImports();
            }
        }
        return null;
    }

    private String[] getProxyImports() {
        ArrayList<String> result = new ArrayList<String>(3);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyCachingConfiguration.class.getName());
        if (jsr107Present && jcacheImplPresent) {
            result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
        }
        return StringUtils.toStringArray(result);
    }

    private String[] getAspectJImports() {
        ArrayList<String> result = new ArrayList<String>(2);
        result.add(CACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        if (jsr107Present && jcacheImplPresent) {
            result.add(JCACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        }
        return StringUtils.toStringArray(result);
    }
}

