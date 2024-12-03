/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties;

public interface LdapConnectionProperties {
    public String getSecurityPrincipal();

    public String getSecurityCredential();

    public String getProviderURL();

    public String getJNDIInitialContextFactoryIdentifier();

    public int getSearchBatchSize();

    public String getSecurityAuthentication();

    public String getSecurityProtocol();

    public boolean isPoolingOn();

    public int getConnectTimeoutMillis();

    public int getReadTimeoutMillis();
}

