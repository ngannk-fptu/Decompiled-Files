/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.util;

import java.util.Properties;
import org.apache.log4j.Logger;

public class LDAPConnectionPoolUtils {
    private static final Logger log = Logger.getLogger(LDAPConnectionPoolUtils.class);
    private static boolean initialized;
    public static final String POOL_MAX = "com.sun.jndi.ldap.connect.pool.maxsize";
    public static final String POOL_INITSIZE = "com.sun.jndi.ldap.connect.pool.initsize";
    public static final String POOL_PREFSIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
    public static final String POOL_DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
    public static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
    public static final String POOL_AUTH = "com.sun.jndi.ldap.connect.pool.authentication";
    public static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
    private static Properties connectionPoolProperties;

    public static Properties getConnectionPoolProperties() {
        if (connectionPoolProperties != null) {
            return connectionPoolProperties;
        }
        connectionPoolProperties = new Properties();
        if (connectionPoolProperties.getProperty(POOL_MAX) == null) {
            connectionPoolProperties.setProperty(POOL_MAX, "10");
        }
        if (connectionPoolProperties.getProperty(POOL_INITSIZE) == null) {
            connectionPoolProperties.setProperty(POOL_INITSIZE, "10");
        }
        if (connectionPoolProperties.getProperty(POOL_PREFSIZE) == null) {
            connectionPoolProperties.setProperty(POOL_PREFSIZE, "10");
        }
        if (connectionPoolProperties.getProperty(POOL_DEBUG) == null) {
            connectionPoolProperties.setProperty(POOL_DEBUG, "fine");
        }
        if (connectionPoolProperties.getProperty(POOL_PROTOCOL) == null) {
            connectionPoolProperties.setProperty(POOL_PROTOCOL, "plain ssl");
        }
        if (connectionPoolProperties.getProperty(POOL_AUTH) == null) {
            connectionPoolProperties.setProperty(POOL_AUTH, "none simple DIGEST-MD5");
        }
        if (connectionPoolProperties.getProperty(POOL_TIMEOUT) == null) {
            connectionPoolProperties.setProperty(POOL_TIMEOUT, "3000000");
        }
        return connectionPoolProperties;
    }

    static {
        connectionPoolProperties = null;
    }
}

