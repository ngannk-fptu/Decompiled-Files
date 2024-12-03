/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public class StatementDecoratorInterceptor
extends AbstractCreateStatementInterceptor {
    private static final Log logger = LogFactory.getLog(StatementDecoratorInterceptor.class);
    protected static final String EXECUTE_QUERY = "executeQuery";
    protected static final String GET_GENERATED_KEYS = "getGeneratedKeys";
    protected static final String GET_RESULTSET = "getResultSet";
    protected static final String[] RESULTSET_TYPES = new String[]{"executeQuery", "getGeneratedKeys", "getResultSet"};
    protected static volatile Constructor<?> resultSetConstructor = null;

    @Override
    public void closeInvoked() {
    }

    protected Constructor<?> getResultSetConstructor() throws NoSuchMethodException {
        if (resultSetConstructor == null) {
            Class<?> proxyClass = Proxy.getProxyClass(StatementDecoratorInterceptor.class.getClassLoader(), ResultSet.class);
            resultSetConstructor = proxyClass.getConstructor(InvocationHandler.class);
        }
        return resultSetConstructor;
    }

    @Override
    public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
        try {
            String name = method.getName();
            Constructor<?> constructor = null;
            String sql = null;
            if (this.compare("createStatement", name)) {
                constructor = this.getConstructor(0, Statement.class);
            } else if (this.compare("prepareStatement", name)) {
                constructor = this.getConstructor(1, PreparedStatement.class);
                sql = (String)args[0];
            } else if (this.compare("prepareCall", name)) {
                constructor = this.getConstructor(2, CallableStatement.class);
                sql = (String)args[0];
            } else {
                return statement;
            }
            return this.createDecorator(proxy, method, args, statement, constructor, sql);
        }
        catch (Exception x) {
            if (x instanceof InvocationTargetException) {
                Throwable cause = x.getCause();
                if (cause instanceof ThreadDeath) {
                    throw (ThreadDeath)cause;
                }
                if (cause instanceof VirtualMachineError) {
                    throw (VirtualMachineError)cause;
                }
            }
            logger.warn((Object)"Unable to create statement proxy for slow query report.", (Throwable)x);
            return statement;
        }
    }

    protected Object createDecorator(Object proxy, Method method, Object[] args, Object statement, Constructor<?> constructor, String sql) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        StatementProxy statementProxy = new StatementProxy(this, (Statement)statement, sql);
        result = constructor.newInstance(statementProxy);
        statementProxy.setActualProxy(result);
        statementProxy.setConnection(proxy);
        statementProxy.setConstructor(constructor);
        return result;
    }

    protected boolean isExecuteQuery(String methodName) {
        return EXECUTE_QUERY.equals(methodName);
    }

    protected boolean isExecuteQuery(Method method) {
        return this.isExecuteQuery(method.getName());
    }

    protected boolean isResultSet(Method method, boolean process) {
        return this.process(RESULTSET_TYPES, method, process);
    }

    protected static class StatementProxy<T extends Statement>
    implements InvocationHandler {
        protected boolean closed = false;
        protected T delegate;
        private Object actualProxy;
        private Object connection;
        private String sql;
        private Constructor<?> constructor;
        final /* synthetic */ StatementDecoratorInterceptor this$0;

        public StatementProxy(T delegate, String sql) {
            this.this$0 = this$0;
            this.delegate = delegate;
            this.sql = sql;
        }

        public T getDelegate() {
            return this.delegate;
        }

        public String getSql() {
            return this.sql;
        }

        public void setConnection(Object proxy) {
            this.connection = proxy;
        }

        public Object getConnection() {
            return this.connection;
        }

        public void setActualProxy(Object proxy) {
            this.actualProxy = proxy;
        }

        public Object getActualProxy() {
            return this.actualProxy;
        }

        public Constructor<?> getConstructor() {
            return this.constructor;
        }

        public void setConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        public void closeInvoked() {
            if (this.getDelegate() != null) {
                try {
                    this.getDelegate().close();
                }
                catch (SQLException sQLException) {
                    // empty catch block
                }
            }
            this.closed = true;
            this.delegate = null;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (this.this$0.compare("toString", method)) {
                return this.toString();
            }
            boolean close = this.this$0.compare("close", method);
            if (close && this.closed) {
                return null;
            }
            if (this.this$0.compare("isClosed", method)) {
                return this.closed;
            }
            if (this.closed) {
                throw new SQLException("Statement closed.");
            }
            if (this.this$0.compare("getConnection", method)) {
                return this.connection;
            }
            boolean process = false;
            process = this.this$0.isResultSet(method, process);
            Object result = null;
            try {
                if (close) {
                    this.closeInvoked();
                } else {
                    result = method.invoke(this.delegate, args);
                }
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException && t.getCause() != null) {
                    throw t.getCause();
                }
                throw t;
            }
            if (process && result != null) {
                Constructor<?> cons = this.this$0.getResultSetConstructor();
                result = cons.newInstance(new ResultSetProxy(this.actualProxy, result));
            }
            return result;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer(StatementProxy.class.getName());
            buf.append("[Proxy=");
            buf.append(System.identityHashCode(this));
            buf.append("; Sql=");
            buf.append(this.getSql());
            buf.append("; Delegate=");
            buf.append(this.getDelegate());
            buf.append("; Connection=");
            buf.append(this.getConnection());
            buf.append(']');
            return buf.toString();
        }
    }

    protected static class ResultSetProxy
    implements InvocationHandler {
        private Object st;
        private Object delegate;

        public ResultSetProxy(Object st, Object delegate) {
            this.st = st;
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getStatement")) {
                return this.st;
            }
            try {
                return method.invoke(this.delegate, args);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException && t.getCause() != null) {
                    throw t.getCause();
                }
                throw t;
            }
        }
    }
}

