/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.core.net.ProxyConfig;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertiesProxyConfig
implements ProxyConfig {
    private static final Logger log = LoggerFactory.getLogger(SystemPropertiesProxyConfig.class);
    public static final String PROXY_HOST_PROPERTY_NAME = "http.proxyHost";
    public static final String PROXY_PORT_PROPERTY_NAME = "http.proxyPort";
    public static final String PROXY_USER_PROPERTY_NAME = "http.proxyUser";
    public static final String PROXY_PASSWORD_PROPERTY_NAME = "http.proxyPassword";
    public static final String PROXY_NON_HOSTS_PROPERTY_NAME = "http.nonProxyHosts";
    public static final int DEFAULT_PROXY_PORT = 80;
    private final String proxyHost = System.getProperty("http.proxyHost");
    private final int proxyPort = Integer.getInteger("http.proxyPort", 80);
    private final String proxyUser = System.getProperty("http.proxyUser");
    private final String proxyPassword = System.getProperty("http.proxyPassword");
    private final String[] nonProxyHosts = System.getProperty("http.nonProxyHosts", "").split("\\|");

    public SystemPropertiesProxyConfig() {
        if (log.isDebugEnabled()) {
            log.debug("Found nonProxyHosts - " + Arrays.toString(this.nonProxyHosts));
        }
    }

    @Override
    public boolean isSet() {
        return StringUtils.isNotBlank((CharSequence)this.proxyHost);
    }

    @Override
    public boolean requiresAuthentication() {
        return this.isSet() && StringUtils.isNotBlank((CharSequence)this.proxyUser);
    }

    @Override
    public String getHost() {
        return this.proxyHost;
    }

    @Override
    public int getPort() {
        return this.proxyPort;
    }

    @Override
    public String getUser() {
        return this.proxyUser;
    }

    @Override
    public String getPassword() {
        return this.proxyPassword;
    }

    @Override
    public String[] getNonProxyHosts() {
        return this.nonProxyHosts;
    }

    public String toString() {
        return "SystemPropertiesProxyConfig{proxyHost='" + this.proxyHost + '\'' + ", proxyPort=" + this.proxyPort + ", proxyUser='" + this.proxyUser + '\'' + ", proxyPassword='" + this.proxyPassword + '\'' + ", nonProxyHosts=" + Arrays.toString(this.nonProxyHosts) + '}';
    }
}

