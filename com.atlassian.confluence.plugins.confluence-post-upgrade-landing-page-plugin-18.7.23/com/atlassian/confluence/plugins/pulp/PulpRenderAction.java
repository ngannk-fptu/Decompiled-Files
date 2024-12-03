/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.pulp;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.springframework.beans.factory.annotation.Qualifier;

public class PulpRenderAction
extends ConfluenceActionSupport {
    private final DarkFeaturesManager darkFeaturesManager;
    private final WebResourceAssembler webResourceAssembler;

    PulpRenderAction(@ComponentImport @Qualifier(value="darkFeaturesManager") DarkFeaturesManager darkFeaturesManager, PageBuilderService pageBuilderService) {
        this.darkFeaturesManager = darkFeaturesManager;
        this.webResourceAssembler = pageBuilderService.assembler();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.webResourceAssembler.resources().requireContext("pulpjs");
        return "success";
    }

    public boolean isPermitted() {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("pulp") && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }
}

