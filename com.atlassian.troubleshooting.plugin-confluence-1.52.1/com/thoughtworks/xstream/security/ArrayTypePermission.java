/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.TypePermission;

public class ArrayTypePermission
implements TypePermission {
    public static final TypePermission ARRAYS = new ArrayTypePermission();

    public boolean allows(Class type) {
        return type != null && type.isArray();
    }

    public int hashCode() {
        return 13;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == ArrayTypePermission.class;
    }
}

