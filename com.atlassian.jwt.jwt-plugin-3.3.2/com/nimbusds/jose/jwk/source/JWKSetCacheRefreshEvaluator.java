/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ForceRefreshJWKSetCacheEvaluator;
import com.nimbusds.jose.jwk.source.NoRefreshJWKSetCacheEvaluator;
import com.nimbusds.jose.jwk.source.ReferenceComparisonRefreshJWKSetEvaluator;

public abstract class JWKSetCacheRefreshEvaluator {
    private static final ForceRefreshJWKSetCacheEvaluator FORCE_REFRESH = new ForceRefreshJWKSetCacheEvaluator();
    private static final NoRefreshJWKSetCacheEvaluator NO_REFRESH = new NoRefreshJWKSetCacheEvaluator();

    public static JWKSetCacheRefreshEvaluator forceRefresh() {
        return FORCE_REFRESH;
    }

    public static JWKSetCacheRefreshEvaluator noRefresh() {
        return NO_REFRESH;
    }

    public static JWKSetCacheRefreshEvaluator referenceComparison(JWKSet jwtSet) {
        return new ReferenceComparisonRefreshJWKSetEvaluator(jwtSet);
    }

    public abstract boolean requiresRefresh(JWKSet var1);
}

