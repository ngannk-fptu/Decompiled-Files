/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.config.Config;
import com.hazelcast.config.PermissionConfig;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.Subject;

public interface IPermissionPolicy {
    public void configure(Config var1, Properties var2);

    public PermissionCollection getPermissions(Subject var1, Class<? extends Permission> var2);

    public void refreshPermissions(Set<PermissionConfig> var1);

    public void destroy();
}

