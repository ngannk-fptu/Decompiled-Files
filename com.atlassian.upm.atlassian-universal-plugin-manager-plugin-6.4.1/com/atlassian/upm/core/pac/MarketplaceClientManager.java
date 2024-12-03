/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.pac;

import com.atlassian.marketplace.client.MarketplaceClient;

public interface MarketplaceClientManager {
    public MarketplaceClient getMarketplaceClient();

    public String getUserAgent();
}

