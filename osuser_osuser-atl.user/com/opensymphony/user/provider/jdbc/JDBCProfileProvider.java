/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.jdbc;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.jdbc.BaseJDBCProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCProfileProvider
extends BaseJDBCProvider
implements ProfileProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$jdbc$JDBCProfileProvider == null ? (class$com$opensymphony$user$provider$jdbc$JDBCProfileProvider = JDBCProfileProvider.class$("com.opensymphony.user.provider.jdbc.JDBCProfileProvider")) : class$com$opensymphony$user$provider$jdbc$JDBCProfileProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$jdbc$JDBCProfileProvider;

    public PropertySet getPropertySet(String name) {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("globalKey", "OSUser_" + name);
        return PropertySetManager.getInstance((String)"jdbc", args);
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

    public List list() {
        return null;
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

