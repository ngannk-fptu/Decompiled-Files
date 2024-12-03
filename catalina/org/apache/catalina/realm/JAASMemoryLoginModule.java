/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.realm;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.realm.MemoryRuleSet;
import org.apache.catalina.realm.MessageDigestCredentialHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

public class JAASMemoryLoginModule
extends MemoryRealm
implements LoginModule {
    private static final Log log = LogFactory.getLog(JAASMemoryLoginModule.class);
    protected CallbackHandler callbackHandler = null;
    protected boolean committed = false;
    protected Map<String, ?> options = null;
    protected String pathname = "conf/tomcat-users.xml";
    protected Principal principal = null;
    protected Map<String, ?> sharedState = null;
    protected Subject subject = null;

    public JAASMemoryLoginModule() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"MEMORY LOGIN MODULE");
        }
    }

    @Override
    public boolean abort() throws LoginException {
        if (this.principal == null) {
            return false;
        }
        if (this.committed) {
            this.logout();
        } else {
            this.committed = false;
            this.principal = null;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Abort");
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("commit " + this.principal));
        }
        if (this.principal == null) {
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.principal)) {
            this.subject.getPrincipals().add(this.principal);
            if (this.principal instanceof GenericPrincipal) {
                String[] roles;
                for (String role : roles = ((GenericPrincipal)this.principal).getRoles()) {
                    this.subject.getPrincipals().add(new GenericPrincipal(role, null, null));
                }
            }
        }
        this.committed = true;
        return true;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Init");
        }
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        Object option = options.get("pathname");
        if (option instanceof String) {
            this.pathname = (String)option;
        }
        CredentialHandler credentialHandler = null;
        option = options.get("credentialHandlerClassName");
        if (option instanceof String) {
            try {
                Class<?> clazz = Class.forName((String)option);
                credentialHandler = (CredentialHandler)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (credentialHandler == null) {
            credentialHandler = new MessageDigestCredentialHandler();
        }
        for (Map.Entry<String, ?> entry : options.entrySet()) {
            if ("pathname".equals(entry.getKey()) || "credentialHandlerClassName".equals(entry.getKey()) || !(entry.getValue() instanceof String)) continue;
            IntrospectionUtils.setProperty((Object)credentialHandler, (String)entry.getKey(), (String)((String)entry.getValue()));
        }
        this.setCredentialHandler(credentialHandler);
        this.load();
    }

    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException(sm.getString("jaasMemoryLoginModule.noCallbackHandler"));
        }
        Callback[] callbacks = new Callback[]{new NameCallback("Username: "), new PasswordCallback("Password: ", false), new TextInputCallback("nonce"), new TextInputCallback("nc"), new TextInputCallback("cnonce"), new TextInputCallback("qop"), new TextInputCallback("realmName"), new TextInputCallback("digestA2"), new TextInputCallback("algorithm"), new TextInputCallback("authMethod")};
        String username = null;
        String password = null;
        String nonce = null;
        String nc = null;
        String cnonce = null;
        String qop = null;
        String realmName = null;
        String digestA2 = null;
        String algorithm = null;
        String authMethod = null;
        try {
            this.callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] passwordArray = ((PasswordCallback)callbacks[1]).getPassword();
            password = passwordArray == null ? null : new String(passwordArray);
            nonce = ((TextInputCallback)callbacks[2]).getText();
            nc = ((TextInputCallback)callbacks[3]).getText();
            cnonce = ((TextInputCallback)callbacks[4]).getText();
            qop = ((TextInputCallback)callbacks[5]).getText();
            realmName = ((TextInputCallback)callbacks[6]).getText();
            digestA2 = ((TextInputCallback)callbacks[7]).getText();
            algorithm = ((TextInputCallback)callbacks[8]).getText();
            authMethod = ((TextInputCallback)callbacks[9]).getText();
        }
        catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException(sm.getString("jaasMemoryLoginModule.callbackHandlerError", new Object[]{e.toString()}));
        }
        if (authMethod == null) {
            this.principal = super.authenticate(username, password);
        } else if (authMethod.equals("DIGEST")) {
            this.principal = super.authenticate(username, password, nonce, nc, cnonce, qop, realmName, digestA2, algorithm);
        } else if (authMethod.equals("CLIENT_CERT")) {
            this.principal = super.getPrincipal(username);
        } else {
            throw new LoginException(sm.getString("jaasMemoryLoginModule.unknownAuthenticationMethod"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("login " + username + " " + this.principal));
        }
        if (this.principal != null) {
            return true;
        }
        throw new FailedLoginException(sm.getString("jaasMemoryLoginModule.invalidCredentials"));
    }

    @Override
    public boolean logout() throws LoginException {
        this.subject.getPrincipals().remove(this.principal);
        this.committed = false;
        this.principal = null;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void load() {
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            String catalinaBase = this.getCatalinaBase();
            if (catalinaBase == null) {
                log.error((Object)sm.getString("jaasMemoryLoginModule.noCatalinaBase", new Object[]{this.pathname}));
                return;
            }
            file = new File(catalinaBase, this.pathname);
        }
        if (!file.canRead()) {
            log.error((Object)sm.getString("jaasMemoryLoginModule.noConfig", new Object[]{file.getAbsolutePath()}));
            return;
        }
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addRuleSet((RuleSet)new MemoryRuleSet());
        try {
            digester.push((Object)this);
            digester.parse(file);
        }
        catch (Exception e) {
            log.error((Object)sm.getString("jaasMemoryLoginModule.parseError", new Object[]{file.getAbsolutePath()}), (Throwable)e);
        }
        finally {
            digester.reset();
        }
    }

    private String getCatalinaBase() {
        if (this.callbackHandler == null) {
            return null;
        }
        Callback[] callbacks = new Callback[]{new TextInputCallback("catalinaBase")};
        String result = null;
        try {
            this.callbackHandler.handle(callbacks);
            result = ((TextInputCallback)callbacks[0]).getText();
        }
        catch (IOException | UnsupportedCallbackException e) {
            return null;
        }
        return result;
    }
}

