/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.lang.Nullable;

public interface AspectJAdvisorFactory {
    public boolean isAspect(Class<?> var1);

    public void validate(Class<?> var1) throws AopConfigException;

    public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory var1);

    @Nullable
    public Advisor getAdvisor(Method var1, MetadataAwareAspectInstanceFactory var2, int var3, String var4);

    @Nullable
    public Advice getAdvice(Method var1, AspectJExpressionPointcut var2, MetadataAwareAspectInstanceFactory var3, int var4, String var5);
}

