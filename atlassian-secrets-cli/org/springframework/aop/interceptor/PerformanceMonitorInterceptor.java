/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.StopWatch;

public class PerformanceMonitorInterceptor
extends AbstractMonitoringInterceptor {
    public PerformanceMonitorInterceptor() {
    }

    public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = this.createInvocationTraceName(invocation);
        StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            Object object = invocation.proceed();
            return object;
        }
        finally {
            stopWatch.stop();
            this.writeToLog(logger, stopWatch.shortSummary());
        }
    }
}

