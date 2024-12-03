/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.api.extractor;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ProductProvidedPropertyExtractor {
    @Nullable
    default public Map<String, Object> extractProperty(@Nonnull String name, @Nonnull Object value) {
        return null;
    }

    @Nullable
    default public String extractUser(@Nonnull Object event, @Nonnull Map<String, Object> properties) {
        return null;
    }

    @Nullable
    default public String getApplicationAccess() {
        return null;
    }

    @Nullable
    default public Map<String, Object> enrichProperties(@Nonnull Object event) {
        return null;
    }

    @Nullable
    default public String extractName(@Nonnull Object event) {
        return null;
    }
}

