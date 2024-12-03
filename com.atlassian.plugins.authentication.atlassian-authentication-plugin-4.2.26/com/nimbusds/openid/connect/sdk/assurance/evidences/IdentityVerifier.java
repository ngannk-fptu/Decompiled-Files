/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.secevent.sdk.claims.TXN;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class IdentityVerifier
implements JSONAware {
    private final String organization;
    private final TXN txn;

    public IdentityVerifier(String organization, TXN txn) {
        this.organization = organization;
        this.txn = txn;
    }

    public String getOrganization() {
        return this.organization;
    }

    public TXN getTXN() {
        return this.txn;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getOrganization() != null) {
            o.put("organization", this.getOrganization());
        }
        if (this.getTXN() != null) {
            o.put("txn", this.getTXN().getValue());
        }
        return o;
    }

    @Override
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
        return this.getOrganization().equals(verifier.getOrganization()) && this.txn.equals(verifier.txn);
    }

    public int hashCode() {
        return Objects.hash(this.getOrganization(), this.txn);
    }

    public static IdentityVerifier parse(JSONObject jsonObject) throws ParseException {
        String org = JSONObjectUtils.getString(jsonObject, "organization", null);
        TXN txn = null;
        if (jsonObject.get("txn") != null) {
            txn = new TXN(JSONObjectUtils.getString(jsonObject, "txn"));
        }
        return new IdentityVerifier(org, txn);
    }
}

