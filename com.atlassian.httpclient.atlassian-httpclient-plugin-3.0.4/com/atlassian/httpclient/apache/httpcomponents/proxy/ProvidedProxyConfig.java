/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.httpclient.apache.httpcomponents.proxy;

import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfig;
import com.atlassian.httpclient.api.factory.Host;
import com.atlassian.httpclient.api.factory.Scheme;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvidedProxyConfig
extends ProxyConfig {
    private static final Logger log = LoggerFactory.getLogger(ProvidedProxyConfig.class);
    private static final List<Proxy> NO_PROXIES = Collections.singletonList(Proxy.NO_PROXY);
    private static final Iterable<String> SUPPORTED_SCHEMAS = Lists.newArrayList((Object[])new String[]{"http", "https"});
    private final Map<String, HttpHost> proxyHostMap;
    private final Map<String, Predicate<String>> nonProxyHosts;

    public ProvidedProxyConfig(@Nonnull Map<Scheme, Host> proxyHostMap, @Nonnull Map<Scheme, List<String>> nonProxyHosts) {
        Preconditions.checkNotNull(proxyHostMap);
        Preconditions.checkNotNull(nonProxyHosts);
        this.proxyHostMap = new HashMap<String, HttpHost>(proxyHostMap.size());
        for (Scheme s : proxyHostMap.keySet()) {
            Host h = proxyHostMap.get((Object)s);
            this.proxyHostMap.put(s.schemeName(), new HttpHost(h.getHost(), h.getPort()));
        }
        this.nonProxyHosts = new HashMap<String, Predicate<String>>(nonProxyHosts.size());
        for (Scheme scheme : nonProxyHosts.keySet()) {
            List<String> nonProxyHostList = nonProxyHosts.get((Object)scheme);
            if (nonProxyHostList == null) continue;
            Pattern wildcardHostsPattern = this.getWildcardHostsPattern(nonProxyHostList);
            Set<String> literalHosts = this.getLiteralHosts(nonProxyHostList);
            this.nonProxyHosts.put(scheme.schemeName(), host -> literalHosts.contains(host) || wildcardHostsPattern != null && wildcardHostsPattern.matcher((CharSequence)host).matches());
        }
    }

    private Set<String> getLiteralHosts(List<String> nonProxyHosts) {
        Set<String> literalHosts = nonProxyHosts.stream().filter(host -> host.indexOf(42) == -1).map(String::toLowerCase).collect(Collectors.toSet());
        log.trace("Literal hosts for http.nonProxyHost: {}", literalHosts);
        return literalHosts;
    }

    private Pattern getWildcardHostsPattern(List<String> nonProxyHosts) {
        String compoundPattern = nonProxyHosts.stream().filter(host -> host.indexOf(42) != -1).map(String::toLowerCase).map(this::hostWildcardToPattern).filter(Objects::nonNull).map(subPattern -> "(:?" + subPattern + ")").collect(Collectors.joining("|"));
        try {
            if (compoundPattern.isEmpty()) {
                return null;
            }
            log.trace("Compound pattern for http.nonProxyHost wildcard values {}: {}", nonProxyHosts, (Object)compoundPattern);
            return Pattern.compile(compoundPattern);
        }
        catch (PatternSyntaxException e) {
            log.warn("Ignoring http.nonProxyHost values \"{}\" because converting these to a regular expression failed", nonProxyHosts, (Object)e);
            return null;
        }
    }

    @Override
    Iterable<HttpHost> getProxyHosts() {
        Iterable httpHosts = Iterables.transform(SUPPORTED_SCHEMAS, schema -> Option.option((Object)this.proxyHostMap.get(schema)));
        return Options.flatten((Iterable)Options.filterNone((Iterable)httpHosts));
    }

    @Override
    public Iterable<ProxyConfig.AuthenticationInfo> getAuthenticationInfo() {
        log.info("Authentication info not supported for ProvidedProxyConfig");
        return Collections.emptyList();
    }

    @Override
    public ProxySelector toProxySelector() {
        return new ProxySelector(){

            @Override
            public List<Proxy> select(URI uri) {
                String scheme = uri.getScheme().toLowerCase();
                String host = uri.getHost().toLowerCase();
                HttpHost proxyHost = (HttpHost)ProvidedProxyConfig.this.proxyHostMap.get(scheme);
                if (proxyHost == null) {
                    return NO_PROXIES;
                }
                if (ProvidedProxyConfig.this.nonProxyMatch(scheme, host).booleanValue()) {
                    return NO_PROXIES;
                }
                return Collections.singletonList(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost.getHostName(), proxyHost.getPort())));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        };
    }

    private String compileHostPattern(String wildcardHost, String pattern) {
        String regex = "^" + pattern + "$";
        try {
            Pattern.compile(regex);
        }
        catch (PatternSyntaxException e) {
            log.warn("Ignoring http.nonProxyHost \"{}\" because converting it to a regular expression failed", (Object)wildcardHost, (Object)e);
            return null;
        }
        return regex;
    }

    private String hostWildcardToPattern(String wildcardHost) {
        if (wildcardHost.startsWith("*")) {
            return this.compileHostPattern(wildcardHost, ".*" + Pattern.quote(wildcardHost.substring(1)));
        }
        if (wildcardHost.endsWith("*")) {
            return this.compileHostPattern(wildcardHost, Pattern.quote(wildcardHost.substring(0, wildcardHost.length() - 1)) + ".*");
        }
        return this.compileHostPattern(wildcardHost, Pattern.quote(wildcardHost));
    }

    private Boolean nonProxyMatch(String scheme, String host) {
        return Optional.ofNullable(this.nonProxyHosts.get(scheme)).map(predicate -> predicate.test(host)).orElse(false);
    }
}

