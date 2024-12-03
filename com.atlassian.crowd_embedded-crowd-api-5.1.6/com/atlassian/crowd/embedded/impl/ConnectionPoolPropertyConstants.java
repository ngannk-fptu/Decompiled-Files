/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.embedded.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

public final class ConnectionPoolPropertyConstants {
    public static final String POOL_INITIAL_SIZE = "com.sun.jndi.ldap.connect.pool.initsize";
    public static final String POOL_MAXIMUM_SIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
    public static final String POOL_PREFERRED_SIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
    public static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
    public static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
    public static final String POOL_AUTHENTICATION = "com.sun.jndi.ldap.connect.pool.authentication";
    public static final String DEFAULT_INITIAL_POOL_SIZE = "1";
    public static final String DEFAULT_MAXIMUM_POOL_SIZE = "0";
    public static final String DEFAULT_PREFERRED_POOL_SIZE = "0";
    public static final String DEFAULT_POOL_TIMEOUT_MS = String.valueOf(Duration.ofMinutes(5L).toMillis());
    public static final String DEFAULT_POOL_PROTOCOL = "plain ssl";
    public static final String DEFAULT_POOL_AUTHENTICATION = "simple";
    public static final Set<String> VALID_PROTOCOL_TYPES = ImmutableSet.of((Object)"plain", (Object)"ssl");
    public static final Set<String> VALID_AUTHENTICATION_TYPES = ImmutableSet.of((Object)"none", (Object)"simple", (Object)"DIGEST-MD5");
    public static final Map<String, String> DEFAULT_PROPERTIES = ImmutableMap.of((Object)"com.sun.jndi.ldap.connect.pool.initsize", (Object)"1", (Object)"com.sun.jndi.ldap.connect.pool.maxsize", (Object)"0", (Object)"com.sun.jndi.ldap.connect.pool.prefsize", (Object)"0", (Object)"com.sun.jndi.ldap.connect.pool.timeout", (Object)DEFAULT_POOL_TIMEOUT_MS, (Object)"com.sun.jndi.ldap.connect.pool.protocol", (Object)"plain ssl", (Object)"com.sun.jndi.ldap.connect.pool.authentication", (Object)"simple");

    private ConnectionPoolPropertyConstants() {
    }
}

