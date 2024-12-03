/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.feature;

import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import javax.annotation.Nonnull;

public final class JsonApplinksFeatures {
    private final ApplinksFeatureService featureService;

    public JsonApplinksFeatures(ApplinksFeatureService featureService) {
        this.featureService = featureService;
    }

    @Nonnull
    public BaseRestEntity isEnabled(@Nonnull ApplinksFeatures feature) {
        return BaseRestEntity.createSingleFieldEntity(feature.name(), this.featureService.isEnabled(feature));
    }

    @Nonnull
    public BaseRestEntity allFeatures() {
        BaseRestEntity.Builder allFeatures = new BaseRestEntity.Builder();
        for (ApplinksFeatures feature : ApplinksFeatures.values()) {
            allFeatures.add(feature.name(), this.featureService.isEnabled(feature));
        }
        return allFeatures.build();
    }
}

