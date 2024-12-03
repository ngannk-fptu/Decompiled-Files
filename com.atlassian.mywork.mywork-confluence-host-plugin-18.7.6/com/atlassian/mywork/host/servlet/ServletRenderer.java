/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.mywork.service.LocalTaskService
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.servlet;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.mywork.host.servlet.LoginMiniviewServlet;
import com.atlassian.mywork.service.LocalTaskService;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ServletRenderer {
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final LocalRegistrationService registrationService;
    private final LocaleResolver localeResolver;
    private final UserAccessor userAccessor;
    private final LocalTaskService taskService;

    public ServletRenderer(TemplateRenderer templateRenderer, UserManager userManager, I18nResolver i18nResolver, LocalRegistrationService registrationService, LocaleResolver localeResolver, UserAccessor userAccessor, LocalTaskService taskService) {
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.i18nResolver = i18nResolver;
        this.registrationService = registrationService;
        this.localeResolver = localeResolver;
        this.userAccessor = userAccessor;
        this.taskService = taskService;
    }

    public void renderWithAnchor(HttpServletRequest req, HttpServletResponse resp, String template) throws IOException {
        this.renderWithAnchor(req, resp, template, req.getParameter("decorator"));
    }

    public void renderWithAnchor(HttpServletRequest req, HttpServletResponse resp, String template, String decorator) throws IOException {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey(req);
        ConfluenceUser currentUser = this.userAccessor.getUserByKey(remoteUserKey);
        String username = currentUser.getName();
        if (username == null) {
            String requestPath = this.getRequestPath(req);
            String loginPath = LoginMiniviewServlet.getLoginPath(requestPath);
            resp.sendRedirect(req.getContextPath() + loginPath);
        } else {
            String firstName = this.userManager.getUserProfile(username).getFullName().split(" ")[0];
            String anchorTarget = req.getParameter("anchorTarget");
            if (StringUtils.isBlank((CharSequence)anchorTarget)) {
                anchorTarget = "_parent";
            }
            ImmutableMap context = ImmutableMap.builder().put((Object)"resp", (Object)resp).put((Object)"urlMode", (Object)UrlMode.RELATIVE).put((Object)"req", (Object)req).put((Object)"firstName", (Object)firstName).put((Object)"decorator", (Object)(decorator != null ? decorator : "")).put((Object)"anchorTarget", (Object)anchorTarget).put((Object)"i18n", (Object)this.i18nResolver).put((Object)"configurationVersion", (Object)this.registrationService.getCacheValue(this.localeResolver.getLocale(req))).put((Object)"requestTime", (Object)System.currentTimeMillis()).put((Object)"shouldMigrateTasks", (Object)this.shouldUserMigrateTasks(currentUser)).put((Object)"userHasInteractedWithDeprecatedTaskWorkbox", (Object)this.hasUserInteractedWithDeprecatedTaskWorkbox(currentUser)).put((Object)"hasTasksToMigrate", (Object)this.taskService.hasTasksToMigrate(currentUser.getName())).build();
            resp.setContentType("text/html; charset=UTF-8");
            this.templateRenderer.render(template, (Map)context, (Writer)resp.getWriter());
        }
    }

    private String getRequestPath(HttpServletRequest req) {
        StringBuilder path = new StringBuilder(req.getServletPath());
        if (req.getPathInfo() != null) {
            path.append(req.getPathInfo());
        }
        if (req.getQueryString() != null) {
            path.append("?").append(req.getQueryString());
        }
        return path.toString();
    }

    private boolean shouldUserMigrateTasks(ConfluenceUser user) {
        String personalTasksMigratedKey = "confluence.plugins.myworkday.personaltasks.migrated";
        boolean userHasMigratedAlready = this.userAccessor.getUserPreferences((User)user).getBoolean(personalTasksMigratedKey);
        if (userHasMigratedAlready) {
            return false;
        }
        return this.taskService.hasTasksToMigrate(user.getName());
    }

    private boolean hasUserInteractedWithDeprecatedTaskWorkbox(ConfluenceUser user) {
        String hasUserInteractedKey = "confluence.plugins.myworkday.personaltasks.hasinteracted";
        boolean userHasInteractedAlready = this.userAccessor.getUserPreferences((User)user).getBoolean(hasUserInteractedKey);
        return userHasInteractedAlready;
    }
}

