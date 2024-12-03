/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.core.pac;

import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import org.apache.commons.lang3.StringUtils;

public class MarketplaceClientConfiguration {
    public static HttpConfiguration.Builder httpConfigurationFromSystemProperties() {
        HttpConfiguration.Builder builder = HttpConfiguration.builder();
        builder.connectTimeoutMillis(Integer.getInteger("http.pac.connectTimeout"));
        builder.readTimeoutMillis(Integer.getInteger("http.pac.readTimeout"));
        String prefix = Sys.getMpacBaseUrl().startsWith("https://") ? "https" : "http";
        Option<String> pacProxyHost = MarketplaceClientConfiguration.getOptString("http.pac.proxyHost");
        Option<String> proxyAuth = MarketplaceClientConfiguration.getOptString("http.pac.proxyAuth").orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyAuth"));
        Option<String> proxyUser = MarketplaceClientConfiguration.getOptString("http.pac.proxyUser").orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyUser"));
        Option<String> proxyPassword = MarketplaceClientConfiguration.getOptString("http.pac.proxyPassword").orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyPassword"));
        if (pacProxyHost.isDefined() || proxyAuth.isDefined() || proxyUser.isDefined()) {
            HttpConfiguration.ProxyConfiguration.Builder proxy = HttpConfiguration.ProxyConfiguration.builder();
            for (String proxyHost : pacProxyHost.orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyHost"))) {
                int proxyPort = Integer.getInteger("http.pac.proxyPort", Integer.getInteger(prefix + ".proxyPort", 80));
                proxy.proxyHost(UpmFugueConverters.fugueSome(new HttpConfiguration.ProxyHost(proxyHost, proxyPort)));
            }
            for (String username : proxyUser) {
                HttpConfiguration.ProxyAuthParams authParams = new HttpConfiguration.ProxyAuthParams(new HttpConfiguration.Credentials(username, proxyPassword.getOrElse("")), (HttpConfiguration.ProxyAuthMethod)((Object)HttpConfiguration.ProxyAuthMethod.fromKey(proxyAuth.getOrElse("basic")).getOrElse((Object)HttpConfiguration.ProxyAuthMethod.BASIC)), UpmFugueConverters.toFugueOption(MarketplaceClientConfiguration.getOptString("http.pac.proxyNtlmDomain").orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyNtlmDomain"))), UpmFugueConverters.toFugueOption(MarketplaceClientConfiguration.getOptString("http.pac.proxyNtlmWorkstation").orElse(MarketplaceClientConfiguration.getOptString(prefix + ".proxyNtlmWorkstation"))));
                proxy.authParams(UpmFugueConverters.fugueSome(authParams));
            }
            builder.proxyConfiguration(UpmFugueConverters.fugueSome(proxy.build()));
        }
        return builder;
    }

    private static Option<String> getOptString(String name) {
        return Option.option(StringUtils.trimToNull((String)System.getProperty(name)));
    }
}

