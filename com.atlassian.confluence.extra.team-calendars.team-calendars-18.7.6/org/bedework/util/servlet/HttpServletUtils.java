/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.util.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class HttpServletUtils {
    public HttpServletUtils() throws Exception {
        throw new Exception("Dont instantiate");
    }

    public static String getBrowserType(HttpServletRequest request) {
        if (request == null) {
            return "default";
        }
        String userAgent = String.valueOf(request.getHeader("User-Agent")).toLowerCase();
        if (userAgent.indexOf("aladdino") >= 0 || userAgent.indexOf("avantgo") >= 0 || userAgent.indexOf("docomo") >= 0 || userAgent.indexOf("Elaine") >= 0 || userAgent.indexOf("isilo") >= 0 || userAgent.indexOf("mazingo") >= 0 || userAgent.indexOf("mobipocket webcompanion") >= 0 || userAgent.indexOf("mobipocket+webcompanion") >= 0 || userAgent.indexOf("plucker") >= 0 || userAgent.indexOf("webwasher") >= 0) {
            return "PDA";
        }
        if (userAgent.indexOf("opera") >= 0) {
            return "Opera";
        }
        if (userAgent.indexOf("msie") >= 0) {
            return "MSIE";
        }
        if (userAgent.indexOf("netscape6") >= 0) {
            return "Netscape6";
        }
        if (userAgent.indexOf("gecko") >= 0) {
            return "Mozilla";
        }
        if (userAgent.indexOf("mozilla/4") >= 0 && userAgent.indexOf("spoofer") == -1 && userAgent.indexOf("webtv") == -1) {
            return "Netscape4";
        }
        return "default";
    }

    public static String getReqLine(HttpServletRequest req) {
        StringBuffer ret = new StringBuffer(HttpServletUtils.getUrl(req));
        String query = req.getQueryString();
        if (query != null) {
            ret.append("?").append(query);
        }
        return ret.toString();
    }

    public static String getUrl(HttpServletRequest request) {
        try {
            StringBuffer sb = request.getRequestURL();
            if (sb != null) {
                return sb.toString();
            }
            return request.getRequestURI();
        }
        catch (Throwable t) {
            Logger.getLogger(HttpServletUtils.class).warn("Unable to get url from " + request, t);
            return "BogusURL.this.is.probably.a.portal";
        }
    }

    public static String getURLshp(HttpServletRequest request) {
        String scheme = request.getScheme();
        StringBuilder sb = new StringBuilder(scheme);
        sb.append("://");
        sb.append(request.getServerName());
        int port = request.getServerPort();
        if ("http".equalsIgnoreCase(scheme) && port == 80) {
            return sb.toString();
        }
        if ("https".equalsIgnoreCase(scheme) && port == 443) {
            return sb.toString();
        }
        sb.append(":");
        sb.append(port);
        return sb.toString();
    }

    public static String getURLPrefix(HttpServletRequest request) {
        return HttpServletUtils.getURLshp(request) + HttpServletUtils.getContext(request);
    }

    public static String getContext(HttpServletRequest request) {
        String context = request.getContextPath();
        if (context == null || context.equals(".")) {
            context = "";
        }
        return context;
    }

    public static String getHeaders(HttpServletRequest req) {
        Enumeration en = req.getHeaderNames();
        StringBuffer sb = new StringBuffer();
        while (en.hasMoreElements()) {
            String name = (String)en.nextElement();
            sb.append(name);
            sb.append(": ");
            sb.append(req.getHeader(name));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void dumpHeaders(HttpServletRequest req, Logger log) {
        Enumeration en = req.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = (String)en.nextElement();
            log.debug(name + ": " + req.getHeader(name));
        }
    }

    public static Collection<Locale> getLocales(HttpServletRequest req) {
        if (req.getHeader("Accept-Language") == null) {
            return null;
        }
        Enumeration lcs = req.getLocales();
        ArrayList<Locale> locales = new ArrayList<Locale>();
        while (lcs.hasMoreElements()) {
            locales.add((Locale)lcs.nextElement());
        }
        return locales;
    }
}

