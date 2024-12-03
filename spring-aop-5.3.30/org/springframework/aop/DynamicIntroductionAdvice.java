/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.aopalliance.aop.Advice;

public interface DynamicIntroductionAdvice
extends Advice {
    public boolean implementsInterface(Class<?> var1);
}

