/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.properties.factory;

import com.atlassian.user.impl.ldap.properties.DefaultLdapConnectionProperties;
import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;
import java.util.Properties;
import org.apache.log4j.Logger;

public class LdapConnectionPropertiesFactory {
    protected final Logger log = Logger.getLogger(this.getClass());

    public LdapConnectionProperties createInstance(Properties properties) {
        DefaultLdapConnectionProperties result = new DefaultLdapConnectionProperties();
        result.setHost(properties.getProperty("host", "localhost"));
        result.setJndiInitialContextFactoryIdentifier(properties.getProperty("initialContextFactory"));
        result.setPort(this.getInt(properties, "port", 389, "LDAP server port"));
        result.setSearchBatchSize(this.getInt(properties, "batchSize", 1000, "LDAP search batch size"));
        result.setConnectTimeoutMillis(this.getInt(properties, "connectTimeout", 30000, "LDAP connection timeout"));
        result.setReadTimeoutMillis(this.getInt(properties, "readTimeout", 60000, "LDAP read timeout"));
        result.setSecurityAuthentication(properties.getProperty("authentication", "simple"));
        result.setSecurityCredential(properties.getProperty("securityCredential"));
        result.setSecurityPrincipal(properties.getProperty("securityPrincipal"));
        result.setSecurityProtocol(properties.getProperty("securityProtocol"));
        String poolingOn = properties.getProperty("poolingOn", "true");
        result.setPoolingOn(Boolean.valueOf(poolingOn));
        result.setProviderURL(properties.getProperty("providerUrl"));
        return result;
    }

    private int getInt(Properties properties, String key, int defaultValue, String description) {
        String value = properties.getProperty(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            this.log.warn((Object)("Error parsing " + description + " in configuration file, using default value"), (Throwable)e);
            return defaultValue;
        }
    }
}

