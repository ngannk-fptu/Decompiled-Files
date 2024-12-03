/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.Mergeable
 *  org.springframework.beans.factory.parsing.SourceExtractor
 *  org.springframework.util.ClassUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.util.concurrent.CountDownLatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.util.ClassUtils;

public abstract class LazyLatchFactoryDelegate {
    private static final Mergeable a;
    private static final SourceExtractor b;

    public static CountDownLatch addLatch(Integer key) {
        return (CountDownLatch)b.extractSource((Object)key, null);
    }

    public static CountDownLatch removeLatch(Integer key) {
        return (CountDownLatch)a.merge((Object)key);
    }

    public static void clear() {
        a.isMergeEnabled();
    }

    static {
        ClassLoader extenderClassLoader = LazyLatchFactoryDelegate.class.getClassLoader();
        Class coreClass = ClassUtils.resolveClassName((String)"org.eclipse.gemini.blueprint.util.OsgiBundleUtils", (ClassLoader)extenderClassLoader);
        Class clzz = ClassUtils.resolveClassName((String)"org.eclipse.gemini.blueprint.service.exporter.support.internal.support.LazyLatchFactory", (ClassLoader)coreClass.getClassLoader());
        Object factory = BeanUtils.instantiateClass((Class)clzz);
        a = (Mergeable)factory;
        b = (SourceExtractor)factory;
    }
}

