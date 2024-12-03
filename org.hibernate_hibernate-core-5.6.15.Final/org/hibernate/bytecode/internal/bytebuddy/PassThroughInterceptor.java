/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal.bytebuddy;

import java.lang.reflect.Method;
import java.util.HashMap;
import org.hibernate.proxy.ProxyConfiguration;

public class PassThroughInterceptor
implements ProxyConfiguration.Interceptor {
    private HashMap<Object, Object> data = new HashMap();
    private final String proxiedClassName;

    public PassThroughInterceptor(String proxiedClassName) {
        this.proxiedClassName = proxiedClassName;
    }

    @Override
    public Object intercept(Object instance, Method method, Object[] arguments) throws Exception {
        String name = method.getName();
        if ("toString".equals(name) && arguments.length == 0) {
            return this.proxiedClassName + "@" + System.identityHashCode(instance);
        }
        if ("equals".equals(name) && arguments.length == 1) {
            return instance == arguments[0];
        }
        if ("hashCode".equals(name) && arguments.length == 0) {
            return System.identityHashCode(instance);
        }
        if (name.startsWith("get") && this.hasGetterSignature(method)) {
            String propName = name.substring(3);
            return this.data.get(propName);
        }
        if (name.startsWith("is") && this.hasGetterSignature(method)) {
            String propName = name.substring(2);
            return this.data.get(propName);
        }
        if (name.startsWith("set") && this.hasSetterSignature(method)) {
            String propName = name.substring(3);
            this.data.put(propName, arguments[0]);
            return null;
        }
        return null;
    }

    private boolean hasGetterSignature(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != null;
    }

    private boolean hasSetterSignature(Method method) {
        return method.getParameterCount() == 1 && (method.getReturnType() == null || method.getReturnType() == Void.TYPE);
    }
}

