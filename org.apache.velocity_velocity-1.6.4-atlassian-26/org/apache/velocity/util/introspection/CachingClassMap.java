/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.MapFactory;
import org.apache.velocity.util.introspection.ClassMap;
import org.apache.velocity.util.introspection.MethodMap;

public class CachingClassMap
implements ClassMap {
    private static final boolean debugReflection = false;
    private final Log log;
    private final Class clazz;
    private final MethodCache methodCache;

    public CachingClassMap(Class clazz, Log log) {
        this.clazz = clazz;
        this.log = log;
        this.methodCache = this.createMethodCache();
    }

    public Class getCachedClass() {
        return this.clazz;
    }

    @Override
    public Method findMethod(String name, Object[] params) throws MethodMap.AmbiguousException {
        return this.methodCache.get(name, params);
    }

    private MethodCache createMethodCache() {
        MethodCache methodCache = new MethodCache(this.log);
        for (Class classToReflect = this.getCachedClass(); classToReflect != null; classToReflect = classToReflect.getSuperclass()) {
            if (Modifier.isPublic(classToReflect.getModifiers())) {
                this.populateMethodCacheWith(methodCache, classToReflect);
            }
            Class<?>[] interfaces = classToReflect.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                this.populateMethodCacheWithInterface(methodCache, interfaces[i]);
            }
        }
        return methodCache;
    }

    private void populateMethodCacheWithInterface(MethodCache methodCache, Class iface) {
        if (Modifier.isPublic(iface.getModifiers())) {
            this.populateMethodCacheWith(methodCache, iface);
        }
        Class<?>[] supers = iface.getInterfaces();
        for (int i = 0; i < supers.length; ++i) {
            this.populateMethodCacheWithInterface(methodCache, supers[i]);
        }
    }

    private void populateMethodCacheWith(MethodCache methodCache, Class classToReflect) {
        block3: {
            try {
                Method[] methods = classToReflect.getDeclaredMethods();
                for (int i = 0; i < methods.length; ++i) {
                    int modifiers = methods[i].getModifiers();
                    if (!Modifier.isPublic(modifiers)) continue;
                    methodCache.put(methods[i]);
                }
            }
            catch (SecurityException se) {
                if (!this.log.isDebugEnabled()) break block3;
                this.log.debug("While accessing methods of " + classToReflect + ": ", se);
            }
        }
    }

    private static final class MethodCache {
        private static final Object CACHE_MISS = new Object();
        private static final String NULL_ARG = Object.class.getName();
        private static final Map convertPrimitives = new HashMap();
        private final Log log;
        private final Map cache = MapFactory.create(false);
        private final MethodMap methodMap = new MethodMap();

        private MethodCache(Log log) {
            this.log = log;
        }

        public Method get(String name, Object[] params) throws MethodMap.AmbiguousException {
            String methodKey = this.makeMethodKey(name, params);
            Object cacheEntry = this.cache.get(methodKey);
            if (cacheEntry == CACHE_MISS) {
                return null;
            }
            if (cacheEntry == null) {
                try {
                    cacheEntry = this.methodMap.find(name, params);
                }
                catch (MethodMap.AmbiguousException ae) {
                    this.cache.put(methodKey, CACHE_MISS);
                    throw ae;
                }
                this.cache.put(methodKey, cacheEntry != null ? cacheEntry : CACHE_MISS);
            }
            return (Method)cacheEntry;
        }

        private void put(Method method) {
            String methodKey = this.makeMethodKey(method);
            if (this.cache.get(methodKey) == null) {
                this.cache.put(methodKey, method);
                this.methodMap.add(method);
            }
        }

        private String makeMethodKey(Method method) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            int args = parameterTypes.length;
            if (args == 0) {
                return method.getName();
            }
            StrBuilder methodKey = new StrBuilder((args + 1) * 16).append(method.getName());
            for (int j = 0; j < args; ++j) {
                if (parameterTypes[j].isPrimitive()) {
                    methodKey.append((String)convertPrimitives.get(parameterTypes[j]));
                    continue;
                }
                methodKey.append(parameterTypes[j].getName());
            }
            return methodKey.toString();
        }

        private String makeMethodKey(String method, Object[] params) {
            int args = params.length;
            if (args == 0) {
                return method;
            }
            StrBuilder methodKey = new StrBuilder((args + 1) * 16).append(method);
            for (int j = 0; j < args; ++j) {
                Object arg = params[j];
                if (arg == null) {
                    methodKey.append(NULL_ARG);
                    continue;
                }
                methodKey.append(arg.getClass().getName());
            }
            return methodKey.toString();
        }

        static {
            convertPrimitives.put(Boolean.TYPE, Boolean.class.getName());
            convertPrimitives.put(Byte.TYPE, Byte.class.getName());
            convertPrimitives.put(Character.TYPE, Character.class.getName());
            convertPrimitives.put(Double.TYPE, Double.class.getName());
            convertPrimitives.put(Float.TYPE, Float.class.getName());
            convertPrimitives.put(Integer.TYPE, Integer.class.getName());
            convertPrimitives.put(Long.TYPE, Long.class.getName());
            convertPrimitives.put(Short.TYPE, Short.class.getName());
        }
    }
}

