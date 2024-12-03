/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.OwnerOverride;
import java.io.Serializable;

public class AccessControlTranslation
implements Serializable,
Cloneable {
    private String owner;

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public AccessControlTranslation withOwner(String owner) {
        this.setOwner(owner);
        return this;
    }

    public AccessControlTranslation withOwner(OwnerOverride owner) {
        this.setOwner(owner == null ? null : owner.toString());
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getOwner() != null) {
            sb.append("Owner: ").append(this.getOwner()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AccessControlTranslation)) {
            return false;
        }
        AccessControlTranslation other = (AccessControlTranslation)obj;
        if (other.getOwner() == null ^ this.getOwner() == null) {
            return false;
        }
        return other.getOwner() == null || other.getOwner().equals(this.getOwner());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getOwner() == null ? 0 : this.getOwner().hashCode());
        return hashCode;
    }

    public AccessControlTranslation clone() {
        try {
            return (AccessControlTranslation)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

