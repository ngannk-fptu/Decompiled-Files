/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.spi.feature.DelegatedViewFeature
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.audit.frontend.servlet;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.analytics.ViewEvent;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.spi.feature.DelegatedViewFeature;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuditServlet
extends HttpServlet {
    private static final String ENCODING = StandardCharsets.UTF_8.name();
    @VisibleForTesting
    static final String RESOURCE_KEY = ":audit-base-resources";
    private static final String TEMPLATE_KEY = "atlassian.audit.auditBase";
    @VisibleForTesting
    static final String TEMPLATE_KEY_UNAUTHORISED = "atlassian.audit.auditUnauthorised";
    @VisibleForTesting
    static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    private final LoginUriProvider loginUriProvider;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final ApplicationProperties applicationProperties;
    private final PermissionChecker permissionChecker;
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo auditPluginInfo;
    private final DelegatedViewFeature delegatedViewFeature;
    private final WebSudoManager webSudoManager;

    public AuditServlet(@Nonnull LoginUriProvider loginUriProvider, @Nonnull SoyTemplateRenderer soyTemplateRenderer, @Nonnull UserManager userManager, @Nonnull ApplicationProperties applicationProperties, @Nonnull EventPublisher eventPublisher, @Nonnull PermissionChecker permissionChecker, @Nonnull DelegatedViewFeature delegatedViewFeature, @Nonnull AuditPluginInfo auditPluginInfo, @Nonnull WebSudoManager webSudoManager) {
        this.auditPluginInfo = Objects.requireNonNull(auditPluginInfo, "auditPluginInfo");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.delegatedViewFeature = Objects.requireNonNull(delegatedViewFeature, "delegatedViewFeature");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.loginUriProvider = Objects.requireNonNull(loginUriProvider, "loginUriProvider");
        this.permissionChecker = Objects.requireNonNull(permissionChecker, "permissionChecker");
        this.soyTemplateRenderer = Objects.requireNonNull(soyTemplateRenderer, "soyTemplateRenderer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.webSudoManager = Objects.requireNonNull(webSudoManager, "webSudoManager");
    }

    private String safeSoyParam(String param) {
        if (param == null) {
            return "";
        }
        return param;
    }

    private Map<String, Object> getMetaParams(Map<String, String[]> params) {
        HashMap<String, Object> metaParams = new HashMap<String, Object>();
        params.forEach((key, val) -> {
            if (key.startsWith("meta.")) {
                String keyName = key.split("\\.", 2)[1];
                metaParams.put(keyName, this.safeSoyParam(val[0]));
            }
        });
        return metaParams;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserProfile user = this.userManager.getRemoteUser(req);
        if (user == null) {
            this.redirectToLogin(req, resp);
            return;
        }
        resp.setContentType("text/html;charset=" + ENCODING);
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (req.getPathInfo().contains("/resource")) {
            String affectedObject = "";
            String resourceType = "";
            String resourceId = "";
            Map<String, Object> metaParams = this.getMetaParams(req.getParameterMap());
            Matcher matcher = Pattern.compile("/resource/(.+)/*").matcher(req.getPathInfo());
            if (matcher.find()) {
                affectedObject = matcher.group(1);
            }
            if (affectedObject != null && affectedObject.contains(",")) {
                String[] affectedObjectParts = affectedObject.split(",\\s*");
                resourceType = affectedObjectParts[0];
                resourceId = affectedObjectParts[1];
            }
            params.put("affectedObject", this.safeSoyParam(affectedObject));
            params.put("resourceId", this.safeSoyParam(resourceId));
            params.put("productName", this.applicationProperties.getDisplayName());
            params.put("isResourceView", true);
            params.putAll(metaParams);
            params.put("resourceType", this.safeSoyParam(resourceType));
            if (this.delegatedViewFeature.isEnabled() && this.permissionChecker.hasResourceAuditViewPermission(resourceType, resourceId)) {
                this.publishViewEvent(this.safeSoyParam(resourceType));
                this.renderView(resp, params);
                return;
            }
        } else if (this.permissionChecker.hasUnrestrictedAuditViewPermission()) {
            try {
                this.webSudoManager.willExecuteWebSudoRequest(req);
                this.publishViewEvent("global");
                this.renderView(resp, params);
                return;
            }
            catch (WebSudoSessionException wes) {
                this.webSudoManager.enforceWebSudoProtection(req, resp);
                return;
            }
        }
        if (this.isJiraPlatform()) {
            this.redirectToElevatedPermissionsLogin(req, resp);
        } else {
            this.renderUnauthorizedView(resp);
        }
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = AuditServlet.getUri(request);
        response.sendRedirect(this.loginUriProvider.getLoginUri(uri).toASCIIString());
    }

    private boolean isJiraPlatform() {
        return "jira".equals(this.applicationProperties.getPlatformId());
    }

    private void redirectToElevatedPermissionsLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = AuditServlet.getUri(request);
        request.getSession().setAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL, (Object)uri.toASCIIString());
        response.sendRedirect(this.loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
    }

    private static URI getUri(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return URI.create(requestURL.toString());
    }

    private void renderView(HttpServletResponse resp, Map<String, Object> params) throws IOException, ServletException {
        this.render(resp, TEMPLATE_KEY, params);
    }

    private void renderUnauthorizedView(HttpServletResponse resp) throws IOException, ServletException {
        this.render(resp, TEMPLATE_KEY_UNAUTHORISED, Collections.singletonMap("message", "Unauthorised access"));
    }

    private void render(HttpServletResponse resp, String template, Map<String, Object> soyData) throws IOException, ServletException {
        try {
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), this.auditPluginInfo.getPluginKey() + RESOURCE_KEY, template, soyData);
        }
        catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new ServletException((Throwable)e);
        }
    }

    private void publishViewEvent(String resourceType) {
        this.eventPublisher.publish((Object)new ViewEvent(resourceType, this.auditPluginInfo.getPluginVersion()));
    }
}

