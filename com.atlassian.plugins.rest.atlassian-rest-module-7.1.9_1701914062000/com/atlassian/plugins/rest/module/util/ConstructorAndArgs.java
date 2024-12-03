/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.util;

import com.atlassian.plugins.rest.module.ChainingClassLoader;
import com.atlassian.plugins.rest.module.util.ProxyUtils;
import com.atlassian.plugins.rest.module.util.UnsupportedOperationInvocationHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

class ConstructorAndArgs {
    private Class<?> clazz;
    private Object prototype;
    private Object[] args;
    private Constructor<?> constructor;

    ConstructorAndArgs(Class<?> clazz) {
        this.clazz = clazz;
        this.initialise();
    }

    private void initialise() {
        Constructor<?>[] constructors;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.clazz);
        enhancer.setCallback(new UnsupportedOperationInvocationHandler());
        enhancer.setClassLoader(new ChainingClassLoader(ProxyUtils.class.getClassLoader(), this.clazz.getClassLoader()));
        for (Constructor<?> constructor : constructors = this.clazz.getConstructors()) {
            if ((constructor.getModifiers() & 1) == 0) continue;
            this.constructor = constructor;
            int size = constructor.getParameterTypes().length;
            this.args = new Object[size];
            for (int i = 0; i < this.args.length; ++i) {
                this.args[i] = ConstructorAndArgs.createEmptyValue(constructor.getParameterTypes()[i]);
            }
            this.prototype = this.clazz.cast(enhancer.create(constructor.getParameterTypes(), this.args));
            return;
        }
        throw new IllegalArgumentException("Class has no accessible constructor");
    }

    private static Object createEmptyValue(Class aClass) {
        if (aClass.isInterface()) {
            return ConstructorAndArgs.stubInterface(aClass);
        }
        if (aClass == Long.TYPE) {
            return 0L;
        }
        return null;
    }

    private static Object stubInterface(Class _interface) {
        return Proxy.newProxyInstance(_interface.getClassLoader(), new Class[]{_interface}, (InvocationHandler)UnsupportedOperationInvocationHandler.INSTANCE);
    }

    public Object create(Callback ... callback) {
        return this.clazz.cast(((Factory)this.prototype).newInstance(this.constructor.getParameterTypes(), this.args, callback));
    }
}

