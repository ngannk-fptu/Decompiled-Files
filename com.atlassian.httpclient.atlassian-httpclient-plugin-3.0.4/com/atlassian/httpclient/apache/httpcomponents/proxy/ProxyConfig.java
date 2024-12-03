/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.apache.httpcomponents.proxy;

import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.ProxySelector;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;

public abstract class ProxyConfig {
    public Option<HttpHost> getProxyHost() {
        HttpHost httpHost = (HttpHost)Iterables.getFirst(this.getProxyHosts(), null);
        if (httpHost != null) {
            return Option.some((Object)new HttpHost(httpHost.getHostName(), httpHost.getPort()));
        }
        return Option.none();
    }

    abstract Iterable<HttpHost> getProxyHosts();

    public abstract Iterable<AuthenticationInfo> getAuthenticationInfo();

    public abstract ProxySelector toProxySelector();

    public static class AuthenticationInfo {
        private final AuthScope authScope;
        private final Option<Credentials> credentials;

        public AuthenticationInfo(AuthScope authScope, Option<Credentials> credentials) {
            this.authScope = authScope;
            this.credentials = credentials;
        }

        public AuthScope getAuthScope() {
            return this.authScope;
        }

        public Option<Credentials> getCredentials() {
            return this.credentials;
        }
    }
}

