/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dailysummary.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/admin")
public class AdminResource {
    private static final Logger log = LoggerFactory.getLogger(AdminResource.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private TransactionTemplate transactionTemplate;
    private final UserManager userManager;

    public AdminResource(@ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport(value="salUserManager") UserManager userManager) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.userManager = userManager;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getConfig(@Context AuthenticationContext context) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.userManager.isAdmin(currentUser != null ? currentUser.getKey() : null)) {
            return Response.status((int)403).type("application/json").build();
        }
        return Response.ok((Object)this.transactionTemplate.execute(() -> {
            PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
            return this.buildConfig(settings);
        })).type("application/json").build();
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setConfig(@Context AuthenticationContext context, DailySummaryAdminConfig config) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.userManager.isAdmin(currentUser != null ? currentUser.getKey() : null)) {
            return Response.status((int)403).type("application/json").build();
        }
        Response.Status status = (Response.Status)this.transactionTemplate.execute(() -> {
            try {
                PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
                this.setIfNotNull(settings, "atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule", config.getDefaultSchedule());
                this.setIfNotNull(settings, "atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled", config.getDefaultEnabled());
                return Response.Status.NO_CONTENT;
            }
            catch (Exception ex) {
                log.error("Could not set summary email admin settings", (Throwable)ex);
                return Response.Status.INTERNAL_SERVER_ERROR;
            }
        });
        return Response.status((Response.Status)status).type("application/json").build();
    }

    private void setIfNotNull(PluginSettings settings, String key, Object value) {
        if (value != null) {
            settings.put(key, (Object)value.toString());
        }
    }

    private DailySummaryAdminConfig buildConfig(PluginSettings settings) {
        DailySummaryAdminConfig config = new DailySummaryAdminConfig();
        config.setDefaultSchedule((String)settings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule"));
        String enabledStr = (String)settings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled");
        if (enabledStr != null) {
            config.setDefaultEnabled(Boolean.parseBoolean(enabledStr));
        }
        return config;
    }

    @XmlRootElement
    private static class DailySummaryAdminConfig {
        @XmlElement
        private String defaultSchedule;
        @XmlElement
        private Boolean defaultEnabled;

        private DailySummaryAdminConfig() {
        }

        public String getDefaultSchedule() {
            return this.defaultSchedule;
        }

        public void setDefaultSchedule(String defaultSchedule) {
            this.defaultSchedule = defaultSchedule;
        }

        public Boolean getDefaultEnabled() {
            return this.defaultEnabled;
        }

        public void setDefaultEnabled(Boolean enabled) {
            this.defaultEnabled = enabled;
        }
    }
}

