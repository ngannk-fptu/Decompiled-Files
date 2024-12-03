/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ClusterPermissionCollection;
import java.security.Permission;
import java.security.PermissionCollection;

public abstract class ClusterPermission
extends Permission {
    private int hashcode;

    public ClusterPermission(String name) {
        super(name);
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new ClusterPermissionCollection(this.getClass());
    }

    @Override
    public int hashCode() {
        if (this.hashcode == 0) {
            int prime = 31;
            int result = 1;
            result = this.getName() == null ? 31 * result + 13 : 31 * result + this.getName().hashCode();
            this.hashcode = result;
        }
        return this.hashcode;
    }

    @Override
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
        ClusterPermission other = (ClusterPermission)obj;
        if (this.getName() == null && other.getName() != null) {
            return false;
        }
        return this.getName().equals(other.getName());
    }
}

