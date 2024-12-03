/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.TypePermission;

public class NoPermission
implements TypePermission {
    private final TypePermission permission;

    public NoPermission(TypePermission permission) {
        this.permission = permission;
    }

    public boolean allows(Class type) {
        if (this.permission == null || this.permission.allows(type)) {
            throw new ForbiddenClassException(type);
        }
        return false;
    }
}

