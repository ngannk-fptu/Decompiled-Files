/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.internal.S3RequesterChargedResult;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AccessControlList
implements Serializable,
S3RequesterChargedResult {
    private static final long serialVersionUID = 8095040648034788376L;
    private Set<Grant> grantSet;
    private List<Grant> grantList;
    private Owner owner = null;
    private boolean isRequesterCharged;

    public Owner getOwner() {
        return this.owner;
    }

    public AccessControlList withOwner(Owner owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void grantPermission(Grantee grantee, Permission permission) {
        this.getGrantsAsList().add(new Grant(grantee, permission));
    }

    public void grantAllPermissions(Grant ... grantsVarArg) {
        for (Grant gap : grantsVarArg) {
            this.grantPermission(gap.getGrantee(), gap.getPermission());
        }
    }

    public void revokeAllPermissions(Grantee grantee) {
        ArrayList<Grant> grantsToRemove = new ArrayList<Grant>();
        List<Grant> existingGrants = this.getGrantsAsList();
        for (Grant gap : existingGrants) {
            if (!gap.getGrantee().equals(grantee)) continue;
            grantsToRemove.add(gap);
        }
        this.grantList.removeAll(grantsToRemove);
    }

    @Deprecated
    public Set<Grant> getGrants() {
        this.checkState();
        if (this.grantSet == null) {
            if (this.grantList == null) {
                this.grantSet = new HashSet<Grant>();
            } else {
                this.grantSet = new HashSet<Grant>(this.grantList);
                this.grantList = null;
            }
        }
        return this.grantSet;
    }

    private void checkState() {
        if (this.grantSet != null && this.grantList != null) {
            throw new IllegalStateException("Both grant set and grant list cannot be null");
        }
    }

    public List<Grant> getGrantsAsList() {
        this.checkState();
        if (this.grantList == null) {
            if (this.grantSet == null) {
                this.grantList = new LinkedList<Grant>();
            } else {
                this.grantList = new LinkedList<Grant>(this.grantSet);
                this.grantSet = null;
            }
        }
        return this.grantList;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.owner == null ? 0 : this.owner.hashCode());
        result = 31 * result + (this.grantSet == null ? 0 : this.grantSet.hashCode());
        result = 31 * result + (this.grantList == null ? 0 : this.grantList.hashCode());
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
        AccessControlList other = (AccessControlList)obj;
        if (this.owner == null ? other.owner != null : !this.owner.equals(other.owner)) {
            return false;
        }
        if (this.grantSet == null ? other.grantSet != null : !this.grantSet.equals(other.grantSet)) {
            return false;
        }
        return !(this.grantList == null ? other.grantList != null : !this.grantList.equals(other.grantList));
    }

    public String toString() {
        return "AccessControlList [owner=" + this.owner + ", grants=" + this.getGrantsAsList() + "]";
    }

    @Override
    public boolean isRequesterCharged() {
        return this.isRequesterCharged;
    }

    @Override
    public void setRequesterCharged(boolean isRequesterCharged) {
        this.isRequesterCharged = isRequesterCharged;
    }
}

