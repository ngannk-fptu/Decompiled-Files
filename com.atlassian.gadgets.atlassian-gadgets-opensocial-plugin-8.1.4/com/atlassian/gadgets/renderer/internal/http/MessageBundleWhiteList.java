/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.http.url.SameOrigin
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.http.url.SameOrigin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserKey;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class MessageBundleWhiteList
implements Whitelist {
    private static final String MESSAGE_BUNDLE_URI = "/rest/gadgets/1.0/g/messagebundle";
    private final ApplicationProperties applicationProperties;

    public MessageBundleWhiteList(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public boolean allows(URI uri, UserKey userKey) {
        try {
            URI applicationUri = new URI(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
            String allowedPrefix = applicationUri.toString() + MESSAGE_BUNDLE_URI;
            boolean isValidUri = Optional.ofNullable(uri).map(URI::normalize).map(URI::toASCIIString).map(String::toLowerCase).filter(normalizedUri -> normalizedUri.startsWith(allowedPrefix.toLowerCase())).isPresent();
            return isValidUri && SameOrigin.isSameOrigin((URI)uri, (URI)applicationUri);
        }
        catch (MalformedURLException | URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
    }
}

