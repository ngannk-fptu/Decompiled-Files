/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.profile;

import com.atlassian.confluence.api.impl.pagination.PaginationQueryImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.internal.follow.FollowManagerInternal;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.MaxResultsParameter;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.security.Principal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class NetworkMacro
extends BaseMacro {
    private UserAccessor userAccessor;
    private FollowManagerInternal followManager;
    private PermissionManager permissionManager;
    public static final int PEOPLE_MAX = 30;

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        String username = (String)params.get("username");
        if (StringUtils.isBlank((CharSequence)username)) {
            username = AuthenticatedUserThreadLocal.getUsername();
        }
        if (username == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_PEOPLE_DIRECTORY)) {
            return GeneralUtil.getI18n().getText("community.macro.notpermitted");
        }
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new MacroException("Username " + username + " is not valid");
        }
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        context.put("username", username);
        context.put("user", user);
        context.put("viewingMyProfile", user.equals(AuthenticatedUserThreadLocal.get()));
        FollowMode followMode = this.getFollowMode((String)params.get("0"));
        context.put("mode", (Object)followMode);
        Integer maxResults = this.getMaxResults(params, body, renderContext);
        maxResults = maxResults == null ? Integer.valueOf(30) : Integer.valueOf(Math.min(maxResults, 30));
        SimplePageRequest pageRequest = new SimplePageRequest(0, maxResults.intValue());
        PageResponse<String> usernames = followMode == FollowMode.followers ? this.followManager.getFollowers(user, PaginationQueryImpl.createNewQuery(Principal::getName)).page((PageRequest)pageRequest) : this.followManager.getFollowing(user, PaginationQueryImpl.createNewQuery(Principal::getName)).page((PageRequest)pageRequest);
        context.put("people", usernames.getResults());
        context.put("totalPeople", usernames.size());
        context.put("maxResults", maxResults);
        context.put("morePeople", usernames.hasMore());
        FollowTheme theme = this.getFollowTheme((String)params.get("theme"));
        context.put("theme", (Object)theme);
        context.put("showAddUser", !this.isStaticContext(renderContext) && (theme == FollowTheme.full || theme == FollowTheme.dashboard) && followMode == FollowMode.following && username.equals(AuthenticatedUserThreadLocal.getUsername()));
        return VelocityUtils.getRenderedTemplate("/includes/network-macro.vm", context);
    }

    private boolean isStaticContext(RenderContext renderContext) {
        return "pdf".equals(renderContext.getOutputType());
    }

    private Integer getMaxResults(Map params, String body, RenderContext renderContext) throws MacroException {
        if (!(renderContext instanceof PageContext)) {
            throw new MacroException("Incorrect render context: this macro is only usable within Confluence");
        }
        return (Integer)new MaxResultsParameter().findValue(new MacroExecutionContext(params, body, (PageContext)renderContext));
    }

    private FollowMode getFollowMode(String followMode) throws MacroException {
        try {
            return StringUtils.isEmpty((CharSequence)followMode) ? FollowMode.followers : FollowMode.valueOf(followMode);
        }
        catch (IllegalArgumentException e) {
            throw new MacroException(String.format("Encountered invalid follow mode: %s. Mode must be either '%s' or '%s'.", followMode, FollowMode.followers.name(), FollowMode.following.name()));
        }
    }

    private FollowTheme getFollowTheme(String followThemeString) throws MacroException {
        FollowTheme result;
        if (StringUtils.isBlank((CharSequence)followThemeString)) {
            result = FollowTheme.full;
        } else {
            try {
                result = FollowTheme.valueOf(followThemeString);
            }
            catch (IllegalArgumentException e) {
                throw new MacroException(String.format("Encountered invalid follow theme: %s. Theme must be either '%s', '%s' or '%s'.", followThemeString, FollowTheme.full.name(), FollowTheme.tiny.name(), FollowTheme.dashboard.name()));
            }
        }
        return result;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setFollowManager(FollowManagerInternal followManager) {
        this.followManager = followManager;
    }

    private static enum FollowTheme {
        full,
        tiny,
        dashboard;

    }

    private static enum FollowMode {
        following,
        followers;

    }
}

