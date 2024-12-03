/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 *  com.nimbusds.jwt.util.DateUtils
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.assertions.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.assertions.AssertionDetails;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class JWTAssertionDetails
extends AssertionDetails {
    private static final Set<String> reservedClaimsNames = new LinkedHashSet<String>();
    private final Date nbf;
    private final Map<String, Object> other;

    public static Set<String> getReservedClaimsNames() {
        return Collections.unmodifiableSet(reservedClaimsNames);
    }

    public JWTAssertionDetails(Issuer iss, Subject sub, Audience aud) {
        this(iss, sub, aud.toSingleAudienceList(), new Date(new Date().getTime() + 300000L), null, null, new JWTID(), null);
    }

    public JWTAssertionDetails(Issuer iss, Subject sub, List<Audience> aud, Date exp, Date nbf, Date iat, JWTID jti, Map<String, Object> other) {
        super(iss, sub, aud, iat, exp, jti);
        this.nbf = nbf;
        this.other = other;
    }

    public Date getNotBeforeTime() {
        return this.nbf;
    }

    public JWTID getJWTID() {
        Identifier id = this.getID();
        return id != null ? new JWTID(id.getValue()) : null;
    }

    public Map<String, Object> getCustomClaims() {
        return this.other;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put((Object)"iss", (Object)this.getIssuer().getValue());
        o.put((Object)"sub", (Object)this.getSubject().getValue());
        o.put((Object)"aud", Audience.toStringList(this.getAudience()));
        o.put((Object)"exp", (Object)DateUtils.toSecondsSinceEpoch((Date)this.getExpirationTime()));
        if (this.nbf != null) {
            o.put((Object)"nbf", (Object)DateUtils.toSecondsSinceEpoch((Date)this.nbf));
        }
        if (this.getIssueTime() != null) {
            o.put((Object)"iat", (Object)DateUtils.toSecondsSinceEpoch((Date)this.getIssueTime()));
        }
        if (this.getID() != null) {
            o.put((Object)"jti", (Object)this.getID().getValue());
        }
        if (this.other != null) {
            o.putAll(this.other);
        }
        return o;
    }

    public JWTClaimsSet toJWTClaimsSet() {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().issuer(this.getIssuer().getValue()).subject(this.getSubject().getValue()).audience(Audience.toStringList(this.getAudience())).expirationTime(this.getExpirationTime()).notBeforeTime(this.nbf).issueTime(this.getIssueTime()).jwtID(this.getID() != null ? this.getJWTID().getValue() : null);
        if (this.other != null) {
            for (Map.Entry<String, Object> entry : this.other.entrySet()) {
                builder = builder.claim(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public static JWTAssertionDetails parse(JSONObject jsonObject) throws ParseException {
        Issuer iss = new Issuer(JSONObjectUtils.getString(jsonObject, "iss"));
        Subject sub = new Subject(JSONObjectUtils.getString(jsonObject, "sub"));
        List<Audience> aud = jsonObject.get((Object)"aud") instanceof String ? new Audience(JSONObjectUtils.getString(jsonObject, "aud")).toSingleAudienceList() : Audience.create(JSONObjectUtils.getStringList(jsonObject, "aud"));
        Date exp = DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(jsonObject, "exp"));
        Date nbf = null;
        if (jsonObject.containsKey((Object)"nbf")) {
            nbf = DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(jsonObject, "nbf"));
        }
        Date iat = null;
        if (jsonObject.containsKey((Object)"iat")) {
            iat = DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(jsonObject, "iat"));
        }
        JWTID jti = null;
        if (jsonObject.containsKey((Object)"jti")) {
            jti = new JWTID(JSONObjectUtils.getString(jsonObject, "jti"));
        }
        LinkedHashMap<String, Object> other = null;
        Set customClaimNames = jsonObject.keySet();
        if (customClaimNames.removeAll(reservedClaimsNames)) {
            other = new LinkedHashMap<String, Object>();
            for (String claim : customClaimNames) {
                other.put(claim, jsonObject.get((Object)claim));
            }
        }
        return new JWTAssertionDetails(iss, sub, aud, exp, nbf, iat, jti, other);
    }

    public static JWTAssertionDetails parse(JWTClaimsSet jwtClaimsSet) throws ParseException {
        return JWTAssertionDetails.parse(JSONObjectUtils.toJSONObject(jwtClaimsSet));
    }

    static {
        reservedClaimsNames.add("iss");
        reservedClaimsNames.add("sub");
        reservedClaimsNames.add("aud");
        reservedClaimsNames.add("exp");
        reservedClaimsNames.add("nbf");
        reservedClaimsNames.add("iat");
        reservedClaimsNames.add("jti");
    }
}

