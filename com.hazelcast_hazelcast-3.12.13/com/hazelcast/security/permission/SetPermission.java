/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ListPermission;

public class SetPermission
extends ListPermission {
    public SetPermission(String name, String ... actions) {
        super(name, actions);
    }
}

