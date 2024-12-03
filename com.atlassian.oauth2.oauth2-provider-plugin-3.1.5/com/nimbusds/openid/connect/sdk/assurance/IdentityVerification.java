/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.IdentityAssuranceLevel;
import com.nimbusds.openid.connect.sdk.assurance.IdentityAssuranceProcess;
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
    private final IdentityAssuranceLevel assuranceLevel;
    private final IdentityAssuranceProcess assuranceProcess;
    private final DateWithTimeZoneOffset time;
    private final VerificationProcess verificationProcess;
    private final List<IdentityEvidence> evidence;

    @Deprecated
    public IdentityVerification(IdentityTrustFramework trustFramework, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, IdentityEvidence evidence) {
        this(trustFramework, time, verificationProcess, Collections.singletonList(evidence));
    }

    public IdentityVerification(IdentityTrustFramework trustFramework, IdentityAssuranceLevel assuranceLevel, IdentityAssuranceProcess assuranceProcess, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, IdentityEvidence evidence) {
        this(trustFramework, assuranceLevel, assuranceProcess, time, verificationProcess, Collections.singletonList(evidence));
    }

    @Deprecated
    public IdentityVerification(IdentityTrustFramework trustFramework, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, List<IdentityEvidence> evidence) {
        this(trustFramework, null, null, time, verificationProcess, evidence);
    }

    public IdentityVerification(IdentityTrustFramework trustFramework, IdentityAssuranceLevel assuranceLevel, IdentityAssuranceProcess assuranceProcess, DateWithTimeZoneOffset time, VerificationProcess verificationProcess, List<IdentityEvidence> evidence) {
        if (trustFramework == null) {
            throw new IllegalArgumentException("The trust framework must not be null");
        }
        this.trustFramework = trustFramework;
        this.assuranceLevel = assuranceLevel;
        this.assuranceProcess = assuranceProcess;
        this.time = time;
        this.verificationProcess = verificationProcess;
        this.evidence = evidence;
    }

    public IdentityTrustFramework getTrustFramework() {
        return this.trustFramework;
    }

    public IdentityAssuranceLevel getAssuranceLevel() {
        return this.assuranceLevel;
    }

    public IdentityAssuranceProcess getAssuranceProcess() {
        return this.assuranceProcess;
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
        if (this.getAssuranceLevel() != null) {
            o.put("assurance_level", this.getAssuranceLevel().getValue());
        }
        if (this.getAssuranceProcess() != null) {
            o.put("assurance_process", this.getAssuranceProcess().toJSONObject());
        }
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
        IdentityAssuranceLevel assuranceLevel = null;
        String stringValue = JSONObjectUtils.getString(jsonObject, "assurance_level", null);
        if (StringUtils.isNotBlank(stringValue)) {
            assuranceLevel = new IdentityAssuranceLevel(stringValue);
        }
        IdentityAssuranceProcess assuranceProcess = null;
        JSONObject jsonObjectValue = JSONObjectUtils.getJSONObject(jsonObject, "assurance_process", null);
        if (jsonObjectValue != null) {
            assuranceProcess = IdentityAssuranceProcess.parse(jsonObjectValue);
        }
        DateWithTimeZoneOffset time = null;
        stringValue = JSONObjectUtils.getString(jsonObject, "time", null);
        if (StringUtils.isNotBlank(stringValue)) {
            time = DateWithTimeZoneOffset.parseISO8601String(stringValue);
        }
        VerificationProcess verificationProcess = null;
        stringValue = JSONObjectUtils.getString(jsonObject, "verification_process", null);
        if (StringUtils.isNotBlank(stringValue)) {
            verificationProcess = new VerificationProcess(stringValue);
        }
        LinkedList<IdentityEvidence> evidence = null;
        if (jsonObject.get("evidence") != null) {
            evidence = new LinkedList<IdentityEvidence>();
            JSONArray jsonArray = JSONObjectUtils.getJSONArray(jsonObject, "evidence");
            for (JSONObject item : JSONArrayUtils.toJSONObjectList(jsonArray)) {
                evidence.add(IdentityEvidence.parse(item));
            }
        }
        return new IdentityVerification(trustFramework, assuranceLevel, assuranceProcess, time, verificationProcess, evidence);
    }
}

