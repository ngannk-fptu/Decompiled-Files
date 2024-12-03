/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.AuthException
 *  javax.security.auth.message.AuthStatus
 *  javax.security.auth.message.MessageInfo
 *  javax.security.auth.message.config.AuthConfigFactory
 *  javax.security.auth.message.config.AuthConfigProvider
 *  javax.security.auth.message.config.RegistrationListener
 *  javax.security.auth.message.config.ServerAuthConfig
 *  javax.security.auth.message.config.ServerAuthContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.authenticator.jaspic.MessageInfoImpl;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.filters.CorsFilter;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

public abstract class AuthenticatorBase
extends ValveBase
implements Authenticator,
RegistrationListener {
    private final Log log = LogFactory.getLog(AuthenticatorBase.class);
    private static final String DATE_ONE = FastHttpDateFormat.formatDate((long)1L);
    protected static final StringManager sm = StringManager.getManager(AuthenticatorBase.class);
    protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";
    protected static final String REALM_NAME = "Authentication required";
    protected boolean alwaysUseSession = false;
    protected boolean cache = true;
    protected boolean changeSessionIdOnAuthentication = true;
    protected Context context = null;
    protected boolean disableProxyCaching = true;
    protected boolean securePagesWithPragma = false;
    protected String secureRandomClass = null;
    protected String secureRandomAlgorithm = SessionIdGeneratorBase.DEFAULT_SECURE_RANDOM_ALGORITHM;
    protected String secureRandomProvider = null;
    protected String jaspicCallbackHandlerClass = "org.apache.catalina.authenticator.jaspic.CallbackHandlerImpl";
    protected boolean sendAuthInfoResponseHeaders = false;
    protected SessionIdGeneratorBase sessionIdGenerator = null;
    protected SingleSignOn sso = null;
    private AllowCorsPreflight allowCorsPreflight = AllowCorsPreflight.NEVER;
    private volatile String jaspicAppContextID = null;
    private volatile Optional<AuthConfigProvider> jaspicProvider = null;
    private volatile CallbackHandler jaspicCallbackHandler = null;

    protected static String getRealmName(Context context) {
        if (context == null) {
            return REALM_NAME;
        }
        LoginConfig config = context.getLoginConfig();
        if (config == null) {
            return REALM_NAME;
        }
        String result = config.getRealmName();
        if (result == null) {
            return REALM_NAME;
        }
        return result;
    }

    public AuthenticatorBase() {
        super(true);
    }

    public String getAllowCorsPreflight() {
        return this.allowCorsPreflight.name().toLowerCase(Locale.ENGLISH);
    }

    public void setAllowCorsPreflight(String allowCorsPreflight) {
        this.allowCorsPreflight = AllowCorsPreflight.valueOf(allowCorsPreflight.trim().toUpperCase(Locale.ENGLISH));
    }

    public boolean getAlwaysUseSession() {
        return this.alwaysUseSession;
    }

    public void setAlwaysUseSession(boolean alwaysUseSession) {
        this.alwaysUseSession = alwaysUseSession;
    }

    public boolean getCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override
    public Container getContainer() {
        return this.context;
    }

    @Override
    public void setContainer(Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("authenticator.notContext"));
        }
        super.setContainer(container);
        this.context = (Context)container;
    }

    public boolean getDisableProxyCaching() {
        return this.disableProxyCaching;
    }

    public void setDisableProxyCaching(boolean nocache) {
        this.disableProxyCaching = nocache;
    }

    public boolean getSecurePagesWithPragma() {
        return this.securePagesWithPragma;
    }

    public void setSecurePagesWithPragma(boolean securePagesWithPragma) {
        this.securePagesWithPragma = securePagesWithPragma;
    }

    public boolean getChangeSessionIdOnAuthentication() {
        return this.changeSessionIdOnAuthentication;
    }

    public void setChangeSessionIdOnAuthentication(boolean changeSessionIdOnAuthentication) {
        this.changeSessionIdOnAuthentication = changeSessionIdOnAuthentication;
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    public String getJaspicCallbackHandlerClass() {
        return this.jaspicCallbackHandlerClass;
    }

    public void setJaspicCallbackHandlerClass(String jaspicCallbackHandlerClass) {
        this.jaspicCallbackHandlerClass = jaspicCallbackHandlerClass;
    }

    public boolean isSendAuthInfoResponseHeaders() {
        return this.sendAuthInfoResponseHeaders;
    }

    public void setSendAuthInfoResponseHeaders(boolean sendAuthInfoResponseHeaders) {
        this.sendAuthInfoResponseHeaders = sendAuthInfoResponseHeaders;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Session session;
        Principal principal;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Security checking request " + request.getMethod() + " " + request.getRequestURI()));
        }
        if (this.cache && (principal = request.getUserPrincipal()) == null && (session = request.getSessionInternal(false)) != null && (principal = session.getPrincipal()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("We have cached auth type " + session.getAuthType() + " for principal " + principal));
            }
            request.setAuthType(session.getAuthType());
            request.setUserPrincipal(principal);
        }
        boolean authRequired = this.isContinuationRequired(request);
        Realm realm = this.context.getRealm();
        SecurityConstraint[] constraints = realm.findSecurityConstraints(request, this.context);
        AuthConfigProvider jaspicProvider = this.getJaspicProvider();
        if (jaspicProvider != null) {
            authRequired = true;
        }
        if (constraints == null && !this.context.getPreemptiveAuthentication() && !authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Not subject to any constraint");
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (constraints != null && this.disableProxyCaching && !"POST".equalsIgnoreCase(request.getMethod())) {
            if (this.securePagesWithPragma) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Expires", DATE_ONE);
            } else {
                response.setHeader("Cache-Control", "private");
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling hasUserDataPermission()");
            }
            if (!realm.hasUserDataPermission(request, response, constraints)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed hasUserDataPermission() test");
                }
                return;
            }
        }
        boolean hasAuthConstraint = false;
        if (constraints != null) {
            hasAuthConstraint = true;
            for (int i = 0; i < constraints.length && hasAuthConstraint; ++i) {
                String[] roles;
                if (!constraints[i].getAuthConstraint()) {
                    hasAuthConstraint = false;
                    continue;
                }
                if (constraints[i].getAllRoles() || constraints[i].getAuthenticatedUsers() || (roles = constraints[i].findAuthRoles()) != null && roles.length != 0) continue;
                hasAuthConstraint = false;
            }
        }
        if (!authRequired && hasAuthConstraint) {
            authRequired = true;
        }
        if (!authRequired && this.context.getPreemptiveAuthentication() && this.isPreemptiveAuthPossible(request)) {
            authRequired = true;
        }
        JaspicState jaspicState = null;
        if ((authRequired || constraints != null) && this.allowCorsPreflightBypass(request)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"CORS Preflight request bypassing authentication");
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling authenticate()");
            }
            if (jaspicProvider != null && (jaspicState = this.getJaspicState(jaspicProvider, request, response, hasAuthConstraint)) == null) {
                return;
            }
            if (jaspicProvider == null && !this.doAuthenticate(request, response) || jaspicProvider != null && !this.authenticateJaspic(request, response, jaspicState, false)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed authenticate() test");
                }
                return;
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling accessControl()");
            }
            if (!realm.hasResourcePermission(request, response, constraints, this.context)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed accessControl() test");
                }
                return;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Successfully passed all security constraints");
        }
        this.getNext().invoke(request, response);
        if (jaspicProvider != null) {
            this.secureResponseJspic(request, response, jaspicState);
        }
    }

    protected boolean allowCorsPreflightBypass(Request request) {
        String accessControlRequestMethodHeader;
        String originHeader;
        boolean allowBypass = false;
        if (this.allowCorsPreflight != AllowCorsPreflight.NEVER && "OPTIONS".equals(request.getMethod()) && (originHeader = request.getHeader("Origin")) != null && !originHeader.isEmpty() && RequestUtil.isValidOrigin((String)originHeader) && !RequestUtil.isSameOrigin((HttpServletRequest)request, (String)originHeader) && (accessControlRequestMethodHeader = request.getHeader("Access-Control-Request-Method")) != null && !accessControlRequestMethodHeader.isEmpty()) {
            if (this.allowCorsPreflight == AllowCorsPreflight.ALWAYS) {
                allowBypass = true;
            } else if (this.allowCorsPreflight == AllowCorsPreflight.FILTER && DispatcherType.REQUEST == request.getDispatcherType()) {
                block0: for (FilterDef filterDef : request.getContext().findFilterDefs()) {
                    if (!CorsFilter.class.getName().equals(filterDef.getFilterClass())) continue;
                    for (FilterMap filterMap : this.context.findFilterMaps()) {
                        if (!filterMap.getFilterName().equals(filterDef.getFilterName())) continue;
                        if ((filterMap.getDispatcherMapping() & 8) <= 0) break block0;
                        for (String urlPattern : filterMap.getURLPatterns()) {
                            if (!"/*".equals(urlPattern)) continue;
                            allowBypass = true;
                            break block0;
                        }
                        break block0;
                    }
                    break;
                }
            }
        }
        return allowBypass;
    }

    @Override
    public boolean authenticate(Request request, HttpServletResponse httpResponse) throws IOException {
        AuthConfigProvider jaspicProvider = this.getJaspicProvider();
        if (jaspicProvider == null) {
            return this.doAuthenticate(request, httpResponse);
        }
        Response response = request.getResponse();
        JaspicState jaspicState = this.getJaspicState(jaspicProvider, request, response, true);
        if (jaspicState == null) {
            return false;
        }
        boolean result = this.authenticateJaspic(request, response, jaspicState, true);
        this.secureResponseJspic(request, response, jaspicState);
        return result;
    }

    private void secureResponseJspic(Request request, Response response, JaspicState state) {
        try {
            state.serverAuthContext.secureResponse(state.messageInfo, null);
            request.setRequest((HttpServletRequest)state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse)state.messageInfo.getResponseMessage());
        }
        catch (AuthException e) {
            this.log.warn((Object)sm.getString("authenticator.jaspicSecureResponseFail"), (Throwable)e);
        }
    }

    private JaspicState getJaspicState(AuthConfigProvider jaspicProvider, Request request, Response response, boolean authMandatory) throws IOException {
        JaspicState jaspicState = new JaspicState();
        jaspicState.messageInfo = new MessageInfoImpl(request.getRequest(), response.getResponse(), authMandatory);
        try {
            CallbackHandler callbackHandler = this.getCallbackHandler();
            ServerAuthConfig serverAuthConfig = jaspicProvider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, callbackHandler);
            String authContextID = serverAuthConfig.getAuthContextID(jaspicState.messageInfo);
            jaspicState.serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
        }
        catch (AuthException e) {
            this.log.warn((Object)sm.getString("authenticator.jaspicServerAuthContextFail"), (Throwable)e);
            response.sendError(500);
            return null;
        }
        return jaspicState;
    }

    private CallbackHandler getCallbackHandler() {
        CallbackHandler handler = this.jaspicCallbackHandler;
        if (handler == null) {
            handler = this.createCallbackHandler();
        }
        return handler;
    }

    private CallbackHandler createCallbackHandler() {
        CallbackHandler callbackHandler = null;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(this.jaspicCallbackHandlerClass, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            if (clazz == null) {
                clazz = Class.forName(this.jaspicCallbackHandlerClass);
            }
            callbackHandler = (CallbackHandler)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new SecurityException(e);
        }
        if (callbackHandler instanceof Contained) {
            ((Contained)((Object)callbackHandler)).setContainer(this.getContainer());
        }
        this.jaspicCallbackHandler = callbackHandler;
        return callbackHandler;
    }

    protected abstract boolean doAuthenticate(Request var1, HttpServletResponse var2) throws IOException;

    protected boolean isContinuationRequired(Request request) {
        return false;
    }

    protected void associate(String ssoId, Session session) {
        if (this.sso == null) {
            return;
        }
        this.sso.associate(ssoId, session);
    }

    private boolean authenticateJaspic(Request request, Response response, JaspicState state, boolean requirePrincipal) {
        AuthStatus authStatus;
        boolean cachedAuth = this.checkForCachedAuthentication(request, response, false);
        Subject client = new Subject();
        try {
            authStatus = state.serverAuthContext.validateRequest(state.messageInfo, client, null);
        }
        catch (AuthException e) {
            this.log.debug((Object)sm.getString("authenticator.loginFail"), (Throwable)e);
            return false;
        }
        request.setRequest((HttpServletRequest)state.messageInfo.getRequestMessage());
        response.setResponse((HttpServletResponse)state.messageInfo.getResponseMessage());
        if (authStatus == AuthStatus.SUCCESS) {
            GenericPrincipal principal = this.getPrincipal(client);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Authenticated user: " + principal));
            }
            if (principal == null) {
                request.setUserPrincipal(null);
                request.setAuthType(null);
                if (requirePrincipal) {
                    return false;
                }
            } else if (!cachedAuth || !principal.getUserPrincipal().equals(request.getUserPrincipal())) {
                String authTypeValue;
                Boolean register = null;
                String authType = "JASPIC";
                Map map = state.messageInfo.getMap();
                String registerValue = (String)map.get("javax.servlet.http.registerSession");
                if (registerValue != null) {
                    register = Boolean.valueOf(registerValue);
                }
                if ((authTypeValue = (String)map.get("javax.servlet.http.authType")) != null) {
                    authType = authTypeValue;
                }
                if (register != null) {
                    this.register(request, response, principal, authType, null, null, this.alwaysUseSession || register != false, register);
                } else {
                    this.register(request, response, principal, authType, null, null);
                }
            }
            request.setNote("org.apache.catalina.authenticator.jaspic.SUBJECT", client);
            return true;
        }
        return false;
    }

    private GenericPrincipal getPrincipal(Subject subject) {
        if (subject == null) {
            return null;
        }
        Set<GenericPrincipal> principals = subject.getPrivateCredentials(GenericPrincipal.class);
        if (principals.isEmpty()) {
            return null;
        }
        return principals.iterator().next();
    }

    protected boolean checkForCachedAuthentication(Request request, HttpServletResponse response, boolean useSSO) {
        String username;
        Principal principal = request.getUserPrincipal();
        String ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
        if (principal != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("authenticator.check.found", new Object[]{principal.getName()}));
            }
            if (ssoId != null) {
                this.associate(ssoId, request.getSessionInternal(true));
            }
            return true;
        }
        if (useSSO && ssoId != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("authenticator.check.sso", new Object[]{ssoId}));
            }
            if (this.reauthenticateFromSSO(ssoId, request)) {
                return true;
            }
        }
        if (request.getCoyoteRequest().getRemoteUserNeedsAuthorization() && (username = request.getCoyoteRequest().getRemoteUser().toString()) != null) {
            String authType;
            Principal authorized;
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("authenticator.check.authorize", new Object[]{username}));
            }
            if ((authorized = this.context.getRealm().authenticate(username)) == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("authenticator.check.authorizeFail", new Object[]{username}));
                }
                authorized = new GenericPrincipal(username, null, null);
            }
            if ((authType = request.getAuthType()) == null || authType.length() == 0) {
                authType = this.getAuthMethod();
            }
            this.register(request, response, authorized, authType, username, null);
            return true;
        }
        return false;
    }

    protected boolean reauthenticateFromSSO(String ssoId, Request request) {
        Realm realm;
        if (this.sso == null || ssoId == null) {
            return false;
        }
        boolean reauthenticated = false;
        Container parent = this.getContainer();
        if (parent != null && (realm = parent.getRealm()) != null) {
            reauthenticated = this.sso.reauthenticate(ssoId, realm, request);
        }
        if (reauthenticated) {
            this.associate(ssoId, request.getSessionInternal(true));
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Reauthenticated cached principal '" + request.getUserPrincipal().getName() + "' with auth type '" + request.getAuthType() + "'"));
            }
        }
        return reauthenticated;
    }

    public void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password) {
        this.register(request, response, principal, authType, username, password, this.alwaysUseSession, this.cache);
    }

    protected void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password, boolean alwaysUseSession, boolean cache) {
        Session session;
        if (this.log.isDebugEnabled()) {
            String name = principal == null ? "none" : principal.getName();
            this.log.debug((Object)("Authenticated '" + name + "' with type '" + authType + "'"));
        }
        request.setAuthType(authType);
        request.setUserPrincipal(principal);
        if (this.sendAuthInfoResponseHeaders && Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.request.forwarded"))) {
            response.setHeader("remote-user", request.getRemoteUser());
            response.setHeader("auth-type", request.getAuthType());
        }
        if ((session = request.getSessionInternal(false)) != null) {
            if (this.getChangeSessionIdOnAuthentication() && principal != null) {
                String newSessionId = this.changeSessionID(request, session);
                if (session.getNote("org.apache.catalina.authenticator.SESSION_ID") != null) {
                    session.setNote("org.apache.catalina.authenticator.SESSION_ID", newSessionId);
                }
            }
        } else if (alwaysUseSession) {
            session = request.getSessionInternal(true);
        }
        if (session != null && cache) {
            session.setAuthType(authType);
            session.setPrincipal(principal);
        }
        if (this.sso == null) {
            return;
        }
        String ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
        if (ssoId == null) {
            ssoId = this.sessionIdGenerator.generateSessionId();
            Cookie cookie = new Cookie(this.sso.getCookieName(), ssoId);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setSecure(request.isSecure());
            String ssoDomain = this.sso.getCookieDomain();
            if (ssoDomain != null) {
                cookie.setDomain(ssoDomain);
            }
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            response.addCookie(cookie);
            this.sso.register(ssoId, principal, authType, username, password);
            request.setNote("org.apache.catalina.request.SSOID", ssoId);
        } else {
            if (principal == null) {
                this.sso.deregister(ssoId);
                request.removeNote("org.apache.catalina.request.SSOID");
                return;
            }
            this.sso.update(ssoId, principal, authType, username, password);
        }
        if (session == null) {
            session = request.getSessionInternal(true);
        }
        this.sso.associate(ssoId, session);
    }

    protected String changeSessionID(Request request, Session session) {
        String oldId = null;
        if (this.log.isDebugEnabled()) {
            oldId = session.getId();
        }
        String newId = request.changeSessionId();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("authenticator.changeSessionId", new Object[]{oldId, newId}));
        }
        return newId;
    }

    @Override
    public void login(String username, String password, Request request) throws ServletException {
        Principal principal = this.doLogin(request, username, password);
        this.register(request, request.getResponse(), principal, this.getAuthMethod(), username, password);
    }

    protected abstract String getAuthMethod();

    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal p = this.context.getRealm().authenticate(username, password);
        if (p == null) {
            throw new ServletException(sm.getString("authenticator.loginFail"));
        }
        return p;
    }

    @Override
    public void logout(Request request) {
        Principal p;
        AuthConfigProvider provider = this.getJaspicProvider();
        if (provider != null) {
            MessageInfoImpl messageInfo = new MessageInfoImpl(request, request.getResponse(), true);
            Subject client = (Subject)request.getNote("org.apache.catalina.authenticator.jaspic.SUBJECT");
            if (client != null) {
                try {
                    ServerAuthConfig serverAuthConfig = provider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, this.getCallbackHandler());
                    String authContextID = serverAuthConfig.getAuthContextID((MessageInfo)messageInfo);
                    ServerAuthContext serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
                    serverAuthContext.cleanSubject((MessageInfo)messageInfo, client);
                }
                catch (AuthException e) {
                    this.log.debug((Object)sm.getString("authenticator.jaspicCleanSubjectFail"), (Throwable)e);
                }
            }
        }
        if ((p = request.getPrincipal()) instanceof TomcatPrincipal) {
            try {
                ((TomcatPrincipal)p).logout();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.log.debug((Object)sm.getString("authenticator.tomcatPrincipalLogoutFail"), t);
            }
        }
        this.register(request, request.getResponse(), null, null, null, null);
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        ServletContext servletContext = this.context.getServletContext();
        this.jaspicAppContextID = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
        Container parent = this.context.getParent();
        while (this.sso == null && parent != null) {
            Valve[] valves;
            for (Valve valve : valves = parent.getPipeline().getValves()) {
                if (!(valve instanceof SingleSignOn)) continue;
                this.sso = (SingleSignOn)valve;
                break;
            }
            if (this.sso != null) continue;
            parent = parent.getParent();
        }
        if (this.log.isDebugEnabled()) {
            if (this.sso != null) {
                this.log.debug((Object)("Found SingleSignOn Valve at " + this.sso));
            } else {
                this.log.debug((Object)"No SingleSignOn Valve is present");
            }
        }
        this.sessionIdGenerator = new StandardSessionIdGenerator();
        this.sessionIdGenerator.setSecureRandomAlgorithm(this.getSecureRandomAlgorithm());
        this.sessionIdGenerator.setSecureRandomClass(this.getSecureRandomClass());
        this.sessionIdGenerator.setSecureRandomProvider(this.getSecureRandomProvider());
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.sso = null;
    }

    protected boolean isPreemptiveAuthPossible(Request request) {
        return false;
    }

    private AuthConfigProvider getJaspicProvider() {
        Optional<AuthConfigProvider> provider = this.jaspicProvider;
        if (provider == null) {
            provider = this.findJaspicProvider();
        }
        return provider.orElse(null);
    }

    private Optional<AuthConfigProvider> findJaspicProvider() {
        AuthConfigFactory factory = AuthConfigFactory.getFactory();
        Optional<AuthConfigProvider> provider = factory == null ? Optional.empty() : Optional.ofNullable(factory.getConfigProvider("HttpServlet", this.jaspicAppContextID, (RegistrationListener)this));
        this.jaspicProvider = provider;
        return provider;
    }

    public void notify(String layer, String appContext) {
        this.findJaspicProvider();
    }

    protected static enum AllowCorsPreflight {
        NEVER,
        FILTER,
        ALWAYS;

    }

    private static class JaspicState {
        public MessageInfo messageInfo = null;
        public ServerAuthContext serverAuthContext = null;

        private JaspicState() {
        }
    }
}

