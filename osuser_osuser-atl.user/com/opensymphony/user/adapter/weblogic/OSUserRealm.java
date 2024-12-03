/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 *  java.security.acl.NotOwnerException
 *  weblogic.logging.LogOutputStream
 *  weblogic.security.acl.AbstractManageableRealm
 *  weblogic.security.acl.BasicRealm
 *  weblogic.security.acl.DebuggableRealm
 *  weblogic.security.acl.User
 *  weblogic.security.acl.UserInfo
 */
package com.opensymphony.user.adapter.weblogic;

import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.adapter.weblogic.AclGroupAdapter;
import com.opensymphony.user.adapter.weblogic.CollectionEnum;
import com.opensymphony.user.adapter.weblogic.WeblogicUserAdapter;
import java.security.acl.NotOwnerException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import weblogic.logging.LogOutputStream;
import weblogic.security.acl.AbstractManageableRealm;
import weblogic.security.acl.BasicRealm;
import weblogic.security.acl.DebuggableRealm;
import weblogic.security.acl.UserInfo;

public class OSUserRealm
extends AbstractManageableRealm
implements DebuggableRealm {
    LogOutputStream log;
    private UserManager um;

    public OSUserRealm() {
        super("OSUserRealm");
    }

    public void setDebug(boolean enable) {
        if (enable && this.log == null) {
            this.log = new LogOutputStream("RDBMSRealm");
        }
        if (!enable) {
            this.log = null;
        }
    }

    public LogOutputStream getDebugLog() {
        return this.log;
    }

    public java.security.acl.Group getGroup(String name) {
        System.out.println("getGroup(" + name + ")");
        Group osGroup = null;
        try {
            if (this.um == null) {
                return null;
            }
            osGroup = this.um.getGroup(name);
        }
        catch (EntityNotFoundException entityNotFoundException) {
            // empty catch block
        }
        if (osGroup == null) {
            return null;
        }
        return new AclGroupAdapter(osGroup);
    }

    public Enumeration getGroups() {
        System.out.println("getGroups()");
        if (this.um == null) {
            return null;
        }
        List groups = this.um.getGroups();
        if (groups == null) {
            return null;
        }
        Iterator iter = groups.iterator();
        ArrayList<AclGroupAdapter> aclGroupList = new ArrayList<AclGroupAdapter>();
        while (iter.hasNext()) {
            Group osGroup = (Group)iter.next();
            aclGroupList.add(new AclGroupAdapter(osGroup));
        }
        return new CollectionEnum(aclGroupList);
    }

    public weblogic.security.acl.User getUser(String name) {
        System.out.println("getUser(" + name + ")");
        User osUser = null;
        try {
            if (this.um == null) {
                return null;
            }
            osUser = this.um.getUser(name);
        }
        catch (EntityNotFoundException entityNotFoundException) {
            // empty catch block
        }
        if (osUser == null) {
            return null;
        }
        return new WeblogicUserAdapter(osUser, (BasicRealm)this);
    }

    public weblogic.security.acl.User getUser(UserInfo userInfo) {
        System.out.println("getUser(" + userInfo + ")");
        return this.getUser(userInfo.getName());
    }

    public Enumeration getUsers() {
        System.out.println("getUsers()");
        if (this.um == null) {
            return null;
        }
        List users = this.um.getUsers();
        if (users == null) {
            return null;
        }
        Iterator iter = users.iterator();
        ArrayList<WeblogicUserAdapter> wlUserList = new ArrayList<WeblogicUserAdapter>();
        while (iter.hasNext()) {
            User osUser = (User)iter.next();
            wlUserList.add(new WeblogicUserAdapter(osUser, (BasicRealm)this));
        }
        return new CollectionEnum(wlUserList);
    }

    public void deleteGroup(java.security.acl.Group group) throws SecurityException {
        System.out.println("deleteGroup(" + group + ")");
        if (this.um == null) {
            return;
        }
        try {
            Group osGroup = this.um.getGroup(group.getName());
            osGroup.remove();
        }
        catch (EntityNotFoundException e) {
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to delete group '" + group.getName() + "'. Groups are immutable.");
        }
    }

    public void deleteUser(weblogic.security.acl.User user) throws SecurityException {
        System.out.println("deleteUser(" + user + ")");
        if (this.um == null) {
            return;
        }
        try {
            User osUser = this.um.getUser(user.getName());
            osUser.remove();
        }
        catch (EntityNotFoundException e) {
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to delete user '" + user.getName() + "'. Users are immutable.");
        }
    }

    public void init(String s, Object o) throws NotOwnerException {
        super.init(s, o);
        DelayThread t = new DelayThread();
        t.start();
    }

    public java.security.acl.Group newGroup(String name) throws SecurityException {
        System.out.println("newGroup(" + name + ")");
        if (this.um == null) {
            return null;
        }
        try {
            Group osGroup = this.um.createGroup(name);
            return new AclGroupAdapter(osGroup);
        }
        catch (DuplicateEntityException e) {
            throw new SecurityException("Unable to create group '" + name + "'. Group already exists");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to create group '" + name + "'. Group set is immutable.");
        }
    }

    public weblogic.security.acl.User newUser(String name, Object credential, Object constraints) throws SecurityException {
        System.out.println("newUser(" + name + ", " + credential + ", " + constraints + ")");
        if (this.um == null) {
            return null;
        }
        try {
            User osUser = null;
            osUser = this.um.createUser(name);
            if (!(credential instanceof String)) {
                throw new SecurityException("Unable to create user '" + name + "'. Non-String credentials (passwords) are not allowed.");
            }
            osUser.setPassword((String)credential);
            return new WeblogicUserAdapter(osUser, (BasicRealm)this);
        }
        catch (DuplicateEntityException e) {
            throw new SecurityException("Unable to create user '" + name + "'. User already exists");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to create user '" + name + "'. User set is immutable.");
        }
    }

    private class DelayThread
    extends Thread {
        private DelayThread() {
        }

        public void run() {
            try {
                DelayThread.sleep(30000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            OSUserRealm.this.um = UserManager.getInstance();
        }
    }
}

