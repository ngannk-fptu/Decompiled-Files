/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.impl;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@EventName(value="cdn.configuration.changed")
public class CdnConfigurationChangedEvent {
    private boolean enabled;
    private String provider;

    public CdnConfigurationChangedEvent(boolean enabled, String url) {
        this.enabled = enabled;
        if (!StringUtils.isBlank((CharSequence)url)) {
            this.provider = Arrays.stream(Provider.values()).filter(value -> value.matchesProvider(url)).findFirst().orElse(Provider.OTHER).name();
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getProvider() {
        return this.provider;
    }

    static enum Provider {
        AKAMAI("akamai.net", "akamaiedge.net"),
        AZURE("azureedge.net"),
        CLOUDFLARE("cloudflare.net"),
        CLOUDFRONT("cloudfront.net"),
        GOOGLE("googleapis.com"),
        STACKPATH("stackpathcdn.com"),
        OTHER(new String[0]);

        private final List<String> domains;

        public boolean matchesProvider(String url) {
            return this.domains.stream().anyMatch(url::contains);
        }

        private Provider(String ... domains) {
            this.domains = Arrays.asList(domains);
        }
    }
}

