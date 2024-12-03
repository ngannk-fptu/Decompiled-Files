/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.ExternalEntity;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultGroup;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultHibernateGroup
extends DefaultGroup {
    private transient Set<ExternalEntity> externalMembers;
    private transient Set<User> localMembers;
    private long id;

    public DefaultHibernateGroup() {
    }

    public DefaultHibernateGroup(String name) {
        super(name);
        this.externalMembers = new HashSet<ExternalEntity>();
        this.localMembers = new HashSet<User>();
    }

    public Set<ExternalEntity> getExternalMembers() {
        return this.externalMembers;
    }

    public void setExternalMembers(Set<ExternalEntity> externalMembers) {
        this.externalMembers = externalMembers;
    }

    public Set<User> getLocalMembers() {
        return this.localMembers;
    }

    public void setLocalMembers(Set<User> localMembers) {
        this.localMembers = localMembers;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultHibernateGroup)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DefaultHibernateGroup defaultHibernateGroup = (DefaultHibernateGroup)o;
        return this.id == defaultHibernateGroup.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (int)(this.id ^ this.id >>> 32);
        return result;
    }
}

