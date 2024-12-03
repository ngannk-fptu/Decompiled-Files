/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.adapter.jrun;

import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JRunUserManager
implements DynamicMBean {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$adapter$jrun$JRunUserManager == null ? (class$com$opensymphony$user$adapter$jrun$JRunUserManager = JRunUserManager.class$("com.opensymphony.user.adapter.jrun.JRunUserManager")) : class$com$opensymphony$user$adapter$jrun$JRunUserManager));
    private String securityStore;
    private UserManager um = UserManager.getInstance();
    static /* synthetic */ Class class$com$opensymphony$user$adapter$jrun$JRunUserManager;

    public JRunUserManager() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"JRun-OSUser adapter initialized");
        }
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    }

    public Object getAttribute(String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return null;
    }

    public AttributeList setAttributes(AttributeList attributeList) {
        return null;
    }

    public AttributeList getAttributes(String[] strings) {
        return null;
    }

    public void setEncrypted(boolean b) {
        log.warn((Object)"setEncrypted not supported");
    }

    public boolean isEncrypted() {
        log.warn((Object)"isEncrypted not supported");
        return false;
    }

    public void setEncrypterClass(String s) {
        log.warn((Object)"setEncrypterClass not supported");
    }

    public String getEncrypterClass() {
        log.warn((Object)"getEncrypterClass not supported");
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo("com.opensymphony.user.adapter.jrun.JRunUserManager", "OSUser-JRun Adapater", null, null, null, null);
    }

    public char[] getPassword(String s) {
        log.warn((Object)"getPassword not supported in OSUser");
        return new char[0];
    }

    public String getPasswordString(String s) {
        log.warn((Object)"getPasswordString not support in OSUser");
        return "";
    }

    public boolean isRole(String role) {
        Group group = null;
        try {
            group = this.um.getGroup(role);
        }
        catch (EntityNotFoundException entityNotFoundException) {
            // empty catch block
        }
        return group != null;
    }

    public String getRoleDescription(String role) {
        return "";
    }

    public Collection getRoles(String username) {
        try {
            User user = this.um.getUser(username);
            return user.getGroups();
        }
        catch (EntityNotFoundException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public void setSecurityStore(String securityStore) {
        this.securityStore = securityStore;
    }

    public String getSecurityStore() {
        return this.securityStore;
    }

    public boolean isUser(String username) {
        User user = null;
        try {
            user = this.um.getUser(username);
        }
        catch (EntityNotFoundException entityNotFoundException) {
            // empty catch block
        }
        return user != null;
    }

    public String getUserDescription(String username) {
        return "";
    }

    public boolean isUserInRole(String username, String role) {
        try {
            return this.um.getUser(username).inGroup(role);
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public Collection getUsers(String role) {
        try {
            Group group = this.um.getGroup(role);
            return group.getUsers();
        }
        catch (EntityNotFoundException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public boolean addRole(String role, String desc) {
        try {
            this.um.createGroup(role);
            return true;
        }
        catch (DuplicateEntityException e) {
            return false;
        }
        catch (ImmutableException e) {
            return false;
        }
    }

    public boolean addUser(String username, String password, String desc) {
        try {
            User user = this.um.createUser(username);
            user.setPassword(password);
            return true;
        }
        catch (DuplicateEntityException e) {
            return false;
        }
        catch (ImmutableException e) {
            return false;
        }
    }

    public boolean addUserToRole(String role, String username) {
        try {
            return this.um.getGroup(role).addUser(this.um.getUser(username));
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public int addUsersToRole(String role, Collection usernames) {
        int count = 0;
        Group group = null;
        try {
            group = this.um.getGroup(role);
        }
        catch (EntityNotFoundException e) {
            return 0;
        }
        Iterator iterator = usernames.iterator();
        while (iterator.hasNext()) {
            String username = (String)iterator.next();
            try {
                group.addUser(this.um.getUser(username));
                ++count;
            }
            catch (EntityNotFoundException e) {}
        }
        return count;
    }

    public boolean changePassword(String username, String oldpassword, String newpassword) {
        User user = null;
        try {
            user = this.um.getUser(username);
        }
        catch (EntityNotFoundException e) {
            return false;
        }
        if (user.authenticate(oldpassword)) {
            try {
                user.setPassword(newpassword);
                return true;
            }
            catch (ImmutableException e) {
                return false;
            }
        }
        return false;
    }

    public boolean changeRoleDescription(String role, String desc) {
        return false;
    }

    public boolean changeUserDescription(String username, String desc) {
        return false;
    }

    public void clearAll() {
        log.warn((Object)"clearAll not supported");
    }

    public Object invoke(String s, Object[] objects, String[] strings) throws MBeanException, ReflectionException {
        return null;
    }

    public boolean removeFromRole(String role, String username) {
        try {
            return this.um.getGroup(role).removeUser(this.um.getUser(username));
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public boolean removeRole(String role) {
        try {
            this.um.getGroup(role).remove();
            return true;
        }
        catch (ImmutableException e) {
            return false;
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public boolean removeUser(String username) {
        try {
            this.um.getUser(username).remove();
            return true;
        }
        catch (ImmutableException e) {
            return false;
        }
        catch (EntityNotFoundException e) {
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

