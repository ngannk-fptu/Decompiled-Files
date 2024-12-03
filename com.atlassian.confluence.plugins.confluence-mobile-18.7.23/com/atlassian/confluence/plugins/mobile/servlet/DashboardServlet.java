/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.MobileUtils
 *  com.atlassian.confluence.util.MobileUtils$MobileOS
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.google.gson.Gson
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.mobile.servlet;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.mobile.AnonymousUserSupport;
import com.atlassian.confluence.plugins.mobile.event.MobileDashboardEvent;
import com.atlassian.confluence.plugins.mobile.event.MobileLoginEvent;
import com.atlassian.confluence.plugins.mobile.velocity.MobileVelocityContextFactory;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.MobileUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DashboardServlet
extends HttpServlet {
    private static final String CONFLUENCE_MOBILE_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-mobile-plugin";
    private final UserDtoFactory userDtoFactory;
    private final Gson gson;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final AnonymousUserSupport anonymousUserSupport;
    private final CaptchaManager captchaManager;
    private final SettingsManager settingsManager;
    private final PageBuilderService pageBuilderService;
    private final WebResourceAssemblerBuilder webResourceAssemblerBuilder;
    private final EventPublisher eventPublisher;
    private final VelocityHelperService velocityHelperService;
    private final PluginAccessor pluginAccessor;

    public DashboardServlet(UserDtoFactory userDtoFactory, WebResourceUrlProvider webResourceUrlProvider, AnonymousUserSupport anonymousUserSupport, CaptchaManager captchaManager, SettingsManager settingsManager, PageBuilderService pageBuilderService, WebResourceAssemblerFactory webResourceAssemblerFactory, EventPublisher eventPublisher, PluginAccessor pluginAccessor, VelocityHelperService velocityHelperService) {
        this.userDtoFactory = userDtoFactory;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.anonymousUserSupport = anonymousUserSupport;
        this.captchaManager = captchaManager;
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
        this.velocityHelperService = velocityHelperService;
        this.gson = new Gson();
        this.pageBuilderService = pageBuilderService;
        this.webResourceAssemblerBuilder = webResourceAssemblerFactory.create();
        this.webResourceAssemblerBuilder.includeSuperbatchResources(false);
        this.pluginAccessor = pluginAccessor;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        Map<String, Object> context = this.createVelocityContext(request, remoteUser);
        this.pageBuilderService.seed(this.webResourceAssemblerBuilder.build());
        response.setContentType("text/html");
        this.eventPublisher.publish((Object)new MobileDashboardEvent(request));
        response.getWriter().write(this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/plugins/mobile/appframe.vm", context));
    }

    private Map<String, Object> createVelocityContext(HttpServletRequest request, ConfluenceUser remoteUser) throws UnsupportedEncodingException {
        UserDto currentUser = this.userDtoFactory.getUserDto(remoteUser);
        Map<String, Object> context = MobileVelocityContextFactory.getInstance().createContext();
        context.put("currentUser", this.gson.toJson((Object)currentUser));
        context.put("contextPath", request.getContextPath());
        context.put("hideHeader", Boolean.valueOf(request.getParameter("hideHeader")));
        context.put("defaultDesktopUrl", request.getContextPath() + "/dashboard.action");
        context.put("isAnalyticsEnabled", this.isAnalyticsInstalled());
        context.put("baseUrl", this.settingsManager.getGlobalSettings().getBaseUrl());
        MobileUtils.MobileOS os = MobileUtils.getMobileOS((HttpServletRequest)request);
        context.put("showBanner", this.isShowBanner(os));
        context.put("os", os.getValue());
        if (remoteUser == null) {
            context.put("loginUrl", this.createLoginUrl(request));
        }
        String staticResourcePrefix = this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO);
        context.put("appleTouchIcon57x57", staticResourcePrefix + "/download/resources/com.atlassian.confluence.plugins.confluence-mobile/images/apple-touch-icon-57x57-precomposed.png");
        context.put("appleTouchIcon72x72", staticResourcePrefix + "/download/resources/com.atlassian.confluence.plugins.confluence-mobile/images/apple-touch-icon-72x72-precomposed.png");
        context.put("appleTouchIcon114x114", staticResourcePrefix + "/download/resources/com.atlassian.confluence.plugins.confluence-mobile/images/apple-touch-icon-114x114-precomposed.png");
        context.put("appleTouchIcon144x144", staticResourcePrefix + "/download/resources/com.atlassian.confluence.plugins.confluence-mobile/images/apple-touch-icon-144x144-precomposed.png");
        context.put("viewUserProfilesPermission", this.anonymousUserSupport.isProfileViewPermitted());
        context.put("requireCaptcha", this.captchaManager.showCaptchaForCurrentUser());
        context.put("emailVisibility", this.settingsManager.getGlobalSettings().getEmailAddressVisibility());
        return context;
    }

    private boolean isAnalyticsInstalled() {
        return false;
    }

    private String createLoginUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        this.eventPublisher.publish((Object)new MobileLoginEvent(request));
        String originalUrl = "/plugins/servlet/mobile";
        return request.getContextPath() + "/login.action?os_destination=" + URLEncoder.encode(originalUrl, "UTF-8");
    }

    private boolean isShowBanner(MobileUtils.MobileOS os) {
        return !AuthenticatedUserThreadLocal.isAnonymousUser() && (os == MobileUtils.MobileOS.ANDROID || os == MobileUtils.MobileOS.IOS) && this.pluginAccessor.isPluginEnabled(CONFLUENCE_MOBILE_PLUGIN_KEY);
    }
}

