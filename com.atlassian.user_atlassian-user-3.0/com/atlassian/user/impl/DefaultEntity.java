/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.Entity;
import java.util.Date;

public abstract class DefaultEntity
implements Entity {
    protected String name;
    protected transient Date created;

    protected DefaultEntity() {
        this.created = new Date();
    }

    public DefaultEntity(String name) {
        this.name = name;
        this.created = new Date();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    protected DefaultEntity(String name, long id, Date created) {
        this.name = name;
        this.created = created;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultEntity)) {
            return false;
        }
        DefaultEntity defaultEntity = (DefaultEntity)o;
        return !(this.name != null ? !this.name.equals(defaultEntity.name) : defaultEntity.name != null);
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}

