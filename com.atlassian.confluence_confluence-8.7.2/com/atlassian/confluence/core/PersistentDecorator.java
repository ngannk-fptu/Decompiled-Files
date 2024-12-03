/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import java.io.Serializable;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PersistentDecorator
implements Serializable {
    private long id;
    private String spaceKey;
    private String name;
    private String body;
    private Date lastModificationDate;

    public PersistentDecorator() {
        this.lastModificationDate = new Date();
    }

    public PersistentDecorator(@Nullable String spaceKey, String name, String body, Date lastModificationDate) {
        this.spaceKey = spaceKey;
        this.name = name;
        this.body = body;
        this.lastModificationDate = lastModificationDate;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
        this.lastModificationDate = new Date();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PersistentDecorator that = (PersistentDecorator)o;
        if (this.id != that.id) {
            return false;
        }
        if (this.body == null && that.body != null) {
            return false;
        }
        if (this.body != null && !this.body.equals(that.body)) {
            return false;
        }
        if (this.name == null && that.name != null) {
            return false;
        }
        if (this.name != null && !this.name.equals(that.name)) {
            return false;
        }
        if (this.spaceKey == null && that.spaceKey != null) {
            return false;
        }
        if (this.spaceKey != null && !this.spaceKey.equals(that.spaceKey)) {
            return false;
        }
        if (this.lastModificationDate == null && that.lastModificationDate != null) {
            return false;
        }
        return this.lastModificationDate == null || this.lastModificationDate.equals(that.getLastModificationDate());
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 29 * result + (this.spaceKey != null ? this.spaceKey.hashCode() : 0);
        result = 29 * result + this.name.hashCode();
        result = 29 * result + (this.body != null ? this.body.hashCode() : 0);
        result = 29 * result + (this.lastModificationDate != null ? this.lastModificationDate.hashCode() : 0);
        return result;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}

