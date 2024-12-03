/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public class TrapException
extends JdbcInterceptor {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return super.invoke(proxy, method, args);
        }
        catch (Exception t) {
            Throwable exception = t;
            if (t instanceof InvocationTargetException && t.getCause() != null && (exception = t.getCause()) instanceof Error) {
                throw exception;
            }
            Class<?> exceptionClass = exception.getClass();
            if (!this.isDeclaredException(method, exceptionClass)) {
                if (this.isDeclaredException(method, SQLException.class)) {
                    SQLException sqlx = new SQLException("Uncaught underlying exception.");
                    sqlx.initCause(exception);
                    exception = sqlx;
                } else {
                    RuntimeException rx = new RuntimeException("Uncaught underlying exception.");
                    rx.initCause(exception);
                    exception = rx;
                }
            }
            throw exception;
        }
    }

    public boolean isDeclaredException(Method m, Class<?> clazz) {
        for (Class<?> cl : m.getExceptionTypes()) {
            if (!cl.equals(clazz) && !cl.isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
    }
}

