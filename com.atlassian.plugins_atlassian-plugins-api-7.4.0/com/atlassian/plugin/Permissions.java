/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginPermission;
import com.atlassian.plugin.RequirePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Permissions {
    public static final String ALL_PERMISSIONS = "all_permissions";
    public static final String EXECUTE_JAVA = "execute_java";
    public static final String CREATE_SYSTEM_MODULES = "create_system_modules";
    public static final String GENERATE_ANY_HTML = "generate_any_html";

    private Permissions() {
    }

    public static Set<String> getRequiredPermissions(Class<?> type) {
        Class<RequirePermission> annotation = RequirePermission.class;
        if (type != null && type.isAnnotationPresent(annotation)) {
            return Collections.unmodifiableSet(Arrays.stream(type.getAnnotation(annotation).value()).collect(Collectors.toSet()));
        }
        return Collections.emptySet();
    }

    public static Plugin addPermission(Plugin plugin, String permission, InstallationMode mode) {
        Objects.requireNonNull(plugin);
        HashSet<PluginPermission> permissions = new HashSet<PluginPermission>(Permissions.getPluginInformation(plugin).getPermissions());
        permissions.add(new PluginPermission(permission, mode));
        Permissions.getPluginInformation(plugin).setPermissions(permissions);
        return plugin;
    }

    private static PluginInformation getPluginInformation(Plugin plugin) {
        return Optional.ofNullable(plugin.getPluginInformation()).orElseGet(PluginInformation::new);
    }
}

