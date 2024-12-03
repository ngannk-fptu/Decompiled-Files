/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public class StatementFacade
extends AbstractCreateStatementInterceptor {
    private static final Log logger = LogFactory.getLog(StatementFacade.class);

    protected StatementFacade(JdbcInterceptor interceptor) {
        this.setUseEquals(interceptor.isUseEquals());
        this.setNext(interceptor);
    }

    @Override
    public void closeInvoked() {
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
            return constructor.newInstance(new StatementProxy(statement, sql));
        }
        catch (Exception x) {
            logger.warn((Object)"Unable to create statement proxy.", (Throwable)x);
            return statement;
        }
    }

    protected class StatementProxy
    implements InvocationHandler {
        protected boolean closed = false;
        protected Object delegate;
        protected final String query;

        public StatementProxy(Object parent, String query) {
            this.delegate = parent;
            this.query = query;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (StatementFacade.this.compare("toString", method)) {
                return this.toString();
            }
            if (StatementFacade.this.compare("equals", method)) {
                return this.equals(Proxy.getInvocationHandler(args[0]));
            }
            if (StatementFacade.this.compare("hashCode", method)) {
                return this.hashCode();
            }
            if (StatementFacade.this.compare("close", method) && this.delegate == null) {
                return null;
            }
            if (StatementFacade.this.compare("isClosed", method) && this.delegate == null) {
                return Boolean.TRUE;
            }
            if (this.delegate == null) {
                throw new SQLException("Statement closed.");
            }
            Object result = null;
            try {
                result = method.invoke(this.delegate, args);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException && t.getCause() != null) {
                    throw t.getCause();
                }
                throw t;
            }
            if (StatementFacade.this.compare("close", method)) {
                this.delegate = null;
            }
            return result;
        }

        public int hashCode() {
            return System.identityHashCode(this);
        }

        public boolean equals(Object obj) {
            return this == obj;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer(StatementProxy.class.getName());
            buf.append("[Proxy=");
            buf.append(this.hashCode());
            buf.append("; Query=");
            buf.append(this.query);
            buf.append("; Delegate=");
            buf.append(this.delegate);
            buf.append(']');
            return buf.toString();
        }
    }
}

