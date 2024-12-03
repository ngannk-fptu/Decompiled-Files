/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Attestation;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerifier;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ValidationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.VerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import java.util.List;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class VouchEvidence
extends IdentityEvidence {
    private final ValidationMethod validationMethod;
    private final VerificationMethod verificationMethod;
    private final IdentityVerifier verifier;
    private final DateWithTimeZoneOffset time;
    private final Attestation attestation;

    public VouchEvidence(ValidationMethod validationMethod, VerificationMethod verificationMethod, IdentityVerifier verifier, DateWithTimeZoneOffset time, Attestation attestation, List<Attachment> attachments) {
        super(IdentityEvidenceType.VOUCH, attachments);
        this.validationMethod = validationMethod;
        this.verificationMethod = verificationMethod;
        this.time = time;
        this.verifier = verifier;
        this.attestation = attestation;
    }

    public ValidationMethod getValidationMethod() {
        return this.validationMethod;
    }

    public VerificationMethod getVerificationMethod() {
        return this.verificationMethod;
    }

    public IdentityVerifier getVerifier() {
        return this.verifier;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.time;
    }

    public Attestation getAttestation() {
        return this.attestation;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getValidationMethod() != null) {
            o.put("validation_method", this.getValidationMethod().toJSONObject());
        }
        if (this.getVerificationMethod() != null) {
            o.put("verification_method", this.getVerificationMethod().toJSONObject());
        }
        if (this.getVerifier() != null) {
            o.put("verifier", this.getVerifier().toJSONObject());
        }
        if (this.getVerificationTime() != null) {
            o.put("time", this.getVerificationTime().toISO8601String());
        }
        if (this.getAttestation() != null) {
            o.put("attestation", this.getAttestation().toJSONObject());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VouchEvidence)) {
            return false;
        }
        VouchEvidence that = (VouchEvidence)o;
        return Objects.equals(this.getValidationMethod(), that.getValidationMethod()) && Objects.equals(this.getVerificationMethod(), that.getVerificationMethod()) && Objects.equals(this.getVerifier(), that.getVerifier()) && Objects.equals(this.getVerificationTime(), that.getVerificationTime()) && Objects.equals(this.getAttestation(), that.getAttestation());
    }

    public int hashCode() {
        return Objects.hash(this.getValidationMethod(), this.getVerificationMethod(), this.getVerifier(), this.getVerificationTime(), this.getAttestation());
    }

    public static VouchEvidence parse(JSONObject jsonObject) throws ParseException {
        VouchEvidence.ensureType(IdentityEvidenceType.VOUCH, jsonObject);
        ValidationMethod validationMethod = null;
        if (jsonObject.get("validation_method") != null) {
            validationMethod = ValidationMethod.parse(JSONObjectUtils.getJSONObject(jsonObject, "validation_method"));
        }
        VerificationMethod verificationMethod = null;
        if (jsonObject.get("verification_method") != null) {
            verificationMethod = VerificationMethod.parse(JSONObjectUtils.getJSONObject(jsonObject, "verification_method"));
        }
        IdentityVerifier verifier = null;
        if (jsonObject.get("verifier") != null) {
            verifier = IdentityVerifier.parse(JSONObjectUtils.getJSONObject(jsonObject, "verifier"));
        }
        DateWithTimeZoneOffset time = null;
        if (jsonObject.get("time") != null) {
            time = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        Attestation attestation = null;
        if (jsonObject.get("attestation") != null) {
            attestation = Attestation.parse(JSONObjectUtils.getJSONObject(jsonObject, "attestation"));
        }
        List<Attachment> attachments = null;
        if (jsonObject.get("attachments") != null) {
            attachments = Attachment.parseList(JSONObjectUtils.getJSONArray(jsonObject, "attachments"));
        }
        return new VouchEvidence(validationMethod, verificationMethod, verifier, time, attestation, attachments);
    }
}

