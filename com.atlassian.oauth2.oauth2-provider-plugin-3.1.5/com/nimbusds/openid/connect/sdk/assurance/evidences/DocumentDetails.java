/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentIssuer;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentNumber;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.PersonalNumber;
import com.nimbusds.openid.connect.sdk.assurance.evidences.SerialNumber;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class DocumentDetails {
    private final DocumentType type;
    private final DocumentNumber documentNumber;
    private final PersonalNumber personalNumber;
    private final SerialNumber serialNumber;
    private final SimpleDate dateOfIssuance;
    private final SimpleDate dateOfExpiry;
    private final DocumentIssuer issuer;

    public DocumentDetails(DocumentType type, DocumentNumber documentNumber, PersonalNumber personalNumber, SerialNumber serialNumber, SimpleDate dateOfIssuance, SimpleDate dateOfExpiry, DocumentIssuer issuer) {
        Objects.requireNonNull(type);
        this.type = type;
        this.documentNumber = documentNumber;
        this.personalNumber = personalNumber;
        this.serialNumber = serialNumber;
        this.dateOfIssuance = dateOfIssuance;
        this.dateOfExpiry = dateOfExpiry;
        this.issuer = issuer;
    }

    public DocumentType getType() {
        return this.type;
    }

    public DocumentNumber getDocumentNumber() {
        return this.documentNumber;
    }

    public PersonalNumber getPersonalNumber() {
        return this.personalNumber;
    }

    public SerialNumber getSerialNumber() {
        return this.serialNumber;
    }

    public SimpleDate getDateOfIssuance() {
        return this.dateOfIssuance;
    }

    public SimpleDate getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    public DocumentIssuer getIssuer() {
        return this.issuer;
    }

    public JSONObject toJSONObject() {
        JSONObject issuerObject;
        JSONObject o = new JSONObject();
        o.put("type", this.getType().getValue());
        if (this.getDocumentNumber() != null) {
            o.put("document_number", this.getDocumentNumber().getValue());
        }
        if (this.getPersonalNumber() != null) {
            o.put("personal_number", this.getPersonalNumber().getValue());
        }
        if (this.getSerialNumber() != null) {
            o.put("serial_number", this.getSerialNumber().getValue());
        }
        if (this.getDateOfIssuance() != null) {
            o.put("date_of_issuance", this.getDateOfIssuance().toISO8601String());
        }
        if (this.getDateOfExpiry() != null) {
            o.put("date_of_expiry", this.getDateOfExpiry().toISO8601String());
        }
        if (this.getIssuer() != null && !(issuerObject = this.getIssuer().toJSONObject()).isEmpty()) {
            o.put("issuer", issuerObject);
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentDetails)) {
            return false;
        }
        DocumentDetails that = (DocumentDetails)o;
        return this.getType().equals(that.getType()) && Objects.equals(this.getDocumentNumber(), that.getDocumentNumber()) && Objects.equals(this.getPersonalNumber(), that.getPersonalNumber()) && Objects.equals(this.getSerialNumber(), that.getSerialNumber()) && Objects.equals(this.getDateOfIssuance(), that.getDateOfIssuance()) && Objects.equals(this.getDateOfExpiry(), that.getDateOfExpiry()) && Objects.equals(this.getIssuer(), that.getIssuer());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getDocumentNumber(), this.getPersonalNumber(), this.getSerialNumber(), this.getDateOfIssuance(), this.getDateOfExpiry(), this.getIssuer());
    }

    public static DocumentDetails parse(JSONObject jsonObject) throws ParseException {
        try {
            DocumentType type = new DocumentType(JSONObjectUtils.getString(jsonObject, "type"));
            DocumentNumber documentNumber = null;
            if (jsonObject.get("document_number") != null) {
                documentNumber = new DocumentNumber(JSONObjectUtils.getString(jsonObject, "document_number"));
            }
            PersonalNumber personalNumber = null;
            if (jsonObject.get("personal_number") != null) {
                personalNumber = new PersonalNumber(JSONObjectUtils.getString(jsonObject, "personal_number"));
            }
            SerialNumber serialNumber = null;
            if (jsonObject.get("serial_number") != null) {
                serialNumber = new SerialNumber(JSONObjectUtils.getString(jsonObject, "serial_number"));
            }
            SimpleDate dateOfIssuance = null;
            if (jsonObject.get("date_of_issuance") != null) {
                dateOfIssuance = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_issuance"));
            }
            SimpleDate dateOfExpiry = null;
            if (jsonObject.get("date_of_expiry") != null) {
                dateOfExpiry = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_expiry"));
            }
            DocumentIssuer issuer = null;
            if (jsonObject.get("issuer") != null) {
                issuer = DocumentIssuer.parse(JSONObjectUtils.getJSONObject(jsonObject, "issuer"));
            }
            return new DocumentDetails(type, documentNumber, personalNumber, serialNumber, dateOfIssuance, dateOfExpiry, issuer);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

