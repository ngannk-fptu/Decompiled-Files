/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.AnalyticsMd5Hasher;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;

public class EventAnonymizer {
    private final ProductUUIDProvider productUUIDProvider;

    public EventAnonymizer(ProductUUIDProvider productUUIDProvider) {
        this.productUUIDProvider = productUUIDProvider;
    }

    public String hash(String data) {
        return AnalyticsMd5Hasher.md5Hex(data, this.productUUIDProvider.getUUID());
    }

    public String hashEventProperty(String propertyValue) {
        return propertyValue != null ? this.hash(propertyValue) : null;
    }
}

