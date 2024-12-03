/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.StateHash;
import com.nimbusds.openid.connect.sdk.validators.InvalidHashException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class StateValidator {
    public static void validate(State state, JWSAlgorithm jwsAlgorithm, StateHash stateHash) throws InvalidHashException {
        StateHash expectedHash = StateHash.compute(state, jwsAlgorithm);
        if (expectedHash == null) {
            throw InvalidHashException.INVALID_STATE_HASH_EXCEPTION;
        }
        if (!expectedHash.equals(stateHash)) {
            throw InvalidHashException.INVALID_STATE_HASH_EXCEPTION;
        }
    }
}

