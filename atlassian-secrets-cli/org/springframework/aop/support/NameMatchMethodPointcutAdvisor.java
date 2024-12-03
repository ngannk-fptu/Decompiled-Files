/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class NameMatchMethodPointcutAdvisor
extends AbstractGenericPointcutAdvisor {
    private final NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();

    public NameMatchMethodPointcutAdvisor() {
    }

    public NameMatchMethodPointcutAdvisor(Advice advice) {
        this.setAdvice(advice);
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public void setMappedName(String mappedName) {
        this.pointcut.setMappedName(mappedName);
    }

    public void setMappedNames(String ... mappedNames) {
        this.pointcut.setMappedNames(mappedNames);
    }

    public NameMatchMethodPointcut addMethodName(String name) {
        return this.pointcut.addMethodName(name);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

