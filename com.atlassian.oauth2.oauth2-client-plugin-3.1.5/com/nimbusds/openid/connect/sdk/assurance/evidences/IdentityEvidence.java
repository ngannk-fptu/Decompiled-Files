/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicRecordEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicSignatureEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.QESEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.UtilityBillEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.VouchEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public abstract class IdentityEvidence
implements JSONAware {
    private final IdentityEvidenceType evidenceType;
    private final List<Attachment> attachments;

    protected IdentityEvidence(IdentityEvidenceType evidenceType, List<Attachment> attachments) {
        if (evidenceType == null) {
            throw new IllegalArgumentException("The evidence type must not be null");
        }
        this.evidenceType = evidenceType;
        this.attachments = attachments;
    }

    public IdentityEvidenceType getEvidenceType() {
        return this.evidenceType;
    }

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public DocumentEvidence toDocumentEvidence() {
        return (DocumentEvidence)this;
    }

    public IDDocumentEvidence toIDDocumentEvidence() {
        return (IDDocumentEvidence)this;
    }

    public ElectronicRecordEvidence toElectronicRecordEvidence() {
        return (ElectronicRecordEvidence)this;
    }

    public VouchEvidence toVouchEvidence() {
        return (VouchEvidence)this;
    }

    public UtilityBillEvidence toUtilityBillEvidence() {
        return (UtilityBillEvidence)this;
    }

    public ElectronicSignatureEvidence toElectronicSignatureEvidence() {
        return (ElectronicSignatureEvidence)this;
    }

    public QESEvidence toQESEvidence() {
        return (QESEvidence)this;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("type", this.getEvidenceType().getValue());
        if (CollectionUtils.isNotEmpty(this.getAttachments())) {
            LinkedList<JSONObject> attachmentsJSONArray = new LinkedList<JSONObject>();
            for (Attachment attachment : this.getAttachments()) {
                attachmentsJSONArray.add(attachment.toJSONObject());
            }
            o.put("attachments", attachmentsJSONArray);
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public static IdentityEvidence parse(JSONObject jsonObject) throws ParseException {
        IdentityEvidenceType type = new IdentityEvidenceType(JSONObjectUtils.getString(jsonObject, "type"));
        if (IdentityEvidenceType.DOCUMENT.equals(type)) {
            return DocumentEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.ID_DOCUMENT.equals(type)) {
            return IDDocumentEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.ELECTRONIC_RECORD.equals(type)) {
            return ElectronicRecordEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.VOUCH.equals(type)) {
            return VouchEvidence.parse(jsonObject);
        }
        if (IdentityEvidenceType.ELECTRONIC_SIGNATURE.equals(type)) {
            return ElectronicSignatureEvidence.parse(jsonObject);
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

