/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.GroupConversionDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import javax.validation.metadata.GroupConversionDescriptor;

public class GroupConversionDescriptorImpl
implements GroupConversionDescriptor {
    private final Class<?> from;
    private final Class<?> to;

    public GroupConversionDescriptorImpl(Class<?> from, Class<?> to) {
        this.from = from;
        this.to = to;
    }

    public Class<?> getFrom() {
        return this.from;
    }

    public Class<?> getTo() {
        return this.to;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.from == null ? 0 : this.from.hashCode());
        result = 31 * result + (this.to == null ? 0 : this.to.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        GroupConversionDescriptorImpl other = (GroupConversionDescriptorImpl)obj;
        if (this.from == null ? other.from != null : !this.from.equals(other.from)) {
            return false;
        }
        return !(this.to == null ? other.to != null : !this.to.equals(other.to));
    }

    public String toString() {
        return "GroupConversionDescriptorImpl [from=" + this.from + ", to=" + this.to + "]";
    }
}

