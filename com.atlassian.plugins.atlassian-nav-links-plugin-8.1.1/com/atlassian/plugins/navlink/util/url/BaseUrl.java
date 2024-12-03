/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.util.url;

import com.atlassian.plugins.navlink.util.url.UrlFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BaseUrl {
    private static final Map<String, Integer> DEFAULT_PORTS = ImmutableMap.of((Object)"http", (Object)80, (Object)"https", (Object)443);
    private final String baseUrl;

    public BaseUrl(@Nonnull String baseUrl) {
        this.baseUrl = (String)Preconditions.checkNotNull((Object)baseUrl);
    }

    public static BaseUrl fromSystemProperty(String key, @Nullable String def) {
        Preconditions.checkNotNull((Object)key, (Object)"key");
        Preconditions.checkNotNull((Object)def, (Object)"def");
        return new BaseUrl(System.getProperty(key, def));
    }

    @Nonnull
    private static BaseUrl createBaseUrl(String scheme, String serverName, int port, String contextPath) throws MalformedURLException {
        return new BaseUrl(new URL(scheme, serverName, port, contextPath).toExternalForm());
    }

    private static boolean isDefaultPort(@Nonnull String scheme, int port) {
        Integer defaultPort = DEFAULT_PORTS.get(scheme.toLowerCase());
        return defaultPort != null && defaultPort == port;
    }

    @Nonnull
    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String resolve(@Nullable String path) {
        return UrlFactory.toAbsoluteUrl(this.baseUrl, Strings.nullToEmpty((String)path));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BaseUrl other = (BaseUrl)o;
        return this.baseUrl.equals(other.baseUrl);
    }

    public int hashCode() {
        return this.baseUrl.hashCode();
    }

    public String toString() {
        return "BaseUrl{" + this.baseUrl + '}';
    }
}

