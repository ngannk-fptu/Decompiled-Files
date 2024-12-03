/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.conn.ConnectionRequest
 */
package com.amazonaws.http.conn;

import com.amazonaws.http.conn.Wrapped;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.metrics.ServiceLatencyProvider;
import com.amazonaws.metrics.ServiceMetricCollector;
import com.amazonaws.util.AWSServiceMetrics;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectionRequest;

class ClientConnectionRequestFactory {
    private static final Log log = LogFactory.getLog(ClientConnectionRequestFactory.class);
    private static final Class<?>[] interfaces = new Class[]{ConnectionRequest.class, Wrapped.class};

    ClientConnectionRequestFactory() {
    }

    static ConnectionRequest wrap(ConnectionRequest orig) {
        if (orig instanceof Wrapped) {
            throw new IllegalArgumentException();
        }
        return (ConnectionRequest)Proxy.newProxyInstance(ClientConnectionRequestFactory.class.getClassLoader(), interfaces, (InvocationHandler)new Handler(orig));
    }

    private static class Handler
    implements InvocationHandler {
        private final ConnectionRequest orig;

        Handler(ConnectionRequest orig) {
            this.orig = orig;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!"get".equals(method.getName())) return method.invoke((Object)this.orig, args);
            ServiceLatencyProvider latencyProvider = new ServiceLatencyProvider(AWSServiceMetrics.HttpClientGetConnectionTime);
            try {
                Object object = method.invoke((Object)this.orig, args);
                ((ServiceMetricCollector)AwsSdkMetrics.getServiceMetricCollector()).collectLatency(latencyProvider.endTiming());
                return object;
            }
            catch (Throwable throwable) {
                try {
                    ((ServiceMetricCollector)AwsSdkMetrics.getServiceMetricCollector()).collectLatency(latencyProvider.endTiming());
                    throw throwable;
                }
                catch (InvocationTargetException e) {
                    log.debug((Object)"", (Throwable)e);
                    throw e.getCause();
                }
            }
        }
    }
}

