/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj;

import org.springframework.aop.PointcutAdvisor;

public interface InstantiationModelAwarePointcutAdvisor
extends PointcutAdvisor {
    public boolean isLazy();

    public boolean isAdviceInstantiated();
}

