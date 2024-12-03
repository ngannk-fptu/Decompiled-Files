/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.conn.ConnectionRequest
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.pool.ConnPoolControl
 */
package com.amazonaws.http.conn;

import com.amazonaws.http.conn.ClientConnectionRequestFactory;
import com.amazonaws.http.conn.Wrapped;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.pool.ConnPoolControl;

public class ClientConnectionManagerFactory {
    private static final Log log = LogFactory.getLog(ClientConnectionManagerFactory.class);

    public static HttpClientConnectionManager wrap(HttpClientConnectionManager orig) {
        if (orig instanceof Wrapped) {
            throw new IllegalArgumentException();
        }
        Class[] interfaces = orig instanceof ConnPoolControl ? new Class[]{HttpClientConnectionManager.class, ConnPoolControl.class, Wrapped.class} : new Class[]{HttpClientConnectionManager.class, Wrapped.class};
        return (HttpClientConnectionManager)Proxy.newProxyInstance(ClientConnectionManagerFactory.class.getClassLoader(), interfaces, (InvocationHandler)new Handler(orig));
    }

    private static class Handler
    implements InvocationHandler {
        private final HttpClientConnectionManager orig;

        Handler(HttpClientConnectionManager real) {
            this.orig = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object ret = method.invoke((Object)this.orig, args);
                return ret instanceof ConnectionRequest ? ClientConnectionRequestFactory.wrap((ConnectionRequest)ret) : ret;
            }
            catch (InvocationTargetException e) {
                log.debug((Object)"", (Throwable)e);
                throw e.getCause();
            }
        }
    }
}

