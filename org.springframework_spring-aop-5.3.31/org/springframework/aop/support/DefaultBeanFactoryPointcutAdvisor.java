/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

public class DefaultBeanFactoryPointcutAdvisor
extends AbstractBeanFactoryPointcutAdvisor {
    private Pointcut pointcut = Pointcut.TRUE;

    public void setPointcut(@Nullable Pointcut pointcut) {
        this.pointcut = pointcut != null ? pointcut : Pointcut.TRUE;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": pointcut [" + this.getPointcut() + "]; advice bean '" + this.getAdviceBeanName() + "'";
    }
}

