/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Name;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Occupation;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization;
import com.nimbusds.openid.connect.sdk.claims.Address;
import java.util.Map;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class Voucher {
    private final Name name;
    private final String birthdateString;
    private final Address address;
    private final Occupation occupation;
    private final Organization organization;

    public Voucher(Name name, String birthdateString, Address address, Occupation occupation, Organization organization) {
        this.name = name;
        this.birthdateString = birthdateString;
        this.address = address;
        this.occupation = occupation;
        this.organization = organization;
    }

    public Name getName() {
        return this.name;
    }

    public String getBirthdateString() {
        return this.birthdateString;
    }

    public Address getAddress() {
        return this.address;
    }

    public Occupation getOccupation() {
        return this.occupation;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getName() != null) {
            o.put((Object)"name", (Object)this.getName().getValue());
        }
        if (this.getBirthdateString() != null) {
            o.put((Object)"birthdate", (Object)this.getBirthdateString());
        }
        if (this.getAddress() != null) {
            o.putAll((Map)this.getAddress().toJSONObject());
        }
        if (this.getOccupation() != null) {
            o.put((Object)"occupation", (Object)this.getOccupation().getValue());
        }
        if (this.getOrganization() != null) {
            o.put((Object)"organization", (Object)this.getOrganization().getValue());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Voucher)) {
            return false;
        }
        Voucher voucher = (Voucher)o;
        return Objects.equals(this.getName(), voucher.getName()) && Objects.equals(this.getBirthdateString(), voucher.getBirthdateString()) && Objects.equals(this.getAddress(), voucher.getAddress()) && Objects.equals(this.getOccupation(), voucher.getOccupation()) && Objects.equals(this.getOrganization(), voucher.getOrganization());
    }

    public int hashCode() {
        return Objects.hash(this.getName(), this.getBirthdateString(), this.getAddress(), this.getOccupation(), this.getOrganization());
    }

    public static Voucher parse(JSONObject jsonObject) throws ParseException {
        try {
            Name name = null;
            if (jsonObject.get((Object)"name") != null) {
                name = new Name(JSONObjectUtils.getString(jsonObject, "name"));
            }
            String birthdateString = JSONObjectUtils.getString(jsonObject, "birthdate", null);
            Occupation occupation = null;
            if (jsonObject.get((Object)"occupation") != null) {
                occupation = new Occupation(JSONObjectUtils.getString(jsonObject, "occupation"));
            }
            Organization organization = null;
            if (jsonObject.get((Object)"organization") != null) {
                organization = new Organization(JSONObjectUtils.getString(jsonObject, "organization"));
            }
            Address address = null;
            if (CollectionUtils.intersect(Address.getStandardClaimNames(), jsonObject.keySet())) {
                JSONObject addressSpecific = new JSONObject((Map)jsonObject);
                addressSpecific.remove((Object)"name");
                addressSpecific.remove((Object)"birthdate");
                addressSpecific.remove((Object)"occupation");
                addressSpecific.remove((Object)"organization");
                address = new Address(addressSpecific);
            }
            return new Voucher(name, birthdateString, address, occupation, organization);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

