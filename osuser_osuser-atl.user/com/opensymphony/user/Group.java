/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 */
package com.opensymphony.user;

import com.opensymphony.user.Entity;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.User;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.ProfileProvider;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public final class Group
extends Entity
implements java.security.acl.Group {
    public Group(String name, ManagerAccessor managerAccessor) {
        super(name, managerAccessor);
        this.accessor = new Accessor();
        this.getAccessProvider().load(name, this.accessor);
    }

    public boolean isMember(Principal member) {
        if (member instanceof User) {
            return this.containsUser((User)member);
        }
        return this.containsUser(member.getName());
    }

    public List getUsers() {
        return Collections.unmodifiableList(this.getAccessProvider().listUsersInGroup(this.name));
    }

    public boolean addMember(Principal user) {
        if (user instanceof User) {
            return this.addUser((User)user);
        }
        try {
            return this.addUser(this.getUserManager().getUser(user.getName()));
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        return this.getAccessProvider().addToGroup(user.getName(), this.name);
    }

    public boolean containsUser(User user) {
        return user != null && this.getAccessProvider().inGroup(user.getName(), this.name);
    }

    public boolean containsUser(String user) {
        return user != null && this.getAccessProvider().inGroup(user, this.name);
    }

    public Enumeration members() {
        List users = this.getAccessProvider().listUsersInGroup(this.name);
        Vector<User> list = new Vector<User>(users.size());
        Iterator iterator = users.iterator();
        while (iterator.hasNext()) {
            String s = (String)iterator.next();
            try {
                list.add(this.getUserManager().getUser(s));
            }
            catch (EntityNotFoundException e) {}
        }
        return list.elements();
    }

    public void remove() throws ImmutableException {
        AccessProvider accessProvider = this.getAccessProvider();
        if (!this.mutable) {
            throw new ImmutableException();
        }
        if (accessProvider == null) {
            throw new ImmutableException();
        }
        if (!accessProvider.remove(this.name)) {
            throw new ImmutableException();
        }
        ProfileProvider profileProvider = this.getProfileProvider();
        if (profileProvider != null) {
            profileProvider.remove(this.name);
        }
    }

    public boolean removeMember(Principal user) {
        if (user instanceof User) {
            return this.removeUser((User)user);
        }
        try {
            return this.removeUser(this.getUserManager().getUser(user.getName()));
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public boolean removeUser(User user) {
        if (user == null) {
            return false;
        }
        return this.getAccessProvider().removeFromGroup(user.getName(), this.name);
    }

    public final class Accessor
    extends Entity.Accessor
    implements Serializable {
        public Group getGroup() {
            return (Group)this.getEntity();
        }
    }
}

