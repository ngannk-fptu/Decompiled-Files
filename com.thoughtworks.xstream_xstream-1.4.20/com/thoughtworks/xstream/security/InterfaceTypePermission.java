/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.TypePermission;

public class InterfaceTypePermission
implements TypePermission {
    public static final TypePermission INTERFACES = new InterfaceTypePermission();

    public boolean allows(Class type) {
        return type != null && type.isInterface();
    }

    public int hashCode() {
        return 31;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == InterfaceTypePermission.class;
    }
}

