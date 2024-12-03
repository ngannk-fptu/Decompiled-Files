/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.WrappedBlob;

public class SerializableBlobProxy
implements InvocationHandler,
Serializable {
    private static final Class[] PROXY_INTERFACES = new Class[]{Blob.class, WrappedBlob.class, Serializable.class};
    private final transient Blob blob;

    private SerializableBlobProxy(Blob blob) {
        this.blob = blob;
    }

    public Blob getWrappedBlob() {
        if (this.blob == null) {
            throw new IllegalStateException("Blobs may not be accessed after serialization");
        }
        return this.blob;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getWrappedBlob".equals(method.getName())) {
            return this.getWrappedBlob();
        }
        try {
            return method.invoke((Object)this.getWrappedBlob(), args);
        }
        catch (AbstractMethodError e) {
            throw new HibernateException("The JDBC driver does not implement the method: " + method, e);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public static Blob generateProxy(Blob blob) {
        return (Blob)Proxy.newProxyInstance(SerializableBlobProxy.getProxyClassLoader(), PROXY_INTERFACES, (InvocationHandler)new SerializableBlobProxy(blob));
    }

    public static ClassLoader getProxyClassLoader() {
        return WrappedBlob.class.getClassLoader();
    }
}

