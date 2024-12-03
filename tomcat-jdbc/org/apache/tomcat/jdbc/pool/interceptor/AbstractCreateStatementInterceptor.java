/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public abstract class AbstractCreateStatementInterceptor
extends JdbcInterceptor {
    protected static final String CREATE_STATEMENT = "createStatement";
    protected static final int CREATE_STATEMENT_IDX = 0;
    protected static final String PREPARE_STATEMENT = "prepareStatement";
    protected static final int PREPARE_STATEMENT_IDX = 1;
    protected static final String PREPARE_CALL = "prepareCall";
    protected static final int PREPARE_CALL_IDX = 2;
    protected static final String[] STATEMENT_TYPES = new String[]{"createStatement", "prepareStatement", "prepareCall"};
    protected static final int STATEMENT_TYPE_COUNT = STATEMENT_TYPES.length;
    protected static final String EXECUTE = "execute";
    protected static final String EXECUTE_QUERY = "executeQuery";
    protected static final String EXECUTE_UPDATE = "executeUpdate";
    protected static final String EXECUTE_BATCH = "executeBatch";
    protected static final String[] EXECUTE_TYPES = new String[]{"execute", "executeQuery", "executeUpdate", "executeBatch"};
    protected static final Constructor<?>[] constructors = new Constructor[STATEMENT_TYPE_COUNT];

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.compare("close", method)) {
            this.closeInvoked();
            return super.invoke(proxy, method, args);
        }
        boolean process = false;
        if (process = this.isStatement(method, process)) {
            long start = System.currentTimeMillis();
            Object statement = super.invoke(proxy, method, args);
            long delta = System.currentTimeMillis() - start;
            return this.createStatement(proxy, method, args, statement, delta);
        }
        return super.invoke(proxy, method, args);
    }

    protected Constructor<?> getConstructor(int idx, Class<?> clazz) throws NoSuchMethodException {
        if (constructors[idx] == null) {
            Class<?> proxyClass = Proxy.getProxyClass(AbstractCreateStatementInterceptor.class.getClassLoader(), clazz);
            AbstractCreateStatementInterceptor.constructors[idx] = proxyClass.getConstructor(InvocationHandler.class);
        }
        return constructors[idx];
    }

    public abstract Object createStatement(Object var1, Method var2, Object[] var3, Object var4, long var5);

    public abstract void closeInvoked();

    protected boolean isStatement(Method method, boolean process) {
        return this.process(STATEMENT_TYPES, method, process);
    }

    protected boolean isExecute(Method method, boolean process) {
        return this.process(EXECUTE_TYPES, method, process);
    }

    protected boolean process(String[] names, Method method, boolean process) {
        String name = method.getName();
        for (int i = 0; !process && i < names.length; ++i) {
            process = this.compare(names[i], name);
        }
        return process;
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
    }
}

