/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.httpclient.api.factory;

import com.atlassian.httpclient.api.factory.Host;
import com.atlassian.httpclient.api.factory.Scheme;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class ProxyOptions {
    private final Map<Scheme, Host> proxyHostMap;
    private final Map<Scheme, List<String>> nonProxyHosts;
    private final ProxyMode proxyMode;

    public Map<Scheme, Host> getProxyHosts() {
        return Collections.unmodifiableMap(this.proxyHostMap);
    }

    public Map<Scheme, List<String>> getNonProxyHosts() {
        return Collections.unmodifiableMap(this.nonProxyHosts);
    }

    public ProxyMode getProxyMode() {
        return this.proxyMode;
    }

    private ProxyOptions(ProxyMode mode, Map<Scheme, Host> proxyHostMap, Map<Scheme, List<String>> nonProxyHosts) {
        this.proxyMode = mode;
        this.proxyHostMap = proxyHostMap;
        this.nonProxyHosts = nonProxyHosts;
    }

    public static class ProxyOptionsBuilder {
        private Map<Scheme, Host> proxyHostMap = new HashMap<Scheme, Host>();
        private Map<Scheme, List<String>> nonProxyHosts = new HashMap<Scheme, List<String>>();
        private ProxyMode proxyMode = ProxyMode.SYSTEM_PROPERTIES;

        public static ProxyOptionsBuilder create() {
            return new ProxyOptionsBuilder();
        }

        public ProxyOptions build() {
            return new ProxyOptions(this.proxyMode, this.proxyHostMap, this.nonProxyHosts);
        }

        public ProxyOptionsBuilder withNoProxy() {
            this.proxyHostMap = ImmutableMap.of();
            this.nonProxyHosts = ImmutableMap.of();
            this.proxyMode = ProxyMode.NO_PROXY;
            return this;
        }

        public ProxyOptionsBuilder withDefaultSystemProperties() {
            this.proxyHostMap = ImmutableMap.of();
            this.nonProxyHosts = ImmutableMap.of();
            this.proxyMode = ProxyMode.SYSTEM_PROPERTIES;
            return this;
        }

        public ProxyOptionsBuilder withProxy(@Nonnull Scheme scheme, @Nonnull Host proxyHost) {
            Preconditions.checkNotNull((Object)proxyHost, (Object)"Proxy host cannot be null");
            Preconditions.checkNotNull((Object)((Object)scheme), (Object)"Scheme must not be null");
            this.proxyHostMap.put(scheme, proxyHost);
            this.proxyMode = ProxyMode.CONFIGURED;
            return this;
        }

        public ProxyOptionsBuilder withNonProxyHost(@Nonnull Scheme scheme, @Nonnull List<String> nonProxyHosts) {
            Preconditions.checkNotNull(nonProxyHosts, (Object)"Non proxy hosts cannot be null");
            Preconditions.checkNotNull((Object)((Object)scheme), (Object)"Scheme must not be null");
            this.nonProxyHosts.put(scheme, nonProxyHosts);
            return this;
        }

        public ProxyOptionsBuilder withProxy(Map<Scheme, Host> proxyHostMap, Map<Scheme, List<String>> nonProxyHosts) {
            this.proxyHostMap = proxyHostMap;
            this.nonProxyHosts = nonProxyHosts;
            this.proxyMode = ProxyMode.CONFIGURED;
            return this;
        }
    }

    public static enum ProxyMode {
        SYSTEM_PROPERTIES,
        CONFIGURED,
        NO_PROXY;

    }
}

