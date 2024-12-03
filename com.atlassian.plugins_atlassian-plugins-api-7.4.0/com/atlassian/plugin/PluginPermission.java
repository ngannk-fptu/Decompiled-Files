/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin;

import com.atlassian.plugin.InstallationMode;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class PluginPermission {
    public static final PluginPermission ALL = new PluginPermission("all_permissions");
    public static final PluginPermission EXECUTE_JAVA = new PluginPermission("execute_java");
    private final String name;
    private final InstallationMode installationMode;

    public PluginPermission(String name) {
        this(name, null);
    }

    public PluginPermission(String name, InstallationMode installationMode) {
        this.name = Objects.requireNonNull(name, "name");
        this.installationMode = installationMode;
    }

    public String getName() {
        return this.name;
    }

    @Nonnull
    public Optional<InstallationMode> getInstallationMode() {
        return Optional.ofNullable(this.installationMode);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginPermission that = (PluginPermission)o;
        return Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.name);
    }
}

