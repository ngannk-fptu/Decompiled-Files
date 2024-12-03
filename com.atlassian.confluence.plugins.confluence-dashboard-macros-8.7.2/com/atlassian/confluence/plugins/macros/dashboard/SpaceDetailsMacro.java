/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.labels.SpaceLabelManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SpaceDetailsMacro
extends BaseMacro {
    public static final int ADMIN_LIMIT = 7;
    private static final Logger log = LoggerFactory.getLogger(SpaceDetailsMacro.class);
    private static final String MACRO_NAME = "space-details";
    @ComponentImport
    private SpaceManager spaceManager;
    @ComponentImport
    private SpaceLabelManager spaceLabelManager;
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private VelocityHelperService velocityHelperService;

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        List adminUsers;
        Map<String, String> macroParameters = SpaceDetailsMacro.castMacroParams(parameters);
        String widthParameter = macroParameters.get("width");
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        if (widthParameter != null) {
            contextMap.put("tableWidth", widthParameter);
        }
        Space currentSpace = null;
        if (renderContext instanceof PageContext) {
            PageContext pageContext = (PageContext)renderContext;
            currentSpace = this.spaceManager.getSpace(pageContext.getSpaceKey());
        }
        if (currentSpace == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, currentSpace)) {
            return "Space not found";
        }
        ConfluenceActionSupport action = this.getWiredConfluenceActionSupport();
        HttpServletRequest request = ServletActionContext.getRequest();
        contextMap.put("dateFormatter", action.getDateFormatter());
        contextMap.put("i18NBean", action.getI18n());
        contextMap.put("req", request);
        contextMap.put("space", currentSpace);
        contextMap.put("teamLabels", this.spaceLabelManager.getTeamLabelsOnSpace(currentSpace.getKey()));
        boolean showAllAdmins = Boolean.valueOf(request.getParameter("showAllAdmins"));
        if (showAllAdmins) {
            adminUsers = this.spaceManager.getSpaceAdmins(currentSpace);
        } else {
            adminUsers = this.spaceManager.getSpaceAdmins(currentSpace, 8);
            if (adminUsers.size() > 7) {
                contextMap.put("moreAdmins", true);
                adminUsers = adminUsers.subList(0, 7);
            }
        }
        contextMap.put("admins", adminUsers);
        try {
            return this.renderSpaceDetails(contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to load the space list template.", (Throwable)e);
            throw new MacroException((Throwable)e);
        }
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    protected String renderSpaceDetails(Map contextMap) {
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/plugins/macros/dashboard/macro-viewspacedetails.vm", contextMap);
    }

    protected ConfluenceActionSupport getWiredConfluenceActionSupport() {
        ConfluenceActionSupport action = new ConfluenceActionSupport();
        ContainerManager.autowireComponent((Object)action);
        return action;
    }

    public String getName() {
        return MACRO_NAME;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setSpaceLabelManager(SpaceLabelManager spaceLabelManager) {
        this.spaceLabelManager = spaceLabelManager;
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    private static Map<String, String> castMacroParams(Map macroParams) {
        return macroParams;
    }
}

