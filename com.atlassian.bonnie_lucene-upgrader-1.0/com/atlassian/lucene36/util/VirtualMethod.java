/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.WeakIdentityMap;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class VirtualMethod<C> {
    private static final Set<Method> singletonSet = Collections.synchronizedSet(new HashSet());
    private final Class<C> baseClass;
    private final String method;
    private final Class<?>[] parameters;
    private final WeakIdentityMap<Class<? extends C>, Integer> cache = WeakIdentityMap.newConcurrentHashMap();

    public VirtualMethod(Class<C> baseClass, String method, Class<?> ... parameters) {
        this.baseClass = baseClass;
        this.method = method;
        this.parameters = parameters;
        try {
            if (!singletonSet.add(baseClass.getDeclaredMethod(method, parameters))) {
                throw new UnsupportedOperationException("VirtualMethod instances must be singletons and therefore assigned to static final members in the same class, they use as baseClass ctor param.");
            }
        }
        catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException(baseClass.getName() + " has no such method: " + nsme.getMessage());
        }
    }

    public int getImplementationDistance(Class<? extends C> subclazz) {
        Integer distance = this.cache.get(subclazz);
        if (distance == null) {
            distance = this.reflectImplementationDistance(subclazz);
            this.cache.put(subclazz, distance);
        }
        return distance;
    }

    public boolean isOverriddenAsOf(Class<? extends C> subclazz) {
        return this.getImplementationDistance(subclazz) > 0;
    }

    private int reflectImplementationDistance(Class<? extends C> subclazz) {
        if (!this.baseClass.isAssignableFrom(subclazz)) {
            throw new IllegalArgumentException(subclazz.getName() + " is not a subclass of " + this.baseClass.getName());
        }
        boolean overridden = false;
        int distance = 0;
        for (Class<C> clazz = subclazz; clazz != this.baseClass && clazz != null; clazz = clazz.getSuperclass()) {
            if (!overridden) {
                try {
                    clazz.getDeclaredMethod(this.method, this.parameters);
                    overridden = true;
                }
                catch (NoSuchMethodException nsme) {
                    // empty catch block
                }
            }
            if (!overridden) continue;
            ++distance;
        }
        return distance;
    }

    public static <C> int compareImplementationDistance(Class<? extends C> clazz, VirtualMethod<C> m1, VirtualMethod<C> m2) {
        return Integer.valueOf(m1.getImplementationDistance(clazz)).compareTo(m2.getImplementationDistance(clazz));
    }
}

