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
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public class ConnectionState
extends JdbcInterceptor {
    private static final Log log = LogFactory.getLog(ConnectionState.class);
    protected final String[] readState = new String[]{"getAutoCommit", "getTransactionIsolation", "isReadOnly", "getCatalog"};
    protected final String[] writeState = new String[]{"setAutoCommit", "setTransactionIsolation", "setReadOnly", "setCatalog"};
    protected Boolean autoCommit = null;
    protected Integer transactionIsolation = null;
    protected Boolean readOnly = null;
    protected String catalog = null;

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        if (parent == null || con == null) {
            this.autoCommit = null;
            this.transactionIsolation = null;
            this.readOnly = null;
            this.catalog = null;
            return;
        }
        PoolConfiguration poolProperties = parent.getPoolProperties();
        if (poolProperties.getDefaultTransactionIsolation() != -1) {
            try {
                if (this.transactionIsolation == null || this.transactionIsolation.intValue() != poolProperties.getDefaultTransactionIsolation()) {
                    con.getConnection().setTransactionIsolation(poolProperties.getDefaultTransactionIsolation());
                    this.transactionIsolation = poolProperties.getDefaultTransactionIsolation();
                }
            }
            catch (SQLException x) {
                this.transactionIsolation = null;
                log.error((Object)"Unable to reset transaction isolation state to connection.", (Throwable)x);
            }
        }
        if (poolProperties.getDefaultReadOnly() != null) {
            try {
                if (this.readOnly == null || this.readOnly.booleanValue() != poolProperties.getDefaultReadOnly().booleanValue()) {
                    con.getConnection().setReadOnly(poolProperties.getDefaultReadOnly());
                    this.readOnly = poolProperties.getDefaultReadOnly();
                }
            }
            catch (SQLException x) {
                this.readOnly = null;
                log.error((Object)"Unable to reset readonly state to connection.", (Throwable)x);
            }
        }
        if (poolProperties.getDefaultAutoCommit() != null) {
            try {
                if (this.autoCommit == null || this.autoCommit.booleanValue() != poolProperties.getDefaultAutoCommit().booleanValue()) {
                    con.getConnection().setAutoCommit(poolProperties.getDefaultAutoCommit());
                    this.autoCommit = poolProperties.getDefaultAutoCommit();
                }
            }
            catch (SQLException x) {
                this.autoCommit = null;
                log.error((Object)"Unable to reset autocommit state to connection.", (Throwable)x);
            }
        }
        if (poolProperties.getDefaultCatalog() != null) {
            try {
                if (this.catalog == null || !this.catalog.equals(poolProperties.getDefaultCatalog())) {
                    con.getConnection().setCatalog(poolProperties.getDefaultCatalog());
                    this.catalog = poolProperties.getDefaultCatalog();
                }
            }
            catch (SQLException x) {
                this.catalog = null;
                log.error((Object)"Unable to reset default catalog state to connection.", (Throwable)x);
            }
        }
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
        this.autoCommit = null;
        this.transactionIsolation = null;
        this.readOnly = null;
        this.catalog = null;
        super.disconnected(parent, con, finalizing);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        boolean read = false;
        int index = -1;
        for (int i = 0; !read && i < this.readState.length; ++i) {
            read = this.compare(name, this.readState[i]);
            if (!read) continue;
            index = i;
        }
        boolean write = false;
        for (int i = 0; !write && !read && i < this.writeState.length; ++i) {
            write = this.compare(name, this.writeState[i]);
            if (!write) continue;
            index = i;
        }
        Object result = null;
        if (read) {
            switch (index) {
                case 0: {
                    result = this.autoCommit;
                    break;
                }
                case 1: {
                    result = this.transactionIsolation;
                    break;
                }
                case 2: {
                    result = this.readOnly;
                    break;
                }
                case 3: {
                    result = this.catalog;
                    break;
                }
            }
            if (result != null) {
                return result;
            }
        }
        try {
            result = super.invoke(proxy, method, args);
            if (read || write) {
                switch (index) {
                    case 0: {
                        this.autoCommit = (Boolean)(read ? result : args[0]);
                        break;
                    }
                    case 1: {
                        this.transactionIsolation = (Integer)(read ? result : args[0]);
                        break;
                    }
                    case 2: {
                        this.readOnly = (Boolean)(read ? result : args[0]);
                        break;
                    }
                    case 3: {
                        this.catalog = (String)(read ? result : args[0]);
                    }
                }
            }
        }
        catch (Throwable e) {
            if (write) {
                log.warn((Object)("Reset state to null as an exception occurred while calling method[" + name + "]."), e);
                switch (index) {
                    case 0: {
                        this.autoCommit = null;
                        break;
                    }
                    case 1: {
                        this.transactionIsolation = null;
                        break;
                    }
                    case 2: {
                        this.readOnly = null;
                        break;
                    }
                    case 3: {
                        this.catalog = null;
                    }
                }
            }
            throw e;
        }
        return result;
    }
}

