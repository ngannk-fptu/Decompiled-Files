/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.AbstractUserManager
 *  com.evermind.security.Group
 *  com.evermind.security.User
 *  com.evermind.security.UserAlreadyExistsException
 *  com.evermind.security.UserManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.adapter.orion;

import com.evermind.security.AbstractUserManager;
import com.evermind.security.UserAlreadyExistsException;
import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.orion.OrionGroupAdapter;
import com.opensymphony.user.provider.orion.OrionUserAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrionUserManagerAdapter
extends AbstractUserManager {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$adapter$orion$OrionUserManagerAdapter == null ? (class$com$opensymphony$user$adapter$orion$OrionUserManagerAdapter = OrionUserManagerAdapter.class$("com.opensymphony.user.adapter.orion.OrionUserManagerAdapter")) : class$com$opensymphony$user$adapter$orion$OrionUserManagerAdapter));
    private UserManager userManager;
    static /* synthetic */ Class class$com$opensymphony$user$adapter$orion$OrionUserManagerAdapter;

    public com.evermind.security.Group getGroup(String name) {
        com.evermind.security.Group group = null;
        try {
            group = new OrionGroupAdapter(this.userManager.getGroup(name));
            com.evermind.security.Group parentGroup = this.parent.getGroup(name);
            if (parentGroup != null) {
                ((OrionGroupAdapter)group).setPermissions(parentGroup.getPermissions());
            }
        }
        catch (EntityNotFoundException ex) {
            group = this.parent.getGroup(name);
        }
        return group;
    }

    public int getGroupCount() {
        return this.userManager.getGroups().size();
    }

    public List getGroups(int start, int end) {
        if (end <= start) {
            return Collections.EMPTY_LIST;
        }
        ArrayList groups = new ArrayList(this.userManager.getGroups());
        ArrayList<OrionGroupAdapter> results = new ArrayList<OrionGroupAdapter>(end - start);
        for (int i = start; i < end; ++i) {
            results.add(new OrionGroupAdapter((Group)groups.get(i)));
        }
        return results;
    }

    public com.evermind.security.User getUser(String name) {
        com.evermind.security.User user = null;
        try {
            user = new OrionUserAdapter((com.evermind.security.UserManager)this, this.userManager, this.userManager.getUser(name));
        }
        catch (EntityNotFoundException ex) {
            user = this.parent.getUser(name);
        }
        return user;
    }

    public int getUserCount() {
        return this.userManager.getUsers().size();
    }

    public List getUsers(int start, int end) {
        if (end <= start) {
            return Collections.EMPTY_LIST;
        }
        ArrayList users = new ArrayList(this.userManager.getUsers());
        ArrayList<OrionUserAdapter> results = new ArrayList<OrionUserAdapter>(end - start);
        for (int i = start; i < end; ++i) {
            results.add(new OrionUserAdapter((com.evermind.security.UserManager)this, this.userManager, (User)users.get(i)));
        }
        return results;
    }

    public com.evermind.security.Group createGroup(String name) throws InstantiationException {
        try {
            Group group = this.userManager.createGroup(name);
            return new OrionGroupAdapter(group);
        }
        catch (DuplicateEntityException ex) {
            throw new InstantiationException("Group " + name + " already exists");
        }
        catch (ImmutableException ex) {
            throw new InstantiationException("Group " + name + " cannot be created:" + ex.getMessage());
        }
    }

    public com.evermind.security.User createUser(String name, String password) throws InstantiationException, UserAlreadyExistsException {
        try {
            this.userManager.getUser(name);
            throw new UserAlreadyExistsException("User " + name + " already exists");
        }
        catch (EntityNotFoundException ex) {
            try {
                User user = this.userManager.createUser(name);
                user.setPassword(password);
                return new OrionUserAdapter((com.evermind.security.UserManager)this, this.userManager, user);
            }
            catch (ImmutableException ex2) {
                log.error((Object)"User is immutable", (Throwable)ex2);
                throw new InstantiationException("Unable to create user " + name);
            }
            catch (DuplicateEntityException ex3) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"user already exists", (Throwable)ex3);
                }
                throw new UserAlreadyExistsException(ex3.getMessage());
            }
        }
    }

    public void init(Properties properties) throws InstantiationException {
        super.init(properties);
        this.userManager = UserManager.getInstance();
    }

    public void invalidate() {
        this.userManager = null;
    }

    public boolean remove(com.evermind.security.User user) {
        try {
            this.userManager.getUser(user.getName()).remove();
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error removing user " + user.getName() + ":" + ex));
            return false;
        }
    }

    public boolean remove(com.evermind.security.Group group) {
        try {
            return this.userManager.getGroup(group.getName()).getAccessProvider().remove(group.getName());
        }
        catch (EntityNotFoundException ex) {
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

