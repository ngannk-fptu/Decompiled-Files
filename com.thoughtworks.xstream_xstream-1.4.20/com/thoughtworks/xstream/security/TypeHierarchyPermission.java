/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.TypePermission;

public class TypeHierarchyPermission
implements TypePermission {
    private Class type;

    public TypeHierarchyPermission(Class type) {
        this.type = type;
    }

    public boolean allows(Class type) {
        if (type == null) {
            return false;
        }
        return this.type.isAssignableFrom(type);
    }
}

