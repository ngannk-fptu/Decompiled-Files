/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public abstract class StaticMethodMatcherPointcutAdvisor
extends StaticMethodMatcherPointcut
implements PointcutAdvisor,
Ordered,
Serializable {
    private Advice advice = EMPTY_ADVICE;
    private int order = Integer.MAX_VALUE;

    public StaticMethodMatcherPointcutAdvisor() {
    }

    public StaticMethodMatcherPointcutAdvisor(Advice advice) {
        Assert.notNull((Object)advice, "Advice must not be null");
        this.advice = advice;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Pointcut getPointcut() {
        return this;
    }
}

