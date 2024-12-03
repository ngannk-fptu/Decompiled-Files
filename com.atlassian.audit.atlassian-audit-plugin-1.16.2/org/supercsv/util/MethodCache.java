/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.lang.reflect.Method;
import org.supercsv.util.ReflectionUtils;
import org.supercsv.util.ThreeDHashMap;
import org.supercsv.util.TwoDHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MethodCache {
    private final ThreeDHashMap<Class<?>, Class<?>, String, Method> setMethodsCache = new ThreeDHashMap();
    private final TwoDHashMap<String, String, Method> getCache = new TwoDHashMap();

    public Method getGetMethod(Object object, String fieldName) {
        if (object == null) {
            throw new NullPointerException("object should not be null");
        }
        if (fieldName == null) {
            throw new NullPointerException("fieldName should not be null");
        }
        Method method = this.getCache.get(object.getClass().getName(), fieldName);
        if (method == null) {
            method = ReflectionUtils.findGetter(object, fieldName);
            this.getCache.set(object.getClass().getName(), fieldName, method);
        }
        return method;
    }

    public <T> Method getSetMethod(Object object, String fieldName, Class<?> argumentType) {
        if (object == null) {
            throw new NullPointerException("object should not be null");
        }
        if (fieldName == null) {
            throw new NullPointerException("fieldName should not be null");
        }
        if (argumentType == null) {
            throw new NullPointerException("argumentType should not be null");
        }
        Method method = this.setMethodsCache.get(object.getClass(), argumentType, fieldName);
        if (method == null) {
            method = ReflectionUtils.findSetter(object, fieldName, argumentType);
            this.setMethodsCache.set(object.getClass(), argumentType, fieldName, method);
        }
        return method;
    }
}

