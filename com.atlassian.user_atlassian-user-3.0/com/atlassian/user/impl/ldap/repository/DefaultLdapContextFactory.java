/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.user.impl.ldap.repository;

import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;
import com.atlassian.user.impl.ldap.repository.LdapConnectionFailedException;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultLdapContextFactory
implements LdapContextFactory {
    private final LdapConnectionProperties connectionProperties;

    public DefaultLdapContextFactory(LdapConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public Hashtable<String, Object> getJNDIEnv() {
        String referral;
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("java.naming.provider.url", this.connectionProperties.getProviderURL());
        result.put("java.naming.factory.initial", this.connectionProperties.getJNDIInitialContextFactoryIdentifier());
        result.put("java.naming.security.authentication", this.connectionProperties.getSecurityAuthentication());
        result.put("java.naming.security.principal", this.connectionProperties.getSecurityPrincipal());
        result.put("java.naming.security.credentials", this.connectionProperties.getSecurityCredential());
        result.put("java.naming.security.protocol", this.connectionProperties.getSecurityProtocol());
        result.put("java.naming.batchsize", Integer.toString(this.connectionProperties.getSearchBatchSize()));
        result.put("com.sun.jndi.ldap.connect.timeout", Integer.toString(this.connectionProperties.getConnectTimeoutMillis()));
        result.put("com.sun.jndi.ldap.read.timeout", Integer.toString(this.connectionProperties.getReadTimeoutMillis()));
        if (this.connectionProperties.isPoolingOn()) {
            result.put("com.sun.jndi.ldap.connect.pool", "true");
        }
        if ((referral = System.getProperty("atlassian.java.naming.referral")) != null) {
            result.put("java.naming.referral", referral);
        }
        return this.createHashtable(result);
    }

    @Override
    public Hashtable getAuthenticationJndiEnvironment(String userDn, String password) {
        HashMap<String, Object> env = new HashMap<String, Object>(this.getJNDIEnv());
        env.put("java.naming.security.principal", userDn);
        env.put("java.naming.security.credentials", password);
        env.put("java.naming.security.authentication", "simple");
        env.put("com.sun.jndi.ldap.connect.pool", "false");
        return this.createHashtable(env);
    }

    private <K, V> Hashtable<K, V> createHashtable(Map<K, V> map) {
        Hashtable<K, V> result = new Hashtable<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue() == null || entry.getKey() == null) continue;
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public DirContext getLDAPContext() throws RepositoryException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_getLDAPContext"));
        }
        try {
            InitialDirContext initialDirContext = new InitialDirContext(this.getJNDIEnv());
            return initialDirContext;
        }
        catch (NamingException e) {
            throw new LdapConnectionFailedException(e);
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_getLDAPContext"));
            }
        }
    }
}

