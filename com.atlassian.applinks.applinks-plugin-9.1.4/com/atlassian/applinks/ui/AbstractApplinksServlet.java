/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.util.HtmlSafeContent;
import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.XsrfProtectedServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractApplinksServlet
extends HttpServlet
implements XsrfProtectedServlet {
    private static final String ERROR_TEMPLATE = "com/atlassian/applinks/ui/auth_container_error.vm";
    public static final String WEB_RESOURCE_KEY = "com.atlassian.applinks.applinks-plugin:";
    public static final String XSRF_AUTH_TEMPLATE = "com/atlassian/applinks/ui/xsrf.vm";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final WebResourceManager webResourceManager;
    protected final DocumentationLinker documentationLinker;
    protected final InternalHostApplication internalHostApplication;
    protected final TemplateRenderer templateRenderer;
    protected final I18nResolver i18nResolver;
    protected final MessageFactory messageFactory;
    protected final AdminUIAuthenticator adminUIAuthenticator;
    private final LoginUriProvider loginUriProvider;
    private final XsrfTokenAccessor xsrfTokenAccessor;
    private final XsrfTokenValidator xsrfTokenValidator;

    public AbstractApplinksServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, AdminUIAuthenticator adminUIAuthenticator) {
        this.i18nResolver = i18nResolver;
        this.messageFactory = messageFactory;
        this.templateRenderer = templateRenderer;
        this.webResourceManager = webResourceManager;
        this.documentationLinker = documentationLinker;
        this.internalHostApplication = internalHostApplication;
        this.loginUriProvider = loginUriProvider;
        this.adminUIAuthenticator = adminUIAuthenticator;
        this.xsrfTokenAccessor = null;
        this.xsrfTokenValidator = null;
    }

    public AbstractApplinksServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, AdminUIAuthenticator adminUIAuthenticator, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        this.i18nResolver = i18nResolver;
        this.messageFactory = messageFactory;
        this.templateRenderer = templateRenderer;
        this.webResourceManager = webResourceManager;
        this.documentationLinker = documentationLinker;
        this.internalHostApplication = internalHostApplication;
        this.loginUriProvider = loginUriProvider;
        this.adminUIAuthenticator = adminUIAuthenticator;
        this.xsrfTokenAccessor = xsrfTokenAccessor;
        this.xsrfTokenValidator = xsrfTokenValidator;
    }

    protected List<String> getRequiredWebResources() {
        return Collections.emptyList();
    }

    protected List<String> getRequiredWebResourceContexts() {
        return Collections.emptyList();
    }

    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("ual.view", (Object)Boolean.TRUE);
        try {
            if (this.requestRequiresProtection(request) && !this.xsrfTokenValidator.validateFormEncodedToken(request)) {
                HashMap<String, Object> renderContext = new HashMap<String, Object>();
                renderContext.put("parameters", request.getParameterNames());
                renderContext.put("parameterMap", request.getParameterMap());
                renderContext.put("requestMethod", request.getMethod().toLowerCase());
                renderContext.put("requestUrl", request.getRequestURL());
                this.render(XSRF_AUTH_TEMPLATE, renderContext, request, response);
                return;
            }
            this.doService(request, response);
            super.service(request, response);
        }
        catch (UnauthorizedBecauseUnauthenticatedException e) {
            StringBuffer callback = request.getRequestURL();
            if (!StringUtils.isBlank((CharSequence)request.getQueryString())) {
                callback.append("?").append(request.getQueryString());
            }
            response.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(callback.toString())).toASCIIString());
        }
        catch (UnauthorizedException e) {
            StringBuffer callback = request.getRequestURL();
            if (!StringUtils.isBlank((CharSequence)request.getQueryString())) {
                callback.append("?").append(request.getQueryString());
            }
            this.render(e.getTemplate(), (Map<String, Object>)ImmutableMap.of((Object)"isAdmin", (Object)this.adminUIAuthenticator.isCurrentUserAdmin(), (Object)"message", (Object)ObjectUtils.defaultIfNull((Object)e.getMessage(), (Object)""), (Object)"url", (Object)this.loginUriProvider.getLoginUri(URIUtil.uncheckedToUri(callback.toString()))), request, response);
        }
        catch (RequestException re) {
            this.logger.warn(String.format("Unable to serve page: \"%s\": %s: %s", request.getRequestURI(), re.getClass().getName(), re.getMessage()));
            response.setStatus(re.getStatus());
            this.render((String)StringUtils.defaultIfEmpty((CharSequence)re.getTemplate(), (CharSequence)ERROR_TEMPLATE), (Map<String, Object>)ImmutableMap.of((Object)"message", (Object)ObjectUtils.defaultIfNull((Object)re.getMessage(), (Object)""), (Object)"status", (Object)re.getStatus()), request, response);
        }
    }

    private boolean requestRequiresProtection(HttpServletRequest request) {
        if (!(this instanceof XsrfProtectedServlet)) {
            return false;
        }
        String method = request.getMethod();
        if (AbstractApplinksServlet.isModifyingMethod(method)) {
            return !"no-check".equals(request.getHeader("X-Atlassian-Token"));
        }
        return false;
    }

    private static boolean isModifyingMethod(String method) {
        return method.equals("POST") || method.equals("PUT") || method.equals("DELETE");
    }

    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected final Map<String, Object> emptyContext() {
        return Collections.emptyMap();
    }

    protected void render(String template, Map<String, Object> renderContext, HttpServletRequest request, HttpServletResponse response) throws IOException {
        for (String resource : this.getRequiredWebResources()) {
            this.webResourceManager.requireResource(resource);
        }
        for (String context : this.getRequiredWebResourceContexts()) {
            this.webResourceManager.requireResourcesForContext(context);
        }
        RendererContextBuilder builder = new RendererContextBuilder(renderContext).put("i18n", this.i18nResolver).put("webResources", new HtmlSafeContent(){

            @Override
            public CharSequence get() {
                StringWriter writer = new StringWriter();
                AbstractApplinksServlet.this.webResourceManager.includeResources((Writer)writer, UrlMode.AUTO);
                return writer.toString();
            }
        }).put("docLinker", this.documentationLinker);
        if (this.xsrfTokenAccessor != null) {
            builder.put("xsrftokenParamValue", this.xsrfTokenAccessor.getXsrfToken(request, response, true)).put("xsrftokenParamName", this.xsrfTokenValidator.getXsrfParameterName());
        }
        response.setContentType("text/html; charset=utf-8");
        this.templateRenderer.render(template, builder.build(), (Writer)response.getWriter());
    }

    protected String getRequiredParameter(HttpServletRequest request, String name) throws BadRequestException {
        String value = request.getParameter(name);
        if (StringUtils.isBlank((CharSequence)value)) {
            throw new BadRequestException(this.messageFactory.newI18nMessage("auth.config.parameter.missing", new Serializable[]{name}));
        }
        return value;
    }

    protected RendererContextBuilder createContextBuilder(ApplicationLink applicationLink) {
        return new RendererContextBuilder().put("localApplicationName", this.internalHostApplication.getName()).put("localApplicationType", this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey())).put("remoteApplicationName", applicationLink.getName()).put("remoteApplicationType", this.i18nResolver.getText(applicationLink.getType().getI18nKey()));
    }

    public static class ForbiddenException
    extends RequestException {
        public ForbiddenException(Message message) {
            super(403, message);
        }
    }

    public static class UnauthorizedBecauseUnauthenticatedException
    extends RequestException {
        public UnauthorizedBecauseUnauthenticatedException() {
            super(401);
        }
    }

    public static class UnauthorizedException
    extends RequestException {
        public UnauthorizedException() {
            this((Message)null);
        }

        public UnauthorizedException(Message message) {
            super(401, message);
        }

        public UnauthorizedException(Message message, Throwable cause) {
            super(401, message, cause);
        }

        @Override
        public final String getTemplate() {
            return "com/atlassian/applinks/ui/no_admin_privileges.vm";
        }
    }

    public static class NotFoundException
    extends RequestException {
        public NotFoundException() {
            this((Message)null);
        }

        public NotFoundException(Message message) {
            super(400, message);
        }

        public NotFoundException(Message message, Throwable cause) {
            super(404, message, cause);
        }
    }

    public static class BadRequestException
    extends RequestException {
        public BadRequestException() {
            this((Message)null);
        }

        public BadRequestException(Message message) {
            super(400, message);
        }

        public BadRequestException(Message message, Throwable cause) {
            super(400, message, cause);
        }
    }

    protected static class RequestException
    extends RuntimeException {
        private final int status;
        private final Message message;
        protected String template;

        public RequestException(int status, Message message, Throwable cause) {
            super(cause);
            this.message = message;
            this.status = status;
        }

        public RequestException(int status, Message message) {
            this.message = message;
            this.status = status;
        }

        public RequestException(int status) {
            this(status, null);
        }

        public int getStatus() {
            return this.status;
        }

        public String getTemplate() {
            return this.template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        @Override
        public String getMessage() {
            return this.message == null ? null : this.message.toString();
        }
    }
}

