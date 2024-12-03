/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties;

import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;

public class DefaultLdapConnectionProperties
implements LdapConnectionProperties {
    public static final int DEFAULT_LDAP_PORT = 389;
    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;
    public static final String DEFAULT_AUTHENTICATION = "simple";
    private static final String LDAPS_PROTOCOL = "ldaps";
    private static final String LDAP_PROTOCOL = "ldap";
    private String securityPrincipal;
    private String securityCredential;
    private String host;
    private int port = 389;
    private String jndiInitialContextFactoryIdentifier;
    private int searchBatchSize = 1000;
    private String securityAuthentication = "simple";
    private String securityProtocol;
    private boolean isPoolingOn = true;
    private int connectTimeoutMillis = 30000;
    private int readTimeoutMillis = 60000;
    private String providerURL;

    public String getSecurityPrincipal() {
        return this.securityPrincipal;
    }

    public String getSecurityCredential() {
        return this.securityCredential;
    }

    public String getProviderURL() {
        if (this.providerURL != null) {
            return this.providerURL;
        }
        return this.getConnectionProtocol() + "://" + this.host + ":" + this.port;
    }

    private String getConnectionProtocol() {
        return this.isSslEnabled() ? LDAPS_PROTOCOL : LDAP_PROTOCOL;
    }

    private boolean isSslEnabled() {
        return this.securityProtocol != null && this.securityProtocol.toLowerCase().indexOf("ssl") != -1;
    }

    public String getJNDIInitialContextFactoryIdentifier() {
        return this.jndiInitialContextFactoryIdentifier;
    }

    public int getSearchBatchSize() {
        return this.searchBatchSize;
    }

    public String getSecurityAuthentication() {
        return this.securityAuthentication;
    }

    public String getSecurityProtocol() {
        return this.securityProtocol;
    }

    public boolean isPoolingOn() {
        return this.isPoolingOn;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setJndiInitialContextFactoryIdentifier(String jndiInitialContextFactoryIdentifier) {
        this.jndiInitialContextFactoryIdentifier = jndiInitialContextFactoryIdentifier;
    }

    public void setSearchBatchSize(int searchBatchSize) {
        this.searchBatchSize = searchBatchSize;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public void setPoolingOn(boolean poolingOn) {
        this.isPoolingOn = poolingOn;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public String toString() {
        return "{ url: " + this.getProviderURL() + ", " + "authentication: '" + this.getSecurityAuthentication() + "', " + "protocol: '" + this.getSecurityProtocol() + "', " + "poolingOn: '" + this.isPoolingOn() + "' }";
    }
}

