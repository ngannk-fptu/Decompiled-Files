/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.util;

import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public class RedirectUtils {
    private static final String HTTP_BASIC_AUTH_HEADER = "Authorization";
    private static final Pattern PATTERN_LEADING_SLASH = Pattern.compile("^(?:[\\/]+)(.*)$");
    private static final Pattern PATTERN_LEADING_DOUBLE_SLASH = Pattern.compile("^([\\/]{2,})(.*)");

    public static String getLoginUrl(HttpServletRequest request) {
        SecurityConfig securityConfig = SecurityConfigFactory.getInstance();
        String loginURL = securityConfig.getLoginURL();
        return RedirectUtils.getLoginURL(loginURL, request);
    }

    public static String getLinkLoginURL(HttpServletRequest request) {
        SecurityConfig securityConfig = SecurityConfigFactory.getInstance();
        String loginURL = securityConfig.getLinkLoginURL();
        return RedirectUtils.getLoginURL(loginURL, request);
    }

    public static String getLoginURL(String loginURL, HttpServletRequest request) {
        boolean externalLoginLink = RedirectUtils.isExternalLoginLink(loginURL);
        loginURL = RedirectUtils.replaceOriginalURL(loginURL, request, externalLoginLink);
        if (externalLoginLink) {
            return loginURL;
        }
        return request.getContextPath() + loginURL;
    }

    private static boolean isExternalLoginLink(String loginURL) {
        return loginURL.indexOf("://") != -1;
    }

    private static String replaceOriginalURL(String loginURL, HttpServletRequest request, boolean external) {
        int i = loginURL.indexOf("${originalurl}");
        if (i != -1) {
            String originalURL = RedirectUtils.getOriginalURL(request, external);
            String osDest = request.getParameter("os_destination");
            return loginURL.substring(0, i) + (osDest != null ? RedirectUtils.encodeUrl(osDest) : RedirectUtils.encodeUrl(originalURL)) + loginURL.substring(i + "${originalurl}".length());
        }
        return loginURL;
    }

    private static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
    }

    private static String getOriginalURL(HttpServletRequest request, boolean external) {
        String originalURL = (String)request.getAttribute("atlassian.core.seraph.original.url");
        if (originalURL != null) {
            if (external) {
                return RedirectUtils.getServerNameAndPath(request) + originalURL;
            }
            return originalURL;
        }
        if (external) {
            return request.getRequestURL() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
        }
        return request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo()) + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }

    public static String getServerNameAndPath(HttpServletRequest request) {
        return RedirectUtils.getServerNameAndPath(request, false);
    }

    private static String getServerNameAndPath(HttpServletRequest request, boolean showDefaultPortNumber) {
        StringBuffer buf = new StringBuffer();
        buf.append(request.getScheme()).append("://").append(IDN.toASCII(request.getServerName()));
        if (showDefaultPortNumber || "http".equals(request.getScheme()) && request.getServerPort() != 80 || "https".equals(request.getScheme()) && request.getServerPort() != 443) {
            buf.append(":").append(request.getServerPort());
        }
        buf.append(request.getContextPath());
        return buf.toString();
    }

    public static boolean isBasicAuthentication(HttpServletRequest request, String basicAuthParameterName) {
        return RedirectUtils.hasHttpBasicAuthenticationRequestParameter(request, basicAuthParameterName) || RedirectUtils.hasHttpBasicAuthenticationRequestHeader(request);
    }

    static boolean hasHttpBasicAuthenticationRequestHeader(HttpServletRequest request) {
        return RedirectUtils.containsIgnoreCase(request.getHeader(HTTP_BASIC_AUTH_HEADER), "BASIC");
    }

    static boolean hasHttpBasicAuthenticationRequestParameter(HttpServletRequest request, String basicAuthParameterName) {
        String queryString = request.getQueryString();
        queryString = queryString == null ? "&&" : "&" + queryString + "&";
        return queryString.indexOf("&" + basicAuthParameterName + "=" + "BASIC".toLowerCase() + "&") != -1;
    }

    public static String appendPathToContext(String context, String path) {
        Matcher matcher;
        String pathToAppend;
        if (context == null) {
            context = "";
        }
        if (path == null) {
            return context;
        }
        if (PATTERN_LEADING_DOUBLE_SLASH.matcher(path).matches()) {
            return context;
        }
        try {
            URI pathUri = new URI(path);
            if (pathUri.getHost() != null) {
                return context;
            }
        }
        catch (URISyntaxException e) {
            return context;
        }
        StringBuffer result = new StringBuffer(context);
        if (!context.endsWith("/")) {
            result.append("/");
        }
        if ((pathToAppend = path).startsWith("/") && (matcher = PATTERN_LEADING_SLASH.matcher(pathToAppend)).matches()) {
            pathToAppend = matcher.group(1);
        }
        result.append(pathToAppend);
        return result.toString();
    }

    static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return RedirectUtils.contains(str.toUpperCase(), searchStr.toUpperCase());
    }

    static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    public static boolean sameContext(String url, HttpServletRequest request) {
        String context = RedirectUtils.getServerNameAndPath(request, false);
        if (RedirectUtils.sameContext(url, context)) {
            return true;
        }
        context = RedirectUtils.getServerNameAndPath(request, true);
        return RedirectUtils.sameContext(url, context);
    }

    private static boolean sameContext(String url, String requestContext) {
        if (url.equals(requestContext)) {
            return true;
        }
        if (!requestContext.endsWith("/")) {
            requestContext = requestContext + '/';
        }
        return url.startsWith(requestContext);
    }
}

