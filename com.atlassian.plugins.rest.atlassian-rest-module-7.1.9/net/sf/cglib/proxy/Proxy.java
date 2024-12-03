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
    static /* synthetic */ Class class$net$sf$cglib$proxy$Proxy$ProxyImpl;
    static /* synthetic */ Class class$net$sf$cglib$proxy$InvocationHandler;
    static /* synthetic */ Class class$net$sf$cglib$proxy$NoOp;

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
        e.setSuperclass(class$net$sf$cglib$proxy$Proxy$ProxyImpl == null ? (class$net$sf$cglib$proxy$Proxy$ProxyImpl = Proxy.class$("net.sf.cglib.proxy.Proxy$ProxyImpl")) : class$net$sf$cglib$proxy$Proxy$ProxyImpl);
        e.setInterfaces(interfaces);
        e.setCallbackTypes(new Class[]{class$net$sf$cglib$proxy$InvocationHandler == null ? (class$net$sf$cglib$proxy$InvocationHandler = Proxy.class$("net.sf.cglib.proxy.InvocationHandler")) : class$net$sf$cglib$proxy$InvocationHandler, class$net$sf$cglib$proxy$NoOp == null ? (class$net$sf$cglib$proxy$NoOp = Proxy.class$("net.sf.cglib.proxy.NoOp")) : class$net$sf$cglib$proxy$NoOp});
        e.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
        e.setUseFactory(false);
        return e.createClass();
    }

    public static boolean isProxyClass(Class cl) {
        return cl.getSuperclass().equals(class$net$sf$cglib$proxy$Proxy$ProxyImpl == null ? (class$net$sf$cglib$proxy$Proxy$ProxyImpl = Proxy.class$("net.sf.cglib.proxy.Proxy$ProxyImpl")) : class$net$sf$cglib$proxy$Proxy$ProxyImpl);
    }

    public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h) {
        try {
            Class clazz = Proxy.getProxyClass(loader, interfaces);
            return clazz.getConstructor(class$net$sf$cglib$proxy$InvocationHandler == null ? (class$net$sf$cglib$proxy$InvocationHandler = Proxy.class$("net.sf.cglib.proxy.InvocationHandler")) : class$net$sf$cglib$proxy$InvocationHandler).newInstance(h);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class ProxyImpl
    extends Proxy {
        protected ProxyImpl(InvocationHandler h) {
            super(h);
        }
    }
}

