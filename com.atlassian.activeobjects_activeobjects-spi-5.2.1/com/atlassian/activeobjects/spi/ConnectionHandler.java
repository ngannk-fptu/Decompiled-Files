/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class ConnectionHandler
implements InvocationHandler {
    private final Connection delegate;
    private final Closeable closeable;

    public ConnectionHandler(Connection delegate, Closeable closeable) {
        this.delegate = Objects.requireNonNull(delegate);
        this.closeable = Objects.requireNonNull(closeable);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws SQLException {
        if (ConnectionHandler.isCloseMethod(method)) {
            this.closeable.close();
            return Void.TYPE;
        }
        return this.delegate(method, args);
    }

    private Object delegate(Method method, Object[] args) throws SQLException {
        try {
            return method.invoke((Object)this.delegate, args);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new RuntimeException("Unexpected checked exception", cause);
        }
    }

    public static Connection newInstance(Connection c) {
        return ConnectionHandler.newInstance(c, () -> {});
    }

    public static Connection newInstance(Connection c, Closeable closeable) {
        return (Connection)Proxy.newProxyInstance(ConnectionHandler.class.getClassLoader(), new Class[]{Connection.class}, (InvocationHandler)new ConnectionHandler(c, closeable));
    }

    private static boolean isCloseMethod(Method method) {
        return method.getName().equals("close") && method.getParameterTypes().length == 0;
    }

    public static interface Closeable {
        public void close() throws SQLException;
    }
}

