/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.http.Cookie
 */
package org.apache.catalina.valves;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.valves.ValveBase;

public class LoadBalancerDrainingValve
extends ValveBase {
    public static final String ATTRIBUTE_KEY_JK_LB_ACTIVATION = "JK_LB_ACTIVATION";
    private int _redirectStatusCode = 307;
    private String _ignoreCookieName;
    private String _ignoreCookieValue;

    public LoadBalancerDrainingValve() {
        super(true);
    }

    public void setRedirectStatusCode(int code) {
        this._redirectStatusCode = code;
    }

    public String getIgnoreCookieName() {
        return this._ignoreCookieName;
    }

    public void setIgnoreCookieName(String cookieName) {
        this._ignoreCookieName = cookieName;
    }

    public String getIgnoreCookieValue() {
        return this._ignoreCookieValue;
    }

    public void setIgnoreCookieValue(String cookieValue) {
        this._ignoreCookieValue = cookieValue;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if ("DIS".equals(request.getAttribute(ATTRIBUTE_KEY_JK_LB_ACTIVATION)) && !request.isRequestedSessionIdValid()) {
            String queryString;
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"Load-balancer is in DISABLED state; draining this node");
            }
            boolean ignoreRebalance = false;
            Cookie sessionCookie = null;
            Cookie[] cookies = request.getCookies();
            String sessionCookieName = SessionConfig.getSessionCookieName(request.getContext());
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace((Object)("Checking cookie " + cookieName + "=" + cookie.getValue()));
                    }
                    if (sessionCookieName.equals(cookieName) && request.getRequestedSessionId().equals(cookie.getValue())) {
                        sessionCookie = cookie;
                        continue;
                    }
                    if (null == this._ignoreCookieName || !this._ignoreCookieName.equals(cookieName) || null == this._ignoreCookieValue || !this._ignoreCookieValue.equals(cookie.getValue())) continue;
                    ignoreRebalance = true;
                }
            }
            if (ignoreRebalance) {
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug((Object)("Client is presenting a valid " + this._ignoreCookieName + " cookie, re-balancing is being skipped"));
                }
                this.getNext().invoke(request, response);
                return;
            }
            if (null != sessionCookie) {
                sessionCookie.setPath(SessionConfig.getSessionCookiePath(request.getContext()));
                sessionCookie.setMaxAge(0);
                sessionCookie.setValue("");
                SessionCookieConfig sessionCookieConfig = request.getContext().getServletContext().getSessionCookieConfig();
                sessionCookie.setSecure(request.isSecure() || sessionCookieConfig.isSecure());
                response.addCookie(sessionCookie);
            }
            String uri = request.getRequestURI();
            String sessionURIParamName = SessionConfig.getSessionUriParamName(request.getContext());
            if (uri.contains(";" + sessionURIParamName + "=")) {
                uri = uri.replaceFirst(";" + sessionURIParamName + "=[^&?]*", "");
            }
            if (null != (queryString = request.getQueryString())) {
                uri = uri + "?" + queryString;
            }
            response.setHeader("Location", uri);
            response.setStatus(this._redirectStatusCode);
        } else {
            this.getNext().invoke(request, response);
        }
    }
}

