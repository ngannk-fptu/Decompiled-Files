/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.reflect.Method;

class MethodImplWrapper {
    private Object instance;
    private Method method;

    public MethodImplWrapper(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

