/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.jdk18;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.InterfaceAdapter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.VMBridge;

public class VMBridge_jdk18
extends VMBridge {
    private static final ThreadLocal<Object[]> contextLocal = new ThreadLocal();

    @Override
    protected Object getThreadContextHelper() {
        Object[] storage = contextLocal.get();
        if (storage == null) {
            storage = new Object[1];
            contextLocal.set(storage);
        }
        return storage;
    }

    @Override
    protected Context getContext(Object contextHelper) {
        Object[] storage = (Object[])contextHelper;
        return (Context)storage[0];
    }

    @Override
    protected void setContext(Object contextHelper, Context cx) {
        Object[] storage = (Object[])contextHelper;
        storage[0] = cx;
    }

    @Override
    protected boolean tryToMakeAccessible(AccessibleObject accessible) {
        if (accessible.isAccessible()) {
            return true;
        }
        try {
            accessible.setAccessible(true);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return accessible.isAccessible();
    }

    @Override
    protected Object getInterfaceProxyHelper(ContextFactory cf, Class<?>[] interfaces) {
        Constructor<?> c;
        ClassLoader loader = interfaces[0].getClassLoader();
        Class<?> cl = Proxy.getProxyClass(loader, interfaces);
        try {
            c = cl.getConstructor(InvocationHandler.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
        return c;
    }

    @Override
    protected Object newInterfaceProxy(Object proxyHelper, final ContextFactory cf, final InterfaceAdapter adapter, final Object target, final Scriptable topScope) {
        Object proxy;
        Constructor c = (Constructor)proxyHelper;
        InvocationHandler handler = new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if (method.getDeclaringClass() == Object.class) {
                    String methodName = method.getName();
                    if (methodName.equals("equals")) {
                        Object other = args[0];
                        return proxy == other;
                    }
                    if (methodName.equals("hashCode")) {
                        return target.hashCode();
                    }
                    if (methodName.equals("toString")) {
                        return "Proxy[" + target.toString() + "]";
                    }
                }
                return adapter.invoke(cf, target, topScope, proxy, method, args);
            }
        };
        try {
            proxy = c.newInstance(handler);
        }
        catch (InvocationTargetException ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
        return proxy;
    }
}

