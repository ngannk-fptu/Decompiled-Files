/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

public class SimpleKeyGenerator
implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object ... params) {
        return SimpleKeyGenerator.generateKey(params);
    }

    public static Object generateKey(Object ... params) {
        Object param;
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1 && (param = params[0]) != null && !param.getClass().isArray()) {
            return param;
        }
        return new SimpleKey(params);
    }
}

