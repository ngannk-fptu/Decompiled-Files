/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.http.impl.client.cache;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.cache.IOUtils;

class ResponseProxyHandler
implements InvocationHandler {
    private static final Method CLOSE_METHOD;
    private final HttpResponse original;

    ResponseProxyHandler(HttpResponse original) {
        this.original = original;
    }

    public void close() throws IOException {
        IOUtils.consume(this.original.getEntity());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(CLOSE_METHOD)) {
            this.close();
            return null;
        }
        try {
            return method.invoke((Object)this.original, args);
        }
        catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause != null) {
                throw cause;
            }
            throw ex;
        }
    }

    static {
        try {
            CLOSE_METHOD = Closeable.class.getMethod("close", new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new Error(ex);
        }
    }
}

