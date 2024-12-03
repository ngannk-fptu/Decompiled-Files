/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.primitives.Ints
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.api.WebResourcesService;
import com.atlassian.troubleshooting.stp.SimpleXsrfTokenGenerator;
import com.atlassian.troubleshooting.stp.Stage;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.SupportActionFactory;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequestImpl;
import com.atlassian.troubleshooting.stp.servlet.StpServletUtils;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportToolsViewServlet
extends HttpServlet {
    private static final String STP_SURVEY_URL = "https://ecosystem.atlassian.net/projects/ATST/issues/?filter=allopenissues";
    private static final Logger LOG = LoggerFactory.getLogger(SupportToolsViewServlet.class);
    private static final long serialVersionUID = -172924069919294316L;
    private static final String AUIPLUGIN_KEY = "com.atlassian.auiplugin";
    private final TemplateRenderer renderer;
    private final SupportActionFactory factory;
    private final SupportApplicationInfo appInfo;
    private final UserManager userManager;
    private final WebSudoManager webSudoManager;
    private final WebResourcesService webResourcesService;
    private final SimpleXsrfTokenGenerator tokenGenerator;
    private final StpServletUtils stpServletUtils;
    private final PluginAccessor pluginAccessor;
    private final WebInterfaceManager webInterfaceManager;

    @Autowired
    public SupportToolsViewServlet(UserManager userManager, TemplateRenderer renderer, WebSudoManager webSudoManager, SupportApplicationInfo appInfo, WebResourcesService webResourcesService, SupportActionFactory factory, StpServletUtils stpServletUtils, PluginAccessor pluginAccessor, WebInterfaceManager webInterfaceManager) {
        this.userManager = Objects.requireNonNull(userManager);
        this.renderer = Objects.requireNonNull(renderer);
        this.appInfo = Objects.requireNonNull(appInfo);
        this.webSudoManager = Objects.requireNonNull(webSudoManager);
        this.webResourcesService = Objects.requireNonNull(webResourcesService);
        this.factory = Objects.requireNonNull(factory);
        this.stpServletUtils = Objects.requireNonNull(stpServletUtils);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.webInterfaceManager = webInterfaceManager;
        this.tokenGenerator = new SimpleXsrfTokenGenerator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.stpServletUtils.initializeHeader(resp);
        try {
            boolean isAdmin = this.performAdminChecks(req, resp);
            if (isAdmin) {
                Map<String, Object> context = this.prepareContext(req);
                String sessionToken = this.tokenGenerator.generateToken(req);
                String tokenName = this.tokenGenerator.getXsrfTokenName();
                context.put("tokenName", tokenName);
                String token = req.getParameter(tokenName);
                context.put("token", sessionToken);
                if (this.tokenGenerator.validateToken(req, token)) {
                    this.displayResults(req, resp, context);
                } else {
                    this.handleXsrfError(req, resp, context);
                }
            }
        }
        catch (WebSudoSessionException wes) {
            resp.sendError(403);
        }
        finally {
            resp.getWriter().flush();
        }
    }

    private void handleXsrfError(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> context) throws IOException {
        SupportToolsAction action = (SupportToolsAction)context.get("action");
        context.put("existingParams", req.getParameterMap());
        if (action.getName().equals("tabs")) {
            this.renderer.render("/templates/html/xsrf-error.vm", context, (Writer)resp.getWriter());
        } else {
            this.renderer.render("/templates/html/xsrf-error-body.vm", context, (Writer)resp.getWriter());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.stpServletUtils.initializeHeader(resp);
        try {
            boolean isAdmin = this.performAdminChecks(req, resp);
            Map<String, Object> context = this.prepareContext(req);
            if (isAdmin) {
                String sessionToken = this.tokenGenerator.generateToken(req);
                String tokenName = this.tokenGenerator.getXsrfTokenName();
                context.put("tokenName", tokenName);
                context.put("token", sessionToken);
                this.displayResults(req, resp, context);
            }
            resp.getWriter().close();
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    private boolean performAdminChecks(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserProfile user;
        String pathInfo = req.getPathInfo();
        String[] tokens = StringUtils.split((String)pathInfo, (char)'/');
        String actionName = tokens == null || tokens.length == 0 ? null : tokens[0];
        SupportToolsAction action = this.factory.getAction(actionName);
        if (action.requiresWebSudo()) {
            this.webSudoManager.willExecuteWebSudoRequest(req);
        }
        if ((user = this.userManager.getRemoteUser(req)) == null) {
            if (action.getName().equals("tabs")) {
                this.stpServletUtils.redirectToLogin(req, resp);
            } else {
                this.renderer.render("/templates/html/ajax-not-logged-in.vm", this.prepareContext(req), (Writer)resp.getWriter());
            }
            return false;
        }
        if (this.userManager.isSystemAdmin(user.getUserKey())) {
            return true;
        }
        if (action.getName().equals("tabs")) {
            this.stpServletUtils.redirectToLogin(req, resp);
        } else {
            this.renderer.render("/templates/html/ajax-no-permission.vm", this.prepareContext(req), (Writer)resp.getWriter());
        }
        return false;
    }

    private Map<String, Object> prepareContext(HttpServletRequest req) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        String pathInfo = req.getPathInfo();
        String[] tokens = StringUtils.split((String)pathInfo, (char)'/');
        Stage stage = tokens == null || tokens.length < 2 ? Stage.START : Stage.lookup(tokens[1]);
        context.put("stage", (Object)stage);
        String actionName = tokens == null || tokens.length == 0 ? null : tokens[0];
        SupportToolsAction action = this.factory.getAction(actionName);
        context.put("action", action);
        if (action.getName().equals("tabs")) {
            context.put("factory", this.factory);
        }
        String baseURL = this.appInfo.getBaseURL(req);
        context.put("servletHomePath", baseURL + req.getServletPath());
        context.put("info", this.appInfo);
        context.put("baseURL", baseURL);
        context.put("stpSurveyUrl", STP_SURVEY_URL);
        context.put("webResource", this.webResourcesService);
        context.put("webInterfaceManager", this.webInterfaceManager);
        context.put("auiVersionMajor", this.getAuiVersionMajor());
        return context;
    }

    private int getAuiVersionMajor() {
        return Optional.ofNullable(this.pluginAccessor.getPlugin(AUIPLUGIN_KEY)).map(Plugin::getPluginInformation).map(PluginInformation::getVersion).map(version -> version.split("\\.")).filter(versionTab -> ((String[])versionTab).length >= 1).map(versionTab -> versionTab[0]).map(versionString -> Ints.tryParse((String)versionString)).orElse(-1);
    }

    private void displayResults(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> context) throws IOException {
        block7: {
            SupportToolsAction action = (SupportToolsAction)context.get("action");
            Stage stage = (Stage)((Object)context.get("stage"));
            ValidationLog validationLog = new ValidationLog(this.appInfo);
            context.put("validationLog", validationLog);
            SafeHttpServletRequestImpl safeReq = new SafeHttpServletRequestImpl(req);
            action.prepare(context, safeReq, validationLog);
            if (stage == Stage.EXECUTE) {
                action.validate(context, safeReq, validationLog);
                if (validationLog.hasErrors()) {
                    this.renderer.render(action.getErrorTemplatePath(), context, (Writer)resp.getWriter());
                } else {
                    try {
                        action.execute(context, safeReq, validationLog);
                        if (validationLog.hasErrors()) {
                            this.renderer.render(action.getErrorTemplatePath(), context, (Writer)resp.getWriter());
                            break block7;
                        }
                        this.renderer.render(action.getSuccessTemplatePath(), context, (Writer)resp.getWriter());
                    }
                    catch (Exception e) {
                        LOG.error(e.getMessage(), (Throwable)e);
                        validationLog.addError("Error rendering the page, check your logs for more details.", new Serializable[0]);
                        this.renderer.render(action.getErrorTemplatePath(), context, (Writer)resp.getWriter());
                    }
                }
            } else {
                this.renderer.render(action.getStartTemplatePath(), context, (Writer)resp.getWriter());
            }
        }
    }
}

