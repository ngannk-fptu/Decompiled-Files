/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ldap;

import com.opensymphony.user.Entity;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.CredentialsProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LDAPCredentialsProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ldap$LDAPCredentialsProvider == null ? (class$com$opensymphony$user$provider$ldap$LDAPCredentialsProvider = LDAPCredentialsProvider.class$("com.opensymphony.user.provider.ldap.LDAPCredentialsProvider")) : class$com$opensymphony$user$provider$ldap$LDAPCredentialsProvider));
    private static Map cache = Collections.synchronizedMap(new HashMap());
    Hashtable env;
    String providerName;
    String searchBase;
    String uidSearchName;
    long timeout;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ldap$LDAPCredentialsProvider;

    public boolean authenticate(String name, String password) {
        NamingEnumeration<SearchResult> results;
        InitialDirContext ctx;
        if (password == null || "".equals(password)) {
            return false;
        }
        TimeAndPassword tp = (TimeAndPassword)cache.get(name);
        if (tp != null && tp.password.equals(password) && tp.time > System.currentTimeMillis()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Successful authentication for " + name + " from cached " + this.providerName() + " lookup"));
            }
            return true;
        }
        try {
            ctx = new InitialDirContext(this.env);
        }
        catch (NamingException e) {
            log.error((Object)("Could not connect to " + this.providerName() + ". Please check your " + "host ('" + this.env.get("java.naming.provider.url") + "'), " + "bind DN ('" + this.env.get("java.naming.security.principal") + "') and bind password."), (Throwable)e);
            return this.tryNextCredentialsProviders(name, password);
        }
        StringBuffer filterBuffer = new StringBuffer(this.uidSearchName).append("=").append(name);
        String[] attrIDs = new String[]{this.uidSearchName};
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(2);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Doing initial search: username='" + this.env.get("java.naming.security.principal") + "', password='" + this.env.get("java.naming.security.credentials") + "', base='" + this.searchBase + "', filter='" + filterBuffer + "'"));
        }
        try {
            results = ctx.search(this.searchBase, filterBuffer.toString(), ctls);
        }
        catch (NamingException e) {
            log.error((Object)("Connected to " + this.providerName() + ", but could not perform " + (this.env.containsKey("java.naming.security.principal") ? "authenticated" : "anonymous") + " search from base '" + this.searchBase + "'"));
            return this.tryNextCredentialsProviders(name, password);
        }
        try {
            if (log.isDebugEnabled()) {
                if (results != null && results.hasMore()) {
                    log.debug((Object)"Found user(s)");
                } else {
                    log.debug((Object)"No users found");
                }
            }
            InitialDirContext ctx2 = new InitialDirContext(ctx.getEnvironment());
            while (results != null && results.hasMore()) {
                SearchResult sr = results.next();
                StringBuffer dnBuffer = new StringBuffer();
                dnBuffer.append(sr.getName());
                dnBuffer.append(",");
                dnBuffer.append(this.searchBase);
                try {
                    ctx2.removeFromEnvironment("java.naming.security.principal");
                    ctx2.removeFromEnvironment("java.naming.security.credentials");
                    ctx2.addToEnvironment("java.naming.security.principal", dnBuffer.toString());
                    ctx2.addToEnvironment("java.naming.security.credentials", password);
                }
                catch (NamingException e) {
                    log.error((Object)("Connected and searched " + this.providerName() + ", but encountered unexpected error when switching authentication details."), (Throwable)e);
                    continue;
                }
                ctls = new SearchControls();
                ctls.setReturningAttributes(new String[0]);
                ctls.setSearchScope(0);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Searching below '" + dnBuffer + "' for '" + filterBuffer + "'"));
                }
                try {
                    try {
                        ctx2.search(dnBuffer.toString(), filterBuffer.toString(), ctls);
                    }
                    catch (CommunicationException ex) {
                        log.info((Object)"Second phase connection failed. Trying to reconnect...");
                        ctx2 = new InitialDirContext(ctx2.getEnvironment());
                        ctx2.search(dnBuffer.toString(), filterBuffer.toString(), ctls);
                    }
                }
                catch (AuthenticationException ae) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("User with dn '" + dnBuffer + "' found, but authentication failed."));
                    }
                    return false;
                }
                catch (NamingException e) {
                    log.error((Object)("Initial connect and search successful, but second phase connection to " + this.providerName() + " as '" + dnBuffer + "' failed."), (Throwable)e);
                    continue;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("User '" + name + "' successfully authenticated; caching for " + this.timeout + " ms"));
                }
                cache.put(name, new TimeAndPassword(System.currentTimeMillis() + this.timeout, password));
                return true;
            }
        }
        catch (PartialResultException pre) {
            log.error((Object)("Connected but encountered error checking if " + this.providerName() + " had more results.  For Unprocessed Continuation References, try adding <property name=\"java.naming.referral\">follow</property> to the LDAPCredentialsProvider config."), (Throwable)pre);
        }
        catch (NamingException e) {
            log.error((Object)("Connected but encountered error checking if " + this.providerName() + " had more results."), (Throwable)e);
        }
        return this.tryNextCredentialsProviders(name, password);
    }

    public boolean changePassword(String name, String password) {
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            CredentialsProvider provider = (CredentialsProvider)iterator.next();
            boolean isLDAP = provider instanceof LDAPCredentialsProvider;
            if (isLDAP || !provider.handles(name)) continue;
            return provider.changePassword(name, password);
        }
        return false;
    }

    public boolean create(String name) {
        return false;
    }

    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            LDAPCredentialsProvider other = (LDAPCredentialsProvider)o;
            return this.env.equals(other.env);
        }
        return false;
    }

    public void flushCaches() {
        cache = Collections.synchronizedMap(new HashMap());
    }

    public boolean handles(String name) {
        TimeAndPassword tp = (TimeAndPassword)cache.get(name);
        if (tp != null && tp.time > System.currentTimeMillis()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Cached lookup: Credentials for '" + name + "' could be handled by " + this.providerName()));
            }
            return true;
        }
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        boolean handles = false;
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            CredentialsProvider provider = (CredentialsProvider)iterator.next();
            boolean isLDAP = provider instanceof LDAPCredentialsProvider;
            if (isLDAP || !provider.handles(name)) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("'" + name + "' could be handled by " + this.providerName()));
            }
            handles = true;
            break;
        }
        if (log.isDebugEnabled() && !handles) {
            log.debug((Object)("Credentials for '" + name + "' NOT handled by LDAP, because '" + name + "' not handled by any other credentials provider. Check you have at least one other" + " credentials provider, and that they contain this user."));
        }
        return handles;
    }

    public int hashCode() {
        return this.env.hashCode();
    }

    public boolean init(Properties properties) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Credentials Provider " + this.providerName() + " $Revision: 1.8 $ initializing"));
        }
        this.env = new Hashtable<Object, Object>(properties);
        this.env.put("java.naming.security.authentication", "simple");
        this.searchBase = properties.getProperty("searchBase");
        this.uidSearchName = properties.getProperty("uidSearchName");
        this.providerName = properties.getProperty("providerName");
        try {
            this.timeout = Long.parseLong(properties.getProperty("cacheTimeout"));
        }
        catch (NumberFormatException e) {
            this.timeout = 1800000L;
        }
        return true;
    }

    public List list() {
        return Collections.EMPTY_LIST;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            CredentialsProvider provider = (CredentialsProvider)iterator.next();
            boolean isLDAP = provider instanceof LDAPCredentialsProvider;
            if (isLDAP || !provider.handles(name)) continue;
            return provider.load(name, accessor);
        }
        return true;
    }

    public boolean remove(String name) {
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            CredentialsProvider provider = (CredentialsProvider)iterator.next();
            boolean isLDAP = provider instanceof LDAPCredentialsProvider;
            if (isLDAP || !provider.handles(name)) continue;
            return provider.remove(name);
        }
        return false;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            CredentialsProvider provider = (CredentialsProvider)iterator.next();
            boolean isLDAP = provider instanceof LDAPCredentialsProvider;
            if (isLDAP || !provider.handles(name)) continue;
            return provider.store(name, accessor);
        }
        return true;
    }

    private final String providerName() {
        return this.providerName == null ? "LDAP" : "LDAP provider '" + this.providerName + "'";
    }

    private boolean tryNextCredentialsProviders(String name, String password) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Couldn't authenticate against " + this.providerName() + ", trying other CredentialsProviders"));
        }
        Collection credentialsProviders = UserManager.getInstance().getCredentialsProviders();
        boolean onUntriedProvider = false;
        Iterator iterator = credentialsProviders.iterator();
        while (iterator.hasNext()) {
            boolean result;
            CredentialsProvider nextProvider = (CredentialsProvider)iterator.next();
            if (!onUntriedProvider) {
                if (!this.equals(nextProvider)) continue;
                onUntriedProvider = true;
                continue;
            }
            String nextProviderName = null;
            if (log.isDebugEnabled()) {
                String string = nextProviderName = nextProvider instanceof LDAPCredentialsProvider ? ((LDAPCredentialsProvider)nextProvider).providerName() : nextProvider.getClass().getName();
            }
            if (!nextProvider.handles(name)) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Next provider " + nextProviderName + "' could handle user; checking authentication..."));
            }
            if (result = nextProvider.authenticate(name, password)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("User authenticated by '" + nextProviderName + "'"));
                }
                cache.put(name, new TimeAndPassword(System.currentTimeMillis() + this.timeout, password));
                return true;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Next provider '" + nextProviderName + "' failed to authenticate user."));
            }
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"No non-LDAP authenticators could authenticate this user");
        }
        return false;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class TimeAndPassword {
        public String password;
        public long time;

        public TimeAndPassword(long time, String password) {
            this.time = time;
            this.password = password;
        }
    }
}

