/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ConnectionPoolProperties
 *  com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.core.util;

import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JndiLdapConnectionPoolUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JndiLdapConnectionPoolUtils.class);

    private JndiLdapConnectionPoolUtils() {
    }

    public static void setPersistedJndiLdapPoolSystemProperties(ConnectionPoolProperties persistedValues) {
        persistedValues.toPropertiesMap().forEach((key, dbValue) -> {
            if (System.getProperty(key) != null && dbValue != null && !System.getProperty(key).equals(dbValue)) {
                LOG.info("LDAP connection pool system property: <{}> is overriding persisted value: <{}>", key, dbValue);
            } else if (System.getProperty(key) == null && dbValue != null) {
                LOG.debug("Setting system-wide LDAP connection pool property: <{}> with persisted value: <{}>", key, dbValue);
                System.setProperty(key, dbValue);
            }
        });
    }

    public static void initJndiLdapPools() {
        try {
            if (JndiLdapConnectionPoolUtils.isPoolTimeoutUnlimited()) {
                LOG.warn("JNDI Pool timeout has value <0> (unlimited). This is not recommended as it might cause issues.");
            }
            Class.forName("com.sun.jndi.ldap.LdapPoolManager");
        }
        catch (ClassNotFoundException ex) {
            LOG.error("Jndi LDAP Connection Pool Manager is not available on the classpath", (Throwable)ex);
        }
    }

    public static boolean isPoolTimeoutUnlimited() {
        return ConnectionPoolPropertyUtil.millisToSeconds((String)System.getProperty("com.sun.jndi.ldap.connect.pool.timeout")).equals("0");
    }
}

