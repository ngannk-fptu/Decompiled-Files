/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.IdentityTrustFramework;
import com.nimbusds.openid.connect.sdk.assurance.VerificationProcess;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class IdentityVerification
implements JSONAware {
    private final IdentityTrustFramework trustFramework;
    private final DateWithTimeZoneOffset time;
    private final VerificationProcess verificationProcess;
    private final List<IdentityEvidence> evidence;

    public IdentityVerification(IdentityTrustFramework trustFramework, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, IdentityEvidence evidence) {
        this(trustFramework, time, verificationProcess, Collections.singletonList(evidence));
    }

    public IdentityVerification(IdentityTrustFramework trustFramework, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, List<IdentityEvidence> evidence) {
        if (trustFramework == null) {
            throw new IllegalArgumentException("The trust framework must not be null");
        }
        this.trustFramework = trustFramework;
        this.time = time;
        this.verificationProcess = verificationProcess;
        this.evidence = evidence;
    }

    public IdentityTrustFramework getTrustFramework() {
        return this.trustFramework;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.time;
    }

    public VerificationProcess getVerificationProcess() {
        return this.verificationProcess;
    }

    public List<IdentityEvidence> getEvidence() {
        return this.evidence;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("trust_framework", this.getTrustFramework().getValue());
        if (this.getVerificationTime() != null) {
            o.put("time", this.getVerificationTime().toISO8601String());
        }
        if (this.getVerificationProcess() != null) {
            o.put("verification_process", this.getVerificationProcess().getValue());
        }
        if (this.getEvidence() != null) {
            JSONArray evidenceArray = new JSONArray();
            for (IdentityEvidence ev : this.getEvidence()) {
                if (ev == null) continue;
                evidenceArray.add(ev.toJSONObject());
            }
            if (!evidenceArray.isEmpty()) {
                o.put("evidence", evidenceArray);
            }
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public static IdentityVerification parse(JSONObject jsonObject) throws ParseException {
        IdentityTrustFramework trustFramework = new IdentityTrustFramework(JSONObjectUtils.getString(jsonObject, "trust_framework"));
        DateWithTimeZoneOffset time = null;
        if (jsonObject.get("time") != null) {
            time = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        VerificationProcess verificationProcess = null;
        if (jsonObject.get("verification_process") != null) {
            verificationProcess = new VerificationProcess(JSONObjectUtils.getString(jsonObject, "verification_process"));
        }
        LinkedList<IdentityEvidence> evidence = null;
        if (jsonObject.get("evidence") != null) {
            evidence = new LinkedList<IdentityEvidence>();
            JSONArray jsonArray = JSONObjectUtils.getJSONArray(jsonObject, "evidence");
            for (JSONObject item : JSONArrayUtils.toJSONObjectList(jsonArray)) {
                evidence.add(IdentityEvidence.parse(item));
            }
        }
        return new IdentityVerification(trustFramework, time, verificationProcess, evidence);
    }
}

