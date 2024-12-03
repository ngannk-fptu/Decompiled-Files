/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextEnvironment
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private boolean override = true;
    private String value = null;

    public boolean getOverride() {
        return this.override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextEnvironment[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.getDescription() != null) {
            sb.append(", description=");
            sb.append(this.getDescription());
        }
        if (this.getType() != null) {
            sb.append(", type=");
            sb.append(this.getType());
        }
        if (this.value != null) {
            sb.append(", value=");
            sb.append(this.value);
        }
        sb.append(", override=");
        sb.append(this.override);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.override ? 1231 : 1237);
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ContextEnvironment other = (ContextEnvironment)obj;
        if (this.override != other.override) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }
}

