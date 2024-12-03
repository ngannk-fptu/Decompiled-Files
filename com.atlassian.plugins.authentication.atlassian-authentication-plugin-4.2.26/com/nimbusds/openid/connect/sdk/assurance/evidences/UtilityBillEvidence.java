/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidence;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.claims.Address;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class UtilityBillEvidence
extends IdentityEvidence {
    private final String providerName;
    private final Address providerAddress;
    private final SimpleDate date;

    public UtilityBillEvidence(String providerName, Address providerAddress, SimpleDate date) {
        super(IdentityEvidenceType.UTILITY_BILL);
        this.providerName = providerName;
        this.providerAddress = providerAddress;
        this.date = date;
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

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        JSONObject providerDetails = new JSONObject();
        if (this.getUtilityProviderName() != null) {
            providerDetails.put("name", this.getUtilityProviderName());
        }
        if (this.getUtilityProviderAddress() != null) {
            providerDetails.putAll(this.getUtilityProviderAddress().toJSONObject());
        }
        if (!providerDetails.isEmpty()) {
            o.put("provider", providerDetails);
        }
        if (this.getUtilityBillDate() != null) {
            o.put("date", this.getUtilityBillDate().toISO8601String());
        }
        return o;
    }

    public static UtilityBillEvidence parse(JSONObject jsonObject) throws ParseException {
        UtilityBillEvidence.ensureType(IdentityEvidenceType.UTILITY_BILL, jsonObject);
        JSONObject providerDetails = JSONObjectUtils.getJSONObject(jsonObject, "provider", null);
        String providerName = null;
        Address providerAddress = null;
        if (providerDetails != null) {
            providerName = JSONObjectUtils.getString(providerDetails, "name", null);
            JSONObject providerDetailsCopy = new JSONObject(providerDetails);
            providerDetailsCopy.remove("name");
            if (!providerDetailsCopy.isEmpty()) {
                providerAddress = new Address(providerDetailsCopy);
            }
        }
        SimpleDate date = null;
        if (jsonObject.get("date") != null) {
            date = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date"));
        }
        return new UtilityBillEvidence(providerName, providerAddress, date);
    }
}

