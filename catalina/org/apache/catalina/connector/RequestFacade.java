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
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.Part
 *  javax.servlet.http.PushBuilder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
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
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Request;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

public class RequestFacade
implements HttpServletRequest {
    private static final StringManager sm = StringManager.getManager(RequestFacade.class);
    protected Request request = null;

    public RequestFacade(Request request) {
        this.request = request;
    }

    public void clear() {
        this.request = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Object getAttribute(String name) {
        this.checkFacade();
        return this.request.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetAttributePrivilegedAction());
        }
        return this.request.getAttributeNames();
    }

    public String getCharacterEncoding() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetCharacterEncodingPrivilegedAction());
        }
        return this.request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        this.checkFacade();
        this.request.setCharacterEncoding(env);
    }

    public int getContentLength() {
        this.checkFacade();
        return this.request.getContentLength();
    }

    public String getContentType() {
        this.checkFacade();
        return this.request.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        this.checkFacade();
        return this.request.getInputStream();
    }

    public String getParameter(String name) {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetParameterPrivilegedAction(name));
        }
        return this.request.getParameter(name);
    }

    public Enumeration<String> getParameterNames() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetParameterNamesPrivilegedAction());
        }
        return this.request.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        this.checkFacade();
        String[] ret = null;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            ret = AccessController.doPrivileged(new GetParameterValuePrivilegedAction(name));
            if (ret != null) {
                ret = (String[])ret.clone();
            }
        } else {
            ret = this.request.getParameterValues(name);
        }
        return ret;
    }

    public Map<String, String[]> getParameterMap() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetParameterMapPrivilegedAction());
        }
        return this.request.getParameterMap();
    }

    public String getProtocol() {
        this.checkFacade();
        return this.request.getProtocol();
    }

    public String getScheme() {
        this.checkFacade();
        return this.request.getScheme();
    }

    public String getServerName() {
        this.checkFacade();
        return this.request.getServerName();
    }

    public int getServerPort() {
        this.checkFacade();
        return this.request.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        this.checkFacade();
        return this.request.getReader();
    }

    public String getRemoteAddr() {
        this.checkFacade();
        return this.request.getRemoteAddr();
    }

    public String getRemoteHost() {
        this.checkFacade();
        return this.request.getRemoteHost();
    }

    public void setAttribute(String name, Object o) {
        this.checkFacade();
        this.request.setAttribute(name, o);
    }

    public void removeAttribute(String name) {
        this.checkFacade();
        this.request.removeAttribute(name);
    }

    public Locale getLocale() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetLocalePrivilegedAction());
        }
        return this.request.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetLocalesPrivilegedAction());
        }
        return this.request.getLocales();
    }

    public boolean isSecure() {
        this.checkFacade();
        return this.request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetRequestDispatcherPrivilegedAction(path));
        }
        return this.request.getRequestDispatcher(path);
    }

    public String getRealPath(String path) {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
        return this.request.getRealPath(path);
    }

    public String getAuthType() {
        this.checkFacade();
        return this.request.getAuthType();
    }

    public Cookie[] getCookies() {
        this.checkFacade();
        Cookie[] ret = null;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            ret = AccessController.doPrivileged(new GetCookiesPrivilegedAction());
            if (ret != null) {
                ret = (Cookie[])ret.clone();
            }
        } else {
            ret = this.request.getCookies();
        }
        return ret;
    }

    public long getDateHeader(String name) {
        this.checkFacade();
        return this.request.getDateHeader(name);
    }

    public String getHeader(String name) {
        this.checkFacade();
        return this.request.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name) {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetHeadersPrivilegedAction(name));
        }
        return this.request.getHeaders(name);
    }

    public Enumeration<String> getHeaderNames() {
        this.checkFacade();
        if (Globals.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(new GetHeaderNamesPrivilegedAction());
        }
        return this.request.getHeaderNames();
    }

    public int getIntHeader(String name) {
        this.checkFacade();
        return this.request.getIntHeader(name);
    }

    public HttpServletMapping getHttpServletMapping() {
        this.checkFacade();
        return this.request.getHttpServletMapping();
    }

    public String getMethod() {
        this.checkFacade();
        return this.request.getMethod();
    }

    public String getPathInfo() {
        this.checkFacade();
        return this.request.getPathInfo();
    }

    public String getPathTranslated() {
        this.checkFacade();
        return this.request.getPathTranslated();
    }

    public String getContextPath() {
        this.checkFacade();
        return this.request.getContextPath();
    }

    public String getQueryString() {
        this.checkFacade();
        return this.request.getQueryString();
    }

    public String getRemoteUser() {
        this.checkFacade();
        return this.request.getRemoteUser();
    }

    public boolean isUserInRole(String role) {
        this.checkFacade();
        return this.request.isUserInRole(role);
    }

    public Principal getUserPrincipal() {
        this.checkFacade();
        return this.request.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        this.checkFacade();
        return this.request.getRequestedSessionId();
    }

    public String getRequestURI() {
        this.checkFacade();
        return this.request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        this.checkFacade();
        return this.request.getRequestURL();
    }

    public String getServletPath() {
        this.checkFacade();
        return this.request.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        this.checkFacade();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new GetSessionPrivilegedAction(create));
        }
        return this.request.getSession(create);
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public String changeSessionId() {
        this.checkFacade();
        return this.request.changeSessionId();
    }

    public boolean isRequestedSessionIdValid() {
        this.checkFacade();
        return this.request.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        this.checkFacade();
        return this.request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        this.checkFacade();
        return this.request.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
        return this.request.isRequestedSessionIdFromURL();
    }

    public String getLocalAddr() {
        this.checkFacade();
        return this.request.getLocalAddr();
    }

    public String getLocalName() {
        this.checkFacade();
        return this.request.getLocalName();
    }

    public int getLocalPort() {
        this.checkFacade();
        return this.request.getLocalPort();
    }

    public int getRemotePort() {
        this.checkFacade();
        return this.request.getRemotePort();
    }

    public ServletContext getServletContext() {
        this.checkFacade();
        return this.request.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        this.checkFacade();
        return this.request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest request, ServletResponse response) throws IllegalStateException {
        this.checkFacade();
        return this.request.startAsync(request, response);
    }

    public boolean isAsyncStarted() {
        this.checkFacade();
        return this.request.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        this.checkFacade();
        return this.request.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        this.checkFacade();
        return this.request.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        this.checkFacade();
        return this.request.getDispatcherType();
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        this.checkFacade();
        return this.request.authenticate(response);
    }

    public void login(String username, String password) throws ServletException {
        this.checkFacade();
        this.request.login(username, password);
    }

    public void logout() throws ServletException {
        this.checkFacade();
        this.request.logout();
    }

    public Collection<Part> getParts() throws IllegalStateException, IOException, ServletException {
        this.checkFacade();
        return this.request.getParts();
    }

    public Part getPart(String name) throws IllegalStateException, IOException, ServletException {
        this.checkFacade();
        return this.request.getPart(name);
    }

    public boolean getAllowTrace() {
        this.checkFacade();
        return this.request.getConnector().getAllowTrace();
    }

    public long getContentLengthLong() {
        this.checkFacade();
        return this.request.getContentLengthLong();
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        this.checkFacade();
        return this.request.upgrade(httpUpgradeHandlerClass);
    }

    public PushBuilder newPushBuilder() {
        this.checkFacade();
        return this.request.newPushBuilder();
    }

    public PushBuilder newPushBuilder(HttpServletRequest request) {
        this.checkFacade();
        return this.request.newPushBuilder(request);
    }

    public boolean isTrailerFieldsReady() {
        this.checkFacade();
        return this.request.isTrailerFieldsReady();
    }

    public Map<String, String> getTrailerFields() {
        this.checkFacade();
        return this.request.getTrailerFields();
    }

    private void checkFacade() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("requestFacade.nullRequest"));
        }
    }

    private final class GetAttributePrivilegedAction
    implements PrivilegedAction<Enumeration<String>> {
        private GetAttributePrivilegedAction() {
        }

        @Override
        public Enumeration<String> run() {
            return RequestFacade.this.request.getAttributeNames();
        }
    }

    private final class GetCharacterEncodingPrivilegedAction
    implements PrivilegedAction<String> {
        private GetCharacterEncodingPrivilegedAction() {
        }

        @Override
        public String run() {
            return RequestFacade.this.request.getCharacterEncoding();
        }
    }

    private final class GetParameterPrivilegedAction
    implements PrivilegedAction<String> {
        public String name;

        GetParameterPrivilegedAction(String name) {
            this.name = name;
        }

        @Override
        public String run() {
            return RequestFacade.this.request.getParameter(this.name);
        }
    }

    private final class GetParameterNamesPrivilegedAction
    implements PrivilegedAction<Enumeration<String>> {
        private GetParameterNamesPrivilegedAction() {
        }

        @Override
        public Enumeration<String> run() {
            return RequestFacade.this.request.getParameterNames();
        }
    }

    private final class GetParameterValuePrivilegedAction
    implements PrivilegedAction<String[]> {
        public String name;

        GetParameterValuePrivilegedAction(String name) {
            this.name = name;
        }

        @Override
        public String[] run() {
            return RequestFacade.this.request.getParameterValues(this.name);
        }
    }

    private final class GetParameterMapPrivilegedAction
    implements PrivilegedAction<Map<String, String[]>> {
        private GetParameterMapPrivilegedAction() {
        }

        @Override
        public Map<String, String[]> run() {
            return RequestFacade.this.request.getParameterMap();
        }
    }

    private final class GetLocalePrivilegedAction
    implements PrivilegedAction<Locale> {
        private GetLocalePrivilegedAction() {
        }

        @Override
        public Locale run() {
            return RequestFacade.this.request.getLocale();
        }
    }

    private final class GetLocalesPrivilegedAction
    implements PrivilegedAction<Enumeration<Locale>> {
        private GetLocalesPrivilegedAction() {
        }

        @Override
        public Enumeration<Locale> run() {
            return RequestFacade.this.request.getLocales();
        }
    }

    private final class GetRequestDispatcherPrivilegedAction
    implements PrivilegedAction<RequestDispatcher> {
        private final String path;

        GetRequestDispatcherPrivilegedAction(String path) {
            this.path = path;
        }

        @Override
        public RequestDispatcher run() {
            return RequestFacade.this.request.getRequestDispatcher(this.path);
        }
    }

    private final class GetCookiesPrivilegedAction
    implements PrivilegedAction<Cookie[]> {
        private GetCookiesPrivilegedAction() {
        }

        @Override
        public Cookie[] run() {
            return RequestFacade.this.request.getCookies();
        }
    }

    private final class GetHeadersPrivilegedAction
    implements PrivilegedAction<Enumeration<String>> {
        private final String name;

        GetHeadersPrivilegedAction(String name) {
            this.name = name;
        }

        @Override
        public Enumeration<String> run() {
            return RequestFacade.this.request.getHeaders(this.name);
        }
    }

    private final class GetHeaderNamesPrivilegedAction
    implements PrivilegedAction<Enumeration<String>> {
        private GetHeaderNamesPrivilegedAction() {
        }

        @Override
        public Enumeration<String> run() {
            return RequestFacade.this.request.getHeaderNames();
        }
    }

    private final class GetSessionPrivilegedAction
    implements PrivilegedAction<HttpSession> {
        private final boolean create;

        GetSessionPrivilegedAction(boolean create) {
            this.create = create;
        }

        @Override
        public HttpSession run() {
            return RequestFacade.this.request.getSession(this.create);
        }
    }
}

