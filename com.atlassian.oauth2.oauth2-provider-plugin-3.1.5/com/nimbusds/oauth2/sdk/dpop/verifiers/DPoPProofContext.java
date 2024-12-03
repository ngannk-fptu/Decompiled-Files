/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;

class DPoPProofContext
implements SecurityContext {
    private final DPoPIssuer issuer;
    private Base64URL ath;

    public DPoPProofContext(DPoPIssuer issuer) {
        if (issuer == null) {
            throw new IllegalArgumentException("The DPoP issuer must not be null");
        }
        this.issuer = issuer;
    }

    public DPoPIssuer getIssuer() {
        return this.issuer;
    }

    public void setAccessTokenHash(Base64URL ath) {
        this.ath = ath;
    }

    public Base64URL getAccessTokenHash() {
        return this.ath;
    }
}

