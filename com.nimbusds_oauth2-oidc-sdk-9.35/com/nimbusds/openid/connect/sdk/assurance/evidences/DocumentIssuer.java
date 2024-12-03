/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.evidences.CommonOriginatorAttributes;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Jurisdiction;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Name;
import com.nimbusds.openid.connect.sdk.claims.Address;
import java.util.Objects;
import net.minidev.json.JSONObject;

public class DocumentIssuer
extends CommonOriginatorAttributes {
    public DocumentIssuer(Name name, Address address, CountryCode countryCode, Jurisdiction jurisdiction) {
        super(name, address, countryCode, jurisdiction);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentIssuer)) {
            return false;
        }
        DocumentIssuer that = (DocumentIssuer)o;
        return Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getAddress(), that.getAddress()) && Objects.equals(this.getCountryCode(), that.getCountryCode()) && Objects.equals(this.getJurisdiction(), that.getJurisdiction());
    }

    public static DocumentIssuer parse(JSONObject jsonObject) throws ParseException {
        CommonOriginatorAttributes commonOriginatorAttributes = CommonOriginatorAttributes.parse(jsonObject);
        return new DocumentIssuer(commonOriginatorAttributes.getName(), commonOriginatorAttributes.getAddress(), commonOriginatorAttributes.getCountryCode(), commonOriginatorAttributes.getJurisdiction());
    }
}

