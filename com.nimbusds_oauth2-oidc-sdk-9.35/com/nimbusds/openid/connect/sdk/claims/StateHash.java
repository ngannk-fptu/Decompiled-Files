/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.jwk.Curve
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public class StateHash
extends HashClaim {
    private static final long serialVersionUID = 6043322975168115376L;

    public StateHash(String value) {
        super(value);
    }

    @Deprecated
    public static StateHash compute(State state, JWSAlgorithm alg) {
        String value = StateHash.computeValue(state, alg);
        if (value == null) {
            return null;
        }
        return new StateHash(value);
    }

    public static StateHash compute(State state, JWSAlgorithm alg, Curve crv) {
        String value = StateHash.computeValue(state, alg, crv);
        if (value == null) {
            return null;
        }
        return new StateHash(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof StateHash && this.toString().equals(object.toString());
    }
}

