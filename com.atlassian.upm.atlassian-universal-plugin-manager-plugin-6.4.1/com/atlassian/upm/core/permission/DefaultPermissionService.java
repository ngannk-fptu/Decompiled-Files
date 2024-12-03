/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.core.permission;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultPermissionService
implements PermissionService,
InitializingBean,
DisposableBean {
    private final PluginMetadataAccessor metadata;
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;
    private final UpmPluginAccessor pluginAccessor;
    private boolean connectPluginAvailable = false;
    private static Set<String> SYSADMIN_ONLY_MODULES = ImmutableSet.of((Object)"confluence.macros.html:html", (Object)"confluence.macros.html:html-xhtml", (Object)"confluence.macros.html:html-include", (Object)"confluence.macros.html:html-include-xhtml");
    @Deprecated
    static final String CONFLUENCE_MACROS_HTML = "confluence.macros.html:html";
    @Deprecated
    static final String CONFLUENCE_MACROS_HTML_INCLUDE = "confluence.macros.html:html-include";

    public DefaultPermissionService(PluginMetadataAccessor metadata, ApplicationProperties applicationProperties, EventPublisher eventPublisher, UpmPluginAccessor pluginAccessor) {
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
    }

    @Override
    public Option<PermissionService.PermissionError> getPermissionError(UserAttributes userAttributes, Permission permission) {
        if (userAttributes == null) {
            return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
        }
        switch (permission) {
            case GET_PLUGIN_MODULES: 
            case GET_INSTALLED_PLUGINS: 
            case GET_AUDIT_LOG: {
                return this.adminOrSysadmin(userAttributes);
            }
            case MANAGE_PLUGIN_ENABLEMENT: 
            case MANAGE_PLUGIN_MODULE_ENABLEMENT: {
                return this.adminOrSysadmin(userAttributes);
            }
            case MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_FILE: {
                return this.sysadminOnly(userAttributes);
            }
            case MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI: {
                return this.sysadminOnly(userAttributes);
            }
            case MANAGE_PLUGIN_UNINSTALL: 
            case MANAGE_AUDIT_LOG: {
                return this.sysadminOnly(userAttributes);
            }
            case MANAGE_PLUGIN_LICENSE: {
                return this.adminOrSysadmin(userAttributes);
            }
            case GET_APPLICATIONS: 
            case MANAGE_APPLICATION_CONFIG: 
            case MANAGE_APPLICATION_LICENSES: {
                return this.sysadminOnly(userAttributes);
            }
            case SCAN_PLUGIN_DIRECTORY: {
                return this.sysadminOnly(userAttributes);
            }
        }
        throw new IllegalArgumentException("Unhandled permission: " + (Object)((Object)permission));
    }

    @Override
    public Option<PermissionService.PermissionError> getPermissionError(UserAttributes userAttributes, Permission permission, Plugin plugin) {
        return this.getPermissionError(userAttributes, permission);
    }

    @Override
    public Option<PermissionService.PermissionError> getPermissionError(UserAttributes userAttributes, Permission permission, Plugin.Module module) {
        switch (permission) {
            case MANAGE_PLUGIN_MODULE_ENABLEMENT: {
                if (!module.getPlugin().isEnabled() || module.getPlugin().isUpmPlugin()) {
                    return Option.some(PermissionService.PermissionError.CONFLICT);
                }
                if (SYSADMIN_ONLY_MODULES.contains(module.getCompleteKey())) {
                    return this.sysadminOnly(userAttributes);
                }
                return this.getPermissionError(userAttributes, permission, module.getPlugin());
            }
            case MANAGE_PLUGIN_ENABLEMENT: {
                return this.getPermissionError(userAttributes, permission, module.getPlugin());
            }
        }
        return this.getPermissionError(userAttributes, permission);
    }

    @Override
    public Option<PermissionService.PermissionError> getInProcessInstallationFromUriPermissionError(UserAttributes userAttributes, URI uri) {
        return this.sysadminOnly(userAttributes);
    }

    protected Option<PermissionService.PermissionError> adminOrSysadmin(UserAttributes userAttributes) {
        if (userAttributes.isSystemAdmin() || userAttributes.isAdmin()) {
            return Option.none(PermissionService.PermissionError.class);
        }
        return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
    }

    protected Option<PermissionService.PermissionError> sysadminOnly(UserAttributes userAttributes) {
        if (userAttributes.isSystemAdmin()) {
            return Option.none(PermissionService.PermissionError.class);
        }
        return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
    }

    protected Option<PermissionService.PermissionError> nonSysadminOnly(UserAttributes userAttributes) {
        if (userAttributes.isSystemAdmin()) {
            return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
        }
        return Option.none(PermissionService.PermissionError.class);
    }

    protected Option<PermissionService.PermissionError> inApplication(String ... apps) {
        String currentApp = this.applicationProperties.getDisplayName();
        for (String app : apps) {
            if (!currentApp.equalsIgnoreCase(app)) continue;
            return Option.none(PermissionService.PermissionError.class);
        }
        return Option.some(PermissionService.PermissionError.FORBIDDEN);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
        this.connectPluginAvailable = this.pluginAccessor.isPluginEnabled("com.atlassian.plugins.atlassian-connect-plugin");
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent ev) {
        if ("com.atlassian.plugins.atlassian-connect-plugin".equals(ev.getPlugin().getKey())) {
            this.connectPluginAvailable = true;
        }
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent ev) {
        if ("com.atlassian.plugins.atlassian-connect-plugin".equals(ev.getPlugin().getKey())) {
            this.connectPluginAvailable = false;
        }
    }
}

