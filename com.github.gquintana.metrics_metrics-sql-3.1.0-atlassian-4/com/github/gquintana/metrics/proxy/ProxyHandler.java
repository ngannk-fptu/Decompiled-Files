/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.MethodInvocation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

public class ProxyHandler<T>
implements InvocationHandler {
    protected final T delegate;
    protected static final InvocationFilter ALL_INVOCATION_FILTER = new InvocationFilter(){

        @Override
        public boolean isIntercepted(Method method) {
            return true;
        }
    };

    public ProxyHandler(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            if ("hashCode".equals(method.getName())) {
                return this.hashCode();
            }
            if ("toString".equals(method.getName())) {
                return this.toString();
            }
        } else if (args.length == 1 && "equals".equals(method.getName()) && Object.class == method.getParameterTypes()[0]) {
            Object other = args[0];
            return proxy.getClass().isInstance(other) && this.equals(Proxy.getInvocationHandler(other));
        }
        return this.invoke(new MethodInvocation<T>(this.delegate, proxy, method, args));
    }

    protected Object invoke(MethodInvocation<T> delegatingMethodInvocation) throws Throwable {
        return delegatingMethodInvocation.proceed();
    }

    public T getDelegate() {
        return this.delegate;
    }

    public InvocationFilter getInvocationFilter() {
        return ALL_INVOCATION_FILTER;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProxyHandler that = (ProxyHandler)o;
        return Objects.equals(this.delegate, that.delegate);
    }

    public int hashCode() {
        return Objects.hash(this.delegate);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + "delegate=" + this.delegate + '}';
    }

    protected static final class MethodNamesInvocationFilter
    implements InvocationFilter {
        private final String[] methodNames;

        public MethodNamesInvocationFilter(String ... methodNames) {
            this.methodNames = methodNames;
            Arrays.sort(this.methodNames);
        }

        @Override
        public boolean isIntercepted(Method method) {
            return Arrays.binarySearch(this.methodNames, method.getName()) >= 0;
        }
    }

    public static interface InvocationFilter {
        public boolean isIntercepted(Method var1);
    }
}

