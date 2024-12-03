/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService
 *  com.atlassian.user.User
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.BaseJsonNode
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.model.modification.Modification;
import com.atlassian.confluence.plugins.gatekeeper.model.modification.ModificationResult;
import com.atlassian.confluence.plugins.gatekeeper.modifier.Modifier;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.ActionUtil;
import com.atlassian.confluence.plugins.gatekeeper.util.FormValidator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import com.atlassian.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class EditPermissionsAction
extends ConfluenceActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(EditPermissionsAction.class);
    private boolean licensed;
    private String pluginKey;
    private String anonymous;
    private String groupname;
    private String username;
    private int permissions;
    private String spaceKeys;
    private String type;
    private AddonLicenseManager licenseManager;
    private ConfluenceService confluenceService;
    private AccessModeService accessModeService;

    private boolean checkLicense() {
        this.licensed = this.licenseManager.getLicenseInfo().isValid() && this.licenseManager.getLicenseInfo().isDCFeatureLicensed();
        return this.licensed;
    }

    public boolean isPermitted() {
        return this.checkLicense() && super.isPermitted();
    }

    public String modifyPermissions() throws IOException {
        Modification modification;
        if (!this.checkLicense()) {
            return "none";
        }
        boolean readOnlyMode = this.accessModeService.isReadOnlyAccessModeEnabled();
        boolean isSysAdmin = this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
        if (readOnlyMode && !isSysAdmin) {
            ActionUtil.sendForbiddenResponse("Read only mode is active, System administrator privilege is required!");
            return "none";
        }
        if (StringUtils.isBlank((CharSequence)this.spaceKeys)) {
            FormValidator formValidator = new FormValidator();
            formValidator.addError("space-list", "Please select at least one space!");
            ActionUtil.sendJsonResponse((BaseJsonNode)formValidator.toJson());
            return "none";
        }
        String path = ServletActionContext.getRequest().getServletPath();
        boolean isSpaceTools = path.startsWith("/plugins/");
        if (isSpaceTools) {
            if (this.spaceKeys.indexOf(44) >= 0) {
                ActionUtil.sendBadResponse("Only single space can be modified in Space Tools!");
                return "none";
            }
            if ("group".equals(this.type)) {
                ActionUtil.sendBadResponse("Group permissions can not be modified in Space Tools!");
                return "none";
            }
        }
        logger.debug("savePermissions called user: {}, anonymous: {}", (Object)this.username, (Object)this.anonymous);
        Modifier modifier = new Modifier(this.confluenceService, this.getI18n());
        ModificationResult modificationResult = null;
        if ("group".equals(this.type) && this.groupname != null && !this.groupname.isEmpty()) {
            modification = new Modification();
            modification.setGroupPermissions(this.groupname, this.permissions);
            modification.setSpaces(this.spaceKeys);
            modificationResult = modifier.setPermissions(modification);
        } else if ("user".equals(this.type)) {
            if ("true".equals(this.anonymous)) {
                logger.debug("showPermissionExplanations anonymous");
                modification = new Modification();
                modification.setAnonymousPermissions(this.permissions);
                modification.setSpaces(this.spaceKeys);
                modificationResult = modifier.setPermissions(modification);
            } else if (this.username != null && !this.username.isEmpty()) {
                logger.debug("showPermissionExplanations user");
                modification = new Modification();
                modification.setUserPermissions(this.username, this.permissions);
                modification.setSpaces(this.spaceKeys);
                modificationResult = modifier.setPermissions(modification);
            }
        }
        if (modificationResult != null) {
            ObjectMapper om = new ObjectMapper();
            String result = om.writeValueAsString(modificationResult);
            ActionUtil.sendJsonResponse(result);
        } else {
            ActionUtil.sendBadResponse();
        }
        return "none";
    }

    public void setPluginRetrievalService(PluginRetrievalService pluginRetrievalService) {
        this.pluginKey = pluginRetrievalService.getPlugin().getKey();
    }

    public void setLicenseManager(AddonLicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public void setAccessModeService(AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLicensed() {
        return this.licensed;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getSpaceKeys() {
        return this.spaceKeys;
    }

    public void setSpaceKeys(String spaceKeys) {
        this.spaceKeys = spaceKeys;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConfluenceService(ConfluenceService confluenceService) {
        this.confluenceService = confluenceService;
    }
}

