/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.ExternalEntity;
import com.atlassian.user.impl.DefaultEntity;
import java.util.Set;

public class DefaultHibernateExternalEntity
extends DefaultEntity
implements ExternalEntity {
    protected String type;
    private Set groups;
    private long id;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set getGroups() {
        return this.groups;
    }

    public void setGroups(Set groups) {
        this.groups = groups;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultHibernateExternalEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DefaultHibernateExternalEntity defaultHibernateExternalEntity = (DefaultHibernateExternalEntity)o;
        if (this.id != defaultHibernateExternalEntity.id) {
            return false;
        }
        return !(this.type != null ? !this.type.equals(defaultHibernateExternalEntity.type) : defaultHibernateExternalEntity.type != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.type != null ? this.type.hashCode() : 0);
        result = 29 * result + (int)(this.id ^ this.id >>> 32);
        return result;
    }
}

