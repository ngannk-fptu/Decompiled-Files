/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.jdbc;

import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ejb.util.Base64;
import com.opensymphony.user.provider.ejb.util.PasswordDigester;
import com.opensymphony.user.provider.jdbc.BaseJDBCProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCCredentialsProvider
extends BaseJDBCProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$jdbc$JDBCCredentialsProvider == null ? (class$com$opensymphony$user$provider$jdbc$JDBCCredentialsProvider = JDBCCredentialsProvider.class$("com.opensymphony.user.provider.jdbc.JDBCCredentialsProvider")) : class$com$opensymphony$user$provider$jdbc$JDBCCredentialsProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$jdbc$JDBCCredentialsProvider;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean authenticate(String name, String password) {
        boolean authenticated = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT " + this.userPassword + " FROM " + this.userTable + " WHERE " + this.userName + " = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                authenticated = this.compareHash(rs.getString(1), password);
            }
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not authenticate user [" + name + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return authenticated;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean changePassword(String name, String password) {
        boolean changedPassword = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("UPDATE " + this.userTable + " SET " + this.userPassword + " = ? WHERE " + this.userName + " = ?");
            ps.setString(1, this.createHash(password));
            ps.setString(2, name);
            ps.executeUpdate();
            changedPassword = true;
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not change password for user [" + name + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return changedPassword;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean create(String name) {
        boolean created = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("INSERT INTO " + this.userTable + " (" + this.userName + ") VALUES (?)");
            ps.setString(1, name);
            try {
                ps.executeUpdate();
                created = true;
            }
            catch (SQLException e) {
                log.warn((Object)("User [" + name + "] must already exist"), (Throwable)e);
            }
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not create user [" + name + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return created;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean handles(String name) {
        boolean handles = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + this.userTable + " WHERE " + this.userName + " = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            handles = rs.next();
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not see if [" + name + "] is handled"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return handles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List list() {
        ArrayList<String> users = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT " + this.userName + " FROM " + this.userTable + " ORDER BY " + this.userName);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString(1));
            }
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)"Could not list users", (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return users;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(String name) {
        boolean removed = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("DELETE FROM " + this.membershipTable + " WHERE " + this.membershipUserName + " = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("DELETE FROM " + this.userTable + " WHERE " + this.userName + " = ?");
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                removed = true;
            }
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Unable to remove user [" + name + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return removed;
    }

    protected String createHash(String original) {
        byte[] digested = PasswordDigester.digest(original.getBytes());
        byte[] encoded = Base64.encode(digested);
        return new String(encoded);
    }

    private boolean compareHash(String hashedValue, String unhashedValue) {
        return hashedValue.equals(this.createHash(unhashedValue));
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

