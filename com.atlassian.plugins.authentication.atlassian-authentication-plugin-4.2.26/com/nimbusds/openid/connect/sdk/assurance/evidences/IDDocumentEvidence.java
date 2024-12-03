/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentDescription;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerifier;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class IDDocumentEvidence
extends IdentityEvidence {
    private final IdentityVerificationMethod method;
    private final DateWithTimeZoneOffset dtz;
    private final IdentityVerifier verifier;
    private final IDDocumentDescription idDocument;

    public IDDocumentEvidence(IdentityVerificationMethod method, IdentityVerifier verifier, DateWithTimeZoneOffset dtz, IDDocumentDescription idDocument) {
        super(IdentityEvidenceType.ID_DOCUMENT);
        this.method = method;
        this.dtz = dtz;
        this.verifier = verifier;
        this.idDocument = idDocument;
    }

    public IdentityVerificationMethod getVerificationMethod() {
        return this.method;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.dtz;
    }

    public IdentityVerifier getVerifier() {
        return this.verifier;
    }

    public IDDocumentDescription getIdentityDocument() {
        return this.idDocument;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getVerificationMethod() != null) {
            o.put("method", this.getVerificationMethod().getValue());
        }
        if (this.dtz != null) {
            o.put("time", this.getVerificationTime().toISO8601String());
        }
        if (this.verifier != null) {
            o.put("verifier", this.getVerifier().toJSONObject());
        }
        if (this.getIdentityDocument() != null) {
            o.put("document", this.getIdentityDocument().toJSONObject());
        }
        return o;
    }

    public static IDDocumentEvidence parse(JSONObject jsonObject) throws ParseException {
        IDDocumentEvidence.ensureType(IdentityEvidenceType.ID_DOCUMENT, jsonObject);
        IdentityVerificationMethod method = null;
        if (jsonObject.get("method") != null) {
            method = new IdentityVerificationMethod(JSONObjectUtils.getString(jsonObject, "method"));
        }
        DateWithTimeZoneOffset dtz = null;
        if (jsonObject.get("time") != null) {
            dtz = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        IdentityVerifier verifier = null;
        if (jsonObject.get("verifier") != null) {
            verifier = IdentityVerifier.parse(JSONObjectUtils.getJSONObject(jsonObject, "verifier"));
        }
        IDDocumentDescription idDocument = null;
        if (jsonObject.get("document") != null) {
            idDocument = IDDocumentDescription.parse(JSONObjectUtils.getJSONObject(jsonObject, "document"));
        }
        return new IDDocumentEvidence(method, verifier, dtz, idDocument);
    }
}

