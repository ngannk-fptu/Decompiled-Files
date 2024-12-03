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
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.confluence.extra.jira.handlers.AbstractProxyResponseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProxyServlet
extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProxyServlet.class);
    private static final String APP_TYPE = "appType";
    private static final String APP_ID = "appId";
    private static final String JSON_STRING = "jsonString";
    private static final String FORMAT_ERRORS = "formatErrors";
    private static final String PATH = "path";
    private static final Set<String> reservedParameters = new HashSet<String>(Arrays.asList("path", "jsonString", "appId", "appType", "formatErrors"));
    static Set<String> headerWhitelist = new HashSet<String>(Arrays.asList("Content-Type", "Cache-Control", "Pragma"));
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;

    public AbstractProxyServlet(ReadOnlyApplicationLinkService readOnlyApplicationLinkService) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
    }

    private static ApplicationLinkRequest prepareRequest(HttpServletRequest req, Request.MethodType methodType, String url, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, IOException {
        ApplicationLinkRequest request = requestFactory.createRequest(methodType, url);
        request.setHeader("X-Atlassian-Token", "no-check");
        if (methodType == Request.MethodType.POST) {
            String ctHeader = req.getHeader("Content-Type");
            if (ctHeader != null) {
                request.setHeader("Content-Type", ctHeader);
            }
            if (ctHeader != null && (ctHeader.contains("multipart/form-data") || ctHeader.contains("application/xml"))) {
                String enc = req.getCharacterEncoding();
                String str = IOUtils.toString((InputStream)req.getInputStream(), (String)(enc == null ? "ISO8859_1" : enc));
                request.setRequestBody(str);
            } else {
                ArrayList<String> params = new ArrayList<String>();
                Map parameters = req.getParameterMap();
                for (Object name : parameters.keySet()) {
                    if (reservedParameters.contains(name)) continue;
                    params.add(name.toString());
                    params.add(req.getParameter(name.toString()));
                }
                request.addRequestParameters(params.toArray(new String[0]));
            }
        }
        return request;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doProxy(req, resp, Request.MethodType.GET);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doProxy(req, resp, Request.MethodType.POST);
    }

    abstract void doProxy(HttpServletRequest var1, HttpServletResponse var2, Request.MethodType var3) throws IOException, ServletException;

    void doProxy(HttpServletResponse resp, HttpServletRequest req, Request.MethodType methodType, String url) throws IOException, ServletException {
        String appType;
        String appId = req.getParameter("serverId");
        if (appId == null) {
            appId = req.getParameter(APP_ID);
        }
        if ((appType = req.getParameter(APP_TYPE)) == null && appId == null) {
            appId = req.getHeader("X-AppId");
            appType = req.getHeader("X-AppType");
            if (appType == null && appId == null) {
                resp.sendError(400, "You must specify an appId or appType request parameter");
            }
        }
        ReadOnlyApplicationLink appLink = null;
        if (appId != null) {
            appLink = this.getApplicationLinkById(appId);
            if (appLink == null) {
                resp.sendError(404, "No Application Link found for the id " + StringEscapeUtils.escapeHtml4((String)appId));
            }
        } else if (appType != null) {
            try {
                appLink = this.getPrimaryAppLinkByType(appType);
                if (appLink == null) {
                    resp.sendError(404, "No Application Link found for the type " + StringEscapeUtils.escapeHtml4((String)appType));
                }
            }
            catch (ClassNotFoundException e) {
                throw new ServletException((Throwable)e);
            }
        }
        String finalUrl = appLink.getRpcUrl() + url;
        boolean formatErrors = Boolean.parseBoolean(req.getParameter(FORMAT_ERRORS));
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
            ApplicationLinkRequest request = AbstractProxyServlet.prepareRequest(req, methodType, url, requestFactory);
            request.setFollowRedirects(false);
            this.handleResponse(requestFactory, req, resp, request, appLink);
        }
        catch (ResponseException re) {
            this.handleProxyingException(formatErrors, finalUrl, resp, (Exception)((Object)re));
        }
        catch (CredentialsRequiredException e) {
            this.handleCredentialsRequiredException(appLink, req, resp, methodType, StringEscapeUtils.escapeHtml4((String)url), e.getAuthorisationURI().toString());
        }
    }

    protected void handleResponse(ApplicationLinkRequestFactory requestFactory, HttpServletRequest req, HttpServletResponse resp, ApplicationLinkRequest request, ReadOnlyApplicationLink appLink) throws ResponseException {
        ProxyApplicationLinkResponseHandler responseHandler = new ProxyApplicationLinkResponseHandler(req, requestFactory, resp);
        request.execute((ApplicationLinkResponseHandler)responseHandler);
    }

    protected void handleCredentialsRequiredException(ReadOnlyApplicationLink appLink, HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType, String url, String authorisationURI) {
        resp.setStatus(401);
        resp.setHeader("WWW-Authenticate", "OAuth realm=\"" + authorisationURI + "\"");
        this.requestByAnonymousUser(appLink, req, resp, methodType, url);
    }

    private void requestByAnonymousUser(ReadOnlyApplicationLink appLink, HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType, String url) {
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            ApplicationLinkRequest request = AbstractProxyServlet.prepareRequest(req, methodType, url, requestFactory);
            request.setFollowRedirects(false);
            this.handleResponse(requestFactory, req, resp, request, appLink);
        }
        catch (Exception e) {
            LOGGER.error("Can not retrieve data from jira servers by anonymous user", (Throwable)e);
        }
    }

    protected final void handleProxyingException(boolean format, String finalUrl, HttpServletResponse resp, Exception e) throws IOException {
        String errorMsg = "There was an error proxying your request to " + finalUrl + " because of " + e.getMessage();
        resp.sendError(504, errorMsg);
    }

    protected ReadOnlyApplicationLink getPrimaryAppLinkByType(String type) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(type);
        return this.readOnlyApplicationLinkService.getPrimaryApplicationLink(clazz);
    }

    protected ReadOnlyApplicationLink getApplicationLinkById(String id) {
        return this.readOnlyApplicationLinkService.getApplicationLink(new ApplicationId(id));
    }

    protected static class ProxyApplicationLinkResponseHandler
    extends AbstractProxyResponseHandler {
        ProxyApplicationLinkResponseHandler(HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp) {
            super(req, requestFactory, resp);
        }

        @Override
        protected Object processSuccess(Response response) throws ResponseException {
            InputStream responseStream = response.getResponseBodyAsStream();
            Map headers = response.getHeaders();
            headers.keySet().stream().filter(key -> headerWhitelist.contains(key)).forEach(key -> this.resp.setHeader(key, (String)headers.get(key)));
            try {
                if (responseStream != null) {
                    ServletOutputStream outputStream = this.resp.getOutputStream();
                    IOUtils.copy((InputStream)responseStream, (OutputStream)outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected Object retryRequest(Response response) throws ResponseException {
            try {
                ApplicationLinkRequest request = AbstractProxyServlet.prepareRequest(this.req, Request.MethodType.GET, response.getHeader("location"), this.requestFactory);
                request.setFollowRedirects(false);
                return request.execute((ApplicationLinkResponseHandler)this);
            }
            catch (CredentialsRequiredException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

