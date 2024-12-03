/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.internal.web;

import com.atlassian.applinks.analytics.ApplinksEditEvent;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.exception.InvalidEntityStateException;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.common.web.AbstractApplinksServiceServlet;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.support.ApplinkStatusValidationService;
import com.atlassian.applinks.internal.web.WebApplinkHelper;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditApplinkServlet
extends AbstractApplinksServiceServlet {
    private static final String WEB_RESOURCE_CONTEXT = "applinks.edit.v3";
    private final ApplinkStatusValidationService applinkStatusValidationService;
    private final EventPublisher eventPublisher;
    private final PermissionValidationService permissionValidationService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final WebApplinkHelper webApplinkHelper;
    private final WebSudoManager webSudoManager;

    public EditApplinkServlet(AppLinkPluginUtil appLinkPluginUtil, I18nResolver i18nResolver, InternalHostApplication internalHostApplication, LoginUriProvider loginProvider, SoyTemplateRenderer soyTemplateRenderer, PageBuilderService pageBuilderService, ApplinkStatusValidationService applinkStatusValidationService, EventPublisher eventPublisher, PermissionValidationService permissionValidationService, ServiceExceptionFactory serviceExceptionFactory, WebApplinkHelper webApplinkHelper, WebSudoManager webSudoManager) {
        super(appLinkPluginUtil, i18nResolver, internalHostApplication, loginProvider, soyTemplateRenderer, pageBuilderService);
        this.applinkStatusValidationService = applinkStatusValidationService;
        this.eventPublisher = eventPublisher;
        this.permissionValidationService = permissionValidationService;
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.webApplinkHelper = webApplinkHelper;
        this.webSudoManager = webSudoManager;
    }

    @Override
    protected void doServiceGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServiceException, IOException, ServletException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            this.permissionValidationService.validateAdmin();
            ApplicationLink applink = this.webApplinkHelper.getApplicationLink(request);
            this.validateApplink(applink);
            this.publishAnalytics(applink);
            this.render(request, response, "page-applink-edit", "applinks.page.applink.edit.main", (Map<String, Object>)ImmutableMap.of((Object)"applink", (Object)applink, (Object)"applinkId", (Object)applink.getId().get(), (Object)"title", (Object)applink.getName()));
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    @Override
    @Nonnull
    protected Iterable<String> webResourceContexts() {
        return Collections.singletonList(WEB_RESOURCE_CONTEXT);
    }

    @Override
    @Nullable
    protected String getPageInitializer(@Nonnull HttpServletRequest request) {
        return "applinks/page/applink-edit";
    }

    private void publishAnalytics(ApplicationLink applink) {
        this.eventPublisher.publish((Object)new ApplinksEditEvent.Builder(applink).build());
    }

    private void validateApplink(ApplicationLink applink) throws ServiceException {
        try {
            this.applinkStatusValidationService.checkEditable(applink);
        }
        catch (ApplinkStatusException e) {
            String statusError = this.i18nResolver.getText((Message)e.getType().getI18nKey());
            throw this.serviceExceptionFactory.raise(InvalidEntityStateException.class, I18nKey.newI18nKey("applinks.service.error.applink.edit.invalidstatus", new Serializable[]{applink.getId(), statusError}));
        }
    }
}

