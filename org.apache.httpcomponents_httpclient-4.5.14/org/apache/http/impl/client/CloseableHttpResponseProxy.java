/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.util.EntityUtils
 */
package org.apache.http.impl.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

@Deprecated
class CloseableHttpResponseProxy
implements InvocationHandler {
    private static final Constructor<?> CONSTRUCTOR;
    private final HttpResponse original;

    CloseableHttpResponseProxy(HttpResponse original) {
        this.original = original;
    }

    public void close() throws IOException {
        HttpEntity entity = this.original.getEntity();
        EntityUtils.consume((HttpEntity)entity);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String mname = method.getName();
        if (mname.equals("close")) {
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

    public static CloseableHttpResponse newProxy(HttpResponse original) {
        try {
            return (CloseableHttpResponse)CONSTRUCTOR.newInstance(new CloseableHttpResponseProxy(original));
        }
        catch (InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static {
        try {
            CONSTRUCTOR = Proxy.getProxyClass(CloseableHttpResponseProxy.class.getClassLoader(), CloseableHttpResponse.class).getConstructor(InvocationHandler.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

