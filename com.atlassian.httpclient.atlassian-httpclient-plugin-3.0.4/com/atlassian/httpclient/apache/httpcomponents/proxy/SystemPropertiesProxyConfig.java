/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.httpclient.apache.httpcomponents.proxy;

import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfig;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import java.net.ProxySelector;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public class SystemPropertiesProxyConfig
extends ProxyConfig {
    private static final Iterable<String> SUPPORTED_SCHEMAS = Lists.newArrayList((Object[])new String[]{"http", "https"});

    @Override
    Iterable<HttpHost> getProxyHosts() {
        return Options.flatten((Iterable)Options.filterNone((Iterable)Iterables.transform(SUPPORTED_SCHEMAS, SystemPropertiesProxyConfig::getProxy)));
    }

    @Override
    public Iterable<ProxyConfig.AuthenticationInfo> getAuthenticationInfo() {
        return Iterables.transform(this.getProxyHosts(), httpHost -> {
            AuthScope authScope = new AuthScope((HttpHost)httpHost);
            Option<Credentials> credentials = SystemPropertiesProxyConfig.credentialsForScheme(httpHost.getSchemeName());
            return new ProxyConfig.AuthenticationInfo(authScope, credentials);
        });
    }

    @Override
    public ProxySelector toProxySelector() {
        return ProxySelector.getDefault();
    }

    private static Option<HttpHost> getProxy(String schemeName) {
        String proxyHost = System.getProperty(schemeName + ".proxyHost");
        if (proxyHost != null) {
            return Option.some((Object)new HttpHost(proxyHost, Integer.parseInt(System.getProperty(schemeName + ".proxyPort")), schemeName));
        }
        return Option.none();
    }

    private static Option<Credentials> credentialsForScheme(String schemeName) {
        String username = System.getProperty(schemeName + ".proxyUser");
        if (username != null) {
            String proxyPassword = System.getProperty(schemeName + ".proxyPassword");
            String proxyAuth = System.getProperty(schemeName + ".proxyAuth");
            if (proxyAuth == null || proxyAuth.equalsIgnoreCase("basic")) {
                return Option.some((Object)new UsernamePasswordCredentials(username, proxyPassword));
            }
            if (proxyAuth.equalsIgnoreCase("digest") || proxyAuth.equalsIgnoreCase("ntlm")) {
                String ntlmDomain = System.getProperty(schemeName + ".proxyNtlmDomain");
                String ntlmWorkstation = System.getProperty(schemeName + ".proxyNtlmWorkstation");
                return Option.some((Object)new NTCredentials(username, proxyPassword, StringUtils.defaultString((String)ntlmWorkstation), StringUtils.defaultString((String)ntlmDomain)));
            }
            return Option.none();
        }
        return Option.none();
    }
}

