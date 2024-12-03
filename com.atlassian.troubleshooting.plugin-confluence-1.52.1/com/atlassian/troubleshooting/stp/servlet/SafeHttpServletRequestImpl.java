/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.Part
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.troubleshooting.stp.servlet;

import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.apache.commons.lang3.StringEscapeUtils;

public class SafeHttpServletRequestImpl
implements SafeHttpServletRequest {
    private final HttpServletRequest request;

    public SafeHttpServletRequestImpl(HttpServletRequest request) {
        this.request = request;
    }

    public String getAuthType() {
        return this.request.getAuthType();
    }

    public Cookie[] getCookies() {
        return this.request.getCookies();
    }

    public long getDateHeader(String name) {
        return this.request.getDateHeader(name);
    }

    public String getHeader(String name) {
        return this.request.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name) {
        return this.request.getHeaders(name);
    }

    public Enumeration<String> getHeaderNames() {
        return this.request.getHeaderNames();
    }

    public int getIntHeader(String name) {
        return this.request.getIntHeader(name);
    }

    public String getMethod() {
        return this.request.getMethod();
    }

    public String getPathInfo() {
        return this.request.getPathInfo();
    }

    public String getPathTranslated() {
        return this.request.getPathTranslated();
    }

    public String getContextPath() {
        return this.request.getContextPath();
    }

    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.request.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(env);
    }

    public int getContentLength() {
        return this.request.getContentLength();
    }

    public long getContentLengthLong() {
        return this.request.getContentLengthLong();
    }

    public String getContentType() {
        return this.request.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }

    public Enumeration<String> getParameterNames() {
        return this.request.getParameterNames();
    }

    public String getProtocol() {
        return this.request.getProtocol();
    }

    public String getScheme() {
        return this.request.getScheme();
    }

    public String getServerName() {
        return this.request.getServerName();
    }

    public String getQueryString() {
        return this.request.getQueryString();
    }

    public int getServerPort() {
        return this.request.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        return this.request.getReader();
    }

    public String getRemoteUser() {
        return this.request.getRemoteUser();
    }

    public boolean isUserInRole(String role) {
        return this.request.isUserInRole(role);
    }

    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }

    public Principal getUserPrincipal() {
        return this.request.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        return this.request.getRequestedSessionId();
    }

    public void setAttribute(String name, Object o) {
        this.request.setAttribute(name, o);
    }

    public String getRequestURI() {
        return this.request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return this.request.getRequestURL();
    }

    public String getServletPath() {
        return this.request.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        return this.request.getSession(create);
    }

    public HttpSession getSession() {
        return this.request.getSession();
    }

    public String changeSessionId() {
        return this.request.changeSessionId();
    }

    public boolean isRequestedSessionIdValid() {
        return this.request.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return this.request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return this.request.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return this.request.isRequestedSessionIdFromUrl();
    }

    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return this.request.authenticate(httpServletResponse);
    }

    public void login(String s, String s1) throws ServletException {
        this.request.login(s, s1);
    }

    public void logout() throws ServletException {
        this.request.logout();
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return this.request.getParts();
    }

    public Part getPart(String s) throws IOException, ServletException {
        return this.request.getPart(s);
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return (T)this.request.upgrade(aClass);
    }

    public void removeAttribute(String name) {
        this.request.removeAttribute(name);
    }

    public Locale getLocale() {
        return this.request.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return this.request.getLocales();
    }

    public boolean isSecure() {
        return this.request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return this.request.getRequestDispatcher(path);
    }

    public String getRealPath(String path) {
        return this.request.getRealPath(path);
    }

    public int getRemotePort() {
        return this.request.getRemotePort();
    }

    public String getLocalName() {
        return this.request.getLocalName();
    }

    public String getLocalAddr() {
        return this.request.getLocalAddr();
    }

    public int getLocalPort() {
        return this.request.getLocalPort();
    }

    public ServletContext getServletContext() {
        return this.request.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return this.request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return this.request.startAsync(servletRequest, servletResponse);
    }

    public boolean isAsyncStarted() {
        return this.request.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return this.request.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        return this.request.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        return this.request.getDispatcherType();
    }

    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml4((String)this.request.getParameter(name));
    }

    public String[] getParameterValues(String name) {
        String[] rawValues = this.request.getParameterValues(name);
        if (rawValues != null) {
            String[] cleanValues = new String[rawValues.length];
            for (int a = 0; a < rawValues.length; ++a) {
                cleanValues[a] = StringEscapeUtils.escapeHtml4((String)rawValues[a]);
            }
            return cleanValues;
        }
        return null;
    }

    public Map<String, String[]> getParameterMap() {
        Map rawMap = this.request.getParameterMap();
        if (rawMap == null) {
            return null;
        }
        HashMap<String, String[]> cleanMap = new HashMap<String, String[]>();
        for (String key : rawMap.keySet()) {
            cleanMap.put(key, (String[])rawMap.get(key));
        }
        return cleanMap;
    }
}

