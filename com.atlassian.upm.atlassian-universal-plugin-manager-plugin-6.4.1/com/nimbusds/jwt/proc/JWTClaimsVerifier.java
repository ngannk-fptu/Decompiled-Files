/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;

@Deprecated
public interface JWTClaimsVerifier {
    @Deprecated
    public void verify(JWTClaimsSet var1) throws BadJWTException;
}

