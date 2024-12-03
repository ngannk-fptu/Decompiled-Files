/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.group.InternalGroup;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalGroupAttribute
extends InternalEntityAttribute {
    private Directory directory;
    private InternalGroup group;

    protected InternalGroupAttribute() {
    }

    public InternalGroupAttribute(Long id, InternalGroup group, String name, String value) {
        this(group, name, value);
        this.setId(id);
    }

    public InternalGroupAttribute(InternalGroup group, String name, String value) {
        super(name, value);
        this.group = group;
        this.directory = group.getDirectory();
    }

    public InternalGroup getGroup() {
        return this.group;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    private void setGroup(InternalGroup group) {
        this.group = group;
    }

    private void setDirectory(Directory directory) {
        this.directory = directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalGroupAttribute)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InternalGroupAttribute that = (InternalGroupAttribute)o;
        if (this.getDirectory().getId() != null ? !this.getDirectory().getId().equals(that.getDirectory().getId()) : that.getDirectory().getId() != null) {
            return false;
        }
        return !(this.getGroup().getId() != null ? !this.getGroup().getId().equals(that.getGroup().getId()) : that.getGroup().getId() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.getDirectory().getId() != null ? this.getDirectory().getId().hashCode() : 0);
        result = 31 * result + (this.getGroup().getId() != null ? this.getGroup().getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).append("directory", (Object)this.directory).append("group", (Object)this.group).toString();
    }
}

