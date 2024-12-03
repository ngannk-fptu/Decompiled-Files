/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.rest.monitor.representations;

import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import com.atlassian.upm.rest.monitor.representations.MonitorRepresentationFactory;
import com.atlassian.upm.rest.monitor.representations.PluginStateCollectionRep;
import com.atlassian.upm.rest.monitor.representations.PluginStateRep;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MonitorRepresentationFactoryImpl
implements MonitorRepresentationFactory {
    private final PermissionService permissionService;

    public MonitorRepresentationFactoryImpl(PermissionService permissionService) {
        this.permissionService = Objects.requireNonNull(permissionService, "permissionService");
    }

    @Override
    public PluginStateRep createPluginStateRep(Plugin plugin) {
        boolean adminCanDisable = !this.permissionService.getPermissionError(UserAttributes.ADMIN_USER, Permission.MANAGE_PLUGIN_ENABLEMENT, plugin).isDefined();
        return new PluginStateRep(plugin.getKey(), plugin.getName(), plugin.getVersion(), plugin.isEnabled(), adminCanDisable, plugin.isConnect());
    }

    @Override
    public PluginStateCollectionRep createPluginStateCollectionRep(Iterable<Plugin> plugins) {
        return new PluginStateCollectionRep(Collections.unmodifiableList(StreamSupport.stream(plugins.spliterator(), false).map(this::createPluginStateRep).collect(Collectors.toList())));
    }
}

