/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import java.util.Objects;
import net.minidev.json.JSONObject;

@Deprecated
public class QESEvidence
extends IdentityEvidence {
    private final Issuer issuer;
    private final String serialNumber;
    private final DateWithTimeZoneOffset createdAt;

    public QESEvidence(Issuer issuer, String serialNumber, DateWithTimeZoneOffset createdAt) {
        super(IdentityEvidenceType.QES, null);
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.createdAt = createdAt;
    }

    public Issuer getQESIssuer() {
        return this.issuer;
    }

    public String getQESSerialNumberString() {
        return this.serialNumber;
    }

    public DateWithTimeZoneOffset getQESCreationTime() {
        return this.createdAt;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getQESIssuer() != null) {
            o.put("issuer", this.getQESIssuer().getValue());
        }
        if (this.getQESSerialNumberString() != null) {
            o.put("serial_number", this.getQESSerialNumberString());
        }
        if (this.getQESCreationTime() != null) {
            o.put("created_at", this.getQESCreationTime().toISO8601String());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QESEvidence)) {
            return false;
        }
        QESEvidence evidence = (QESEvidence)o;
        return Objects.equals(this.getQESIssuer(), evidence.getQESIssuer()) && Objects.equals(this.getQESSerialNumberString(), evidence.getQESSerialNumberString()) && Objects.equals(this.getQESCreationTime(), evidence.getQESCreationTime());
    }

    public int hashCode() {
        return Objects.hash(this.getQESIssuer(), this.getQESSerialNumberString(), this.getQESCreationTime());
    }

    public static QESEvidence parse(JSONObject jsonObject) throws ParseException {
        QESEvidence.ensureType(IdentityEvidenceType.QES, jsonObject);
        Issuer issuer = null;
        if (jsonObject.get("issuer") != null) {
            issuer = new Issuer(JSONObjectUtils.getString(jsonObject, "issuer"));
        }
        String serialNumber = JSONObjectUtils.getString(jsonObject, "serial_number", null);
        DateWithTimeZoneOffset createdAt = null;
        if (jsonObject.get("created_at") != null) {
            createdAt = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "created_at"));
        }
        return new QESEvidence(issuer, serialNumber, createdAt);
    }
}

