/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.TypePermission;

public class AnyTypePermission
implements TypePermission {
    public static final TypePermission ANY = new AnyTypePermission();

    public boolean allows(Class type) {
        return true;
    }

    public int hashCode() {
        return 3;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == AnyTypePermission.class;
    }
}

