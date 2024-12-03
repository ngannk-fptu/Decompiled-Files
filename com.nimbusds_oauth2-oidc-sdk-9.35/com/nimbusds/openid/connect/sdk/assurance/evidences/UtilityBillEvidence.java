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
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import com.nimbusds.openid.connect.sdk.claims.Address;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minidev.json.JSONObject;

@Deprecated
public class UtilityBillEvidence
extends IdentityEvidence {
    private final String providerName;
    private final Address providerAddress;
    private final SimpleDate date;
    private final DateWithTimeZoneOffset time;
    private final IdentityVerificationMethod method;

    @Deprecated
    public UtilityBillEvidence(String providerName, Address providerAddress, SimpleDate date) {
        this(providerName, providerAddress, date, null, null, null);
    }

    public UtilityBillEvidence(String providerName, Address providerAddress, SimpleDate date, DateWithTimeZoneOffset time, IdentityVerificationMethod method, List<Attachment> attachments) {
        super(IdentityEvidenceType.UTILITY_BILL, attachments);
        this.providerName = providerName;
        this.providerAddress = providerAddress;
        this.date = date;
        this.time = time;
        this.method = method;
    }

    public String getUtilityProviderName() {
        return this.providerName;
    }

    public Address getUtilityProviderAddress() {
        return this.providerAddress;
    }

    public SimpleDate getUtilityBillDate() {
        return this.date;
    }

    public DateWithTimeZoneOffset getVerificationTime() {
        return this.time;
    }

    public IdentityVerificationMethod getVerificationMethod() {
        return this.method;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        JSONObject providerDetails = new JSONObject();
        if (this.getUtilityProviderName() != null) {
            providerDetails.put((Object)"name", (Object)this.getUtilityProviderName());
        }
        if (this.getUtilityProviderAddress() != null) {
            providerDetails.putAll((Map)this.getUtilityProviderAddress().toJSONObject());
        }
        if (!providerDetails.isEmpty()) {
            o.put((Object)"provider", (Object)providerDetails);
        }
        if (this.getUtilityBillDate() != null) {
            o.put((Object)"date", (Object)this.getUtilityBillDate().toISO8601String());
        }
        if (this.getVerificationTime() != null) {
            o.put((Object)"time", (Object)this.getVerificationTime().toISO8601String());
        }
        if (this.getVerificationMethod() != null) {
            o.put((Object)"method", (Object)this.getVerificationMethod().getValue());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityBillEvidence)) {
            return false;
        }
        UtilityBillEvidence evidence = (UtilityBillEvidence)o;
        return Objects.equals(this.getUtilityProviderName(), evidence.getUtilityProviderName()) && Objects.equals(this.getUtilityProviderAddress(), evidence.getUtilityProviderAddress()) && Objects.equals(this.getUtilityBillDate(), evidence.getUtilityBillDate()) && Objects.equals(this.getVerificationTime(), evidence.getVerificationTime()) && Objects.equals(this.getVerificationMethod(), evidence.getVerificationMethod());
    }

    public int hashCode() {
        return Objects.hash(this.getUtilityProviderName(), this.getUtilityProviderAddress(), this.getUtilityBillDate(), this.getVerificationTime(), this.getVerificationMethod());
    }

    public static UtilityBillEvidence parse(JSONObject jsonObject) throws ParseException {
        UtilityBillEvidence.ensureType(IdentityEvidenceType.UTILITY_BILL, jsonObject);
        JSONObject providerDetails = JSONObjectUtils.getJSONObject(jsonObject, "provider", null);
        String providerName = null;
        Address providerAddress = null;
        if (providerDetails != null) {
            providerName = JSONObjectUtils.getString(providerDetails, "name", null);
            JSONObject providerDetailsCopy = new JSONObject((Map)providerDetails);
            providerDetailsCopy.remove((Object)"name");
            if (!providerDetailsCopy.isEmpty()) {
                providerAddress = new Address(providerDetailsCopy);
            }
        }
        SimpleDate date = null;
        if (jsonObject.get((Object)"date") != null) {
            date = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date"));
        }
        DateWithTimeZoneOffset dtz = null;
        if (jsonObject.get((Object)"time") != null) {
            dtz = DateWithTimeZoneOffset.parseISO8601String(JSONObjectUtils.getString(jsonObject, "time"));
        }
        IdentityVerificationMethod method = null;
        if (jsonObject.get((Object)"method") != null) {
            method = new IdentityVerificationMethod(JSONObjectUtils.getString(jsonObject, "method"));
        }
        List<Attachment> attachments = null;
        if (jsonObject.get((Object)"attachments") != null) {
            attachments = Attachment.parseList(JSONObjectUtils.getJSONArray(jsonObject, "attachments"));
        }
        return new UtilityBillEvidence(providerName, providerAddress, date, dtz, method, attachments);
    }
}

