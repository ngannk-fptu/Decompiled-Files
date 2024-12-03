/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.apache.httpcomponents.proxy;

import com.atlassian.httpclient.apache.httpcomponents.proxy.ProvidedProxyConfig;
import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfig;
import com.atlassian.httpclient.apache.httpcomponents.proxy.SystemPropertiesProxyConfig;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Option;
import org.apache.http.HttpHost;

public class ProxyConfigFactory {
    public static Option<HttpHost> getProxyHost(HttpClientOptions options) {
        return (Option)ProxyConfigFactory.getProxyConfig(options).fold(Option::none, ProxyConfig::getProxyHost);
    }

    public static Iterable<ProxyConfig.AuthenticationInfo> getProxyAuthentication(HttpClientOptions options) {
        return (Iterable)ProxyConfigFactory.getProxyConfig(options).fold(Lists::newLinkedList, ProxyConfig::getAuthenticationInfo);
    }

    public static Option<ProxyConfig> getProxyConfig(HttpClientOptions options) {
        Option config;
        switch (options.getProxyOptions().getProxyMode()) {
            case SYSTEM_PROPERTIES: {
                config = Option.some((Object)new SystemPropertiesProxyConfig());
                break;
            }
            case CONFIGURED: {
                config = Option.some((Object)new ProvidedProxyConfig(options.getProxyOptions().getProxyHosts(), options.getProxyOptions().getNonProxyHosts()));
                break;
            }
            default: {
                config = Option.none();
            }
        }
        return config;
    }
}

