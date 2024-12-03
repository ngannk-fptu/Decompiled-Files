/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.rest;

import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.Utils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model.Settings;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.AuditingService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/settings")
@Produces(value={"application/json"})
public class SettingsResource {
    private static final String UPDATE_SETTINGS_ACTION_KEY = "audit.logging.collaborative.feedback.settings.updated";
    private final PermissionService permissionService;
    private final SettingsManager settingsManager;
    private final AuditingService auditingService;
    private final SynchronyConfigurationManager configManager;

    public SettingsResource(PermissionService permissionService, SettingsManager settingsManager, AuditingService auditingService, @ComponentImport SynchronyConfigurationManager configManager) {
        this.permissionService = permissionService;
        this.settingsManager = settingsManager;
        this.auditingService = auditingService;
        this.configManager = configManager;
    }

    @GET
    @Path(value="/collab")
    public Response collaborativeEditingStatus() {
        return Utils.executeAndRespond(() -> {
            this.permissionService.enforceSysAdmin((User)AuthenticatedUserThreadLocal.get());
            return Collections.singletonMap("sharedDraftsEnabled", this.configManager.isSharedDraftsEnabled());
        });
    }

    @GET
    public Response settings() {
        return Utils.executeAndRespond(() -> {
            this.permissionService.enforceSysAdmin((User)AuthenticatedUserThreadLocal.get());
            return this.buildSettings();
        });
    }

    @POST
    @Consumes(value={"application/json"})
    public Response updateSettings(Settings settings) {
        return Utils.executeAndRespond(() -> {
            this.permissionService.enforceSysAdmin((User)AuthenticatedUserThreadLocal.get());
            this.auditingService.audit(this.buildSettings(), settings, UPDATE_SETTINGS_ACTION_KEY);
            this.settingsManager.setEditorReportsEnabled(settings.isEditorReportsEnabled());
            return this.buildSettings();
        });
    }

    private Settings buildSettings() {
        return new Settings(this.settingsManager.getDestinationFolder().getAbsolutePath(), this.settingsManager.getMaxFiles(), this.settingsManager.getMaxConcurrentRequests(), this.settingsManager.getOperationTimeout(), this.settingsManager.isEditorReportsEnabled(), this.settingsManager.dataRetention());
    }
}

