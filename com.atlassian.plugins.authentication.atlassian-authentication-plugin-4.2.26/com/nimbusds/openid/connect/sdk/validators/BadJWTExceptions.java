/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jwt.proc.BadJWTException;
import net.jcip.annotations.Immutable;

@Immutable
public final class BadJWTExceptions {
    public static final BadJWTException MISSING_EXP_CLAIM_EXCEPTION = new BadJWTException("Missing JWT expiration (exp) claim");
    public static final BadJWTException MISSING_IAT_CLAIM_EXCEPTION = new BadJWTException("Missing JWT issue time (iat) claim");
    public static final BadJWTException MISSING_ISS_CLAIM_EXCEPTION = new BadJWTException("Missing JWT issuer (iss) claim");
    public static final BadJWTException MISSING_SUB_CLAIM_EXCEPTION = new BadJWTException("Missing JWT subject (sub) claim");
    public static final BadJWTException MISSING_AUD_CLAIM_EXCEPTION = new BadJWTException("Missing JWT audience (aud) claim");
    public static final BadJWTException MISSING_NONCE_CLAIM_EXCEPTION = new BadJWTException("Missing JWT nonce (nonce) claim");
    public static final BadJWTException EXPIRED_EXCEPTION = new BadJWTException("Expired JWT");
    public static final BadJWTException IAT_CLAIM_AHEAD_EXCEPTION = new BadJWTException("JWT issue time ahead of current time");

    private BadJWTExceptions() {
    }
}

