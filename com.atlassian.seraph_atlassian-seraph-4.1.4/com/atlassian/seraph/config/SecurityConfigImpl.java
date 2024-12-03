/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.Initable;
import com.atlassian.seraph.SecurityService;
import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.AuthenticationContextImpl;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.ConfigurationException;
import com.atlassian.seraph.config.DefaultLoginUrlStrategy;
import com.atlassian.seraph.config.DefaultRedirectPolicy;
import com.atlassian.seraph.config.LoginUrlStrategy;
import com.atlassian.seraph.config.RedirectPolicy;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.controller.SecurityController;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.elevatedsecurity.NoopElevatedSecurityGuard;
import com.atlassian.seraph.interceptor.Interceptor;
import com.atlassian.seraph.ioc.ApplicationServicesRegistry;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import com.atlassian.seraph.util.XMLUtils;
import com.opensymphony.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SecurityConfigImpl
implements Serializable,
SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfigImpl.class);
    public static final String DEFAULT_CONFIG_LOCATION = "seraph-config.xml";
    private static final int TWO_WEEKS_IN_SECONDS = Math.toIntExact(Duration.ofDays(14L).getSeconds());
    private final Authenticator authenticator;
    private final ElevatedSecurityGuard elevatedSecurityGuard;
    private final RoleMapper roleMapper;
    private final SecurityController controller;
    private final List<SecurityService> services;
    private final List<Interceptor> interceptors = new CopyOnWriteArrayList<Interceptor>();
    private final String loginURL;
    private final String loginForwardPath;
    private final String logoutURL;
    private final String originalURLKey;
    private final List<String> loginSubmitURL;
    private final String loginCookieKey;
    private final String linkLoginURL;
    private final String authType;
    private final String websudoRequestKey;
    private RedirectPolicy redirectPolicy;
    private boolean insecureCookie;
    private final boolean invalidateSessionOnLogin;
    private final boolean invalidateSessionOnWebsudo;
    private final List<String> invalidateSessionExcludeList;
    private final List<String> invalidateWebsudoSessionExcludeList;
    private final int autoLoginCookieAge;
    private final LoginUrlStrategy loginUrlStrategy;
    private final String loginCookiePath;

    public SecurityConfigImpl(String configFileLocation) throws ConfigurationException {
        if (configFileLocation != null) {
            if (log.isDebugEnabled()) {
                log.debug("Config file location passed.  Location: " + configFileLocation);
            }
        } else {
            configFileLocation = DEFAULT_CONFIG_LOCATION;
            if (log.isDebugEnabled()) {
                log.debug("Initialising securityConfig using default configFile: " + configFileLocation);
            }
        }
        try {
            String[] excludes;
            Element rootEl = this.loadConfigXml(configFileLocation);
            NodeList nl = rootEl.getElementsByTagName("parameters");
            Element parametersEl = (Element)nl.item(0);
            Map<String, String> globalParams = SecurityConfigImpl.getInitParameters(parametersEl);
            this.loginURL = globalParams.get("login.url");
            this.loginForwardPath = globalParams.get("login.forward.path");
            this.linkLoginURL = globalParams.get("link.login.url");
            this.logoutURL = globalParams.get("logout.url");
            this.loginCookiePath = globalParams.get("login.cookie.path");
            this.authType = globalParams.get("authentication.type");
            this.insecureCookie = "true".equals(globalParams.get("insecure.cookie"));
            this.originalURLKey = globalParams.get("original.url.key") != null ? globalParams.get("original.url.key") : "seraph_originalurl";
            this.loginSubmitURL = globalParams.get("login.submit.url") != null ? Arrays.asList(globalParams.get("login.submit.url").split(",")) : new ArrayList<String>();
            this.loginCookieKey = globalParams.get("login.cookie.key") != null ? globalParams.get("login.cookie.key") : "seraph.os.cookie";
            this.websudoRequestKey = globalParams.get("websudo.request.key") != null ? globalParams.get("websudo.request.key") : "seraph.websudo.key";
            this.autoLoginCookieAge = globalParams.get("autologin.cookie.age") != null ? Integer.parseInt(globalParams.get("autologin.cookie.age")) : TWO_WEEKS_IN_SECONDS;
            if (globalParams.get("invalidate.session.on.websudo") != null) {
                this.invalidateSessionOnWebsudo = "true".equalsIgnoreCase(globalParams.get("invalidate.session.on.websudo"));
                if (globalParams.get("invalidate.websudo.session.exclude.list") != null) {
                    excludes = globalParams.get("invalidate.websudo.session.exclude.list").split(",");
                    this.invalidateWebsudoSessionExcludeList = Arrays.asList(excludes);
                } else {
                    this.invalidateWebsudoSessionExcludeList = Collections.emptyList();
                }
            } else {
                this.invalidateSessionOnWebsudo = false;
                this.invalidateWebsudoSessionExcludeList = Collections.emptyList();
            }
            if (globalParams.get("invalidate.session.on.login") != null) {
                this.invalidateSessionOnLogin = "true".equalsIgnoreCase(globalParams.get("invalidate.session.on.login"));
                if (globalParams.get("invalidate.session.exclude.list") != null) {
                    excludes = globalParams.get("invalidate.session.exclude.list").split(",");
                    this.invalidateSessionExcludeList = Arrays.asList(excludes);
                } else {
                    this.invalidateSessionExcludeList = Collections.emptyList();
                }
            } else {
                this.invalidateSessionOnLogin = false;
                this.invalidateSessionExcludeList = Collections.emptyList();
            }
            this.authenticator = this.configureAuthenticator(rootEl);
            this.controller = this.configureController(rootEl);
            this.roleMapper = this.configureRoleMapper(rootEl);
            this.services = Collections.unmodifiableList(this.configureServices(rootEl));
            this.configureInterceptors(rootEl);
            this.loginUrlStrategy = this.configureLoginUrlStrategy(rootEl);
            this.configureRedirectPolicy(rootEl);
            this.elevatedSecurityGuard = this.configureElevatedSecurityGuard(rootEl);
        }
        catch (SAXException e) {
            throw new ConfigurationException("Exception configuring from '" + configFileLocation + "'.", e);
        }
        catch (IOException e) {
            throw new ConfigurationException("Exception configuring from '" + configFileLocation + "'.", e);
        }
        catch (ParserConfigurationException e) {
            throw new ConfigurationException("Exception configuring from '" + configFileLocation + "'.", e);
        }
    }

    private Element loadConfigXml(String configFileLocation) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        URL fileUrl = ClassLoaderUtil.getResource((String)configFileLocation, this.getClass());
        if (fileUrl == null) {
            throw new IllegalArgumentException("No such XML file: " + configFileLocation);
        }
        Document doc = factory.newDocumentBuilder().parse(fileUrl.toString());
        return doc.getDocumentElement();
    }

    protected void configureRedirectPolicy(Element rootEl) throws ConfigurationException {
        this.redirectPolicy = (RedirectPolicy)SecurityConfigImpl.configureClass(rootEl, "redirect-policy", this);
        if (this.redirectPolicy == null) {
            this.redirectPolicy = new DefaultRedirectPolicy();
        }
    }

    private LoginUrlStrategy configureLoginUrlStrategy(Element rootEl) throws ConfigurationException {
        LoginUrlStrategy loginUrlStrategy = (LoginUrlStrategy)SecurityConfigImpl.configureClass(rootEl, "login-url-strategy", this);
        if (loginUrlStrategy == null) {
            loginUrlStrategy = new DefaultLoginUrlStrategy();
        }
        return loginUrlStrategy;
    }

    private Authenticator configureAuthenticator(Element rootEl) throws ConfigurationException {
        Authenticator authenticator = (Authenticator)SecurityConfigImpl.configureClass(rootEl, "authenticator", this);
        if (authenticator == null) {
            throw new ConfigurationException("No authenticator implementation was configured in SecurityConfig.");
        }
        return authenticator;
    }

    private ElevatedSecurityGuard configureElevatedSecurityGuard(Element rootEl) throws ConfigurationException {
        ElevatedSecurityGuard elevatedSecurityGuard = (ElevatedSecurityGuard)SecurityConfigImpl.configureClass(rootEl, "elevatedsecurityguard", this);
        if (elevatedSecurityGuard == null) {
            elevatedSecurityGuard = NoopElevatedSecurityGuard.INSTANCE;
        }
        return elevatedSecurityGuard;
    }

    private SecurityController configureController(Element rootEl) throws ConfigurationException {
        SecurityController controller = (SecurityController)SecurityConfigImpl.configureClass(rootEl, "controller", this);
        try {
            if (controller == null) {
                controller = (SecurityController)ClassLoaderUtil.loadClass((String)SecurityController.NULL_CONTROLLER, this.getClass()).newInstance();
            }
        }
        catch (Exception e) {
            throw new ConfigurationException("Could not lookup class: " + SecurityController.NULL_CONTROLLER, e);
        }
        return controller;
    }

    private RoleMapper configureRoleMapper(Element rootEl) throws ConfigurationException {
        return (RoleMapper)SecurityConfigImpl.configureClass(rootEl, "rolemapper", this);
    }

    private static Initable configureClass(Element rootEl, String tagname, SecurityConfig owner) throws ConfigurationException {
        Initable initable;
        NodeList elementList = rootEl.getElementsByTagName(tagname);
        if (elementList.getLength() == 0) {
            return null;
        }
        Element authEl = (Element)elementList.item(0);
        String clazz = authEl.getAttribute("class");
        if (clazz == null || clazz.trim().length() == 0) {
            return null;
        }
        try {
            initable = (Initable)ClassLoaderUtil.loadClass((String)clazz, owner.getClass()).newInstance();
        }
        catch (InstantiationException ex) {
            throw new ConfigurationException("Unable to instantiate class '" + clazz + "'", ex);
        }
        catch (Exception ex) {
            String message = "Unable to load " + tagname + " class '" + clazz + "': " + ex.getMessage();
            log.error(message, (Throwable)ex);
            throw new ConfigurationException(message, ex);
        }
        try {
            initable.init(SecurityConfigImpl.getInitParameters(authEl), owner);
            return initable;
        }
        catch (Exception ex) {
            String message = "Error caught in initialisation of " + tagname + " class '" + clazz + "': " + ex.getMessage();
            log.error(message, (Throwable)ex);
            throw new ConfigurationException(message, ex);
        }
    }

    private List<SecurityService> configureServices(Element rootEl) throws ConfigurationException {
        NodeList nl = rootEl.getElementsByTagName("services");
        ArrayList<SecurityService> result = new ArrayList<SecurityService>();
        if (nl != null && nl.getLength() > 0) {
            Element servicesEl = (Element)nl.item(0);
            NodeList serviceList = servicesEl.getElementsByTagName("service");
            for (int i = 0; i < serviceList.getLength(); ++i) {
                Element serviceEl = (Element)serviceList.item(i);
                String serviceClazz = serviceEl.getAttribute("class");
                if (serviceClazz == null || "".equals(serviceClazz)) {
                    throw new ConfigurationException("Service element with bad class attribute");
                }
                try {
                    log.debug("Adding seraph service of class: " + serviceClazz);
                    SecurityService service = (SecurityService)ClassLoaderUtil.loadClass((String)serviceClazz, this.getClass()).newInstance();
                    service.init(SecurityConfigImpl.getInitParameters(serviceEl), this);
                    result.add(service);
                    continue;
                }
                catch (Exception e) {
                    throw new ConfigurationException("Could not getRequest service: " + serviceClazz, e);
                }
            }
        }
        return result;
    }

    private void configureInterceptors(Element rootEl) throws ConfigurationException {
        NodeList nl = rootEl.getElementsByTagName("interceptors");
        if (nl != null && nl.getLength() > 0) {
            Element interceptorsEl = (Element)nl.item(0);
            NodeList interceptorList = interceptorsEl.getElementsByTagName("interceptor");
            for (int i = 0; i < interceptorList.getLength(); ++i) {
                Element interceptorEl = (Element)interceptorList.item(i);
                String interceptorClazz = interceptorEl.getAttribute("class");
                if (interceptorClazz == null || "".equals(interceptorClazz)) {
                    throw new ConfigurationException("Interceptor element with bad class attribute");
                }
                try {
                    log.debug("Adding interceptor of class: " + interceptorClazz);
                    Interceptor interceptor = (Interceptor)ClassLoaderUtil.loadClass((String)interceptorClazz, this.getClass()).newInstance();
                    interceptor.init(SecurityConfigImpl.getInitParameters(interceptorEl), this);
                    this.interceptors.add(interceptor);
                    continue;
                }
                catch (Exception e) {
                    throw new ConfigurationException("Could not getRequest service: " + interceptorClazz, e);
                }
            }
        }
    }

    private static Map<String, String> getInitParameters(Element el) {
        HashMap<String, String> params = new HashMap<String, String>();
        NodeList nl = el.getElementsByTagName("init-param");
        for (int i = 0; i < nl.getLength(); ++i) {
            Node initParam = nl.item(i);
            String paramName = XMLUtils.getContainedText(initParam, "param-name");
            String paramValue = XMLUtils.getContainedText(initParam, "param-value");
            params.put(paramName, paramValue);
        }
        return Collections.unmodifiableMap(params);
    }

    @Override
    public void destroy() {
        Iterator<Initable> iterator = this.services.iterator();
        while (iterator.hasNext()) {
            SecurityService securityService;
            SecurityService securityService2 = securityService = iterator.next();
            securityService2.destroy();
        }
        for (Initable initable : this.interceptors) {
            ((Interceptor)initable).destroy();
        }
    }

    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    @Override
    public List<SecurityService> getServices() {
        return this.services;
    }

    @Override
    public String getLoginURL() {
        return this.getLoginURL(false, false);
    }

    @Override
    public String getLoginURL(boolean forUserRole, boolean forPageCaps) {
        String loginUrl = this.loginUrlStrategy.getLoginURL(this, this.loginURL);
        if (!forUserRole) {
            loginUrl = loginUrl.replaceAll("\\$\\{userRole\\}", "");
        }
        if (!forPageCaps) {
            loginUrl = loginUrl.replaceAll("\\$\\{pageCaps\\}", "");
        }
        return loginUrl;
    }

    @Override
    public String getLoginForwardPath() {
        return this.loginForwardPath;
    }

    @Override
    public String getLinkLoginURL() {
        return this.loginUrlStrategy.getLinkLoginURL(this, this.linkLoginURL);
    }

    @Override
    public String getLogoutURL() {
        return this.loginUrlStrategy.getLogoutURL(this, this.logoutURL);
    }

    @Override
    public String getOriginalURLKey() {
        return this.originalURLKey;
    }

    @Override
    public List<String> getLoginSubmitURL() {
        return this.loginSubmitURL;
    }

    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    @Override
    public AuthenticationContext getAuthenticationContext() {
        return new AuthenticationContextImpl();
    }

    @Override
    public SecurityController getController() {
        return this.controller;
    }

    @Override
    public RoleMapper getRoleMapper() {
        return this.roleMapper;
    }

    @Override
    public RedirectPolicy getRedirectPolicy() {
        return this.redirectPolicy;
    }

    @Override
    public <T extends Interceptor> List<T> getInterceptors(Class<T> desiredInterceptorClass) {
        ArrayList<T> result = new ArrayList<T>();
        for (Interceptor interceptor : this.interceptors) {
            if (!desiredInterceptorClass.isAssignableFrom(interceptor.getClass())) continue;
            result.add(desiredInterceptorClass.cast(interceptor));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String getLoginCookiePath() {
        return this.loginCookiePath;
    }

    @Override
    public String getLoginCookieKey() {
        return this.loginCookieKey;
    }

    @Override
    public String getWebsudoRequestKey() {
        return this.websudoRequestKey;
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    @Override
    public boolean isInsecureCookie() {
        return this.insecureCookie;
    }

    @Override
    public int getAutoLoginCookieAge() {
        return this.autoLoginCookieAge;
    }

    @Override
    public ElevatedSecurityGuard getElevatedSecurityGuard() {
        return this.elevatedSecurityGuard;
    }

    @Override
    public RememberMeService getRememberMeService() {
        return ApplicationServicesRegistry.getRememberMeService();
    }

    @Override
    public boolean isInvalidateSessionOnLogin() {
        return this.invalidateSessionOnLogin;
    }

    @Override
    public boolean isInvalidateSessionOnWebsudo() {
        return this.invalidateSessionOnWebsudo;
    }

    @Override
    public List<String> getInvalidateSessionExcludeList() {
        return this.invalidateSessionExcludeList;
    }

    @Override
    public List<String> getInvalidateWebsudoSessionExcludeList() {
        return this.invalidateWebsudoSessionExcludeList;
    }
}

