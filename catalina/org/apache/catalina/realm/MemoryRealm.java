/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.RuleSet
 *  org.apache.tomcat.util.file.ConfigFileLoader
 */
package org.apache.catalina.realm;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.MemoryRuleSet;
import org.apache.catalina.realm.RealmBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.file.ConfigFileLoader;

public class MemoryRealm
extends RealmBase {
    private static final Log log = LogFactory.getLog(MemoryRealm.class);
    private static Digester digester = null;
    private static final Object digesterLock = new Object();
    private String pathname = "conf/tomcat-users.xml";
    private final Map<String, GenericPrincipal> principals = new HashMap<String, GenericPrincipal>();

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("memoryRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        GenericPrincipal principal = this.principals.get(username);
        if (principal == null || principal.getPassword() == null) {
            this.getCredentialHandler().mutate(credentials);
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("memoryRealm.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        boolean validated = this.getCredentialHandler().matches(credentials, principal.getPassword());
        if (validated) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("memoryRealm.authenticateSuccess", new Object[]{username}));
            }
            return principal;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("memoryRealm.authenticateFailure", new Object[]{username}));
        }
        return null;
    }

    void addUser(String username, String password, String roles) {
        int comma;
        ArrayList<String> list = new ArrayList<String>();
        roles = roles + ",";
        while ((comma = roles.indexOf(44)) >= 0) {
            String role = roles.substring(0, comma).trim();
            list.add(role);
            roles = roles.substring(comma + 1);
        }
        GenericPrincipal principal = new GenericPrincipal(username, password, list);
        this.principals.put(username, principal);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Digester getDigester() {
        Object object = digesterLock;
        synchronized (object) {
            if (digester == null) {
                digester = new Digester();
                digester.setValidating(false);
                try {
                    digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("memoryRealm.xmlFeatureEncoding"), (Throwable)e);
                }
                digester.addRuleSet((RuleSet)new MemoryRuleSet());
            }
        }
        return digester;
    }

    @Override
    protected String getPassword(String username) {
        GenericPrincipal principal = this.principals.get(username);
        if (principal != null) {
            return principal.getPassword();
        }
        return null;
    }

    @Override
    protected Principal getPrincipal(String username) {
        return this.principals.get(username);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void startInternal() throws LifecycleException {
        String pathName = this.getPathname();
        try (InputStream is = ConfigFileLoader.getSource().getResource(pathName).getInputStream();){
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("memoryRealm.loadPath", new Object[]{pathName}));
            }
            Object object = digesterLock;
            synchronized (object) {
                Digester digester = this.getDigester();
                try {
                    digester.push((Object)this);
                    digester.parse(is);
                }
                catch (Exception e) {
                    throw new LifecycleException(sm.getString("memoryRealm.readXml"), e);
                }
                finally {
                    digester.reset();
                }
            }
        }
        catch (IOException ioe) {
            throw new LifecycleException(sm.getString("memoryRealm.loadExist", new Object[]{pathName}), ioe);
        }
        super.startInternal();
    }
}

