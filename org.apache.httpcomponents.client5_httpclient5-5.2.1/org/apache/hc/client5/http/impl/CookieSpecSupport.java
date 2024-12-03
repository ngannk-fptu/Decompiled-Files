/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.RegistryBuilder
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.impl.cookie.IgnoreCookieSpecFactory;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.client5.http.psl.PublicSuffixMatcher;
import org.apache.hc.client5.http.psl.PublicSuffixMatcherLoader;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;

@Internal
public final class CookieSpecSupport {
    public static RegistryBuilder<CookieSpecFactory> createDefaultBuilder(PublicSuffixMatcher publicSuffixMatcher) {
        return RegistryBuilder.create().register("relaxed", (Object)new RFC6265CookieSpecFactory(RFC6265CookieSpecFactory.CompatibilityLevel.RELAXED, publicSuffixMatcher)).register("strict", (Object)new RFC6265CookieSpecFactory(RFC6265CookieSpecFactory.CompatibilityLevel.STRICT, publicSuffixMatcher)).register("ignore", (Object)new IgnoreCookieSpecFactory());
    }

    public static RegistryBuilder<CookieSpecFactory> createDefaultBuilder() {
        return CookieSpecSupport.createDefaultBuilder(PublicSuffixMatcherLoader.getDefault());
    }

    public static Lookup<CookieSpecFactory> createDefault() {
        return CookieSpecSupport.createDefault(PublicSuffixMatcherLoader.getDefault());
    }

    public static Lookup<CookieSpecFactory> createDefault(PublicSuffixMatcher publicSuffixMatcher) {
        return CookieSpecSupport.createDefaultBuilder(publicSuffixMatcher).build();
    }

    private CookieSpecSupport() {
    }
}

