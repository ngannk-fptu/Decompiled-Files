/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.onelogin.saml2.servlet;

import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
    private ServletUtils() {
    }

    public static HttpRequest makeHttpRequest(HttpServletRequest req) {
        Map paramsAsArray = req.getParameterMap();
        HashMap<String, List<String>> paramsAsList = new HashMap<String, List<String>>();
        for (Map.Entry param : paramsAsArray.entrySet()) {
            paramsAsList.put((String)param.getKey(), (List<String>)Arrays.asList((Object[])param.getValue()));
        }
        return new HttpRequest(req.getRequestURL().toString(), paramsAsList, req.getQueryString());
    }

    public static String getSelfURLhost(HttpServletRequest request) {
        String hostUrl = "";
        int serverPort = request.getServerPort();
        hostUrl = serverPort == 80 || serverPort == 443 || serverPort == 0 ? String.format("%s://%s", request.getScheme(), request.getServerName()) : String.format("%s://%s:%s", request.getScheme(), request.getServerName(), serverPort);
        return hostUrl;
    }

    public static String getSelfHost(HttpServletRequest request) {
        return request.getServerName();
    }

    public static boolean isHTTPS(HttpServletRequest request) {
        return request.isSecure();
    }

    public static String getSelfURL(HttpServletRequest request) {
        String url = ServletUtils.getSelfURLhost(request);
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (null != requestUri && !requestUri.isEmpty()) {
            url = url + requestUri;
        }
        if (null != queryString && !queryString.isEmpty()) {
            url = url + '?' + queryString;
        }
        return url;
    }

    public static String getSelfURLNoQuery(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public static String getSelfRoutedURLNoQuery(HttpServletRequest request) {
        String url = ServletUtils.getSelfURLhost(request);
        String requestUri = request.getRequestURI();
        if (null != requestUri && !requestUri.isEmpty()) {
            url = url + requestUri;
        }
        return url;
    }

    public static String sendRedirect(HttpServletResponse response, String location, Map<String, String> parameters, Boolean stay) throws IOException {
        String target = location;
        if (!parameters.isEmpty()) {
            boolean first = !location.contains("?");
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                if (first) {
                    target = target + "?";
                    first = false;
                } else {
                    target = target + "&";
                }
                target = target + parameter.getKey();
                if (parameter.getValue().isEmpty()) continue;
                target = target + "=" + Util.urlEncoder(parameter.getValue());
            }
        }
        if (!stay.booleanValue()) {
            response.sendRedirect(target);
        }
        return target;
    }

    public static void sendRedirect(HttpServletResponse response, String location, Map<String, String> parameters) throws IOException {
        ServletUtils.sendRedirect(response, location, parameters, false);
    }

    public static void sendRedirect(HttpServletResponse response, String location) throws IOException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        ServletUtils.sendRedirect(response, location, parameters);
    }
}

