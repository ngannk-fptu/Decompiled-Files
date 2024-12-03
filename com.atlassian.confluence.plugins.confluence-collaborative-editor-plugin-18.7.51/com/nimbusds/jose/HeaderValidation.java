/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.IllegalHeaderException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.UnprotectedHeader;

class HeaderValidation {
    HeaderValidation() {
    }

    static void ensureDisjoint(JWSHeader jwsHeader, UnprotectedHeader unprotectedHeader) throws IllegalHeaderException {
        if (jwsHeader == null || unprotectedHeader == null) {
            return;
        }
        for (String unprotectedParamName : unprotectedHeader.getIncludedParams()) {
            if (!jwsHeader.getIncludedParams().contains(unprotectedParamName)) continue;
            throw new IllegalHeaderException("The parameters in the JWS protected header and the unprotected header must be disjoint");
        }
    }
}

