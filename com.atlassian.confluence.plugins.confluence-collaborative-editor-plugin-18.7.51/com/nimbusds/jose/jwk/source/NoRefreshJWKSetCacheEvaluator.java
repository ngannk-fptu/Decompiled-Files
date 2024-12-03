/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;

class NoRefreshJWKSetCacheEvaluator
extends JWKSetCacheRefreshEvaluator {
    NoRefreshJWKSetCacheEvaluator() {
    }

    @Override
    public boolean requiresRefresh(JWKSet jwkSet) {
        return false;
    }

    public boolean equals(Object obj) {
        return obj instanceof NoRefreshJWKSetCacheEvaluator;
    }

    public int hashCode() {
        return 0;
    }
}

