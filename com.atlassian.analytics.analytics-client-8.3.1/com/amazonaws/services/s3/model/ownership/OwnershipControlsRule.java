/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.ownership;

import com.amazonaws.services.s3.model.ownership.ObjectOwnership;
import java.io.Serializable;

public class OwnershipControlsRule
implements Serializable {
    private String objectOwnership;

    public String getOwnership() {
        return this.objectOwnership;
    }

    public void setOwnership(String objectOwnership) {
        this.objectOwnership = objectOwnership;
    }

    public void setOwnership(ObjectOwnership objectOwnership) {
        this.objectOwnership = objectOwnership.toString();
    }

    public OwnershipControlsRule withOwnership(String objectOwnership) {
        this.setOwnership(objectOwnership);
        return this;
    }

    public OwnershipControlsRule withOwnership(ObjectOwnership objectOwnership) {
        this.setOwnership(objectOwnership);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OwnershipControlsRule that = (OwnershipControlsRule)o;
        return this.objectOwnership != null ? this.objectOwnership.equals(that.objectOwnership) : that.objectOwnership == null;
    }

    public int hashCode() {
        return this.objectOwnership != null ? this.objectOwnership.hashCode() : 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getOwnership() != null) {
            sb.append("Ownership: ").append(this.getOwnership()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}

