/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.NClob;
import org.hibernate.engine.jdbc.ClobProxy;
import org.hibernate.engine.jdbc.NClobImplementer;

public class NClobProxy
extends ClobProxy {
    public static final Class[] PROXY_INTERFACES = new Class[]{NClob.class, NClobImplementer.class};

    protected NClobProxy(String string) {
        super(string);
    }

    protected NClobProxy(Reader reader, long length) {
        super(reader, length);
    }

    public static NClob generateProxy(String string) {
        return (NClob)Proxy.newProxyInstance(NClobProxy.getProxyClassLoader(), PROXY_INTERFACES, (InvocationHandler)new ClobProxy(string));
    }

    public static NClob generateProxy(Reader reader, long length) {
        return (NClob)Proxy.newProxyInstance(NClobProxy.getProxyClassLoader(), PROXY_INTERFACES, (InvocationHandler)new ClobProxy(reader, length));
    }

    protected static ClassLoader getProxyClassLoader() {
        return NClobImplementer.class.getClassLoader();
    }
}

