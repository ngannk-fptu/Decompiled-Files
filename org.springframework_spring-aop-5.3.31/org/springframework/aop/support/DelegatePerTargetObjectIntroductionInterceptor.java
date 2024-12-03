/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.aop.support;

import java.util.Map;
import java.util.WeakHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.IntroductionInfoSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class DelegatePerTargetObjectIntroductionInterceptor
extends IntroductionInfoSupport
implements IntroductionInterceptor {
    private final Map<Object, Object> delegateMap = new WeakHashMap<Object, Object>();
    private Class<?> defaultImplType;
    private Class<?> interfaceType;

    public DelegatePerTargetObjectIntroductionInterceptor(Class<?> defaultImplType, Class<?> interfaceType) {
        this.defaultImplType = defaultImplType;
        this.interfaceType = interfaceType;
        Object delegate = this.createNewDelegate();
        this.implementInterfacesOnObject(delegate);
        this.suppressInterface(IntroductionInterceptor.class);
        this.suppressInterface(DynamicIntroductionAdvice.class);
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (this.isMethodOnIntroducedInterface(mi)) {
            Object delegate = this.getIntroductionDelegateFor(mi.getThis());
            Object retVal = AopUtils.invokeJoinpointUsingReflection(delegate, mi.getMethod(), mi.getArguments());
            if (retVal == delegate && mi instanceof ProxyMethodInvocation) {
                retVal = ((ProxyMethodInvocation)mi).getProxy();
            }
            return retVal;
        }
        return this.doProceed(mi);
    }

    @Nullable
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        return mi.proceed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object getIntroductionDelegateFor(@Nullable Object targetObject) {
        Map<Object, Object> map = this.delegateMap;
        synchronized (map) {
            if (this.delegateMap.containsKey(targetObject)) {
                return this.delegateMap.get(targetObject);
            }
            Object delegate = this.createNewDelegate();
            this.delegateMap.put(targetObject, delegate);
            return delegate;
        }
    }

    private Object createNewDelegate() {
        try {
            return ReflectionUtils.accessibleConstructor(this.defaultImplType, (Class[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot create default implementation for '" + this.interfaceType.getName() + "' mixin (" + this.defaultImplType.getName() + "): " + ex);
        }
    }
}

