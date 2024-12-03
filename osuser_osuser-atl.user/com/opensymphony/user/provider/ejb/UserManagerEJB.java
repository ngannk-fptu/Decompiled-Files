/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.ejb.SessionAdapter
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.ejb.CreateException
 *  javax.ejb.FinderException
 *  javax.ejb.RemoveException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.ejb.SessionAdapter;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.provider.ejb.entity.GroupHomeFactory;
import com.opensymphony.user.provider.ejb.entity.GroupLocal;
import com.opensymphony.user.provider.ejb.entity.GroupLocalHome;
import com.opensymphony.user.provider.ejb.entity.UserHomeFactory;
import com.opensymphony.user.provider.ejb.entity.UserLocal;
import com.opensymphony.user.provider.ejb.entity.UserLocalHome;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserManagerEJB
extends SessionAdapter {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$UserManagerEJB == null ? (class$com$opensymphony$user$provider$ejb$UserManagerEJB = UserManagerEJB.class$("com.opensymphony.user.provider.ejb.UserManagerEJB")) : class$com$opensymphony$user$provider$ejb$UserManagerEJB));
    private GroupLocalHome groupHome = null;
    private UserLocalHome userHome;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$UserManagerEJB;

    public List getGroupNames() {
        try {
            Iterator iter = this.groupHome.findAll().iterator();
            ArrayList<String> results = new ArrayList<String>();
            while (iter.hasNext()) {
                GroupLocal group = (GroupLocal)iter.next();
                results.add(group.getName());
            }
            return results;
        }
        catch (FinderException e) {
            log.error((Object)"Unexpected error getting group names", (Throwable)e);
            return Collections.EMPTY_LIST;
        }
    }

    public PropertySet getGroupPropertySet(String name) throws RemoteException {
        try {
            return this.groupHome.findByName(name).getPropertySet();
        }
        catch (FinderException e) {
            return null;
        }
    }

    public List getUserGroups(String user) {
        try {
            return this.userHome.findByName(user).getGroupNames();
        }
        catch (FinderException e) {
            log.error((Object)("No such user " + user + " in getUserGroups"));
            return Collections.EMPTY_LIST;
        }
    }

    public boolean isUserInGroup(String user, String group) {
        try {
            return this.userHome.findByName(user).inGroup(group);
        }
        catch (FinderException e) {
            log.error((Object)("No user found for isUserInGroup(" + user + ", " + group + ")"));
            return false;
        }
    }

    public List getUserNames() {
        try {
            Iterator iter = this.userHome.findAll().iterator();
            ArrayList<String> results = new ArrayList<String>();
            while (iter.hasNext()) {
                UserLocal user = (UserLocal)iter.next();
                results.add(user.getName());
            }
            return results;
        }
        catch (FinderException e) {
            log.error((Object)"Unexpected error getting user names", (Throwable)e);
            return Collections.EMPTY_LIST;
        }
    }

    public PropertySet getUserPropertySet(String name) throws RemoteException {
        try {
            return this.userHome.findByName(name).getPropertySet();
        }
        catch (FinderException e) {
            return null;
        }
    }

    public List getUsersInGroup(String group) {
        try {
            return this.groupHome.findByName(group).getUserNames();
        }
        catch (FinderException e) {
            log.error((Object)("No such group " + group + " in getUsersInGroup"));
            return Collections.EMPTY_LIST;
        }
    }

    public boolean addToGroup(String userName, String groupName) {
        GroupLocal group;
        UserLocal user;
        try {
            user = this.userHome.findByName(userName);
        }
        catch (FinderException e) {
            log.error((Object)("No such user " + userName + " exists to add to group " + groupName));
            return false;
        }
        try {
            group = this.groupHome.findByName(groupName);
        }
        catch (FinderException e) {
            log.error((Object)("No such group " + groupName + " exists to add user " + userName + " to"));
            return false;
        }
        return user.getGroups().add(group);
    }

    public boolean authenticate(String user, String password) {
        try {
            return this.userHome.findByName(user).authenticate(password);
        }
        catch (FinderException e) {
            log.error((Object)("Unable to authenticate non-existent user " + user));
            return false;
        }
    }

    public boolean changePassword(String user, String password) {
        try {
            this.userHome.findByName(user).setPassword(password);
            return true;
        }
        catch (FinderException e) {
            log.error((Object)("Unable to modify password for non-existent user " + user));
            return false;
        }
    }

    public boolean createGroup(String name) throws CreateException {
        this.groupHome.create(name);
        return true;
    }

    public boolean createUser(String name) throws CreateException {
        this.userHome.create(name);
        return true;
    }

    public void ejbCreate() throws CreateException {
        try {
            this.groupHome = GroupHomeFactory.getLocalHome();
            this.userHome = UserHomeFactory.getLocalHome();
        }
        catch (NamingException e) {
            log.error((Object)"Error looking up homes", (Throwable)e);
            throw new CreateException(e.getMessage());
        }
    }

    public boolean groupExists(String name) {
        try {
            this.groupHome.findByName(name);
            return true;
        }
        catch (FinderException ex) {
            return false;
        }
    }

    public boolean removeFromGroup(String userName, String groupName) {
        try {
            UserLocal user = this.userHome.findByName(userName);
            return user.removeGroup(groupName);
        }
        catch (FinderException e) {
            log.error((Object)("Cannot remove non-existent user " + userName + " from group " + groupName));
            return false;
        }
    }

    public boolean removeGroup(String group) {
        try {
            this.groupHome.findByName(group).remove();
            return true;
        }
        catch (RemoveException e) {
            log.error((Object)"Error removing group", (Throwable)e);
            return false;
        }
        catch (FinderException e) {
            log.error((Object)("Cannot remove non-existent group " + group));
            return false;
        }
    }

    public boolean removeUser(String user) {
        try {
            this.userHome.findByName(user).remove();
            return true;
        }
        catch (RemoveException e) {
            log.error((Object)"Error removing user", (Throwable)e);
            return false;
        }
        catch (FinderException e) {
            log.error((Object)("Cannot remove non-existent user " + user));
            return false;
        }
    }

    public boolean userExists(String name) {
        try {
            this.userHome.findByName(name);
            return true;
        }
        catch (FinderException ex) {
            return false;
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

