/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.SerialNumber;
import com.nimbusds.openid.connect.sdk.assurance.evidences.SignatureType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import java.util.List;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class ElectronicSignatureEvidence
extends IdentityEvidence {
    private final SignatureType signatureType;
    private final Issuer issuer;
    private final SerialNumber certificateSerialNumber;
    private final DateWithTimeZoneOffset createdAt;

    public ElectronicSignatureEvidence(SignatureType signatureType, Issuer issuer, SerialNumber certificateSerialNumber, DateWithTimeZoneOffset createdAt, List<Attachment> attachments) {
        super(IdentityEvidenceType.ELECTRONIC_SIGNATURE, attachments);
        Objects.requireNonNull(signatureType);
        this.signatureType = signatureType;
        this.issuer = issuer;
        this.certificateSerialNumber = certificateSerialNumber;
        this.createdAt = createdAt;
    }

    public SignatureType getSignatureType() {
        return this.signatureType;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public SerialNumber getCertificateSerialNumber() {
        return this.certificateSerialNumber;
    }

    public DateWithTimeZoneOffset getCreationTime() {
        return this.createdAt;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put((Object)"signature_type", (Object)this.getSignatureType().getValue());
        if (this.getIssuer() != null) {
            o.put((Object)"issuer", (Object)this.getIssuer().getValue());
        }
        if (this.getCertificateSerialNumber() != null) {
            o.put((Object)"serial_number", (Object)this.getCertificateSerialNumber().getValue());
        }
        if (this.getCreationTime() != null) {
            o.put((Object)"created_at", (Object)this.getCreationTime().toISO8601String());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElectronicSignatureEvidence)) {
            return false;
        }
        ElectronicSignatureEvidence evidence = (ElectronicSignatureEvidence)o;
        return this.getSignatureType().equals(evidence.getSignatureType()) && Objects.equals(this.getIssuer(), evidence.getIssuer()) && Objects.equals(this.getCertificateSerialNumber(), evidence.getCertificateSerialNumber()) && Objects.equals(this.createdAt, evidence.createdAt);
    }

    public int hashCode() {
        return Objects.hash(this.getSignatureType(), this.getIssuer(), this.getCertificateSerialNumber(), this.createdAt);
    }

    public static ElectronicSignatureEvidence parse(JSONObject jsonObject) throws ParseException {
        ElectronicSignatureEvidence.ensureType(IdentityEvidenceType.ELECTRONIC_SIGNATURE, jsonObject);
        SignatureType signatureType = new SignatureType(JSONObjectUtils.getString(jsonObject, "signature_type"));
        Issuer issuer = null;
        if (jsonObject.get((Object)"issuer") != null) {
            issuer = new Issuer(JSONObjectUtils.getString(jsonObject, "issuer"));
        }
        SerialNumber serialNumber = null;
        if (jsonObject.get((Object)"serial_number") != null) {
            serialNumber = new SerialNumber(JSONObjectUtils.getString(jsonObject, "serial_number", null));
        }
        DateWithTimeZoneOffset createdAt = null;
        if (jsonObject.get((Object)"created_at") != null) {
            createdAt = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "created_at"));
        }
        List<Attachment> attachments = null;
        if (jsonObject.get((Object)"attachments") != null) {
            attachments = Attachment.parseList(JSONObjectUtils.getJSONArray(jsonObject, "attachments"));
        }
        return new ElectronicSignatureEvidence(signatureType, issuer, serialNumber, createdAt, attachments);
    }
}

