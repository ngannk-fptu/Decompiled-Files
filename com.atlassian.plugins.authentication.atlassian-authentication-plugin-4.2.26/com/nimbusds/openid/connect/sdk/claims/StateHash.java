/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public class StateHash
extends HashClaim {
    public StateHash(String value) {
        super(value);
    }

    public static StateHash compute(State state, JWSAlgorithm alg) {
        String value = StateHash.computeValue(state, alg);
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

