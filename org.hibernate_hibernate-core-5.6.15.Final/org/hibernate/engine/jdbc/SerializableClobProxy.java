/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Clob;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.WrappedClob;

public class SerializableClobProxy
implements InvocationHandler,
Serializable {
    private static final Class[] PROXY_INTERFACES = new Class[]{Clob.class, WrappedClob.class, Serializable.class};
    private final transient Clob clob;

    protected SerializableClobProxy(Clob clob) {
        this.clob = clob;
    }

    public Clob getWrappedClob() {
        if (this.clob == null) {
            throw new IllegalStateException("Clobs may not be accessed after serialization");
        }
        return this.clob;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getWrappedClob".equals(method.getName())) {
            return this.getWrappedClob();
        }
        try {
            return method.invoke((Object)this.getWrappedClob(), args);
        }
        catch (AbstractMethodError e) {
            throw new HibernateException("The JDBC driver does not implement the method: " + method, e);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public static Clob generateProxy(Clob clob) {
        return (Clob)Proxy.newProxyInstance(SerializableClobProxy.getProxyClassLoader(), PROXY_INTERFACES, (InvocationHandler)new SerializableClobProxy(clob));
    }

    public static ClassLoader getProxyClassLoader() {
        return WrappedClob.class.getClassLoader();
    }
}

