/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.profile;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.confluence.userstatus.StatusTextRenderer;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ProfileMacro
extends BaseMacro {
    private UserAccessor userAccessor;
    private PermissionManager permissionManager;
    private SettingsManager settingsManager;
    private UserDetailsManager userDetailsManager;
    private StatusTextRenderer statusTextRenderer;

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map params, String s, RenderContext renderContext) throws MacroException {
        String userName = (String)params.get("user");
        if (userName == null) {
            throw new MacroException("You need to specify a username with the user parameter");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_PEOPLE_DIRECTORY)) {
            return GeneralUtil.getI18n().getText("community.macro.notpermitted");
        }
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        context.put("mode", params.get("mode"));
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (user == null) {
            context.put("userNotFound", true);
            context.put("username", userName);
        } else {
            context.put("user", user);
            context.put("userDetailsManager", this.userDetailsManager);
            String maskedEmail = GeneralUtil.maskEmail(user.getEmail());
            context.put("email", maskedEmail == null ? "" : maskedEmail);
            context.put("statusTextRenderer", this.statusTextRenderer);
            context.put("emailvisible", this.isEmailVisible());
            if (AuthenticatedUserThreadLocal.get() != null) {
                context.put("viewingMyProfile", AuthenticatedUserThreadLocal.getUsername().equals(userName));
            } else {
                context.put("viewingMyProfile", false);
            }
            String groups = (String)params.get("groups");
            if (StringUtils.isNotBlank((CharSequence)groups)) {
                context.put("profileGroups", Arrays.asList(groups.split(",")));
            }
        }
        return VelocityUtils.getRenderedTemplate("/includes/profile-macro.vm", context);
    }

    private boolean isEmailVisible() {
        return this.permissionManager.isConfluenceAdministrator(AuthenticatedUserThreadLocal.get()) || !this.settingsManager.getGlobalSettings().areEmailAddressesPrivate();
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public UserAccessor getUserAccessor() {
        return this.userAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setUserDetailsManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public void setStatusTextRenderer(StatusTextRenderer statusTextRenderer) {
        this.statusTextRenderer = statusTextRenderer;
    }
}

