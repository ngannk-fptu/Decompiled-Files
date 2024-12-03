/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;

public interface PointcutAdvisor
extends Advisor {
    public Pointcut getPointcut();
}

