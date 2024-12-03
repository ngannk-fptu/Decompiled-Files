/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.config.Lookup
 *  org.apache.http.config.RegistryBuilder
 */
package org.apache.http.impl.client;

import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;

public final class CookieSpecRegistries {
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder(PublicSuffixMatcher publicSuffixMatcher) {
        DefaultCookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
        RFC6265CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        RFC6265CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return RegistryBuilder.create().register("default", (Object)defaultProvider).register("best-match", (Object)defaultProvider).register("compatibility", (Object)defaultProvider).register("standard", (Object)laxStandardProvider).register("standard-strict", (Object)strictStandardProvider).register("netscape", (Object)new NetscapeDraftSpecProvider()).register("ignoreCookies", (Object)new IgnoreSpecProvider());
    }

    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder() {
        return CookieSpecRegistries.createDefaultBuilder(PublicSuffixMatcherLoader.getDefault());
    }

    public static Lookup<CookieSpecProvider> createDefault() {
        return CookieSpecRegistries.createDefault(PublicSuffixMatcherLoader.getDefault());
    }

    public static Lookup<CookieSpecProvider> createDefault(PublicSuffixMatcher publicSuffixMatcher) {
        return CookieSpecRegistries.createDefaultBuilder(publicSuffixMatcher).build();
    }

    private CookieSpecRegistries() {
    }
}

