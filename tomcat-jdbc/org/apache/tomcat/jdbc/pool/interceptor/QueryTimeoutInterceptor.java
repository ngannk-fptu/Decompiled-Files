/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public class QueryTimeoutInterceptor
extends AbstractCreateStatementInterceptor {
    private static Log log = LogFactory.getLog(QueryTimeoutInterceptor.class);
    int timeout = 1;

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        PoolProperties.InterceptorProperty p = properties.get("queryTimeout");
        if (p != null) {
            this.timeout = p.getValueAsInt(this.timeout);
        }
    }

    @Override
    public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
        if (statement instanceof Statement && this.timeout > 0) {
            Statement s = (Statement)statement;
            try {
                s.setQueryTimeout(this.timeout);
            }
            catch (SQLException x) {
                log.warn((Object)("[QueryTimeoutInterceptor] Unable to set query timeout:" + x.getMessage()), (Throwable)x);
            }
        }
        return statement;
    }

    @Override
    public void closeInvoked() {
    }
}

