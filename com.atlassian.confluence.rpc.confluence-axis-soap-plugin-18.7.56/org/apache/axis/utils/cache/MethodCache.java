/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.utils.ClassUtils;

public class MethodCache {
    private static transient MethodCache instance;
    private static transient ThreadLocal cache;
    private static final Object NULL_OBJECT;

    private MethodCache() {
        cache = new ThreadLocal();
    }

    public static MethodCache getInstance() {
        if (instance == null) {
            instance = new MethodCache();
        }
        return instance;
    }

    private Map getMethodCache() {
        HashMap map = (HashMap)cache.get();
        if (map == null) {
            map = new HashMap();
            cache.set(map);
        }
        return map;
    }

    public Method getMethod(Class clazz, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        MethodKey key;
        HashMap<MethodKey, Object> methods;
        Method method;
        Map cache;
        block9: {
            Object o;
            String className = clazz.getName();
            cache = this.getMethodCache();
            method = null;
            methods = null;
            key = new MethodKey(methodName, parameterTypes);
            methods = (HashMap<MethodKey, Object>)cache.get(clazz);
            if (methods != null && (o = methods.get(key)) != null) {
                if (o instanceof Method) {
                    return (Method)o;
                }
                return null;
            }
            try {
                method = clazz.getMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException e1) {
                if (clazz.isPrimitive() || className.startsWith("java.") || className.startsWith("javax.")) break block9;
                try {
                    Class helper = ClassUtils.forName(className + "_Helper");
                    method = helper.getMethod(methodName, parameterTypes);
                }
                catch (ClassNotFoundException e2) {
                    // empty catch block
                }
            }
        }
        if (methods == null) {
            methods = new HashMap<MethodKey, Object>();
            cache.put(clazz, methods);
        }
        if (null == method) {
            methods.put(key, NULL_OBJECT);
        } else {
            methods.put(key, method);
        }
        return method;
    }

    static {
        NULL_OBJECT = new Object();
    }

    static class MethodKey {
        private final String methodName;
        private final Class[] parameterTypes;

        MethodKey(String methodName, Class[] parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        public boolean equals(Object other) {
            MethodKey that = (MethodKey)other;
            return this.methodName.equals(that.methodName) && Arrays.equals(this.parameterTypes, that.parameterTypes);
        }

        public int hashCode() {
            return this.methodName.hashCode();
        }
    }
}

