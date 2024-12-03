/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.upm.permission;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.permission.DefaultPermissionService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import java.util.Objects;

public class UpmPermissionService
extends DefaultPermissionService {
    private final SysPersisted sysPersisted;

    public UpmPermissionService(PluginMetadataAccessor metadata, SysPersisted sysPersisted, ApplicationProperties applicationProperties, EventPublisher eventPublisher, UpmPluginAccessor pluginAccessor) {
        super(metadata, applicationProperties, eventPublisher, pluginAccessor);
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
    }

    @Override
    public Option<PermissionService.PermissionError> getPermissionError(UserAttributes userAttributes, Permission permission) {
        if (userAttributes == null) {
            return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
        }
        switch (permission) {
            case GET_SAFE_MODE: 
            case GET_SETTINGS: 
            case GET_PLUGIN_LICENSE: 
            case GET_POST_INSTALL_POST_UPDATE_PAGE: 
            case DISABLE_ALL_USER_INSTALLED: 
            case GET_NOTIFICATIONS: 
            case MANAGE_NOTIFICATIONS: 
            case MANAGE_SAFE_MODE: 
            case GET_OSGI_STATE: {
                return this.adminOrSysadmin(userAttributes);
            }
            case GET_AVAILABLE_PLUGINS: {
                return Option.none(PermissionService.PermissionError.class);
            }
            case CREATE_PLUGIN_REQUEST: {
                if (!this.getPermissionError(userAttributes, Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI).isDefined()) {
                    return Option.some(PermissionService.PermissionError.UNAUTHORIZED);
                }
                if (this.pluginRequestsAreDisabled()) {
                    return Option.some(PermissionService.PermissionError.CONFLICT);
                }
                return Option.none(PermissionService.PermissionError.class);
            }
            case REQUEST_PLUGIN_UPDATE: {
                return Sys.isIncompatiblePluginCheckEnabled() ? this.sysadminOnly(userAttributes) : Option.some(PermissionService.PermissionError.FORBIDDEN);
            }
            case GET_PURCHASED_PLUGINS: 
            case MONITOR_PLUGINS_STATE: 
            case GET_PRODUCT_UPDATE_COMPATIBILITY: 
            case MANAGE_PLUGIN_REQUESTS: 
            case MANAGE_ON_PREMISE_SETTINGS: {
                return this.sysadminOnly(userAttributes);
            }
            case GET_PLUGIN_REQUESTS: {
                if (this.pluginRequestsAreDisabled()) {
                    return Option.some(PermissionService.PermissionError.CONFLICT);
                }
                return this.sysadminOnly(userAttributes);
            }
            case GET_USER_SETTINGS: 
            case MANAGE_USER_SETTINGS: {
                return this.nonSysadminOnly(userAttributes);
            }
            case ADD_ANALYTICS_ACTIVITY: {
                return this.sysPersisted.is(UpmSettings.PAC_DISABLED) ? Option.some(PermissionService.PermissionError.CONFLICT) : Option.none(PermissionService.PermissionError.class);
            }
        }
        return super.getPermissionError(userAttributes, permission);
    }

    private boolean pluginRequestsAreDisabled() {
        return this.sysPersisted.is(UpmSettings.REQUESTS_DISABLED) || this.sysPersisted.is(UpmSettings.PAC_DISABLED);
    }
}

