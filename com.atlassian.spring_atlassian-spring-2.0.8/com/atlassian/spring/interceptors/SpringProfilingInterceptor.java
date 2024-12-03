/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.ProfilingUtils
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 */
package com.atlassian.spring.interceptors;

import com.atlassian.util.profiling.ProfilingUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SpringProfilingInterceptor
implements MethodInterceptor {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!UtilTimerStack.isActive()) {
            return invocation.proceed();
        }
        String name = ProfilingUtils.getJustClassName(invocation.getMethod().getDeclaringClass()) + "." + invocation.getMethod().getName() + "()";
        UtilTimerStack.push((String)name);
        try {
            Object object = invocation.proceed();
            return object;
        }
        finally {
            UtilTimerStack.pop((String)name);
        }
    }
}

