/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.Group;
import com.atlassian.user.impl.DefaultUser;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultHibernateUser
extends DefaultUser {
    private transient Set<Group> groups = new HashSet<Group>();
    private long id;

    public DefaultHibernateUser() {
    }

    public DefaultHibernateUser(String name) {
        super(name);
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
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
        if (!(o instanceof DefaultHibernateUser)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DefaultHibernateUser defaultHibernateUser = (DefaultHibernateUser)o;
        return this.id == defaultHibernateUser.id;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer("id:").append(this.id).append(" name:").append(this.name).append(" fullName:").append(this.fullName).append(" email:").append(this.email).append(" created:").append(this.created);
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (int)(this.id ^ this.id >>> 32);
        return result;
    }
}

