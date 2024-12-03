/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.Entity
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.Entity;

public abstract class OSUEntity
implements Entity {
    protected com.opensymphony.user.Entity entity;

    public OSUEntity(com.opensymphony.user.Entity entity) {
        this.entity = entity;
    }

    public String getName() {
        return this.entity.getName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OSUEntity)) {
            return false;
        }
        OSUEntity other = (OSUEntity)o;
        return this.entity == null ? other.entity == null : this.entity.equals((Object)other.entity);
    }

    public int hashCode() {
        return this.entity == null ? 0 : this.entity.hashCode();
    }
}

