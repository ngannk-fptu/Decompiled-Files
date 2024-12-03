/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import com.nimbusds.openid.connect.sdk.assurance.evidences.PersonalNumber;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ReferenceNumber;
import com.nimbusds.openid.connect.sdk.assurance.evidences.VouchType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Voucher;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class Attestation {
    private final VouchType type;
    private final ReferenceNumber referenceNumber;
    private final PersonalNumber personalNumber;
    private final SimpleDate dateOfIssuance;
    private final SimpleDate dateOfExpiry;
    private final Voucher voucher;

    public Attestation(VouchType type, ReferenceNumber referenceNumber, PersonalNumber personalNumber, SimpleDate dateOfIssuance, SimpleDate dateOfExpiry, Voucher voucher) {
        Objects.requireNonNull(type);
        this.type = type;
        this.referenceNumber = referenceNumber;
        this.personalNumber = personalNumber;
        this.dateOfIssuance = dateOfIssuance;
        this.dateOfExpiry = dateOfExpiry;
        this.voucher = voucher;
    }

    public VouchType getType() {
        return this.type;
    }

    public ReferenceNumber getReferenceNumber() {
        return this.referenceNumber;
    }

    public PersonalNumber getPersonalNumber() {
        return this.personalNumber;
    }

    public SimpleDate getDateOfIssuance() {
        return this.dateOfIssuance;
    }

    public SimpleDate getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    public Voucher getVoucher() {
        return this.voucher;
    }

    public JSONObject toJSONObject() {
        JSONObject voucherObject;
        JSONObject o = new JSONObject();
        o.put((Object)"type", (Object)this.getType().getValue());
        if (this.getReferenceNumber() != null) {
            o.put((Object)"reference_number", (Object)this.getReferenceNumber().getValue());
        }
        if (this.getPersonalNumber() != null) {
            o.put((Object)"personal_number", (Object)this.getPersonalNumber().getValue());
        }
        if (this.getDateOfIssuance() != null) {
            o.put((Object)"date_of_issuance", (Object)this.getDateOfIssuance().toISO8601String());
        }
        if (this.getDateOfExpiry() != null) {
            o.put((Object)"date_of_expiry", (Object)this.getDateOfExpiry().toISO8601String());
        }
        if (this.getVoucher() != null && !(voucherObject = this.getVoucher().toJSONObject()).isEmpty()) {
            o.put((Object)"voucher", (Object)voucherObject);
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attestation)) {
            return false;
        }
        Attestation that = (Attestation)o;
        return this.getType().equals(that.getType()) && Objects.equals(this.getReferenceNumber(), that.getReferenceNumber()) && Objects.equals(this.getPersonalNumber(), that.getPersonalNumber()) && Objects.equals(this.getDateOfIssuance(), that.getDateOfIssuance()) && Objects.equals(this.getDateOfExpiry(), that.getDateOfExpiry()) && Objects.equals(this.getVoucher(), that.getVoucher());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getReferenceNumber(), this.getPersonalNumber(), this.getDateOfIssuance(), this.getDateOfExpiry(), this.getVoucher());
    }

    public static Attestation parse(JSONObject jsonObject) throws ParseException {
        try {
            VouchType type = new VouchType(JSONObjectUtils.getString(jsonObject, "type"));
            ReferenceNumber referenceNumber = null;
            if (jsonObject.get((Object)"reference_number") != null) {
                referenceNumber = new ReferenceNumber(JSONObjectUtils.getString(jsonObject, "reference_number"));
            }
            PersonalNumber personalNumber = null;
            if (jsonObject.get((Object)"personal_number") != null) {
                personalNumber = new PersonalNumber(JSONObjectUtils.getString(jsonObject, "personal_number"));
            }
            SimpleDate dateOfIssuance = null;
            if (jsonObject.get((Object)"date_of_issuance") != null) {
                dateOfIssuance = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_issuance"));
            }
            SimpleDate dateOfExpiry = null;
            if (jsonObject.get((Object)"date_of_expiry") != null) {
                dateOfExpiry = SimpleDate.parseISO8601String(JSONObjectUtils.getString(jsonObject, "date_of_expiry"));
            }
            Voucher voucher = null;
            if (jsonObject.get((Object)"voucher") != null) {
                voucher = Voucher.parse(JSONObjectUtils.getJSONObject(jsonObject, "voucher"));
            }
            return new Attestation(type, referenceNumber, personalNumber, dateOfIssuance, dateOfExpiry, voucher);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

