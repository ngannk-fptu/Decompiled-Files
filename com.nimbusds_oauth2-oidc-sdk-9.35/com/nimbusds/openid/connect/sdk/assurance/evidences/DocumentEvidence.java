/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentDetails;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerifier;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ValidationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.VerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import java.util.List;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class DocumentEvidence
extends IdentityEvidence {
    private final ValidationMethod validationMethod;
    private final VerificationMethod verificationMethod;
    @Deprecated
    private final IdentityVerificationMethod method;
    private final IdentityVerifier verifier;
    private final DateWithTimeZoneOffset time;
    private final DocumentDetails documentDetails;

    public DocumentEvidence(ValidationMethod validationMethod, VerificationMethod verificationMethod, IdentityVerificationMethod method, IdentityVerifier verifier, DateWithTimeZoneOffset time, DocumentDetails documentDetails, List<Attachment> attachments) {
        super(IdentityEvidenceType.DOCUMENT, attachments);
        this.validationMethod = validationMethod;
        this.verificationMethod = verificationMethod;
        this.method = method;
        this.time = time;
        this.verifier = verifier;
        this.documentDetails = documentDetails;
    }

    public ValidationMethod getValidationMethod() {
        return this.validationMethod;
    }

    public VerificationMethod getVerificationMethod() {
        return this.verificationMethod;
    }

    @Deprecated
    public IdentityVerificationMethod getMethod() {
        return this.method;
    }

    public IdentityVerifier getVerifier() {
        return this.verifier;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.time;
    }

    public DocumentDetails getDocumentDetails() {
        return this.documentDetails;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getValidationMethod() != null) {
            o.put((Object)"validation_method", (Object)this.getValidationMethod().toJSONObject());
        }
        if (this.getVerificationMethod() != null) {
            o.put((Object)"verification_method", (Object)this.getVerificationMethod().toJSONObject());
        }
        if (this.getMethod() != null) {
            o.put((Object)"method", (Object)this.getMethod().getValue());
        }
        if (this.getVerifier() != null) {
            o.put((Object)"verifier", (Object)this.getVerifier().toJSONObject());
        }
        if (this.getVerificationTime() != null) {
            o.put((Object)"time", (Object)this.getVerificationTime().toISO8601String());
        }
        if (this.getDocumentDetails() != null) {
            o.put((Object)"document_details", (Object)this.getDocumentDetails().toJSONObject());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentEvidence)) {
            return false;
        }
        DocumentEvidence that = (DocumentEvidence)o;
        return Objects.equals(this.getValidationMethod(), that.getValidationMethod()) && Objects.equals(this.getVerificationMethod(), that.getVerificationMethod()) && Objects.equals(this.getMethod(), that.getMethod()) && Objects.equals(this.getVerifier(), that.getVerifier()) && Objects.equals(this.getVerificationTime(), that.getVerificationTime()) && Objects.equals(this.getDocumentDetails(), that.getDocumentDetails());
    }

    public int hashCode() {
        return Objects.hash(this.getValidationMethod(), this.getVerificationMethod(), this.getMethod(), this.getVerifier(), this.getVerificationTime(), this.getDocumentDetails());
    }

    public static DocumentEvidence parse(JSONObject jsonObject) throws ParseException {
        DocumentEvidence.ensureType(IdentityEvidenceType.DOCUMENT, jsonObject);
        ValidationMethod validationMethod = null;
        if (jsonObject.get((Object)"validation_method") != null) {
            validationMethod = ValidationMethod.parse(JSONObjectUtils.getJSONObject(jsonObject, "validation_method"));
        }
        VerificationMethod verificationMethod = null;
        if (jsonObject.get((Object)"verification_method") != null) {
            verificationMethod = VerificationMethod.parse(JSONObjectUtils.getJSONObject(jsonObject, "verification_method"));
        }
        IdentityVerificationMethod method = null;
        if (jsonObject.get((Object)"method") != null) {
            method = new IdentityVerificationMethod(JSONObjectUtils.getString(jsonObject, "method"));
        }
        IdentityVerifier verifier = null;
        if (jsonObject.get((Object)"verifier") != null) {
            verifier = IdentityVerifier.parse(JSONObjectUtils.getJSONObject(jsonObject, "verifier"));
        }
        DateWithTimeZoneOffset dtz = null;
        if (jsonObject.get((Object)"time") != null) {
            dtz = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        DocumentDetails documentDetails = null;
        if (jsonObject.get((Object)"document_details") != null) {
            documentDetails = DocumentDetails.parse(JSONObjectUtils.getJSONObject(jsonObject, "document_details"));
        }
        List<Attachment> attachments = null;
        if (jsonObject.get((Object)"attachments") != null) {
            attachments = Attachment.parseList(JSONObjectUtils.getJSONArray(jsonObject, "attachments"));
        }
        return new DocumentEvidence(validationMethod, verificationMethod, method, verifier, dtz, documentDetails, attachments);
    }
}

