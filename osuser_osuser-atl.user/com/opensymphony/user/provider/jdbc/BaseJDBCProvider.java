/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.jdbc;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.UserProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseJDBCProvider
implements UserProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$jdbc$BaseJDBCProvider == null ? (class$com$opensymphony$user$provider$jdbc$BaseJDBCProvider = BaseJDBCProvider.class$("com.opensymphony.user.provider.jdbc.BaseJDBCProvider")) : class$com$opensymphony$user$provider$jdbc$BaseJDBCProvider));
    protected DataSource ds;
    protected String groupName;
    protected String groupTable;
    protected String membershipGroupName;
    protected String membershipTable;
    protected String membershipUserName;
    protected String userName;
    protected String userPassword;
    protected String userTable;
    protected boolean closeConnWhenDone = false;
    static /* synthetic */ Class class$com$opensymphony$user$provider$jdbc$BaseJDBCProvider;

    public boolean create(String name) {
        return true;
    }

    public void flushCaches() {
    }

    public boolean init(Properties props) {
        this.userTable = (String)props.get("user.table");
        this.groupTable = (String)props.get("group.table");
        this.membershipTable = (String)props.get("membership.table");
        this.userName = (String)props.get("user.name");
        this.userPassword = (String)props.get("user.password");
        this.groupName = (String)props.get("group.name");
        this.membershipUserName = (String)props.get("membership.userName");
        this.membershipGroupName = (String)props.get("membership.groupName");
        String jndi = (String)props.get("datasource");
        if (jndi != null) {
            try {
                this.ds = (DataSource)this.lookup(jndi);
                if (this.ds == null) {
                    this.ds = (DataSource)new InitialContext().lookup(jndi);
                }
            }
            catch (Exception e) {
                log.fatal((Object)("Could not look up DataSource using JNDI location: " + jndi), (Throwable)e);
                return false;
            }
        }
        return true;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        return true;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    protected Connection getConnection() throws SQLException {
        this.closeConnWhenDone = true;
        return this.ds.getConnection();
    }

    protected void cleanup(Connection connection, Statement statement, ResultSet result) {
        if (result != null) {
            try {
                result.close();
            }
            catch (SQLException ex) {
                log.error((Object)"Error closing resultset", (Throwable)ex);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex) {
                log.error((Object)"Error closing statement", (Throwable)ex);
            }
        }
        if (connection != null && this.closeConnWhenDone) {
            try {
                connection.close();
            }
            catch (SQLException ex) {
                log.error((Object)"Error closing connection", (Throwable)ex);
            }
        }
    }

    private Object lookup(String location) throws NamingException {
        InitialContext context = new InitialContext();
        try {
            return context.lookup(location);
        }
        catch (NamingException e) {
            return context.lookup("java:comp/env/" + location);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

