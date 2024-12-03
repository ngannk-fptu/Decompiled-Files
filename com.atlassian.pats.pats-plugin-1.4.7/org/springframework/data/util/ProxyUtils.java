/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.core.io.support.SpringFactoriesLoader
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.util;

import java.util.List;
import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

public abstract class ProxyUtils {
    private static Map<Class<?>, Class<?>> USER_TYPES = new ConcurrentReferenceHashMap();
    private static final List<ProxyDetector> DETECTORS = SpringFactoriesLoader.loadFactories(ProxyDetector.class, (ClassLoader)ProxyUtils.class.getClassLoader());

    private ProxyUtils() {
    }

    public static Class<?> getUserClass(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return USER_TYPES.computeIfAbsent(type, it -> {
            Class<?> result = it;
            for (ProxyDetector proxyDetector : DETECTORS) {
                result = proxyDetector.getUserType(result);
            }
            return result;
        });
    }

    public static Class<?> getUserClass(Object source) {
        Assert.notNull((Object)source, (String)"Source object must not be null!");
        return ProxyUtils.getUserClass(AopUtils.getTargetClass((Object)source));
    }

    static {
        DETECTORS.add(ClassUtils::getUserClass);
    }

    public static interface ProxyDetector {
        public Class<?> getUserType(Class<?> var1);
    }
}

