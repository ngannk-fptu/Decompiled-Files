/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.IntroductionInfoSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DelegatingIntroductionInterceptor
extends IntroductionInfoSupport
implements IntroductionInterceptor {
    @Nullable
    private Object delegate;

    public DelegatingIntroductionInterceptor(Object delegate) {
        this.init(delegate);
    }

    protected DelegatingIntroductionInterceptor() {
        this.init(this);
    }

    private void init(Object delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
        this.implementInterfacesOnObject(delegate);
        this.suppressInterface(IntroductionInterceptor.class);
        this.suppressInterface(DynamicIntroductionAdvice.class);
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (this.isMethodOnIntroducedInterface(mi)) {
            Object retVal = AopUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());
            if (retVal == this.delegate && mi instanceof ProxyMethodInvocation) {
                Object proxy = ((ProxyMethodInvocation)mi).getProxy();
                if (mi.getMethod().getReturnType().isInstance(proxy)) {
                    retVal = proxy;
                }
            }
            return retVal;
        }
        return this.doProceed(mi);
    }

    @Nullable
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        return mi.proceed();
    }
}

