/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 *  java.security.acl.NotOwnerException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.logging.LogOutputStream
 *  weblogic.security.acl.AbstractManageableRealm
 *  weblogic.security.acl.DebuggableRealm
 *  weblogic.security.acl.Everyone
 *  weblogic.security.acl.ListableRealm
 *  weblogic.security.acl.User
 */
package com.opensymphony.user.adapter.weblogic61;

import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.adapter.weblogic61.OSUserRealmGroup;
import com.opensymphony.user.adapter.weblogic61.OSUserRealmUser;
import java.security.acl.NotOwnerException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.logging.LogOutputStream;
import weblogic.security.acl.AbstractManageableRealm;
import weblogic.security.acl.DebuggableRealm;
import weblogic.security.acl.Everyone;
import weblogic.security.acl.ListableRealm;

public class OSUserRealm
extends AbstractManageableRealm
implements DebuggableRealm {
    private static final Log osLog = LogFactory.getLog((Class)(class$com$opensymphony$user$adapter$weblogic61$OSUserRealm == null ? (class$com$opensymphony$user$adapter$weblogic61$OSUserRealm = OSUserRealm.class$("com.opensymphony.user.adapter.weblogic61.OSUserRealm")) : class$com$opensymphony$user$adapter$weblogic61$OSUserRealm));
    java.security.acl.Group everyoneGroup = new Everyone((ListableRealm)this);
    private LogOutputStream wlLog;
    private UserManager um;
    static /* synthetic */ Class class$com$opensymphony$user$adapter$weblogic61$OSUserRealm;

    public OSUserRealm() {
        super("OSUserRealm");
    }

    public void setDebug(boolean enable) {
        if (enable && this.wlLog == null) {
            this.wlLog = new LogOutputStream("RDBMSRealm");
        } else if (!enable) {
            this.wlLog = null;
        }
    }

    public LogOutputStream getDebugLog() {
        return this.wlLog;
    }

    public java.security.acl.Group getGroup(String name) {
        osLog.info((Object)("Starting OSUserRealm::getGroup(" + name + ")"));
        Group osGroup = null;
        if ("everyone".equals(name)) {
            return this.everyoneGroup;
        }
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request.");
                return null;
            }
            osGroup = this.um.getGroup(name);
            if (osGroup == null) {
                osLog.debug((Object)"Group returned by UserManager was null");
                return null;
            }
            return new OSUserRealmGroup(osGroup, this);
        }
        catch (EntityNotFoundException enfe) {
            osLog.error((Object)("Requested group [" + name + "] was not found"));
            return null;
        }
    }

    public Enumeration getGroups() {
        osLog.info((Object)"Starting OSUserRealm::getGroups()");
        return new OSUserRealmGroup.GroupEnum(this);
    }

    public weblogic.security.acl.User getUser(String name) {
        osLog.info((Object)("Starting OSUserRealm::getUser(" + name + ")"));
        User osUser = null;
        try {
            if (this.um == null) {
                osLog.debug((Object)"UserManager was null; unable to complete request");
                return null;
            }
            osUser = this.um.getUser(name);
            if (osUser == null) {
                osLog.debug((Object)"osUser was not found");
                return null;
            }
            return new OSUserRealmUser(osUser, this);
        }
        catch (EntityNotFoundException enfe) {
            if (osLog.isDebugEnabled()) {
                osLog.debug((Object)("Requested user [" + name + "] was not found"));
            }
            return null;
        }
    }

    public Enumeration getUsers() {
        osLog.info((Object)"Starting OSUserRealm::getUsers()");
        return new OSUserRealmUser.UserEnum(this);
    }

    public void deleteGroup(java.security.acl.Group group) throws SecurityException {
        osLog.info((Object)("Starting OSUserRealm::deleteGroup(" + group + ")"));
        Group osGroup = null;
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request.");
                return;
            }
            osGroup = this.um.getGroup(group.getName());
            osGroup.remove();
        }
        catch (EntityNotFoundException e) {
            osLog.info((Object)"Group to be removed did not exists in OSUser security store.");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to delete group [name=" + group.getName() + "]. Groups are immutable.");
        }
    }

    public void deleteUser(weblogic.security.acl.User user) throws SecurityException {
        osLog.info((Object)("Starting OSUserRealm::deleteUser(" + user + ")"));
        User osUser = null;
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request.");
                return;
            }
            osUser = this.um.getUser(user.getName());
            osUser.remove();
        }
        catch (EntityNotFoundException e) {
            osLog.info((Object)"User to be removed did not exists in OSUser security store.");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to delete user [name=" + user.getName() + "]. Users are immutable.");
        }
    }

    public void init(String name, Object owner) throws NotOwnerException {
        super.init(name, owner);
        if (osLog.isDebugEnabled()) {
            osLog.debug((Object)("Init values -> name: " + name + ", owner:" + owner));
        }
        this.um = UserManager.getInstance();
    }

    public java.security.acl.Group newGroup(String name) throws SecurityException {
        osLog.info((Object)("Starting OSUserRealm::newGroup() for [" + name + "]"));
        Group osGroup = null;
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request.");
                return null;
            }
            osGroup = this.um.createGroup(name);
            return new OSUserRealmGroup(osGroup, this);
        }
        catch (DuplicateEntityException e) {
            throw new SecurityException("Unable to create group '" + name + "'. Group already exists");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to create group '" + name + "'. Group set is immutable.");
        }
    }

    public weblogic.security.acl.User newUser(String name, Object credential, Object constraints) throws SecurityException {
        osLog.info((Object)("Starting OSUserRealm::newUser() for [" + name + "]"));
        User osUser = null;
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request");
                return null;
            }
            if (!(credential instanceof String)) {
                throw new SecurityException("Unable to create user '" + name + "'. Non-String credentials (passwords) are not allowed.");
            }
            osUser = this.um.createUser(name);
            osUser.setPassword((String)credential);
            return new OSUserRealmUser(osUser, this);
        }
        catch (DuplicateEntityException e) {
            throw new SecurityException("Unable to create user '" + name + "'. User already exists");
        }
        catch (ImmutableException e) {
            throw new SecurityException("Unable to create user '" + name + "'. User set is immutable.");
        }
    }

    protected Hashtable getGroupMembersInternal(String name) {
        osLog.info((Object)("Starting OSUserRealm::getGroupMembersInternal(" + name + ")"));
        Group osGroup = null;
        User osUser = null;
        Hashtable<String, OSUserRealmUser> members = new Hashtable<String, OSUserRealmUser>();
        try {
            if (this.um == null) {
                osLog.warn((Object)"UserManager was null; unable to complete request.");
                return null;
            }
            osGroup = this.um.getGroup(name);
            if (osGroup == null) {
                osLog.debug((Object)"Group returned by UserManager was null");
                return null;
            }
            Iterator itr = osGroup.getUsers().iterator();
            while (itr.hasNext()) {
                osUser = (User)itr.next();
                members.put(osUser.getName(), new OSUserRealmUser(osUser, this));
            }
        }
        catch (Throwable t) {
            osLog.warn((Object)"Unexpected error occurred loading group membership", t);
        }
        return members;
    }

    protected weblogic.security.acl.User authUserPassword(String username, String password) {
        osLog.info((Object)("Starting OSUserRealm::authUserPassword(" + username + ", *)"));
        User osUser = null;
        try {
            weblogic.security.acl.User wlUser = this.getUser(username);
            if (wlUser == null) {
                osLog.debug((Object)("Unable to locate user [name=" + username + "]"));
                return null;
            }
            osUser = ((OSUserRealmUser)wlUser).osUser;
            if (osUser.authenticate(password)) {
                osLog.debug((Object)"User.authenticate() was successful");
                return new OSUserRealmUser(osUser, this);
            }
            osLog.debug((Object)"User.authenticate() failed");
            return null;
        }
        catch (Throwable t) {
            osLog.warn((Object)"Error performing authentication", t);
            return null;
        }
    }

    List getOSGroups() {
        return this.um.getGroups();
    }

    List getOSUsers() {
        return this.um.getUsers();
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

