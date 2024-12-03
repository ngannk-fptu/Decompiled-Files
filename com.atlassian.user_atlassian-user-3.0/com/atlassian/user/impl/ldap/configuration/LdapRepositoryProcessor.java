/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.configuration;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultRepositoryProcessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.properties.factory.LdapConnectionPropertiesFactory;
import com.atlassian.user.impl.ldap.properties.factory.LdapMembershipPropertiesFactory;
import com.atlassian.user.impl.ldap.properties.factory.LdapSearchPropertiesFactory;
import com.atlassian.user.impl.ldap.repository.DefaultLdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLdapFilterFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LdapRepositoryProcessor
extends DefaultRepositoryProcessor {
    public RepositoryAccessor process(RepositoryConfiguration config) throws ConfigurationException {
        this.setConnectionPoolProperties(config);
        Properties schemaMappingsProperties = this.parseSchemaMappings(config);
        Properties serverProperties = this.parseLDAPServerConfiguration(config);
        LdapConnectionProperties connectionProperties = new LdapConnectionPropertiesFactory().createInstance(serverProperties);
        LdapSearchProperties searchProperties = new LdapSearchPropertiesFactory().createInstance(schemaMappingsProperties);
        LdapMembershipProperties membershipProperties = new LdapMembershipPropertiesFactory().createInstance(schemaMappingsProperties);
        config.addComponent("connectionProperties", connectionProperties);
        config.addComponent("searchProperties", searchProperties);
        config.addComponent("membershipProperties", membershipProperties);
        config.addComponent("schemaMappings", schemaMappingsProperties);
        config.addComponent("server", serverProperties);
        config.addComponent("contextFactory", new DefaultLdapContextFactory(connectionProperties));
        config.addComponent("filterFactory", new DefaultLdapFilterFactory(searchProperties));
        config.addComponent("userAdaptor", this.createBean("userAdaptor", config));
        config.addComponent("userFactory", this.createBean("userFactory", config));
        config.addComponent("groupFactory", this.createBean("groupFactory", config));
        config.addComponent("groupAdaptor", this.createBean("groupAdaptor", config));
        return super.process(config);
    }

    public void setConnectionPoolProperties(RepositoryConfiguration config) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("com.sun.jndi.ldap.connect.pool.maxsize", config.getStringComponent("maxSize"));
        properties.put("com.sun.jndi.ldap.connect.pool.initsize", config.getStringComponent("initSize"));
        properties.put("com.sun.jndi.ldap.connect.pool.prefsize", config.getStringComponent("prefSize"));
        properties.put("com.sun.jndi.ldap.connect.pool.debug", config.getStringComponent("debugLevel"));
        properties.put("com.sun.jndi.ldap.connect.pool.protocol", config.getStringComponent("securityProtocol"));
        properties.put("com.sun.jndi.ldap.connect.pool.authentication", config.getStringComponent("poolAuthentication"));
        properties.put("com.sun.jndi.ldap.connect.pool.timeout", config.getStringComponent("timeout"));
        properties.put("javax.net.ssl.trustStore", config.getStringComponent("trustStore"));
        for (Map.Entry entry : properties.entrySet()) {
            String value = (String)entry.getValue();
            if (value == null) continue;
            System.setProperty((String)entry.getKey(), value);
        }
    }

    public Properties parseLDAPServerConfiguration(RepositoryConfiguration config) {
        String[] propertyKeys;
        Properties serverProperties = new Properties();
        for (String key : propertyKeys = new String[]{"host", "port", "securityPrincipal", "securityCredential", "securityProtocol", "authentication", "baseContext", "batchSize", "initialContextFactory", "poolingOn", "connectTimeout", "readTimeout"}) {
            String value = (String)config.getComponent(key);
            if (value == null) continue;
            serverProperties.put(key, value);
        }
        return serverProperties;
    }

    public Properties parseSchemaMappings(RepositoryConfiguration config) {
        String[] propertyKeys;
        Properties schemaMappingsProperties = new Properties();
        for (String key : propertyKeys = new String[]{"baseUserNamespace", "baseGroupNamespace", "usernameAttribute", "groupnameAttribute", "userSearchFilter", "groupSearchFilter", "firstnameAttribute", "surnameAttribute", "emailAttribute", "membershipAttribute", "userSearchAllDepths", "groupSearchAllDepths", "useUnqualifiedUsernameForMembershipComparison", "timeToLive"}) {
            String value = (String)config.getComponent(key);
            if (value == null) continue;
            schemaMappingsProperties.setProperty(key, value);
        }
        return schemaMappingsProperties;
    }
}

