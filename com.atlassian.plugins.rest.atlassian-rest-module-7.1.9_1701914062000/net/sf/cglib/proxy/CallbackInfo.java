/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackGenerator;
import net.sf.cglib.proxy.DispatcherGenerator;
import net.sf.cglib.proxy.FixedValueGenerator;
import net.sf.cglib.proxy.InvocationHandlerGenerator;
import net.sf.cglib.proxy.LazyLoaderGenerator;
import net.sf.cglib.proxy.MethodInterceptorGenerator;
import net.sf.cglib.proxy.NoOpGenerator;
import org.objectweb.asm.Type;

class CallbackInfo {
    private Class cls;
    private CallbackGenerator generator;
    private Type type;
    private static final CallbackInfo[] CALLBACKS = new CallbackInfo[]{new CallbackInfo(class$net$sf$cglib$proxy$NoOp == null ? (class$net$sf$cglib$proxy$NoOp = CallbackInfo.class$("net.sf.cglib.proxy.NoOp")) : class$net$sf$cglib$proxy$NoOp, NoOpGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$MethodInterceptor == null ? (class$net$sf$cglib$proxy$MethodInterceptor = CallbackInfo.class$("net.sf.cglib.proxy.MethodInterceptor")) : class$net$sf$cglib$proxy$MethodInterceptor, MethodInterceptorGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$InvocationHandler == null ? (class$net$sf$cglib$proxy$InvocationHandler = CallbackInfo.class$("net.sf.cglib.proxy.InvocationHandler")) : class$net$sf$cglib$proxy$InvocationHandler, InvocationHandlerGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$LazyLoader == null ? (class$net$sf$cglib$proxy$LazyLoader = CallbackInfo.class$("net.sf.cglib.proxy.LazyLoader")) : class$net$sf$cglib$proxy$LazyLoader, LazyLoaderGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$Dispatcher == null ? (class$net$sf$cglib$proxy$Dispatcher = CallbackInfo.class$("net.sf.cglib.proxy.Dispatcher")) : class$net$sf$cglib$proxy$Dispatcher, DispatcherGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$FixedValue == null ? (class$net$sf$cglib$proxy$FixedValue = CallbackInfo.class$("net.sf.cglib.proxy.FixedValue")) : class$net$sf$cglib$proxy$FixedValue, FixedValueGenerator.INSTANCE), new CallbackInfo(class$net$sf$cglib$proxy$ProxyRefDispatcher == null ? (class$net$sf$cglib$proxy$ProxyRefDispatcher = CallbackInfo.class$("net.sf.cglib.proxy.ProxyRefDispatcher")) : class$net$sf$cglib$proxy$ProxyRefDispatcher, DispatcherGenerator.PROXY_REF_INSTANCE)};
    static /* synthetic */ Class class$net$sf$cglib$proxy$NoOp;
    static /* synthetic */ Class class$net$sf$cglib$proxy$MethodInterceptor;
    static /* synthetic */ Class class$net$sf$cglib$proxy$InvocationHandler;
    static /* synthetic */ Class class$net$sf$cglib$proxy$LazyLoader;
    static /* synthetic */ Class class$net$sf$cglib$proxy$Dispatcher;
    static /* synthetic */ Class class$net$sf$cglib$proxy$FixedValue;
    static /* synthetic */ Class class$net$sf$cglib$proxy$ProxyRefDispatcher;

    public static Type[] determineTypes(Class[] callbackTypes) {
        Type[] types = new Type[callbackTypes.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = CallbackInfo.determineType(callbackTypes[i]);
        }
        return types;
    }

    public static Type[] determineTypes(Callback[] callbacks) {
        Type[] types = new Type[callbacks.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = CallbackInfo.determineType(callbacks[i]);
        }
        return types;
    }

    public static CallbackGenerator[] getGenerators(Type[] callbackTypes) {
        CallbackGenerator[] generators = new CallbackGenerator[callbackTypes.length];
        for (int i = 0; i < generators.length; ++i) {
            generators[i] = CallbackInfo.getGenerator(callbackTypes[i]);
        }
        return generators;
    }

    private CallbackInfo(Class cls, CallbackGenerator generator) {
        this.cls = cls;
        this.generator = generator;
        this.type = Type.getType(cls);
    }

    private static Type determineType(Callback callback) {
        if (callback == null) {
            throw new IllegalStateException("Callback is null");
        }
        return CallbackInfo.determineType(callback.getClass());
    }

    private static Type determineType(Class callbackType) {
        Class cur = null;
        for (int i = 0; i < CALLBACKS.length; ++i) {
            CallbackInfo info = CALLBACKS[i];
            if (!info.cls.isAssignableFrom(callbackType)) continue;
            if (cur != null) {
                throw new IllegalStateException("Callback implements both " + cur + " and " + info.cls);
            }
            cur = info.cls;
        }
        if (cur == null) {
            throw new IllegalStateException("Unknown callback type " + callbackType);
        }
        return Type.getType(cur);
    }

    private static CallbackGenerator getGenerator(Type callbackType) {
        for (int i = 0; i < CALLBACKS.length; ++i) {
            CallbackInfo info = CALLBACKS[i];
            if (!info.type.equals(callbackType)) continue;
            return info.generator;
        }
        throw new IllegalStateException("Unknown callback type " + callbackType);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

