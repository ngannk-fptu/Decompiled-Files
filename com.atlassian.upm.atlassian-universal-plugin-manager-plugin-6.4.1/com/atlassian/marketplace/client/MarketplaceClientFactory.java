/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client;

import com.atlassian.marketplace.client.MarketplaceClient;
import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.impl.DefaultMarketplaceClient;
import java.net.URI;

public abstract class MarketplaceClientFactory {
    public static final URI DEFAULT_MARKETPLACE_URI = URI.create("https://marketplace.atlassian.com");

    private MarketplaceClientFactory() {
    }

    public static MarketplaceClient createMarketplaceClient(URI baseUri, HttpConfiguration configuration) {
        return new DefaultMarketplaceClient(baseUri, configuration);
    }

    public static MarketplaceClient createMarketplaceClient(URI baseUri) {
        return MarketplaceClientFactory.createMarketplaceClient(baseUri, HttpConfiguration.defaults());
    }

    public static MarketplaceClient createMarketplaceClient(HttpConfiguration configuration) {
        return MarketplaceClientFactory.createMarketplaceClient(DEFAULT_MARKETPLACE_URI, configuration);
    }

    public static MarketplaceClient createMarketplaceClient() {
        return MarketplaceClientFactory.createMarketplaceClient(DEFAULT_MARKETPLACE_URI);
    }
}

