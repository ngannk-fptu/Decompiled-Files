/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.pramati.services.security.UserManagerException
 *  com.pramati.services.security.spi.UserManager
 */
package com.opensymphony.user.adapter.pramati;

import com.opensymphony.user.Group;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.pramati.services.security.UserManagerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PramatiUserManager
implements com.pramati.services.security.spi.UserManager {
    private Map options;
    private String realmName;
    private UserManager um;

    public List getAllGroups() throws UserManagerException {
        System.out.println("getAllGroups");
        try {
            this.getUserManager();
            List groups = this.um.getGroups();
            ArrayList<String> g = new ArrayList<String>(groups.size());
            Iterator iterator = groups.iterator();
            while (iterator.hasNext()) {
                Group group = (Group)iterator.next();
                g.add(group.getName());
            }
            return g;
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public List getAllGroupsForUser(String name) throws UserManagerException {
        System.out.println("getAllGroupsForUser");
        try {
            this.getUserManager();
            return this.um.getUser(name).getGroups();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public List getAllUsers() throws UserManagerException {
        System.out.println("getAllUsers");
        try {
            this.getUserManager();
            List users = this.um.getUsers();
            ArrayList<String> u = new ArrayList<String>(users.size());
            Iterator iterator = users.iterator();
            while (iterator.hasNext()) {
                User user = (User)iterator.next();
                u.add(user.getName());
            }
            return u;
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public List getAllUsersForGroup(String name) throws UserManagerException {
        System.out.println("getAllUsersForGroup");
        try {
            this.getUserManager();
            List users = this.um.getGroup(name).getUsers();
            ArrayList<String> u = new ArrayList<String>(users.size());
            Iterator iterator = users.iterator();
            while (iterator.hasNext()) {
                String user = (String)iterator.next();
                u.add(user);
            }
            return u;
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public List getChildGroupsForGroup(String s) throws UserManagerException {
        System.out.println("getChildGroupsForGroup");
        return Collections.EMPTY_LIST;
    }

    public Map getOptions() {
        return this.options;
    }

    public List getParentGroupsForGroup(String s) throws UserManagerException {
        System.out.println("getParentGroupsForGroup");
        return Collections.EMPTY_LIST;
    }

    public boolean addGroup(String name, List list, List list1) throws UserManagerException {
        System.out.println("addGroup");
        try {
            this.getUserManager();
            this.um.createGroup(name);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(String name, String password) throws UserManagerException {
        System.out.println("addUser");
        try {
            this.getUserManager();
            this.um.createUser(name).setPassword(password);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(String name, String password, List list) throws UserManagerException {
        System.out.println("addUser2");
        try {
            this.getUserManager();
            User user = this.um.createUser(name);
            user.setPassword(password);
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                String group = (String)iterator.next();
                user.addToGroup(this.um.getGroup(group));
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(String name, String oldPassword, String newPassword) throws UserManagerException {
        System.out.println("changePassword");
        try {
            this.getUserManager();
            User user = this.um.getUser(name);
            if (user.authenticate(oldPassword)) {
                user.setPassword(newPassword);
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initialize(String realmName, Map options) throws UserManagerException {
        this.um = UserManager.getInstance();
        this.options = options;
        this.realmName = realmName;
    }

    public boolean modifyGroup(String name, List users, List groups) throws UserManagerException {
        System.out.println("modifyGroup");
        return false;
    }

    public boolean modifyUser(String name, List groups) throws UserManagerException {
        System.out.println("modifyUser");
        try {
            Object group;
            this.getUserManager();
            User user = this.um.getUser(name);
            List oldGroups = user.getGroups();
            Iterator iterator = oldGroups.iterator();
            while (iterator.hasNext()) {
                group = (Group)iterator.next();
                user.removeFromGroup((Group)group);
            }
            iterator = groups.iterator();
            while (iterator.hasNext()) {
                group = (String)iterator.next();
                user.addToGroup(this.um.getGroup((String)group));
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeChildGroupsForGroup(List list, String s) throws UserManagerException {
        System.out.println("removeChildGroupsForGroup");
        return false;
    }

    public boolean removeGroup(String name) throws UserManagerException {
        System.out.println("removeGroup");
        try {
            this.getUserManager();
            this.um.getGroup(name).remove();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUser(String s) throws UserManagerException {
        System.out.println("removeUser");
        try {
            this.getUserManager();
            this.um.getUser(s).remove();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUsersForGroup(List list, String name) throws UserManagerException {
        System.out.println("removeUsersForGroup");
        try {
            this.getUserManager();
            Group group = this.um.getGroup(name);
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                String user = (String)iterator.next();
                group.removeUser(this.um.getUser(user));
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getUserManager() {
        if (this.um == null) {
            this.um = UserManager.getInstance();
        }
    }
}

