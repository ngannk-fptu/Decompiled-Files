/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.ContinueResponseTiming
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.http.MimeHeaders
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.SavedRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.coyote.ActionCode;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.http.MimeHeaders;

public class FormAuthenticator
extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(FormAuthenticator.class);
    protected String characterEncoding = null;
    protected String landingPage = null;
    protected int authenticationSessionTimeout = 120;

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String encoding) {
        this.characterEncoding = encoding;
    }

    public String getLandingPage() {
        return this.landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public int getAuthenticationSessionTimeout() {
        return this.authenticationSessionTimeout;
    }

    public void setAuthenticationSessionTimeout(int authenticationSessionTimeout) {
        this.authenticationSessionTimeout = authenticationSessionTimeout;
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        String uri;
        String expectedSessionId;
        Session session = null;
        Principal principal = null;
        if (!this.cache) {
            session = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Checking for reauthenticate in session " + session));
            }
            String username = (String)session.getNote("org.apache.catalina.session.USERNAME");
            String password = (String)session.getNote("org.apache.catalina.session.PASSWORD");
            if (username != null && password != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Reauthenticating username '" + username + "'"));
                }
                if ((principal = this.context.getRealm().authenticate(username, password)) != null) {
                    this.register(request, response, principal, "FORM", username, password);
                    if (!this.matchRequest(request)) {
                        return true;
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Reauthentication failed, proceed normally");
                }
            }
        }
        if (this.matchRequest(request)) {
            session = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Restore request from session '" + session.getIdInternal() + "'"));
            }
            if (this.restoreRequest(request, session)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Proceed to restored request");
                }
                return true;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Restore of original request failed");
            }
            response.sendError(400);
            return false;
        }
        if (this.checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        String contextPath = request.getContextPath();
        String requestURI = request.getDecodedRequestURI();
        boolean loginAction = requestURI.startsWith(contextPath) && requestURI.endsWith("/j_security_check");
        LoginConfig config = this.context.getLoginConfig();
        if (!loginAction) {
            if (request.getServletPath().length() == 0 && request.getPathInfo() == null) {
                StringBuilder location = new StringBuilder(requestURI);
                location.append('/');
                if (request.getQueryString() != null) {
                    location.append('?');
                    location.append(request.getQueryString());
                }
                response.sendRedirect(response.encodeRedirectURL(location.toString()));
                return false;
            }
            session = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Save request in session '" + session.getIdInternal() + "'"));
            }
            try {
                this.saveRequest(request, session);
            }
            catch (IOException ioe) {
                this.log.debug((Object)"Request body too big to save during authentication");
                response.sendError(403, sm.getString("authenticator.requestBodyTooBig"));
                return false;
            }
            this.forwardToLoginPage(request, response, config);
            return false;
        }
        request.getResponse().sendAcknowledgement(ContinueResponseTiming.ALWAYS);
        Realm realm = this.context.getRealm();
        if (this.characterEncoding != null) {
            request.setCharacterEncoding(this.characterEncoding);
        }
        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Authenticating username '" + username + "'"));
        }
        if ((principal = realm.authenticate(username, password)) == null) {
            this.forwardToErrorPage(request, response, config);
            return false;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Authentication of '" + username + "' was successful"));
        }
        if (session == null) {
            session = request.getSessionInternal(false);
        }
        if (session != null && this.getChangeSessionIdOnAuthentication() && ((expectedSessionId = (String)session.getNote("org.apache.catalina.authenticator.SESSION_ID")) == null || !expectedSessionId.equals(request.getRequestedSessionId()))) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("formAuthenticator.sessionIdMismatch", new Object[]{session.getId(), expectedSessionId}));
            }
            session.expire();
            session = null;
        }
        if (session == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"User took so long to log on the session expired");
            }
            if (this.landingPage == null) {
                response.sendError(408, sm.getString("authenticator.sessionExpired"));
            } else {
                uri = request.getContextPath() + this.landingPage;
                SavedRequest saved = new SavedRequest();
                saved.setMethod("GET");
                saved.setRequestURI(uri);
                saved.setDecodedRequestURI(uri);
                request.getSessionInternal(true).setNote("org.apache.catalina.authenticator.REQUEST", saved);
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
            return false;
        }
        this.register(request, response, principal, "FORM", username, password);
        requestURI = this.savedRequestURL(session);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Redirecting to original '" + requestURI + "'"));
        }
        if (requestURI == null) {
            if (this.landingPage == null) {
                response.sendError(400, sm.getString("authenticator.formlogin"));
            } else {
                uri = request.getContextPath() + this.landingPage;
                SavedRequest saved = new SavedRequest();
                saved.setMethod("GET");
                saved.setRequestURI(uri);
                saved.setDecodedRequestURI(uri);
                session.setNote("org.apache.catalina.authenticator.REQUEST", saved);
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
        } else {
            Response internalResponse = request.getResponse();
            String location = response.encodeRedirectURL(requestURI);
            if ("HTTP/1.1".equals(request.getProtocol())) {
                internalResponse.sendRedirect(location, 303);
            } else {
                internalResponse.sendRedirect(location, 302);
            }
        }
        return false;
    }

    @Override
    protected boolean isContinuationRequired(Request request) {
        SavedRequest savedRequest;
        String contextPath = this.context.getPath();
        String decodedRequestURI = request.getDecodedRequestURI();
        if (decodedRequestURI.startsWith(contextPath) && decodedRequestURI.endsWith("/j_security_check")) {
            return true;
        }
        Session session = request.getSessionInternal(false);
        return session != null && (savedRequest = (SavedRequest)session.getNote("org.apache.catalina.authenticator.REQUEST")) != null && decodedRequestURI.equals(savedRequest.getDecodedRequestURI());
    }

    @Override
    protected String getAuthMethod() {
        return "FORM";
    }

    @Override
    protected void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password, boolean alwaysUseSession, boolean cache) {
        Session session;
        super.register(request, response, principal, authType, username, password, alwaysUseSession, cache);
        if (!cache && (session = request.getSessionInternal(false)) != null) {
            if (username != null) {
                session.setNote("org.apache.catalina.session.USERNAME", username);
            } else {
                session.removeNote("org.apache.catalina.session.USERNAME");
            }
            if (password != null) {
                session.setNote("org.apache.catalina.session.PASSWORD", password);
            } else {
                session.removeNote("org.apache.catalina.session.PASSWORD");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void forwardToLoginPage(Request request, HttpServletResponse response, LoginConfig config) throws IOException {
        Session session;
        String loginPage;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("formAuthenticator.forwardLogin", new Object[]{request.getRequestURI(), request.getMethod(), config.getLoginPage(), this.context.getName()}));
        }
        if ((loginPage = config.getLoginPage()) == null || loginPage.length() == 0) {
            String msg = sm.getString("formAuthenticator.noLoginPage", new Object[]{this.context.getName()});
            this.log.warn((Object)msg);
            response.sendError(500, msg);
            return;
        }
        if (this.getChangeSessionIdOnAuthentication() && (session = request.getSessionInternal(false)) != null) {
            String oldSessionId = session.getId();
            String newSessionId = this.changeSessionID(request, session);
            session.setNote("org.apache.catalina.authenticator.SESSION_ID", newSessionId);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("formAuthenticator.changeSessionIdLogin", new Object[]{oldSessionId, newSessionId}));
            }
        }
        String oldMethod = request.getMethod();
        request.getCoyoteRequest().method().setString("GET");
        RequestDispatcher disp = this.context.getServletContext().getRequestDispatcher(loginPage);
        try {
            if (this.context.fireRequestInitEvent((ServletRequest)request.getRequest())) {
                disp.forward((ServletRequest)request.getRequest(), (ServletResponse)response);
                this.context.fireRequestDestroyEvent((ServletRequest)request.getRequest());
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            String msg = sm.getString("formAuthenticator.forwardLoginFail");
            this.log.warn((Object)msg, t);
            request.setAttribute("javax.servlet.error.exception", t);
            response.sendError(500, msg);
        }
        finally {
            request.getCoyoteRequest().method().setString(oldMethod);
        }
    }

    protected void forwardToErrorPage(Request request, HttpServletResponse response, LoginConfig config) throws IOException {
        String errorPage = config.getErrorPage();
        if (errorPage == null || errorPage.length() == 0) {
            String msg = sm.getString("formAuthenticator.noErrorPage", new Object[]{this.context.getName()});
            this.log.warn((Object)msg);
            response.sendError(500, msg);
            return;
        }
        RequestDispatcher disp = this.context.getServletContext().getRequestDispatcher(config.getErrorPage());
        try {
            if (this.context.fireRequestInitEvent((ServletRequest)request.getRequest())) {
                disp.forward((ServletRequest)request.getRequest(), (ServletResponse)response);
                this.context.fireRequestDestroyEvent((ServletRequest)request.getRequest());
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            String msg = sm.getString("formAuthenticator.forwardErrorFail");
            this.log.warn((Object)msg, t);
            request.setAttribute("javax.servlet.error.exception", t);
            response.sendError(500, msg);
        }
    }

    protected boolean matchRequest(Request request) {
        String expectedSessionId;
        Session session = request.getSessionInternal(false);
        if (session == null) {
            return false;
        }
        SavedRequest sreq = (SavedRequest)session.getNote("org.apache.catalina.authenticator.REQUEST");
        if (sreq == null) {
            return false;
        }
        if (this.cache && session.getPrincipal() == null || !this.cache && request.getPrincipal() == null) {
            return false;
        }
        if (this.getChangeSessionIdOnAuthentication() && ((expectedSessionId = (String)session.getNote("org.apache.catalina.authenticator.SESSION_ID")) == null || !expectedSessionId.equals(request.getRequestedSessionId()))) {
            return false;
        }
        String decodedRequestURI = request.getDecodedRequestURI();
        if (decodedRequestURI == null) {
            return false;
        }
        return decodedRequestURI.equals(sreq.getDecodedRequestURI());
    }

    protected boolean restoreRequest(Request request, Session session) throws IOException {
        SavedRequest saved = (SavedRequest)session.getNote("org.apache.catalina.authenticator.REQUEST");
        session.removeNote("org.apache.catalina.authenticator.REQUEST");
        session.removeNote("org.apache.catalina.authenticator.SESSION_ID");
        if (saved == null) {
            return false;
        }
        byte[] buffer = new byte[4096];
        ServletInputStream is = request.createInputStream();
        while (is.read(buffer) >= 0) {
        }
        request.clearCookies();
        Iterator<Cookie> cookies = saved.getCookies();
        while (cookies.hasNext()) {
            request.addCookie(cookies.next());
        }
        String method = saved.getMethod();
        MimeHeaders rmh = request.getCoyoteRequest().getMimeHeaders();
        rmh.recycle();
        boolean cacheable = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
        Iterator<String> names = saved.getHeaderNames();
        while (names.hasNext()) {
            String name = names.next();
            if ("If-Modified-Since".equalsIgnoreCase(name) || cacheable && "If-None-Match".equalsIgnoreCase(name)) continue;
            Iterator<String> values = saved.getHeaderValues(name);
            while (values.hasNext()) {
                rmh.addValue(name).setString(values.next());
            }
        }
        request.clearLocales();
        Iterator<Locale> locales = saved.getLocales();
        while (locales.hasNext()) {
            request.addLocale(locales.next());
        }
        request.getCoyoteRequest().getParameters().recycle();
        ByteChunk body = saved.getBody();
        if (body != null) {
            request.getCoyoteRequest().action(ActionCode.REQ_SET_BODY_REPLAY, (Object)body);
            MessageBytes contentType = MessageBytes.newInstance();
            String savedContentType = saved.getContentType();
            if (savedContentType == null && "POST".equalsIgnoreCase(method)) {
                savedContentType = "application/x-www-form-urlencoded";
            }
            contentType.setString(savedContentType);
            request.getCoyoteRequest().setContentType(contentType);
        }
        request.getCoyoteRequest().method().setString(method);
        request.getRequestURI();
        request.getQueryString();
        request.getProtocol();
        if (saved.getOriginalMaxInactiveInterval() > 0) {
            session.setMaxInactiveInterval(saved.getOriginalMaxInactiveInterval());
        }
        return true;
    }

    protected void saveRequest(Request request, Session session) throws IOException {
        SavedRequest saved = new SavedRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                saved.addCookie(cookie);
            }
        }
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                saved.addHeader(name, value);
            }
        }
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            saved.addLocale(locale);
        }
        request.getResponse().sendAcknowledgement(ContinueResponseTiming.ALWAYS);
        int maxSavePostSize = request.getConnector().getMaxSavePostSize();
        if (maxSavePostSize != 0) {
            int bytesRead;
            ByteChunk body = new ByteChunk();
            body.setLimit(maxSavePostSize);
            byte[] buffer = new byte[4096];
            ServletInputStream is = request.getInputStream();
            while ((bytesRead = is.read(buffer)) >= 0) {
                body.append(buffer, 0, bytesRead);
            }
            if (body.getLength() > 0) {
                saved.setContentType(request.getContentType());
                saved.setBody(body);
            }
        }
        saved.setMethod(request.getMethod());
        saved.setQueryString(request.getQueryString());
        saved.setRequestURI(request.getRequestURI());
        saved.setDecodedRequestURI(request.getDecodedRequestURI());
        SavedRequest previousSavedRequest = (SavedRequest)session.getNote("org.apache.catalina.authenticator.REQUEST");
        if (session instanceof HttpSession) {
            if (((HttpSession)session).isNew()) {
                int originalMaxInactiveInterval = session.getMaxInactiveInterval();
                if (originalMaxInactiveInterval > this.getAuthenticationSessionTimeout()) {
                    saved.setOriginalMaxInactiveInterval(originalMaxInactiveInterval);
                    session.setMaxInactiveInterval(this.getAuthenticationSessionTimeout());
                }
            } else if (previousSavedRequest != null && previousSavedRequest.getOriginalMaxInactiveInterval() > 0) {
                saved.setOriginalMaxInactiveInterval(previousSavedRequest.getOriginalMaxInactiveInterval());
            }
        }
        session.setNote("org.apache.catalina.authenticator.REQUEST", saved);
    }

    protected String savedRequestURL(Session session) {
        SavedRequest saved = (SavedRequest)session.getNote("org.apache.catalina.authenticator.REQUEST");
        if (saved == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(saved.getRequestURI());
        if (saved.getQueryString() != null) {
            sb.append('?');
            sb.append(saved.getQueryString());
        }
        while (sb.length() > 1 && sb.charAt(1) == '/') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
}

