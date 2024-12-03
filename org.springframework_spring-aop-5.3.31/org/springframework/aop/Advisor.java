/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.aopalliance.aop.Advice;

public interface Advisor {
    public static final Advice EMPTY_ADVICE = new Advice(){};

    public Advice getAdvice();

    public boolean isPerInstance();
}

