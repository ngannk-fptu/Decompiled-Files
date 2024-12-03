/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust.marks;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.CommonClaimsSet;
import java.net.URI;
import java.util.Date;

public class TrustMarkClaimsSet
extends CommonClaimsSet {
    public static final String ID_CLAIM_NAME = "id";
    public static final String MARK_CLAIM_NAME = "mark";
    public static final String EXP_CLAIM_NAME = "exp";
    public static final String REF_CLAIM_NAME = "ref";

    public TrustMarkClaimsSet(Issuer iss, Subject sub, Identifier id, Date iat) {
        this.setClaim("iss", iss.getValue());
        this.setClaim("sub", sub.getValue());
        this.setClaim(ID_CLAIM_NAME, id.getValue());
        this.setDateClaim("iat", iat);
    }

    public TrustMarkClaimsSet(JWTClaimsSet jwtClaimsSet) throws ParseException {
        super(JSONObjectUtils.toJSONObject(jwtClaimsSet));
        this.validateRequiredClaimsPresence();
    }

    public void validateRequiredClaimsPresence() throws ParseException {
        if (this.getIssuer() == null) {
            throw new ParseException("Missing iss (issuer) claim");
        }
        if (this.getSubject() == null) {
            throw new ParseException("Missing sub (subject) claim");
        }
        if (this.getID() == null) {
            throw new ParseException("Missing id (identifier) claim");
        }
        if (this.getIssueTime() == null) {
            throw new ParseException("Missing iat (issued-at) claim");
        }
    }

    public Identifier getID() {
        return new Identifier(this.getStringClaim(ID_CLAIM_NAME));
    }

    public URI getMark() {
        return this.getURIClaim(MARK_CLAIM_NAME);
    }

    public void setMark(URI markURI) {
        this.setURIClaim(MARK_CLAIM_NAME, markURI);
    }

    public Date getExpirationTime() {
        return this.getDateClaim(EXP_CLAIM_NAME);
    }

    public void setExpirationTime(Date exp) {
        this.setDateClaim(EXP_CLAIM_NAME, exp);
    }

    public URI getReference() {
        return this.getURIClaim(REF_CLAIM_NAME);
    }

    public void setReference(URI refURI) {
        this.setURIClaim(REF_CLAIM_NAME, refURI);
    }
}

