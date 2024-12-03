/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.features;

import com.atlassian.sal.api.features.FeatureKeyScope;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class FeatureKeyScopePredicate
implements Predicate<FeatureKeyScope> {
    private final FeatureKeyScope featureKeyScope;

    public FeatureKeyScopePredicate(FeatureKeyScope featureKeyScope) {
        this.featureKeyScope = featureKeyScope;
    }

    public static FeatureKeyScopePredicate filterBy(FeatureKeyScope featureKeyScope) {
        return new FeatureKeyScopePredicate(featureKeyScope);
    }

    @Deprecated
    public boolean apply(@Nullable FeatureKeyScope input) {
        return input == this.featureKeyScope;
    }

    public boolean test(@Nullable FeatureKeyScope input) {
        return input == this.featureKeyScope;
    }
}

