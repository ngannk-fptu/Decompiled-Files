/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.DateWithTimeZoneOffset;
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicRecordSource;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicRecordType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.PersonalNumber;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class ElectronicRecordDetails {
    private final ElectronicRecordType type;
    private final PersonalNumber personalNumber;
    private final DateWithTimeZoneOffset createdAt;
    private final SimpleDate dateOfExpiry;
    private final ElectronicRecordSource source;

    public ElectronicRecordDetails(ElectronicRecordType type, PersonalNumber personalNumber, DateWithTimeZoneOffset createdAt, SimpleDate dateOfExpiry, ElectronicRecordSource source) {
        Objects.requireNonNull(type);
        this.type = type;
        this.personalNumber = personalNumber;
        this.createdAt = createdAt;
        this.dateOfExpiry = dateOfExpiry;
        this.source = source;
    }

    public ElectronicRecordType getType() {
        return this.type;
    }

    public PersonalNumber getPersonalNumber() {
        return this.personalNumber;
    }

    public DateWithTimeZoneOffset getCreatedAt() {
        return this.createdAt;
    }

    public SimpleDate getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    public ElectronicRecordSource getSource() {
        return this.source;
    }

    public JSONObject toJSONObject() {
        JSONObject sourceObject;
        JSONObject o = new JSONObject();
        o.put("type", this.getType().getValue());
        if (this.getPersonalNumber() != null) {
            o.put("personal_number", this.getPersonalNumber().getValue());
        }
        if (this.getCreatedAt() != null) {
            o.put("created_at", this.getCreatedAt().toISO8601String());
        }
        if (this.getDateOfExpiry() != null) {
            o.put("date_of_expiry", this.getDateOfExpiry().toISO8601String());
        }
        if (this.getSource() != null && !(sourceObject = this.getSource().toJSONObject()).isEmpty()) {
            o.put("source", sourceObject);
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElectronicRecordDetails)) {
            return false;
        }
        ElectronicRecordDetails that = (ElectronicRecordDetails)o;
        return this.getType().equals(that.getType()) && Objects.equals(this.getPersonalNumber(), that.getPersonalNumber()) && Objects.equals(this.getCreatedAt(), that.getCreatedAt()) && Objects.equals(this.getDateOfExpiry(), that.getDateOfExpiry()) && Objects.equals(this.getSource(), that.getSource());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getPersonalNumber(), this.getCreatedAt(), this.getDateOfExpiry(), this.getSource());
    }

    public static ElectronicRecordDetails parse(JSONObject jsonObject) throws ParseException {
        try {
            ElectronicRecordType type = new ElectronicRecordType(JSONObjectUtils.getString(jsonObject, "type"));
            PersonalNumber personalNumber = null;
            if (jsonObject.get("personal_number") != null) {
                personalNumber = new PersonalNumber(JSONObjectUtils.getString(jsonObject, "personal_number"));
            }
            DateWithTimeZoneOffset createdAt = null;
            if (jsonObject.get("created_at") != null) {
                createdAt = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "created_at"));
            }
            SimpleDate dateOfExpiry = null;
            if (jsonObject.get("date_of_expiry") != null) {
                dateOfExpiry = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_expiry"));
            }
            ElectronicRecordSource source = null;
            if (jsonObject.get("source") != null) {
                source = ElectronicRecordSource.parse(JSONObjectUtils.getJSONObject(jsonObject, "source"));
            }
            return new ElectronicRecordDetails(type, personalNumber, createdAt, dateOfExpiry, source);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

