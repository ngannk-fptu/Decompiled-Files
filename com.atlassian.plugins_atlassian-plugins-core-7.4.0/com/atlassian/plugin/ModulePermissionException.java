/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ModulePermissionException
extends PluginException {
    private final String moduleKey;
    private final Set<String> permissions;

    public ModulePermissionException(String moduleKey, Set<String> permissions) {
        super("Could not load module " + moduleKey + ". The plugin is missing the following permissions: " + permissions);
        this.moduleKey = Objects.requireNonNull(moduleKey);
        this.permissions = Collections.unmodifiableSet(new HashSet(Objects.requireNonNull(permissions)));
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }
}

