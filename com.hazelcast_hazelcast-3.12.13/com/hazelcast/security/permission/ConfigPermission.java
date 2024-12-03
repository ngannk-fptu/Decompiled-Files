/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ClusterPermission;
import java.security.Permission;

public class ConfigPermission
extends ClusterPermission {
    private static final String CONFIG_PERMISSION_NAME = "<config>";
    private static final String CONFIG_PERMISSION_ACTIONS = "config";

    public ConfigPermission() {
        super(CONFIG_PERMISSION_NAME);
    }

    @Override
    public boolean implies(Permission permission) {
        return this.getClass() == permission.getClass();
    }

    @Override
    public String getActions() {
        return CONFIG_PERMISSION_ACTIONS;
    }
}

