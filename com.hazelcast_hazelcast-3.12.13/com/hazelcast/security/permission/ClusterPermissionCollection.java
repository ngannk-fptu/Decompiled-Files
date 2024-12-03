/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ClusterPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClusterPermissionCollection
extends PermissionCollection {
    final Set<Permission> perms = new HashSet<Permission>();
    final Class<? extends Permission> permClass;

    public ClusterPermissionCollection() {
        this.permClass = null;
    }

    public ClusterPermissionCollection(Class<? extends Permission> permClass) {
        this.permClass = permClass;
    }

    @Override
    public void add(Permission permission) {
        boolean shouldAdd;
        if (this.isReadOnly()) {
            throw new SecurityException("ClusterPermissionCollection is read-only!");
        }
        boolean bl = shouldAdd = this.permClass != null && this.permClass.equals(permission.getClass()) || permission instanceof ClusterPermission;
        if (shouldAdd && !this.implies(permission)) {
            this.perms.add(permission);
        }
    }

    public void add(PermissionCollection permissions) {
        if (this.isReadOnly()) {
            throw new SecurityException("ClusterPermissionCollection is read-only!");
        }
        if (permissions instanceof ClusterPermissionCollection) {
            for (Permission p : ((ClusterPermissionCollection)permissions).perms) {
                this.add(p);
            }
        }
    }

    @Override
    public boolean implies(Permission permission) {
        for (Permission p : this.perms) {
            if (!p.implies(permission)) continue;
            return true;
        }
        return false;
    }

    public void compact() {
        if (this.isReadOnly()) {
            throw new SecurityException("ClusterPermissionCollection is read-only!");
        }
        Iterator<Permission> iter = this.perms.iterator();
        while (iter.hasNext()) {
            Permission perm = iter.next();
            boolean implies = false;
            for (Permission p : this.perms) {
                if (p == perm || !p.implies(perm)) continue;
                implies = true;
                break;
            }
            if (!implies) continue;
            iter.remove();
        }
        this.setReadOnly();
    }

    @Override
    public Enumeration<Permission> elements() {
        return Collections.enumeration(this.perms);
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(this.perms);
    }

    @Override
    public String toString() {
        return "ClusterPermissionCollection [permClass=" + this.permClass + "]";
    }
}

