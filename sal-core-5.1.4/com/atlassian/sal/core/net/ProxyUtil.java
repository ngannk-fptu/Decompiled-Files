/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.core.net.ProxyConfig;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUtil {
    private static final Logger log = LoggerFactory.getLogger(ProxyUtil.class);

    static boolean requiresAuthentication(ProxyConfig proxyConfig, String requestUrl) {
        if (proxyConfig.getNonProxyHosts().length != 0 && proxyConfig.requiresAuthentication()) {
            try {
                String host = new URI(requestUrl).getHost();
                if (!ProxyUtil.shouldBeProxied(host, proxyConfig.getNonProxyHosts())) {
                    return false;
                }
            }
            catch (URISyntaxException e) {
                log.debug("Can't get host value from {}", (Object)requestUrl);
            }
        }
        return proxyConfig.requiresAuthentication();
    }

    static boolean shouldBeProxied(@Nullable String host, @Nonnull String[] nonProxyHosts) {
        if (StringUtils.isBlank((CharSequence)host)) {
            return false;
        }
        try {
            for (String nonProxyHost : nonProxyHosts) {
                String pattern = nonProxyHost.replace(".", "\\.").replace("*", ".*").replace("[", "\\[").replace("]", "\\]");
                if (!host.matches(pattern)) continue;
                return false;
            }
        }
        catch (Exception e) {
            log.debug("Failed to match host {} against non proxy hosts {}, will assume host should be proxied: {}", new Object[]{host, Arrays.toString(nonProxyHosts), e.getMessage()});
        }
        return true;
    }
}

