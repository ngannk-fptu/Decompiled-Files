/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.google.common.primitives.Longs
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.user.InternalUser;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalUserAttribute
extends InternalEntityAttribute {
    private Directory directory;
    private InternalUser user;
    private Long numericValue;

    protected InternalUserAttribute() {
    }

    public InternalUserAttribute(Long id, InternalUser user, String name, String value) {
        this(user, name, value);
        this.setId(id);
    }

    public InternalUserAttribute(InternalUser user, String name, String value) {
        super(name, value);
        this.user = user;
        this.directory = user.getDirectory();
        this.updateNumericValue();
    }

    public void updateNumericValue() {
        if (this.getValue() != null) {
            this.numericValue = this.toNumericValue(this.getValue());
        }
    }

    private Long toNumericValue(String value) {
        return Longs.tryParse((String)value);
    }

    public Long getNumericValue() {
        return this.numericValue;
    }

    public InternalUser getUser() {
        return this.user;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    private void setUser(InternalUser user) {
        this.user = user;
    }

    private void setDirectory(Directory directory) {
        this.directory = directory;
    }

    private void setNumericValue(Long numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.updateNumericValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalUserAttribute)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InternalUserAttribute that = (InternalUserAttribute)o;
        if (this.getDirectory().getId() != null ? !this.getDirectory().getId().equals(that.getDirectory().getId()) : that.getDirectory().getId() != null) {
            return false;
        }
        return !(this.getUser().getId() != null ? !this.getUser().getId().equals(that.getUser().getId()) : that.getUser().getId() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.getDirectory().getId() != null ? this.getDirectory().getId().hashCode() : 0);
        result = 31 * result + (this.getUser().getId() != null ? this.getUser().getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).append("directory", (Object)this.directory).append("user", (Object)this.user).toString();
    }
}

