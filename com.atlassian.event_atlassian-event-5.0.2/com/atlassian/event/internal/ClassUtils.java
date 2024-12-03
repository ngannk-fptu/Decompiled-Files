/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.event.internal;

import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.Set;

class ClassUtils {
    private ClassUtils() {
    }

    static Set<Class<?>> findAllTypes(Class<?> cls) {
        LinkedHashSet types = Sets.newLinkedHashSet();
        ClassUtils.findAllTypes(cls, types);
        return types;
    }

    static void findAllTypes(Class<?> cls, Set<Class<?>> types) {
        if (cls == null) {
            return;
        }
        if (types.contains(cls)) {
            return;
        }
        types.add(cls);
        ClassUtils.findAllTypes(cls.getSuperclass(), types);
        for (int x = 0; x < cls.getInterfaces().length; ++x) {
            ClassUtils.findAllTypes(cls.getInterfaces()[x], types);
        }
    }
}

