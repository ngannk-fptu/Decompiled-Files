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
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentDescription;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerifier;
import java.util.Objects;
import net.minidev.json.JSONObject;

@Deprecated
public class IDDocumentEvidence
extends IdentityEvidence {
    private final IdentityVerificationMethod method;
    private final DateWithTimeZoneOffset time;
    private final IdentityVerifier verifier;
    private final IDDocumentDescription idDocument;

    public IDDocumentEvidence(IdentityVerificationMethod method, IdentityVerifier verifier, DateWithTimeZoneOffset time, IDDocumentDescription idDocument) {
        super(IdentityEvidenceType.ID_DOCUMENT, null);
        this.method = method;
        this.time = time;
        this.verifier = verifier;
        this.idDocument = idDocument;
    }

    public IdentityVerificationMethod getVerificationMethod() {
        return this.method;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.time;
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
            o.put((Object)"method", (Object)this.getVerificationMethod().getValue());
        }
        if (this.getVerificationTime() != null) {
            o.put((Object)"time", (Object)this.getVerificationTime().toISO8601String());
        }
        if (this.getVerifier() != null) {
            o.put((Object)"verifier", (Object)this.getVerifier().toJSONObject());
        }
        if (this.getIdentityDocument() != null) {
            o.put((Object)"document", (Object)this.getIdentityDocument().toJSONObject());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IDDocumentEvidence)) {
            return false;
        }
        IDDocumentEvidence that = (IDDocumentEvidence)o;
        return Objects.equals(this.getVerificationMethod(), that.getVerificationMethod()) && Objects.equals(this.getVerificationTime(), that.getVerificationTime()) && Objects.equals(this.getVerifier(), that.getVerifier()) && Objects.equals(this.getIdentityDocument(), that.getIdentityDocument());
    }

    public int hashCode() {
        return Objects.hash(this.method, this.time, this.getVerifier(), this.idDocument);
    }

    public static IDDocumentEvidence parse(JSONObject jsonObject) throws ParseException {
        IDDocumentEvidence.ensureType(IdentityEvidenceType.ID_DOCUMENT, jsonObject);
        IdentityVerificationMethod method = null;
        if (jsonObject.get((Object)"method") != null) {
            method = new IdentityVerificationMethod(JSONObjectUtils.getString(jsonObject, "method"));
        }
        DateWithTimeZoneOffset dtz = null;
        if (jsonObject.get((Object)"time") != null) {
            dtz = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        IdentityVerifier verifier = null;
        if (jsonObject.get((Object)"verifier") != null) {
            verifier = IdentityVerifier.parse(JSONObjectUtils.getJSONObject(jsonObject, "verifier"));
        }
        IDDocumentDescription idDocument = null;
        if (jsonObject.get((Object)"document") != null) {
            idDocument = IDDocumentDescription.parse(JSONObjectUtils.getJSONObject(jsonObject, "document"));
        }
        return new IDDocumentEvidence(method, verifier, dtz, idDocument);
    }
}

