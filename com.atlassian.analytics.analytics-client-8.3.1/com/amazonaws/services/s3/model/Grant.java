/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.Permission;
import java.io.Serializable;

public class Grant
implements Serializable {
    private Grantee grantee = null;
    private Permission permission = null;

    public Grant(Grantee grantee, Permission permission) {
        this.grantee = grantee;
        this.permission = permission;
    }

    public Grantee getGrantee() {
        return this.grantee;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.grantee == null ? 0 : this.grantee.hashCode());
        result = 31 * result + (this.permission == null ? 0 : this.permission.hashCode());
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
        Grant other = (Grant)obj;
        if (this.grantee == null ? other.grantee != null : !this.grantee.equals(other.grantee)) {
            return false;
        }
        return this.permission == other.permission;
    }

    public String toString() {
        return "Grant [grantee=" + this.grantee + ", permission=" + (Object)((Object)this.permission) + "]";
    }
}

