/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.TokenPropertiesFactory
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.Request;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.TokenPropertiesFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AggregateTokenPropertiesFactory
implements TokenPropertiesFactory {
    private final Iterable<TokenPropertiesFactory> propertyFactories;

    public AggregateTokenPropertiesFactory(Iterable<TokenPropertiesFactory> propertyFactories) {
        this.propertyFactories = Objects.requireNonNull(propertyFactories, "propertyFactories");
    }

    public Map<String, String> newRequestTokenProperties(Request request) {
        HashMap properties = new HashMap();
        for (TokenPropertiesFactory propertiesFactory : this.propertyFactories) {
            try {
                properties.putAll(propertiesFactory.newRequestTokenProperties(request));
            }
            catch (RuntimeException e) {
                if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) continue;
                throw e;
            }
        }
        return Collections.unmodifiableMap(properties);
    }

    public Map<String, String> newAccessTokenProperties(ServiceProviderToken requestToken) {
        HashMap properties = new HashMap();
        for (TokenPropertiesFactory propertiesFactory : this.propertyFactories) {
            try {
                properties.putAll(propertiesFactory.newAccessTokenProperties(requestToken));
            }
            catch (RuntimeException e) {
                if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) continue;
                throw e;
            }
        }
        return Collections.unmodifiableMap(properties);
    }
}

