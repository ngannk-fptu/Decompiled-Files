/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugin.webresource.impl.helpers;

import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.google.common.base.Predicate;
import java.util.Map;

public class BaseHelpers {
    public static Predicate<Bundle> isConditionsSatisfied(final RequestCache requestCache, final Map<String, String> params) {
        return new Predicate<Bundle>(){

            public boolean apply(Bundle bundle) {
                CachedCondition condition = bundle.getCondition();
                return condition == null || condition.evaluateSafely(requestCache, params);
            }
        };
    }

    public static Predicate<Bundle> isConditionsSatisfied(final RequestCache requestCache, final UrlBuildingStrategy urlBuilderStrategy) {
        return new Predicate<Bundle>(){

            public boolean apply(Bundle bundle) {
                CachedCondition condition = bundle.getCondition();
                return condition == null || condition.evaluateSafely(requestCache, urlBuilderStrategy);
            }
        };
    }

    public static Predicate<Bundle> hasLegacyCondition() {
        return new Predicate<Bundle>(){

            public boolean apply(Bundle bundle) {
                return bundle.hasLegacyConditions();
            }
        };
    }

    public static Predicate<Bundle> hasConditions() {
        return new Predicate<Bundle>(){

            public boolean apply(Bundle bundle) {
                return bundle.getCondition() != null;
            }
        };
    }
}

