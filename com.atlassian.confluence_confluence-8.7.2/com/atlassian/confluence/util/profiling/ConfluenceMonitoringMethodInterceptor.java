/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.ImmutableMap
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceMonitoringMethodInterceptor
implements MethodInterceptor {
    private final String beanName;
    private ConfluenceMonitoring confluenceMonitoring;

    public ConfluenceMonitoringMethodInterceptor(String beanName) {
        this.beanName = beanName;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (null == this.confluenceMonitoring) {
            this.confluenceMonitoring = (ConfluenceMonitoring)ContainerManager.getComponent((String)this.beanName, ConfluenceMonitoring.class);
        }
        try (Split ignored = this.startTimer(invocation.getMethod());){
            Object object = invocation.proceed();
            return object;
        }
    }

    private @NonNull Split startTimer(Method method) {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return this.confluenceMonitoring.startSplit(ConfluenceMonitoringMethodInterceptor.class.getSimpleName(), (Map<String, String>)ImmutableMap.of((Object)"className", (Object)method.getDeclaringClass().getName(), (Object)"methodName", (Object)method.getName()));
        }
        return this.confluenceMonitoring.startSplit(this.methodSignature(method));
    }

    private String methodSignature(Method meth) {
        return meth.getDeclaringClass().getName() + "." + meth.getName();
    }
}

