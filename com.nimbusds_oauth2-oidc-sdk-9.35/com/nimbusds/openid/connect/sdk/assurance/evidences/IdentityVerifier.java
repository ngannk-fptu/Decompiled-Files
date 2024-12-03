/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization;
import com.nimbusds.secevent.sdk.claims.TXN;
import java.util.Objects;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public class IdentityVerifier
implements JSONAware {
    private final Organization organization;
    private final TXN txn;

    public IdentityVerifier(Organization organization, TXN txn) {
        this.organization = organization;
        this.txn = txn;
    }

    @Deprecated
    public IdentityVerifier(String organizationString, TXN txn) {
        this.organization = organizationString != null ? new Organization(organizationString) : null;
        this.txn = txn;
    }

    public Organization getOrganizationEntity() {
        return this.organization;
    }

    public String getOrganizationString() {
        return this.getOrganizationEntity() != null ? this.getOrganizationEntity().getValue() : null;
    }

    @Deprecated
    public String getOrganization() {
        return this.getOrganizationString();
    }

    public TXN getTXN() {
        return this.txn;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getOrganization() != null) {
            o.put((Object)"organization", (Object)this.getOrganizationEntity().getValue());
        }
        if (this.getTXN() != null) {
            o.put((Object)"txn", (Object)this.getTXN().getValue());
        }
        return o;
    }

    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentityVerifier)) {
            return false;
        }
        IdentityVerifier verifier = (IdentityVerifier)o;
        return Objects.equals(this.getOrganizationEntity(), verifier.getOrganizationEntity()) && Objects.equals(this.getTXN(), verifier.getTXN());
    }

    public int hashCode() {
        return Objects.hash(this.getOrganizationEntity(), this.getTXN());
    }

    public static IdentityVerifier parse(JSONObject jsonObject) throws ParseException {
        Organization org = null;
        if (jsonObject.get((Object)"organization") != null) {
            org = new Organization(JSONObjectUtils.getString(jsonObject, "organization"));
        }
        TXN txn = null;
        if (jsonObject.get((Object)"txn") != null) {
            txn = new TXN(JSONObjectUtils.getString(jsonObject, "txn"));
        }
        return new IdentityVerifier(org, txn);
    }
}

