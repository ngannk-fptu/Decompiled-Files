/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public class StatementFinalizer
extends AbstractCreateStatementInterceptor {
    private static final Log log = LogFactory.getLog(StatementFinalizer.class);
    protected List<StatementEntry> statements = new LinkedList<StatementEntry>();
    private boolean logCreationStack = false;

    @Override
    public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
        try {
            if (statement instanceof Statement) {
                this.statements.add(new StatementEntry((Statement)statement));
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return statement;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void closeInvoked() {
        while (!this.statements.isEmpty()) {
            StatementEntry ws = this.statements.remove(0);
            Statement st = ws.getStatement();
            boolean shallClose = false;
            try {
                shallClose = st != null && !st.isClosed();
                if (!shallClose) continue;
                st.close();
            }
            catch (Exception ignore) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)"Unable to closed statement upon connection close.", (Throwable)ignore);
            }
            finally {
                if (!this.logCreationStack || !shallClose) continue;
                log.warn((Object)"Statement created, but was not closed at:", ws.getAllocationStack());
            }
        }
    }

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        PoolProperties.InterceptorProperty logProperty = properties.get("trace");
        if (null != logProperty) {
            this.logCreationStack = logProperty.getValueAsBoolean(this.logCreationStack);
        }
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        this.statements.clear();
        super.reset(parent, con);
    }

    protected class StatementEntry {
        private WeakReference<Statement> statement;
        private Throwable allocationStack;

        public StatementEntry(Statement statement) {
            this.statement = new WeakReference<Statement>(statement);
            if (StatementFinalizer.this.logCreationStack) {
                this.allocationStack = new Throwable();
            }
        }

        public Statement getStatement() {
            return (Statement)this.statement.get();
        }

        public Throwable getAllocationStack() {
            return this.allocationStack;
        }
    }
}

