/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyConstants;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultConnectionPoolProperties
implements ConnectionPoolProperties {
    private String initialSize = "1";
    private String preferredSize = "0";
    private String maximumSize = "0";
    private String timeoutInSec = ConnectionPoolPropertyUtil.millisToSeconds(ConnectionPoolPropertyConstants.DEFAULT_POOL_TIMEOUT_MS);
    private String supportedProtocol = "plain ssl";
    private String supportedAuthentication = "simple";

    @Override
    public String getInitialSize() {
        return this.initialSize;
    }

    public void setInitialSize(String initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public String getMaximumSize() {
        return this.maximumSize;
    }

    public void setMaximumSize(String maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public String getPreferredSize() {
        return this.preferredSize;
    }

    public void setPreferredSize(String preferredSize) {
        this.preferredSize = preferredSize;
    }

    @Override
    public String getTimeoutInSec() {
        return this.timeoutInSec;
    }

    public void setTimeoutInSec(String timeoutInSec) {
        this.timeoutInSec = timeoutInSec;
    }

    @Override
    public String getSupportedAuthentication() {
        return this.supportedAuthentication;
    }

    public void setSupportedAuthentication(String supportedAuthentication) {
        this.supportedAuthentication = supportedAuthentication;
    }

    @Override
    public String getSupportedProtocol() {
        return this.supportedProtocol;
    }

    public void setSupportedProtocol(String supportedProtocol) {
        this.supportedProtocol = supportedProtocol;
    }

    @Override
    public Map<String, String> toPropertiesMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("com.sun.jndi.ldap.connect.pool.initsize", this.getInitialSize());
        map.put("com.sun.jndi.ldap.connect.pool.maxsize", this.getMaximumSize());
        map.put("com.sun.jndi.ldap.connect.pool.prefsize", this.getPreferredSize());
        map.put("com.sun.jndi.ldap.connect.pool.timeout", ConnectionPoolPropertyUtil.secondsToMillis(this.getTimeoutInSec()));
        map.put("com.sun.jndi.ldap.connect.pool.protocol", this.getSupportedProtocol());
        map.put("com.sun.jndi.ldap.connect.pool.authentication", this.getSupportedAuthentication());
        return map;
    }

    public static ConnectionPoolProperties fromPropertiesMap(Map<String, String> map) {
        Map<String, String> attributes = DefaultConnectionPoolProperties.clearKeysWithNullValue(map);
        DefaultConnectionPoolProperties poolProperties = new DefaultConnectionPoolProperties();
        poolProperties.setInitialSize(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.initsize", "1"));
        poolProperties.setMaximumSize(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.maxsize", "0"));
        poolProperties.setPreferredSize(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.prefsize", "0"));
        poolProperties.setTimeoutInSec(ConnectionPoolPropertyUtil.millisToSeconds(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.timeout", ConnectionPoolPropertyConstants.DEFAULT_POOL_TIMEOUT_MS)));
        poolProperties.setSupportedProtocol(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl"));
        poolProperties.setSupportedAuthentication(attributes.getOrDefault("com.sun.jndi.ldap.connect.pool.authentication", "simple"));
        return poolProperties;
    }

    private static Map<String, String> clearKeysWithNullValue(Map<String, String> map) {
        return map.entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

