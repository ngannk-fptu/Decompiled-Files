/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.JwtParser;
import com.atlassian.jwt.core.SimpleJwt;
import com.atlassian.jwt.exception.JwtParseException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import javax.annotation.Nonnull;

public class SimpleJwtParser
implements JwtParser {
    @Override
    @Nonnull
    public Jwt parse(String jwt) throws JwtParseException {
        JWSObject jwsObject = this.parseJWSObject(jwt);
        try {
            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            return new SimpleJwt(claims.getIssuer(), claims.getSubject(), jwsObject.getPayload().toString());
        }
        catch (ParseException e) {
            throw new JwtParseException(e);
        }
    }

    private JWSObject parseJWSObject(String jwt) throws JwtParseException {
        JWSObject jwsObject;
        try {
            jwsObject = JWSObject.parse(jwt);
        }
        catch (ParseException e) {
            throw new JwtParseException(e);
        }
        return jwsObject;
    }
}

