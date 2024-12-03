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
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Jurisdiction;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Name;
import com.nimbusds.openid.connect.sdk.claims.Address;
import java.util.Map;
import java.util.Objects;
import net.minidev.json.JSONObject;

class CommonOriginatorAttributes {
    private final Name name;
    private final Address address;
    private final CountryCode countryCode;
    private final Jurisdiction jurisdiction;

    public CommonOriginatorAttributes(Name name, Address address, CountryCode countryCode, Jurisdiction jurisdiction) {
        this.name = name;
        this.address = address;
        this.countryCode = countryCode;
        this.jurisdiction = jurisdiction;
    }

    public Name getName() {
        return this.name;
    }

    public Address getAddress() {
        return this.address;
    }

    public CountryCode getCountryCode() {
        return this.countryCode;
    }

    public Jurisdiction getJurisdiction() {
        return this.jurisdiction;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getName() != null) {
            o.put((Object)"name", (Object)this.getName().getValue());
        }
        if (this.getAddress() != null) {
            o.putAll((Map)this.getAddress().toJSONObject());
        }
        if (this.getCountryCode() != null) {
            o.put((Object)"country_code", (Object)this.getCountryCode().getValue());
        }
        if (this.getJurisdiction() != null) {
            o.put((Object)"jurisdiction", (Object)this.getJurisdiction().getValue());
        }
        return o;
    }

    public int hashCode() {
        return Objects.hash(this.getName(), this.getAddress(), this.getCountryCode(), this.getJurisdiction());
    }

    static CommonOriginatorAttributes parse(JSONObject jsonObject) throws ParseException {
        try {
            Name name = null;
            if (jsonObject.get((Object)"name") != null) {
                name = new Name(JSONObjectUtils.getString(jsonObject, "name"));
            }
            CountryCode countryCode = null;
            if (jsonObject.get((Object)"country_code") != null) {
                countryCode = CountryCode.parse(JSONObjectUtils.getString(jsonObject, "country_code"));
            }
            Jurisdiction jurisdiction = null;
            if (jsonObject.get((Object)"jurisdiction") != null) {
                jurisdiction = new Jurisdiction(JSONObjectUtils.getString(jsonObject, "jurisdiction"));
            }
            Address address = null;
            if (CollectionUtils.intersect(Address.getStandardClaimNames(), jsonObject.keySet())) {
                JSONObject addressSpecific = new JSONObject((Map)jsonObject);
                addressSpecific.remove((Object)"name");
                addressSpecific.remove((Object)"country_code");
                addressSpecific.remove((Object)"jurisdiction");
                address = new Address(addressSpecific);
            }
            return new CommonOriginatorAttributes(name, address, countryCode, jurisdiction);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

