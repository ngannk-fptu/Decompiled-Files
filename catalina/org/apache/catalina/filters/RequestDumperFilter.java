/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class RequestDumperFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    private static final String NON_HTTP_REQ_MSG = "Not available. Non-http request.";
    private static final String NON_HTTP_RES_MSG = "Not available. Non-http response.";
    private static final ThreadLocal<Timestamp> timestamp = ThreadLocal.withInitial(() -> new Timestamp());
    private transient Log log = LogFactory.getLog(RequestDumperFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = null;
        HttpServletResponse hResponse = null;
        if (request instanceof HttpServletRequest) {
            hRequest = (HttpServletRequest)request;
        }
        if (response instanceof HttpServletResponse) {
            hResponse = (HttpServletResponse)response;
        }
        this.doLog("START TIME        ", this.getTimestamp());
        if (hRequest == null) {
            this.doLog("        requestURI", NON_HTTP_REQ_MSG);
            this.doLog("          authType", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("        requestURI", hRequest.getRequestURI());
            this.doLog("          authType", hRequest.getAuthType());
        }
        this.doLog(" characterEncoding", request.getCharacterEncoding());
        this.doLog("     contentLength", Long.toString(request.getContentLengthLong()));
        this.doLog("       contentType", request.getContentType());
        if (hRequest == null) {
            this.doLog("       contextPath", NON_HTTP_REQ_MSG);
            this.doLog("            cookie", NON_HTTP_REQ_MSG);
            this.doLog("            header", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("       contextPath", hRequest.getContextPath());
            Cookie[] cookies = hRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    this.doLog("            cookie", cookie.getName() + "=" + cookie.getValue());
                }
            }
            Enumeration hnames = hRequest.getHeaderNames();
            while (hnames.hasMoreElements()) {
                String hname = (String)hnames.nextElement();
                Enumeration hvalues = hRequest.getHeaders(hname);
                while (hvalues.hasMoreElements()) {
                    String hvalue = (String)hvalues.nextElement();
                    this.doLog("            header", hname + "=" + hvalue);
                }
            }
        }
        this.doLog("            locale", request.getLocale().toString());
        if (hRequest == null) {
            this.doLog("            method", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("            method", hRequest.getMethod());
        }
        Enumeration pnames = request.getParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = (String)pnames.nextElement();
            String[] pvalues = request.getParameterValues(pname);
            StringBuilder result = new StringBuilder(pname);
            result.append('=');
            for (int i = 0; i < pvalues.length; ++i) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(pvalues[i]);
            }
            this.doLog("         parameter", result.toString());
        }
        if (hRequest == null) {
            this.doLog("          pathInfo", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("          pathInfo", hRequest.getPathInfo());
        }
        this.doLog("          protocol", request.getProtocol());
        if (hRequest == null) {
            this.doLog("       queryString", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("       queryString", hRequest.getQueryString());
        }
        this.doLog("        remoteAddr", request.getRemoteAddr());
        this.doLog("        remoteHost", request.getRemoteHost());
        if (hRequest == null) {
            this.doLog("        remoteUser", NON_HTTP_REQ_MSG);
            this.doLog("requestedSessionId", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("        remoteUser", hRequest.getRemoteUser());
            this.doLog("requestedSessionId", hRequest.getRequestedSessionId());
        }
        this.doLog("            scheme", request.getScheme());
        this.doLog("        serverName", request.getServerName());
        this.doLog("        serverPort", Integer.toString(request.getServerPort()));
        if (hRequest == null) {
            this.doLog("       servletPath", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("       servletPath", hRequest.getServletPath());
        }
        this.doLog("          isSecure", Boolean.valueOf(request.isSecure()).toString());
        this.doLog("------------------", "--------------------------------------------");
        chain.doFilter(request, response);
        this.doLog("------------------", "--------------------------------------------");
        if (hRequest == null) {
            this.doLog("          authType", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("          authType", hRequest.getAuthType());
        }
        this.doLog("       contentType", response.getContentType());
        if (hResponse == null) {
            this.doLog("            header", NON_HTTP_RES_MSG);
        } else {
            Collection rhnames = hResponse.getHeaderNames();
            for (String rhname : rhnames) {
                Collection rhvalues = hResponse.getHeaders(rhname);
                for (String rhvalue : rhvalues) {
                    this.doLog("            header", rhname + "=" + rhvalue);
                }
            }
        }
        if (hRequest == null) {
            this.doLog("        remoteUser", NON_HTTP_REQ_MSG);
        } else {
            this.doLog("        remoteUser", hRequest.getRemoteUser());
        }
        if (hResponse == null) {
            this.doLog("            status", NON_HTTP_RES_MSG);
        } else {
            this.doLog("            status", Integer.toString(hResponse.getStatus()));
        }
        this.doLog("END TIME          ", this.getTimestamp());
        this.doLog("==================", "============================================");
    }

    private void doLog(String attribute, String value) {
        StringBuilder sb = new StringBuilder(80);
        sb.append(Thread.currentThread().getName());
        sb.append(' ');
        sb.append(attribute);
        sb.append('=');
        sb.append(value);
        this.log.info((Object)sb.toString());
    }

    private String getTimestamp() {
        Timestamp ts = timestamp.get();
        long currentTime = System.currentTimeMillis();
        if (ts.date.getTime() + 999L < currentTime) {
            ts.date.setTime(currentTime - currentTime % 1000L);
            ts.update();
        }
        return ts.dateString;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(RequestDumperFilter.class);
    }

    private static final class Timestamp {
        private final Date date = new Date(0L);
        private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        private String dateString = this.format.format(this.date);

        private Timestamp() {
        }

        private void update() {
            this.dateString = this.format.format(this.date);
        }
    }
}

