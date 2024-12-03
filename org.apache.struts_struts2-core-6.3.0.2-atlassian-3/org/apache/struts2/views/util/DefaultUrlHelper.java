/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.util;

import com.opensymphony.xwork2.inject.Inject;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.url.QueryStringBuilder;
import org.apache.struts2.views.util.UrlHelper;

public class DefaultUrlHelper
implements UrlHelper {
    private static final Logger LOG = LogManager.getLogger(DefaultUrlHelper.class);
    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";
    private int httpPort = 80;
    private int httpsPort = 443;
    private QueryStringBuilder queryStringBuilder;

    @Inject(value="struts.url.http.port")
    public void setHttpPort(String httpPort) {
        this.httpPort = Integer.parseInt(httpPort);
    }

    @Inject(value="struts.url.https.port")
    public void setHttpsPort(String httpsPort) {
        this.httpsPort = Integer.parseInt(httpsPort);
    }

    @Inject
    public void setQueryStringBuilder(QueryStringBuilder builder) {
        this.queryStringBuilder = builder;
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
        return this.buildUrl(action, request, response, params, null, true, true);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult) {
        return this.buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
        return this.buildUrl(action, request, response, params, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, true);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String urlScheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        StringBuilder link = new StringBuilder();
        boolean changedScheme = false;
        String scheme = null;
        if (this.isValidScheme(urlScheme)) {
            scheme = urlScheme;
        }
        if (forceAddSchemeHostAndPort) {
            String reqScheme = request.getScheme();
            changedScheme = true;
            link.append(scheme != null ? scheme : reqScheme);
            link.append("://");
            link.append(request.getServerName());
            if (scheme != null) {
                if (!scheme.equals(reqScheme)) {
                    this.appendPort(link, scheme, HTTP_PROTOCOL.equals(scheme) ? this.httpPort : this.httpsPort);
                } else {
                    this.appendPort(link, scheme, request.getServerPort());
                }
            } else {
                this.appendPort(link, reqScheme, request.getServerPort());
            }
        } else if (scheme != null && !scheme.equals(request.getScheme())) {
            changedScheme = true;
            link.append(scheme);
            link.append("://");
            link.append(request.getServerName());
            this.appendPort(link, scheme, HTTP_PROTOCOL.equals(scheme) ? this.httpPort : this.httpsPort);
        }
        if (action != null) {
            if (action.startsWith("/") && includeContext) {
                String contextPath = request.getContextPath();
                if (!contextPath.equals("/")) {
                    link.append(contextPath);
                }
            } else if (changedScheme) {
                String uri = (String)request.getAttribute("javax.servlet.forward.request_uri");
                if (uri == null) {
                    uri = request.getRequestURI();
                }
                link.append(uri, 0, uri.lastIndexOf(47) + 1);
            }
            link.append(action);
        } else {
            String requestURI = (String)request.getAttribute("struts.request_uri");
            if (requestURI == null) {
                requestURI = (String)request.getAttribute("javax.servlet.forward.request_uri");
            }
            if (requestURI == null) {
                requestURI = request.getRequestURI();
            }
            link.append(requestURI);
        }
        if (escapeAmp) {
            this.queryStringBuilder.build(params, link, "&amp;");
        } else {
            this.queryStringBuilder.build(params, link, "&");
        }
        String result = link.toString();
        if (StringUtils.containsIgnoreCase((CharSequence)result, (CharSequence)"<script")) {
            result = StringEscapeUtils.escapeEcmaScript((String)result);
        }
        try {
            result = encodeResult ? response.encodeURL(result) : result;
        }
        catch (Exception ex) {
            LOG.debug("Could not encode the URL for some reason, use it unchanged", (Throwable)ex);
            result = link.toString();
        }
        return result;
    }

    private void appendPort(StringBuilder link, String scheme, int port) {
        if (HTTP_PROTOCOL.equals(scheme) && port != 80 || HTTPS_PROTOCOL.equals(scheme) && port != 443) {
            link.append(":");
            link.append(port);
        }
    }

    protected boolean isValidScheme(String scheme) {
        return HTTP_PROTOCOL.equals(scheme) || HTTPS_PROTOCOL.equals(scheme);
    }
}

