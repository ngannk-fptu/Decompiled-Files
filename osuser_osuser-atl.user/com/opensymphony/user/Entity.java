/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.opensymphony.user;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import java.io.Serializable;

public abstract class Entity
implements Serializable {
    protected Accessor accessor;
    protected String name;
    protected boolean mutable;
    private ManagerAccessor managerAccessor;

    protected Entity(String name, ManagerAccessor managerAccessor) {
        this.name = name;
        this.managerAccessor = managerAccessor;
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.managerAccessor.getCredentialsProvider(this.name);
    }

    public String getName() {
        return this.name;
    }

    public ProfileProvider getProfileProvider() {
        return this.managerAccessor.getProfileProvider(this.name);
    }

    public PropertySet getPropertySet() {
        ProfileProvider profileProvider = this.getProfileProvider();
        if (!profileProvider.handles(this.name)) {
            profileProvider.create(this.name);
        }
        return profileProvider.getPropertySet(this.name);
    }

    public abstract void remove() throws ImmutableException;

    public AccessProvider getAccessProvider() {
        return this.managerAccessor.getAccessProvider(this.name);
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public UserManager getUserManager() {
        return this.managerAccessor.getUserManager();
    }

    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        return this.name.equals(((Entity)obj).getName());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public void store() throws ImmutableException {
        if (!this.mutable) {
            throw new ImmutableException();
        }
    }

    public String toString() {
        return this.name;
    }

    public abstract class Accessor
    implements Serializable {
        public Entity getEntity() {
            return Entity.this;
        }

        public void setMutable(boolean mutable) {
            Entity.this.mutable = mutable;
        }

        public void setName(String name) {
            Entity.this.name = name;
        }
    }
}

