/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.util.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

@Deprecated(forRemoval=true)
public class HttpProxyConfiguration {
    private static final Logger log = LoggerFactory.getLogger(HttpProxyConfiguration.class);
    public static final String PROXY_HOST_PROPERTY = "http.proxyHost";
    public static final String PROXY_PORT_PROPERTY = "http.proxyPort";
    public static final String PROXY_USERNAME_PROPERTY = "http.proxyUser";
    public static final String PROXY_PASSWORD_PROPERTY = "http.proxyPassword";
    public static final String NTLM_DOMAIN_PROPERTY = "http.auth.ntlm.domain";
    public static final String NON_PROXY_HOSTS_PROPERTY = "http.nonProxyHosts";
    public static final String PROXY_AUTHENTICATION = "http.proxyAuth";
    private final String host;
    private final int port;
    private final List<ProxyAuthentication> authentication;
    private final String username;
    private final String password;
    private final String ntlmDomain;
    private final String[] nonProxyHosts;

    public static HttpProxyConfiguration fromSystemProperties() {
        return HttpProxyConfiguration.fromProperties(System.getProperties());
    }

    public static HttpProxyConfiguration fromProperties(Properties properties) {
        int port = 80;
        try {
            port = Integer.parseInt(properties.getProperty(PROXY_PORT_PROPERTY, "80"));
        }
        catch (NumberFormatException e) {
            log.warn("Property 'http.proxyPort' is not a number. Defaulting to 80.");
        }
        String httpNonProxyHosts = properties.getProperty(NON_PROXY_HOSTS_PROPERTY, "");
        String singleHttpNonProxyHost = properties.getProperty("http.nonProxyHost");
        if (StringUtils.isBlank((CharSequence)httpNonProxyHosts) && StringUtils.isNotBlank((CharSequence)singleHttpNonProxyHost)) {
            log.warn("Property http.nonProxyHost is set. You probably meant to set http.nonProxyHosts.");
            httpNonProxyHosts = singleHttpNonProxyHost;
        }
        String[] nonProxyHosts = httpNonProxyHosts.split("\\|");
        List<ProxyAuthentication> authentication = HttpProxyConfiguration.parseAuthentication(properties.getProperty(PROXY_AUTHENTICATION, ""));
        if (authentication == null || authentication.isEmpty()) {
            authentication = Arrays.asList(ProxyAuthentication.NTLM, ProxyAuthentication.DIGEST, ProxyAuthentication.BASIC);
        }
        return new HttpProxyConfiguration(properties.getProperty(PROXY_HOST_PROPERTY), port, authentication, properties.getProperty(PROXY_USERNAME_PROPERTY), properties.getProperty(PROXY_PASSWORD_PROPERTY), properties.getProperty(NTLM_DOMAIN_PROPERTY), nonProxyHosts);
    }

    private static List<ProxyAuthentication> parseAuthentication(String propertyValue) {
        String[] values;
        if (StringUtils.isBlank((CharSequence)propertyValue)) {
            return Collections.emptyList();
        }
        ArrayList<ProxyAuthentication> authentication = new ArrayList<ProxyAuthentication>();
        for (String value : values = propertyValue.split(",")) {
            try {
                authentication.add(ProxyAuthentication.valueOf(value.trim().toUpperCase()));
            }
            catch (IllegalArgumentException e) {
                log.warn("Could not parse proxy authentication type: " + value + ". Possible values: " + StringUtils.join((Object[])ProxyAuthentication.values(), (String)", ") + ". Check the value of the http.proxyAuth property.");
            }
        }
        return authentication;
    }

    private HttpProxyConfiguration(String host, int port, List<ProxyAuthentication> authentication, String username, String password, String ntlmDomain, String[] nonProxyHosts) {
        Assert.notNull(authentication, (String)"Authentication cannot be null");
        this.host = host;
        this.port = port;
        this.authentication = authentication;
        this.username = username;
        this.password = password;
        this.ntlmDomain = ntlmDomain;
        this.nonProxyHosts = nonProxyHosts;
    }

    private boolean shouldProxyHostInternal(String destinationHost) {
        if (StringUtils.isBlank((CharSequence)destinationHost)) {
            return false;
        }
        try {
            for (String nonProxyHost : this.nonProxyHosts) {
                String pattern = nonProxyHost.replace(".", "\\.").replace("*", ".*").replace("[", "\\[").replace("]", "\\]");
                if (!destinationHost.matches(pattern)) continue;
                return false;
            }
        }
        catch (Exception e) {
            log.debug("Failed to match host {} against non proxy hosts {}, will assume host should be proxied: {}", new Object[]{destinationHost, Arrays.toString(this.nonProxyHosts), e.getMessage()});
        }
        return true;
    }

    public boolean isProxyConfigured() {
        return this.host != null;
    }

    public boolean shouldProxy(String destinationHost) {
        return this.isProxyConfigured() && this.shouldProxyHostInternal(destinationHost);
    }

    public boolean hasBasicAuthentication() {
        return this.isProxyConfigured() && this.username != null;
    }

    public boolean hasDigestAuthentication() {
        return this.isProxyConfigured() && this.username != null;
    }

    public boolean hasNtlmAuthentication() {
        return this.isProxyConfigured() && this.ntlmDomain != null;
    }

    public List<ProxyAuthentication> getAuthentication() {
        return Collections.unmodifiableList(this.authentication);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNtlmDomain() {
        return this.ntlmDomain;
    }

    public static enum ProxyAuthentication {
        BASIC,
        DIGEST,
        NTLM;

    }
}

