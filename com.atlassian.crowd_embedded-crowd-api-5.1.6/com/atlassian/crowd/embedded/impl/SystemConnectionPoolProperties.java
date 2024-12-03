/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum SystemConnectionPoolProperties implements ConnectionPoolProperties
{
    INSTANCE;


    public static ConnectionPoolProperties getInstance() {
        return INSTANCE;
    }

    @Override
    public String getInitialSize() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.initsize");
    }

    @Override
    public String getMaximumSize() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.maxsize");
    }

    @Override
    public String getPreferredSize() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.prefsize");
    }

    @Override
    public String getSupportedProtocol() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.protocol");
    }

    @Override
    public String getTimeoutInSec() {
        String poolTimeoutSysProperty = SystemConnectionPoolProperties.getTimeoutInMillis();
        return poolTimeoutSysProperty != null ? ConnectionPoolPropertyUtil.millisToSeconds(poolTimeoutSysProperty) : null;
    }

    @Override
    public String getSupportedAuthentication() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.authentication");
    }

    @Override
    public Map<String, String> toPropertiesMap() {
        return ImmutableMap.of((Object)"com.sun.jndi.ldap.connect.pool.initsize", (Object)this.getInitialSize(), (Object)"com.sun.jndi.ldap.connect.pool.maxsize", (Object)this.getMaximumSize(), (Object)"com.sun.jndi.ldap.connect.pool.prefsize", (Object)this.getPreferredSize(), (Object)"com.sun.jndi.ldap.connect.pool.timeout", (Object)SystemConnectionPoolProperties.getTimeoutInMillis(), (Object)"com.sun.jndi.ldap.connect.pool.protocol", (Object)this.getSupportedProtocol(), (Object)"com.sun.jndi.ldap.connect.pool.authentication", (Object)this.getSupportedAuthentication());
    }

    private static String getTimeoutInMillis() {
        return System.getProperty("com.sun.jndi.ldap.connect.pool.timeout");
    }
}

