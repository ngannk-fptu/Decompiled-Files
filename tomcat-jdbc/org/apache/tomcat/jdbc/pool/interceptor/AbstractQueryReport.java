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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public abstract class AbstractQueryReport
extends AbstractCreateStatementInterceptor {
    private static final Log log = LogFactory.getLog(AbstractQueryReport.class);
    protected long threshold = 1000L;

    protected abstract void prepareStatement(String var1, long var2);

    protected abstract void prepareCall(String var1, long var2);

    protected String reportFailedQuery(String query, Object[] args, String name, long start, Throwable t) {
        String sql;
        String string = sql = query == null && args != null && args.length > 0 ? (String)args[0] : query;
        if (sql == null && this.compare("executeBatch", name)) {
            sql = "batch";
        }
        return sql;
    }

    protected String reportQuery(String query, Object[] args, String name, long start, long delta) {
        String sql;
        String string = sql = query == null && args != null && args.length > 0 ? (String)args[0] : query;
        if (sql == null && this.compare("executeBatch", name)) {
            sql = "batch";
        }
        return sql;
    }

    protected String reportSlowQuery(String query, Object[] args, String name, long start, long delta) {
        String sql;
        String string = sql = query == null && args != null && args.length > 0 ? (String)args[0] : query;
        if (sql == null && this.compare("executeBatch", name)) {
            sql = "batch";
        }
        return sql;
    }

    public long getThreshold() {
        return this.threshold;
    }

    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    @Override
    public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
        try {
            Object result = null;
            String name = method.getName();
            String sql = null;
            Constructor<?> constructor = null;
            if (this.compare("createStatement", name)) {
                constructor = this.getConstructor(0, Statement.class);
            } else if (this.compare("prepareStatement", name)) {
                sql = (String)args[0];
                constructor = this.getConstructor(1, PreparedStatement.class);
                if (sql != null) {
                    this.prepareStatement(sql, time);
                }
            } else if (this.compare("prepareCall", name)) {
                sql = (String)args[0];
                constructor = this.getConstructor(2, CallableStatement.class);
                this.prepareCall(sql, time);
            } else {
                return statement;
            }
            result = constructor.newInstance(new StatementProxy(statement, sql));
            return result;
        }
        catch (Exception x) {
            log.warn((Object)"Unable to create statement proxy for slow query report.", (Throwable)x);
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
            long delta;
            String name = method.getName();
            boolean close = AbstractQueryReport.this.compare("close", name);
            if (close && this.closed) {
                return null;
            }
            if (AbstractQueryReport.this.compare("isClosed", name)) {
                return this.closed;
            }
            if (this.closed) {
                throw new SQLException("Statement closed.");
            }
            boolean process = false;
            long start = (process = AbstractQueryReport.this.isExecute(method, process)) ? System.currentTimeMillis() : 0L;
            Object result = null;
            try {
                result = method.invoke(this.delegate, args);
            }
            catch (Throwable t) {
                AbstractQueryReport.this.reportFailedQuery(this.query, args, name, start, t);
                if (t instanceof InvocationTargetException && t.getCause() != null) {
                    throw t.getCause();
                }
                throw t;
            }
            long l = delta = process ? System.currentTimeMillis() - start : Long.MIN_VALUE;
            if (delta > AbstractQueryReport.this.threshold) {
                try {
                    AbstractQueryReport.this.reportSlowQuery(this.query, args, name, start, delta);
                }
                catch (Exception t) {
                    if (log.isWarnEnabled()) {
                        log.warn((Object)"Unable to process slow query", (Throwable)t);
                    }
                }
            } else if (process) {
                AbstractQueryReport.this.reportQuery(this.query, args, name, start, delta);
            }
            if (close) {
                this.closed = true;
                this.delegate = null;
            }
            return result;
        }
    }
}

