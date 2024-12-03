/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.security.TypePermission;

public class NullPermission
implements TypePermission {
    public static final TypePermission NULL = new NullPermission();

    public boolean allows(Class type) {
        return type == null || type == Mapper.Null.class;
    }
}

