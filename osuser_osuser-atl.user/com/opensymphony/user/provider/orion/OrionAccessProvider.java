/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.Group
 *  com.evermind.security.User
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.orion;

import com.evermind.security.Group;
import com.evermind.security.User;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.orion.OrionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrionAccessProvider
extends OrionProvider
implements AccessProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$orion$OrionAccessProvider == null ? (class$com$opensymphony$user$provider$orion$OrionAccessProvider = OrionAccessProvider.class$("com.opensymphony.user.provider.orion.OrionAccessProvider")) : class$com$opensymphony$user$provider$orion$OrionAccessProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$orion$OrionAccessProvider;

    public boolean addToGroup(String username, String groupname) {
        User user = this.userManager.getUser(username);
        try {
            user.addToGroup(this.userManager.getGroup(groupname));
            this.userManager.store();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Added user " + username + " to group " + groupname));
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error assigning user " + username + " to group " + groupname), (Throwable)ex);
            return false;
        }
    }

    public boolean create(String name) {
        try {
            this.userManager.createGroup(name);
            this.userManager.store();
            return true;
        }
        catch (IllegalArgumentException ex) {
            log.warn((Object)("Cannot create group " + name + ": Group already exists"));
        }
        catch (Exception ex) {
            log.error((Object)("Error creating group " + name), (Throwable)ex);
        }
        return false;
    }

    public boolean handles(String name) {
        if (this.userManager.getUser(name) != null) {
            return true;
        }
        return this.userManager.getGroup(name) != null;
    }

    public boolean inGroup(String username, String groupname) {
        User user = this.userManager.getUser(username);
        if (user == null) {
            return false;
        }
        try {
            return user.isMemberOf(this.userManager.getGroup(groupname));
        }
        catch (Exception ex) {
            log.error((Object)("Error checking inGroup(" + username + ", " + groupname + ")"), (Throwable)ex);
            return false;
        }
    }

    public List list() {
        try {
            List groups = this.userManager.getGroups(0, this.userManager.getGroupCount());
            ArrayList<String> result = new ArrayList<String>(groups.size());
            Iterator iter = groups.iterator();
            while (iter.hasNext()) {
                Group group = (Group)iter.next();
                result.add(group.getName());
            }
            return Collections.unmodifiableList(result);
        }
        catch (Exception ex) {
            log.error((Object)"Error getting list of groups", (Throwable)ex);
            return null;
        }
    }

    public List listGroupsContainingUser(String username) {
        User user = this.userManager.getUser(username);
        Set set = user.getGroups();
        if (set == null) {
            return Collections.EMPTY_LIST;
        }
        if (set.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<String> result = new ArrayList<String>(set.size());
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            Group group = (Group)iter.next();
            result.add(group.getName());
        }
        return result;
    }

    public List listUsersInGroup(String groupname) {
        return null;
    }

    public boolean remove(String name) {
        try {
            this.userManager.remove(this.userManager.getGroup(name));
            this.userManager.store();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Removed " + name));
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error removing group " + name), (Throwable)ex);
            return false;
        }
    }

    public boolean removeFromGroup(String username, String groupname) {
        User user = this.userManager.getUser(username);
        try {
            user.removeFromGroup(this.userManager.getGroup(groupname));
            this.userManager.store();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Removed user " + username + " from group " + groupname));
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error removing user " + username + " to group " + groupname), (Throwable)ex);
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

