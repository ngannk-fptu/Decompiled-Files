/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ClassLoaderUtil;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class JCacheDetector {
    private static final String JCACHE_CACHING_CLASSNAME = "javax.cache.Caching";
    private static final String[] JCACHE_ADDITIONAL_REQUIRED_CLASSES = new String[]{"javax.cache.integration.CacheLoaderException", "javax.cache.integration.CacheWriterException", "javax.cache.processor.EntryProcessorException", "javax.cache.configuration.CompleteConfiguration"};

    private JCacheDetector() {
    }

    public static boolean isJCacheAvailable(ClassLoader classLoader) {
        return JCacheDetector.isJCacheAvailable(classLoader, null);
    }

    public static boolean isJCacheAvailable(ClassLoader classLoader, ILogger logger) {
        ClassLoader backupClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return JCacheDetector.class.getClassLoader();
            }
        });
        return JCacheDetector.isJCacheAvailableInternal(classLoader, logger) || JCacheDetector.isJCacheAvailableInternal(backupClassLoader, logger);
    }

    private static boolean isJCacheAvailableInternal(ClassLoader classLoader, ILogger logger) {
        if (!ClassLoaderUtil.isClassAvailable(classLoader, JCACHE_CACHING_CLASSNAME)) {
            return false;
        }
        for (String className : JCACHE_ADDITIONAL_REQUIRED_CLASSES) {
            if (ClassLoaderUtil.isClassAvailable(classLoader, className)) continue;
            if (logger != null) {
                logger.warning("An outdated version of JCache API was located in the classpath, please use newer versions of JCache API rather than 1.0.0-PFD or 0.x versions.");
            }
            return false;
        }
        return true;
    }
}

