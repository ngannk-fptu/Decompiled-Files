/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.reflect.ReflectUtils
 *  com.mchange.v2.sql.filter.FilterConnection
 */
package com.mchange.v2.c3p0.debug;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.reflect.ReflectUtils;
import com.mchange.v2.sql.filter.FilterConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLWarning;

public class AfterCloseLoggingConnectionWrapper
extends FilterConnection {
    static final MLogger logger = MLog.getLogger(AfterCloseLoggingConnectionWrapper.class);

    public static Connection wrap(Connection inner) {
        try {
            Constructor ctor = ReflectUtils.findProxyConstructor((ClassLoader)AfterCloseLoggingConnectionWrapper.class.getClassLoader(), Connection.class);
            return (Connection)ctor.newInstance(new AfterCloseLoggingInvocationHandler(inner));
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.SEVERE)) {
                logger.log(MLevel.SEVERE, "An unexpected Exception occured while trying to instantiate a dynamic proxy.", (Throwable)e);
            }
            throw new RuntimeException(e);
        }
    }

    private static class AfterCloseLoggingInvocationHandler
    implements InvocationHandler {
        final Connection inner;
        volatile SQLWarning closeStackTrace = null;

        AfterCloseLoggingInvocationHandler(Connection inner) {
            this.inner = inner;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("close".equals(method.getName()) && this.closeStackTrace == null) {
                this.closeStackTrace = new SQLWarning("DEBUG STACK TRACE -- " + this.inner + ".close() first-call stack trace.");
            } else if (this.closeStackTrace != null) {
                if (logger.isLoggable(MLevel.INFO)) {
                    logger.log(MLevel.INFO, String.format("Method '%s' called after call to Connection close().", method));
                }
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "After-close() method call stack trace:", (Throwable)new SQLWarning("DEBUG STACK TRACE -- ILLEGAL use of " + this.inner + " after call to close()."));
                    logger.log(MLevel.FINE, "Original close() call stack trace:", (Throwable)this.closeStackTrace);
                }
            }
            return method.invoke((Object)this.inner, args);
        }
    }
}

