/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.CacheControl
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions;
import com.atlassian.plugins.whitelist.ui.WhitelistBean;
import com.atlassian.plugins.whitelist.ui.WhitelistBeanService;
import com.atlassian.plugins.whitelist.ui.WhitelistSettingsBean;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.CacheControl;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhitelistServlet
extends HttpServlet {
    private static final String WHITELIST_TEMPLATE_KEY = "com.atlassian.plugins.atlassian-whitelist-ui-plugin:whitelist-bootstrap-template";
    private static final String WHITELIST_WEB_RESOURCES_KEY = "com.atlassian.plugins.atlassian-whitelist-ui-plugin:whitelist-web-resources";
    private static final String WHITELIST_SOY_TEMPLATE = "com.atlassian.plugins.whitelist.ui.whitelistPage";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistServlet.class);
    private final ApplicationLinkRestrictions restrictionsService;
    private final LoginUriProvider loginUriProvider;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final WebResourceManager webResourceManager;
    private final WebSudoManager webSudoManager;
    private final WhitelistBeanService whitelistBeanService;
    private final WhitelistService whitelistService;

    public WhitelistServlet(SoyTemplateRenderer soyTemplateRenderer, WebResourceManager webResourceManager, WhitelistService whitelistService, WebSudoManager webSudoManager, UserManager userManager, LoginUriProvider loginUriProvider, WhitelistBeanService whitelistBeanService, ApplicationLinkRestrictions restrictionsService) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webResourceManager = webResourceManager;
        this.whitelistService = whitelistService;
        this.webSudoManager = webSudoManager;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.whitelistBeanService = whitelistBeanService;
        this.restrictionsService = restrictionsService;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            if (this.isCurrentUserSysAdmin(req)) {
                this.renderResponse(req, resp);
            } else {
                resp.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().toString())).toASCIIString());
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    private boolean isCurrentUserSysAdmin(HttpServletRequest req) {
        return Optional.ofNullable(this.userManager.getRemoteUserKey(req)).map(arg_0 -> ((UserManager)this.userManager).isSystemAdmin(arg_0)).orElse(false);
    }

    private void renderResponse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setHeader("Cache-Control", WhitelistServlet.noCache());
        this.webResourceManager.requireResource(WHITELIST_WEB_RESOURCES_KEY);
        WhitelistSettingsBean settings = new WhitelistSettingsBean(this.restrictionsService.getRestrictiveness());
        try {
            List<WhitelistBean> beans = this.whitelistBeanService.getAll();
            ImmutableMap params = ImmutableMap.of((Object)"contextPath", (Object)req.getContextPath(), (Object)"enabled", (Object)this.whitelistService.isWhitelistEnabled(), (Object)"data", (Object)OBJECT_MAPPER.writeValueAsString(beans), (Object)"settings", (Object)OBJECT_MAPPER.writeValueAsString((Object)settings));
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), WHITELIST_TEMPLATE_KEY, WHITELIST_SOY_TEMPLATE, (Map)params);
        }
        catch (SoyException e) {
            LOGGER.info("Failed to render soy template '{}': {}", (Object)WHITELIST_SOY_TEMPLATE, (Object)e.getMessage());
            LOGGER.debug("Failed to render soy template 'com.atlassian.plugins.whitelist.ui.whitelistPage' contained in resource 'com.atlassian.plugins.atlassian-whitelist-ui-plugin:whitelist-bootstrap-template'", (Throwable)e);
            resp.sendError(500, "Failed to render response");
        }
    }

    private static String noCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        return cacheControl.toString();
    }
}

