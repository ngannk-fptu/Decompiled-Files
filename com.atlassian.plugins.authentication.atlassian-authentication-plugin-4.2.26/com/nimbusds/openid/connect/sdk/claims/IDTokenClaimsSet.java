/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.claims.AMR;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.claims.AuthorizedParty;
import com.nimbusds.openid.connect.sdk.claims.CodeHash;
import com.nimbusds.openid.connect.sdk.claims.CommonOIDCTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.StateHash;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class IDTokenClaimsSet
extends CommonOIDCTokenClaimsSet {
    public static final String EXP_CLAIM_NAME = "exp";
    public static final String AUTH_TIME_CLAIM_NAME = "auth_time";
    public static final String NONCE_CLAIM_NAME = "nonce";
    public static final String AT_HASH_CLAIM_NAME = "at_hash";
    public static final String C_HASH_CLAIM_NAME = "c_hash";
    public static final String S_HASH_CLAIM_NAME = "s_hash";
    public static final String ACR_CLAIM_NAME = "acr";
    public static final String AMR_CLAIM_NAME = "amr";
    public static final String AZP_CLAIM_NAME = "azp";
    public static final String SUB_JWK_CLAIM_NAME = "sub_jwk";
    private static final Set<String> STD_CLAIM_NAMES;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    public IDTokenClaimsSet(Issuer iss, Subject sub, List<Audience> aud, Date exp, Date iat) {
        this.setClaim("iss", iss.getValue());
        this.setClaim("sub", sub.getValue());
        JSONArray audList = new JSONArray();
        for (Audience a : aud) {
            audList.add(a.getValue());
        }
        this.setClaim("aud", audList);
        this.setDateClaim(EXP_CLAIM_NAME, exp);
        this.setDateClaim("iat", iat);
    }

    private IDTokenClaimsSet(JSONObject jsonObject) throws ParseException {
        super(jsonObject);
        if (this.getStringClaim("iss") == null) {
            throw new ParseException("Missing or invalid iss claim");
        }
        if (this.getStringClaim("sub") == null) {
            throw new ParseException("Missing or invalid sub claim");
        }
        if (this.getStringClaim("aud") == null && this.getStringListClaim("aud") == null || this.getStringListClaim("aud") != null && this.getStringListClaim("aud").isEmpty()) {
            throw new ParseException("Missing or invalid aud claim");
        }
        if (this.getDateClaim(EXP_CLAIM_NAME) == null) {
            throw new ParseException("Missing or invalid exp claim");
        }
        if (this.getDateClaim("iat") == null) {
            throw new ParseException("Missing or invalid iat claim");
        }
    }

    public IDTokenClaimsSet(JWTClaimsSet jwtClaimsSet) throws ParseException {
        this(jwtClaimsSet.toJSONObject());
    }

    public boolean hasRequiredClaims(ResponseType responseType, boolean iatAuthzEndpoint) {
        if (new ResponseType("code").equals(responseType)) {
            return true;
        }
        if (new ResponseType("id_token").equals(responseType)) {
            return this.getNonce() != null;
        }
        if (new ResponseType("id_token", "token").equals(responseType)) {
            if (this.getNonce() == null) {
                return false;
            }
            return this.getAccessTokenHash() != null;
        }
        if (new ResponseType("code", "id_token").equals(responseType)) {
            if (this.getNonce() == null) {
                return false;
            }
            if (!iatAuthzEndpoint) {
                return true;
            }
            return this.getCodeHash() != null;
        }
        if (new ResponseType("code", "token").equals(responseType)) {
            if (this.getNonce() == null) {
                return false;
            }
            if (!iatAuthzEndpoint) {
                return true;
            }
            return true;
        }
        if (new ResponseType("code", "id_token", "token").equals(responseType)) {
            if (this.getNonce() == null) {
                return false;
            }
            if (!iatAuthzEndpoint) {
                return true;
            }
            if (this.getAccessTokenHash() == null) {
                return false;
            }
            return this.getCodeHash() != null;
        }
        throw new IllegalArgumentException("Unsupported response_type: " + responseType);
    }

    @Deprecated
    public boolean hasRequiredClaims(ResponseType responseType) {
        return this.hasRequiredClaims(responseType, true);
    }

    public Date getExpirationTime() {
        return this.getDateClaim(EXP_CLAIM_NAME);
    }

    public Date getAuthenticationTime() {
        return this.getDateClaim(AUTH_TIME_CLAIM_NAME);
    }

    public void setAuthenticationTime(Date authTime) {
        this.setDateClaim(AUTH_TIME_CLAIM_NAME, authTime);
    }

    public Nonce getNonce() {
        String value = this.getStringClaim(NONCE_CLAIM_NAME);
        return value != null ? new Nonce(value) : null;
    }

    public void setNonce(Nonce nonce) {
        this.setClaim(NONCE_CLAIM_NAME, nonce != null ? nonce.getValue() : null);
    }

    public AccessTokenHash getAccessTokenHash() {
        String value = this.getStringClaim(AT_HASH_CLAIM_NAME);
        return value != null ? new AccessTokenHash(value) : null;
    }

    public void setAccessTokenHash(AccessTokenHash atHash) {
        this.setClaim(AT_HASH_CLAIM_NAME, atHash != null ? atHash.getValue() : null);
    }

    public CodeHash getCodeHash() {
        String value = this.getStringClaim(C_HASH_CLAIM_NAME);
        return value != null ? new CodeHash(value) : null;
    }

    public void setCodeHash(CodeHash cHash) {
        this.setClaim(C_HASH_CLAIM_NAME, cHash != null ? cHash.getValue() : null);
    }

    public StateHash getStateHash() {
        String value = this.getStringClaim(S_HASH_CLAIM_NAME);
        return value != null ? new StateHash(value) : null;
    }

    public void setStateHash(StateHash sHash) {
        this.setClaim(S_HASH_CLAIM_NAME, sHash != null ? sHash.getValue() : null);
    }

    public ACR getACR() {
        String value = this.getStringClaim(ACR_CLAIM_NAME);
        return value != null ? new ACR(value) : null;
    }

    public void setACR(ACR acr) {
        this.setClaim(ACR_CLAIM_NAME, acr != null ? acr.getValue() : null);
    }

    public List<AMR> getAMR() {
        List<String> rawList = this.getStringListClaim(AMR_CLAIM_NAME);
        if (rawList == null || rawList.isEmpty()) {
            return null;
        }
        ArrayList<AMR> amrList = new ArrayList<AMR>(rawList.size());
        for (String s : rawList) {
            amrList.add(new AMR(s));
        }
        return amrList;
    }

    public void setAMR(List<AMR> amr) {
        if (amr != null) {
            ArrayList<String> amrList = new ArrayList<String>(amr.size());
            for (AMR a : amr) {
                amrList.add(a.getValue());
            }
            this.setClaim(AMR_CLAIM_NAME, amrList);
        } else {
            this.setClaim(AMR_CLAIM_NAME, null);
        }
    }

    public AuthorizedParty getAuthorizedParty() {
        String value = this.getStringClaim(AZP_CLAIM_NAME);
        return value != null ? new AuthorizedParty(value) : null;
    }

    public void setAuthorizedParty(AuthorizedParty azp) {
        this.setClaim(AZP_CLAIM_NAME, azp != null ? azp.getValue() : null);
    }

    public JWK getSubjectJWK() {
        JSONObject jsonObject = this.getClaim(SUB_JWK_CLAIM_NAME, JSONObject.class);
        if (jsonObject == null) {
            return null;
        }
        try {
            return JWK.parse(jsonObject);
        }
        catch (java.text.ParseException e) {
            return null;
        }
    }

    public void setSubjectJWK(JWK subJWK) {
        if (subJWK != null) {
            if (subJWK.isPrivate()) {
                throw new IllegalArgumentException("The subject's JSON Web Key (JWK) must be public");
            }
            this.setClaim(SUB_JWK_CLAIM_NAME, subJWK.toJSONObject());
        } else {
            this.setClaim(SUB_JWK_CLAIM_NAME, null);
        }
    }

    public static IDTokenClaimsSet parse(JSONObject jsonObject) throws ParseException {
        try {
            return new IDTokenClaimsSet(jsonObject);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static IDTokenClaimsSet parse(String json) throws ParseException {
        return IDTokenClaimsSet.parse(JSONObjectUtils.parse(json));
    }

    static {
        HashSet<String> claimNames = new HashSet<String>(CommonOIDCTokenClaimsSet.getStandardClaimNames());
        claimNames.add(EXP_CLAIM_NAME);
        claimNames.add(AUTH_TIME_CLAIM_NAME);
        claimNames.add(NONCE_CLAIM_NAME);
        claimNames.add(AT_HASH_CLAIM_NAME);
        claimNames.add(C_HASH_CLAIM_NAME);
        claimNames.add(S_HASH_CLAIM_NAME);
        claimNames.add(ACR_CLAIM_NAME);
        claimNames.add(AMR_CLAIM_NAME);
        claimNames.add(AZP_CLAIM_NAME);
        claimNames.add(SUB_JWK_CLAIM_NAME);
        STD_CLAIM_NAMES = Collections.unmodifiableSet(claimNames);
    }
}

