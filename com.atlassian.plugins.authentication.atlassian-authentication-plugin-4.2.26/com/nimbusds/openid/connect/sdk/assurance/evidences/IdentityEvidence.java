/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.QESEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.UtilityBillEvidence;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public abstract class IdentityEvidence
implements JSONAware {
    private final IdentityEvidenceType evidenceType;

    protected IdentityEvidence(IdentityEvidenceType evidenceType) {
        if (evidenceType == null) {
            throw new IllegalArgumentException("The evidence type must not be null");
        }
        this.evidenceType = evidenceType;
    }

    public IdentityEvidenceType getEvidenceType() {
        return this.evidenceType;
    }

    public IDDocumentEvidence toIDDocumentEvidence() {
        return (IDDocumentEvidence)this;
    }

    public UtilityBillEvidence toUtilityBillEvidence() {
        return (UtilityBillEvidence)this;
    }

    public QESEvidence toQESEvidence() {
        return (QESEvidence)this;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("type", this.getEvidenceType().getValue());
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public static IdentityEvidence parse(JSONObject jsonObject) throws ParseException {
        IdentityEvidenceType type = new IdentityEvidenceType(JSONObjectUtils.getString(jsonObject, "type"));
        if (IdentityEvidenceType.ID_DOCUMENT.equals(type)) {
            return IDDocumentEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.QES.equals(type)) {
            return QESEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.UTILITY_BILL.equals(type)) {
            return UtilityBillEvidence.parse(jsonObject);
        }
        throw new ParseException("Unsupported type: " + type);
    }

    protected static void ensureType(IdentityEvidenceType expectedType, JSONObject jsonObject) throws ParseException {
        String parsedType = JSONObjectUtils.getString(jsonObject, "type");
        if (!expectedType.getValue().equals(parsedType)) {
            throw new ParseException("The identity evidence type must be " + expectedType);
        }
    }
}

