/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.nimbusds.jwt.JWTClaimsSet
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jwt.JWTClaimsSet;
import java.io.Serializable;
import java.text.ParseException;

class IdToken
implements Serializable {
    static final String ISSUER = "iss";
    static final String SUBJECT = "sub";
    static final String AUDIENCE = "aud";
    static final String EXPIRATION_TIME = "exp";
    static final String ISSUED_AT = "issuedAt";
    static final String NOT_BEFORE = "nbf";
    static final String NAME = "name";
    static final String PREFERRED_USERNAME = "preferred_username";
    static final String OBJECT_IDENTIFIER = "oid";
    static final String TENANT_IDENTIFIER = "tid";
    static final String UPN = "upn";
    static final String UNIQUE_NAME = "unique_name";
    @JsonProperty(value="iss")
    protected String issuer;
    @JsonProperty(value="sub")
    protected String subject;
    @JsonProperty(value="aud")
    protected String audience;
    @JsonProperty(value="exp")
    protected Long expirationTime;
    @JsonProperty(value="iat")
    protected Long issuedAt;
    @JsonProperty(value="nbf")
    protected Long notBefore;
    @JsonProperty(value="name")
    protected String name;
    @JsonProperty(value="preferred_username")
    protected String preferredUsername;
    @JsonProperty(value="oid")
    protected String objectIdentifier;
    @JsonProperty(value="tid")
    protected String tenantIdentifier;
    @JsonProperty(value="upn")
    protected String upn;
    @JsonProperty(value="unique_name")
    protected String uniqueName;

    IdToken() {
    }

    static IdToken createFromJWTClaims(JWTClaimsSet claims) throws ParseException {
        IdToken idToken = new IdToken();
        idToken.issuer = claims.getStringClaim(ISSUER);
        idToken.subject = claims.getStringClaim(SUBJECT);
        idToken.audience = claims.getStringClaim(AUDIENCE);
        idToken.expirationTime = claims.getLongClaim(EXPIRATION_TIME);
        idToken.issuedAt = claims.getLongClaim(ISSUED_AT);
        idToken.notBefore = claims.getLongClaim(NOT_BEFORE);
        idToken.name = claims.getStringClaim(NAME);
        idToken.preferredUsername = claims.getStringClaim(PREFERRED_USERNAME);
        idToken.objectIdentifier = claims.getStringClaim(OBJECT_IDENTIFIER);
        idToken.tenantIdentifier = claims.getStringClaim(TENANT_IDENTIFIER);
        idToken.upn = claims.getStringClaim(UPN);
        idToken.uniqueName = claims.getStringClaim(UNIQUE_NAME);
        return idToken;
    }
}

