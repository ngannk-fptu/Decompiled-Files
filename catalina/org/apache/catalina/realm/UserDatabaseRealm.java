/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.realm;

import java.io.ObjectStreamException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import javax.naming.Context;
import org.apache.catalina.Group;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.util.ExceptionUtils;

public class UserDatabaseRealm
extends RealmBase {
    protected volatile UserDatabase database = null;
    private final Object databaseLock = new Object();
    protected String resourceName = "UserDatabase";
    private boolean localJndiResource = false;
    private boolean useStaticPrincipal = false;

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean getUseStaticPrincipal() {
        return this.useStaticPrincipal;
    }

    public void setUseStaticPrincipal(boolean useStaticPrincipal) {
        this.useStaticPrincipal = useStaticPrincipal;
    }

    public boolean getLocalJndiResource() {
        return this.localJndiResource;
    }

    public void setLocalJndiResource(boolean localJndiResource) {
        this.localJndiResource = localJndiResource;
    }

    @Override
    public void backgroundProcess() {
        UserDatabase database = this.getUserDatabase();
        if (database != null) {
            database.backgroundProcess();
        }
    }

    @Override
    protected String getPassword(String username) {
        UserDatabase database = this.getUserDatabase();
        if (database == null) {
            return null;
        }
        User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        return user.getPassword();
    }

    public static String[] getRoles(User user) {
        HashSet<String> roles = new HashSet<String>();
        Iterator<Role> uroles = user.getRoles();
        while (uroles.hasNext()) {
            Role role = uroles.next();
            roles.add(role.getName());
        }
        Iterator<Group> groups = user.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            uroles = group.getRoles();
            while (uroles.hasNext()) {
                Role role = uroles.next();
                roles.add(role.getName());
            }
        }
        return roles.toArray(new String[0]);
    }

    @Override
    protected Principal getPrincipal(String username) {
        UserDatabase database = this.getUserDatabase();
        if (database == null) {
            return null;
        }
        User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        if (this.useStaticPrincipal) {
            return new GenericPrincipal(username, null, Arrays.asList(UserDatabaseRealm.getRoles(user)));
        }
        return new UserDatabasePrincipal(user, database);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UserDatabase getUserDatabase() {
        if (this.database == null) {
            Object object = this.databaseLock;
            synchronized (object) {
                if (this.database == null) {
                    try {
                        Context context = null;
                        if (this.localJndiResource) {
                            context = ContextBindings.getClassLoader();
                            context = (Context)context.lookup("comp/env");
                        } else {
                            Server server = this.getServer();
                            if (server == null) {
                                this.containerLog.error((Object)sm.getString("userDatabaseRealm.noNamingContext"));
                                return null;
                            }
                            context = this.getServer().getGlobalNamingContext();
                        }
                        this.database = (UserDatabase)context.lookup(this.resourceName);
                    }
                    catch (Throwable e) {
                        ExceptionUtils.handleThrowable((Throwable)e);
                        if (this.containerLog != null) {
                            this.containerLog.error((Object)sm.getString("userDatabaseRealm.lookup", new Object[]{this.resourceName}), e);
                        }
                        this.database = null;
                    }
                }
            }
        }
        return this.database;
    }

    @Override
    protected void startInternal() throws LifecycleException {
        UserDatabase database;
        if (!this.localJndiResource && (database = this.getUserDatabase()) == null) {
            throw new LifecycleException(sm.getString("userDatabaseRealm.noDatabase", new Object[]{this.resourceName}));
        }
        super.startInternal();
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.database = null;
    }

    @Override
    public boolean isAvailable() {
        return this.database == null ? false : this.database.isAvailable();
    }

    public static final class UserDatabasePrincipal
    extends GenericPrincipal {
        private static final long serialVersionUID = 1L;
        private final transient UserDatabase database;

        public UserDatabasePrincipal(User user, UserDatabase database) {
            super(user.getName(), null, null);
            this.database = database;
        }

        @Override
        public String[] getRoles() {
            if (this.database == null) {
                return new String[0];
            }
            User user = this.database.findUser(this.name);
            if (user == null) {
                return new String[0];
            }
            HashSet<String> roles = new HashSet<String>();
            Iterator<Role> uroles = user.getRoles();
            while (uroles.hasNext()) {
                Role role = uroles.next();
                roles.add(role.getName());
            }
            Iterator<Group> groups = user.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                uroles = group.getRoles();
                while (uroles.hasNext()) {
                    Role role = uroles.next();
                    roles.add(role.getName());
                }
            }
            return roles.toArray(new String[0]);
        }

        @Override
        public boolean hasRole(String role) {
            if ("*".equals(role)) {
                return true;
            }
            if (role == null) {
                return false;
            }
            if (this.database == null) {
                return super.hasRole(role);
            }
            Role dbrole = this.database.findRole(role);
            if (dbrole == null) {
                return false;
            }
            User user = this.database.findUser(this.name);
            if (user == null) {
                return false;
            }
            if (user.isInRole(dbrole)) {
                return true;
            }
            Iterator<Group> groups = user.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                if (!group.isInRole(dbrole)) continue;
                return true;
            }
            return false;
        }

        private Object writeReplace() throws ObjectStreamException {
            return new GenericPrincipal(this.getName(), null, Arrays.asList(this.getRoles()));
        }
    }
}

