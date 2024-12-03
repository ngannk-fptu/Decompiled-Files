/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;

public class PolicyStatus
implements Serializable,
Cloneable {
    private Boolean isPublic;

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public PolicyStatus withIsPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PolicyStatus that = (PolicyStatus)o;
        return this.isPublic != null ? this.isPublic.equals(that.isPublic) : that.isPublic == null;
    }

    public int hashCode() {
        return this.isPublic != null ? this.isPublic.hashCode() : 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getIsPublic() != null) {
            sb.append("IsPublic: ").append(this.getIsPublic()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public PolicyStatus clone() {
        try {
            return (PolicyStatus)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

