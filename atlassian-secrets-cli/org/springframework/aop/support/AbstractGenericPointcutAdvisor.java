/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public abstract class AbstractGenericPointcutAdvisor
extends AbstractPointcutAdvisor {
    private Advice advice = EMPTY_ADVICE;

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    public String toString() {
        return this.getClass().getName() + ": advice [" + this.getAdvice() + "]";
    }
}

