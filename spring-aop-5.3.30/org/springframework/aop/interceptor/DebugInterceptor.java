/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.lang.Nullable;

public class DebugInterceptor
extends SimpleTraceInterceptor {
    private volatile long count;

    public DebugInterceptor() {
    }

    public DebugInterceptor(boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        DebugInterceptor debugInterceptor = this;
        synchronized (debugInterceptor) {
            ++this.count;
        }
        return super.invoke(invocation);
    }

    @Override
    protected String getInvocationDescription(MethodInvocation invocation) {
        return invocation + "; count=" + this.count;
    }

    public long getCount() {
        return this.count;
    }

    public synchronized void resetCount() {
        this.count = 0L;
    }
}

