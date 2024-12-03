/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.SessionListener;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.SingleSignOnEntry;
import org.apache.catalina.authenticator.SingleSignOnListener;
import org.apache.catalina.authenticator.SingleSignOnSessionKey;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.res.StringManager;

public class SingleSignOn
extends ValveBase {
    private static final StringManager sm = StringManager.getManager(SingleSignOn.class);
    private Engine engine;
    protected Map<String, SingleSignOnEntry> cache = new ConcurrentHashMap<String, SingleSignOnEntry>();
    private boolean requireReauthentication = false;
    private String cookieDomain;
    private String cookieName = Constants.SINGLE_SIGN_ON_COOKIE;

    public SingleSignOn() {
        super(true);
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain != null && cookieDomain.trim().length() == 0 ? null : cookieDomain;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public boolean getRequireReauthentication() {
        return this.requireReauthentication;
    }

    public void setRequireReauthentication(boolean required) {
        this.requireReauthentication = required;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        SingleSignOnEntry entry;
        request.removeNote("org.apache.catalina.request.SSOID");
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.invoke", new Object[]{request.getRequestURI()}));
        }
        if (request.getUserPrincipal() != null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.hasPrincipal", new Object[]{request.getUserPrincipal().getName()}));
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.cookieCheck"));
        }
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie value : cookies) {
                if (!this.cookieName.equals(value.getName())) continue;
                cookie = value;
                break;
            }
        }
        if (cookie == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.cookieNotFound"));
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.principalCheck", new Object[]{cookie.getValue()}));
        }
        if ((entry = this.cache.get(cookie.getValue())) != null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.principalFound", new Object[]{entry.getPrincipal() != null ? entry.getPrincipal().getName() : "", entry.getAuthType()}));
            }
            request.setNote("org.apache.catalina.request.SSOID", cookie.getValue());
            if (!this.getRequireReauthentication()) {
                request.setAuthType(entry.getAuthType());
                request.setUserPrincipal(entry.getPrincipal());
            }
        } else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.principalNotFound", new Object[]{cookie.getValue()}));
            }
            cookie.setValue("REMOVE");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            String domain = this.getCookieDomain();
            if (domain != null) {
                cookie.setDomain(domain);
            }
            cookie.setSecure(request.isSecure());
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            response.addCookie(cookie);
        }
        this.getNext().invoke(request, response);
    }

    public void sessionDestroyed(String ssoId, Session session) {
        if (!this.getState().isAvailable()) {
            return;
        }
        if (session.getMaxInactiveInterval() > 0 && session.getIdleTimeInternal() >= (long)(session.getMaxInactiveInterval() * 1000) || !session.getManager().getContext().getState().isAvailable()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.sessionTimeout", new Object[]{ssoId, session}));
            }
            this.removeSession(ssoId, session);
        } else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.sessionLogout", new Object[]{ssoId, session}));
            }
            this.removeSession(ssoId, session);
            if (this.cache.containsKey(ssoId)) {
                this.deregister(ssoId);
            }
        }
    }

    protected boolean associate(String ssoId, Session session) {
        SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.associateFail", new Object[]{ssoId, session}));
            }
            return false;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.associate", new Object[]{ssoId, session}));
        }
        sso.addSession(this, ssoId, session);
        return true;
    }

    protected void deregister(String ssoId) {
        SingleSignOnEntry sso = this.cache.remove(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.deregisterFail", new Object[]{ssoId}));
            }
            return;
        }
        Set<SingleSignOnSessionKey> ssoKeys = sso.findSessions();
        if (ssoKeys.size() == 0 && this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.deregisterNone", new Object[]{ssoId}));
        }
        for (SingleSignOnSessionKey ssoKey : ssoKeys) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.deregister", new Object[]{ssoKey, ssoId}));
            }
            this.expire(ssoKey);
        }
    }

    private void expire(SingleSignOnSessionKey key) {
        if (this.engine == null) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.engineNull", new Object[]{key}));
            return;
        }
        Container host = this.engine.findChild(key.getHostName());
        if (host == null) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.hostNotFound", new Object[]{key}));
            return;
        }
        Context context = (Context)host.findChild(key.getContextName());
        if (context == null) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.contextNotFound", new Object[]{key}));
            return;
        }
        Manager manager = context.getManager();
        if (manager == null) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.managerNotFound", new Object[]{key}));
            return;
        }
        Session session = null;
        try {
            session = manager.findSession(key.getSessionId());
        }
        catch (IOException e) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.managerError", new Object[]{key}), (Throwable)e);
            return;
        }
        if (session == null) {
            this.containerLog.warn((Object)sm.getString("singleSignOn.sessionExpire.sessionNotFound", new Object[]{key}));
            return;
        }
        session.expire();
    }

    protected boolean reauthenticate(String ssoId, Realm realm, Request request) {
        Principal reauthPrincipal;
        String username;
        if (ssoId == null || realm == null) {
            return false;
        }
        boolean reauthenticated = false;
        SingleSignOnEntry entry = this.cache.get(ssoId);
        if (entry != null && entry.getCanReauthenticate() && (username = entry.getUsername()) != null && (reauthPrincipal = realm.authenticate(username, entry.getPassword())) != null) {
            reauthenticated = true;
            request.setAuthType(entry.getAuthType());
            request.setUserPrincipal(reauthPrincipal);
        }
        return reauthenticated;
    }

    protected void register(String ssoId, Principal principal, String authType, String username, String password) {
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.register", new Object[]{ssoId, principal != null ? principal.getName() : "", authType}));
        }
        this.cache.put(ssoId, new SingleSignOnEntry(principal, authType, username, password));
    }

    protected boolean update(String ssoId, Principal principal, String authType, String username, String password) {
        SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso != null && !sso.getCanReauthenticate()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)sm.getString("singleSignOn.debug.update", new Object[]{ssoId, authType}));
            }
            sso.updateCredentials(principal, authType, username, password);
            return true;
        }
        return false;
    }

    protected void removeSession(String ssoId, Session session) {
        SingleSignOnEntry entry;
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)sm.getString("singleSignOn.debug.removeSession", new Object[]{session, ssoId}));
        }
        if ((entry = this.cache.get(ssoId)) == null) {
            return;
        }
        entry.removeSession(session);
        if (entry.findSessions().size() == 0) {
            this.deregister(ssoId);
        }
    }

    protected SessionListener getSessionListener(String ssoId) {
        return new SingleSignOnListener(ssoId);
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Container c;
        for (c = this.getContainer(); c != null && !(c instanceof Engine); c = c.getParent()) {
        }
        if (c != null) {
            this.engine = (Engine)c;
        }
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.engine = null;
    }
}

