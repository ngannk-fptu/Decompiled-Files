/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.util.Objects;
import net.java.ao.DelegateConnection;
import net.java.ao.DelegateLoggingPreparedStatement;
import net.java.ao.DelegateLoggingStatement;
import net.java.ao.sql.CallStackProvider;
import net.java.ao.sql.LoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DelegateConnectionHandler
implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegateConnectionHandler.class);
    private static final CallStackProvider CALL_STACK_PROVIDER = new CallStackProvider();
    public static final String CREATE_STATEMENT_METHOD = "createStatement";
    public static final String PREPARE_STATEMENT_METHOD = "prepareStatement";
    private final Connection delegate;
    private boolean closeable;
    private boolean extraLogging;

    private DelegateConnectionHandler(Connection delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.closeable = true;
        this.extraLogging = false;
    }

    private DelegateConnectionHandler(Connection delegate, boolean extraLogging) {
        this(delegate);
        this.extraLogging = extraLogging;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (DelegateConnectionHandler.isSetCloseableMethod(method)) {
            this.setCloseable((Boolean)args[0]);
            return Void.TYPE;
        }
        if (DelegateConnectionHandler.isIsCloseableMethod(method)) {
            return this.isCloseable();
        }
        if (DelegateConnectionHandler.isCloseMethod(method)) {
            this.close();
            return Void.TYPE;
        }
        if (DelegateConnectionHandler.isIsClosedMethod(method)) {
            return this.isClosed();
        }
        if (this.isExtraLoggingEnabled()) {
            if (method.getName().equals(CREATE_STATEMENT_METHOD)) {
                return new DelegateLoggingStatement((Statement)this.delegate(method, args), new LoggingInterceptor(LOGGER, CALL_STACK_PROVIDER, Clock.systemUTC(), this.isCallStackLoggingEnabled()));
            }
            if (method.getName().equals(PREPARE_STATEMENT_METHOD)) {
                return new DelegateLoggingPreparedStatement((PreparedStatement)this.delegate(method, args), new LoggingInterceptor(LOGGER, CALL_STACK_PROVIDER, Clock.systemUTC(), this.isCallStackLoggingEnabled()), (String)args[0]);
            }
        }
        return this.delegate(method, args);
    }

    private boolean isCallStackLoggingEnabled() {
        return Boolean.getBoolean("net.java.ao.sql.logging.callstack");
    }

    private boolean isExtraLoggingEnabled() {
        return this.extraLogging;
    }

    private void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    private boolean isCloseable() {
        return this.closeable;
    }

    private void close() throws SQLException {
        if (this.isCloseable()) {
            this.delegate.close();
        }
    }

    private boolean isClosed() throws SQLException {
        return this.delegate.isClosed();
    }

    private Object delegate(Method method, Object[] args) throws Throwable {
        Method m = this.delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
        m.setAccessible(true);
        try {
            return m.invoke((Object)this.delegate, args);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public static DelegateConnection newInstance(Connection c) {
        return DelegateConnectionHandler.newInstance(c, false);
    }

    public static DelegateConnection newInstance(Connection c, boolean extraLogging) {
        return (DelegateConnection)Proxy.newProxyInstance(DelegateConnectionHandler.class.getClassLoader(), new Class[]{DelegateConnection.class}, (InvocationHandler)new DelegateConnectionHandler(c, extraLogging));
    }

    private static boolean isSetCloseableMethod(Method method) {
        return method.getName().equals("setCloseable") && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(Boolean.TYPE);
    }

    private static boolean isIsCloseableMethod(Method method) {
        return method.getName().equals("isCloseable") && method.getParameterTypes().length == 0;
    }

    private static boolean isCloseMethod(Method method) {
        return method.getName().equals("close") && method.getParameterTypes().length == 0;
    }

    private static boolean isIsClosedMethod(Method method) {
        return method.getName().equals("isClosed") && method.getParameterTypes().length == 0;
    }
}

