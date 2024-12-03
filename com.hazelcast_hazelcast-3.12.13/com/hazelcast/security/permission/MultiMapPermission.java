/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.MapPermission;

public class MultiMapPermission
extends MapPermission {
    public MultiMapPermission(String name, String ... actions) {
        super(name, actions);
    }
}

