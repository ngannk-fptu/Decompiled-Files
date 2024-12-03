/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.modeler.BaseModelMBean
 *  org.apache.tomcat.util.modeler.ManagedBean
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class SparseUserDatabaseMBean
extends BaseModelMBean {
    private static final StringManager sm = StringManager.getManager(SparseUserDatabaseMBean.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final MBeanServer mserver = MBeanUtils.createServer();
    protected final ManagedBean managed = this.registry.findManagedBean("SparseUserDatabase");
    protected final ManagedBean managedGroup = this.registry.findManagedBean("Group");
    protected final ManagedBean managedRole = this.registry.findManagedBean("Role");
    protected final ManagedBean managedUser = this.registry.findManagedBean("User");

    public String[] getGroups() {
        UserDatabase database = (UserDatabase)this.resource;
        ArrayList<String> results = new ArrayList<String>();
        Iterator<Group> groups = database.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            results.add(this.findGroup(group.getGroupname()));
        }
        return results.toArray(new String[0]);
    }

    public String[] getRoles() {
        UserDatabase database = (UserDatabase)this.resource;
        ArrayList<String> results = new ArrayList<String>();
        Iterator<Role> roles = database.getRoles();
        while (roles.hasNext()) {
            Role role = roles.next();
            results.add(this.findRole(role.getRolename()));
        }
        return results.toArray(new String[0]);
    }

    public String[] getUsers() {
        UserDatabase database = (UserDatabase)this.resource;
        ArrayList<String> results = new ArrayList<String>();
        Iterator<User> users = database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            results.add(this.findUser(user.getUsername()));
        }
        return results.toArray(new String[0]);
    }

    public String createGroup(String groupname, String description) {
        UserDatabase database = (UserDatabase)this.resource;
        Group group = database.createGroup(groupname, description);
        try {
            MBeanUtils.createMBean(group);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createMBeanError.group", new Object[]{groupname}), e);
        }
        return this.findGroup(groupname);
    }

    public String createRole(String rolename, String description) {
        UserDatabase database = (UserDatabase)this.resource;
        Role role = database.createRole(rolename, description);
        try {
            MBeanUtils.createMBean(role);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createMBeanError.role", new Object[]{rolename}), e);
        }
        return this.findRole(rolename);
    }

    public String createUser(String username, String password, String fullName) {
        UserDatabase database = (UserDatabase)this.resource;
        User user = database.createUser(username, password, fullName);
        try {
            MBeanUtils.createMBean(user);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createMBeanError.user", new Object[]{username}), e);
        }
        return this.findUser(username);
    }

    public String findGroup(String groupname) {
        UserDatabase database = (UserDatabase)this.resource;
        Group group = database.findGroup(groupname);
        if (group == null) {
            return null;
        }
        try {
            ObjectName oname = MBeanUtils.createObjectName(this.managedGroup.getDomain(), group);
            if (database.isSparse() && !this.mserver.isRegistered(oname)) {
                MBeanUtils.createMBean(group);
            }
            return oname.toString();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createError.group", new Object[]{groupname}), e);
        }
    }

    public String findRole(String rolename) {
        UserDatabase database = (UserDatabase)this.resource;
        Role role = database.findRole(rolename);
        if (role == null) {
            return null;
        }
        try {
            ObjectName oname = MBeanUtils.createObjectName(this.managedRole.getDomain(), role);
            if (database.isSparse() && !this.mserver.isRegistered(oname)) {
                MBeanUtils.createMBean(role);
            }
            return oname.toString();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createError.role", new Object[]{rolename}), e);
        }
    }

    public String findUser(String username) {
        UserDatabase database = (UserDatabase)this.resource;
        User user = database.findUser(username);
        if (user == null) {
            return null;
        }
        try {
            ObjectName oname = MBeanUtils.createObjectName(this.managedUser.getDomain(), user);
            if (database.isSparse() && !this.mserver.isRegistered(oname)) {
                MBeanUtils.createMBean(user);
            }
            return oname.toString();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.createError.user", new Object[]{username}), e);
        }
    }

    public void removeGroup(String groupname) {
        UserDatabase database = (UserDatabase)this.resource;
        Group group = database.findGroup(groupname);
        if (group == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(group);
            database.removeGroup(group);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.destroyError.group", new Object[]{groupname}), e);
        }
    }

    public void removeRole(String rolename) {
        UserDatabase database = (UserDatabase)this.resource;
        Role role = database.findRole(rolename);
        if (role == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(role);
            database.removeRole(role);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.destroyError.role", new Object[]{rolename}), e);
        }
    }

    public void removeUser(String username) {
        UserDatabase database = (UserDatabase)this.resource;
        User user = database.findUser(username);
        if (user == null) {
            return;
        }
        try {
            MBeanUtils.destroyMBean(user);
            database.removeUser(user);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.destroyError.user", new Object[]{username}), e);
        }
    }

    public void save() {
        try {
            UserDatabase database = (UserDatabase)this.resource;
            if (database.isSparse()) {
                ObjectName query = null;
                Set<ObjectName> results = null;
                query = new ObjectName("Users:type=Group,database=" + database.getId() + ",*");
                results = this.mserver.queryNames(query, null);
                for (ObjectName result : results) {
                    this.mserver.unregisterMBean(result);
                }
                query = new ObjectName("Users:type=Role,database=" + database.getId() + ",*");
                results = this.mserver.queryNames(query, null);
                for (ObjectName result : results) {
                    this.mserver.unregisterMBean(result);
                }
                query = new ObjectName("Users:type=User,database=" + database.getId() + ",*");
                results = this.mserver.queryNames(query, null);
                for (ObjectName result : results) {
                    this.mserver.unregisterMBean(result);
                }
            }
            database.save();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(sm.getString("userMBean.saveError"), e);
        }
    }
}

