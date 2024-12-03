/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.realm;

import java.security.Principal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.tomcat.util.ExceptionUtils;

@Deprecated
public class JDBCRealm
extends RealmBase {
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    protected Connection dbConnection = null;
    protected Driver driver = null;
    protected String driverName = null;
    protected PreparedStatement preparedCredentials = null;
    protected PreparedStatement preparedRoles = null;
    protected String roleNameCol = null;
    protected String userCredCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userTable = null;

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    @Override
    public synchronized Principal authenticate(String username, String credentials) {
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            try {
                this.open();
                Principal principal = this.authenticate(this.dbConnection, username, credentials);
                return principal;
            }
            catch (SQLException e) {
                this.containerLog.error((Object)sm.getString("jdbcRealm.exception"), (Throwable)e);
                if (this.dbConnection == null) continue;
                this.close(this.dbConnection);
                continue;
            }
        }
        return null;
    }

    public synchronized Principal authenticate(Connection dbConnection, String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("jdbcRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        String dbCredentials = this.getPassword(username);
        if (dbCredentials == null) {
            this.getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("jdbcRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        boolean validated = this.getCredentialHandler().matches(credentials, dbCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("jdbcRealm.authenticateSuccess", new Object[]{username}));
            }
        } else {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("jdbcRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        ArrayList<String> roles = this.getRoles(username);
        return new GenericPrincipal(username, credentials, roles);
    }

    @Override
    public boolean isAvailable() {
        return this.dbConnection != null;
    }

    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            this.preparedCredentials.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedCredentials = null;
        try {
            this.preparedRoles.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.preparedRoles = null;
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            this.containerLog.warn((Object)sm.getString("jdbcRealm.close"), (Throwable)e);
        }
        finally {
            this.dbConnection = null;
        }
    }

    protected PreparedStatement credentials(Connection dbConnection, String username) throws SQLException {
        if (this.preparedCredentials == null) {
            StringBuilder sb = new StringBuilder("SELECT ");
            sb.append(this.userCredCol);
            sb.append(" FROM ");
            sb.append(this.userTable);
            sb.append(" WHERE ");
            sb.append(this.userNameCol);
            sb.append(" = ?");
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("credentials query: " + sb.toString()));
            }
            this.preparedCredentials = dbConnection.prepareStatement(sb.toString());
        }
        if (username == null) {
            this.preparedCredentials.setNull(1, 12);
        } else {
            this.preparedCredentials.setString(1, username);
        }
        return this.preparedCredentials;
    }

    @Override
    protected synchronized String getPassword(String username) {
        String dbCredentials = null;
        int numberOfTries = 2;
        while (numberOfTries > 0) {
            String string;
            block12: {
                this.open();
                PreparedStatement stmt = this.credentials(this.dbConnection, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        dbCredentials = rs.getString(1);
                    }
                    this.dbConnection.commit();
                    if (dbCredentials != null) {
                        dbCredentials = dbCredentials.trim();
                    }
                    string = dbCredentials;
                    if (rs == null) break block12;
                }
                catch (Throwable throwable) {
                    try {
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
                    catch (SQLException e) {
                        this.containerLog.error((Object)sm.getString("jdbcRealm.exception"), (Throwable)e);
                        if (this.dbConnection != null) {
                            this.close(this.dbConnection);
                        }
                        --numberOfTries;
                    }
                }
                rs.close();
            }
            return string;
        }
        return null;
    }

    @Override
    protected synchronized Principal getPrincipal(String username) {
        return new GenericPrincipal(username, this.getPassword(username), this.getRoles(username));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected ArrayList<String> getRoles(String username) {
        if (this.allRolesMode != RealmBase.AllRolesMode.STRICT_MODE && !this.isRoleStoreDefined()) {
            return null;
        }
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            try {
                this.open();
                PreparedStatement stmt = this.roles(this.dbConnection, username);
                try {
                    ArrayList<String> arrayList;
                    block14: {
                        ResultSet rs = stmt.executeQuery();
                        try {
                            ArrayList<String> roleList = new ArrayList<String>();
                            while (rs.next()) {
                                String role = rs.getString(1);
                                if (null == role) continue;
                                roleList.add(role.trim());
                            }
                            arrayList = roleList;
                            if (rs == null) break block14;
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
                finally {
                    this.dbConnection.commit();
                }
            }
            catch (SQLException e) {
                this.containerLog.error((Object)sm.getString("jdbcRealm.exception"), (Throwable)e);
                if (this.dbConnection == null) continue;
                this.close(this.dbConnection);
                continue;
            }
        }
        return null;
    }

    protected Connection open() throws SQLException {
        if (this.dbConnection != null) {
            return this.dbConnection;
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                throw new SQLException(e.getMessage(), e);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put("user", this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put("password", this.connectionPassword);
        }
        this.dbConnection = this.driver.connect(this.connectionURL, props);
        if (this.dbConnection == null) {
            throw new SQLException(sm.getString("jdbcRealm.open.invalidurl", new Object[]{this.driverName, this.connectionURL}));
        }
        this.dbConnection.setAutoCommit(false);
        return this.dbConnection;
    }

    protected synchronized PreparedStatement roles(Connection dbConnection, String username) throws SQLException {
        if (this.preparedRoles == null) {
            StringBuilder sb = new StringBuilder("SELECT ");
            sb.append(this.roleNameCol);
            sb.append(" FROM ");
            sb.append(this.userRoleTable);
            sb.append(" WHERE ");
            sb.append(this.userNameCol);
            sb.append(" = ?");
            this.preparedRoles = dbConnection.prepareStatement(sb.toString());
        }
        this.preparedRoles.setString(1, username);
        return this.preparedRoles;
    }

    private boolean isRoleStoreDefined() {
        return this.userRoleTable != null || this.roleNameCol != null;
    }

    @Override
    protected void startInternal() throws LifecycleException {
        try {
            this.open();
        }
        catch (SQLException e) {
            this.containerLog.error((Object)sm.getString("jdbcRealm.open"), (Throwable)e);
        }
        super.startInternal();
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.close(this.dbConnection);
    }
}

