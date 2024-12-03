/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.config.PermissionConfig;
import java.util.Set;

public interface SecurityService {
    public void refreshClientPermissions(Set<PermissionConfig> var1);

    public Set<PermissionConfig> getClientPermissionConfigs();
}

