/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model;

import java.util.Date;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalEntityTemplate {
    private Long id;
    private String name;
    private boolean active;
    private Date createdDate;
    private Date updatedDate;

    public InternalEntityTemplate() {
    }

    public InternalEntityTemplate(Long id, String name, boolean active, Date createdDate, Date updatedDate) {
        this.setId(id);
        this.setName(name);
        this.setActive(active);
        this.setCreatedDate(createdDate);
        this.setUpdatedDate(updatedDate);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        Validate.notNull((Object)name);
        this.name = name;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalEntityTemplate that = (InternalEntityTemplate)o;
        if (this.active != that.active) {
            return false;
        }
        if (this.createdDate != null ? !this.createdDate.equals(that.createdDate) : that.createdDate != null) {
            return false;
        }
        if (this.id != null ? !this.id.equals(that.id) : that.id != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.updatedDate != null ? !this.updatedDate.equals(that.updatedDate) : that.updatedDate != null);
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.active ? 1 : 0);
        result = 31 * result + (this.createdDate != null ? this.createdDate.hashCode() : 0);
        result = 31 * result + (this.updatedDate != null ? this.updatedDate.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.id).append("name", (Object)this.name).append("active", this.active).append("createdDate", (Object)this.createdDate).append("updatedDate", (Object)this.updatedDate).toString();
    }

    public String toFriendlyString() {
        return "Username: " + this.name + ", Created Date: " + this.createdDate + ", Updated Date: " + this.updatedDate;
    }
}

