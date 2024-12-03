/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public class DisposableConnectionFacade
extends JdbcInterceptor {
    protected DisposableConnectionFacade(JdbcInterceptor interceptor) {
        this.setUseEquals(interceptor.isUseEquals());
        this.setNext(interceptor);
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.compare("equals", method)) {
            return this.equals(Proxy.getInvocationHandler(args[0]));
        }
        if (this.compare("hashCode", method)) {
            return this.hashCode();
        }
        if (this.getNext() == null) {
            if (this.compare("isClosed", method)) {
                return Boolean.TRUE;
            }
            if (this.compare("close", method)) {
                return null;
            }
            if (this.compare("isValid", method)) {
                return Boolean.FALSE;
            }
        }
        try {
            Object object = super.invoke(proxy, method, args);
            return object;
        }
        catch (NullPointerException e) {
            if (this.getNext() == null) {
                if (this.compare("toString", method)) {
                    String string = "DisposableConnectionFacade[null]";
                    return string;
                }
                throw new SQLException("PooledConnection has already been closed.");
            }
            throw e;
        }
        finally {
            if (this.compare("close", method)) {
                this.setNext(null);
            }
        }
    }
}

