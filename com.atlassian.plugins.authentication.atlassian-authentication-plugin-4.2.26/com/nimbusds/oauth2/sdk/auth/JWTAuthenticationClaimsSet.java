/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;
import java.util.Date;
import java.util.List;
import net.minidev.json.JSONObject;

public class JWTAuthenticationClaimsSet
extends JWTAssertionDetails {
    public JWTAuthenticationClaimsSet(ClientID clientID, Audience aud) {
        this(clientID, aud.toSingleAudienceList(), new Date(new Date().getTime() + 300000L), null, null, new JWTID());
    }

    public JWTAuthenticationClaimsSet(ClientID clientID, List<Audience> aud, Date exp, Date nbf, Date iat, JWTID jti) {
        super(new Issuer(clientID.getValue()), new Subject(clientID.getValue()), aud, exp, nbf, iat, jti, null);
    }

    public ClientID getClientID() {
        return new ClientID(this.getIssuer());
    }

    public static JWTAuthenticationClaimsSet parse(JSONObject jsonObject) throws ParseException {
        JWTAssertionDetails assertion = JWTAssertionDetails.parse(jsonObject);
        return new JWTAuthenticationClaimsSet(new ClientID(assertion.getIssuer()), assertion.getAudience(), assertion.getExpirationTime(), assertion.getNotBeforeTime(), assertion.getIssueTime(), assertion.getJWTID());
    }

    public static JWTAuthenticationClaimsSet parse(JWTClaimsSet jwtClaimsSet) throws ParseException {
        return JWTAuthenticationClaimsSet.parse(jwtClaimsSet.toJSONObject());
    }
}

