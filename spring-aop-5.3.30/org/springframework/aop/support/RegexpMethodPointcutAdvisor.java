/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.aop.support.AbstractRegexpMethodPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class RegexpMethodPointcutAdvisor
extends AbstractGenericPointcutAdvisor {
    @Nullable
    private String[] patterns;
    @Nullable
    private AbstractRegexpMethodPointcut pointcut;
    private final Object pointcutMonitor = new SerializableMonitor();

    public RegexpMethodPointcutAdvisor() {
    }

    public RegexpMethodPointcutAdvisor(Advice advice) {
        this.setAdvice(advice);
    }

    public RegexpMethodPointcutAdvisor(String pattern, Advice advice) {
        this.setPattern(pattern);
        this.setAdvice(advice);
    }

    public RegexpMethodPointcutAdvisor(String[] patterns, Advice advice) {
        this.setPatterns(patterns);
        this.setAdvice(advice);
    }

    public void setPattern(String pattern) {
        this.setPatterns(pattern);
    }

    public void setPatterns(String ... patterns) {
        this.patterns = patterns;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Pointcut getPointcut() {
        Object object = this.pointcutMonitor;
        synchronized (object) {
            if (this.pointcut == null) {
                this.pointcut = this.createPointcut();
                if (this.patterns != null) {
                    this.pointcut.setPatterns(this.patterns);
                }
            }
            return this.pointcut;
        }
    }

    protected AbstractRegexpMethodPointcut createPointcut() {
        return new JdkRegexpMethodPointcut();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": advice [" + this.getAdvice() + "], pointcut patterns " + ObjectUtils.nullSafeToString((Object[])this.patterns);
    }

    private static class SerializableMonitor
    implements Serializable {
        private SerializableMonitor() {
        }
    }
}

