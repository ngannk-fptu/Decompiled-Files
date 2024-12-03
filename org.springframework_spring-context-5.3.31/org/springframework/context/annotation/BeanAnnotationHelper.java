/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.context.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ConcurrentReferenceHashMap;

abstract class BeanAnnotationHelper {
    private static final Map<Method, String> beanNameCache = new ConcurrentReferenceHashMap();
    private static final Map<Method, Boolean> scopedProxyCache = new ConcurrentReferenceHashMap();

    BeanAnnotationHelper() {
    }

    public static boolean isBeanAnnotated(Method method) {
        return AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, Bean.class);
    }

    public static String determineBeanNameFor(Method beanMethod) {
        String beanName = beanNameCache.get(beanMethod);
        if (beanName == null) {
            String[] names;
            beanName = beanMethod.getName();
            AnnotationAttributes bean2 = AnnotatedElementUtils.findMergedAnnotationAttributes((AnnotatedElement)beanMethod, Bean.class, (boolean)false, (boolean)false);
            if (bean2 != null && (names = bean2.getStringArray("name")).length > 0) {
                beanName = names[0];
            }
            beanNameCache.put(beanMethod, beanName);
        }
        return beanName;
    }

    public static boolean isScopedProxy(Method beanMethod) {
        Boolean scopedProxy = scopedProxyCache.get(beanMethod);
        if (scopedProxy == null) {
            AnnotationAttributes scope = AnnotatedElementUtils.findMergedAnnotationAttributes((AnnotatedElement)beanMethod, Scope.class, (boolean)false, (boolean)false);
            scopedProxy = scope != null && scope.getEnum("proxyMode") != ScopedProxyMode.NO;
            scopedProxyCache.put(beanMethod, scopedProxy);
        }
        return scopedProxy;
    }
}

