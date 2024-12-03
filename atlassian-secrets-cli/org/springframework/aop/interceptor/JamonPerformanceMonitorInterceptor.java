/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jamonapi.MonKey
 *  com.jamonapi.MonKeyImp
 *  com.jamonapi.Monitor
 *  com.jamonapi.MonitorFactory
 *  com.jamonapi.utils.Misc
 */
package org.springframework.aop.interceptor;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

public class JamonPerformanceMonitorInterceptor
extends AbstractMonitoringInterceptor {
    private boolean trackAllInvocations = false;

    public JamonPerformanceMonitorInterceptor() {
    }

    public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }

    public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger, boolean trackAllInvocations) {
        this.setUseDynamicLogger(useDynamicLogger);
        this.setTrackAllInvocations(trackAllInvocations);
    }

    public void setTrackAllInvocations(boolean trackAllInvocations) {
        this.trackAllInvocations = trackAllInvocations;
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return this.trackAllInvocations || this.isLogEnabled(logger);
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = this.createInvocationTraceName(invocation);
        MonKeyImp key = new MonKeyImp(name, (Object)name, "ms.");
        Monitor monitor = MonitorFactory.start((MonKey)key);
        try {
            Object object = invocation.proceed();
            return object;
        }
        catch (Throwable ex) {
            this.trackException((MonKey)key, ex);
            throw ex;
        }
        finally {
            monitor.stop();
            if (!this.trackAllInvocations || this.isLogEnabled(logger)) {
                this.writeToLog(logger, "JAMon performance statistics for method [" + name + "]:\n" + monitor);
            }
        }
    }

    protected void trackException(MonKey key, Throwable ex) {
        String stackTrace = "stackTrace=" + Misc.getExceptionTrace((Throwable)ex);
        key.setDetails((Object)stackTrace);
        MonitorFactory.add((MonKey)new MonKeyImp(ex.getClass().getName(), (Object)stackTrace, "Exception"), (double)1.0);
        MonitorFactory.add((MonKey)new MonKeyImp("com.jamonapi.Exceptions", (Object)stackTrace, "Exception"), (double)1.0);
    }
}

