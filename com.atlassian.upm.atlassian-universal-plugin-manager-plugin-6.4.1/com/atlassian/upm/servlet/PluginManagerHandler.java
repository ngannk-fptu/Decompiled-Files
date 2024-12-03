/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.upm.servlet;

import com.atlassian.marketplace.client.model.Links;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.servlet.UpmServletHandler;
import com.atlassian.upm.license.impl.LicensedPlugins;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.pac.PacClient;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

public final class PluginManagerHandler
extends UpmServletHandler {
    public static final String SOURCE_PARAM = "source";
    public static final String SOURCE_TYPE_PARAM = "source-type";
    private final PermissionEnforcer permissionEnforcer;
    private final UpmInformation upm;
    private final SysPersisted sysPersisted;
    private final UpmHostApplicationInformation appInfo;
    private final UpmAppManager appManager;
    private final HostApplicationDescriptor hostApplicationDescriptor;
    private final HostLicenseProvider hostLicenseProvider;
    private final PacClient pacClient;
    private final ApplicationProperties applicationProperties;

    public PluginManagerHandler(TemplateRenderer renderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager, UpmInformation upm, SysPersisted sysPersisted, UpmHostApplicationInformation appInfo, UpmAppManager appManager, HostApplicationDescriptor hostApplicationDescriptor, HostLicenseProvider hostLicenseProvider, PacClient pacClient, ApplicationProperties applicationProperties) {
        super(renderer, permissionEnforcer, loginUriProvider, webSudoManager);
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
        this.appManager = Objects.requireNonNull(appManager, "appManager");
        this.hostApplicationDescriptor = Objects.requireNonNull(hostApplicationDescriptor, "hostApplicationDescriptor");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    @Override
    public Map<String, Object> getContext(HttpServletRequest request) {
        HashMap<String, Object> contextBuilder = new HashMap<String, Object>();
        contextBuilder.put("pacWebsiteUrl", UpmSys.getMpacWebsiteBaseUrl());
        contextBuilder.put("upmVersion", this.upm.getVersion());
        contextBuilder.put("macBaseurl", UpmSys.getMacBaseUrl());
        contextBuilder.put("isOnDemand", false);
        contextBuilder.put("isApplicationApiSupported", this.appManager.isApplicationSupportEnabled());
        contextBuilder.put("licensedHostUsers", this.getLicensedHostUsers());
        contextBuilder.put("isPlatformFreeTier", false);
        contextBuilder.put("hostDataCenter", this.appInfo.isHostDataCenterEnabled());
        contextBuilder.put("isCareBearServerSpecific", LicensedPlugins.isServerWithCloudAlternative(this.appInfo.getHostingType(), this.applicationProperties.getPlatformId()));
        UpmHostApplicationInformation.AuiCapabilities ac = this.appInfo.getAuiCapabilities();
        if (ac != null) {
            contextBuilder.put("auiCapabilities", ac);
        }
        for (Links mpacLinks : this.pacClient.getMarketplaceRootLinks()) {
            for (URI uri : mpacLinks.getUri("addons")) {
                contextBuilder.put("mpacAddonsUrl", Sys.resolveMarketplaceUri(uri));
            }
        }
        return Collections.unmodifiableMap(contextBuilder);
    }

    private int getLicensedHostUsers() {
        HostApplicationLicenseAttributes attrs = this.hostLicenseProvider.getHostApplicationLicenseAttributes();
        if (!attrs.isEvaluation()) {
            return attrs.getEdition().getOrElse(-1);
        }
        return 0;
    }
}

