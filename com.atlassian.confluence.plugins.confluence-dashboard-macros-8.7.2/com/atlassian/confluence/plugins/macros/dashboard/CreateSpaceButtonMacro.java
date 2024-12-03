/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSpaceButtonMacro
extends BaseMacro {
    public static final String MACRO_NAME = "create-space-button";
    private static final Logger log = LoggerFactory.getLogger(CreateSpaceButtonMacro.class);
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private VelocityHelperService velocityHelperService;

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasCreatePermission((User)currentUser, PermissionManager.TARGET_APPLICATION, Space.class)) {
            return "";
        }
        Map<String, String> macroParameters = CreateSpaceButtonMacro.castMacroParams(parameters);
        String sizeParameter = macroParameters.get("size");
        String heightParameter = macroParameters.get("height");
        String widthParameter = macroParameters.get("width");
        sizeParameter = sizeParameter == null ? "large" : sizeParameter.trim();
        if (heightParameter == null) {
            heightParameter = this.getSize(sizeParameter);
        }
        if (widthParameter == null) {
            widthParameter = this.getSize(sizeParameter);
        }
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("req", ServletActionContext.getRequest());
        contextMap.put("width", widthParameter);
        contextMap.put("height", heightParameter);
        contextMap.put("largeIcon", this.isLarge(sizeParameter));
        try {
            return this.renderButton(contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to load the create space button template.", (Throwable)e);
            throw new MacroException((Throwable)e);
        }
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    protected String renderButton(Map contextMap) {
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/plugins/macros/dashboard/createspacebutton.vm", contextMap);
    }

    private boolean isLarge(String sizeParameter) {
        return "large".equals(sizeParameter) || "\"large\"".equals(sizeParameter) || "'large'".equals(sizeParameter);
    }

    private String getSize(String sizeParameter) {
        return this.isLarge(sizeParameter) ? "32" : "16";
    }

    public String getName() {
        return MACRO_NAME;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    private static Map<String, String> castMacroParams(Map macroParams) {
        return macroParams;
    }
}

