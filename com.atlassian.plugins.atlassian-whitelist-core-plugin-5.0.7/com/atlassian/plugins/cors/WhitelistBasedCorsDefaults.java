/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.CorsHeaders
 *  com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults
 *  com.atlassian.plugins.whitelist.InboundWhitelist
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.cors;

import com.atlassian.plugins.rest.common.security.CorsHeaders;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.whitelist.InboundWhitelist;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class WhitelistBasedCorsDefaults
implements CorsDefaults {
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final Set<String> ALLOWED_REQUEST_HEADERS = ImmutableSet.of((Object)"X-Atlassian-Token", (Object)"Authorization", (Object)"Content-Type");
    private static final Set<String> ALLOWED_RESPONSE_HEADERS = ImmutableSet.of((Object)CorsHeaders.ORIGIN.value(), (Object)"Authorization", (Object)"Content-Type");
    private final InboundWhitelist inboundWhitelist;

    public WhitelistBasedCorsDefaults(InboundWhitelist inboundWhitelist) {
        this.inboundWhitelist = inboundWhitelist;
    }

    public boolean allowsCredentials(String origin) throws IllegalArgumentException {
        return this.allowsOrigin(origin);
    }

    public boolean allowsOrigin(String origin) throws IllegalArgumentException {
        URI originUri;
        if (StringUtils.isBlank((CharSequence)origin)) {
            return false;
        }
        try {
            originUri = new URI(origin);
        }
        catch (URISyntaxException e) {
            return false;
        }
        return this.inboundWhitelist.isAllowed(originUri);
    }

    public Set<String> getAllowedRequestHeaders(String origin) throws IllegalArgumentException {
        return ALLOWED_REQUEST_HEADERS;
    }

    public Set<String> getAllowedResponseHeaders(String origin) throws IllegalArgumentException {
        return ALLOWED_RESPONSE_HEADERS;
    }
}

