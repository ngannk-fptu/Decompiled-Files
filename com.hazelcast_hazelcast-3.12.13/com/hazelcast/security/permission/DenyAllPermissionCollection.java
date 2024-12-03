/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;

public class DenyAllPermissionCollection
extends PermissionCollection {
    @Override
    public void add(Permission permission) {
    }

    @Override
    public boolean implies(Permission permission) {
        return false;
    }

    @Override
    public Enumeration<Permission> elements() {
        return new Enumeration<Permission>(){

            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public Permission nextElement() {
                return null;
            }
        };
    }

    public int hashCode() {
        return 37;
    }

    @Override
    public String toString() {
        return "<deny all permissions>";
    }

    public boolean equals(Object obj) {
        return obj instanceof DenyAllPermissionCollection;
    }
}

