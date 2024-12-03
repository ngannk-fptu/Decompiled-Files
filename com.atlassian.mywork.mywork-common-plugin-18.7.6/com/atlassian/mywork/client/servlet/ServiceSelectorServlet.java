/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.mywork.client.servlet;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.mywork.service.ClientService;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.mywork.service.TimeoutService;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;

public class ServiceSelectorServlet
extends HttpServlet {
    private static final String XSRF_OVERRIDE_HEADER_NAME = "X-Atlassian-Token";
    private static final String XSRF_OVERRIDE_HEADER_VALUE = "no-check";
    private final WebSudoManager webSudoManager;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final XsrfTokenAccessor xsrfTokenAccessor;
    private final XsrfTokenValidator xsrfTokenValidator;
    private final ServiceSelector serviceSelector;
    private final HostService hostService;
    private final ClientService clientService;
    private final TimeoutService timeoutService;
    private final TemplateRenderer templateRenderer;
    private final HelpPathResolver helpPathResolver;

    public ServiceSelectorServlet(WebSudoManager webSudoManager, UserManager userManager, LoginUriProvider loginUriProvider, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, ServiceSelector serviceSelector, HostService hostService, ClientService clientService, TimeoutService timeoutService, TemplateRenderer templateRenderer, HelpPathResolver helpPathResolver) {
        this.webSudoManager = webSudoManager;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.xsrfTokenAccessor = xsrfTokenAccessor;
        this.xsrfTokenValidator = xsrfTokenValidator;
        this.serviceSelector = serviceSelector;
        this.hostService = hostService;
        this.clientService = clientService;
        this.timeoutService = timeoutService;
        this.templateRenderer = templateRenderer;
        this.helpPathResolver = helpPathResolver;
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            String username = this.userManager.getRemoteUsername(req);
            if (username == null) {
                URI loginUri = this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().toString()));
                resp.sendRedirect(loginUri.toASCIIString());
                return;
            }
            if (!this.userManager.isSystemAdmin(username)) {
                resp.sendError(403, "Only a system administrator can access this resource");
            }
            super.service(req, resp);
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ArrayList<ApplicationLink> availableHosts = new ArrayList<ApplicationLink>();
        Iterables.addAll(availableHosts, this.hostService.getAvailableHosts());
        ApplicationLink host = this.findSuggestedHost(availableHosts);
        boolean isHostAvailable = this.serviceSelector.isHostAvailable();
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("target", (Object)this.serviceSelector.getEffectiveTarget());
        context.put("host", host);
        context.put("availableHosts", availableHosts);
        context.put("hostAvailable", isHostAvailable);
        context.put("helpPathResolver", this.helpPathResolver);
        context.put("req", req);
        if (isHostAvailable) {
            ArrayList activeClients = Lists.newArrayList(this.clientService.getActiveClients());
            Collections.sort(activeClients, new Comparator<ApplicationLink>(){

                @Override
                public int compare(ApplicationLink o1, ApplicationLink o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            context.put("activeClients", activeClients);
            context.put("pollingInterval", this.timeoutService.getTimeout());
            context.put("maxPollingInterval", this.timeoutService.getMaxTimeout());
        }
        this.render("templates/service-selector.vm", context, req, resp);
    }

    private ApplicationLink findSuggestedHost(List<ApplicationLink> availableHosts) {
        Iterator iterator = this.hostService.getActiveHost().iterator();
        if (iterator.hasNext()) {
            ApplicationLink activeHost = (ApplicationLink)iterator.next();
            return activeHost;
        }
        try {
            return (ApplicationLink)Iterables.find(availableHosts, (Predicate)new Predicate<ApplicationLink>(){

                public boolean apply(ApplicationLink availableHost) {
                    return availableHost.isPrimary();
                }
            });
        }
        catch (NoSuchElementException e) {
            if (!availableHosts.isEmpty()) {
                return availableHosts.get(0);
            }
            return null;
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!this.ignoreXsrfCheck(req) && !this.xsrfTokenValidator.validateFormEncodedToken(req)) {
            resp.setStatus(403);
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put("parameterMap", req.getParameterMap());
            context.put("requestMethod", req.getMethod().toLowerCase());
            context.put("requestUrl", req.getRequestURL());
            context.put("writer", resp.getWriter());
            context.put("urlMode", UrlMode.RELATIVE);
            this.render("templates/xsrf.vm", context, req, resp);
            return;
        }
        String targetParam = req.getParameter("target");
        ServiceSelector.Target target = targetParam != null ? ServiceSelector.Target.valueOf(targetParam) : null;
        String hostParam = req.getParameter("host");
        ApplicationId host = hostParam != null ? new ApplicationId(hostParam) : null;
        this.serviceSelector.setTarget(target, host);
        if (this.serviceSelector.isHostAvailable() && target == ServiceSelector.Target.LOCAL) {
            this.timeoutService.setTimeout(NumberUtils.toInt((String)req.getParameter("pollingInterval"), (int)this.timeoutService.getTimeout()));
            this.timeoutService.setMaxTimeout(NumberUtils.toInt((String)req.getParameter("maxPollingInterval"), (int)this.timeoutService.getMaxTimeout()));
        }
        resp.sendRedirect(req.getRequestURL().toString());
    }

    private void render(String templateName, Map<String, Object> context, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        context.put("xsrfTokenName", this.xsrfTokenValidator.getXsrfParameterName());
        context.put("xsrfTokenValue", this.xsrfTokenAccessor.getXsrfToken(req, resp, true));
        this.templateRenderer.render(templateName, context, (Writer)resp.getWriter());
    }

    private boolean ignoreXsrfCheck(HttpServletRequest request) {
        return XSRF_OVERRIDE_HEADER_VALUE.equals(request.getHeader(XSRF_OVERRIDE_HEADER_NAME));
    }
}

