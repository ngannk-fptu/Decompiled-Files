/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.cglib.proxy.$MethodInterceptor;
import com.google.inject.internal.cglib.proxy.$MethodProxy;
import com.google.inject.internal.util.$Lists;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InterceptorStackCallback
implements $MethodInterceptor {
    private static final Set<String> AOP_INTERNAL_CLASSES = new HashSet<String>(Arrays.asList(InterceptorStackCallback.class.getName(), InterceptedMethodInvocation.class.getName(), $MethodProxy.class.getName()));
    final MethodInterceptor[] interceptors;
    final Method method;

    public InterceptorStackCallback(Method method, List<MethodInterceptor> interceptors) {
        this.method = method;
        this.interceptors = interceptors.toArray(new MethodInterceptor[interceptors.size()]);
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] arguments, $MethodProxy methodProxy) throws Throwable {
        return new InterceptedMethodInvocation(proxy, methodProxy, arguments).proceed();
    }

    private void pruneStacktrace(Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            StackTraceElement[] stackTrace = t.getStackTrace();
            ArrayList<StackTraceElement> pruned = $Lists.newArrayList();
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (AOP_INTERNAL_CLASSES.contains(className) || className.contains("$EnhancerByGuice$")) continue;
                pruned.add(element);
            }
            t.setStackTrace(pruned.toArray(new StackTraceElement[pruned.size()]));
        }
    }

    private class InterceptedMethodInvocation
    implements MethodInvocation {
        final Object proxy;
        final Object[] arguments;
        final $MethodProxy methodProxy;
        int index = -1;

        public InterceptedMethodInvocation(Object proxy, $MethodProxy methodProxy, Object[] arguments) {
            this.proxy = proxy;
            this.methodProxy = methodProxy;
            this.arguments = arguments;
        }

        public Object proceed() throws Throwable {
            try {
                ++this.index;
                Object object = this.index == InterceptorStackCallback.this.interceptors.length ? this.methodProxy.invokeSuper(this.proxy, this.arguments) : InterceptorStackCallback.this.interceptors[this.index].invoke(this);
                return object;
            }
            catch (Throwable t) {
                InterceptorStackCallback.this.pruneStacktrace(t);
                throw t;
            }
            finally {
                --this.index;
            }
        }

        public Method getMethod() {
            return InterceptorStackCallback.this.method;
        }

        public Object[] getArguments() {
            return this.arguments;
        }

        public Object getThis() {
            return this.proxy;
        }

        public AccessibleObject getStaticPart() {
            return this.getMethod();
        }
    }
}

