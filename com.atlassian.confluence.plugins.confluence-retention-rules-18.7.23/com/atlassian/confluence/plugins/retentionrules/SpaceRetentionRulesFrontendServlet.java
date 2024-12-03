/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.pages.TrashManager
 *  com.atlassian.confluence.retention.RetentionPolicyPermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.retentionrules;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.plugins.retentionrules.FrontendServlet;
import com.atlassian.confluence.retention.RetentionPolicyPermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SpaceRetentionRulesFrontendServlet
extends FrontendServlet {
    protected static final Logger log = LoggerFactory.getLogger(SpaceRetentionRulesFrontendServlet.class);
    protected static final String SPACE_KEY = "key";
    protected static final String SPACE_TEMPLATE_KEY = "confluence.retention.rules.space";
    protected static final String NOT_FOUND_ERROR = "Space not found";
    private final SpaceManager spaceManager;
    private final RetentionPolicyPermissionManager retentionPolicyPermissionManager;

    @Autowired
    public SpaceRetentionRulesFrontendServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport RetentionFeatureChecker retentionFeatureChecker, @ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport WebSudoManager webSudoManager, @ComponentImport SpaceManager spaceManager, @ComponentImport RetentionPolicyPermissionManager retentionPolicyPermissionManager, @ComponentImport TrashManager trashManager) {
        super(soyTemplateRenderer, retentionFeatureChecker, userManager, loginUriProvider, webSudoManager, trashManager);
        this.spaceManager = spaceManager;
        this.retentionPolicyPermissionManager = retentionPolicyPermissionManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.retentionRulesFeatureNotAvailable()) {
            try {
                response.sendRedirect(request.getContextPath() + "/admin/viewgeneralconfig.action");
            }
            catch (IOException e) {
                log.warn("Error redirecting to General config page", (Throwable)e);
            }
            return;
        }
        UserProfile user = this.getUserManager().getRemoteUser(request);
        if (user == null) {
            try {
                this.redirectToLogin(request, response);
            }
            catch (IOException e) {
                log.warn("Error redirecting to login", (Throwable)e);
            }
            return;
        }
        Space space = this.spaceManager.getSpace(request.getParameter(SPACE_KEY));
        if (space == null) {
            try {
                response.sendError(404, NOT_FOUND_ERROR);
            }
            catch (IOException e) {
                log.warn("Error redirecting to Not found page", (Throwable)e);
            }
            return;
        }
        try {
            if (!this.retentionPolicyPermissionManager.canViewSpacePolicy(AuthenticatedUserThreadLocal.get(), space)) {
                this.getSoyTemplateRenderer().render((Appendable)response.getWriter(), "com.atlassian.confluence.plugins.confluence-retention-rules:confluence-retention-rules-soy-resources", "confluence.retention.rules.unauthorised", Collections.emptyMap());
                return;
            }
            try {
                this.getWebSudoManager().willExecuteWebSudoRequest(request);
                this.getSoyTemplateRenderer().render((Appendable)response.getWriter(), "com.atlassian.confluence.plugins.confluence-retention-rules:confluence-retention-rules-soy-resources", SPACE_TEMPLATE_KEY, this.getData(space));
            }
            catch (WebSudoSessionException wes) {
                this.getWebSudoManager().enforceWebSudoProtection(request, response);
            }
            catch (IOException e) {
                log.warn("Error getting response stream", (Throwable)e);
            }
        }
        catch (SoyException e) {
            log.warn("Error rendering Soy template", (Throwable)e);
        }
    }

    private Map<String, Object> getData(Space space) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("spaceId", String.valueOf(space.getId()));
        this.getTrashManager().getTrashDateMigrationTime().ifPresent(instant -> data.put("trashDateMigrationTime", Date.from(instant)));
        return data;
    }
}

