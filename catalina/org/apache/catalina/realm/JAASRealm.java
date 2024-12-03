/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.realm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JAASCallbackHandler;
import org.apache.catalina.realm.RealmBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

public class JAASRealm
extends RealmBase {
    private static final Log log = LogFactory.getLog(JAASRealm.class);
    protected String appName = null;
    protected final List<String> roleClasses = new ArrayList<String>();
    protected final List<String> userClasses = new ArrayList<String>();
    protected boolean useContextClassLoader = true;
    protected String configFile;
    protected volatile Configuration jaasConfiguration;
    protected volatile boolean jaasConfigurationLoaded = false;
    private volatile boolean invocationSuccess = true;
    protected String roleClassNames = null;
    protected String userClassNames = null;

    public String getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setAppName(String name) {
        this.appName = name;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setUseContextClassLoader(boolean useContext) {
        this.useContextClassLoader = useContext;
    }

    public boolean isUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    @Override
    public void setContainer(Container container) {
        super.setContainer(container);
        if (this.appName == null) {
            this.appName = this.makeLegalForJAAS(container.getName());
            log.info((Object)sm.getString("jaasRealm.appName", new Object[]{this.appName}));
        }
    }

    public String getRoleClassNames() {
        return this.roleClassNames;
    }

    public void setRoleClassNames(String roleClassNames) {
        this.roleClassNames = roleClassNames;
    }

    protected void parseClassNames(String classNamesString, List<String> classNamesList) {
        String[] classNames;
        classNamesList.clear();
        if (classNamesString == null) {
            return;
        }
        ClassLoader loader = this.getClass().getClassLoader();
        if (this.isUseContextClassLoader()) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        for (String className : classNames = classNamesString.split("[ ]*,[ ]*")) {
            if (className.length() == 0) continue;
            try {
                Class<?> principalClass = Class.forName(className, false, loader);
                if (Principal.class.isAssignableFrom(principalClass)) {
                    classNamesList.add(className);
                    continue;
                }
                log.error((Object)sm.getString("jaasRealm.notPrincipal", new Object[]{className}));
            }
            catch (ClassNotFoundException e) {
                log.error((Object)sm.getString("jaasRealm.classNotFound", new Object[]{className}));
            }
        }
    }

    public String getUserClassNames() {
        return this.userClassNames;
    }

    public void setUserClassNames(String userClassNames) {
        this.userClassNames = userClassNames;
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        return this.authenticate(username, new JAASCallbackHandler(this, username, credentials));
    }

    @Override
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm) {
        return this.authenticate(username, new JAASCallbackHandler(this, username, clientDigest, nonce, nc, cnonce, qop, realmName, digestA2, algorithm, "DIGEST"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected Principal authenticate(String username, CallbackHandler callbackHandler) {
        try {
            Principal principal;
            LoginContext loginContext = null;
            if (this.appName == null) {
                this.appName = "Tomcat";
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jaasRealm.beginLogin", new Object[]{username, this.appName}));
            }
            ClassLoader ocl = null;
            Thread currentThread = null;
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            try {
                Configuration config = this.getConfig();
                loginContext = new LoginContext(this.appName, null, callbackHandler, config);
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                log.error((Object)sm.getString("jaasRealm.unexpectedError"), e);
                this.invocationSuccess = false;
                Principal principal2 = null;
                return principal2;
            }
            finally {
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Login context created " + username));
            }
            Subject subject = null;
            try {
                loginContext.login();
                subject = loginContext.getSubject();
                this.invocationSuccess = true;
                if (subject == null) {
                    if (!log.isDebugEnabled()) return null;
                    log.debug((Object)sm.getString("jaasRealm.failedLogin", new Object[]{username}));
                    return null;
                }
            }
            catch (AccountExpiredException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jaasRealm.accountExpired", new Object[]{username}));
                }
                this.invocationSuccess = true;
                return null;
            }
            catch (CredentialExpiredException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jaasRealm.credentialExpired", new Object[]{username}));
                }
                this.invocationSuccess = true;
                return null;
            }
            catch (FailedLoginException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jaasRealm.failedLogin", new Object[]{username}));
                }
                this.invocationSuccess = true;
                return null;
            }
            catch (LoginException e) {
                log.warn((Object)sm.getString("jaasRealm.loginException", new Object[]{username}), (Throwable)e);
                this.invocationSuccess = true;
                return null;
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                log.error((Object)sm.getString("jaasRealm.unexpectedError"), e);
                this.invocationSuccess = false;
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jaasRealm.loginContextCreated", new Object[]{username}));
            }
            if ((principal = this.createPrincipal(username, subject, loginContext)) == null) {
                log.debug((Object)sm.getString("jaasRealm.authenticateFailure", new Object[]{username}));
                return null;
            }
            if (!log.isDebugEnabled()) return principal;
            log.debug((Object)sm.getString("jaasRealm.authenticateSuccess", new Object[]{username, principal}));
            return principal;
        }
        catch (Throwable t) {
            log.error((Object)"error ", t);
            this.invocationSuccess = false;
            return null;
        }
    }

    @Override
    protected String getPassword(String username) {
        return null;
    }

    @Override
    protected Principal getPrincipal(String username) {
        return this.authenticate(username, new JAASCallbackHandler(this, username, null, null, null, null, null, null, null, null, "CLIENT_CERT"));
    }

    protected Principal createPrincipal(String username, Subject subject, LoginContext loginContext) {
        ArrayList<String> roles = new ArrayList<String>();
        Principal userPrincipal = null;
        for (Principal principal : subject.getPrincipals()) {
            String principalClass = principal.getClass().getName();
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jaasRealm.checkPrincipal", new Object[]{principal, principalClass}));
            }
            if (userPrincipal == null && this.userClasses.contains(principalClass)) {
                userPrincipal = principal;
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("jaasRealm.userPrincipalSuccess", new Object[]{principal.getName()}));
                }
            }
            if (!this.roleClasses.contains(principalClass)) continue;
            roles.add(principal.getName());
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)sm.getString("jaasRealm.rolePrincipalAdd", new Object[]{principal.getName()}));
        }
        if (userPrincipal == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jaasRealm.userPrincipalFailure"));
                log.debug((Object)sm.getString("jaasRealm.rolePrincipalFailure"));
            }
            return null;
        }
        if (roles.size() == 0 && log.isDebugEnabled()) {
            log.debug((Object)sm.getString("jaasRealm.rolePrincipalFailure"));
        }
        return new GenericPrincipal(username, null, roles, userPrincipal, loginContext);
    }

    protected String makeLegalForJAAS(String src) {
        String result = src;
        if (result == null) {
            result = "other";
        }
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }

    @Override
    protected void startInternal() throws LifecycleException {
        this.parseClassNames(this.userClassNames, this.userClasses);
        this.parseClassNames(this.roleClassNames, this.roleClasses);
        super.startInternal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Configuration getConfig() {
        String configFile = this.configFile;
        try {
            if (this.jaasConfigurationLoaded) {
                return this.jaasConfiguration;
            }
            JAASRealm jAASRealm = this;
            synchronized (jAASRealm) {
                Configuration config;
                if (configFile == null) {
                    this.jaasConfigurationLoaded = true;
                    return null;
                }
                URL resource = Thread.currentThread().getContextClassLoader().getResource(configFile);
                URI uri = resource.toURI();
                Class<?> sunConfigFile = Class.forName("com.sun.security.auth.login.ConfigFile");
                Constructor<?> constructor = sunConfigFile.getConstructor(URI.class);
                this.jaasConfiguration = config = (Configuration)constructor.newInstance(uri);
                this.jaasConfigurationLoaded = true;
                return this.jaasConfiguration;
            }
        }
        catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause());
        }
        catch (IllegalArgumentException | ReflectiveOperationException | SecurityException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isAvailable() {
        return this.invocationSuccess;
    }
}

