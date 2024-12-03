/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.JavaMethodInvoker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class JavaMethodInvokerFactory {
    static JavaMethodInvoker defaultInstance = new JavaMethodInvoker(){

        @Override
        public Object invoke(Method m, Object o, Object ... parameters) throws InvocationTargetException, IllegalAccessException {
            return m.invoke(o, parameters);
        }
    };

    public static JavaMethodInvoker getDefault() {
        return defaultInstance;
    }
}

