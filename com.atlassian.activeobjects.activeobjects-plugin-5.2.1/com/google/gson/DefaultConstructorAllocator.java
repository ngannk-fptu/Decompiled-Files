/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Cache;
import com.google.gson.LruCache;
import java.lang.reflect.Constructor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DefaultConstructorAllocator {
    private static final Constructor<Null> NULL_CONSTRUCTOR = DefaultConstructorAllocator.createNullConstructor();
    private final Cache<Class<?>, Constructor<?>> constructorCache;

    public DefaultConstructorAllocator() {
        this(200);
    }

    public DefaultConstructorAllocator(int cacheSize) {
        this.constructorCache = new LruCache(cacheSize);
    }

    final boolean isInCache(Class<?> cacheKey) {
        return this.constructorCache.getElement(cacheKey) != null;
    }

    private static final Constructor<Null> createNullConstructor() {
        try {
            return DefaultConstructorAllocator.getNoArgsConstructor(Null.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public <T> T newInstance(Class<T> c) throws Exception {
        Constructor<T> constructor = this.findConstructor(c);
        return constructor != null ? (T)constructor.newInstance(new Object[0]) : null;
    }

    private <T> Constructor<T> findConstructor(Class<T> c) {
        Constructor<?> cachedElement = this.constructorCache.getElement(c);
        if (cachedElement != null) {
            if (cachedElement == NULL_CONSTRUCTOR) {
                return null;
            }
            return cachedElement;
        }
        Constructor<T> noArgsConstructor = DefaultConstructorAllocator.getNoArgsConstructor(c);
        if (noArgsConstructor != null) {
            this.constructorCache.addElement(c, noArgsConstructor);
        } else {
            this.constructorCache.addElement(c, NULL_CONSTRUCTOR);
        }
        return noArgsConstructor;
    }

    private static <T> Constructor<T> getNoArgsConstructor(Class<T> c) {
        try {
            Constructor<T> declaredConstructor = c.getDeclaredConstructor(new Class[0]);
            declaredConstructor.setAccessible(true);
            return declaredConstructor;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static final class Null {
        private Null() {
        }
    }
}

