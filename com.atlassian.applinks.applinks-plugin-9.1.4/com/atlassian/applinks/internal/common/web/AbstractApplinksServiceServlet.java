/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.entity.ContentType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.common.web;

import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.net.ResponseHeaderUtil;
import com.atlassian.applinks.internal.common.net.ServiceExceptionHttpMapper;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractApplinksServiceServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AbstractApplinksServiceServlet.class);
    protected static final String DEFAULT_CONTENT_TYPE = ContentType.create((String)"text/html", (Charset)StandardCharsets.UTF_8).toString();
    protected static final String MODULE_PAGE_COMMON = "page-common";
    protected static final String IJ_PARAM_APPLICATION_TYPE = "applicationType";
    protected static final String PARAM_DECORATOR = "decorator";
    protected static final String PARAM_TITLE = "title";
    protected static final String PARAM_ACTIVE_TAB = "activeTab";
    protected static final String PARAM_PAGE_INITIALIZER = "pageInitializer";
    protected static final String PARAM_ERROR_MESSAGE = "errorMessage";
    protected final AppLinkPluginUtil appLinkPluginUtil;
    protected final I18nResolver i18nResolver;
    protected final InternalHostApplication internalHostApplication;
    protected final LoginUriProvider loginProvider;
    protected final SoyTemplateRenderer soyTemplateRenderer;
    protected final PageBuilderService pageBuilderService;

    protected AbstractApplinksServiceServlet(AppLinkPluginUtil appLinkPluginUtil, I18nResolver i18nResolver, InternalHostApplication internalHostApplication, LoginUriProvider loginProvider, SoyTemplateRenderer soyTemplateRenderer, PageBuilderService pageBuilderService) {
        this.appLinkPluginUtil = appLinkPluginUtil;
        this.i18nResolver = i18nResolver;
        this.internalHostApplication = internalHostApplication;
        this.loginProvider = loginProvider;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ResponseHeaderUtil.preventCrossFrameClickJacking(response);
            this.doServiceGet(request, response);
        }
        catch (Exception e) {
            ServiceException serviceException = ApplinkErrors.findCauseOfType(e, ServiceException.class);
            if (serviceException == null || response.isCommitted()) {
                Throwables.propagateIfInstanceOf((Throwable)e, ServletException.class);
                Throwables.propagateIfInstanceOf((Throwable)e, IOException.class);
                Throwables.propagateIfInstanceOf((Throwable)e, RuntimeException.class);
                throw new ServletException((Throwable)e);
            }
            log.debug("Service exception while processing request to {}: {}", (Object)request.getRequestURI(), (Object)serviceException);
            ResponseHeaderUtil.preventCrossFrameClickJacking(response);
            Response.Status status = ServiceExceptionHttpMapper.getStatus(serviceException);
            if (status == Response.Status.UNAUTHORIZED) {
                response.sendRedirect(this.getLoginRedirectUrl(request));
            }
            response.setStatus(status.getStatusCode());
            this.renderError(status, request, response, serviceException);
        }
    }

    protected final void render(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull String moduleKey, @Nonnull String templateName, @Nonnull Map<String, Object> data) throws IOException, ServletException {
        this.renderInternal(response, moduleKey, templateName, this.createData(request, data));
    }

    protected abstract void doServiceGet(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2) throws IOException, ServiceException, ServletException;

    @Nonnull
    protected abstract Iterable<String> webResourceContexts();

    @Nullable
    protected String getDecorator(@Nonnull HttpServletRequest request) {
        return null;
    }

    @Nullable
    protected String getPageTitle(@Nonnull HttpServletRequest request) {
        return null;
    }

    @Nullable
    protected String getActiveTab(@Nonnull HttpServletRequest request) {
        return null;
    }

    @Nullable
    protected String getPageInitializer(@Nonnull HttpServletRequest request) {
        return null;
    }

    private void renderInternal(@Nonnull HttpServletResponse response, @Nonnull String moduleKey, @Nonnull String templateName, @Nonnull Map<String, Object> data) throws IOException, ServletException {
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(moduleKey, "moduleKey");
        Objects.requireNonNull(templateName, "templateName");
        Objects.requireNonNull(data, "data");
        this.requireResources();
        response.setContentType(DEFAULT_CONTENT_TYPE);
        try {
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), this.appLinkPluginUtil.completeModuleKey(moduleKey), templateName, data, this.createInjectedData());
        }
        catch (SoyException e) {
            throw new ServletException((Throwable)e);
        }
    }

    private Map<String, Object> createErrorData(Response.Status status, String errorMessage) {
        HashMap data = Maps.newHashMap();
        data.put(PARAM_TITLE, this.getErrorPageTitle(status));
        data.put(PARAM_ERROR_MESSAGE, errorMessage);
        return data;
    }

    private Map<String, Object> createData(HttpServletRequest request, Map<String, Object> mainData) {
        HashMap data = Maps.newHashMap();
        data.put(PARAM_ACTIVE_TAB, this.getActiveTab(request));
        data.put(PARAM_DECORATOR, this.getDecorator(request));
        data.put(PARAM_TITLE, this.getPageTitle(request));
        data.put(PARAM_PAGE_INITIALIZER, this.getPageInitializer(request));
        data.putAll(mainData);
        return data;
    }

    protected final Map<String, Object> createInjectedData() {
        return Collections.singletonMap(IJ_PARAM_APPLICATION_TYPE, ApplicationTypes.resolveApplicationTypeId(this.internalHostApplication.getType()));
    }

    private void requireResources() {
        this.pageBuilderService.assembler().resources().requireWebResource(this.appLinkPluginUtil.completeModuleKey(MODULE_PAGE_COMMON));
        for (String context : this.webResourceContexts()) {
            this.pageBuilderService.assembler().resources().requireContext(context);
        }
    }

    private void renderError(Response.Status status, HttpServletRequest request, HttpServletResponse response, ServiceException serviceException) throws IOException, ServletException {
        this.renderInternal(response, MODULE_PAGE_COMMON, "applinks.page.common.error", this.createErrorData(status, serviceException.getLocalizedMessage()));
    }

    private String getErrorPageTitle(Response.Status status) {
        return this.i18nResolver.getText("applinks.common.error.title", new Serializable[]{Integer.valueOf(status.getStatusCode())});
    }

    private String getLoginRedirectUrl(HttpServletRequest request) {
        StringBuffer callback = request.getRequestURL();
        if (!StringUtils.isBlank((CharSequence)request.getQueryString())) {
            callback.append("?").append(request.getQueryString());
        }
        return this.loginProvider.getLoginUriForRole(URI.create(callback.toString()), UserRole.ADMIN).toASCIIString();
    }
}

