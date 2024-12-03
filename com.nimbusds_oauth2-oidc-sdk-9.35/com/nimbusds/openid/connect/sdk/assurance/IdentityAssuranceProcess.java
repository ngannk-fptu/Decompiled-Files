/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.assurance.Policy;
import com.nimbusds.openid.connect.sdk.assurance.Procedure;
import com.nimbusds.openid.connect.sdk.assurance.Status;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class IdentityAssuranceProcess {
    private final Policy policy;
    private final Procedure procedure;
    private final Status status;

    public IdentityAssuranceProcess(Policy policy, Procedure procedure, Status status) {
        if (policy == null && procedure == null && status == null) {
            throw new IllegalArgumentException("At least one assurance process element must be specified");
        }
        this.policy = policy;
        this.procedure = procedure;
        this.status = status;
    }

    public Policy getPolicy() {
        return this.policy;
    }

    public Procedure getProcedure() {
        return this.procedure;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentityAssuranceProcess)) {
            return false;
        }
        IdentityAssuranceProcess that = (IdentityAssuranceProcess)o;
        return Objects.equals(this.getPolicy(), that.getPolicy()) && Objects.equals(this.getProcedure(), that.getProcedure()) && Objects.equals(this.getStatus(), that.getStatus());
    }

    public int hashCode() {
        return Objects.hash(this.getPolicy(), this.getProcedure(), this.getStatus());
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.policy != null) {
            o.put((Object)"policy", (Object)this.policy.getValue());
        }
        if (this.procedure != null) {
            o.put((Object)"procedure", (Object)this.procedure.getValue());
        }
        if (this.status != null) {
            o.put((Object)"status", (Object)this.status.getValue());
        }
        return o;
    }

    public static IdentityAssuranceProcess parse(JSONObject jsonObject) throws ParseException {
        Policy policy = null;
        String value = JSONObjectUtils.getString(jsonObject, "policy", null);
        if (StringUtils.isNotBlank(value)) {
            policy = new Policy(value);
        }
        Procedure procedure = null;
        value = JSONObjectUtils.getString(jsonObject, "procedure", null);
        if (StringUtils.isNotBlank(value)) {
            procedure = new Procedure(value);
        }
        Status status = null;
        value = JSONObjectUtils.getString(jsonObject, "status", null);
        if (StringUtils.isNotBlank(value)) {
            status = new Status(value);
        }
        try {
            return new IdentityAssuranceProcess(policy, procedure, status);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

