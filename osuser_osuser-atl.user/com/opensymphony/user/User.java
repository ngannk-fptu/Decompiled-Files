/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user;

import com.opensymphony.user.Entity;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

public final class User
extends Entity
implements Principal {
    public static final String PROPERTY_FULLNAME = "fullName";
    public static final String PROPERTY_EMAIL = "email";

    public User(String name, ManagerAccessor managerAccessor) {
        super(name, managerAccessor);
        this.accessor = new Accessor();
        this.getCredentialsProvider().load(name, this.accessor);
    }

    public void setEmail(String email) {
        this.getPropertySet().setString(PROPERTY_EMAIL, email);
    }

    public String getEmail() {
        return this.getPropertySet().getString(PROPERTY_EMAIL);
    }

    public void setFullName(String fullName) {
        this.getPropertySet().setString(PROPERTY_FULLNAME, fullName);
    }

    public String getFullName() {
        return this.getPropertySet().getString(PROPERTY_FULLNAME);
    }

    public List getGroups() {
        if (this.getAccessProvider() == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.getAccessProvider().listGroupsContainingUser(this.getName()));
    }

    public void setPassword(String password) throws ImmutableException {
        if (this.mutable && this.getCredentialsProvider().changePassword(this.name, password)) {
            return;
        }
        throw new ImmutableException();
    }

    public boolean addToGroup(Group group) {
        if (group == null) {
            return false;
        }
        return group.getAccessProvider().addToGroup(this.getName(), group.getName());
    }

    public boolean authenticate(String password) {
        if (password == null) {
            return false;
        }
        return this.getCredentialsProvider().authenticate(this.name, password);
    }

    public boolean inGroup(Group group) {
        if (group == null) {
            return false;
        }
        return group.getAccessProvider().inGroup(this.getName(), group.getName());
    }

    public boolean inGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        try {
            return this.inGroup(this.getUserManager().getGroup(groupName));
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public void remove() throws ImmutableException {
        CredentialsProvider credentialsProvider = this.getCredentialsProvider();
        if (!this.mutable) {
            throw new ImmutableException("User is not mutable");
        }
        if (credentialsProvider == null) {
            throw new ImmutableException("No credentials provider for user");
        }
        if (!credentialsProvider.remove(this.name)) {
            throw new ImmutableException("Credentials provider failed to remove user");
        }
        ProfileProvider profileProvider = this.getProfileProvider();
        if (profileProvider != null) {
            profileProvider.remove(this.name);
        }
    }

    public boolean removeFromGroup(Group group) {
        if (group == null) {
            return false;
        }
        return group.getAccessProvider().removeFromGroup(this.getName(), group.getName());
    }

    public void store() throws ImmutableException {
        super.store();
        this.getProfileProvider().store(this.name, this.accessor);
        this.getCredentialsProvider().store(this.name, this.accessor);
    }

    public final class Accessor
    extends Entity.Accessor
    implements Serializable {
        public User getUser() {
            return (User)this.getEntity();
        }
    }
}

