/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Method;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractQueryReport;
import org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimerMBean;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;

public class ResetAbandonedTimer
extends AbstractQueryReport
implements ResetAbandonedTimerMBean {
    private PooledConnection pcon;
    private ObjectName oname = null;

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        super.reset(parent, con);
        if (con == null) {
            this.pcon = null;
            if (this.oname != null) {
                JmxUtil.unregisterJmx(this.oname);
                this.oname = null;
            }
        } else {
            this.pcon = con;
            if (this.oname == null) {
                String keyprop = ",JdbcInterceptor=" + this.getClass().getSimpleName();
                this.oname = JmxUtil.registerJmx(this.pcon.getObjectName(), keyprop, this);
            }
        }
    }

    @Override
    public boolean resetTimer() {
        boolean result = false;
        if (this.pcon != null) {
            this.pcon.setTimestamp(System.currentTimeMillis());
            result = true;
        }
        return result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = super.invoke(proxy, method, args);
        this.resetTimer();
        return result;
    }

    @Override
    protected void prepareCall(String query, long time) {
        this.resetTimer();
    }

    @Override
    protected void prepareStatement(String sql, long time) {
        this.resetTimer();
    }

    @Override
    public void closeInvoked() {
        this.resetTimer();
    }

    @Override
    protected String reportQuery(String query, Object[] args, String name, long start, long delta) {
        this.resetTimer();
        return super.reportQuery(query, args, name, start, delta);
    }

    @Override
    protected String reportSlowQuery(String query, Object[] args, String name, long start, long delta) {
        this.resetTimer();
        return super.reportSlowQuery(query, args, name, start, delta);
    }
}

