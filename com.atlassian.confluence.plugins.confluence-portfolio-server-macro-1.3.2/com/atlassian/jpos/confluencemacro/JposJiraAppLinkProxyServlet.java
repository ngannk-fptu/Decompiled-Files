/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.jpos.confluencemacro;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.jpos.confluencemacro.PlanHtmlTransform;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class JposJiraAppLinkProxyServlet
extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(JposJiraAppLinkProxyServlet.class);
    private static final String APP_ID = "appId";
    private static final String PATH = "path";
    private static final Set<String> RESERVED_PARAMETERS = new HashSet<String>(Arrays.asList("path", "appId"));
    private static final Set<String> HEADER_ALLOW_LIST = new HashSet<String>(Arrays.asList("Content-Type", "Cache-Control", "Pragma"));
    private static final List<Pattern> PLAN_LINK_OUTS = Arrays.asList(Pattern.compile("^/secure/PortfolioPlanView.jspa\\?id=[0-9]+(&sid=[0-9]+)?$"), Pattern.compile("^/secure/PortfolioRoadmapConfluence.jspa\\?r=[a-zA-Z0-9]{0,30}$"), Pattern.compile("^/secure/RapidBoard.jspa\\?rapidView=[0-9]+(&view=(planning|reporting))?(&chart=(velocityChart|sprintRetrospective))?(&sprint=[0-9]+)?$"), Pattern.compile("^/browse/[A-Z][0-9_a-zA-Z]+-[0-9]+$"), Pattern.compile("^/browse/[A-Z][0-9_a-zA-Z]+/fixforversion/[0-9]+$"));
    private static final String PLAN_URL_PROXY = "/PortfolioRoadmapConfluence";
    private static final String PLAN_URL_TARGET = "/secure/PortfolioRoadmapConfluence.jspa?r=";
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final I18nResolver i18nResolver;
    private final PermissionManager permissionManager;
    private final ApplicationProperties applicationProperties;

    public JposJiraAppLinkProxyServlet(ReadOnlyApplicationLinkService readOnlyApplicationLinkService, I18nResolver i18nResolver, PermissionManager permissionManager, ApplicationProperties applicationProperties) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.i18nResolver = i18nResolver;
        this.permissionManager = permissionManager;
        this.applicationProperties = applicationProperties;
    }

    private static ApplicationLinkRequest prepareRequest(HttpServletRequest req, Request.MethodType methodType, String url, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, IOException {
        String contentTypeHeader;
        ApplicationLinkRequest request = requestFactory.createRequest(methodType, url);
        request.setHeader("X-Atlassian-Token", "no-check");
        if ((methodType == Request.MethodType.POST || methodType == Request.MethodType.PUT) && (contentTypeHeader = req.getHeader("Content-Type")) != null) {
            request.setHeader("Content-Type", contentTypeHeader);
            if (contentTypeHeader.contains("application/json")) {
                String encoding = (String)StringUtils.defaultIfEmpty((CharSequence)req.getCharacterEncoding(), (CharSequence)"ISO8859_1");
                request.setRequestBody(IOUtils.toString((InputStream)req.getInputStream(), (String)encoding));
            }
        }
        return request;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doProxy(req, resp, Request.MethodType.GET);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doProxy(req, resp, Request.MethodType.POST);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doProxy(req, resp, Request.MethodType.PUT);
    }

    private void doProxy(HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType) throws IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, this.notPermittedError());
            return;
        }
        this.doProxyWithAppLink(resp, req, methodType);
    }

    private String notPermittedError() {
        return this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-portfolio-server-macro.portfolio-for-jira-plan.applink.notpermitted.error");
    }

    private void doProxyWithAppLink(HttpServletResponse httpResponse, HttpServletRequest httpRequest, Request.MethodType methodType) throws IOException {
        ApplicationId applicationId = this.parseApplicationId(httpRequest.getParameter(APP_ID));
        ReadOnlyApplicationLink appLink = this.readOnlyApplicationLinkService.getApplicationLink(applicationId);
        if (appLink == null) {
            httpResponse.sendError(403, this.notPermittedError());
            return;
        }
        String path = StringUtils.defaultString((String)httpRequest.getParameter(PATH));
        boolean isProxyPlanPage = httpRequest.getServletPath().endsWith(PLAN_URL_PROXY);
        String targetUrl = isProxyPlanPage ? this.getPlanPageUrl(httpRequest) : this.getProxyTargetUrl(httpRequest, methodType, path);
        Optional<String> planLinkOut = this.isLinkOutFromPlan(path);
        if (planLinkOut.isPresent()) {
            httpResponse.sendRedirect(appLink.getRpcUrl().toString() + planLinkOut.get());
            return;
        }
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
            ApplicationLinkRequest request = JposJiraAppLinkProxyServlet.prepareRequest(httpRequest, methodType, targetUrl, requestFactory);
            request.setFollowRedirects(false);
            ProxyApplicationLinkResponseHandler responseHandler = isProxyPlanPage ? new PageTransformingResponseHandler(httpRequest, requestFactory, httpResponse, new PlanHtmlTransform(appLink, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL))) : new ProxyApplicationLinkResponseHandler(httpRequest, requestFactory, httpResponse);
            request.execute((ApplicationLinkResponseHandler)responseHandler);
        }
        catch (ResponseException re) {
            String finalUrl = appLink.getRpcUrl() + targetUrl;
            this.handleProxyingException(finalUrl, httpResponse, (Exception)((Object)re));
        }
        catch (CredentialsRequiredException e) {
            this.handleCredentialsRequiredException(appLink, httpRequest, httpResponse, methodType, targetUrl, e.getAuthorisationURI().toString());
        }
    }

    Optional<String> isLinkOutFromPlan(String url) {
        return PLAN_LINK_OUTS.stream().map(pattern -> pattern.matcher(url)).filter(Matcher::find).map(Matcher::group).findAny();
    }

    private String getProxyTargetUrl(HttpServletRequest httpRequest, Request.MethodType methodType, String path) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString((String)path);
        if (methodType == Request.MethodType.GET) {
            httpRequest.getParameterMap().forEach((name, values) -> {
                if (!RESERVED_PARAMETERS.contains(name)) {
                    uriComponentsBuilder.queryParam(name, (Object[])values);
                }
            });
        }
        return uriComponentsBuilder.build(true).toUriString();
    }

    private String getPlanPageUrl(HttpServletRequest httpRequest) {
        return PLAN_URL_TARGET + httpRequest.getParameter("r");
    }

    private void handleCredentialsRequiredException(ReadOnlyApplicationLink appLink, HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType, String url, String authorisationURI) {
        resp.setStatus(401);
        resp.setHeader("WWW-Authenticate", "OAuth realm=\"" + authorisationURI + "\"");
        this.requestByAnonymousUser(appLink, req, resp, methodType, url);
    }

    private void requestByAnonymousUser(ReadOnlyApplicationLink appLink, HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType, String url) {
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            ApplicationLinkRequest request = JposJiraAppLinkProxyServlet.prepareRequest(req, methodType, url, requestFactory);
            request.setFollowRedirects(false);
            request.execute((ApplicationLinkResponseHandler)new ProxyApplicationLinkResponseHandler(req, requestFactory, resp));
        }
        catch (Exception e) {
            LOGGER.error("Can not retrieve data from jira servers by anonymous user", (Throwable)e);
        }
    }

    private void handleProxyingException(String finalUrl, HttpServletResponse resp, Exception e) throws IOException {
        String errorMsg = "There was an error proxying your request to " + finalUrl + " because of " + e.getMessage();
        resp.sendError(504, errorMsg);
    }

    private ApplicationId parseApplicationId(String applicationId) {
        try {
            return new ApplicationId(applicationId);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static class ProxyApplicationLinkResponseHandler
    implements ApplicationLinkResponseHandler<Object> {
        protected final HttpServletRequest req;
        protected final ApplicationLinkRequestFactory requestFactory;
        protected final HttpServletResponse resp;

        ProxyApplicationLinkResponseHandler(HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp) {
            this.req = req;
            this.requestFactory = requestFactory;
            this.resp = resp;
        }

        public Object handle(Response response) throws ResponseException {
            if (response.isSuccessful()) {
                if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
                    return this.retryRequest(response);
                }
                return this.processSuccess(response);
            }
            try {
                this.resp.sendError(response.getStatusCode(), response.getStatusText());
            }
            catch (IOException e) {
                throw new ResponseException((Throwable)e);
            }
            return null;
        }

        public Object credentialsRequired(Response response) {
            this.resp.setStatus(401);
            this.resp.setHeader("WWW-Authenticate", "OAuth realm=\"" + this.requestFactory.getAuthorisationURI().toString() + "\"");
            return null;
        }

        protected Object processSuccess(Response response) throws ResponseException {
            InputStream responseStream = response.getResponseBodyAsStream();
            Map headers = response.getHeaders();
            headers.keySet().stream().filter(HEADER_ALLOW_LIST::contains).forEach(key -> this.resp.setHeader(key, (String)headers.get(key)));
            try {
                if (responseStream != null) {
                    ServletOutputStream outputStream = this.resp.getOutputStream();
                    IOUtils.copy((InputStream)responseStream, (OutputStream)outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
            catch (IOException e) {
                throw new ResponseException((Throwable)e);
            }
            return null;
        }

        protected Object retryRequest(Response response) throws ResponseException {
            try {
                ApplicationLinkRequest request = JposJiraAppLinkProxyServlet.prepareRequest(this.req, Request.MethodType.GET, response.getHeader("location"), this.requestFactory);
                request.setFollowRedirects(false);
                return request.execute((ApplicationLinkResponseHandler)this);
            }
            catch (CredentialsRequiredException | IOException e) {
                throw new ResponseException(e);
            }
        }
    }

    private static class PageTransformingResponseHandler
    extends ProxyApplicationLinkResponseHandler {
        private final PlanHtmlTransform planHtmlTransform;

        public PageTransformingResponseHandler(HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp, PlanHtmlTransform planHtmlTransform) {
            super(req, requestFactory, resp);
            this.planHtmlTransform = planHtmlTransform;
        }

        @Override
        public Object handle(Response response) throws ResponseException {
            if (response.isSuccessful()) {
                if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
                    return this.retryRequest(response);
                }
                InputStream responseStream = response.getResponseBodyAsStream();
                Map headers = response.getHeaders();
                headers.keySet().stream().filter(HEADER_ALLOW_LIST::contains).forEach(key -> this.resp.setHeader(key, (String)headers.get(key)));
                try {
                    if (responseStream != null) {
                        String planHtml = IOUtils.toString((InputStream)responseStream);
                        String transformed = this.planHtmlTransform.apply(planHtml);
                        ServletOutputStream outputStream = this.resp.getOutputStream();
                        IOUtils.copy((Reader)new StringReader(transformed), (OutputStream)outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                }
                catch (IOException e) {
                    throw new ResponseException((Throwable)e);
                }
                return null;
            }
            try {
                this.resp.sendError(response.getStatusCode(), response.getStatusText());
            }
            catch (IOException e) {
                throw new ResponseException((Throwable)e);
            }
            return null;
        }
    }
}

