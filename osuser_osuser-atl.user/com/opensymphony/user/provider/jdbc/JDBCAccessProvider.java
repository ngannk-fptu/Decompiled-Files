/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.jdbc;

import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.jdbc.BaseJDBCProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCAccessProvider
extends BaseJDBCProvider
implements AccessProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$jdbc$JDBCAccessProvider == null ? (class$com$opensymphony$user$provider$jdbc$JDBCAccessProvider = JDBCAccessProvider.class$("com.opensymphony.user.provider.jdbc.JDBCAccessProvider")) : class$com$opensymphony$user$provider$jdbc$JDBCAccessProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$jdbc$JDBCAccessProvider;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addToGroup(String username, String groupname) {
        boolean addedToGroup = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("INSERT INTO " + this.membershipTable + " (" + this.userName + ", " + this.groupName + ") VALUES (?, ?)");
            ps.setString(1, username);
            ps.setString(2, groupname);
            ps.executeUpdate();
            addedToGroup = true;
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not add user [" + username + "] to group [" + groupname + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return addedToGroup;
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
            ps = conn.prepareStatement("INSERT INTO " + this.groupTable + " (" + this.groupName + ") VALUES (?)");
            ps.setString(1, name);
            try {
                ps.executeUpdate();
                created = true;
            }
            catch (SQLException e) {
                log.warn((Object)("Group [" + name + "] must already exist"), (Throwable)e);
            }
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not insert [" + name + "] into groups"), (Throwable)e);
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
            ps = conn.prepareStatement("SELECT " + this.userName + " FROM " + this.userTable + " WHERE " + this.userName + " = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                handles = true;
            }
            if (!handles) {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("SELECT " + this.groupName + " FROM " + this.groupTable + " WHERE " + this.groupName + " = ?");
                ps.setString(1, name);
                rs = ps.executeQuery();
                handles = rs.next();
            }
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
    public boolean inGroup(String username, String groupname) {
        boolean inGroup = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + this.membershipTable + " WHERE " + this.userName + " = ? AND " + this.groupName + " = ?");
            ps.setString(1, username);
            ps.setString(2, groupname);
            rs = ps.executeQuery();
            inGroup = rs.next();
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not determine if user [" + username + "] is in group [" + groupname + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return inGroup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List list() {
        ArrayList<String> groups = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT " + this.groupName + " FROM " + this.groupTable + " ORDER BY " + this.groupName);
            rs = ps.executeQuery();
            while (rs.next()) {
                groups.add(rs.getString(1));
            }
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)"Could not list groups", (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return groups;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List listGroupsContainingUser(String username) {
        ArrayList<String> groups = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT " + this.membershipGroupName + " FROM " + this.membershipTable + " WHERE " + this.membershipUserName + " = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                groups.add(rs.getString(1));
            }
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not list groups containing user [" + username + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, rs);
        }
        return groups;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List listUsersInGroup(String groupname) {
        ArrayList<String> users = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("SELECT " + this.membershipUserName + " FROM " + this.membershipTable + " WHERE " + this.membershipGroupName + " = ?");
            ps.setString(1, groupname);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString(1));
            }
            this.cleanup(conn, ps, rs);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not list users in group [" + groupname + "]"), (Throwable)e);
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
            ps = conn.prepareStatement("DELETE FROM " + this.membershipTable + " WHERE " + this.membershipGroupName + " = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("DELETE FROM " + this.groupTable + " WHERE " + this.groupName + " = ?");
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                removed = true;
            }
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not remove group [" + name + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return removed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeFromGroup(String username, String groupname) {
        boolean removedFromGroup = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            ps = conn.prepareStatement("DELETE FROM " + this.membershipTable + " WHERE " + this.userName + " = ? AND " + this.groupName + " =?");
            ps.setString(1, username);
            ps.setString(2, groupname);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                removedFromGroup = true;
            }
            this.cleanup(conn, ps, null);
        }
        catch (SQLException e) {
            log.fatal((Object)("Could not detele user [" + username + "] from group [" + groupname + "]"), (Throwable)e);
        }
        finally {
            this.cleanup(conn, ps, null);
        }
        return removedFromGroup;
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

