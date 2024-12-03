/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.realm;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.Context;
import javax.sql.DataSource;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.naming.ContextBindings;

public class DataSourceRealm
extends RealmBase {
    private String preparedRoles = null;
    private String preparedCredentials = null;
    protected String dataSourceName = null;
    protected boolean localDataSource = false;
    protected String roleNameCol = null;
    protected String userCredCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userTable = null;
    private volatile boolean connectionSuccess = true;

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public boolean getLocalDataSource() {
        return this.localDataSource;
    }

    public void setLocalDataSource(boolean localDataSource) {
        this.localDataSource = localDataSource;
    }

    public String getRoleNameCol() {
        return this.roleNameCol;
    }

    public void setRoleNameCol(String roleNameCol) {
        this.roleNameCol = roleNameCol;
    }

    public String getUserCredCol() {
        return this.userCredCol;
    }

    public void setUserCredCol(String userCredCol) {
        this.userCredCol = userCredCol;
    }

    public String getUserNameCol() {
        return this.userNameCol;
    }

    public void setUserNameCol(String userNameCol) {
        this.userNameCol = userNameCol;
    }

    public String getUserRoleTable() {
        return this.userRoleTable;
    }

    public void setUserRoleTable(String userRoleTable) {
        this.userRoleTable = userRoleTable;
    }

    public String getUserTable() {
        return this.userTable;
    }

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            return null;
        }
        Connection dbConnection = null;
        dbConnection = this.open();
        if (dbConnection == null) {
            return null;
        }
        try {
            Principal principal = this.authenticate(dbConnection, username, credentials);
            return principal;
        }
        finally {
            this.close(dbConnection);
        }
    }

    @Override
    public boolean isAvailable() {
        return this.connectionSuccess;
    }

    protected Principal authenticate(Connection dbConnection, String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("dataSourceRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        String dbCredentials = this.getPassword(dbConnection, username);
        if (dbCredentials == null) {
            this.getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("dataSourceRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        boolean validated = this.getCredentialHandler().matches(credentials, dbCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("dataSourceRealm.authenticateSuccess", new Object[]{username}));
            }
        } else {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("dataSourceRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        ArrayList<String> list = this.getRoles(dbConnection, username);
        return new GenericPrincipal(username, credentials, list);
    }

    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        }
        catch (SQLException e) {
            this.containerLog.error((Object)sm.getString("dataSourceRealm.commit"), (Throwable)e);
        }
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            this.containerLog.error((Object)sm.getString("dataSourceRealm.close"), (Throwable)e);
        }
    }

    protected Connection open() {
        try {
            Context context = null;
            if (this.localDataSource) {
                context = ContextBindings.getClassLoader();
                context = (Context)context.lookup("comp/env");
            } else {
                Server server = this.getServer();
                if (server == null) {
                    this.connectionSuccess = false;
                    this.containerLog.error((Object)sm.getString("dataSourceRealm.noNamingContext"));
                    return null;
                }
                context = server.getGlobalNamingContext();
            }
            DataSource dataSource = (DataSource)context.lookup(this.dataSourceName);
            Connection connection = dataSource.getConnection();
            this.connectionSuccess = true;
            return connection;
        }
        catch (Exception e) {
            this.connectionSuccess = false;
            this.containerLog.error((Object)sm.getString("dataSourceRealm.exception"), (Throwable)e);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String getPassword(String username) {
        Connection dbConnection = null;
        dbConnection = this.open();
        if (dbConnection == null) {
            return null;
        }
        try {
            String string = this.getPassword(dbConnection, username);
            return string;
        }
        finally {
            this.close(dbConnection);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected String getPassword(Connection dbConnection, String username) {
        String dbCredentials = null;
        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedCredentials);){
            String string;
            block15: {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        dbCredentials = rs.getString(1);
                    }
                    String string2 = string = dbCredentials != null ? dbCredentials.trim() : null;
                    if (rs == null) break block15;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return string;
        }
        catch (SQLException e) {
            this.containerLog.error((Object)sm.getString("dataSourceRealm.getPassword.exception", new Object[]{username}), (Throwable)e);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Principal getPrincipal(String username) {
        Connection dbConnection = this.open();
        if (dbConnection == null) {
            return new GenericPrincipal(username, null, null);
        }
        try {
            GenericPrincipal genericPrincipal = new GenericPrincipal(username, this.getPassword(dbConnection, username), this.getRoles(dbConnection, username));
            return genericPrincipal;
        }
        finally {
            this.close(dbConnection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ArrayList<String> getRoles(String username) {
        Connection dbConnection = null;
        dbConnection = this.open();
        if (dbConnection == null) {
            return null;
        }
        try {
            ArrayList<String> arrayList = this.getRoles(dbConnection, username);
            return arrayList;
        }
        finally {
            this.close(dbConnection);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected ArrayList<String> getRoles(Connection dbConnection, String username) {
        if (this.allRolesMode != RealmBase.AllRolesMode.STRICT_MODE && !this.isRoleStoreDefined()) {
            return null;
        }
        ArrayList<String> list = null;
        try (PreparedStatement stmt = dbConnection.prepareStatement(this.preparedRoles);){
            ArrayList<String> arrayList;
            block16: {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    list = new ArrayList<String>();
                    while (rs.next()) {
                        String role = rs.getString(1);
                        if (role == null) continue;
                        list.add(role.trim());
                    }
                    arrayList = list;
                    if (rs == null) break block16;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return arrayList;
        }
        catch (SQLException e) {
            this.containerLog.error((Object)sm.getString("dataSourceRealm.getRoles.exception", new Object[]{username}), (Throwable)e);
            return null;
        }
    }

    private boolean isRoleStoreDefined() {
        return this.userRoleTable != null || this.roleNameCol != null;
    }

    @Override
    protected void startInternal() throws LifecycleException {
        StringBuilder temp = new StringBuilder("SELECT ");
        temp.append(this.roleNameCol);
        temp.append(" FROM ");
        temp.append(this.userRoleTable);
        temp.append(" WHERE ");
        temp.append(this.userNameCol);
        temp.append(" = ?");
        this.preparedRoles = temp.toString();
        temp = new StringBuilder("SELECT ");
        temp.append(this.userCredCol);
        temp.append(" FROM ");
        temp.append(this.userTable);
        temp.append(" WHERE ");
        temp.append(this.userNameCol);
        temp.append(" = ?");
        this.preparedCredentials = temp.toString();
        super.startInternal();
    }
}

