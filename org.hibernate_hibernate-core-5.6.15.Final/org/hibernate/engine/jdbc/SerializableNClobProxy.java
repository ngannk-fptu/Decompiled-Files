/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Clob;
import java.sql.NClob;
import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.hibernate.engine.jdbc.WrappedNClob;

public class SerializableNClobProxy
extends SerializableClobProxy {
    private static final Class[] PROXY_INTERFACES = new Class[]{NClob.class, WrappedNClob.class};

    @Deprecated
    public static boolean isNClob(Clob clob) {
        return NClob.class.isInstance(clob);
    }

    protected SerializableNClobProxy(Clob clob) {
        super(clob);
    }

    public static NClob generateProxy(NClob nclob) {
        return (NClob)Proxy.newProxyInstance(SerializableNClobProxy.getProxyClassLoader(), PROXY_INTERFACES, (InvocationHandler)new SerializableNClobProxy(nclob));
    }

    public static ClassLoader getProxyClassLoader() {
        return SerializableClobProxy.getProxyClassLoader();
    }
}

