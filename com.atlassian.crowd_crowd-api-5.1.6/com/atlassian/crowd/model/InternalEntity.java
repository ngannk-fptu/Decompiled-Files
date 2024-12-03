/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.TimestampedEntity
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.model.TimestampedEntity;
import com.atlassian.crowd.util.InternalEntityUtils;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.Validate;

@Deprecated
public abstract class InternalEntity
implements Serializable,
TimestampedEntity {
    protected Long id;
    protected String name;
    protected boolean active;
    protected Date createdDate;
    protected Date updatedDate;

    protected InternalEntity() {
        this.id = null;
        this.createdDate = null;
        this.updatedDate = null;
        this.active = true;
    }

    protected InternalEntity(InternalEntityTemplate template) {
        this.setId(template.getId());
        this.setName(template.getName());
        this.setActive(template.isActive());
        this.setCreatedDate(template.getCreatedDate());
        this.setUpdatedDate(template.getUpdatedDate());
    }

    public void setUpdatedDateToNow() {
        this.updatedDate = new Date();
    }

    public void setCreatedDateToNow() {
        this.createdDate = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        return this.active;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    private void setId(Long id) {
        this.id = id;
    }

    protected void setName(String name) {
        Validate.notNull((Object)name);
        InternalEntityUtils.validateLength(name);
        this.name = name;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public abstract int hashCode();

    public abstract boolean equals(Object var1);
}

