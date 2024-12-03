/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib.sql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.xalan.lib.sql.ConnectionPool;

public class JNDIConnectionPool
implements ConnectionPool {
    protected Object jdbcSource = null;
    private Method getConnectionWithArgs = null;
    private Method getConnection = null;
    protected String jndiPath = null;
    protected String user = null;
    protected String pwd = null;

    public JNDIConnectionPool() {
    }

    public JNDIConnectionPool(String jndiDatasourcePath) {
        this.jndiPath = jndiDatasourcePath.trim();
    }

    public void setJndiPath(String jndiPath) {
        this.jndiPath = jndiPath;
    }

    public String getJndiPath() {
        return this.jndiPath;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setDriver(String d) {
        throw new Error("This method is not supported. All connection information is handled by the JDBC datasource provider");
    }

    @Override
    public void setURL(String url) {
        throw new Error("This method is not supported. All connection information is handled by the JDBC datasource provider");
    }

    @Override
    public void freeUnused() {
    }

    @Override
    public boolean hasActiveConnections() {
        return false;
    }

    @Override
    public void setPassword(String p) {
        if (p != null) {
            p = p.trim();
        }
        if (p != null && p.length() == 0) {
            p = null;
        }
        this.pwd = p;
    }

    @Override
    public void setUser(String u) {
        if (u != null) {
            u = u.trim();
        }
        if (u != null && u.length() == 0) {
            u = null;
        }
        this.user = u;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.jdbcSource == null) {
            try {
                this.findDatasource();
            }
            catch (NamingException ne) {
                throw new SQLException("Could not create jndi context for " + this.jndiPath + " - " + ne.getLocalizedMessage());
            }
        }
        try {
            if (this.user != null || this.pwd != null) {
                Object[] arglist = new Object[]{this.user, this.pwd};
                return (Connection)this.getConnectionWithArgs.invoke(this.jdbcSource, arglist);
            }
            Object[] arglist = new Object[]{};
            return (Connection)this.getConnection.invoke(this.jdbcSource, arglist);
        }
        catch (Exception e) {
            throw new SQLException("Could not create jndi connection for " + this.jndiPath + " - " + e.getLocalizedMessage());
        }
    }

    protected void findDatasource() throws NamingException {
        try {
            InitialContext context = new InitialContext();
            this.jdbcSource = context.lookup(this.jndiPath);
            Class[] withArgs = new Class[]{String.class, String.class};
            this.getConnectionWithArgs = this.jdbcSource.getClass().getDeclaredMethod("getConnection", withArgs);
            Class[] noArgs = new Class[]{};
            this.getConnection = this.jdbcSource.getClass().getDeclaredMethod("getConnection", noArgs);
        }
        catch (NamingException e) {
            throw e;
        }
        catch (NoSuchMethodException e) {
            throw new NamingException("Unable to resolve JNDI DataSource - " + e);
        }
    }

    @Override
    public void releaseConnection(Connection con) throws SQLException {
        con.close();
    }

    @Override
    public void releaseConnectionOnError(Connection con) throws SQLException {
        con.close();
    }

    @Override
    public void setPoolEnabled(boolean flag) {
        if (!flag) {
            this.jdbcSource = null;
        }
    }

    @Override
    public void setProtocol(Properties p) {
    }

    @Override
    public void setMinConnections(int n) {
    }

    @Override
    public boolean testConnection() {
        if (this.jdbcSource == null) {
            try {
                this.findDatasource();
            }
            catch (NamingException ne) {
                return false;
            }
        }
        return true;
    }
}

