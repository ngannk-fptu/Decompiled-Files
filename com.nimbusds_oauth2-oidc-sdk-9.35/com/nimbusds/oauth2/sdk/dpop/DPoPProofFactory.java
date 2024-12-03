/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JOSEObjectType
 *  com.nimbusds.jwt.SignedJWT
 */
package com.nimbusds.oauth2.sdk.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import java.net.URI;
import java.util.Date;

public interface DPoPProofFactory {
    public static final JOSEObjectType TYPE = new JOSEObjectType("dpop+jwt");
    public static final int MINIMAL_JTI_BYTE_LENGTH = 12;

    public SignedJWT createDPoPJWT(String var1, URI var2) throws JOSEException;

    public SignedJWT createDPoPJWT(String var1, URI var2, AccessToken var3) throws JOSEException;

    public SignedJWT createDPoPJWT(JWTID var1, String var2, URI var3, Date var4, AccessToken var5) throws JOSEException;
}

