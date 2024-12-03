/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentType;
import java.util.Objects;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Deprecated
public class IDDocumentDescription
implements JSONAware {
    private final IDDocumentType type;
    private final String number;
    private final String issuerName;
    private final CountryCode issuerCountry;
    private final SimpleDate dateOfIssuance;
    private final SimpleDate dateOfExpiry;

    public IDDocumentDescription(IDDocumentType type, String number, String issuerName, CountryCode issuerCountry, SimpleDate dateOfIssuance, SimpleDate dateOfExpiry) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        this.type = type;
        this.number = number;
        this.issuerName = issuerName;
        this.issuerCountry = issuerCountry;
        this.dateOfIssuance = dateOfIssuance;
        this.dateOfExpiry = dateOfExpiry;
    }

    public IDDocumentType getType() {
        return this.type;
    }

    public String getNumber() {
        return this.number;
    }

    public String getIssuerName() {
        return this.issuerName;
    }

    public CountryCode getIssuerCountry() {
        return this.issuerCountry;
    }

    public SimpleDate getDateOfIssuance() {
        return this.dateOfIssuance;
    }

    public SimpleDate getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("type", this.getType().getValue());
        if (this.getNumber() != null) {
            o.put("number", this.getNumber());
        }
        JSONObject issuerObject = new JSONObject();
        if (this.getIssuerName() != null) {
            issuerObject.put("name", this.getIssuerName());
        }
        if (this.getIssuerCountry() != null) {
            issuerObject.put("country", this.getIssuerCountry().getValue());
        }
        if (!issuerObject.isEmpty()) {
            o.put("issuer", issuerObject);
        }
        if (this.getDateOfIssuance() != null) {
            o.put("date_of_issuance", this.getDateOfIssuance().toISO8601String());
        }
        if (this.getDateOfExpiry() != null) {
            o.put("date_of_expiry", this.getDateOfExpiry().toISO8601String());
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IDDocumentDescription)) {
            return false;
        }
        IDDocumentDescription that = (IDDocumentDescription)o;
        return this.getType().equals(that.getType()) && Objects.equals(this.getNumber(), that.getNumber()) && Objects.equals(this.getIssuerName(), that.getIssuerName()) && Objects.equals(this.getIssuerCountry(), that.getIssuerCountry()) && Objects.equals(this.getDateOfIssuance(), that.getDateOfIssuance()) && Objects.equals(this.getDateOfExpiry(), that.getDateOfExpiry());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getNumber(), this.getIssuerName(), this.getIssuerCountry(), this.getDateOfIssuance(), this.getDateOfExpiry());
    }

    public static IDDocumentDescription parse(JSONObject jsonObject) throws ParseException {
        IDDocumentType type = new IDDocumentType(JSONObjectUtils.getString(jsonObject, "type"));
        String number = JSONObjectUtils.getString(jsonObject, "number", null);
        JSONObject issuerObject = JSONObjectUtils.getJSONObject(jsonObject, "issuer", null);
        String issuerName = null;
        ISO3166_1Alpha2CountryCode issuerCountry = null;
        if (issuerObject != null) {
            issuerName = JSONObjectUtils.getString(issuerObject, "name", null);
            if (issuerObject.get("country") != null) {
                issuerCountry = ISO3166_1Alpha2CountryCode.parse(JSONObjectUtils.getString(issuerObject, "country"));
            }
        }
        SimpleDate dateOfIssuance = null;
        if (jsonObject.get("date_of_issuance") != null) {
            dateOfIssuance = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_issuance"));
        }
        SimpleDate dateOfExpiry = null;
        if (jsonObject.get("date_of_expiry") != null) {
            dateOfExpiry = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_expiry"));
        }
        return new IDDocumentDescription(type, number, issuerName, issuerCountry, dateOfIssuance, dateOfExpiry);
    }
}

