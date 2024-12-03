/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.lang.Nullable;

public class ReflectiveMethodInvocation
implements ProxyMethodInvocation,
Cloneable {
    protected final Object proxy;
    @Nullable
    protected final Object target;
    protected final Method method;
    protected Object[] arguments = new Object[0];
    @Nullable
    private final Class<?> targetClass;
    @Nullable
    private Map<String, Object> userAttributes;
    protected final List<?> interceptorsAndDynamicMethodMatchers;
    private int currentInterceptorIndex = -1;

    protected ReflectiveMethodInvocation(Object proxy, @Nullable Object target, Method method, @Nullable Object[] arguments, @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments);
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override
    public final Object getProxy() {
        return this.proxy;
    }

    @Override
    @Nullable
    public final Object getThis() {
        return this.target;
    }

    @Override
    public final AccessibleObject getStaticPart() {
        return this.method;
    }

    @Override
    public final Method getMethod() {
        return this.method;
    }

    @Override
    public final Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public void setArguments(Object ... arguments) {
        this.arguments = arguments;
    }

    @Override
    @Nullable
    public Object proceed() throws Throwable {
        Object interceptorOrInterceptionAdvice;
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.invokeJoinpoint();
        }
        if ((interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex)) instanceof InterceptorAndDynamicMethodMatcher) {
            InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher)interceptorOrInterceptionAdvice;
            if (dm.methodMatcher.matches(this.method, this.targetClass, this.arguments)) {
                return dm.interceptor.invoke(this);
            }
            return this.proceed();
        }
        return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
    }

    @Nullable
    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
    }

    @Override
    public MethodInvocation invocableClone() {
        Object[] cloneArguments = this.arguments;
        if (this.arguments.length > 0) {
            cloneArguments = new Object[this.arguments.length];
            System.arraycopy(this.arguments, 0, cloneArguments, 0, this.arguments.length);
        }
        return this.invocableClone(cloneArguments);
    }

    @Override
    public MethodInvocation invocableClone(Object ... arguments) {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<String, Object>();
        }
        try {
            ReflectiveMethodInvocation clone = (ReflectiveMethodInvocation)this.clone();
            clone.arguments = arguments;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Should be able to clone object of type [" + this.getClass() + "]: " + ex);
        }
    }

    @Override
    public void setUserAttribute(String key, @Nullable Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<String, Object>();
            }
            this.userAttributes.put(key, value);
        } else if (this.userAttributes != null) {
            this.userAttributes.remove(key);
        }
    }

    @Override
    @Nullable
    public Object getUserAttribute(String key) {
        return this.userAttributes != null ? this.userAttributes.get(key) : null;
    }

    public Map<String, Object> getUserAttributes() {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<String, Object>();
        }
        return this.userAttributes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
        sb.append(this.method).append("; ");
        if (this.target == null) {
            sb.append("target is null");
        } else {
            sb.append("target is of class [").append(this.target.getClass().getName()).append(']');
        }
        return sb.toString();
    }
}

