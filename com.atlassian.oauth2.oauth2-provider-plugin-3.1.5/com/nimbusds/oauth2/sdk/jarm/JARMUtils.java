/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.jarm;

import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JARMUtils {
    public static final Set<ResponseMode> RESPONSE_MODES = new HashSet<ResponseMode>(Arrays.asList(ResponseMode.JWT, ResponseMode.QUERY_JWT, ResponseMode.FRAGMENT_JWT, ResponseMode.FORM_POST_JWT));

    public static boolean supportsJARM(AuthorizationServerMetadata asMetadata) {
        if (CollectionUtils.isEmpty(asMetadata.getAuthorizationJWSAlgs())) {
            return false;
        }
        if (CollectionUtils.isEmpty(asMetadata.getResponseModes())) {
            return false;
        }
        for (ResponseMode responseMode : RESPONSE_MODES) {
            if (!asMetadata.getResponseModes().contains(responseMode)) continue;
            return true;
        }
        return false;
    }

    public static JWTClaimsSet toJWTClaimsSet(Issuer iss, ClientID aud, Date exp, AuthorizationResponse response) {
        if (exp == null) {
            throw new IllegalArgumentException("The expiration time must not be null");
        }
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().issuer(iss.getValue()).audience(aud.getValue()).expirationTime(exp);
        for (Map.Entry<String, String> en : MultivaluedMapUtils.toSingleValuedMap(response.toParameters()).entrySet()) {
            if ("response".equals(en.getKey())) continue;
            if ("iss".equals(en.getKey()) && !iss.getValue().equals(en.getValue())) {
                throw new IllegalArgumentException("Authorization response iss doesn't match JWT iss claim: " + en.getValue());
            }
            builder = builder.claim(en.getKey(), en.getValue() + "");
        }
        return builder.build();
    }

    public static Map<String, List<String>> toMultiValuedStringParameters(JWTClaimsSet jwtClaimsSet) {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        for (Map.Entry<String, Object> en : jwtClaimsSet.getClaims().entrySet()) {
            params.put(en.getKey(), Collections.singletonList(en.getValue() + ""));
        }
        return params;
    }

    public static boolean impliesAuthorizationErrorResponse(String jwtString) throws ParseException {
        try {
            return JARMUtils.impliesAuthorizationErrorResponse(JWTParser.parse(jwtString));
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid JWT-secured authorization response: " + e.getMessage(), e);
        }
    }

    public static boolean impliesAuthorizationErrorResponse(JWT jwt) throws ParseException {
        if (jwt instanceof PlainJWT) {
            throw new ParseException("Invalid JWT-secured authorization response: The JWT must not be plain (unsecured)");
        }
        if (jwt instanceof EncryptedJWT) {
            return false;
        }
        if (jwt instanceof SignedJWT) {
            SignedJWT signedJWT = (SignedJWT)jwt;
            try {
                return signedJWT.getJWTClaimsSet().getStringClaim("error") != null;
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid JWT claims set: " + e.getMessage());
            }
        }
        throw new ParseException("Unexpected JWT type");
    }

    private JARMUtils() {
    }
}

