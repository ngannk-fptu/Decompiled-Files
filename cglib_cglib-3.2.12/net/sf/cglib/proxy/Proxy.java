/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.NoOp;

public class Proxy
implements Serializable {
    protected InvocationHandler h;
    private static final CallbackFilter BAD_OBJECT_METHOD_FILTER = new CallbackFilter(){

        public int accept(Method method) {
            String name;
            if (method.getDeclaringClass().getName().equals("java.lang.Object") && !(name = method.getName()).equals("hashCode") && !name.equals("equals") && !name.equals("toString")) {
                return 1;
            }
            return 0;
        }
    };

    protected Proxy(InvocationHandler h) {
        Enhancer.registerCallbacks(this.getClass(), new Callback[]{h, null});
        this.h = h;
    }

    public static InvocationHandler getInvocationHandler(Object proxy) {
        if (!(proxy instanceof ProxyImpl)) {
            throw new IllegalArgumentException("Object is not a proxy");
        }
        return ((Proxy)proxy).h;
    }

    public static Class getProxyClass(ClassLoader loader, Class[] interfaces) {
        Enhancer e = new Enhancer();
        e.setSuperclass(ProxyImpl.class);
        e.setInterfaces(interfaces);
        e.setCallbackTypes(new Class[]{InvocationHandler.class, NoOp.class});
        e.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
        e.setUseFactory(false);
        return e.createClass();
    }

    public static boolean isProxyClass(Class cl) {
        return cl.getSuperclass().equals(ProxyImpl.class);
    }

    public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h) {
        try {
            Class clazz = Proxy.getProxyClass(loader, interfaces);
            return clazz.getConstructor(InvocationHandler.class).newInstance(h);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    private static class ProxyImpl
    extends Proxy {
        protected ProxyImpl(InvocationHandler h) {
            super(h);
        }
    }
}

