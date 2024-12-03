/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWTClaimsSet
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.CommonOIDCTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.SessionID;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class LogoutTokenClaimsSet
extends CommonOIDCTokenClaimsSet {
    public static final String JTI_CLAIM_NAME = "jti";
    public static final String EVENTS_CLAIM_NAME = "events";
    public static final String EVENT_TYPE = "http://schemas.openid.net/event/backchannel-logout";
    private static final Set<String> STD_CLAIM_NAMES;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    public LogoutTokenClaimsSet(Issuer iss, Subject sub, List<Audience> aud, Date iat, JWTID jti, SessionID sid) {
        if (sub == null && sid == null) {
            throw new IllegalArgumentException("Either the subject or the session ID must be set, or both");
        }
        this.setClaim("iss", iss.getValue());
        if (sub != null) {
            this.setClaim("sub", sub.getValue());
        }
        JSONArray audList = new JSONArray();
        for (Audience a : aud) {
            audList.add((Object)a.getValue());
        }
        this.setClaim("aud", audList);
        this.setDateClaim("iat", iat);
        this.setClaim(JTI_CLAIM_NAME, jti.getValue());
        JSONObject events = new JSONObject();
        events.put((Object)EVENT_TYPE, (Object)new JSONObject());
        this.setClaim(EVENTS_CLAIM_NAME, events);
        if (sid != null) {
            this.setClaim("sid", sid.getValue());
        }
    }

    private LogoutTokenClaimsSet(JSONObject jsonObject) throws ParseException {
        super(jsonObject);
        if (this.getStringClaim("iss") == null) {
            throw new ParseException("Missing or invalid iss claim");
        }
        if (this.getStringClaim("sub") == null && this.getStringClaim("sid") == null) {
            throw new ParseException("Missing or invalid sub and / or sid claim(s)");
        }
        if (this.getStringClaim("aud") == null && this.getStringListClaim("aud") == null || this.getStringListClaim("aud") != null && this.getStringListClaim("aud").isEmpty()) {
            throw new ParseException("Missing or invalid aud claim");
        }
        if (this.getDateClaim("iat") == null) {
            throw new ParseException("Missing or invalid iat claim");
        }
        if (this.getStringClaim(JTI_CLAIM_NAME) == null) {
            throw new ParseException("Missing or invalid jti claim");
        }
        if (this.getClaim(EVENTS_CLAIM_NAME) == null) {
            throw new ParseException("Missing or invalid events claim");
        }
        JSONObject events = this.getClaim(EVENTS_CLAIM_NAME, JSONObject.class);
        if (JSONObjectUtils.getJSONObject(events, EVENT_TYPE, null) == null) {
            throw new ParseException("Missing event type http://schemas.openid.net/event/backchannel-logout");
        }
        if (jsonObject.containsKey((Object)"nonce")) {
            throw new ParseException("Nonce is prohibited");
        }
    }

    public LogoutTokenClaimsSet(JWTClaimsSet jwtClaimsSet) throws ParseException {
        this(JSONObjectUtils.toJSONObject(jwtClaimsSet));
    }

    public JWTID getJWTID() {
        return new JWTID(this.getStringClaim(JTI_CLAIM_NAME));
    }

    @Override
    public JSONObject toJSONObject() {
        if (this.getClaim("nonce") != null) {
            throw new IllegalStateException("Nonce is prohibited");
        }
        return super.toJSONObject();
    }

    @Override
    public JWTClaimsSet toJWTClaimsSet() throws ParseException {
        if (this.getClaim("nonce") != null) {
            throw new ParseException("Nonce is prohibited");
        }
        return super.toJWTClaimsSet();
    }

    public static LogoutTokenClaimsSet parse(String json) throws ParseException {
        JSONObject jsonObject = JSONObjectUtils.parse(json);
        try {
            return new LogoutTokenClaimsSet(jsonObject);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    static {
        HashSet<String> claimNames = new HashSet<String>(CommonOIDCTokenClaimsSet.getStandardClaimNames());
        claimNames.add(JTI_CLAIM_NAME);
        claimNames.add(EVENTS_CLAIM_NAME);
        STD_CLAIM_NAMES = Collections.unmodifiableSet(claimNames);
    }
}

